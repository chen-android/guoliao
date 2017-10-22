package com.GuoGuo.JuicyChat.server.response;

/**
 * Created by chenshuai12619 on 2017-10-20.
 */

public class TransferRecordTypesData {
	private int typeid;
	private String typename;

	public TransferRecordTypesData() {
	}

	public TransferRecordTypesData(int typeid, String typename) {
		this.typeid = typeid;
		this.typename = typename;
	}

	public int getTypeid() {
		return typeid;
    }

	public void setTypeid(int typeid) {
		this.typeid = typeid;
    }
    
    public String getTypename() {
        return typename;
    }
    
    public void setTypename(String typename) {
        this.typename = typename;
    }
}
