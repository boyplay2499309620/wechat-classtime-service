package com.daliu.classtime.service;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.aspectj.apache.bcel.generic.ReturnaddressType;
import org.hibernate.loader.plan.exec.process.spi.ReturnReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daliu.classtime.dao.RoomDao;
import com.daliu.classtime.dao.RoomPeopleDao;
import com.daliu.classtime.domain.RoomDoMain;
import com.daliu.classtime.domain.RoomPeopleDoMain;
import com.daliu.classtime.domain.UserDoMain;
import com.daliu.classtime.service.inservice.InRoomService;
import com.daliu.classtime.test.internetTest;



@Service
public class RoomServiceimp implements InRoomService{
	
	//@Autowired
	//不能用@Autowired注入，好像系统规定的包要认为的new一下
	//private Random random=new Random();
	//因为没有创建，springboot不能管理
	
	@Value("${room_time}")
	int roomTime;
	
	@Autowired
	private Random random;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private RoomDoMain room;
	
	@Autowired
	private RoomDoMain room1;
	
	@Autowired
	private RoomDao roomDao;
	
	@Autowired
	private UserDoMain user;
	
	@Autowired
	private UserServiceImp users;
	
	@Autowired
	private RoomPeopleDao roomPeopleDao;
	
	@Autowired
	private RoomPeopleDoMain roomPeopleDoMain;
	
	@Autowired 
	private RoomPeopleDoMain roomPeopleDoMain1;
	
