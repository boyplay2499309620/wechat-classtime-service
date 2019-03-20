package com.daliu.classtime.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daliu.classtime.dao.RankDao;
import com.daliu.classtime.dao.UserDao;
import com.daliu.classtime.domain.RankDoMain;
import com.daliu.classtime.domain.TimeDoMain;
import com.daliu.classtime.domain.UserDoMain;

@Service
public class Ranking {
	/**
	 * 用来刷新排行榜
	 * 
	 * 由于排行榜并非对系统有高精度要求，这里不做并发控制
	 */
	/**
	 * redis中存储有关排行榜的前十名同学
	 * 这十名同学的信息采用hash结构存取
	 * 命名规则如下：
	 * 
	 * 历史排行榜
	 * RedisTemplate.opsForValue.set("ran0",rankDoMain)
	 * RedisTemplate.opsForValue.set("ran1",rankDoMain)
	 *          ***********
	 * RedisTemplate.opsForValue.set("ran9",rankDoMain)
	 * 每位同学保留openId和times
	 * 姓名优先绑定的学号，其次是昵称
	 * 学号若是没有则为""
	 * 
	 * 周榜，RedisTemplate.opsForValue.set("week0",rankDoMain),其余同上
	 * 日榜，RedisTemplate.opsForValue.set("day0",rankDoMain),其余同上
	 * 历史排行榜参考数据库中rank表的记录，选十位最大的出来
	 * 
	 * 周榜不写入数据库，但每周一00:00刷新redis中的全部记录
	 * 日榜不写入数据库，但每天00:00刷新redis中的全部记录
	 * 
	 * redis中的所有的对象用RedisTemplate<String, Object>操作，
	 * RedisTemplate<String, Object>的序列化方式已经过特殊配置
	 * 
	 * 字符串用StringRedisTemplate操作，二者数据不相通
	 * 
	 * StringRedisTemplate保存有如下格式的信息：
	 * 1、"room"+roomId 房间的有效时间，有效期为两个小时
	 * 2、"time"+openId 用户是否在线，有效期为三分钟
	 * 3、"days"+openId 日榜的相关用户信息，有效期为一天
	 * 4、"week"+openId 周榜的相关用户信息，有效期为以一周
	 */
	
	@Autowired
	private RankDao rankDao;
	
	@Autowired
	private RankDoMain rankDoMain;
	
	@Autowired
	private RankDoMain rankDoMain2;
	
	@Autowired
	private RankDoMain rankDoMain3;
	
