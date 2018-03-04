package com.kuaishou.hb.server.response;

/**
 *
 */
public class GetUserInfoByIdResponse {

	private int code;

	private ResultEntity data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public ResultEntity getData() {
		return data;
	}

	public void setData(ResultEntity data) {
		this.data = data;
	}

	public static class ResultEntity {
		private String userid;
		private String nickname;
		private String account;
		private String headico;
		private String sex;

		public String getUserid() {
			return userid;
		}

		public void setUserid(String userid) {
			this.userid = userid;
		}

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		public String getAccount() {
			return account;
		}

		public void setAccount(String account) {
			this.account = account;
		}

		public String getHeadico() {
			return headico;
		}

		public void setHeadico(String headico) {
			this.headico = headico;
		}

		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}
	}
}
