package com.imooc.miaosha.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MQConfig {

    public static final String MIOAHSA_QUEUE="miaosha.queue";

    public static final String QUEUE="queue";
    public static final String TOPIC_QUEUE1="topic.queue1";
    public static final String TOPIC_QUEUE2="topic.queue2";
    public static final String TOPIC_EXCHANGE="topicExchange";
    public static final String FANOUT_EXCHANGE="fanoutExchange";

    //    public static final String ROUNT_KEY1="topic.key1";
//    public static final String ROUNT_KEY2="topic.#";   //*表示一个单词，#表示0个或者多个单词


    /**
     * drict模式，最简单的一种。
     *
     * 其实也就是直接连接交换机，生产者投递的消息被交换机转发到通过rounting key绑定到具体的某个
     * Queue，把消息放入队列，然后消费者从Queue中订阅消息。
     */

    @Bean
    public Queue queue(){
        return new Queue(MIOAHSA_QUEUE,true);  //默认是持久化的
    }

    /**
     * topic模式，
     *
     * 举个现实生活中的栗子：
     *
     * 假如你想在淘宝上买一双运动鞋，那么你是不是会在搜索框中搜“XXX运动鞋”，
     * 这个时候系统将会模糊匹配的所有符合要求的运动鞋，然后展示给你。
     */

    @Bean
    public Queue topicQueue1(){
        return new Queue(TOPIC_QUEUE1,true);
    }

    @Bean
    public Queue topicQueue2(){
        return new Queue(TOPIC_QUEUE2,true);
    }

    @Bean
    public TopicExchange topibExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);   //先把消息放到交换机
    }
    @Bean
    public Binding topicBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(topibExchange()).with("topic.key1");
    }
    @Bean
    public Binding topicBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(topibExchange()).with("topic.#");
    }
    /**
     *  Fanout 广播模式，使用这个交换机不需要routingkey绑定，和路由没有关系，它是直接绑定到队列的。
     */
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);   //先把消息放到交换机
    }
    @Bean
    public Binding FanoutBinding1(){     //广播模式不需要key
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }
    @Bean
    public Binding FanoutBinding2(){     //广播模式不需要key
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }

}
