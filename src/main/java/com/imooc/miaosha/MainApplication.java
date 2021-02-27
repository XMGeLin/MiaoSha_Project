package com.imooc.miaosha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/**
 * jar包的话main函数可以运行了
 */
public class MainApplication {
    //启动类
    public static void main(String[] args) throws Exception {
        SpringApplication.run(MainApplication.class, args);
        System.out.println("springboot项目启动了！");
    }
}