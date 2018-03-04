package com.kuaishou.hb.server.response;

import com.kuaishou.hb.db.Groups;

/**
 * Created by AMing on 16/1/25.
 * Company RongCloud
 */
public class CreateGroupResponse {


	/**
	 * code : 200
	 * result : {"id":"ArVtlWJSv"}
	 */

	private int code;
	/**
	 * id : ArVtlWJSv
	 */

	private Groups data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Groups getData() {
		return data;
	}

	public void setData(Groups data) {
		this.data = data;
	}
}
