package com.GuoGuo.JuicyChat.db;


import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 */
@Entity
public class GroupMember {
	
	@Id
	private String id;
	private String groupid;
	private String userid;
	private String groupname;
	private long redpacketlimit;
	private long locklimit;
	private String grouphead;
	private String username;
	private String nickname;
	private String userhead;
	private int sex;
	private String leaderid;
	private int isnonotice;
	private String remark;
	
	@Generated(hash = 1024659536)
	public GroupMember(String id, String groupid, String userid, String groupname,
	                   long redpacketlimit, long locklimit, String grouphead, String username,
	                   String nickname, String userhead, int sex, String leaderid, int isnonotice,
	                   String remark) {
		this.id = id;
		this.groupid = groupid;
		this.userid = userid;
		this.groupname = groupname;
		this.redpacketlimit = redpacketlimit;
		this.locklimit = locklimit;
		this.grouphead = grouphead;
		this.username = username;
		this.nickname = nickname;
		this.userhead = userhead;
		this.sex = sex;
		this.leaderid = leaderid;
		this.isnonotice = isnonotice;
		this.remark = remark;
	}
	
	@Generated(hash = 1668463032)
	public GroupMember() {
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
	
	public String getGrouphead() {
		return grouphead;
	}
	
	public void setGrouphead(String grouphead) {
		this.grouphead = grouphead;
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
	
	public String getRemark() {
		return TextUtils.isEmpty(remark) ? nickname : remark;
	}
	
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof GroupMember) {
			GroupMember newP = (GroupMember) obj;
			if (newP.getUserid().equals(this.getUserid()) && newP.getGroupid().equals(this.getGroupid())) {
				return true;
			}
			return false;
		}
		return super.equals(obj);
	}
}
