package com.GuoGuo.JuicyChat.server.response;


import com.GuoGuo.JuicyChat.db.Friend;

/**
 */
public class FriendInvitationResponse {
	
	
	private int code;
	private String message;
	
	private Friend data;
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Friend getData() {
		return data;
	}
	
	public void setData(Friend data) {
		this.data = data;
	}
	
}
