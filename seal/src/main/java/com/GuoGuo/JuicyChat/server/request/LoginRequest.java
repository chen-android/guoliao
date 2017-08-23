package com.GuoGuo.JuicyChat.server.request;


/**
 * Created by AMing on 15/12/24.
 * Company RongCloud
 */
public class LoginRequest {
	
	private String account;
	private String password;
	
	public LoginRequest(String account, String password) {
		this.account = account;
		this.password = password;
	}
	
	public String getAccount() {
		return account;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
