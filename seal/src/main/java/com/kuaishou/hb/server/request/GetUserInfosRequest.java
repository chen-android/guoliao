package com.kuaishou.hb.server.request;

/**
 * Created by AMing on 16/5/23.
 * Company RongCloud
 */
public class GetUserInfosRequest {
	private String token;

	public GetUserInfosRequest(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
