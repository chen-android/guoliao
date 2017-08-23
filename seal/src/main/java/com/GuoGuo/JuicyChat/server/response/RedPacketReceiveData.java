package com.GuoGuo.JuicyChat.server.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chenshuai12619 on 2017-08-22.
 */

public class RedPacketReceiveData implements Parcelable {
	private long id;
	private long tomemberid;
	private long fromuserid;
	private int type;//1 个人  2 群
	private int count;
	private int state;//1可领取  2 已领取  3 过期
	private long userid;
	private long unpackmoney;
	private String createtime;
	private long redpacketmoney;
	private String fromuser;
	private String tomember;
	
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
	
	public long getUserid() {
		return userid;
	}
	
	public void setUserid(long userid) {
		this.userid = userid;
	}
	
	public long getUnpackmoney() {
		return unpackmoney;
	}
	
	public void setUnpackmoney(long unpackmoney) {
		this.unpackmoney = unpackmoney;
	}
	
	public String getCreatetime() {
		return createtime;
	}
	
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	
	public long getRedpacketmoney() {
		return redpacketmoney;
	}
	
	public void setRedpacketmoney(long redpacketmoney) {
		this.redpacketmoney = redpacketmoney;
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
		dest.writeInt(this.count);
		dest.writeInt(this.state);
		dest.writeLong(this.userid);
		dest.writeLong(this.unpackmoney);
		dest.writeString(this.createtime);
		dest.writeLong(this.redpacketmoney);
		dest.writeString(this.fromuser);
		dest.writeString(this.tomember);
	}
	
	public RedPacketReceiveData() {
	}
	
	protected RedPacketReceiveData(Parcel in) {
		this.id = in.readLong();
		this.tomemberid = in.readLong();
		this.fromuserid = in.readLong();
		this.type = in.readInt();
		this.count = in.readInt();
		this.state = in.readInt();
		this.userid = in.readLong();
		this.unpackmoney = in.readLong();
		this.createtime = in.readString();
		this.redpacketmoney = in.readLong();
		this.fromuser = in.readString();
		this.tomember = in.readString();
	}
	
	public static final Parcelable.Creator<RedPacketReceiveData> CREATOR = new Parcelable.Creator<RedPacketReceiveData>() {
		@Override
		public RedPacketReceiveData createFromParcel(Parcel source) {
			return new RedPacketReceiveData(source);
		}
		
		@Override
		public RedPacketReceiveData[] newArray(int size) {
			return new RedPacketReceiveData[size];
		}
	};
}
