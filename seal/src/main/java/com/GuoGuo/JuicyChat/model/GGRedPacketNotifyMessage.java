package com.GuoGuo.JuicyChat.model;

import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * Created by cs on 2017/5/12.
 */

@MessageTag(
		value = "JC:RedPacketNtf",
		flag = MessageTag.ISPERSISTED
)
public class GGRedPacketNotifyMessage extends MessageContent {
	private String redpacketId;
	private String message;
	private String iosmessage;
	private String touserid;
	private int islink;
	
	public static GGRedPacketNotifyMessage obtain(String redpacketId, String message, String iosmessage, String touserid, int islink) {
		return new GGRedPacketNotifyMessage(redpacketId, message, iosmessage, touserid, islink);
	}
	
	public GGRedPacketNotifyMessage(String redpacketId, String message, String iosmessage, String touserid, int islink) {
		this.redpacketId = redpacketId;
		this.message = message;
		this.touserid = touserid;
		this.iosmessage = iosmessage;
		this.islink = islink;
	}
	
	@Override
	public byte[] encode() {
		JSONObject jsonObj = new JSONObject();

		try {
			if (!TextUtils.isEmpty(this.redpacketId)) {
				jsonObj.put("redpacketId", this.redpacketId);
			}
			if (!TextUtils.isEmpty(this.message)) {
				jsonObj.put("message", this.message);
			}
			if (!TextUtils.isEmpty(this.iosmessage)) {
				jsonObj.put("iosmessage", this.iosmessage);
			}
			if (!TextUtils.isEmpty(this.touserid)) {
				jsonObj.put("touserid", this.touserid);
			}
			jsonObj.put("islink", this.islink);
			
		} catch (JSONException e) {
			Log.e("JSONException", e.getMessage());
		}

		try {
			return jsonObj.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public GGRedPacketNotifyMessage(byte[] bytes) {
		String msg = null;

		try {
			msg = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException var5) {

		}

		try {
			org.json.JSONObject var3 = new org.json.JSONObject(msg);
			if (var3.has("redpacketId")) {
				this.setRedpacketId(var3.optString("redpacketId"));
			}
			if (var3.has("message")) {
				this.setMessage(var3.optString("message"));
			}
			if (var3.has("iosmessage")) {
				this.setIosmessage(var3.optString("iosmessage"));
			}
			if (var3.has("touserid")) {
				this.setTouserid(var3.optString("touserid"));
			}
			if (var3.has("islink")) {
				this.setIslink(var3.optInt("islink"));
			}
			
		} catch (org.json.JSONException var4) {

		}
	}


	public GGRedPacketNotifyMessage() {
	}


	public String getRedpacketId() {
		return redpacketId;
	}

	public void setRedpacketId(String redpacketId) {
		this.redpacketId = redpacketId;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getIosmessage() {
		return iosmessage;
	}
	
	public void setIosmessage(String iosmessage) {
		this.iosmessage = iosmessage;
	}
	
	public String getTouserid() {
		return touserid;
	}
	
	public void setTouserid(String touserid) {
		this.touserid = touserid;
	}
	
	public int getIslink() {
		return islink;
	}
	
	public void setIslink(int islink) {
		this.islink = islink;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.redpacketId);
		dest.writeString(this.message);
		dest.writeString(this.iosmessage);
		dest.writeString(this.touserid);
		dest.writeInt(this.islink);
	}

	protected GGRedPacketNotifyMessage(Parcel in) {
		this.redpacketId = in.readString();
		this.message = in.readString();
		this.iosmessage = in.readString();
		this.touserid = in.readString();
		this.islink = in.readInt();
	}

	public static final Creator<GGRedPacketNotifyMessage> CREATOR = new Creator<GGRedPacketNotifyMessage>() {
		@Override
		public GGRedPacketNotifyMessage createFromParcel(Parcel source) {
			return new GGRedPacketNotifyMessage(source);
		}

		@Override
		public GGRedPacketNotifyMessage[] newArray(int size) {
			return new GGRedPacketNotifyMessage[size];
		}
	};
}
