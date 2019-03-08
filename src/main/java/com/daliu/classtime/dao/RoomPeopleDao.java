package com.daliu.classtime.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daliu.classtime.domain.RoomPeopleDoMain;

public interface RoomPeopleDao extends JpaRepository<RoomPeopleDoMain,String>{

	RoomPeopleDoMain findByOpenIdAndRoomId(String openId, Integer roomId);
	
	List<RoomPeopleDoMain> findByRoomId(Integer roomId);

}
