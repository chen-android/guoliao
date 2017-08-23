package com.GuoGuo.JuicyChat.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class BlackList {
	
	@Id
	private String userId;
	
	private String status;
	private Long timestamp;
	
	@Generated(hash = 1041967902)
	public BlackList(String userId, String status, Long timestamp) {
		this.userId = userId;
		this.status = status;
		this.timestamp = timestamp;
	}
	
	@Generated(hash = 1200343381)
	public BlackList() {
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
}
