package com.daliu.classtime.service.inservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.daliu.classtime.domain.TimeDoMain;

public interface InTimeService {
	
	public TimeDoMain saveAll (TimeDoMain timeDoMain)throws Exception;
	
	public void online(String openId);
	
	public void suspend(String openId);
	
	public boolean queryOnline(String openId);
	
	public Page<TimeDoMain> findByOpenId(String openId,Pageable pageable);
	
	public TimeDoMain findByTimeId(Integer id);
	


}
