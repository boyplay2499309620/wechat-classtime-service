package com.daliu.classtime.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daliu.classtime.domain.TimeDoMain;

@RestController
@RequestMapping("/testssss")
public class internetTest {
	
	@Autowired
	public TimeDoMain time;
	
	@RequestMapping("/connection")
	public String test(){
		return "successful!!";
	}
	
	@RequestMapping("/test1")
	public String test1(){
		
		return time.getOpenId()+time.getBegain()+time.getEnds();
		//  http://localhost:8080/test/test1
	}
	
	@RequestMapping("/test2")
	public void test2(){
		
	}
	

}
