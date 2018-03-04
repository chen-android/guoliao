package com.kuaishou.hb.server.response;

import com.kuaishou.hb.db.Friend;

/**
 * Created by wangmingqiang on 16/9/11.
 * Company RongCloud
 */

public class GetFriendInfoByIDResponse {
	private int code;
	private Friend data;

	public void setCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public Friend getData() {
		return data;
	}

	public void setData(Friend data) {
		this.data = data;
	}
}
