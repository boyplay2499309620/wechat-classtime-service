package com.daliu.classtime.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.context.annotation.Scope;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Component;


/**
 * 这张表用来映射每个房间的人员信息
 * @author Administrator
 *
 */
@Component
@Scope("prototype")
@Entity
@Table(name="room_people")
public class RoomPeopleDoMain { 
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String openId;
	
	private Integer roomId;
	
	private String begainTime;
	
	private Integer times;
	
	private String name;
	
	private String schoolId;
	
	//不在数据库的字段，用于向前端传送是否在线的标志
	//@NotNull(message = "状态不能为空",groups = Group.class)
	@Transient  
	//忽略不了
	//java.sql.SQLSyntaxErrorException: Unknown column 'roompeople0_.state' in 'field list'
	private transient Integer state;

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public String getBegainTime() {
		return begainTime;
	}

	public void setBegainTime(String begainTime) {
		this.begainTime = begainTime;
	}

	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
	}

	@Override
	public String toString() {
		return "RoomPeopleDoMain [id=" + id + ", openId=" + openId + ", roomId=" + roomId + ", begainTime=" + begainTime
				+ ", times=" + times + ", name=" + name + ", schoolId=" + schoolId + ", state=" + state + "]";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}
	
	

}
