package com.kuaishou.hb.server.request;

/**
 * Created by AMing on 16/2/17.
 * Company RongCloud
 */
public class SetFriendDisplayNameRequest {
	private String token;
	private String friendId;
	private String remark;

	public SetFriendDisplayNameRequest(String token, String friendId, String remark) {
		this.token = token;
		this.friendId = friendId;
		this.remark = remark;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getFriendId() {
		return friendId;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
