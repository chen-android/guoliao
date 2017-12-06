package com.GuoGuo.JuicyChat.server.response;

/**
 * Created by AMing on 16/2/17.
 * Company RongCloud
 */
public class DeleteFriendResponse {
	
	private int code;
    private String message;
    
    public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
