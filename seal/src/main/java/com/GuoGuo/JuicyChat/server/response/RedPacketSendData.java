package com.GuoGuo.JuicyChat.server.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chenshuai12619 on 2017-08-22.
 */

public class RedPacketSendData implements Parcelable {
	private long id;
	private long tomemberid;
	private long fromuserid;
	private int type;//1 个人  2 群
	private long money;
	private String note;
	private int count;
	private int state;//1可领取  2 已领取  3 过期
	private String createtime;
	private String fromuser;
	private String tomember;
	private long unpacksummoney;
	private int unpackcount;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
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
	
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
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
	
	public String getFromuser() {
		return fromuser;
	}
	
	public void setFromuser(String fromuser) {
		this.fromuser = fromuser;
	}
	
	public String getTomember() {
		return tomember;
	}
	
	public void setTomember(String tomember) {
		this.tomember = tomember;
	}
	
	public long getUnpacksummoney() {
		return unpacksummoney;
	}
	
	public void setUnpacksummoney(long unpacksummoney) {
		this.unpacksummoney = unpacksummoney;
	}
	
	public int getUnpackcount() {
		return unpackcount;
	}
	
	public void setUnpackcount(int unpackcount) {
		this.unpackcount = unpackcount;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.id);
		dest.writeLong(this.tomemberid);
		dest.writeLong(this.fromuserid);
		dest.writeInt(this.type);
		dest.writeLong(this.money);
		dest.writeString(this.note);
		dest.writeInt(this.count);
		dest.writeInt(this.state);
		dest.writeString(this.createtime);
		dest.writeString(this.fromuser);
		dest.writeString(this.tomember);
		dest.writeLong(this.unpacksummoney);
		dest.writeInt(this.unpackcount);
	}
	
	public RedPacketSendData() {
	}
	
	protected RedPacketSendData(Parcel in) {
		this.id = in.readLong();
		this.tomemberid = in.readLong();
		this.fromuserid = in.readLong();
		this.type = in.readInt();
		this.money = in.readLong();
		this.note = in.readString();
		this.count = in.readInt();
		this.state = in.readInt();
		this.createtime = in.readString();
		this.fromuser = in.readString();
		this.tomember = in.readString();
		this.unpacksummoney = in.readLong();
		this.unpackcount = in.readInt();
	}
	
	public static final Creator<RedPacketSendData> CREATOR = new Creator<RedPacketSendData>() {
		@Override
		public RedPacketSendData createFromParcel(Parcel source) {
			return new RedPacketSendData(source);
		}
		
		@Override
		public RedPacketSendData[] newArray(int size) {
			return new RedPacketSendData[size];
		}
	};
}
