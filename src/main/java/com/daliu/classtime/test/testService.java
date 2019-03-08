package com.daliu.classtime.test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class testService {
	
	
	
	@Transactional
	public void update1(testEntity teste){
		teste.setName("bb");
	}
	
	

}
