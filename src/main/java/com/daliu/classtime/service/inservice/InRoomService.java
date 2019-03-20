package com.daliu.classtime.service.inservice;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.daliu.classtime.domain.RoomDoMain;
import com.daliu.classtime.domain.RoomPeopleDoMain;

public interface InRoomService {
	
	public RoomDoMain createNum(String openId,String remark);
	
	public RoomDoMain createNumByNum(String openId,String remark,Integer number);
	
	public RoomDoMain getRoom(String openId,Integer roomNumber);
	
	public void updataAddRoom(RoomDoMain room);
	
	public void updataTime(RoomPeopleDoMain roomPeopleDoMain,int time);
	
	public Page<RoomDoMain> myRoom(String openId,Pageable pageable);
	
	public List<RoomPeopleDoMain> findAllRoomId(Integer openId);
	
	public RoomDoMain findByRoomId(Integer roomId);
	
	public String SendEmail(String openId,Integer roomId,String emailAddress) throws Exception;

}
