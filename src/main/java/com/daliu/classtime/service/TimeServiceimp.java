package com.daliu.classtime.service;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daliu.classtime.dao.RankDao;
import com.daliu.classtime.dao.RoomPeopleDao;
import com.daliu.classtime.dao.TimeDao;
import com.daliu.classtime.dao.UserDao;
import com.daliu.classtime.domain.RankDoMain;
import com.daliu.classtime.domain.RoomPeopleDoMain;
import com.daliu.classtime.domain.TimeDoMain;
import com.daliu.classtime.domain.UserDoMain;
import com.daliu.classtime.service.inservice.InTimeService;



@Service
public class TimeServiceimp implements InTimeService {
	
	@Autowired
	private TimeDao timeDao; 
	
	@Autowired
	private TimeDoMain timeDoMain;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private RoomPeopleDoMain roomPeopleDoMain;
	
	@Autowired
	private RoomPeopleDao roomPeopleDao;
	
	@Autowired
	private RoomServiceimp roomServiceimp;
	
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
	
	
	public Page<TimeDoMain> findByOpenId(String openId,Pageable pageable){
		//查询我的记录
        Page<TimeDoMain> pages=null;
        try{
        	pages=timeDao.findByOpenId(openId,pageable);
        	return pages;
        }catch(Exception e){
        	System.out.println("TimeServiceimp  findByOpenId---"+e);
        	throw e;
        }
	}
	

    //计时结束时提交的信息
	@Transactional
	public TimeDoMain saveAll(TimeDoMain timeDoMain1 ) throws Exception {
		try {
			/**
			 * timeDoMain.setId(null);的作用如下
			 * 如果不设置为null，那么在timeDoMain1不销毁的的这段时间类，
			 * 下一次的提交将覆盖数据库中的上一次提交，
			 * 只有设置主键为null后，
			 * 数据库中才会重新建立一条记录
			 */
			/**
			 * 针对以上原因：
			 * 将TimeDoMain的scop改为prototype模式，
			 * 每次请求都创建一个TimeDoMain
			 * 但这里人保留将id设置为null的习惯
			 */
			//System.out.println(timeDoMain1);
			timeDoMain1.setId(null);
			//System.out.println("service"+timeDoMain1);
			
			timeDoMain=timeDao.save(timeDoMain1);
			
			//如果加入了房间，则更新room_people表的times字段，将时间加上去
			if(timeDoMain1.getRoomId()!=1){
				
				roomPeopleDoMain=roomPeopleDao.findByOpenIdAndRoomId(timeDoMain1.getOpenId(), 
						timeDoMain1.getRoomId());
				if(roomPeopleDoMain!=null){
					String str=timeDoMain1.getTimes();
					
					StringBuffer strbuff=new StringBuffer(str);
					//约定字符串的形状是"00:00:00"
					//int a=strbuff.indexOf(":");
					//将字符串转化为以秒为单位的int型
					int time=Integer.parseInt(strbuff.substring(0,2).toString()) * 3600+
							Integer.parseInt(strbuff.substring(3,5).toString()) * 60 + 
							Integer.parseInt(strbuff.substring(6).toString());
					
						
					roomServiceimp.updataTime(roomPeopleDoMain,time);
				}else{
					throw new Exception("没有在room_people表中找到openId为"+timeDoMain1.getOpenId()+
							"roomId为"+timeDoMain1.getRoomId()+"的记录！"+"详细信息如下："+
							timeDoMain1.toString());
				}
	
			}
			
			return timeDoMain;
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		
		
	}

	//更新总的统计时间，更新排行榜
	//redis中存放有排行榜中有前五位的记录
	@Transactional
	public void refreshRank(TimeDoMain timeDoMain){
		try {
			String str=timeDoMain.getTimes();
			StringBuffer strbuff=new StringBuffer(str);
			//约定字符串的形状是"00:00:00"
			//int a=strbuff.indexOf(":");
			//将字符串转化为以秒为单位的int型
			int time=Integer.parseInt(strbuff.substring(0,2).toString()) * 3600+
					Integer.parseInt(strbuff.substring(3,5).toString()) * 60 + 
					Integer.parseInt(strbuff.substring(6).toString());
			
			//该用户在系统上的总时间，用来刷新redis的排行榜
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
				rankDoMain2.setId(null);
				rankDoMain2.setOpenId(timeDoMain.getOpenId());
				rankDoMain2.setTimes(time);
				rankDao.saveAndFlush(rankDoMain2);
				
				times=time;
			}
			
			//更新redis记录
			refreshRedis(timeDoMain.getOpenId(),times);
			
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
	}
	
	//更新redis中ran0到ran9的记录
	public void refreshRedis(String openId, int times) {
		/**
		 * redis中存储有关排行榜的前十名同学
		 * 这十名同学的信息采用hash结构存取
		 * 命名规则如下：
		 * RedisTemplate.opsForValue.set("ran0",rankDoMain)
		 * RedisTemplate.opsForValue.set("ran1",rankDoMain)
		 *          ***********
		 * RedisTemplate.opsForValue.set("ran9",rankDoMain)
		 * 每位同学保留openId和times
		 * 姓名优先绑定的学号，其次是昵称
		 * 学号若是没有则为""
		 */
		
		/**
		 * 由于排行榜并非对系统有高精度要求，这里不做并发控制
		 */
		
		rankDoMain=(RankDoMain)redisTemplate.opsForValue().get("ran9");
		if(rankDoMain!=null && rankDoMain.getTimes() >= times){
			//该用户没进前十，不干嘛！
		}else{
			//系统里没有十个人或者该用户进了前十
			
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
			}
		}
		
	}
	
	//查询排行榜
	public ArrayList< RankDoMain > queryRank(){
		try {
			ArrayList<RankDoMain> list=new ArrayList<RankDoMain>();
			for (int i = 0; i < 10; i++) {
				rankDoMain=(RankDoMain)redisTemplate.opsForValue().get("ran"+i);
				if(rankDoMain!=null)
				list.add(rankDoMain);
				else  break;
			}
			return list;
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
	}
	
	//在线
    public void online(String openId){
    	try {
    		//stringRedisTemplate.opsForValue().set(openId,"ok");
        	//五分钟后就过期
    		stringRedisTemplate.opsForValue().set("time"+openId, "ok",250,TimeUnit.SECONDS);//向redis里存入数据和设置缓存时间
        	//stringRedisTemplate.expire(openId,200,TimeUnit.SECONDS);
        	//System.out.println("online:");	
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
    }
	
    //暂停
	public void suspend(String openId){
		try {
			stringRedisTemplate.delete("time"+openId);
			//System.out.println("suspend:");
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
	}
	
	public boolean queryOnline(String openId){
		if(stringRedisTemplate.opsForValue().get("time"+openId).equals("ok")){
			return true;
		}else{
			return false;
		}
	}

	
	@Transactional
	public void updateRankTimes(RankDoMain rank,int time){
		rank.setTimes(rank.getTimes()+time);
	}


	
}
