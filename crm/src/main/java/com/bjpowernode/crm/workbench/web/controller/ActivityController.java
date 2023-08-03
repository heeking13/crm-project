package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.HSSFUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.service.ActivityService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

@Controller
public class ActivityController {

    @Resource
    private UserService userService;
    @Resource
    private ActivityService activityService;

    //跳转到市场活动主页面
    @RequestMapping("/workbench/activity/index.do")
    public String index(HttpServletRequest request) {
        List<User> users = userService.queryAllUsers();
        request.setAttribute("users", users);
        return "workbench/activity/index";
    }

    //新增活动
    @RequestMapping("/workbench/activity/saveCreateActivity.do")
    @ResponseBody
    public Object saveCreateActivity(Activity activity, HttpSession session) {
        //封装参数
        //获取当前session的用户，session获得后强转
        //因为前台没有这些数据，需要传一下
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        activity.setId(UUIDUtils.getUUID());
        activity.setCreateTime(DateUtils.formatDateTime(new Date()));
        activity.setCreateBy(user.getId()); //传id,不要传name,防止重名
        ReturnObject ro = new ReturnObject();
        try {
            //调用service层，保存创建市场活动
            int ret = activityService.saveCreateActivity(activity);
            if (ret > 0) {
                ro.setCode(Contants.RETURN_RETURN_CODE_SUCCESS);
            } else {
                ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
                ro.setMessage("系统忙，请稍后重试～");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
            ro.setMessage("系统忙，请稍后重试～");
        }
        return ro;
    }

    /*
    查询活动
     */
    @RequestMapping("/workbench/activity/queryActivityByConditionForPage.do")
    @ResponseBody
    public Object queryActivityByConditionForPage(String name, String owner, String startDate, String endDate,
                                                  int pageNo, int pageSize) {
        //封装参数
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("owner", owner);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("beginNo", (pageNo - 1) * pageSize);
        map.put("pageSize", pageSize);
        //调用service层方法，查询数据
        List<Activity> activityList = activityService.queryActivityByConditionForPage(map);
        int totalRows = activityService.queryCountOfActivityByCondition(map);
        //根据查询结果结果，生成响应信息
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("activityList", activityList);
        retMap.put("totalRows", totalRows);
        return retMap;
    }

    /*
    删除活动功能
     */
    @RequestMapping("/workbench/activity/deleteActivity.do")
    @ResponseBody
    public Object deleteActivity(String[] id) {
        ReturnObject ro = new ReturnObject();
        try {
            int ret = activityService.deleteActivityByIds(id);
            if (ret > 0) {
                ro.setCode(Contants.RETURN_RETURN_CODE_SUCCESS);
            } else {
                ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
                ro.setMessage("系统繁忙，请稍后重试～");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
            ro.setMessage("系统繁忙，请稍后重试～");
        }
        return ro;
    }

    /*
        查询活动填充到修改页面
     */
    @RequestMapping("/workbench/activity/queryActivityById.do")
    @ResponseBody
    public Object queryActivityById(String id) {
        //调用service层
        return activityService.queryActivityById(id);
    }

    /*
    修改市场活动的信息
     */
    @RequestMapping("/workbench/activity/updateActivityById.do")
    @ResponseBody
    public Object updateActivityById(Activity activity, HttpSession session) {
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        activity.setEditTime(DateUtils.formatDateTime(new Date()));
        activity.setEditBy(user.getId());
        int flag = activityService.updateActivity(activity);
        ReturnObject ro = new ReturnObject();
        try {
            if (flag > 0) {
                ro.setCode(Contants.RETURN_RETURN_CODE_SUCCESS);
            } else {
                ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
                ro.setMessage("系统繁忙，请稍后重试～");
            }

        } catch (Exception e) {
            e.printStackTrace();
            ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
            ro.setMessage("系统繁忙，请稍后重试～");
        }
        return ro;
    }

    /*
    导出全部活动
     */
    @RequestMapping("/workbench/activity/exportAllActivities.do")
    public void exportAllActivities(HttpServletResponse response) throws Exception {
        //查询所有市场活动
        List<Activity> activityList = activityService.queryAllActivities();
        HSSFWorkbook wb = HSSFUtils.exportActivities(activityList);
        //把生成的文件下载到客户端
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=activityList.xls");
        OutputStream out = response.getOutputStream();
        wb.write(out);
        wb.close();
        out.flush();
    }

    /*
    导出选中的活动
     */
    @RequestMapping("/workbench/activity/exportActivitiesByChoose.do")
    public void exportActivitiesByChoose(String[] id, HttpServletResponse response) throws Exception {
        List<Activity> activityList = activityService.selectActivitiesByChoose(id);
        HSSFWorkbook wb = HSSFUtils.exportActivities(activityList);
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=activityList.xls");
        OutputStream out = response.getOutputStream();
        wb.write(out);
        wb.close();
        out.flush();
    }

    @RequestMapping("/workbench/activity/downloadTemplate.do")
    public void downloadTemplate(HttpServletResponse response) throws Exception {
        HSSFWorkbook wb = HSSFUtils.hssfTemplate();
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=activityList.xls");
        OutputStream out = response.getOutputStream();
        wb.write(out);
        wb.close();
        out.flush();
    }

    @RequestMapping("/workbench/activity/importActivityByList.do")
    @ResponseBody
    public Object importActivityByList(MultipartFile activityFile, HttpSession session) {
        ReturnObject ro = new ReturnObject();
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        try {
            //把excel文件写到磁盘目录中
            String originalFilename = activityFile.getOriginalFilename();
            File file = new File("/Users/heqing/Desktop/crm project/test/", originalFilename);
            activityFile.transferTo(file);
            //解析excel文件，获取文件中的数据，存入到activity实体类中，
            FileInputStream is = new FileInputStream("/Users/heqing/Desktop/crm project/test/" + originalFilename);
            HSSFWorkbook wb = new HSSFWorkbook(is);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row = null;
            HSSFCell cell = null;
            Activity activity = null;
            List<Activity> activityList = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { //sheet.getLastRowNum()最后一行的下标
                row = sheet.getRow(i);
                activity = new Activity();
                activity.setId(UUIDUtils.getUUID());
                activity.setOwner(user.getId());
                activity.setCreateTime(DateUtils.formatDateTime(new Date()));
                activity.setCreateBy(user.getId());
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    cell = row.getCell(j);
                    //获取列中的数据
                    String cellValue = HSSFUtils.getCellValueForString(cell);
                    if (j == 0) {
                        activity.setName(cellValue);
                    } else if (j == 1) {
                        activity.setStartDate(cellValue);
                    } else if (j == 2) {
                        activity.setEndDate(cellValue);
                    } else if (j == 3) {
                        activity.setCost(cellValue);
                    } else if (j == 4) {
                        activity.setDescription(cellValue);
                    }
                }
                activityList.add(activity);
            }
            int count = activityService.saveCreateActivityByList(activityList);
            ro.setCode(Contants.RETURN_RETURN_CODE_SUCCESS);
            ro.setRetData(count);
        } catch (Exception e) {
            e.printStackTrace();
            ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
            ro.setMessage("系统繁忙，请稍后再试");
        }
        return ro;
    }
}
