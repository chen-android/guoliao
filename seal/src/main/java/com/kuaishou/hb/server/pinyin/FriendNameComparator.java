package com.kuaishou.hb.server.pinyin;

import android.text.TextUtils;

import com.kuaishou.hb.db.Friend;

import java.util.Comparator;

/**
 * Created by tiankui on 16/9/1.
 */
public class FriendNameComparator implements Comparator<Friend> {


	private static FriendNameComparator singleInstance = null;

	private FriendNameComparator() {
	}

	public static FriendNameComparator getInstance() {
		if (singleInstance == null) {
			synchronized (FriendNameComparator.class) {
				if (singleInstance == null) {
					singleInstance = new FriendNameComparator();
				}
			}
		}
		return singleInstance;
	}

	public int compare(Friend o1, Friend o2) {
		String nameOne;
		String nameTwo;
		if (!TextUtils.isEmpty(o1.getUsername())) {
			nameOne = o1.getUsername();
		} else {
			nameOne = o1.getUsername();
		}

		if (!TextUtils.isEmpty(o2.getUsername())) {
			nameTwo = o2.getUsername();
		} else {
			nameTwo = o2.getUsername();
		}
		return nameOne.compareToIgnoreCase(nameTwo);
	}
}
