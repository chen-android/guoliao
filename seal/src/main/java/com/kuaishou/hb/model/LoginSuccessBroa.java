package com.kuaishou.hb.model;

/**
 * Created by cs on 2017/5/29.
 */

public class LoginSuccessBroa {
	private String phone;
	private String pwd;
	private String userId;
	private String unionid;

	public LoginSuccessBroa() {
	}
	
	public LoginSuccessBroa(String phone, String pwd, String userId, String unionid) {
		this.phone = phone;
		this.pwd = pwd;
		this.userId = userId;
		this.unionid = unionid;
	}
	
	public String getPhone() {
		return phone == null ? "" : phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getPwd() {
		return pwd == null ? "" : pwd;
	}
	
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	public String getUserId() {
		return userId == null ? "" : userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getUnionid() {
		return unionid == null ? "" : unionid;
	}
	
	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
}
