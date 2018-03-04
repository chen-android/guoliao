package com.kuaishou.hb.server.request;


/**
 * Created by AMing on 16/1/7.
 * Company RongCloud
 */
public class FriendInvitationRequest {
	private String friendId;
	private String token;
	private String note;

	public FriendInvitationRequest(String userid, String token, String note) {
		this.token = token;
		this.friendId = userid;
		this.note = note;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}


