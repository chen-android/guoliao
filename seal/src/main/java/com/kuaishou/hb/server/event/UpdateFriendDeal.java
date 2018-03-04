package com.kuaishou.hb.server.event;

/**
 * Created by chenshuai12619 on 2017-11-15.
 */

public class UpdateFriendDeal {
	private UpdateAction action;
	private int num;

	public UpdateFriendDeal(UpdateAction action) {
		this.action = action;
	}

	public UpdateFriendDeal(UpdateAction action, int num) {
		this.action = action;
		this.num = num;
	}

	public UpdateAction getAction() {
		return action;
	}

	public void setAction(UpdateAction action) {
		this.action = action;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public enum UpdateAction {
		ADD, REDUCE, NUM
	}
}
