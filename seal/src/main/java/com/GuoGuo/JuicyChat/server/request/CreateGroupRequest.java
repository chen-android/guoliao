package com.GuoGuo.JuicyChat.server.request;

import java.util.List;

/**
 * Created by AMing on 16/1/25.
 * Company RongCloud
 */
public class CreateGroupRequest {
	
	private String groupName;
	private String token;
	private String headico;
	private List<Integer> userList;
	
	public CreateGroupRequest(String groupName, String token, String headico, List<Integer> userList) {
		this.groupName = groupName;
		this.token = token;
		this.headico = headico;
		this.userList = userList;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public String getHeadico() {
		return headico;
	}
	
	public void setHeadico(String headico) {
		this.headico = headico;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public List<Integer> getUserList() {
		return userList;
	}
	
	public void setUserList(List<Integer> userList) {
		this.userList = userList;
	}
}
