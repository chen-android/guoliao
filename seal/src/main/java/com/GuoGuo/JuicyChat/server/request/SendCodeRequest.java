package com.GuoGuo.JuicyChat.server.request;


/**
 * Created by AMing on 15/12/23.
 * Company RongCloud
 */
public class SendCodeRequest {
	private String account;
	
	public SendCodeRequest(String account) {
		this.account = account;
	}
	
	public String getAccount() {
		return account;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}
}
