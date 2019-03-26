/**
 * 
 */
package com.daliu.classtime.service;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.daliu.classtime.utils.ErrorMsg;

/**  
* @Title: Schedule.java
* @Package:com.daliu.classtime.service
* @Description:(定时任务的实现类)
* @author:刘严岩 
* @date:2019年3月24日
*/
@Component
public class Schedule {
	
	@Autowired
	private  RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	private static Logger logger = LogManager.getLogger("system");
	
	
	/**
	 * 
	 * @Description:(每天23点56分开始执行,清空日榜)
	 * @param:   
	 * @return:void  
	 * @date:2019年3月24日
	 */
	@Scheduled(cron="0 56 23 * * ?")
	public void clearnDayRanking(){
		try {
			//清空日榜
	        for (int i = 0;  i<10; i++) {
	        	redisTemplate.delete("day"+i);
			}
	        
	        //清空周榜和日榜提交的数据
	        Set<String> keys=stringRedisTemplate.keys("days"+"*");
	        stringRedisTemplate.delete(keys);
	        
	        logger.info("clearnDayRanking");
		} catch (Exception e) {
			// TODO: handle exception
			ErrorMsg msg=new ErrorMsg();
			logger.error(msg.getStackTrace(e));
		}
          
	}
	
	/**
	 * 
	 * @Description:(每周星期日23点56分开始执行,清空周榜)
	 * @param:   
	 * @return:void  
	 * @date:2019年3月24日
	 */
	@Scheduled(cron="0 56 23 ? * 1")
	public void clearnWeekRanking(){
		try {
			//清空周榜
	        for (int i = 0;  i<10; i++) {
	        	redisTemplate.delete("week"+i);
			}
	        
	        //清空周榜和日榜提交的数据
	        Set<String> keys=stringRedisTemplate.keys("week"+"*");
	        stringRedisTemplate.delete(keys);
	        
	        logger.info("clearnWeekRanking");
		} catch (Exception e) {
			// TODO: handle exception
			ErrorMsg msg=new ErrorMsg();
			logger.error(msg.getStackTrace(e));
		}
	}

}
