/**
 * 
 */
package com.daliu.classtime.service.inservice;

import java.util.ArrayList;
import java.util.List;

import com.daliu.classtime.domain.RankDoMain;

/**  
* @Title: InRankService.java
* @Package:com.daliu.classtime.service.inservice
* @Description:(作用)
* @author:刘严岩 
* @date:2019年4月16日
*/
public interface InRankService {
	
	public void refreshRank(String openId,int time);
	
	public void historyRank(String openId, int times);
	
	public void weekRank(String openId,int time);
	
	public void dayRank(String openId,int time);
	
	public void insertRank(String rank,String openId,int times);
	
	public List<ArrayList< RankDoMain >> queryRank();
	
	public void clickLove(int rankType,int ranking);

}
