package com.GuoGuo.JuicyChat.server.response;

import java.util.List;

/**
 * Created by chenshuai12619 on 2017-08-22.
 */

public class RedPacketReceiveResponse {
	private int count;
	private int sum;
	private int code;
	private String message;
	private List<RedPacketReceiveData> data;
	
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public int getSum() {
		return sum;
	}
	
	public void setSum(int sum) {
		this.sum = sum;
	}
	
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
	
	public List<RedPacketReceiveData> getData() {
		return data;
	}
	
	public void setData(List<RedPacketReceiveData> data) {
		this.data = data;
	}
}
