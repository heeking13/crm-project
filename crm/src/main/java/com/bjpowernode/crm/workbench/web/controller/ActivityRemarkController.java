package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
public class ActivityRemarkController {
    @Autowired
    private ActivityRemarkService activityRemarkService;

    @ResponseBody
    @RequestMapping("/workbench/activity/saveCreateActivityRemark.do")
    public Object saveCreateActivityRemark(ActivityRemark activityRemark, HttpSession session) {
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        activityRemark.setId(UUIDUtils.getUUID());
        activityRemark.setCreateTime(DateUtils.formatDateTime(new Date()));
        activityRemark.setCreateBy(user.getId());
        activityRemark.setEditFlag(Contants.REMARK_EDIT_FLAG_NO_EDIT);
        ReturnObject ro = new ReturnObject();
        try {
            int count = activityRemarkService.saveCreateActivityRemark(activityRemark);
            if (count > 0) {
                ro.setCode(Contants.RETURN_RETURN_CODE_SUCCESS);
                ro.setRetData(activityRemark);
            } else {
                ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
                ro.setMessage("系统繁忙，请稍后再试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
            ro.setMessage("系统繁忙，请稍后再试");
        }
        return ro;
    }

    @ResponseBody
    @RequestMapping("/workbench/activity/deleteActivityRemarkById.do")
    public Object deleteActivityRemarkById(String id) {
        ReturnObject ro = new ReturnObject();
        try {
            int count = activityRemarkService.deleteActivityRemarkById(id);
            if (count > 0) {
                ro.setCode(Contants.RETURN_RETURN_CODE_SUCCESS);
            } else {
                ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
                ro.setMessage("系统繁忙，请稍后再试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
            ro.setMessage("系统繁忙，请稍后再试");
        }
        return ro;
    }
}
