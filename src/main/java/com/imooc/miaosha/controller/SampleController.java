package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.User;
import com.imooc.miaosha.rabbitmq.MQSender;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.redis.UserKey;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    @RequestMapping("/thymeleaf")
    public String Thymeleaf(Model model){
        /**
         * 创建一个model对象 往里面添加参数,返回到前端。
         */
        model.addAttribute("name","shua");
        return "hello";  //hello这个是模板名
    }

    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> home(){
        return Result.success("你好，世界！");
    }

//    @RequestMapping("/mq")
//    @ResponseBody
//    public Result<String> mq(){
//        mqSender.send("nihao  消息中间件！");
//        return Result.success("消息发送出去了！");
//    }
//
//    @RequestMapping("/mq/topic")
//    @ResponseBody
//    public Result<String> topic(){
//        mqSender.sendTopic("你好，消息中间件！");
//        return Result.success("消息发送出去了！");
//    }
//
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public Result<String> fanout(){
//        mqSender.sendFanout("你好，消息中间件！");
//        return Result.success("消息发送出去了！");
//    }

    @RequestMapping("/error")
    @ResponseBody
    public Result<String> error(){
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @ResponseBody
    @RequestMapping("/db/get")
    public Result<User> dbGet(){    //这里的返回值应该就是User了
        User user=userService.getById(1);
        return Result.success(user);
    }

    @ResponseBody
    @RequestMapping("/db/tx")
    public Result<Boolean> dbTx(){    //这里的返回值应该就是User了
        userService.tx();
        return Result.success(true);
    }

    /**
     *  其实这里返回的Result<User>其实是类对象Result的两个自带属性code和msg，还有第三个属性T
     *  而T是User，所以加上User类的几个属性一起返回到浏览器页面。
     * @return
     */
    @ResponseBody
    @RequestMapping("/redis/get")
    public Result<User> redisGet(){
        User user=redisService.get(UserKey.getById,""+1,User.class);
        return Result.success(user);
    }

    /**
     * responseBody注解的作用是将controller的方法返回的对象通过适当的转换器转换为指定的格式之后，
     * 写入到response对象的body区，通常用来返回JSON数据或者是XML数据。返回结果假如是字符串则直接将
     * 字符串写到客户端,假如是一个对象（这里是map对象），此时会将对象转化为json串然后写到客户端。
     * @return
     */
    @ResponseBody
    @RequestMapping("/redis/set")
    public Result<Boolean> redisSet(){    //这里的返回值应该就是User了
        User user=new User();
        user.setName("1111");
        user.setId(1);
        redisService.set(UserKey.getById,"1",user); //这里getById其实是一个对象，赋给父接口
        return Result.success(true);
    }


}
