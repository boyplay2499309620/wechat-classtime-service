package com.daliu.classtime.control;

import com.daliu.classtime.domain.ModelDoMain;
import com.daliu.classtime.domain.TimeDoMain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.daliu.classtime.domain.UserDoMain;
import com.daliu.classtime.service.ModelServiceimp;
import com.daliu.classtime.service.TimeServiceimp;
import com.daliu.classtime.service.UserServiceImp;
import com.daliu.classtime.utils.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import com.google.gson.Gson;


@RestController
@RequestMapping("classtime/user")
@Api("user相关的所有请求")
public class UserControl {
	
	private static Logger logger = LogManager.getLogger("control.user");
	
	static String log="\r\n****************      纪录结束       **********************\r\n";
	
	@Autowired
	private UserServiceImp userServiceImp;
	
	@Autowired
	private TimeServiceimp timeServiceimp;
	
	@Autowired
	private ModelServiceimp modelSeviceimp;
	
	
	 /**
     * 小程序端 获取根据小程序端发过来的code获取用户信息
     * @param code
     * @return
     */
	
	@Value("${com.classtime.appid}")
	String appid;
	
	@Value("${com.classtime.appsecret}")
	String appsecret;
	
	@RequestMapping(value="/",method=RequestMethod.GET)
	@ApiOperation("测试springboot能否访问")
	public String test(){
		return "wellcome to use classtime!";
	}
	
