package com.kuaishou.hb.server.request;

/**
 * Created by cs on 2017/5/5.
 */

public class SetSexRequest {
	private String sex;
	private String token;

	public SetSexRequest(String sex, String token) {
		this.sex = sex;
		this.token = token;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
