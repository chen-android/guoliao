package com.GuoGuo.JuicyChat.server.response;

/**
 * Created by cs on 2017/5/16.
 */

public class GetRedPacketStatisticResponse {
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
		private int id;
		private int userid;
		private String moneysend;
		private String moneyreceive;
		private String bestluckcount;
		private String sendcount;
		private String receivecount;
		
		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			this.id = id;
		}
		
		public int getUserid() {
			return userid;
		}
		
		public void setUserid(int userid) {
			this.userid = userid;
		}
		
		public String getMoneysend() {
			return moneysend;
		}
		
		public void setMoneysend(String moneysend) {
			this.moneysend = moneysend;
		}
		
		public String getMoneyreceive() {
			return moneyreceive;
		}
		
		public void setMoneyreceive(String moneyreceive) {
			this.moneyreceive = moneyreceive;
		}
		
		public String getBestluckcount() {
			return bestluckcount;
		}
		
		public void setBestluckcount(String bestluckcount) {
			this.bestluckcount = bestluckcount;
		}
		
		public String getSendcount() {
			return sendcount;
		}
		
		public void setSendcount(String sendcount) {
			this.sendcount = sendcount;
		}
		
		public String getReceivecount() {
			return receivecount;
		}
		
		public void setReceivecount(String receivecount) {
			this.receivecount = receivecount;
		}
	}
}
