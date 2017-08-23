package com.GuoGuo.JuicyChat.server.response;

/**
 * Created by cs on 2017/5/16.
 */

public class CheckRedPacketCountResponse {
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
		private int redpacketid;
		private int count;
		
		public int getRedpacketid() {
			return redpacketid;
		}
		
		public void setRedpacketid(int redpacketid) {
			this.redpacketid = redpacketid;
		}
		
		public int getCount() {
			return count;
		}
		
		public void setCount(int count) {
			this.count = count;
		}
	}
}
