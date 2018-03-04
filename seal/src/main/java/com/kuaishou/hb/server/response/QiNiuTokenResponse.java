package com.kuaishou.hb.server.response;

public class QiNiuTokenResponse {

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
		private String qiniutoken;

		public String getQiniutoken() {
			return qiniutoken;
		}

		public void setQiniutoken(String qiniutoken) {
			this.qiniutoken = qiniutoken;
		}
	}
}
