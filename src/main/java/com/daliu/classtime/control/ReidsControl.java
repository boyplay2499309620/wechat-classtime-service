/**
 * 
 */
package com.daliu.classtime.control;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.daliu.classtime.domain.RankDoMain;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**  
* @Title: ReidsControl.java
* @Package:control
* @Description:(作用)
* @author:刘严岩 
* @date:2019年4月7日
*/
@RestController
@RequestMapping("/classtime/redis") 
@Api("打印redis的内容，调试用")
public class ReidsControl {
	
	@Autowired
	private  RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	//http://localhost:8080/classtime/redis/rankingRedis
	@RequestMapping(value="/rankingRedis",method=RequestMethod.GET)
	@ApiOperation("打印redis的排行榜信息")
	public List<Object> inputAllRedisContent(){
		//System.out.println("rankingRedis"); 
		List<Object> list=new ArrayList<Object>();
		
		try {
			//打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writerWeek=new FileWriter("C:/classtime/redisContent/week.txt",true);
			
			FileWriter writerDay=new FileWriter("C:/classtime/redisContent/day.txt",true);
			
			Set<String> keys=stringRedisTemplate.keys("days"+"*");
			for(String key : keys){
				writerDay.write(key+"-----"+stringRedisTemplate.opsForValue().get(key)+"\r\n");
				//System.out.println(stringRedisTemplate.opsForValue().get(key));
			}
			
			keys=stringRedisTemplate.keys("week"+"*");
			for(String key : keys){
				writerWeek.write(key+"-----"+stringRedisTemplate.opsForValue().get(key)+"\r\n");
			}
			
			writerWeek.close();
			writerDay.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		list.add("历史排行榜记录如下");
		for(int i=0;i<10;i++){
			RankDoMain rank=(RankDoMain)redisTemplate.opsForValue().get("ran"+i);
			if(rank!=null)list.add("ran"+i+rank);
		}
		
		list.add("星期排行榜记录如下");
		for(int i=0;i<10;i++){
			RankDoMain rank=(RankDoMain)redisTemplate.opsForValue().get("week"+i);
			if(rank!=null)list.add("week"+i+rank);
		}
		
		list.add("天排行榜记录如下");
		for(int i=0;i<10;i++){
			RankDoMain rank=(RankDoMain)redisTemplate.opsForValue().get("day"+i);
			if(rank!=null)list.add("day"+i+rank);
		}
		
		
		return list;
	}

}
