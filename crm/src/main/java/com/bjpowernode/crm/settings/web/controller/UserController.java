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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
    public Object login(String loginAct, String loginPwd, String isRemPwd, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
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
            String nowStr = DateUtils.formatDateTime(new Date()); //将当前的时间转换为字符串格式
            if (nowStr.compareTo(user.getExpireTime()) > 0) {
                //登陆失败，账号过期
                ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
                ro.setMessage("账号已经过期");
            } else if ("0".equals(user.getLockState())) {
                //登陆失败，状态被锁定
                ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
                ro.setMessage("账号被锁定");
            }
//            else if (!user.getAllowIps().contains(request.getRemoteAddr())) {
//                //登陆失败，ip被锁定
//                ro.setCode(Contants.RETURN_RETURN_CODE_FAIL);
//                ro.setMessage("账号ip被锁定");
//            }
            else {
                ro.setCode(Contants.RETURN_RETURN_CODE_SUCCESS);
                session.setAttribute(Contants.SESSION_USER, user); // 将user存入session, 可以再前台使用
                //如果需要记住密码，则往外写cookie
                if("true".equals(isRemPwd)){
                    Cookie c1 = new Cookie("loginAct", user.getLoginAct());
                    Cookie c2 = new Cookie("loginPwd", user.getLoginPwd());
                    c1.setMaxAge(10*24*60*60);
                    c2.setMaxAge(10*24*60*60);
                    response.addCookie(c1);
                    response.addCookie(c2);
                } else {
                    //把没有过期的cookie删除
                    Cookie c1 = new Cookie("loginAct", "1");
                    Cookie c2 = new Cookie("loginPwd", "1");
                    c1.setMaxAge(0);
                    c2.setMaxAge(0);
                    response.addCookie(c1);
                    response.addCookie(c2);
                }
            }
        }
        return ro;
    }

    @RequestMapping("/settings/qx/user/logout.do")
    public String logout(HttpServletResponse response, HttpSession session){
        //清空cookie
        Cookie c1 = new Cookie("loginAct", "1");
        Cookie c2 = new Cookie("loginPwd", "1");
        c1.setMaxAge(0);
        c2.setMaxAge(0);
        response.addCookie(c1);
        response.addCookie(c2);
        //销毁session
        session.invalidate();
//        return "redirect:/settings/qx/user/toLogin.do";
        return "redirect:/";
    }

}
