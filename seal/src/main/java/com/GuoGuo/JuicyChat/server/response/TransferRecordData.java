package com.GuoGuo.JuicyChat.server.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cs on 2017/8/20.
 */

public class TransferRecordData implements Parcelable {
	private String fromuserid;
	private String touserid;
	private String money;
	private String time;
	private int type;
	private String option;
	private String touser;
	private String fromuser;
	private String fromuserheadico;
	private String touserheadico;
	private String note;

	public String getFromuserid() {
		return fromuserid;
	}

	public void setFromuserid(String fromuserid) {
		this.fromuserid = fromuserid;
	}

	public String getTouserid() {
		return touserid;
	}

	public void setTouserid(String touserid) {
		this.touserid = touserid;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getTouser() {
		return touser;
	}

	public void setTouser(String touser) {
		this.touser = touser;
	}

	public String getFromuser() {
		return fromuser;
	}

	public void setFromuser(String fromuser) {
		this.fromuser = fromuser;
	}

	public String getFromuserheadico() {
		return fromuserheadico;
	}

	public void setFromuserheadico(String fromuserheadico) {
		this.fromuserheadico = fromuserheadico;
	}

	public String getTouserheadico() {
		return touserheadico;
	}

	public void setTouserheadico(String touserheadico) {
		this.touserheadico = touserheadico;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.fromuserid);
		dest.writeString(this.touserid);
		dest.writeString(this.money);
		dest.writeString(this.time);
		dest.writeInt(this.type);
		dest.writeString(this.option);
		dest.writeString(this.touser);
		dest.writeString(this.fromuser);
		dest.writeString(this.fromuserheadico);
		dest.writeString(this.touserheadico);
		dest.writeString(this.note);
	}

	public TransferRecordData() {
	}

	protected TransferRecordData(Parcel in) {
		this.fromuserid = in.readString();
		this.touserid = in.readString();
		this.money = in.readString();
		this.time = in.readString();
		this.type = in.readInt();
		this.option = in.readString();
		this.touser = in.readString();
		this.fromuser = in.readString();
		this.fromuserheadico = in.readString();
		this.touserheadico = in.readString();
		this.note = in.readString();
	}

	public static final Creator<TransferRecordData> CREATOR = new Creator<TransferRecordData>() {
		@Override
		public TransferRecordData createFromParcel(Parcel source) {
			return new TransferRecordData(source);
		}

		@Override
		public TransferRecordData[] newArray(int size) {
			return new TransferRecordData[size];
		}
	};
}
