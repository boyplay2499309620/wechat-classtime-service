package com.daliu.classtime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daliu.classtime.domain.ModelDoMain;
import com.daliu.classtime.domain.UserDoMain;
import com.daliu.classtime.dao.ModelDao;
import com.daliu.classtime.dao.UserDao;
import com.daliu.classtime.service.inservice.InUesrService;

import java.util.List;


@Service
public class UserServiceImp implements InUesrService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ModelDao modelDao;


	public List<UserDoMain> queryAllUser() {
		try {
			return (List<UserDoMain>)userDao.findAll();
		} catch (Exception e) {
			throw e;
		}

	}
	
	@Transactional
	public void saveUser(UserDoMain user){
		try {
			userDao.saveAndFlush(user);
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		
	}
	
	@Transactional
	public  UserDoMain findByOpenId(String openId){
		try{
			UserDoMain userDoMain1=userDao.findByOpenId(openId);
			
			//看看数据库中是否有这个openid，没有就插入
			if(userDoMain1==null){
				UserDoMain userDoMain2=new UserDoMain();
				userDoMain2.setOpenId(openId);
				userDao.saveAndFlush(userDoMain2);
		        return userDoMain2;
			}else{
				return userDoMain1;
			}
		}catch (Exception e) {
			throw e;
		}
		
		
	}
	
	@Transactional
	public void saveModel(ModelDoMain model){
		try {
			modelDao.saveAndFlush(model);
		} catch (Exception e) {
			throw e;
		}
	}

	

}
