package com.GuoGuo.JuicyChat.server.request;

import com.GuoGuo.JuicyChat.utils.SharedPreferencesContext;

/**
 * Created by cs on 2017/5/2.
 */

public class BaseTokenRequest {
	private String token;
	
	public BaseTokenRequest() {
		this.token = SharedPreferencesContext.getInstance().getToken();
	}
	
	public BaseTokenRequest(String token) {
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
}
