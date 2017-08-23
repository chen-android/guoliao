package com.GuoGuo.JuicyChat.server.request;


/**
 * Created by AMing on 16/1/7.
 * Company RongCloud
 */
public class FriendInvitationRequest {
	private String friendId;
	private String token;
	
	public FriendInvitationRequest(String userid, String token) {
		this.token = token;
		this.friendId = userid;
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
