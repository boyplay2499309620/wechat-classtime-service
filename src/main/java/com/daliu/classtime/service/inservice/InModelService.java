/**
 * 
 */
package com.daliu.classtime.service.inservice;

import com.daliu.classtime.domain.ModelDoMain;

/**  
* @Title: InModelService.java
* @Package:com.daliu.classtime.service.inservice
* @Description:(作用)
* @author:刘严岩 
* @date:2019年4月8日
*/
public interface InModelService {
	
	public ModelDoMain findByOpenId(String opneId);

}
