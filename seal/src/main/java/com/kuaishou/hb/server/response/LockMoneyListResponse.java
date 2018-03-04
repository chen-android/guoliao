package com.kuaishou.hb.server.response;

import java.util.List;

/**
 * Created by cs on 2017/5/31.
 */

public class LockMoneyListResponse {

	private int code;
	private List<ResultEntity> data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public List<ResultEntity> getData() {
		return data;
	}

	public void setData(List<ResultEntity> data) {
		this.data = data;
	}

	public static class ResultEntity {
		private String groupid;
		private String groupname;
		private String groupheadico;
		private String lockmoney;
		private String locktime;

		public String getGroupid() {
			return groupid;
		}

		public void setGroupid(String groupid) {
			this.groupid = groupid;
		}

		public String getGroupname() {
			return groupname;
		}

		public void setGroupname(String groupname) {
			this.groupname = groupname;
		}

		public String getGroupheadico() {
			return groupheadico;
		}

		public void setGroupheadico(String groupheadico) {
			this.groupheadico = groupheadico;
		}

		public String getLockmoney() {
			return lockmoney;
		}

		public void setLockmoney(String lockmoney) {
			this.lockmoney = lockmoney;
		}

		public String getLocktime() {
			return locktime;
		}

		public void setLocktime(String locktime) {
			this.locktime = locktime;
		}
	}
}
