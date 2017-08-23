package com.GuoGuo.JuicyChat.server.response;

import java.util.List;

public class GetUserInfoByPhoneResponse {
	
	private int code;
	private String message;
	
	private List<FriendData> data;
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public List<FriendData> getData() {
		return data;
	}
	
	public void setData(List<FriendData> data) {
		this.data = data;
	}
	
	public static class FriendData {
		private String memberId;
		private String nickName;
		private String headIco;
		
		public String getMemberId() {
			return memberId;
		}
		
		public void setMemberId(String memberId) {
			this.memberId = memberId;
		}
		
		public String getNickName() {
			return nickName;
		}
		
		public void setNickName(String nickName) {
			this.nickName = nickName;
		}
		
		public String getHeadIco() {
			return headIco;
		}
		
		public void setHeadIco(String headIco) {
			this.headIco = headIco;
		}
	}
}
