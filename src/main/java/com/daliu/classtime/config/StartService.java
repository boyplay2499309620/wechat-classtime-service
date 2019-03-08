package com.daliu.classtime.config;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.daliu.classtime.dao.RankDao;
import com.daliu.classtime.dao.RoomDao;
import com.daliu.classtime.dao.UserDao;
import com.daliu.classtime.domain.RankDoMain;
import com.daliu.classtime.domain.UserDoMain;
import com.daliu.classtime.test.internetTest;

/**
 * 继承Application接口后项目启动时会按照执行顺序执行run方法
 * 通过设置Order的value来指定执行的顺序
 */
@Component
@Order(value = 1)
public class StartService implements ApplicationRunner {
	
	@Autowired
	private  RankDoMain rankDoMain;
	
	@Autowired
	private  RankDao rankDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired  
	private UserDoMain user;
	
	@Autowired
	private  RedisTemplate<String, Object> redisTemplate;
	
    @Override
    public void run(ApplicationArguments args) throws Exception {
    	loadRedis();
    }
    
    public void loadRedis(){
    	System.out.println("******   开始加载redis          ****** ");
        for (int i = 0; i < 10; i++) {
        	redisTemplate.delete("ran"+i);
		}
        System.out.println("******   清空redis中的排行榜！              ****** ");
        System.out.println("******   redis开始加载排行榜数据！       ****** ");
        //System.out.println(rankDao.findRank());
        List<RankDoMain> list=rankDao.findRank();
        int i=0;
        for(RankDoMain rank : list){
        	user=userDao.findByOpenId(rank.getOpenId());
        	if(user.getSchoolName()!=null) rank.setName(user.getSchoolName());
        	else rank.setName(user.getName());
        	
        	if(user.getSchoolId()!=null) rank.setSchoolId(user.getSchoolId());
        	else rank.setSchoolId("");
        	
        	redisTemplate.opsForValue().set("ran"+i,rank);
        	i++;
        }
        System.out.println("******   redis加载排行榜数据完成！       ****** ");
    }

}
