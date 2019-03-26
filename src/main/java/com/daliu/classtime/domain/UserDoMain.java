package com.daliu.classtime.domain;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="user")
public class UserDoMain {  
	@Id
	//@GeneratedValue(strategy = "uuid")
	private String openId;
	
	private String schoolId;
	
	private String name;
	
	private String sessionKey;
	
	private String unionId;
	
	private String schoolName;
	
	public UserDoMain(){
		//System.out.println("userDoMain create!");
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	@Override
	public String toString() {
		return "UserDoMain [openId=" + openId + ", schoolId=" + schoolId + ", name=" + name + ", sessionKey="
				+ sessionKey + ", unionId=" + unionId + ", schoolName=" + schoolName + "]";
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	

	
	
}
