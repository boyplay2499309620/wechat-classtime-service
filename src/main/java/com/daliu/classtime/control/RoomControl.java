package com.daliu.classtime.control;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daliu.classtime.domain.RoomDoMain;
import com.daliu.classtime.service.RoomServiceimp;
import com.daliu.classtime.utils.ErrorMsg;
import com.daliu.classtime.utils.StringUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/classtime/room")
@Api("和room有关的网络请求")
public class RoomControl {
	
	private static Logger logger = LogManager.getLogger("control.room");
	
	static String log="\r\n****************      纪录结束       **********************\r\n";
	
	@Autowired
	private RoomServiceimp rooms;
	
	
	//查看该房间里的人
	@RequestMapping(value="/getRoomPeople",method=RequestMethod.GET)
	@ApiOperation("依据房间号查询所有该房间的人和该房间的信息")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query",name="roomId",value="房间号",required=true )
	})
	public Map<String,Object> getRoomPeople(@RequestParam Integer roomId){
		try {
			
			Map<String,Object> map=new HashMap<String, Object>();
			map.put("list",rooms.findAllRoomId(roomId));
			map.put("room",rooms.findByRoomId(roomId));
			return map;
		} catch (Exception e) {
			ErrorMsg msg=new ErrorMsg();
			logger.error("roomId:"+roomId+"    错误原因：\r\n"+msg.getStackTrace(e)+log);
			System.out.println("roomControl getRoomPeople have error");
			return null;
		}
	}
	
	//查看我的房间的记录
	@RequestMapping(value="/myRoom",method=RequestMethod.GET)
	@ApiOperation("依据openid查询我所创建过的房间")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query",name="openId",value="openId",required=true ),
		@ApiImplicitParam(paramType="query",name="pageNnm",value="分页信息",required=true )
	})
    public Page<RoomDoMain> myRoom(@RequestParam String openId,
    		@RequestParam int pageNum){
		try {
			Sort sort = new Sort(Sort.Direction.DESC, "roomId");
			Pageable pageable = PageRequest.of(pageNum,5, sort);
			return rooms.myRoom(openId,pageable); 
			//return null;
		} catch (Exception e) {
			ErrorMsg msg=new ErrorMsg();
			logger.error("openId:"+openId+"    查询的页数："+pageNum+"    错误原因：\r\n"+msg.getStackTrace(e)+log);
			System.out.println("roomControl myRoom have error");
			return null;
		}
		
	}
	
	//创建房间
	@RequestMapping(value="/createRoom",method=RequestMethod.POST)
	@ApiOperation("用户创建房间")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query",name="openId",value="openId",required=true ),
		@ApiImplicitParam(paramType="query",name="number",value="用户填写的房间号，0代表未填写",required=true ),
		@ApiImplicitParam(paramType="query",name="remark",value="用户指定的房间备注",required=true )
	})
	public Map<String, String> createRoom(@RequestParam String openId,
			@RequestParam Integer number,
			@RequestParam String remark ){
		
		long startTime=System.currentTimeMillis();
		Map<String,String> map=new HashMap<String, String>();
		try{
			//对参数的必要性检查
			if(number>9999)return null;
			if(StringUtils.getWordCount(remark)>49) 
				remark=StringUtils.getSubString(remark,48);
			
			if(number==0){
				//用户没有指定房间号
				RoomDoMain room=rooms.createNum(openId,remark);
				number=room.getRoomNumber();
				map.put("status","0");
				map.put("number",room.getRoomNumber().toString());
				map.put("roomId",room.getRoomId().toString());
			}else{
				//用户指定房间号
				RoomDoMain room=rooms.createNumByNum(openId,remark,number);
				if(room==null){
					//表明这个number正在使用
					map.put("status","2");
				}else{
					map.put("status","0");
					map.put("number",room.getRoomNumber().toString());
					map.put("roomId",room.getRoomId().toString());
				}
			}
			
			//创建者自动进入该房间
			if(rooms.getRoom(openId, number)==null){
				//进入房间失败，可能由于新建立的房间信息还未插入数据库，则个前台返回房间建立失败的信息
				map.put("status","3");
			}
			
			//long endTime=System.currentTimeMillis();
			//logger.info(openId+"---"+number+"---"+remark+"---"+(endTime-startTime)+"ms"+map);
			return map;
		}catch (Exception e) {
			long endTime=System.currentTimeMillis();
			ErrorMsg msg=new ErrorMsg();
			logger.error("openId:"+openId+"    number:"+number+"     remark:"+remark+"     耗时："+
					(endTime-startTime)+"ms \r\n map:"+map+"\r\n 错误原因：\r\n"+msg.getStackTrace(e)+log);
			System.out.println("roomControl createRoom have error");
			return map;
		}
		
		
	}

	//进入房间
	@RequestMapping(value="/getRoom",method=RequestMethod.POST)
	@ApiOperation("用户加入房间")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query",name="openId",value="openId",required=true ),
		@ApiImplicitParam(paramType="query",name="roomNumber",value="房间号",required=true )
	})
	public Map<String, String> getroom(@RequestParam String openId,
			@RequestParam Integer roomNumber){
		try {
			Map<String, String> map=new HashMap<String, String>();
			
			RoomDoMain room=rooms.getRoom(openId,roomNumber);
			
			if(room!=null){
				//该房间存在
				map.put("remark",room.getRemark());
				map.put("status","1");
				map.put("roomId",room.getRoomId().toString());
				return map;
			}else{
				//该房间不存在
				map.put("status","0");
				return map;
			}
		} catch (Exception e) {
			// TODO: handle exception
			ErrorMsg msg=new ErrorMsg();
			logger.error("openId:"+openId+"    roomNumber:"+roomNumber+
					"\r\n错误原因：\r\n"+msg.getStackTrace(e)+log);
			System.out.println("roomControl getroom have error");
			return null;
		}
		
	}
	
	
	//发送邮件
	@RequestMapping(value="/sendEmail",method=RequestMethod.GET)
	@ApiOperation("将房间里的记录以电子邮件的形式发送")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query",name="openId",value="openId",required=true ),
		@ApiImplicitParam(paramType="query",name="roomId",value="房间索引",required=true ),
		@ApiImplicitParam(paramType="query",name="emailAddress",value="邮件地址，经过校验了",required=true )
	})
	public void SendEmail(@RequestParam String openId,
			@RequestParam Integer roomId,
			@RequestParam String emailAddress){
		//System.out.println(roomId+"---"+emailAddress);
		try {
            String string=rooms.SendEmail(openId, roomId, emailAddress);
            logger.info(openId+"---"+roomId+"---"+roomId+"---"+string+log);
            //System.out.println("发送成功！"+string);
		} catch (Exception e) {
			ErrorMsg msg=new ErrorMsg();
			logger.error("openId:"+openId+"    roomId:"+roomId+
					"   错误原因:\r\n"+msg.getStackTrace(e)+log);
			System.out.println("roomControl SendEmail have error");
		}
	}
	
	
}
