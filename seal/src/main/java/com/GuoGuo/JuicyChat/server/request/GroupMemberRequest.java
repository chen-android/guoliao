package com.GuoGuo.JuicyChat.server.request;

import java.util.List;

/**
 * Created by AMing on 16/1/28.
 * Company RongCloud
 */
public class GroupMemberRequest {
    private String groupId;
	
	private List<String> userList;
	
	private String token;
    
    public GroupMemberRequest(String groupId, List<String> userList, String token) {
        this.groupId = groupId;
		this.userList = userList;
		this.token = token;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public List<String> getUserList() {
		return userList;
	}
	
	public void setUserList(List<String> userList) {
		this.userList = userList;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
}
