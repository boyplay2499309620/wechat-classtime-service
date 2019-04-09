package com.daliu.classtime.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.daliu.classtime.domain.TimeDoMain;



public interface TimeDao extends JpaRepository<TimeDoMain, Integer>{
	
	public Page<TimeDoMain> findByOpenId(String openId,Pageable pageable);
	
	public TimeDoMain findByTimeId(Integer timeId);
}
