package com.daliu.classtime.control;

import com.daliu.classtime.domain.ModelDoMain;
import com.daliu.classtime.domain.TimeDoMain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.daliu.classtime.domain.UserDoMain;
import com.daliu.classtime.service.TimeServiceimp;
import com.daliu.classtime.service.UserServiceImp;
import com.daliu.classtime.utils.*;
import com.google.gson.Gson;

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
	
	@Autowired
	private UserServiceImp userServiceImp;
	
	@Autowired
	private UserDoMain userDoMain;
	
	@Autowired
	private TimeServiceimp timeServiceimp;
	
	
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
	@ApiOperation("查询我的记录")
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
			if(pageNum<0){
				pageNum=0;
			}
			Sort sort = new Sort(Sort.Direction.DESC, "id");
			Pageable pageable = PageRequest.of(pageNum,10, sort);
			//PageRequest.of(当前查询的是第几页，每页展示多少条数据，sort参数)
			Page<TimeDoMain> pages=timeServiceimp.findByOpenId(openId, pageable);
			//System.out.println(pages.getTotalElements()+"---"+pages.getTotalPages());
			return pages; 
		} catch (Exception e) {
			ErrorMsg msg=new ErrorMsg();
			System.out.println("myRecord---"+openId+"---"+pageNum+"---"+msg.getStackTrace(e));
			logger.error(openId+"---"+pageNum+"---"+msg.getStackTrace(e));
			return null;
		}
	}
  
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getOpenId",method=RequestMethod.GET)
	@ApiOperation("通过code请求腾讯服务器得到openId")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query",name="code",value="前端通过wx.login得到的code",required=true )
	})
    public Map<String,String> getopenid(@RequestParam String code) {
		long startTime=System.currentTimeMillis();   //获取开始时间
		long time=0;
		Map<String, String> map = new HashMap<String ,String>();
		try{
			
			//Group group=new Group();
			//group.setId(155);
			//redis.opsForValue().set("ran",group);
			//System.out.println(redis.opsForValue().get("ragn"));
			//group=(Group)redis.opsForValue().get("ran");
			
			
			String url = String.format("https://api.weixin.qq.com/sns/jscode2session?"
	        		+ "appid=%s&secret=%s&js_code=%s&grant_type=authorization_code\n", 
	        		appid, appsecret, code);
	        String response = HttpUtil.doGet(url);

	        Gson gson = new Gson();
	        map = gson.fromJson(response, map.getClass());
	        //System.out.println(map);
	        //现在暂且不能获取union_id
	        
	        //记录访问腾讯服务器耗时
	        time=System.currentTimeMillis()-startTime;
	        
	        //看看数据库中是否有该openid，若有，不干什么，没有，新建一个用户，插入此openid
	        userDoMain=userServiceImp.findByOpenId(map.get("openid"));
	        
	        //保存这次的sessionkey
	        userServiceImp.updateSessionKey(userDoMain, map.get("session_key"));
	        
	        //返回数据库中已有的昵称给前端
	        if(userDoMain.getName()==null) map.put("nickName","");
	        else                           map.put("nickName",userDoMain.getName());
	        
	        //没有绑定学号或者姓名返回no，否则返回学号和姓名
	        //System.out.println(userDoMain);
	        if(userDoMain.getSchoolId()==null ||userDoMain.getSchoolId().equals("") || 
	        		userDoMain.getSchoolName()==null ||userDoMain.getSchoolName().equals("")){
	        	map.put("status","no");
	        }else{
	        	map.put("status",userDoMain.getSchoolId());
	        	if(userDoMain.getSchoolName()==null){
	        		map.put("schoolName","");
	        	}else{
	        		map.put("schoolName",userDoMain.getSchoolName());
	        	}
	        }
	        
	        map.put("session_key",null);
	        //int i=6/0;
	        
	        //System.out.println("微信服务器返回的有关个人信息的数据---"+map);
	        
	        long endTime=System.currentTimeMillis(); //获取结束时间
	        
	        logger.info(map.get("openid")+"---"+time+"---"+(endTime-startTime)+"ms");
	        //执行logger.info的时间大概在20ms左右，整个方法耗时大概250ms，相比较而言可以接受
	        //endTime=System.currentTimeMillis();
	        //System.out.println((endTime-startTime)+"ms");
	    	return map;
		}catch(Exception e){
			ErrorMsg msg=new ErrorMsg();
			long endTime=System.currentTimeMillis(); //获取结束时间
			logger.error(code+"---"+map+"---"+(endTime-startTime)+"ms"+"---"+
					msg.getStackTrace(e));
			System.out.println(code+"---"+map+"---"+time+"---"+(endTime-startTime)+"ms"+
					"---"+msg.getStackTrace(e));
			return map;
		}
        

    }
	
	@RequestMapping(value="schoolId",method=RequestMethod.POST)
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
			userDoMain=userServiceImp.findByOpenId(openid);
			userServiceImp.updateSchoolId(userDoMain,studentId);
			userServiceImp.updateSchoolName(userDoMain, schoolName);
			
			map.put("status","yes");
			
			//long endTime=System.currentTimeMillis();
			//logger.info(openid+"---"+studentId+"---"+schoolName+"---"+(endTime-startTime)+"ms");
			return map;
		}catch(Exception e){
			ErrorMsg msg=new ErrorMsg();
			long endTime=System.currentTimeMillis();
			logger.error(openid+"---"+studentId+"---"+schoolName+"---"+
			(endTime-startTime)+"ms"+"---"+msg.getStackTrace(e));
			System.out.println(openid+"---"+studentId+"---"+schoolName+"---"+
			(endTime-startTime)+"ms"+"---"+msg.getStackTrace(e));
			return map;
		}
		
	}
	
	@RequestMapping(value="addNickName",method=RequestMethod.POST)
	@ApiOperation("用户进入index界面并加载完成后发送自己的的昵称")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query",name="openId",value="openId",required=true ),
		@ApiImplicitParam(paramType="query",name="nickName",value="用户的昵称",required=true ),
		@ApiImplicitParam(paramType="query",name="modelDoMain",value="接受用户的设备信息",required=true )
	})
	public void addNickName(@RequestParam String openId,
			@RequestParam String nickName,
			ModelDoMain modelDoMain){
		try {
			//System.out.println(modelDoMain);
			//保存昵称
			//System.out.println(openId+nickName);
			userDoMain=userServiceImp.findByOpenId(openId);
			userServiceImp.updateName(userDoMain, nickName);
			userServiceImp.saveAndFlush(modelDoMain);
		} catch (Exception e) {
			ErrorMsg msg=new ErrorMsg();
			logger.error(openId+"---"+nickName+"---"+msg.getStackTrace(e));
			System.out.println(openId+"---"+nickName+"---"+msg.getStackTrace(e));
		}
		
	}
	

}
