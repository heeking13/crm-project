package com.bjpowernode.crm.commons.domain;

public class ReturnObject {
    //专门返回json信息给浏览器
    private String code; //处理成功的标记，1代表成功，0代表失败
    private String message; //提示信息
    private Object retData; //返回其他格式的其他数据

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getRetData() {
        return retData;
    }

    public void setRetData(Object retData) {
        this.retData = retData;
    }
}