	//findByRoomId(Integer roomId)
	public RoomDoMain findByRoomId(Integer roomId){
		try {
			return roomDao.findByRoomId(roomId);
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
	}
	
	//根据roonId查找所有进入该房间的人
	public List<RoomPeopleDoMain> findAllRoomId(Integer roomId){
		try {
			List<RoomPeopleDoMain> list=roomPeopleDao.findByRoomId(roomId);
			
			if(list==null){
				//System.out.println("房间号为"+roomId+"里面没人！");
				return null;
			}else{
				//System.out.println("这是向前端发送的房间号为"+roomId+"的数据");
				
				for(RoomPeopleDoMain roomPeopleDoMain :list){
					String str=stringRedisTemplate.opsForValue().get(roomPeopleDoMain.getOpenId());
					if(str==null){
						roomPeopleDoMain.setState(0);
					}else if(str.equals("ok")){
						roomPeopleDoMain.setState(1);
					}else{
						roomPeopleDoMain.setState(0);
					}
					//System.out.println(roomPeopleDoMain);
				}
				return list;
			}
		} catch (Exception e) {
			throw e;
			//return null;
		}
		
	}
	
	//查找我所创建过的房间
	public Page<RoomDoMain> myRoom(String openId,Pageable pageable){ 
		Page<RoomDoMain> page=null;
		
		try{
			page=roomDao.findByOpenId(openId, pageable);
			return page;
		}catch(Exception e){
			throw e;
		}
		
	}
	
	//创建一个房间,系统生成房间号
	@Transactional
	public RoomDoMain createNum(String openId,String remark){
		try {
			//找到一个可用的房间号
			Boolean flag=true;
			int a=0;
			while(flag){
				a=random.nextInt(999)%100+100;
				if(a<1 || a>999) continue;
				if(roomDao.findByRoomNumberAndRoomState(a,1)!=null){
					continue;
				}else{
					flag=false; 
				}
			}
			
			//现将该用户上一次创建的还没有结束房间标志   为结束
			room1=roomDao.findByOpenIdAndRoomState(openId,1);
			if(room1!=null){
				updateRoomState(room1);
			}
			
			//构造一条新纪录
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			room.setRoomNumber(a);
			room.setRoomState(1);
			room.setRemark(remark);
			room.setOpenId(openId);
			room.setRoomPeoples(0);
			room.setRoomId(null);
			room.setCreateTime(df.format(new Date()));
			
			//return(roomDao.save(room));
			//不立即刷新则加入房间时会报错，因为查不到房间
			roomDao.saveAndFlush(room);
			
			//在redis中添加一条该房间的记录，使其在两个小时以后自动失效
			stringRedisTemplate.opsForValue().set("room"+String.valueOf(a), String.valueOf(a),roomTime,TimeUnit.HOURS);
			
			//该用户自动进入该房间内
			//放到control中去
			//this.getRoom(openId, a);
			
			return room;
		} catch (Exception e) {
			//System.out.println("eee");
			throw e;
		}
		
		
	}
	
	//创建一个房间,人为指定房间号
	@Transactional
	public RoomDoMain createNumByNum(String openId,String remark,Integer number){
		try {
			if(roomDao.findByRoomNumberAndRoomState(number,1)!=null){
				//表明这个number正在使用
				return null;
			}else{
				//查询该用户是否有上一间房还未到期，有的话让其到期
				room1=roomDao.findByOpenIdAndRoomState(openId,1);
				if(room1!=null){
					updateRoomState(room1);
				}
				
				//构造一条记录
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				room.setOpenId(openId);
				room.setRemark(remark);
				room.setRoomId(null);
				room.setRoomNumber(number);
				room.setRoomState(1);
				room.setRoomPeoples(0);
				room.setCreateTime(df.format(new Date()));
				
				//在redis中添加一条该房间的记录，使其在两个小时以后自动失效
				stringRedisTemplate.opsForValue().set("room"+String.valueOf(number), String.valueOf(number),roomTime,TimeUnit.HOURS);
				
				//该用户自动进入该房间内
				//放到control中去
				//this.getRoom(openId, number);
				
				//return(roomDao.save(room));
				return(roomDao.saveAndFlush(room));
			}
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	/**
	 * 使用了@Transactional的方法，对同一个类里面的方法调用， @Transactional无效
	 *但这里仍然添加了@Transactional，以防外部类的调用
	 */
	@Transactional
	public void updateRoomState(RoomDoMain room){
		//将RoomState置位0
		//System.out.println(room.getRoomId()+":"+room.getRoomState());
		try {
			room.setRoomState(0);
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	
	//进入房间
	@Transactional
	synchronized public RoomDoMain getRoom(String openId,Integer roomNumber){
		try {
			//判断房间是否存在，不存在返回null，否则返回room信息
			
			room=roomDao.findByRoomNumberAndRoomState(roomNumber, 1);
			
			if(room!=null){ 
				//房间存在,向room_people表插入一条记录
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

				roomPeopleDoMain=roomPeopleDao.findByOpenIdAndRoomId(openId,room.getRoomId());
				
				if(roomPeopleDoMain==null){
					//表明以前没进来过
					//插入一条新纪录
					//System.out.println(openId+"---"+roomNumber);
					//查询姓名
					user=users.findByOpenId(openId);
					
					roomPeopleDoMain1.setId(null);
					roomPeopleDoMain1.setOpenId(openId);
					roomPeopleDoMain1.setRoomId(room.getRoomId());
					roomPeopleDoMain1.setBegainTime(df.format(new Date()));
					//roomPeopleDoMain1.setName(user.getSchoolName());
					roomPeopleDoMain1.setTimes(0);
					//roomPeopleDoMain1.setState(0);
					if(user.getSchoolName()==null||user.getSchoolName().equals(""))
						//要是没有绑定学校姓名，就用昵称
						roomPeopleDoMain1.setName(user.getName());
					else roomPeopleDoMain1.setName(user.getSchoolName());
					
					roomPeopleDao.save(roomPeopleDoMain1);
					
					//System.out.println("已经入房间"+openId+"--"+room.getRoomId());
					
					//房间人数加一
					updataAddRoom(room);
					
					return room;
				}else{
					//以前已经进来过
					//直接返回房间id，啥都不干
					return room;
					
				}
				
				
			}else{
				return null;
			}
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	//房间人数加一
	@Transactional
	public void updataAddRoom(RoomDoMain room){
		//System.out.println(room.getRoomPeoples());
		//System.out.println(room.getRoomPeoples());
		//有时这些信息不能更新数据库的表，后来我在调用该方法上也加了@Transactional又暂时能更新了
		try {
			Integer i=room.getRoomPeoples();
			i++;
			room.setRoomPeoples(i);
		} catch (Exception e) {
			throw e;
		}
	}
	
	//更新room_people表的time字段
	@Transactional
	synchronized public void updataTime(RoomPeopleDoMain roomPeopleDoMain,int time){
		
		try {
			if(roomPeopleDoMain.getTimes()==null){
				roomPeopleDoMain.setTimes(time);
			}else{
				roomPeopleDoMain.setTimes(time+roomPeopleDoMain.getTimes());
			}
		} catch (Exception e) {
			throw e;
		}
	}

}
