package com.kuaishou.hb.server.response;

/**
 * Created by cs on 2017/6/1.
 */

public class GetShareRewardResponse {
	private int code;
	private String message;
	private ResultEntity data;

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

	public ResultEntity getData() {
		return data;
	}

	public void setData(ResultEntity data) {
		this.data = data;
	}

	public static class ResultEntity {
		private String money;

		public String getMoney() {
			return money;
		}

		public void setMoney(String money) {
			this.money = money;
		}
	}
}
