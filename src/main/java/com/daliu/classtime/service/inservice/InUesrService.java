package com.daliu.classtime.service.inservice;

import com.daliu.classtime.domain.ModelDoMain;
import com.daliu.classtime.domain.UserDoMain;

import java.util.List;

public interface InUesrService {
	
	public List<UserDoMain> queryAllUser();
	
	public UserDoMain findByOpenId(String openId);
	
	public void updateSchoolId(UserDoMain userDoMain,String schoolId);
	
	public void updateSessionKey(UserDoMain userDoMain,String sessionKey);
	
	public void updateName(UserDoMain userDoMain,String name);
	
	public void updateSchoolName(UserDoMain userDoMain,String schoolName);

	public void saveAndFlush(ModelDoMain modelDoMain);

}
