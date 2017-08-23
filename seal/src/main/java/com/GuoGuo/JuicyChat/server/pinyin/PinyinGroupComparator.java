package com.GuoGuo.JuicyChat.server.pinyin;


import android.text.TextUtils;

import com.GuoGuo.JuicyChat.db.Groups;

import java.util.Comparator;


/**
 * @author
 */
public class PinyinGroupComparator implements Comparator<Groups> {
	
	
	public static PinyinGroupComparator instance = null;
	
	public static PinyinGroupComparator getInstance() {
		if (instance == null) {
			instance = new PinyinGroupComparator();
		}
		return instance;
	}
	
	public int compare(Groups o1, Groups o2) {
		
		String letter1 = getLetter(o1.getGroupname());
		String letter2 = getLetter(o2.getGroupname());
		if (!TextUtils.isEmpty(letter1) && !TextUtils.isEmpty(letter2)) {
			if (letter1.equals("@")
					|| letter2.equals("#")) {
				return -1;
			} else if (letter1.equals("#")
					|| letter2.equals("@")) {
				return 1;
			} else {
				return letter1.compareTo(letter2);
			}
		}
		return 0;
	}
	
	public static String getLetter(String string) {
		if (!TextUtils.isEmpty(string)) {
			String convert = CharacterParser.getInstance().convert(string.substring(0, 1));
			String ch = convert.toUpperCase().substring(0, 1);
			if (ch.matches("[A-Z]")) {
				return ch;
			} else {
				return "#";
			}
		}
		return null;
	}
	
}
