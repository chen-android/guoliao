package com.kuaishou.hb.server.response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenshuai12619
 * @date 2018-05-03
 */
public class GetVipUserListResponse {
	private int code;
	private String message;
	private List<ResultEntity> data;
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getMessage() {
		return message == null ? "" : message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public List<ResultEntity> getData() {
		if (data == null) {
			return new ArrayList<>();
		}
		return data;
	}
	
	public void setData(List<ResultEntity> data) {
		this.data = data;
	}
	
	public static class ResultEntity {
		private String userid;
		private String account;
		private String nickname;
		private String headico;
		private String sex;
		private String whatsup;
		
		public String getUserid() {
			return userid == null ? "" : userid;
		}
		
		public void setUserid(String userid) {
			this.userid = userid;
		}
		
		public String getAccount() {
			return account == null ? "" : account;
		}
		
		public void setAccount(String account) {
			this.account = account;
		}
		
		public String getNickname() {
			return nickname == null ? "" : nickname;
		}
		
		public void setNickname(String nickname) {
			this.nickname = nickname;
		}
		
		public String getHeadico() {
			return headico == null ? "" : headico;
		}
		
		public void setHeadico(String headico) {
			this.headico = headico;
		}
		
		public String getSex() {
			return sex == null ? "" : sex;
		}
		
		public void setSex(String sex) {
			this.sex = sex;
		}
		
		public String getWhatsup() {
			return whatsup == null ? "" : whatsup;
		}
		
		public void setWhatsup(String whatsup) {
			this.whatsup = whatsup;
		}
	}
}
