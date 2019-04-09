/**
 * 
 */
package com.daliu.classtime.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.daliu.classtime.dao.ModelDao;
import com.daliu.classtime.domain.ModelDoMain;
import com.daliu.classtime.service.inservice.InModelService;

/**  
* @Title: ModelSeviceimp.java
* @Package:com.daliu.classtime.service
* @Description:(作用)
* @author:刘严岩 
* @date:2019年4月8日
*/
@Service
public class ModelServiceimp implements InModelService {
	
	@Autowired
	private ModelDao modelDao;
	
	public ModelDoMain findByOpenId(String openId){
		try {
			List<ModelDoMain> list= modelDao.findByOpenId(openId);
			//返回最近一次登录的设备信息
			return list.get(list.size()-1);
		} catch (Exception e) {
			return null;
		}
	}

}
