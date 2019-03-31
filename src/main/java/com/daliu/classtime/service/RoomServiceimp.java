package com.daliu.classtime.service;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daliu.classtime.dao.RoomDao;
import com.daliu.classtime.dao.RoomPeopleDao;
import com.daliu.classtime.domain.RoomDoMain;
import com.daliu.classtime.domain.RoomPeopleDoMain;
import com.daliu.classtime.domain.UserDoMain;
import com.daliu.classtime.service.inservice.InRoomService;



@Service
public class RoomServiceimp implements InRoomService{
	
	//@Autowired
	//不能用@Autowired注入，好像系统规定的包要认为的new一下
	//private Random random=new Random();
	//因为没有创建，springboot不能管理
	
	@Value("${room_time}")
	int roomTime;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private RoomDao roomDao;
	
	@Autowired
	private UserServiceImp users;
	
	@Autowired
	private RoomPeopleDao roomPeopleDao;
	
	@Autowired
	private GenerateXls generateXls;
	
	@Autowired
    private JavaMailSender mailSender; //自动注入的Bean 

    @Value("${spring.mail.username}")
    private String Sender; //读取配置文件中的参数
	
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
					String str=stringRedisTemplate.opsForValue().get("time"+roomPeopleDoMain.getOpenId());
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
			Random random=new Random();
			while(flag){
				a=random.nextInt(999)%100+100;
				if(a<1 || a>999) continue;
				if(roomDao.findByRoomNumberAndRoomState(a,1)!=null){
					continue;
				}else{
					flag=false; 
				}
			}
			
			//现将该用户上一次创建的还没有结束房间标志  为结束
			//防止因为错误导致的数据库中有多个上次创建的还没有结束的房间引发错误
			List<RoomDoMain> list=roomDao.findByOpenIdAndRoomState(openId,1);
			for (RoomDoMain room1 :list) {
				room1.setRoomState(0);
				roomDao.saveAndFlush(room1);
				stringRedisTemplate.delete("room"+room1.getRoomNumber());
			}
			
			//构造一条新纪录
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			RoomDoMain room=new RoomDoMain();
			room.setRoomNumber(a);
			room.setRoomState(1);
			room.setRemark(remark);
			room.setOpenId(openId);
			room.setRoomPeoples(0);
			//room.setRoomId(null);
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
				List<RoomDoMain> list=roomDao.findByOpenIdAndRoomState(openId,1);
				for (RoomDoMain room1 :list) {
					room1.setRoomState(0);
					roomDao.saveAndFlush(room1);
					stringRedisTemplate.delete("room"+room1.getRoomNumber());
				}
				
				//构造一条记录
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				RoomDoMain room=new RoomDoMain();
				room.setOpenId(openId);
				room.setRemark(remark);
				//room.setRoomId(null);
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
	
	
	//进入房间
	@Transactional
	public RoomDoMain getRoom(String openId,Integer roomNumber){
		try {
			//判断房间是否存在，不存在返回null，否则返回room信息
			
			RoomDoMain room=roomDao.findByRoomNumberAndRoomState(roomNumber, 1);
			
			if(room!=null){ 
				//房间存在,向room_people表插入一条记录
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

				RoomPeopleDoMain roomPeopleDoMain=roomPeopleDao.findByOpenIdAndRoomId(
						openId,room.getRoomId());
				
				if(roomPeopleDoMain==null){
					//表明以前没进来过
					//插入一条新纪录
					//System.out.println(openId+"---"+roomNumber);
					//查询姓名
					UserDoMain user=users.findByOpenId(openId);
					
					//roomPeopleDoMain1.setId(null);
					RoomPeopleDoMain roomPeopleDoMain1=new RoomPeopleDoMain();
					roomPeopleDoMain1.setOpenId(openId);
					roomPeopleDoMain1.setRoomId(room.getRoomId());
					roomPeopleDoMain1.setBegainTime(df.format(new Date()));
					roomPeopleDoMain1.setTimes(0);
					roomPeopleDoMain1.setSchoolId(user.getSchoolId());
					if(user.getSchoolName()==null||user.getSchoolName().equals(""))
						//要是没有绑定学校姓名，就用昵称
						roomPeopleDoMain1.setName(user.getName());
					else roomPeopleDoMain1.setName(user.getSchoolName());
					
					roomPeopleDao.save(roomPeopleDoMain1);
					
					//System.out.println("已经入房间"+openId+"--"+room.getRoomId());
					
					//房间人数加一
					if(room.getRoomPeoples()==null){
						room.setRoomPeoples(1);
					}else{
						room.setRoomPeoples(room.getRoomPeoples()+1);
					}
					roomDao.saveAndFlush(room);
					
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
	
	//发送邮件
	public String SendEmail(String openId,Integer roomId,String emailAddress) throws Exception{
		
		try {
			long begainTime=System.currentTimeMillis();
			MimeMessage message = null;
			String path=generateXls.CreateXls(roomId);
			
			long xlsTime=System.currentTimeMillis();
			
			message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(Sender);
            helper.setTo(emailAddress);
            helper.setSubject("主题：带附件的邮件");
            helper.setText("带附件的邮件内容");
            //注意项目路径问题，自动补用项目路径
            FileSystemResource file = new FileSystemResource(new File(path));
            //加入邮件
            helper.addAttachment("记录报表.xls", file);
            
            mailSender.send(message);
            
            long emailTime=System.currentTimeMillis();
            
            return "生成邮件耗时："+String.valueOf(xlsTime-begainTime)+"ms,发送邮件耗时："+String.valueOf(emailTime-xlsTime)+"ms";
            
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		
	}

}

