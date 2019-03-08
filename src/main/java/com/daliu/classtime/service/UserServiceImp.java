package com.daliu.classtime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daliu.classtime.domain.UserDoMain;
import com.daliu.classtime.dao.UserDao;
import com.daliu.classtime.service.inservice.InUesrService;

import java.util.List;


@Service
public class UserServiceImp implements InUesrService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UserDoMain userDoMain1;
	
	@Autowired
	private UserDoMain userDoMain2;


	public List<UserDoMain> queryAllUser() {
		try {
			return (List<UserDoMain>)userDao.findAll();
		} catch (Exception e) {
			throw e;
		}

	}
	
	@Transactional
	public void updateName(UserDoMain userDoMain,String name){
		try{
			userDoMain.setName(name);
		}catch(Exception e){
			throw e;
		}
	}
	
	public  UserDoMain findByOpenId(String openId){
		try{
			userDoMain1=userDao.findByOpenId(openId);
			
			//看看数据库中是否有这个openid，没有就插入
			if(userDoMain1==null){
				userDoMain2.setOpenId(openId);
				userDao.save(userDoMain2);
		        return userDoMain2;
			}else{
				return userDoMain1;
			}
		}catch (Exception e) {
			throw e;
		}
		
		
	}
	
	@Transactional
	public void updateSchoolId(UserDoMain userDoMain,String schoolId){
		//手动绑定openid和学号的信息
		try {
			userDoMain.setSchoolId(schoolId);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Transactional
	public void updateSessionKey(UserDoMain userDoMain,String sessionKey){
		try {
			userDoMain.setSessionKey(sessionKey);
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Transactional
	public void updateSchoolName(UserDoMain userDoMain,String schoolName){
		try {
			userDoMain.setSchoolName(schoolName);
		} catch (Exception e) {
			throw e;
		}
	}
	

}
