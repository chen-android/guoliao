package com.GuoGuo.JuicyChat.server.response;

/**
 * 获取余额
 * Created by cs on 2017/5/13.
 */

public class GetMoneyResponse {
	private int code;
	private ResultEntity data;
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public ResultEntity getData() {
		return data;
	}
	
	public void setData(ResultEntity data) {
		this.data = data;
	}
	
	public static class ResultEntity {
		private long money;
		
		public long getMoney() {
			return money;
		}
		
		public void setMoney(long money) {
			this.money = money;
		}
	}
}
