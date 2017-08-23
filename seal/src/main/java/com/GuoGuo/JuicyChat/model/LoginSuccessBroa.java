package com.GuoGuo.JuicyChat.model;

/**
 * Created by cs on 2017/5/29.
 */

public class LoginSuccessBroa {
	private String phone;
	private String pwd;
	private String userId;
	
	public LoginSuccessBroa() {
	}
	
	public LoginSuccessBroa(String phone, String pwd, String userId) {
		this.phone = phone;
		this.pwd = pwd;
		this.userId = userId;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getPwd() {
		return pwd;
	}
	
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
}
