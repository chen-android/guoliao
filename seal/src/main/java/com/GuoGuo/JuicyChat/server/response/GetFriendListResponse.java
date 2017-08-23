package com.GuoGuo.JuicyChat.server.response;

import com.GuoGuo.JuicyChat.db.Friend;

import java.util.List;

/**
 * Created by cs on 2017/5/2.
 */

public class GetFriendListResponse {
	private int code;
	private String message;
	private List<Friend> data;
	
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
	
	public List<Friend> getData() {
		return data;
	}
	
	public void setData(List<Friend> data) {
		this.data = data;
	}
}
