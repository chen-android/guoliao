package com.kuaishou.hb.model;

import android.os.Parcel;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * @author cs
 */

@MessageTag(
		value = "JC:PayResultMsg",
		flag = MessageTag.NONE
)
public class PaySuccessNotifyMessage extends MessageContent {
	private String content;

	public PaySuccessNotifyMessage(byte[] data) {
		String msg = null;

		try {
			msg = new String(data, "UTF-8");
		} catch (UnsupportedEncodingException var5) {

		}

		try {
			JSONObject jsonObject = new JSONObject(msg);
			if (jsonObject.has("content")) {
				this.content = jsonObject.getString("content");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] encode() {
		JSONObject jsonObject = new JSONObject();
		try {
			if (!TextUtils.isEmpty(content)) {
				jsonObject.put("content", content);
			}
		} catch (Exception e) {

		}

		try {
			return jsonObject.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.content);
	}

	protected PaySuccessNotifyMessage(Parcel in) {
		this.content = in.readString();
	}

	public static final Creator<PaySuccessNotifyMessage> CREATOR = new Creator<PaySuccessNotifyMessage>() {
		@Override
		public PaySuccessNotifyMessage createFromParcel(Parcel source) {
			return new PaySuccessNotifyMessage(source);
		}

		@Override
		public PaySuccessNotifyMessage[] newArray(int size) {
			return new PaySuccessNotifyMessage[size];
		}
	};

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
