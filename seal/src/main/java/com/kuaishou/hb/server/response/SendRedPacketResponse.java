package com.kuaishou.hb.server.response;

import com.kuaishou.hb.model.RedPacketMessage;

/**
 * Created by cs on 2017/5/13.
 */

public class SendRedPacketResponse {
	private int code;
	private RedPacketMessage data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public RedPacketMessage getData() {
		return data;
	}

	public void setData(RedPacketMessage data) {
		this.data = data;
	}
}
