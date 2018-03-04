package com.kuaishou.hb.server.response;

import java.util.List;

/**
 * Created by chenshuai12619 on 2017-10-20.
 */

public class TransferRecordTypesRes {
	private int code;
	private List<TransferRecordTypesData> data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public List<TransferRecordTypesData> getData() {
		return data;
	}

	public void setData(List<TransferRecordTypesData> data) {
		this.data = data;
	}
}
