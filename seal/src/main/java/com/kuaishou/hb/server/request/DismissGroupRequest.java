package com.kuaishou.hb.server.request;

/**
 * Created by AMing on 16/1/29.
 * Company RongCloud
 */
public class DismissGroupRequest {

	private String groupId;

	private String token;

	public DismissGroupRequest(String groupId, String token) {
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
