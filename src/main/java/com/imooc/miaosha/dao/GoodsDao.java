package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.MiaoshaGoods;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {
    //左连接，要使秒杀表里面的goodsID等于商品表里面的ID
    @Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id = g.id")
    public List<GoodsVo> listGoodsVo();

    @Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id = g.id where g.id=#{goodsId}")
    GoodsVo getGoodVoByGoodId(@Param("goodsId") long goodsId);

    /**
     * 注意，@Param("goodsId") 加了这个注解，SQL语句中可以使用#{goodsId} ,也可以使用${goodsId} ,如果不加则不能用后种方式。
     * @param g
     * @return
     */

    @Update("update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count>0")
    public int reduceStock(MiaoshaGoods g);   //解决超卖问题。1，and判断库存要大于0， 2，建立唯一索引。在userId
       //和goodID。 当然在实际开发中，会加入验证码不让一个用户同时刷新两次请求。

    @Update("update miaosha_goods set stock_count = #{stockCount} where goods_id = #{goodsId}")
    public int resetStock(MiaoshaGoods g);
}
