package com.GuoGuo.JuicyChat.server.request;

/**
 * Created by cs on 2017/5/11.
 */

public class GetGroupMemberRequest {
	private String groupId;
	private String token;
	
	public GetGroupMemberRequest(String groupId, String token) {
		this.groupId = groupId;
		this.token = token;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
}
