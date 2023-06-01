package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.service.ActivityService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    @RequestMapping("/workbench/activity/saveCreateActivity.do")
    @ResponseBody
    public Object saveCreateActivity(Activity activity, HttpSession session) {
        //封装参数
        //获取当前session的用户，session获得后强转
        //因为前台没有这些数据，需要传一下
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        activity.setId(UUIDUtils.getUUID());
        activity.setCreateTime(DateUtils.formatDateTime(new Date()));
        activity.setCreateBy(user.getId());
        ReturnObject ro = new ReturnObject();
        try {
            //调用service层，保存创建市场活动
            int ret = activityService.saveCreateActivity(activity);
            if(ret>0){
                ro.setCode(Contants.RETURN_RETURN_CODE_SUCCESS);
            } else{
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
}
