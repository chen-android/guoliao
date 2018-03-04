package com.kuaishou.hb.server.request;

/**
 */
public class SetPortraitRequest {

	private String headIco;
	private String token;

	public SetPortraitRequest(String headIco, String token) {
		this.headIco = headIco;
		this.token = token;
	}

	public String getHeadIco() {
		return headIco;
	}

	public void setHeadIco(String headIco) {
		this.headIco = headIco;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
