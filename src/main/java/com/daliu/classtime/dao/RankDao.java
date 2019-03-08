package com.daliu.classtime.dao;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.daliu.classtime.domain.RankDoMain;


public interface RankDao extends JpaRepository<RankDoMain, Integer>{
	
	public RankDoMain findByOpenId(String openId);
	
	@Query(value = "select * from ranking order by times desc limit 10", nativeQuery = true)
	public ArrayList<RankDoMain> findRank();

}
