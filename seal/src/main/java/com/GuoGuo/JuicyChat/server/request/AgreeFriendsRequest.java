package com.GuoGuo.JuicyChat.server.request;


/**
 * Created by AMing on 16/1/8.
 * Company RongCloud
 */
public class AgreeFriendsRequest {
	
	private String friendId;
	private String token;
	private int state;
	
	public AgreeFriendsRequest(String friendId, String token, int state) {
		this.friendId = friendId;
		this.token = token;
		this.state = state;
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
	
	public int getState() {
		return state;
	}
	
	public void setState(int state) {
		this.state = state;
	}
}
