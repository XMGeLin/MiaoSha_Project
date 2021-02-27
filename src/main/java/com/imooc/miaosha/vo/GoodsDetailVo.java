package com.imooc.miaosha.vo;

import com.imooc.miaosha.domain.MiaoshaUser;

public class GoodsDetailVo {
    private int miaoshaStatus = 0;
    private int remainSeconds = 0;
    private GoodsVo goods;
    private MiaoshaUser user;

    public void setUser(MiaoshaUser user) {
        this.user = user;
    }

    public MiaoshaUser getUser() {
        return user;
    }

    public void setMiaoshaStatus(int miaoshaStatus) {
        this.miaoshaStatus = miaoshaStatus;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    public int getMiaoshaStatus() {
        return miaoshaStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public GoodsVo getGoods() {
        return goods;
    }
}
