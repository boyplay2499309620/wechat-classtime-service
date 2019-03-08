package com.daliu.classtime.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.context.annotation.Scope;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Entity
@Table(name="ranking")
public class RankDoMain {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Integer times;
	
	private String openId;
	
	
	
	//不需要持久化的字段
	@Transient 
	private transient String name;
	
	@Transient
	private transient String schoolId;
	
	
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	@Override
	public String toString() {
		return "RankDoMain [id=" + id + ", times=" + times + ", openId=" + openId + ", name=" + name + ", schoolId="
				+ schoolId + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

}
