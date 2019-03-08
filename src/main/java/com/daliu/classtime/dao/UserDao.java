package com.daliu.classtime.dao;


import org.springframework.data.jpa.repository.JpaRepository;


import com.daliu.classtime.domain.*;


public interface UserDao extends JpaRepository<UserDoMain,String>{
	
	
	public UserDoMain findByOpenId(String openId);
	


}
