package com.daliu.classtime.control;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daliu.classtime.domain.TimeDoMain;
import com.daliu.classtime.service.TimeServiceimp;
import com.daliu.classtime.service.RankServiceimp;
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
	private RankServiceimp rank;
	
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
			//System.out.println(timeDoMain);
			//对参数的必要性检查
			//timeDoMain的pauseMsg字段可能由于暂停次数过多而导致太长，数据库只有250字节
			if(timeDoMain.getPauseMsg().length()>250)
				timeDoMain.setPauseMsg(new StringBuffer(timeDoMain.getPauseMsg()).substring(0,250));
			
			//前台已经传过来roonId为1,不知道为什么有时候拿不到，总是为null
			//可能是前端控制逻辑出现了错误，导致用户退出房间时没有及时更新roomId
			//可能是类型转换的错误，js的类型控制不是非常严格，Java中使int型的数据，接受不到，则变成了null
			if(timeDoMain.getRoomId()==null || timeDoMain.getRoomId().toString()=="")
				timeDoMain.setRoomId(1);
			
			//前端若为第一次提交记录，则返回主键id，否则直接修改记录
			if(timeDoMain.getTimeId()==0 || timeDoMain.getTimeId().equals("")){
				timeDoMain.setTimeId(null);
			}
			
			//排行榜有时候会出现数值特别巨大的计时的时间，这里检测下，若前端传递的数值过于巨大，则人为抛出错误，不插到排行榜上去
			if(timeDoMain.getTimes()>7200){//大于两个小时
				System.out.println(""+String.valueOf(timeDoMain.getTimes()));
				throw new Exception("检测到前端传过来的计时数值特别变态，请检查！！！计时数值为："+timeDoMain.getTimes());
			}
			
			Map<String, String> map=new HashMap<String,String>();
			
			//如果之前提交过的话得到以前的总记录时间
			int lastTime=0;
			TimeDoMain lastTimeDoMain=timeService.findByTimeId(timeDoMain.getTimeId());
			if(lastTimeDoMain!=null) lastTime=lastTimeDoMain.getTimes();
			 
			//保存记录
			timeDoMain=timeService.saveAll(timeDoMain);
			//返回该条记录的主键
			map.put("timeId",timeDoMain.getTimeId().toString());
	    	
	    	//刷新排行榜
			//传递给排行榜的是自从上次保存记录之后新增的时间
			rank.refreshRank(timeDoMain.getOpenId(),timeDoMain.getTimes()-lastTime);
	    	
	    	map.put("status","successful!");
	    	
			return map;
			
		} catch (Exception e) {
			// TODO: handle exception
			ErrorMsg msg=new ErrorMsg();
			logger.error("\r\n timeDoMain:"+timeDoMain.toString()+"\r\n 错误原因：\r\n"+msg.getStackTrace(e)+log);
			System.out.println("timeControl endTime have error");
			return null; 
		}
    	
	}
    //https://daliu.mynatapp.cc/classtime/time/endTime?openId=orxZW40HVbGQTr2OqE1Bfne8r1z4&dates=jjijijijiji&begain=90909099&ends=kokoko&pause=8&times=989899jiji
    //http://localhost:8080/classtime/time/endTime?openId=orxZW40HVbGQTr2OqE1Bfne8r1z4&dates=jjijijijiji&begain=90909099&ends=kokoko&pause=8&times=989899jiji

	//报告在线
	@RequestMapping(value="/online",method=RequestMethod.POST)
	@ApiOperation("报告在线")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query",name="openId",value="openId",required=true )
	})
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
	@RequestMapping(value="/suspend",method=RequestMethod.POST)
	@ApiOperation("报告不在线，暂停")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query",name="openId",value="openId",required=true )
	})
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
	

}
