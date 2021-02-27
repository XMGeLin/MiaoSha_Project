package com.imooc.miaosha.vo;

import com.imooc.miaosha.domain.Goods;

import java.util.Date;

public class GoodsVo extends Goods {

    // 把两个表的数据拼到一起
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
    private Double miaoshaPrice;

    public void setMiaoshaPrice(Double miaoshaPrice) {
        this.miaoshaPrice = miaoshaPrice;
    }

    public Double getMiaoshaPrice() {
        return miaoshaPrice;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public Integer getStockCount() {
        return stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}
