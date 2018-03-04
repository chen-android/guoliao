package com.kuaishou.hb.server.response;

/**
 * Created by cs on 2017/5/16.
 */

public class OpenRedPacketResponse {
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
		private long money;

		public int getRedpacketid() {
			return redpacketid;
		}

		public void setRedpacketid(int redpacketid) {
			this.redpacketid = redpacketid;
		}

		public long getMoney() {
			return money;
		}

		public void setMoney(long money) {
			this.money = money;
		}
	}
}
