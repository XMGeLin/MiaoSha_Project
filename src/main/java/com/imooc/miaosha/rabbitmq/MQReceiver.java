package com.imooc.miaosha.rabbitmq;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    private static Logger log= LoggerFactory.getLogger(MQReceiver.class);

    @RabbitListener(queues = MQConfig.MIOAHSA_QUEUE)
    public void receive(String message){
        log.info("receive message:"+message);
        MiaoshaMessage mm=RedisService.stringToBean(message,MiaoshaMessage.class);
        MiaoshaUser user= mm.getUser();
        long goodsId=mm.getGoodsId();

        GoodsVo goods = goodsService.getGoodVoByGoodId(goodsId);
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return;
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        miaoshaService.miaosha(user, goods);
    }

    /**
     * @RabbitListener 底层使用 AOP 进行异常通知拦截，如果程序没有抛出异常信息，那么就会自动提交事务；
     * 如果 AOP 异常通知拦截有捕获到异常信息的话，就会自动实现重试(补偿)机制，同时，这个补偿机制的消息
     * 会缓存到 RabbitMQ 服务器端进行存放，一直重试到不抛出异常为止。
     *
     * 我们通过 RabbitMQ 配置，增加了 RabbitMQ 重试时间以及重试次数限制，在一定程度上解决了重复消费的问题，
     *
     * @param message
     */
//    @RabbitListener(queues = MQConfig.QUEUE)
//    public void receive(String message){
//        log.info("receive message:"+message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
//    public void receiveTopic1(String message){
//        log.info("topic queue1 message:"+message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
//    public void receiveTopic2(String message){
//        log.info("topic queue2 message:"+message);
//    }
}
