package com.GuoGuo.JuicyChat.server.request;

/**
 * Created by AMing on 16/2/2.
 * Company RongCloud
 */
public class SetGroupDisplayNameRequest {
	
	private String groupId;
	private String token;
	private String groupName;
	private String redPacketLimit;
	private String lockLimit;
	private String headIco;
	private int isnonotice;
	private String gonggao;
	private int iscanadduser;
	
	public SetGroupDisplayNameRequest(String groupId, String token, String groupName, String redPacketLimit, String lockLimit, String headIco, int isnonotice, String gonggao, int iscanadduser) {
		this.groupId = groupId;
		this.token = token;
		this.groupName = groupName;
		this.redPacketLimit = redPacketLimit;
		this.lockLimit = lockLimit;
		this.headIco = headIco;
		this.isnonotice = isnonotice;
		this.gonggao = gonggao;
		this.iscanadduser = iscanadduser;
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
	
	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public String getRedPacketLimit() {
		return redPacketLimit;
	}
	
	public void setRedPacketLimit(String redPacketLimit) {
		this.redPacketLimit = redPacketLimit;
	}
	
	public String getLockLimit() {
		return lockLimit;
	}
	
	public void setLockLimit(String lockLimit) {
		this.lockLimit = lockLimit;
	}
	
	public String getHeadIco() {
		return headIco;
	}
	
	public void setHeadIco(String headIco) {
		this.headIco = headIco;
	}
	
	public int getIsnonotice() {
		return isnonotice;
	}
	
	public void setIsnonotice(int isnonotice) {
		this.isnonotice = isnonotice;
	}
	
	public String getGonggao() {
		return gonggao;
	}
	
	public void setGonggao(String gonggao) {
		this.gonggao = gonggao;
	}
	
	public int getIscanadduser() {
		return iscanadduser;
	}
	
	public void setIscanadduser(int iscanadduser) {
		this.iscanadduser = iscanadduser;
	}
}
