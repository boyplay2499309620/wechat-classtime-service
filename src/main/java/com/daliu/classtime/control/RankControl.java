/**
 * 
 */
package com.daliu.classtime.control;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daliu.classtime.domain.RankDoMain;
import com.daliu.classtime.service.RankServiceimp;
import com.daliu.classtime.utils.ErrorMsg;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**  
* @Title: RankControl.java
* @Package:com.daliu.classtime.control
* @Description:(作用)
* @author:刘严岩 
* @date:2019年4月16日
*/
@RestController
@RequestMapping("/classtime/rank")
@Api("有关排行榜的请求")
public class RankControl {
	
	
	static String log="\r\n****************      纪录结束       **********************\r\n";
	
	private static Logger loggerRedis = LogManager.getLogger("redis");
	
	@Autowired 
	private RankServiceimp rankService;
	
	/**
	 * 
	 * @Description:(查询排行榜)
	 * @param:@return   
	 * @return:List<ArrayList<RankDoMain>>  
	 * @date:2019年4月16日
	 */
	@RequestMapping(value="/queryRank",method=RequestMethod.GET)
	@ApiOperation("查询排行榜")
	public List<ArrayList< RankDoMain >> queryRank(){
		try {
			return rankService.queryRank();
		} catch (Exception e) {
			// TODO: handle exception
			ErrorMsg msg=new ErrorMsg();
			loggerRedis.error("错误原因:\r\n"+msg.getStackTrace(e)+log);
			System.out.println("rankControl queryRank have error,please checkout redis log for error msg");
			return null; 
		}
	}
	
	/**
	 * 
	 * @Description:(给排行榜点赞)
	 * @param:@param rankType 0代表日榜，1代表周榜，2代表历史排行榜
	 * @param:@param ranking  代表哪一名被点赞了，从0开始
	 * @return:void  
	 * @date:2019年4月16日
	 */
	@RequestMapping(value="/clickLove",method=RequestMethod.POST)
	@ApiOperation("给排行榜点赞")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query",name="rankType",value="表示哪个排行榜，0代表日榜，1代表周榜，2代表历史排行榜",required=true ),
		@ApiImplicitParam(paramType="query",name="ranking",value="表示第几名，从0开始",required=true )
	})
	public void clickLove(@RequestParam int rankType,@RequestParam int ranking){
		try {
			//System.out.println(rankType+"---"+ranking);
			rankService.clickLove(rankType,ranking);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
