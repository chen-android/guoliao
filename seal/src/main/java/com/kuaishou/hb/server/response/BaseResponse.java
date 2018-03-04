package com.kuaishou.hb.server.response;

/**
 * Created by cs on 2017/5/6.
 */

public class BaseResponse {
	private int code;
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
