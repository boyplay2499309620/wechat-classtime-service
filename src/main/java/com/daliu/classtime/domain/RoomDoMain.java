package com.daliu.classtime.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Entity
@Table(name="room")
public class RoomDoMain {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer roomId;
	
	private Integer roomNumber;
	
	private String openId;
	
	private Integer roomState;
	
	private Integer roomPeoples;
	
	private String remark;
	
	private String createTime;

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public Integer getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(Integer roomNumber) {
		this.roomNumber = roomNumber;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public Integer getRoomState() {
		return roomState;
	}

	public void setRoomState(Integer roomState) {
		this.roomState = roomState;
	}

	public Integer getRoomPeoples() {
		return roomPeoples;
	}

	public void setRoomPeoples(Integer roomPeoples) {
		this.roomPeoples = roomPeoples;
	}

	@Override
	public String toString() {
		return "RoomDoMain [roomId=" + roomId + ", roomNumber=" + roomNumber + ", openId=" + openId + ", roomState="
				+ roomState + ", roomPeoples=" + roomPeoples + ", remark=" + remark + ", createTime=" + createTime
				+ "]";
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
