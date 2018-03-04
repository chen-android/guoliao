package com.kuaishou.hb.server.response;

import com.kuaishou.hb.db.Groups;

/**
 * Created by AMing on 16/1/26.
 * Company RongCloud
 */
public class GetGroupInfoResponse {


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
		private String groupid;
		private String userid;
		private String groupname;
		private long redpacketlimit;
		private long locklimit;
		private String groupico;
		private String username;
		private String nickname;
		private String userhead;
		private int sex;
		private String leaderid;
		private int isnonotice;
		private String gonggao;
		private int iscanadduser;
		private String remark;

		public Groups toGroups() {
			return new Groups(id, groupid, userid, groupname, redpacketlimit, locklimit, groupico,
					username, nickname, userhead, sex, leaderid, isnonotice, gonggao, iscanadduser, remark);
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getGroupid() {
			return groupid;
		}

		public void setGroupid(String groupid) {
			this.groupid = groupid;
		}

		public String getUserid() {
			return userid;
		}

		public void setUserid(String userid) {
			this.userid = userid;
		}

		public String getGroupname() {
			return groupname;
		}

		public void setGroupname(String groupname) {
			this.groupname = groupname;
		}

		public long getRedpacketlimit() {
			return redpacketlimit;
		}

		public void setRedpacketlimit(long redpacketlimit) {
			this.redpacketlimit = redpacketlimit;
		}

		public long getLocklimit() {
			return locklimit;
		}

		public void setLocklimit(long locklimit) {
			this.locklimit = locklimit;
		}

		public String getGroupico() {
			return groupico;
		}

		public void setGroupico(String groupico) {
			this.groupico = groupico;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		public String getUserhead() {
			return userhead;
		}

		public void setUserhead(String userhead) {
			this.userhead = userhead;
		}

		public int getSex() {
			return sex;
		}

		public void setSex(int sex) {
			this.sex = sex;
		}

		public String getLeaderid() {
			return leaderid;
		}

		public void setLeaderid(String leaderid) {
			this.leaderid = leaderid;
		}

		public int getIsnonotice() {
			return isnonotice;
		}

		public void setIsnonotice(int isnonotice) {
			this.isnonotice = isnonotice;
		}

		public String getGonggao() {
			return gonggao;
		}

		public void setGonggao(String gonggao) {
			this.gonggao = gonggao;
		}

		public int getIscanadduser() {
			return iscanadduser;
		}

		public void setIscanadduser(int iscanadduser) {
			this.iscanadduser = iscanadduser;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}
	}
}
