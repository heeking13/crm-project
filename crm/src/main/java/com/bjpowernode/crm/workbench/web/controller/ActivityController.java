package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
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

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

    @RequestMapping("/workbench/activity/exportAllActivities.do")
    public void exportAllActivities(HttpServletResponse response) throws Exception {
        //查询所有市场活动
        List<Activity> activityList = activityService.queryAllActivities();
        //创建excel文件，把activityList写入到excel文件中
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("市场活动列表");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("所有者");
        cell = row.createCell(2);
        cell.setCellValue("名称");
        cell = row.createCell(3);
        cell.setCellValue("开始日期");
        cell = row.createCell(4);
        cell.setCellValue("结束日期");
        cell = row.createCell(5);
        cell.setCellValue("成本");
        cell = row.createCell(6);
        cell.setCellValue("描述");
        cell = row.createCell(7);
        cell.setCellValue("创建时间");
        cell = row.createCell(8);
        cell.setCellValue("创建者");
        cell = row.createCell(9);
        cell.setCellValue("修改时间");
        cell = row.createCell(10);
        cell.setCellValue("修改者");

        //需要对list进行判断，如果是空的，不需要进行遍历
        if (activityList != null && activityList.size() > 0) {
            Activity activity = null;
            for (int i = 0; i < activityList.size(); i++) {
                activity = activityList.get(i);
                row = sheet.createRow(i + 1);
                cell = row.createCell(0);
                cell.setCellValue(activity.getId());
                cell = row.createCell(1);
                cell.setCellValue(activity.getOwner());
                cell = row.createCell(2);
                cell.setCellValue(activity.getName());
                cell = row.createCell(3);
                cell.setCellValue(activity.getStartDate());
                cell = row.createCell(4);
                cell.setCellValue(activity.getEndDate());
                cell = row.createCell(5);
                cell.setCellValue(activity.getCost());
                cell = row.createCell(6);
                cell.setCellValue(activity.getDescription());
                cell = row.createCell(7);
                cell.setCellValue(activity.getCreateTime());
                cell = row.createCell(8);
                cell.setCellValue(activity.getCreateBy());
                cell = row.createCell(9);
                cell.setCellValue(activity.getEditTime());
                cell = row.createCell(10);
                cell.setCellValue(activity.getEditBy());
            }
        }

        //把生成的文件下载到客户端
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=activityList.xls");
        OutputStream out = response.getOutputStream();
        wb.write(out);
        wb.close();
        out.flush();
    }
}
