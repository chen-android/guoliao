package com.kuaishou.hb.server.response;

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
		private double money;
		
		public double getMoney() {
			return money;
		}
		
		public void setMoney(double money) {
			this.money = money;
		}
	}
}
