package com.imooc.miaosha.redis;

public class UserKey extends BasePrefix{
    private UserKey(String prefix){
        super(prefix);
    }

     public static UserKey getById= new UserKey("id");//其实是一个静态类对象。

     public static UserKey getByName= new UserKey("name");

}
