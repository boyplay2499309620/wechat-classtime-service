package com.daliu.classtime.config;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.daliu.classtime.dao.RankDao;
import com.daliu.classtime.dao.UserDao;
import com.daliu.classtime.domain.RankDoMain;
import com.daliu.classtime.domain.UserDoMain;
/**
 * 继承Application接口后项目启动时会按照执行顺序执行run方法
 * 通过设置Order的value来指定执行的顺序
 */
@Component
@Order(value = 1)
public class StartService implements ApplicationRunner {
	
	@Autowired
	private  RankDao rankDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private  RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	

    @Value("${spring.mail.username}")
    private String Sender; //读取配置文件中的参数
	
    @Override
    public void run(ApplicationArguments args) throws Exception {
    	//加载排行榜
    	loadRedis();
    	
    	
    	//UserServiceImp.saveModel(modelDoMain);
    	
    }
    
    /**
     * 
     * @Description:(系统启动时加载redis的历史排行榜，同时清空日榜和周榜)
     * @param:   
     * @return:void  
     * @date:2019年3月18日
     */
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
        	
        	UserDoMain user=userDao.findByOpenId(rank.getOpenId());
        	
        	if(user.getSchoolName()==null || user.getSchoolName().equals("")) rank.setName(user.getNickName());
        	else rank.setName(user.getSchoolName());
        	
        	if(user.getSchoolId()!=null) rank.setSchoolId(user.getSchoolId());
        	else rank.setSchoolId("");
        	
        	if(user.getAvatarUrl()==null) rank.setAvatarUrl("");
			else rank.setAvatarUrl(user.getAvatarUrl());
        	
        	redisTemplate.opsForValue().set("ran"+i,rank);
        	
        	i++;
        }
        
        //清空周榜和日榜
        for (i = 0;  i<10; i++) {
        	redisTemplate.delete("day"+i);
        	redisTemplate.delete("week"+i);
		}
        
        //清空周榜和日榜提交的数据
        Set<String> keys=stringRedisTemplate.keys("days"+"*");
        stringRedisTemplate.delete(keys);
        keys=stringRedisTemplate.keys("week"+"*");
        stringRedisTemplate.delete(keys);
        
        System.out.println("******   redis加载排行榜数据完成！       ****** ");
    }

}
