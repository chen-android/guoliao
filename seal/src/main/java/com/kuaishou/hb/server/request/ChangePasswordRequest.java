package com.kuaishou.hb.server.request;


/**
 */
public class ChangePasswordRequest {

	private String oldPassword;

	private String password;

	private String token;

	public ChangePasswordRequest(String oldPassword, String password, String token) {
		this.oldPassword = oldPassword;
		this.password = password;
		this.token = token;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
