package com.GuoGuo.JuicyChat.server.response;

import java.io.Serializable;

/**
 * Created by cs on 2017/5/16.
 */

public class GetRedPacketDetailResponse {
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
	
	public static class ResultEntity implements Serializable {
		private int id;
		private int fromuserid;
		private int type;//1个人  2群
		private long money;
		private String note;
		private int count;
		private String createtime;
		private int state;//1未领取   2已领取   3已退回
		private String fromnickname;
		private String fromusername;
		private String fromheadico;
		private long unpacksummoney;
		private int unpackcount;
		private int tomemberid;
		private int bestluckuserid;
		
		public long getMoney() {
			return money;
		}
		
		public void setMoney(long money) {
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
		
		public String getFromnickname() {
			return fromnickname;
		}
		
		public void setFromnickname(String fromnickname) {
			this.fromnickname = fromnickname;
		}
		
		public String getFromusername() {
			return fromusername;
		}
		
		public void setFromusername(String fromusername) {
			this.fromusername = fromusername;
		}
		
		public String getFromheadico() {
			return fromheadico;
		}
		
		public void setFromheadico(String fromheadico) {
			this.fromheadico = fromheadico;
		}
		
		public long getUnpacksummoney() {
			return unpacksummoney;
		}
		
		public void setUnpacksummoney(long unpacksummoney) {
			this.unpacksummoney = unpacksummoney;
		}
		
		public int getUnpackcount() {
			return unpackcount;
		}
		
		public void setUnpackcount(int unpackcount) {
			this.unpackcount = unpackcount;
		}
		
		public int getTomemberid() {
			return tomemberid;
		}
		
		public void setTomemberid(int tomemberid) {
			this.tomemberid = tomemberid;
		}
		
		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			this.id = id;
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
		
		public int getBestluckuserid() {
			return bestluckuserid;
		}
		
		public void setBestluckuserid(int bestluckuserid) {
			this.bestluckuserid = bestluckuserid;
		}
	}
}
