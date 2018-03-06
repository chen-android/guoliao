package com.kuaishou.hb.server.response;

/**
 *
 */
public class GetUserInfoByTokenResponse {

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
		private String id;
		private String nickname;
		private String username;
		private String headico;
		private String createtime;
		private String phone;
		private String email;
		private String sort;
		private String accounttype;
		private double money;
		private double lockmoney;
		private String rebate;
		private String sex;
		private String whatsup;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getHeadico() {
			return headico;
		}

		public void setHeadico(String headico) {
			this.headico = headico;
		}

		public String getCreatetime() {
			return createtime;
		}

		public void setCreatetime(String createtime) {
			this.createtime = createtime;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getSort() {
			return sort;
		}

		public void setSort(String sort) {
			this.sort = sort;
		}

		public String getAccounttype() {
			return accounttype;
		}

		public void setAccounttype(String accounttype) {
			this.accounttype = accounttype;
		}
		
		public double getMoney() {
			return money;
		}
		
		public void setMoney(double money) {
			this.money = money;
		}
		
		public double getLockmoney() {
			return lockmoney;
		}
		
		public void setLockmoney(double lockmoney) {
			this.lockmoney = lockmoney;
		}

		public String getRebate() {
			return rebate;
		}

		public void setRebate(String rebate) {
			this.rebate = rebate;
		}

		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}

		public String getWhatsup() {
			return whatsup;
		}

		public void setWhatsup(String whatsup) {
			this.whatsup = whatsup;
		}
	}
}
