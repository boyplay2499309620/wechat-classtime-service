package com.daliu.classtime.dao;



import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.daliu.classtime.domain.RoomDoMain;
import com.mysql.cj.x.protobuf.MysqlxCrud.Find;

public interface RoomDao extends JpaRepository<RoomDoMain, Integer>{

	public RoomDoMain findByRoomNumberAndRoomState(int a, int i);
	
	public RoomDoMain findByOpenIdAndRoomState(String openId,Integer roomState);
	
	public Page<RoomDoMain> findByOpenId(String openId,Pageable pageable);
	
	public RoomDoMain findByRoomId(Integer roomId);
	
	@Query(value="select room_number from room order by room_number desc limit 10",nativeQuery=true)
	public ArrayList<Integer> find();
	

}
