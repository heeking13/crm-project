package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping("/settings/qx/user/toLogin.do")
    public String toLogin() {
        return "settings/qx/user/login";
    }

    @RequestMapping("/settings/qx/user/login.do")
    @ResponseBody
    public Object login(String loginAct, String loginPwd, String isRemPwd, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("loginAct", loginAct);
        map.put("loginPwd", loginPwd);
        User user = userService.queryUserByLoginActAndPwd(map);
        ReturnObject ro = new ReturnObject();
        if (user == null) {
            ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
            ro.setMessage("用户名或密码不正确");
        } else {
            //进一步查询账号是否合法
            String nowStr = DateUtils.formatDateTime(new Date());
            if (nowStr.compareTo(user.getExpireTime()) > 0) {
                //登陆失败，账号过期
                ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
                ro.setMessage("账号已经过期");
            } else if ("0".equals(user.getLockState())) {
                //登陆失败，状态被锁定
                ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
                ro.setMessage("账号被锁定");
            } else if (!user.getAllowIps().contains(request.getRemoteAddr())) {
                //登陆失败，ip被锁定
                ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
                ro.setMessage("账号ip被锁定");
                System.out.println(request.getRemoteAddr());
            } else {
                ro.setCode(Contants.RETURN_RETURN_CODE_SUCCESS);
            }
        }
        return ro;
    }
}
