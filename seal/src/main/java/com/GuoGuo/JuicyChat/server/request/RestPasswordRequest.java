package com.GuoGuo.JuicyChat.server.request;


/**
 */
public class RestPasswordRequest {
	
	private String paypwd;
	private String code;
	private String token;
	
	public RestPasswordRequest(String paypwd, String code, String token) {
		this.paypwd = paypwd;
		this.code = code;
		this.token = token;
	}
	
	public String getPaypwd() {
		return paypwd;
	}
	
	public void setPaypwd(String paypwd) {
		this.paypwd = paypwd;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
}
