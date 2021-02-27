package com.imooc.miaosha.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MQConnectionUtils {

    /** 队列名称 */
    private static final String QUEUE_NAME = "test_queue";

    public static void main(String[] args) throws Exception {
        /** 1.获取连接 */
        Connection newConnection = MQConnectionUtils.getConnection();
        /** 2.创建通道 */
        Channel channel = newConnection.createChannel(true);
        /** 3.创建队列声明 */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        /** 4.发送消息 */
        try {

            /** 4.1 开启事务 */
            channel.txSelect();
            String msg = "我是生产者生成的消息";
            System.out.println("生产者发送消息："+msg);
            channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
            /** 4.2 提交事务 - 模拟异常 */
            int i = 1/0;
            channel.txCommit();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("发生异常，我要进行事务回滚了！");
            /** 4.3 事务回滚 */
            channel.txRollback();
        }finally {
            channel.close();
            newConnection.close();
        }

    }
    public static Connection getConnection() throws Exception {
        //定义连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        //设置vhost
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("guest");
        //通过工厂获取连接
        Connection connection = (Connection) factory.newConnection();
        return connection;
    }

}
