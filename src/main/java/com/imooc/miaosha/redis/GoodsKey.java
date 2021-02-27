package com.imooc.miaosha.redis;

public class GoodsKey extends BasePrefix{
    private GoodsKey(int expires,String prefix){
        super(expires,prefix);
    }

     public static GoodsKey getGoodsList= new GoodsKey(60,"gl");//其实是一个静态类对象。
    public static GoodsKey getMiaoshaGoodsStock= new GoodsKey(0,"gs");//其实是一个静态类对象。


}