	@RequestMapping(value="/myRecord",method=RequestMethod.GET)
	@ApiOperation("查询我的记录")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query",name="openId",value="依据openid查询记录",required=true ),
		@ApiImplicitParam(paramType="query",name="pageNum",value="分页数",required=true )
	})
	public Page<TimeDoMain> myRecord(@RequestParam String openId,
			@RequestParam int pageNum){
		try {
			//System.out.println(pageNum);
			
			Sort sort = new Sort(Sort.Direction.DESC, "timeId");
			Pageable pageable = PageRequest.of(pageNum,10, sort);
			//PageRequest.of(当前查询的是第几页，每页展示多少条数据，sort参数)
			Page<TimeDoMain> pages=timeServiceimp.findByOpenId(openId, pageable);
			//System.out.println(pages.getTotalElements()+"---"+pages.getTotalPages());
			return pages; 
		} catch (Exception e) {
			ErrorMsg msg=new ErrorMsg();
			System.out.println("UserControl myRecord have error\n");
			logger.error("openId:"+openId+"       查询页数："+pageNum+
					"     错误原因\r\n"+msg.getStackTrace(e)+log);
			return null;
		}
	}
  
	@RequestMapping(value="/getOpenId",method=RequestMethod.GET)
	@ApiOperation("通过code请求腾讯服务器得到openId")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query",name="code",value="前端通过wx.login得到的code",required=true )
	})
    public Map<String,String> getopenid(@RequestParam String code) {
		long startTime=System.currentTimeMillis();   //获取开始时间
		long time=0;
		Map<String, String> map = new HashMap<String ,String>();
		WxGetOpneIdReturnJson json=null;
		try{
			
			String url = String.format("https://api.weixin.qq.com/sns/jscode2session?"
	        		+ "appid=%s&secret=%s&js_code=%s&grant_type=authorization_code", 
	        		appid, appsecret, code);
	        String response = HttpUtil.doGet(url);

	        ObjectMapper jsonMapper = new ObjectMapper();
	        json=jsonMapper.readValue(response,WxGetOpneIdReturnJson.class);
	        
	        //map.put("errcode","40029");
	        
	        if(json.getErrcode()!=null && json.getErrcode()!=0){
	        	//腾讯服务器返回不正常
	        	map.put("Errcode",json.getErrcode().toString());
	        	logger.info("腾讯服务器返回异常，返回值如下"+json+log);
	        	System.out.println("userControl getopenid TenXun rentrun error");
	        	return map;
	        }else{
	        	map.put("errcode","0");
	        }
	        
	        map.put("openId",json.getOpenid());
	        
	        //记录访问腾讯服务器耗时
	        time=System.currentTimeMillis()-startTime;
	        
	        //看看数据库中是否有该openid，若有，不干什么，没有，新建一个用户，插入此openid
	        UserDoMain userDoMain=userServiceImp.findByOpenId(json.getOpenid());
	        
	        //保存这次的sessionkey
	        userDoMain.setSessionKey(json.getSession_key());
	        userServiceImp.saveUser(userDoMain);
	        
	        if(userDoMain.getAvatarUrl()==null || userDoMain.getAvatarUrl().equals("")) map.put("userMsgComplete","false");
	        else{
	        	map.put("userMsgComplete","true");
	        	map.put("avatarUrl",userDoMain.getAvatarUrl());
	        }
	        
	        //返回数据库中已有的昵称给前端
	        if(userDoMain.getNickName()==null) map.put("nickName","");
	        else   map.put("nickName",userDoMain.getNickName());
	        
	        //没有绑定学号或者姓名返回no，否则返回学号和姓名
	        if(userDoMain.getSchoolId()==null ||userDoMain.getSchoolId().equals("") || 
	        		userDoMain.getSchoolName()==null ||userDoMain.getSchoolName().equals("")){
	        	map.put("status","no");
	        }else{
	        	map.put("schoolId",userDoMain.getSchoolId());
	        	map.put("schoolName",userDoMain.getSchoolName());
	        }
	        
	        ModelDoMain modelDoMain=modelSeviceimp.findByOpenId(userDoMain.getOpenId());
	        if(modelDoMain==null || modelDoMain.getModels()==null) map.put("model","");
	        else  map.put("model",modelDoMain.getModels());
	        
	        
	        long endTime=System.currentTimeMillis(); //获取结束时间
	        
	        logger.info("openid:"+json.getOpenid()+
	        		"    服务器返回时间:"+time+
	        		"    整个方法的耗时："+(endTime-startTime)+"ms"+log);
	        //执行logger.info的时间大概在20ms左右，整个方法耗时大概250ms，相比较而言可以接受
	    	return map;
		}catch(Exception e){
			ErrorMsg msg=new ErrorMsg();
			long endTime=System.currentTimeMillis(); //获取结束时间
			
			logger.error("code:"+code+"    服务器返回数据:"+json+
					"\r\n耗时"+(endTime-startTime)+"ms"+
					"      错误信息如下：\r\n"+msg.getStackTrace(e)+log);
			System.out.println("UserControl getopenid have error\n");
			return map;
		}
        

    }
	
	@RequestMapping(value="/schoolId",method=RequestMethod.POST)
	@ApiOperation("用户绑定学号")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query",name="openid",value="openid",required=true ),
		@ApiImplicitParam(paramType="query",name="studentId",value="用户填写的学校号码",required=true ),
		@ApiImplicitParam(paramType="query",name="schoolName",value="用户填写的姓名",required=true )
	})
	public Map<String, String> schoolId(@RequestParam String openid,
			@RequestParam String studentId,
			@RequestParam String schoolName){
		
		long startTime=System.currentTimeMillis();
		Map<String,String> map=new HashMap<String, String>();
		
		try{
			//对参数的必要性检查
			if(openid==null) return null;
			if( StringUtils.getWordCount(schoolName)>30) 
				schoolName=StringUtils.getSubString(schoolName,29);
			if(StringUtils.getWordCount(studentId)>14) 
				studentId=StringUtils.getSubString(studentId,29);
			
			UserDoMain userDoMain=userServiceImp.findByOpenId(openid);
			userDoMain.setSchoolId(studentId);
			userDoMain.setSchoolName(schoolName);
			userServiceImp.saveUser(userDoMain);
			
			map.put("status","yes");
			
			//long endTime=System.currentTimeMillis();
			//logger.info(openid+"---"+studentId+"---"+schoolName+"---"+(endTime-startTime)+"ms");
			return map;
		}catch(Exception e){
			ErrorMsg msg=new ErrorMsg();
			long endTime=System.currentTimeMillis();
			logger.error("openId:"+openid+"      studentId:"+studentId+"     schoolName"+schoolName+
					"耗时："+(endTime-startTime)+"ms"+
					"\r\n 错误原因：\r\n"+msg.getStackTrace(e)+log);
			System.out.println("userControl schoolId hava error\n");
			return map;
		}
		
	}
	
	@RequestMapping(value="/updateUserMsg",method=RequestMethod.POST)
	@ApiOperation("用户进入index界面并加载完成后发送自己的昵称")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query",name="openId",value="openId",required=true ),
		@ApiImplicitParam(paramType="query",name="nickName",value="用户的昵称",required=true )
	})
	public void updateUserMsg(@RequestParam String openId,
			@RequestParam String nickName,
			@RequestParam String avatarUrl,
			@RequestParam int sex){
		//把addNickName和addModel分开发送，避免一个函数执行失败造成回滚，两个信息都无法保存
		try {
			//http://localhost:8080/classtime/user/addNickName?nickName=非官方个非官方 & openId=orxZW4-KPDilobrjiLfn2Bdc5mok & brand=LeEco & model=LEX720 & languages=zh_CN & version=6.7.3 & system=Android 6.0.1 & platform=android & sdkVersion=2.4.4
			if(StringUtils.getWordCount(nickName)>28) 
				nickName=StringUtils.getSubString(nickName,28);
			//特殊情况，前端就是不能传送微信昵称过来
			if(nickName==null || nickName.equals(""))
				nickName="unKnow";
			
			//对参数的必要性检查
			if(sex!=0 && sex!=1) new Exception("前端传送的性别是错误的格式");
			if(StringUtils.getWordCount(avatarUrl)>225) new Exception("前端传送的头像地址太长");
			if(openId==null) new Exception("收不到前端传送的openId值");
			
			//保存昵称
			//System.out.println(openId+nickName);
			UserDoMain userDoMain=userServiceImp.findByOpenId(openId);
			if(userDoMain==null) throw new Exception("数据库中没有前端传送过来的openId,openId值如下："+openId);
			
			userDoMain.setNickName(nickName);
			userDoMain.setAvatarUrl(avatarUrl);
			userDoMain.setSex(sex);
			userDoMain.setOpenId(openId);
			
			userServiceImp.saveUser(userDoMain);
		
		} catch (Exception e) {
			ErrorMsg msg=new ErrorMsg();
			logger.error("错误原因：\r\n"+msg.getStackTrace(e)+log);
			System.out.println("userControl addNickName have error");
		}
		
	}
	
	
	@RequestMapping(value="/addModel",method=RequestMethod.POST)
	@ApiOperation("用户进入index界面并加载完成后发送自己的手机信息")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query",name="modelDoMain",value="接受用户的设备信息",required=true )
	})
	public void addModel(ModelDoMain modelDoMain){
		//把addNickName和addModel分开发送，避免一个函数执行失败造成回滚，两个信息都无法保存
		try {
			userServiceImp.saveModel(modelDoMain);
		} catch (Exception e) {
			// TODO: handle exception
			ErrorMsg msg=new ErrorMsg();
			logger.error("modelDoMain：  "+modelDoMain+"\r\n错误原因：\r\n"+msg.getStackTrace(e)+log);
			System.out.println("userControl addModel have error");
		}
	}
	

}
