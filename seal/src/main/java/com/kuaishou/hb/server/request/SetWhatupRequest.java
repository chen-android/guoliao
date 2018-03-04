package com.kuaishou.hb.server.request;

/**
 * Created by AMing on 16/1/18.
 * Company RongCloud
 */
public class SetWhatupRequest {

	private String whatsup;

	private String token;

	public SetWhatupRequest(String whatsup, String token) {
		this.whatsup = whatsup;
		this.token = token;
	}

	public String getWhatsup() {
		return whatsup;
	}

	public void setWhatsup(String whatsup) {
		this.whatsup = whatsup;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
