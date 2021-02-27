package com.imooc.miaosha.vo;

import com.imooc.miaosha.validator.IsMobile;

import javax.validation.constraints.NotNull;

public class LoginVo {

    @NotNull
    @IsMobile   //自定义注解
    private String mobile;

    @NotNull
    private String password;

    public String getMobile() {
        return mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
