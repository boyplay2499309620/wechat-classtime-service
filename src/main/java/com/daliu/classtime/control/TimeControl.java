package com.daliu.classtime.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daliu.classtime.domain.RankDoMain;
import com.daliu.classtime.domain.TimeDoMain;
import com.daliu.classtime.service.TimeServiceimp;
import com.daliu.classtime.service.Ranking;
import com.daliu.classtime.utils.ErrorMsg;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/classtime/time")
@Api("有关时间的请求")
public class TimeControl {
	
	@Autowired
	private TimeServiceimp timeService;
	
	@Autowired 
	private Ranking rank;
	
	private static Logger logger = LogManager.getLogger("control.time");
	
	private static Logger loggerRedis = LogManager.getLogger("redis");
	
	static String log="\r\n****************      纪录结束       **********************\r\n";
	
	//结束的时候提交信息
	@RequestMapping(value="/endTime",method=RequestMethod.POST)
	@ApiOperation("计时结束的时候提交信息")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query",name="timeDoMain",value="一个保存时间信息的对象",required=true ),
		//@ApiImplicitParam(paramType="query",name="roomId",value="加入的房间号",required=true )
	})
    public Map<String, String> endTime(TimeDoMain timeDoMain ){

		try {
			//对参数的必要性检查
			//timeDoMain的pauseMsg字段可能由于暂停次数过多而导致太长，数据库只有250字节
			//前端控制了暂停次数，但现在后台仍旧做检查
			if(timeDoMain.getPauseMsg().length()>250)
				timeDoMain.setPauseMsg(new StringBuffer(timeDoMain.getPauseMsg()).substring(0,250));
			
			//前台已经传过来roonId为1,不知道为什么有时候拿不到，总是为null
			//可能是前端控制逻辑出现了错误，导致用户退出房间时没有及时更新roomId
			if(timeDoMain.getRoomId()==null || timeDoMain.getRoomId().toString()=="")
				timeDoMain.setRoomId(1);
			
			Map<String, String> map=new HashMap<String,String>();
			 
			//保存记录
	    	timeService.saveAll(timeDoMain);
	    	
	    	//刷新排行榜
	    	rank.refreshRank(timeDoMain);
	    	
	    	//System.out.println(timeService.queryRank());
	    	
	    	map.put("status","successful!");
	    	
			return map;
			
		} catch (Exception e) {
			// TODO: handle exception
			ErrorMsg msg=new ErrorMsg();
			logger.error("timeDoMain:"+timeDoMain.toString()+"\r\n 错误原因：\r\n"+msg.getStackTrace(e)+log);
			System.out.println("timeControl endTime have error");
			return null;
		}
    	
	}
    //https://daliu.mynatapp.cc/classtime/time/endTime?openId=orxZW40HVbGQTr2OqE1Bfne8r1z4&dates=jjijijijiji&begain=90909099&ends=kokoko&pause=8&times=989899jiji
    //http://localhost:8080/classtime/time/endTime?openId=orxZW40HVbGQTr2OqE1Bfne8r1z4&dates=jjijijijiji&begain=90909099&ends=kokoko&pause=8&times=989899jiji

	//报告在线
	@RequestMapping("/online")
	public void online(@RequestParam String openId){
		try {
			timeService.online(openId);
		} catch (Exception e) {
			// TODO: handle exception
			ErrorMsg msg=new ErrorMsg();
			loggerRedis.error("openId:"+openId+"   错误原因:\r\n"+msg.getStackTrace(e)+log);
			System.out.println("timeControl online have error");
		}
	}
	
	//报告不在线，暂停
	@RequestMapping("/suspend")
	public void suspend(@RequestParam String openId){
		try {
			timeService.suspend(openId);
		} catch (Exception e) {
			// TODO: handle exception
			ErrorMsg msg=new ErrorMsg();
			loggerRedis.error("openId:"+openId+"    错误原因:\r\n"+msg.getStackTrace(e)+log);
			System.out.println("timeControl suspend have error");
		}
	}
	
	//查询某人是否在线
	@RequestMapping("/queryOnline")
	public Map<String, String> queryOnline(@RequestParam String openId){
		
		Map<String, String> map=new HashMap<String, String>();
		try {
			if(timeService.queryOnline(openId)){
				map.put("state","ok");
			}else{
				map.put("state","no");
			}
			return map;
		} catch (Exception e) {
			// TODO: handle exception
			ErrorMsg msg=new ErrorMsg();
			logger.error("openId:"+openId+"   错误原因:\r\n"+msg.getStackTrace(e)+log);
			System.out.println("timeControl queryOnline have error");
			return map;
		}

	}
	
	
	//查询排行榜
	@RequestMapping("/queryRank")
	public List<ArrayList< RankDoMain >> queryRank(){
		try {
			return rank.queryRank();
		} catch (Exception e) {
			// TODO: handle exception
			ErrorMsg msg=new ErrorMsg();
			loggerRedis.error("错误原因:\r\n"+msg.getStackTrace(e)+log);
			System.out.println("timeControl queryRank have error");
			return null; 
		}
	}

}
