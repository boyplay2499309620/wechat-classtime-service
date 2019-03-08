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
@Table(name="time")
public class TimeDoMain {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String openId;
	
	private String dates;
	
	private String begain;
	
	private String ends;
	
	private int pause;
	
	private String times;
	
	private String pauseMsg;
	
	private String netWorkType;
	
	private Integer roomId;
	
	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getDates() {
		return dates;
	}

	public void setDates(String dates) {
		this.dates = dates;
	}

	public String getBegain() {
		return begain;
	}

	public void setBegain(String begain) {
		this.begain = begain;
	}

	public String getEnds() {
		return ends;
	}

	public void setEnds(String ends) {
		this.ends = ends;
	}

	public int getPause() {
		return pause;
	}

	public void setPause(int pause) {
		this.pause = pause;
	}

	@Override
	public String toString() {
		return "TimeDoMain [id=" + id + ", openId=" + openId + ", dates=" + dates + ", begain=" + begain + ", ends="
				+ ends + ", pause=" + pause + ", times=" + times + ", pauseMsg=" + pauseMsg + ", netWorkType="
				+ netWorkType + ", roomId=" + roomId + "]";
	}

	

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

	public String getPauseMsg() {
		return pauseMsg;
	}

	public void setPauseMsg(String pauseMsg) {
		this.pauseMsg = pauseMsg;
	}

	public String getNetWorkType() {
		return netWorkType;
	}

	public void setNetWorkType(String netWorkType) {
		this.netWorkType = netWorkType;
	}
	
	
}
