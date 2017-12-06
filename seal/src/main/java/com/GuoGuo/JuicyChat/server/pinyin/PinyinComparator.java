package com.GuoGuo.JuicyChat.server.pinyin;


import android.text.TextUtils;

import com.GuoGuo.JuicyChat.db.Friend;

import java.util.Comparator;


/**
 * @author
 */
public class PinyinComparator implements Comparator<Friend> {
	
	
	public static PinyinComparator instance = null;
	
	public static PinyinComparator getInstance() {
		if (instance == null) {
			instance = new PinyinComparator();
		}
		return instance;
	}
    
    @Override
    public int compare(Friend o1, Friend o2) {
        
        String letter1 = o1.getLetter();
        String letter2 = o2.getLetter();
        if (!TextUtils.isEmpty(letter1) && !TextUtils.isEmpty(letter2)) {
            if (letter1.startsWith("#") && !letter2.startsWith("#")) {
                return 1;
            } else if (!letter1.startsWith("#") && letter2.startsWith("#")) {
                return -1;
            }
            return letter1.compareTo(letter2);
        }
        return 0;
    }
    
    public static String getLetter(String string) {
		if (!TextUtils.isEmpty(string)) {
			String convert = CharacterParser.getInstance().convert(string.substring(0, 1));
			if (TextUtils.isEmpty(convert)) {//处理特殊字符
                return "#" + string;
            }
            String ch = convert.toUpperCase().substring(0, 1);
			if (ch.matches("[A-Z]")) {
				return ch;
			} else {
                return "#" + ch;
            }
        }
		return null;
	}
	
}
