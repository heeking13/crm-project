package com.bjpowernode.crm.workbench.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    @RequestMapping("/workbench/main/index.do")
    public String mainIndexPage(){
        return "workbench/main/index";
    }
}
