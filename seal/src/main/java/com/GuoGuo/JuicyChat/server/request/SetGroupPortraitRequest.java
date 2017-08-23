package com.GuoGuo.JuicyChat.server.request;

/**
 * Created by AMing on 16/1/25.
 * Company RongCloud
 */
public class SetGroupPortraitRequest {
	
	private String groupId;
	private String token;
	private String headIco;
	private int redPacketLimit = 0;
	private int lockLimit = 0;
	
	public SetGroupPortraitRequest(String groupId, String token, String headIco) {
		this.groupId = groupId;
		this.token = token;
		this.headIco = headIco;
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
	
	public String getHeadIco() {
		return headIco;
	}
	
	public void setHeadIco(String headIco) {
		this.headIco = headIco;
	}
	
	public int getRedPacketLimit() {
		return redPacketLimit;
	}
	
	public void setRedPacketLimit(int redPacketLimit) {
		this.redPacketLimit = redPacketLimit;
	}
	
	public int getLockLimit() {
		return lockLimit;
	}
	
	public void setLockLimit(int lockLimit) {
		this.lockLimit = lockLimit;
	}
}
