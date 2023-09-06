package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.DicValueService;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Controller
public class TranController {
    @Autowired
    private DicValueService dicValueService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private TranService tranService;

    @RequestMapping("/workbench/transaction/index.do")
    public String index(HttpServletRequest request) {
        List<DicValue> transactionTypeList = dicValueService.queryDicValueByTypeCode("transactionType");
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        request.setAttribute("transactionTypeList", transactionTypeList);
        request.setAttribute("sourceList", sourceList);
        request.setAttribute("stageList", stageList);
        return "workbench/transaction/index";
    }

    @RequestMapping("/workbench/transaction/toSave.do")
    public String toSave(HttpServletRequest request) {
        List<User> userList = userService.queryAllUsers();
        List<DicValue> transactionTypeList = dicValueService.queryDicValueByTypeCode("transactionType");
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        request.setAttribute("transactionTypeList", transactionTypeList);
        request.setAttribute("sourceList", sourceList);
        request.setAttribute("stageList", stageList);
        request.setAttribute("userList", userList);
        return "workbench/transaction/save";
    }

    @RequestMapping("/workbench/transaction/getPossibilityByStage.do")
    public @ResponseBody Object getPossibilityByStage(String stageValue){
        //解析properties配置文件，根据阶段获取可能性
        ResourceBundle bundle=ResourceBundle.getBundle("possibility");
        String possibility=bundle.getString(stageValue);
        //返回响应信息
        return possibility;
    }

    @RequestMapping("/workbench/transaction/queryCustomerName.do")
    @ResponseBody
    public Object queryCustomerName(String name){
        List<String> nameList = customerService.selectCustomerNameByName(name);
        return nameList;
    }

    @RequestMapping("/workbench/transaction/saveCreateTran.do")
    @ResponseBody
    public Object saveCreateTran(@RequestParam Map<String,Object> map, HttpSession session){
        //封装参数
        map.put(Contants.SESSION_USER,session.getAttribute(Contants.SESSION_USER));

        ReturnObject returnObject=new ReturnObject();
        try {
            //调用service层方法，保存创建的交易
            tranService.saveCreateTran(map);
            returnObject.setCode(Contants.RETURN_RETURN_CODE_SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_RETURN_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试....");
        }

        return returnObject;
    }
}
