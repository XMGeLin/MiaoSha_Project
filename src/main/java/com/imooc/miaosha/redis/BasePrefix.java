package com.imooc.miaosha.redis;

import java.util.Map;

/**
 * 这个是采用模板方法模式。
 */
public abstract class BasePrefix implements KeyPrefix{

    private int expireSeconds;   //这叫过期时间
    private String prefix;

    public BasePrefix(int expireSeconds,String prefix){
        this.expireSeconds=expireSeconds;
        this.prefix=prefix;
    }

    public BasePrefix(String prefix){
        this(0,prefix);   //这里的0代表是永不过期
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;   //
    }

    @Override
    public String getPrefix() {
        String className=getClass().getSimpleName();  //得到类名。
        return className+":"+prefix;
    }
}
