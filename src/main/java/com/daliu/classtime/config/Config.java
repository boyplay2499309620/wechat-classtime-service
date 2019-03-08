package com.daliu.classtime.config;

import java.util.Random;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class Config { 
	
	@Bean
	public Random Random(){
		return new Random();
	}
	
	

}
