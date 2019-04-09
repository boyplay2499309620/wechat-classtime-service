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
	
	private String nickName;
	
	private String sessionKey;
	
	private String unionId;
	
	private String schoolName;
	
	private int sex;
	
	private String avatarUrl;
	
	public UserDoMain(){
		//System.out.println("userDoMain create!");
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
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
		return "UserDoMain [openId=" + openId + ", schoolId=" + schoolId + ", nickName=" + nickName + ", sessionKey="
				+ sessionKey + ", unionId=" + unionId + ", schoolName=" + schoolName + ", sex=" + sex + ", avatarUrl="
				+ avatarUrl + "]";
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avataUrl) {
		this.avatarUrl = avataUrl;
	}

	

	
	
}
