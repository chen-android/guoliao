package com.kuaishou.hb.server.response;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by cs on 2017/5/16.
 */

public class GetRedPacketUsersResponse {
	private int code;
	private ArrayList<ResultEntity> data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public ArrayList<ResultEntity> getData() {
		return data;
	}

	public void setData(ArrayList<ResultEntity> data) {
		this.data = data;
	}

	public static class ResultEntity implements Serializable {
		private int id;
		private int redpacketid;
		private int userid;
		private String unpacktime;
		private double unpackmoney;
		private double redpacketmoney;
		private String sendtime;
		private String nickname;
		private String username;
		private String headico;
		private int fromuserid;
		private int type;
		private double money;
		private String note;
		private int count;
		private int sort;
		private String createtime;
		private int state;
		private int tomemberid;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getRedpacketid() {
			return redpacketid;
		}

		public void setRedpacketid(int redpacketid) {
			this.redpacketid = redpacketid;
		}

		public int getUserid() {
			return userid;
		}

		public void setUserid(int userid) {
			this.userid = userid;
		}

		public String getUnpacktime() {
			return unpacktime;
		}

		public void setUnpacktime(String unpacktime) {
			this.unpacktime = unpacktime;
		}

		public String getSendtime() {
			return sendtime;
		}

		public void setSendtime(String sendtime) {
			this.sendtime = sendtime;
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

		public int getFromuserid() {
			return fromuserid;
		}

		public void setFromuserid(int fromuserid) {
			this.fromuserid = fromuserid;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}
		
		public double getMoney() {
			return money;
		}
		
		public void setMoney(double money) {
			this.money = money;
		}

		public String getNote() {
			return note;
		}

		public void setNote(String note) {
			this.note = note;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public String getCreatetime() {
			return createtime;
		}

		public void setCreatetime(String createtime) {
			this.createtime = createtime;
		}

		public int getState() {
			return state;
		}

		public void setState(int state) {
			this.state = state;
		}

		public int getTomemberid() {
			return tomemberid;
		}

		public void setTomemberid(int tomemberid) {
			this.tomemberid = tomemberid;
		}
		
		public double getUnpackmoney() {
			return unpackmoney;
		}
		
		public void setUnpackmoney(double unpackmoney) {
			this.unpackmoney = unpackmoney;
		}
		
		public double getRedpacketmoney() {
			return redpacketmoney;
		}
		
		public void setRedpacketmoney(double redpacketmoney) {
			this.redpacketmoney = redpacketmoney;
		}

		public int getSort() {
			return sort;
		}

		public void setSort(int sort) {
			this.sort = sort;
		}
	}
}
