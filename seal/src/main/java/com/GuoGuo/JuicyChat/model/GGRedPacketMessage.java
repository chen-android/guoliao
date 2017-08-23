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
		value = "JC:RedPacketMsg",
		flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED
)
public class GGRedPacketMessage extends MessageContent {
	public static final String CONTENT_PREFIX = "[红包]";
	private String redpacketId;
	private long tomemberid = -1;
	private long fromuserid = -1;
	private int type = -1;//1私聊  2群
	private long money = -1;
	private String content;
	private int sort = -1;//1普通  2任务
	private int count = -1;
	private int state = -1;//1未领取  2已领取  3已退回
	private String createtime;
	
	
	public static GGRedPacketMessage obtain(String redpacketId, long tomemberid, long fromuserid, int type, long money, String content, int sort, int count, int state, String createtime) {
		return new GGRedPacketMessage(redpacketId, tomemberid, fromuserid, type, money, content, sort, count, state, createtime);
	}
	
	public GGRedPacketMessage(String redpacketId, long tomemberid, long fromuserid, int type, long money, String content, int sort, int count, int state, String createtime) {
		this.redpacketId = redpacketId;
		this.tomemberid = tomemberid;
		this.fromuserid = fromuserid;
		this.type = type;
		this.money = money;
		this.content = content;
		this.sort = sort;
		this.count = count;
		this.state = state;
		this.createtime = createtime;
	}
	
	@Override
	public byte[] encode() {
		JSONObject jsonObj = new JSONObject();
		
		try {
			if (!TextUtils.isEmpty(this.redpacketId)) {
				jsonObj.put("redpacketId", this.redpacketId);
			}
			if (tomemberid != -1) {
				jsonObj.put("tomemberid", tomemberid);
			}
			if (fromuserid != -1) {
				jsonObj.put("fromuserid", fromuserid);
			}
			if (type != -1) {
				jsonObj.put("type", type);
			}
			if (money != -1) {
				jsonObj.put("money", money);
			}
			if (!TextUtils.isEmpty(content)) {
				jsonObj.put("content", content);
			}
			if (sort != -1) {
				jsonObj.put("sort", sort);
			}
			if (count != -1) {
				jsonObj.put("count", count);
			}
			if (state != -1) {
				jsonObj.put("state", state);
			}
			if (!TextUtils.isEmpty(createtime)) {
				jsonObj.put("createtime", createtime);
			}
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
	
	public GGRedPacketMessage(byte[] bytes) {
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
			if (var3.has("tomemberid")) {
				this.setTomemberid(var3.optLong("tomemberid"));
			}
			if (var3.has("fromuserid")) {
				this.setFromuserid(var3.optLong("fromuserid"));
			}
			if (var3.has("type")) {
				this.setType(var3.optInt("type"));
			}
			if (var3.has("money")) {
				this.setMoney(var3.optLong("money"));
			}
			if (var3.has("content")) {
				this.setContent(var3.optString("content"));
			}
			if (var3.has("sort")) {
				this.setSort(var3.optInt("sort"));
			}
			if (var3.has("count")) {
				this.setCount(var3.optInt("count"));
			}
			if (var3.has("state")) {
				this.setState(var3.optInt("state"));
			}
			if (var3.has("createtime")) {
				this.setCreatetime(var3.optString("createtime"));
			}
		} catch (org.json.JSONException var4) {
			
		}
	}
	
	
	public GGRedPacketMessage() {
	}
	
	
	public String getRedpacketId() {
		return redpacketId;
	}
	
	public void setRedpacketId(String redpacketId) {
		this.redpacketId = redpacketId;
	}
	
	public long getTomemberid() {
		return tomemberid;
	}
	
	public void setTomemberid(long tomemberid) {
		this.tomemberid = tomemberid;
	}
	
	public long getFromuserid() {
		return fromuserid;
	}
	
	public void setFromuserid(long fromuserid) {
		this.fromuserid = fromuserid;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public long getMoney() {
		return money;
	}
	
	public void setMoney(long money) {
		this.money = money;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getSort() {
		return sort;
	}
	
	public void setSort(int sort) {
		this.sort = sort;
	}
	
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public int getState() {
		return state;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	public String getCreatetime() {
		return createtime;
	}
	
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.redpacketId);
		dest.writeLong(this.tomemberid);
		dest.writeLong(this.fromuserid);
		dest.writeInt(this.type);
		dest.writeLong(this.money);
		dest.writeString(this.content);
		dest.writeInt(this.sort);
		dest.writeInt(this.count);
		dest.writeInt(this.state);
		dest.writeString(this.createtime);
	}
	
	protected GGRedPacketMessage(Parcel in) {
		this.redpacketId = in.readString();
		this.tomemberid = in.readLong();
		this.fromuserid = in.readLong();
		this.type = in.readInt();
		this.money = in.readLong();
		this.content = in.readString();
		this.sort = in.readInt();
		this.count = in.readInt();
		this.state = in.readInt();
		this.createtime = in.readString();
	}
	
	public static final Creator<GGRedPacketMessage> CREATOR = new Creator<GGRedPacketMessage>() {
		@Override
		public GGRedPacketMessage createFromParcel(Parcel source) {
			return new GGRedPacketMessage(source);
		}
		
		@Override
		public GGRedPacketMessage[] newArray(int size) {
			return new GGRedPacketMessage[size];
		}
	};
}
