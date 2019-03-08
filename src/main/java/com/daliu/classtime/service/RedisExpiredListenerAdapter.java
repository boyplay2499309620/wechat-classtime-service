package com.daliu.classtime.service;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * @author zhuzhen
 * @version 1.0
 * @description TODO 自定义Redis过期监听事件
 * @className com.nongcai.rabbitmq.demo.redis.RedisExpiredListener
 * @date 2018/12/21 14:42
 */
public class RedisExpiredListenerAdapter implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] bytes) {
        // 建议使用: valueSerializer
        byte[] body = message.getBody();
        byte[] channel = message.getChannel();
        //Redis数据的键
        String redisId = new String(body);

        System.out.println("onMessage >> " );
        System.out.println(String.format("channel: %s \n body: %s \n bytes: %s"
                ,new String(channel), new String(body), new String(bytes)));

    }
}
