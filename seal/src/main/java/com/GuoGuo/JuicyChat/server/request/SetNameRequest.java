package com.GuoGuo.JuicyChat.server.request;

/**
 * Created by AMing on 16/1/18.
 * Company RongCloud
 */
public class SetNameRequest {
	
	private String nickName;
	
	private String token;
	
	public SetNameRequest(String nickName, String token) {
		this.nickName = nickName;
		this.token = token;
	}
	
	public String getNickName() {
		return nickName;
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
}
