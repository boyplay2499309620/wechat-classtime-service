/**
 * 
 */
package com.daliu.classtime.utils;

/**  
* @Title: WxGetOpneIdReturnJson.java
* @Package:com.daliu.classtime.utils
* @Description:(作用)
* @author:刘严岩 
* @date:2019年4月14日
*/
public class WxGetOpneIdReturnJson {
	
	private String openid;
	
	private String session_key;
	
	private String unionid;
	
	private Integer errcode;
	
	private String errmsg;

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getSession_key() {
		return session_key;
	}

	public void setSession_key(String session_key) {
		this.session_key = session_key;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public Integer getErrcode() {
		return errcode;
	}

	public void setErrcode(Integer errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	@Override
	public String toString() {
		return "WxGetOpneIdReturnJson [openid=" + openid + ", session_key=" + session_key + ", unionid=" + unionid
				+ ", errcode=" + errcode + ", errmsg=" + errmsg + "]";
	}
	
	

}
