package com.GuoGuo.JuicyChat.server.response;

import com.GuoGuo.JuicyChat.db.Groups;

import java.util.List;

/**
 */
public class GetGroupResponse {
	
	private int code;
	
	private List<Groups> data;
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public List<Groups> getData() {
		return data;
	}
	
	public void setData(List<Groups> data) {
		this.data = data;
	}
}
