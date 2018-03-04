package com.kuaishou.hb.model;

/**
 * Created by cs on 2017/5/13.
 */

public class RedPacketMessage {
	private int id;
	private int tomemberid;
	private int fromuserid;
	private int type;
	private long money;
	private String note;
	private int sort;
	private int count;
	private int state;
	private String createtime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTomemberid() {
		return tomemberid;
	}

	public void setTomemberid(int tomemberid) {
		this.tomemberid = tomemberid;
	}

	public int getFromuserid() {
		return fromuserid;
	}

	public void setFromuserid(int fromuserid) {
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
}
