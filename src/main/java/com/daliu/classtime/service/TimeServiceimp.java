package com.daliu.classtime.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daliu.classtime.dao.RoomPeopleDao;
import com.daliu.classtime.dao.TimeDao;
import com.daliu.classtime.domain.RoomPeopleDoMain;
import com.daliu.classtime.domain.TimeDoMain;
import com.daliu.classtime.service.inservice.InTimeService;



@Service
public class TimeServiceimp implements InTimeService {
	
	@Autowired
	private TimeDao timeDao; 
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private RoomPeopleDao roomPeopleDao;
	

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
			/*
			 * 程序修改处于Persistent状态的实体的id,hibernate是不允许这样操作的，所以保存失败。
			 * 还是不设置id为null了
			 */
			//System.out.println(timeDoMain1);
			//timeDoMain1.setId(null);
			//System.out.println("service"+timeDoMain1);
			
			TimeDoMain timeDoMain=timeDao.saveAndFlush(timeDoMain1);
			
			//如果加入了房间，则更新room_people表的times字段，将时间加上去
			if(timeDoMain1.getRoomId()!=1){
				
				RoomPeopleDoMain roomPeopleDoMain=roomPeopleDao.findByOpenIdAndRoomId(
						timeDoMain1.getOpenId(), timeDoMain1.getRoomId());
				if(roomPeopleDoMain!=null){
					
			        int time=timeDoMain1.getTimes();
					
					//更新时间
					if(roomPeopleDoMain.getTimes()==null){
						roomPeopleDoMain.setTimes(time);
					}else{
						roomPeopleDoMain.setTimes(roomPeopleDoMain.getTimes()+time);
					}
					roomPeopleDao.saveAndFlush(roomPeopleDoMain);
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


	/* (non-Javadoc)
	 * @see com.daliu.classtime.service.inservice.InTimeService#findById(java.lang.Integer)
	 */

	public TimeDoMain findByTimeId(Integer id) {
		// TODO Auto-generated method stub
		return timeDao.findByTimeId(id);
	}

	
}
