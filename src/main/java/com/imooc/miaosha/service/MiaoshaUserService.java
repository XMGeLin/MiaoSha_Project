package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.MiaoshaUserDao;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.exception.GlobalException;
import com.imooc.miaosha.redis.MiaoshaUserKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {

    public static final String COOKI_NAME_TOKEN="token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    /**
     * 对象级别的缓存
     * @param id
     * @return
     */
    public MiaoshaUser getById(Long id){

        //取缓存
        MiaoshaUser user=redisService.get(MiaoshaUserKey.getById,""+id,MiaoshaUser.class);
        if(user!=null){
            return user;
        }
        //取数据库
        user=miaoshaUserDao.getById(id);
        if(user!=null){
            redisService.set(MiaoshaUserKey.getById,""+id,user);   //加载到缓存。
        }
         return user;
    }

    /**
     *  更新数据库数据时候一定要更新缓存的。
     */
    public boolean updatePassword(String token,long id,String formPass){
        //取User
        MiaoshaUser user= getById(id);
        if(user==null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        MiaoshaUser toBeUpdate=new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass,user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
        //更新缓存
        redisService.delete(MiaoshaUserKey.getById,""+id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoshaUserKey.token,token,user);  //token是一串生成的数字
        return true;
    }

    /**
     * 登录功能，登录成功会返回一个CodeMsg.
     * @param loginVo
     * @return
     */
    public String login(HttpServletResponse response,LoginVo loginVo) {
        if(loginVo==null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);   //业务问题，直接抛出异常
        }
        String mobile=loginVo.getMobile();
        String formPass=loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser user= getById(Long.parseLong(mobile));
        if(user==null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        //验证密码
        String dbPass=user.getPassword();   //获取数据库里面的密码
        String salt=user.getSalt();
        String calPass=MD5Util.formPassToDBPass(formPass,salt);  //计算出来的pass
        if(!calPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        /**
         * 登录成功之后，我们保存session。   生成cookie。
         */
        String token= UUIDUtil.uuid();   //要标识一下这个token对应的是哪一个用户。
        addCookie(response,token,user);  //把cookie就写到客户端去了，cookie将存在浏览器
        return token;   //其实返回的是一个对象
    }

    public MiaoshaUser getByToken(HttpServletResponse response,String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        MiaoshaUser user= redisService.get(MiaoshaUserKey.token,token,MiaoshaUser.class);
        addCookie(response,token,user);
        return user;
    }

    /**
     * 延长有效期
     * @param response
     * @param user
     */
    public void addCookie(HttpServletResponse response,String token,MiaoshaUser user){
        //没必要每次生成一个新的cookie。
        redisService.set(MiaoshaUserKey.token,token,user);
        Cookie cookie=new Cookie(COOKI_NAME_TOKEN,token);
        //这里是cookie的有效期改成和Redis中一样
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);  //把cookie就写到客户端去了
    }
}
