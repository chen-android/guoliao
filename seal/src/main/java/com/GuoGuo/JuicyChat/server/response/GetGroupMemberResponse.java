package com.GuoGuo.JuicyChat.server.response;

import com.GuoGuo.JuicyChat.db.GroupMember;

import java.io.Serializable;
import java.util.List;

/**
 */
public class GetGroupMemberResponse implements Serializable {
	
	private static final long serialVersionUID = -3972802951229254770L;
	
	private int code;
	
	private List<GroupMember> data;
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public List<GroupMember> getData() {
		return data;
	}
	
	public void setData(List<GroupMember> data) {
		this.data = data;
	}
}
