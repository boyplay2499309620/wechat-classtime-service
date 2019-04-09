package com.daliu.classtime.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daliu.classtime.domain.ModelDoMain;

public interface ModelDao extends JpaRepository<ModelDoMain, Integer>{

	public List<ModelDoMain> findByOpenId(String openId);
}
