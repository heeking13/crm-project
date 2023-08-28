package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.DicValueService;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.ClueActivityRelation;
import com.bjpowernode.crm.workbench.domain.ClueRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueActivityRelationService;
import com.bjpowernode.crm.workbench.service.ClueRemarkService;
import com.bjpowernode.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class ClueController {

    @Autowired
    private UserService userService;
    @Autowired
    private DicValueService dicValueService;
    @Autowired
    private ClueService clueService;

    @Autowired
    private ClueRemarkService clueRemarkService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ClueActivityRelationService clueActivityRelationService;

    @RequestMapping("/workbench/clue/index.do")
    public String index(HttpServletRequest request) {
        List<User> userList = userService.queryAllUsers();
        List<DicValue> appellationList = dicValueService.queryDicValueByTypeCode("appellation");
        List<DicValue> clueStateList = dicValueService.queryDicValueByTypeCode("clueState");
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");
        request.setAttribute("userList", userList);
        request.setAttribute("appellationList", appellationList);
        request.setAttribute("clueStateList", clueStateList);
        request.setAttribute("sourceList", sourceList);
        return "workbench/clue/index";
    }

    @ResponseBody
    @RequestMapping("/workbench/clue/insertClue.do")
    public Object insertClue(Clue clue, HttpSession session) {
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        clue.setId(UUIDUtils.getUUID());
        clue.setCreateTime(DateUtils.formatDateTime(new Date()));
        clue.setCreateBy(user.getId());
        ReturnObject ro = new ReturnObject();
        try {
            int count = clueService.insertClue(clue);
            if (count > 0) {
                ro.setCode(Contants.RETURN_RETURN_CODE_SUCCESS);
            } else {
                ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
                ro.setMessage("系统繁忙，请稍后重试~");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
            ro.setMessage("系统繁忙，请稍后重试~");
        }
        return ro;
    }

    @ResponseBody
    @RequestMapping("/workbench/clue/queryClueByConditionForPage.do")
    public Object queryClueByConditionForPage(String fullname, String company, String source, String owner, String mphone, String state,
                                              int pageNo, int pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("fullname", fullname);
        map.put("company", company);
        map.put("source", source);
        map.put("owner", owner);
        map.put("mphone", mphone);
        map.put("state", state);
        map.put("beginNo", (pageNo - 1) * pageSize);
        map.put("pageSize", pageSize);
        List<Clue> clueList = clueService.queryClueByConditionForPage(map);
        int totalRows = clueService.queryCountOfClueByConditionForPage(map);
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("clueList", clueList);
        returnMap.put("totalRows", totalRows);
        return returnMap;
    }

    @RequestMapping("/workbench/clue/clueDetail.do")
    public String clueDetail(String id, HttpServletRequest request) {
        Clue clue = clueService.queryClueDetail(id);
        List<ClueRemark> clueRemarkList = clueRemarkService.queryClueRemarkById(id);
        List<Activity> activityList = activityService.queryActivityForDetailByClueId(id);
        request.setAttribute("clue", clue);
        request.setAttribute("clueRemarkList", clueRemarkList);
        request.setAttribute("activityList", activityList);
        return "workbench/clue/detail";
    }

    @ResponseBody
    @RequestMapping("/workbench/clue/queryActivityForDetailByNameClueId.do")
    public Object queryActivityForDetailByNameClueId(String activityName, String clueId) {
        Map<String, Object> map = new HashMap<>();
        map.put("activityName", activityName);
        map.put("clueId", clueId);
        List<Activity> activityList = activityService.queryActivityForDetailByNameClueId(map);
        return activityList;
    }

    @ResponseBody
    @RequestMapping("/workbench/clue/saveBoundActivityClue.do")
    public Object saveBoundActivityClue(String[] activityId, String clueId) {
//        List<ClueActivityRelation> list = new ArrayList<>();
//        for(int i=0; i <activityId.length; i++){
//            ClueActivityRelation clueActivityRelation = new ClueActivityRelation();
//            clueActivityRelation.setActivityId(activityId[i]);
//            clueActivityRelation.setClueId(clueId);
//            list.add(clueActivityRelation);
//        }
        List<ClueActivityRelation> list = new ArrayList<>();
        ClueActivityRelation clueActivityRelation = null;
        for (String ai : activityId) {
            clueActivityRelation = new ClueActivityRelation();
            clueActivityRelation.setActivityId(ai);
            clueActivityRelation.setClueId(clueId);
            clueActivityRelation.setId(UUIDUtils.getUUID());
            list.add(clueActivityRelation);
        }
        ReturnObject ro = new ReturnObject();
        try {
            int count = clueActivityRelationService.saveCreateClueActivityRelationByList(list);
            if (count > 0) {
                ro.setCode(Contants.RETURN_RETURN_CODE_SUCCESS);
                List<Activity> activityList = activityService.queryActivityForDetailByIds(activityId);
                ro.setRetData(activityList);
            } else {
                ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
                ro.setMessage("系统繁忙，请稍后重试~");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ro;
    }

    @ResponseBody
    @RequestMapping("/workbench/clue/deleteClueActivityRelation.do")
    public Object deleteClueActivityRelation(ClueActivityRelation car) {
        ReturnObject ro = new ReturnObject();
        try {
            int count = clueActivityRelationService.deleteClueActivityRelation(car);
            if (count > 0) {
                ro.setCode(Contants.RETURN_RETURN_CODE_SUCCESS);
            } else {
                ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
                ro.setMessage("系统繁忙，请稍后重试~");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
            ro.setMessage("系统繁忙，请稍后重试~");
        }
        return ro;
    }

    @RequestMapping("/workbench/clue/toConvert.do")
    public String toConvert(String id, HttpServletRequest request) {
        Clue clue = clueService.queryClueDetail(id);
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        request.setAttribute("clue", clue);
        request.setAttribute("stageList", stageList);
        return "workbench/clue/convert";
    }
}
