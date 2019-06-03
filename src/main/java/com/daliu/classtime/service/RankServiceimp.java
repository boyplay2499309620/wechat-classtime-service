package com.daliu.classtime.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daliu.classtime.dao.RankDao;
import com.daliu.classtime.dao.UserDao;
import com.daliu.classtime.domain.RankDoMain;
import com.daliu.classtime.domain.UserDoMain;
import com.daliu.classtime.service.inservice.InRankService;

@Service
public class RankServiceimp implements InRankService{
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
	private UserDao userDao;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	/**
	 * 
	 * @Description:(作用)
	 * @param:@param openId
	 * @param:@param time   距离上一次保存后新增的计时时间  
	 * @return:void  
	 * @date:2019年4月6日
	 */
	@Transactional
	public void refreshRank(String openId,int time){
		try {
			//该用户在系统上的总时间，用来刷新redis的排行榜,time则为该次提交的时间
			int times=0;
			
			//更新数据库记录
			RankDoMain rankDoMain=rankDao.findByOpenId(openId);
			if(rankDoMain!=null){
				//该用户已经在本系统上提交过记录了
				times=time+rankDoMain.getTimes();
				rankDoMain.setTimes(times);
				rankDao.saveAndFlush(rankDoMain);
			}else{
				//该用户这是第一次提交时间记录
				//构造记录并保存
				//rankDoMain2.setId(null);
				RankDoMain rankDoMain2=new RankDoMain();
				rankDoMain2.setOpenId(openId);
				rankDoMain2.setTimes(time);
				rankDao.saveAndFlush(rankDoMain2);
				
				times=time;
				
			}
			
			//更新redis的历史记录
			//redis中并不保存历史记录，所以要传入times，周和天榜的总计时信息都在redis中，所以传入新增的计时记录即可
			historyRank(openId,times);
			
			dayRank(openId,time);
			
			weekRank(openId,time);
			
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
	public void historyRank(String openId, int times) {
		
		try {
			RankDoMain rankDoMain=(RankDoMain)redisTemplate.opsForValue().get("ran9");
			if(rankDoMain!=null && rankDoMain.getTimes() >= times){
				//该用户没进前十，不干嘛！
			}else{
				//历史排行榜里没有十个人或者该用户进了前十
				insertRank("ran", openId, times);
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		
		
		
	}
	
	
	//刷新周榜
	public void weekRank(String openId,int time){
		
		try {
			int times=time;
	        
			String key="week"+openId;
			String timeString=stringRedisTemplate.opsForValue().get(key);
			if(timeString!=null){
				//得到用户这周的总数据
				times+=Integer.parseInt(timeString);
			}
			//不设置失效时间了，因为在Schedule.java中已经设置按时失效了
			stringRedisTemplate.opsForValue().set(key,String.valueOf(times));
			
			RankDoMain rankDoMain=(RankDoMain)redisTemplate.opsForValue().get("week9");
			if(rankDoMain!=null && rankDoMain.getTimes() >= times){ 
				//该用户没进前十，不干嘛！
			}else{
				//周榜里没有十个人或者该用户进了前十
				insertRank("week",openId,times);
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		
	}
	
	//刷新日榜
	public void dayRank(String openId,int time){
		//time是这次提交的数据
		try {
			int times=time;
			
			String key="days"+openId;
			String timeString=stringRedisTemplate.opsForValue().get(key);
			if(timeString!=null){
				//得到用户今天的总数据
				times+=Integer.parseInt(timeString);
			}
			//不设置失效时间了，因为在Schedule.java中已经设置按时失效了
			stringRedisTemplate.opsForValue().set(key,String.valueOf(times));
			
			RankDoMain rankDoMain=(RankDoMain)redisTemplate.opsForValue().get("day9");
			if(rankDoMain!=null && rankDoMain.getTimes() >= times){
				//该用户没进前十，不干嘛！
			}else{
				//日榜里没有十个人或者该用户进了前十
				insertRank("day",openId,times);
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
			UserDoMain userDoMain=userDao.findByOpenId(openId);
			RankDoMain rankDoMain3=new RankDoMain();
			
			if(userDoMain.getSchoolName()==null || userDoMain.getSchoolName().equals(""))
				rankDoMain3.setName(userDoMain.getNickName());
			else 
				rankDoMain3.setName(userDoMain.getSchoolName());
			
			if(userDoMain.getSchoolId()!=null)
			rankDoMain3.setSchoolId(userDoMain.getSchoolId());
			else
			rankDoMain3.setSchoolId("");
			
			if(userDoMain.getAvatarUrl()==null) rankDoMain3.setAvatarUrl("");
			else rankDoMain3.setAvatarUrl(userDoMain.getAvatarUrl());
			
			rankDoMain3.setOpenId(openId);
			rankDoMain3.setTimes(times);
			rankDoMain3.setLove(0);
			
			int i=0;
			//找到需要变动的用户在哪一名,i=0表示第一名
			while(i<10){
				//从记录最大的开始循环遍历，找到合适的位置，将该用户插入排行榜
				RankDoMain rankDoMain=(RankDoMain)redisTemplate.opsForValue().get(rank+i);
				if(rankDoMain==null){
					//第i名没人,直接插入，退出循环
					redisTemplate.opsForValue().set(rank+i,rankDoMain3);
					break;
				}else if(times>rankDoMain.getTimes()){
					//该用户比第i名大，将第i名置换成该用户，从原第i名开始，依次后退一名。
					break;
				}
				//比较下一个
				i++;
			}
			
			//从第i个开始，依次后退一名,最后的抛弃掉。第i名前面不可能有该用户，但后面可能有该用户
			while(i<10){
				RankDoMain rankDoMain=(RankDoMain)redisTemplate.opsForValue().get(rank+i);
				if(rankDoMain==null){
					//后面都没人了
					redisTemplate.opsForValue().set(rank+i,rankDoMain3);
					break;
				}else if(rankDoMain.getOpenId().equals(openId)){
					//该用户以前就已经在排行榜中，只是现在名词靠前了
					//覆盖掉该用户，退出循环
					redisTemplate.opsForValue().set(rank+i,rankDoMain3);
					break;
				}else{
					redisTemplate.opsForValue().set(rank+i,rankDoMain3);
					rankDoMain3=rankDoMain;
					i++;
				}
				
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
			
			RankDoMain rankDoMain=new RankDoMain();
			
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
		
	
	/**
	 * 
	 * @Description:(点赞的)
	 * @param:@param string 代表哪种排行榜
	 * @param:@param ranking 代表第几名
	 * @return:void  
	 * @date:2019年3月18日
	 */
	public void clickLove(int rankType,int ranking){
		
		try {
			
			String string="day";
			if(rankType==1){
				string="week";
			}else if (rankType==2) {
				string="ran";
			}
			
			RankDoMain rankDoMain=(RankDoMain)redisTemplate.opsForValue().get(string+ranking);
			rankDoMain.setLove(rankDoMain.getLove()+1);
			redisTemplate.opsForValue().set(string+ranking, rankDoMain);
			
			if(rankType==2){
				//更新数据库中的rank表的love字段
				rankDoMain=rankDao.findByOpenId(rankDoMain.getOpenId());
				rankDoMain.setLove(rankDoMain.getLove()+1);
				rankDao.saveAndFlush(rankDoMain);
			}
			
		} catch (Exception e) {
			throw e;
		}
		
	}
}