	@Autowired
	private UserDoMain userDoMain;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	/**
	 * 
	 * @Description:(更新总的统计时间，更新排行榜)
	 * @param:@param timeDoMain   
	 * @return:void  
	 * @date:2019年3月18日
	 */
	@Transactional
	public void refreshRank(TimeDoMain timeDoMain){
		try {
			StringBuffer strbuff=new StringBuffer(timeDoMain.getTimes());
			//约定字符串的形状是"00:00:00"
			//int a=strbuff.indexOf(":");
			//将字符串转化为以秒为单位的int型
			//time为该次提交的时间
			int time=Integer.parseInt(strbuff.substring(0,2).toString()) * 3600+
					Integer.parseInt(strbuff.substring(3,5).toString()) * 60 + 
					Integer.parseInt(strbuff.substring(6).toString());
			
			//该用户在系统上的总时间，用来刷新redis的排行榜,time则为该次提交的时间
			int times=0;
			
			//更新数据库记录
			rankDoMain=rankDao.findByOpenId(timeDoMain.getOpenId());
			if(rankDoMain!=null){
				//该用户已经在本系统上提交过记录了
				times=time+rankDoMain.getTimes();

				updateRankTimes(rankDoMain, time);
			}else{
				//该用户这是第一次提交时间记录
				//构造记录并保存
				//rankDoMain2.setId(null);
				rankDoMain2.setOpenId(timeDoMain.getOpenId());
				rankDoMain2.setTimes(time);
				rankDao.saveAndFlush(rankDoMain2);
				
				times=time;
				
			}
			
			//更新redis的历史记录
			refreshRedis(timeDoMain.getOpenId(),times);
			
			dayRank(timeDoMain,time);
			
			weekRank(timeDoMain,time);
			
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
	}
	
	/**
	 * 
	 * @Description:(更新redis中ran0到ran9的记录，这是历史排行榜)
	 * @param:@param openId
	 * @param:@param times   
	 * @return:void  
	 * @date:2019年3月18日
	 */
	public void refreshRedis(String openId, int times) {
		
		try {
			rankDoMain=(RankDoMain)redisTemplate.opsForValue().get("ran9");
			if(rankDoMain!=null && rankDoMain.getTimes() >= times){
				//该用户没进前十，不干嘛！
			}else{
				//历史排行榜里没有十个人或者该用户进了前十
				insertRank("ran", openId, times);
				/*
				//得到该用户的排行榜基本信息保存在rankDoMain3中
				userDoMain=userDao.findByOpenId(timeDoMain.getOpenId());
				
				if(userDoMain.getSchoolName()!=null)
					rankDoMain3.setName(userDoMain.getSchoolName());
				else 
				rankDoMain3.setName(userDoMain.getName());
				
				if(userDoMain.getSchoolId()!=null)
				rankDoMain3.setSchoolId(userDoMain.getSchoolId());
				else
				rankDoMain3.setSchoolId("");
				
				rankDoMain3.setOpenId(timeDoMain.getOpenId());
				rankDoMain3.setTimes(times);
				
				int i=0;
				while(i<10){
					//从记录最大的开始循环遍历，找到合适的位置，将该用户插入排行榜
					rankDoMain=(RankDoMain)redisTemplate.opsForValue().get("ran"+i);
					if(rankDoMain==null){
						//第i名没人,直接插入，退出循环
						redisTemplate.opsForValue().set("ran"+i,rankDoMain3);
						i=100;
					}else if(times>rankDoMain.getTimes()){
						//该用户比第i名大，将第i名置换成该用户，从原第i名开始，依次后退一名。
						//redisTemplate.opsForValue().set("ran"+i,rankDoMain3);
						while(i<10){
							rankDoMain=(RankDoMain)redisTemplate.opsForValue().get("ran"+i);
							if(rankDoMain==null){
								//后面都没人了
								redisTemplate.opsForValue().set("ran"+i,rankDoMain3);
								i=100;
							}else if(rankDoMain.getOpenId().equals(openId)){
								//该用户以前就已经在排行榜中，只是现在名词靠前了
								redisTemplate.opsForValue().set("ran"+i,rankDoMain3);
								i=100;
							}else{
								redisTemplate.opsForValue().set("ran"+i,rankDoMain3);
								rankDoMain3=rankDoMain;
								i++;
							}
							
						}
						
						//退出循环
						i=100;
					}
					
					//比较下一个
					i++;
				}*/
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		
		
		
	}
	
	
	//刷新周榜
	public void weekRank(TimeDoMain timeDoMain,int time){
		
		try {
			int times=time;
			String key="week"+timeDoMain.getOpenId();
			String timeString=stringRedisTemplate.opsForValue().get(key);
			if(timeString==null){
				//该用户这周第一次提交
				//得到当前星期数，1-7表示星期日-星期一
		    	Calendar c=Calendar.getInstance();
		        c.setTime(new Date());
		        int weekday=c.get(Calendar.DAY_OF_WEEK);
		        
				if(weekday==1){
					stringRedisTemplate.opsForValue().set(key,String.valueOf(time));
				}else{
					stringRedisTemplate.opsForValue().set(key,String.valueOf(time));
				}
				
				
			}else{
				//该用户这周已经提交过了
				//得到用户这周的总数据
				times+=Integer.parseInt(timeString);
				stringRedisTemplate.opsForValue().set(key,String.valueOf(times));
			}
			
			rankDoMain=(RankDoMain)redisTemplate.opsForValue().get("week9");
			if(rankDoMain!=null && rankDoMain.getTimes() >= times){
				//该用户没进前十，不干嘛！
			}else{
				//周榜里没有十个人或者该用户进了前十
				insertRank("week",timeDoMain.getOpenId(),times);
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		
	}
	
	//刷新日榜
	public void dayRank(TimeDoMain timeDoMain,int time){
		//time是这次提交的数据
		try {
			int times=time;
			String key="days"+timeDoMain.getOpenId();
			String timeString=stringRedisTemplate.opsForValue().get(key);
			if(timeString==null){
				//该用户今天第一次提交
				SimpleDateFormat df = new SimpleDateFormat("HH");//设置日期格式
				int i=Integer.parseInt(df.format(new Date()));
				
				stringRedisTemplate.opsForValue().set(key,String.valueOf(time),
						24-i,TimeUnit.HOURS);
				
			}else{
				//该用户今天已经提交过了
				//得到用户今天的总数据
				times+=Integer.parseInt(timeString);
				stringRedisTemplate.opsForValue().set(key,String.valueOf(times));
			}
			
			rankDoMain=(RankDoMain)redisTemplate.opsForValue().get("day9");
			if(rankDoMain!=null && rankDoMain.getTimes() >= times){
				//该用户没进前十，不干嘛！
			}else{
				//日榜里没有十个人或者该用户进了前十
				insertRank("day",timeDoMain.getOpenId(),times);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		
	}
	
	/**
	 * 
	 * @Description:(已经进了排行榜前十，或者排行榜没有十个人)
	 * @param:@param rank 代表哪种排行榜
	 * @param:@param timeDoMain
	 * @param:@param times   总的时间数
	 * @return:void  
	 * @date:2019年3月18日
	 */
	public void insertRank(String rank,String openId,int times){
		try {
			//得到该用户的排行榜基本信息保存在rankDoMain3中 
			userDoMain=userDao.findByOpenId(openId);
			
			if(userDoMain.getSchoolName()!=null)
				rankDoMain3.setName(userDoMain.getSchoolName());
			else 
			rankDoMain3.setName(userDoMain.getName());
			
			if(userDoMain.getSchoolId()!=null)
			rankDoMain3.setSchoolId(userDoMain.getSchoolId());
			else
			rankDoMain3.setSchoolId("");
			
			rankDoMain3.setOpenId(openId);
			rankDoMain3.setTimes(times);
			
			int i=0;
			while(i<10){
				//从记录最大的开始循环遍历，找到合适的位置，将该用户插入排行榜
				rankDoMain=(RankDoMain)redisTemplate.opsForValue().get(rank+i);
				if(rankDoMain==null){
					//第i名没人,直接插入，退出循环
					redisTemplate.opsForValue().set(rank+i,rankDoMain3);
					i=100;
				}else if(times>rankDoMain.getTimes()){
					//该用户比第i名大，将第i名置换成该用户，从原第i名开始，依次后退一名。
					//redisTemplate.opsForValue().set("ran"+i,rankDoMain3);
					while(i<10){
						rankDoMain=(RankDoMain)redisTemplate.opsForValue().get(rank+i);
						if(rankDoMain==null){
							//后面都没人了
							redisTemplate.opsForValue().set(rank+i,rankDoMain3);
							i=100;
						}else if(rankDoMain.getOpenId().equals(openId)){
							//该用户以前就已经在排行榜中，只是现在名词靠前了
							//覆盖掉该用户，退出循环
							redisTemplate.opsForValue().set(rank+i,rankDoMain3);
							i=100;
						}else{
							redisTemplate.opsForValue().set(rank+i,rankDoMain3);
							rankDoMain3=rankDoMain;
							i++;
						}
						
					}
					
					//退出循环
					i=100;
				}
				
				//比较下一个
				i++;
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
	}
	
	//查询排行榜
	public List<ArrayList< RankDoMain >> queryRank(){
		try {
			List<ArrayList< RankDoMain >> list=new ArrayList<ArrayList< RankDoMain >>();
			ArrayList<RankDoMain> arrayListHostory=new ArrayList<RankDoMain>();
			ArrayList<RankDoMain> arrayListWeek=new ArrayList<RankDoMain>();
			ArrayList<RankDoMain> arrayListDay=new ArrayList<RankDoMain>();
			
			//返回日榜
			for (int i = 0; i < 10; i++) {
				rankDoMain=(RankDoMain)redisTemplate.opsForValue().get("day"+i);
				if(rankDoMain!=null)
					arrayListDay.add(rankDoMain);
				else  break;
			}
			list.add(arrayListDay);
			
			//返回周榜
			for (int i = 0; i < 10; i++) {
				rankDoMain=(RankDoMain)redisTemplate.opsForValue().get("week"+i);
				if(rankDoMain!=null)
					arrayListWeek.add(rankDoMain);
				else  break;
			}
			list.add(arrayListWeek);
			
			//返回历史排行榜
			for (int i = 0; i < 10; i++) {
				rankDoMain=(RankDoMain)redisTemplate.opsForValue().get("ran"+i);
				if(rankDoMain!=null)
					arrayListHostory.add(rankDoMain);
				else  break;
			}
			list.add(arrayListHostory);
			
			return list;
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
	}
	
	@Transactional
	//在数据库记录表rank中更新记录
	public void updateRankTimes(RankDoMain rank,int time){
		rank.setTimes(rank.getTimes()+time);
	}
		
		
}
