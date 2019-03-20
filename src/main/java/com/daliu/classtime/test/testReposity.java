package com.daliu.classtime.test;


import org.springframework.data.jpa.repository.JpaRepository;


public interface testReposity extends JpaRepository<testEntity,String>{

	testEntity findByIds(String string);
	 

}
