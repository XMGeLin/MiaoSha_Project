package com.imooc.miaosha.rabbitmq;

import com.imooc.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    private static Logger log=LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;
    public void sendMiaoshaMessage(MiaoshaMessage mm) {
          String msg= RedisService.beanToString(mm);
          log.info("send message:"+msg);
          amqpTemplate.convertAndSend(MQConfig.MIOAHSA_QUEUE,msg);
    }

//    public void send(Object message){
//          String msg= RedisService.beanToString(message);
//          log.info("send message:"+msg);
//          amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
//    }
//
    public void sendTopic(Object message){
        String msg= RedisService.beanToString(message);
        log.info("send topic message:"+msg);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key1",msg+"1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key2",msg+"2");

    }
//
//    public void sendFanout(Object message){
//        String msg= RedisService.beanToString(message);
//        log.info("send fanout message:"+msg);
//        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg);  //这里必须是要传入空字符串的
//    }

}
