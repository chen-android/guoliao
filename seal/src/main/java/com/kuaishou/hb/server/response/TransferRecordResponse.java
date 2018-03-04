package com.kuaishou.hb.server.response;

import java.util.List;

/**
 * Created by cs on 2017/8/20.
 */

public class TransferRecordResponse {
	private int count;
	private int sum;
	private int code;
	private String message;
	private List<TransferRecordData> data;

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

	public List<TransferRecordData> getData() {
		return data;
	}

	public void setData(List<TransferRecordData> data) {
		this.data = data;
	}
}
