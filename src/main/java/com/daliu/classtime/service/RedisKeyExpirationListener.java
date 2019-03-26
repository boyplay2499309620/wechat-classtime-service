package com.daliu.classtime.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import com.daliu.classtime.dao.RoomDao;
import com.daliu.classtime.domain.RoomDoMain;

/**
 * @author zhuzhen
 * @version 1.0
 * @description TODO 监听所有db的过期事件__keyevent@*__:expired"
 * @className com.nongcai.rabbitmq.demo.redis.RedisKeyExpirationListener
 * @date 2018/12/21 14:39
 */
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
	
	private static Logger logger = LogManager.getLogger("redis");
	
	@Autowired
	RoomServiceimp roomServiceimp;
	
	@Autowired
	RoomDao roomDao;
	

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 用户做自己的业务处理即可,注意message.toString()可以获取失效的key
		 /*System.out.println("message>>>  " + message);
		 System.out.println("pattern>>>  " + new String(pattern));
		 String expiredKey = message.toString();
		 System.out.println("Redis的键：" + expiredKey);
		 */
    	//由于失效无法获得value信息，必须在key中携带value信息
    	try {
    		//约定key的前四位用于表示标识信息
    		String str=message.toString();
    		
    		//防止有key的长度小于4的值过来时报错
    		if(str.length()<4) return;
    		
        	String key=str.substring(0,4);
        	String value=str.substring(4);
        	//System.out.println(key+"---"+value);
        	switch (key) {
    		case "room":
    			//房间失效，value为房间号码
    			RoomDoMain roomDoMain=roomDao.findByRoomNumberAndRoomState(Integer.parseInt(value),1);
    			//可能用户又重新创建了房间，该房间已经失效
    			if(roomDoMain!=null){
    				roomDoMain.setRoomState(0);
    				roomDao.saveAndFlush(roomDoMain);
    			}
    			break;
    		case "time":
    			//加入房间的用户到时间未发送在线信息，表示退出房间，这里暂时不干嘛
    			break;

    		default:
    			//可能还有排行榜中的信息失效，这里暂不操心
    			//logger.info(message.toString());
    			//System.out.println("RedisKeyExpirationListener unknow :"+str);
    			break;
    		}
		} catch (Exception e) {
			logger.error(message.toString()+"---"+e);
			System.out.println("RedisKeyExpirationListener error :"+message.toString());
		}
    	

    }
}
