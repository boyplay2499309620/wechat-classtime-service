package com.daliu.classtime.test;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class test {
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	private testService tests;
	
	@Autowired
	private testReposity testr;
	
	@Autowired
	private testEntity teste;
	
	@RequestMapping("/test1")
	public String test1(){
		stringRedisTemplate.opsForValue().set("room1234567890","room1234567890",3,TimeUnit.SECONDS);
		//stringRedisTemplate.expire("key1",10,TimeUnit.SECONDS);
		return stringRedisTemplate.opsForValue().get("room1234567890");
		//https://daliu.mynatapp.cc/redis/test1
	}
	
	@RequestMapping("/test2")
	public String tests(){
		System.out.println(stringRedisTemplate.opsForValue().get("aaa"));
		return stringRedisTemplate.opsForValue().get("aaa");
	}
	
	@RequestMapping("/word")
    public String sayHello() {
        return "/hello";
    }
	
	@RequestMapping("/test3")
	public void  tests2(){
		 teste= testr.findByIds("1");
		 System.out.println(teste);
		tests.update1(teste);
	}
	

}
