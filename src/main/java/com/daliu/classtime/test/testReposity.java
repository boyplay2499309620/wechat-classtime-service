package com.daliu.classtime.test;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface testReposity extends JpaRepository<testEntity,String>{

	testEntity findByIds(String string);
	 

}
