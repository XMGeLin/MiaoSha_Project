package com.imooc.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    public <T> T get(KeyPrefix keyPrefix,String key,Class<T> clazz) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            /**
             * 生成真正的key
             */
            String realKey=keyPrefix.getPrefix()+key;
            String str=jedis.get(realKey);   //这里的str是从Redis中获取到的value，也就是类转化的字符串
            T t=stringToBean(str,clazz);   //把我们的string转换成bean。
            return t;

        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 支持Class类型是一个bean类型，如果是集合不一样
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */

    public static  <T> T stringToBean(String str,Class<T> clazz) {
        if(str==null || str.length()==0|| clazz==null){
            return null;
        }
        if(clazz==int.class ||clazz==Integer.class){
            return (T)Integer.valueOf(str);
        }else if(clazz==String.class){
            return (T)str;
        }else if(clazz==long.class || clazz==Long.class){
            return (T)Long.valueOf(str);
        }else {     //这里其实是如果是类的话。先将字符串转化为json然后在转换成对象。
            //fastjson 是一个性能很好的 Java 语言实现的 JSON 解析器和生成器，来自阿里巴巴的工程师开发。
            return JSON.toJavaObject(JSON.parseObject(str),clazz);
        }

    }

    //这里的KeyPreFix其实就是模板方法中的接口，有两个方法，它可以指向派生类对象
    public <T> boolean set(KeyPrefix keyPrefix,String key,T value) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            String str=beanToString(value);
            if(str==null ||str.length()==0){
                return false;
            }
            /**
             * 这一步也是一样的
             */
            String realKey=keyPrefix.getPrefix()+key;   //这里其实返回的就是UserKey:id+1
            /**
             * 要判断一下key的过期时间
             */
            int seconds= keyPrefix.expireSeconds();
            if(seconds<=0){    //如果是永不过期的话
                jedis.set(realKey,str);   //这里的value是T类型的，我们需要转换为string类型
            }else {
                jedis.setex(realKey,seconds,str);   //可以设置有效期的函数
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    public static <T> String beanToString(T value) {
        if(value==null)
            return null;
        Class<?> clazz=value.getClass();

        if(clazz==int.class ||clazz==Integer.class){
            return ""+value;
        }else if(clazz==String.class){
            return (String) value;
        }else if(clazz==long.class || clazz==Long.class){
            return ""+value;
        }else {
            return JSON.toJSONString(value);  //如果是bean则直接转换
        }
    }

    private void returnToPool(Jedis jedis) {
        if(jedis!=null){
            jedis.close();
        }
    }

    /**
     * 判断key是否存在
     * @param keyPrefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> boolean exists(KeyPrefix keyPrefix,String key) {
        Jedis jedis=null;
        try {
            String realKey=keyPrefix.getPrefix()+key;
            jedis=jedisPool.getResource();
            return  jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * redis中incr命令将key中存储的数字值增1.如果key不存在，那么key的值会先被初始化为0，
     *  然后再执行incr操作。
     * @param keyPrefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr(KeyPrefix keyPrefix, String key) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            //生成真正的key
            String realKey=keyPrefix.getPrefix()+key;
            return  jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    //减少值
    public <T> Long decr(KeyPrefix keyPrefix, String key) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            //生成真正的key
            String realKey=keyPrefix.getPrefix()+key;
            return  jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    public boolean delete(KeyPrefix keyPrefix, String key) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            //生成真正的key
            String realKey=keyPrefix.getPrefix()+key;
            long ret= jedis.del(realKey);
            return ret>0;
        }finally {
            returnToPool(jedis);
        }
    }

    public boolean delete(KeyPrefix prefix) {
        if(prefix == null) {
            return false;
        }
        List<String> keys = scanKeys(prefix.getPrefix());
        if(keys==null || keys.size() <= 0) {
            return true;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(keys.toArray(new String[0]));
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> scanKeys(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            List<String> keys = new ArrayList<String>();
            String cursor = "0";
            ScanParams sp = new ScanParams();
            sp.match("*"+key+"*");
            sp.count(100);
            do{
                ScanResult<String> ret = jedis.scan(cursor, sp);
                List<String> result = ret.getResult();
                if(result!=null && result.size() > 0){
                    keys.addAll(result);
                }
                //再处理cursor
                cursor = ret.getStringCursor();
            }while(!cursor.equals("0"));
            return keys;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
