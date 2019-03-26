package com.daliu.classtime.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="phone_model")
public class ModelDoMain {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer modelId;
	
	private String openId;
	
	private String brands;
	
	private String models;
	
	private String wechatLanguages;
	
	private String wechatVersion;
	
	private String phoneSystem;
	
	private String wechatPlatform;
	
	private String sdkVersion;

	public Integer getModelId() {
		return modelId;
	}

	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	

	public String getSdkVersion() {
		return sdkVersion;
	}

	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	public String getBrands() {
		return brands;
	}

	public void setBrands(String brands) {
		this.brands = brands;
	}

	public String getModels() {
		return models;
	}

	public void setModels(String models) {
		this.models = models;
	}

	public String getWechatLanguages() {
		return wechatLanguages;
	}

	public void setWechatLanguages(String wechatLanguages) {
		this.wechatLanguages = wechatLanguages;
	}

	public String getWechatVersion() {
		return wechatVersion;
	}

	public void setWechatVersion(String wechatVersion) {
		this.wechatVersion = wechatVersion;
	}

	public String getPhoneSystem() {
		return phoneSystem;
	}

	public void setPhoneSystem(String phoneSystem) {
		this.phoneSystem = phoneSystem;
	}

	public String getWechatPlatform() {
		return wechatPlatform;
	}

	public void setWechatPlatform(String wechatPlatform) {
		this.wechatPlatform = wechatPlatform;
	}

	@Override
	public String toString() {
		return "ModelDoMain [modelId=" + modelId + ", openId=" + openId + ", brands=" + brands + ", models=" + models
				+ ", wechatLanguages=" + wechatLanguages + ", wechatVersion=" + wechatVersion + ", phoneSystem="
				+ phoneSystem + ", wechatPlatform=" + wechatPlatform + ", sdkVersion=" + sdkVersion + "]";
	}


}
