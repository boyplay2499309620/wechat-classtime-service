package com.daliu.classtime.service.inservice;

import com.daliu.classtime.domain.ModelDoMain;
import com.daliu.classtime.domain.UserDoMain;

import java.util.List;

public interface InUesrService {
	
	public List<UserDoMain> queryAllUser();
	
	public UserDoMain findByOpenId(String openId);
	
	public void saveUser(UserDoMain user);
	
	public void saveModel(ModelDoMain model);

}
