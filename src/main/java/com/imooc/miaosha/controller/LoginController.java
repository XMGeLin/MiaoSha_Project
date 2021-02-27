package com.imooc.miaosha.controller;

import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.vo.LoginVo;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";   //页面假如是login
    }

    @RequestMapping("/do_login")
    @ResponseBody     //使用注解代替每次
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        log.info(loginVo.toString());
        /**
         *  项目中使用@NotNull@Max等配合@Valid注解进行验证传过来的参数校验，然后统一异常处理，直接
         *  返回给前端，不用在业务代码中对这些参数进行校验。
         *
         *  也就是说使用注解验证参数配合异常处理，很方便且减少了很多的业务代码，各种if判断让人头疼。
         */
        //登录

//        //参数校验
//        String passInput=loginVo.getPassword();
//        String mobile=loginVo.getMobile();
//        if(StringUtils.isEmpty(passInput)){
//            return Result.error(CodeMsg.PASSWORD_EMPTY);
//        }
//        if(StringUtils.isEmpty(mobile))  {
//            return Result.error(CodeMsg.MOBILE_EMPTY) ;
//        }
//        if(!ValidatorUtil.isMobile(mobile))  {
//            return Result.error(CodeMsg.MOBILE_ERROR);  //手机号错误。
//        }
        //登录
        String token=userService.login(response,loginVo);   //将我们的token的值写入到文件中
        return Result.success(token);

    }
}
