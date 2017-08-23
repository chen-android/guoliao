package com.GuoGuo.JuicyChat.server.request;

/**
 * Created by AMing on 16/2/17.
 * Company RongCloud
 */
public class DeleteFriendRequest {
	private String friendId;
	private String token;
	
	public DeleteFriendRequest(String friendId, String token) {
		this.friendId = friendId;
		this.token = token;
	}
	
	public String getFriendId() {
		return friendId;
	}
	
	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
}
