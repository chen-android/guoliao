package com.kuaishou.hb.server.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cs on 2017/5/18.
 */

public class StringUtils {
	public static Date sTimeToDate(String dateTime) {
		String resultTime = dateTime.replace("T", " ").substring(0, 19);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date updateAtDate = null;
		try {
			updateAtDate = simpleDateFormat.parse(resultTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return updateAtDate;
	}

	public static String sTimeToString(String dateTime) {
		return dateTime.replace("T", " ").substring(0, 19);
	}

	public static String sTimeToTimeStr(String dateTime) {
		return dateTime.substring(11, 16);
	}

	public static String getFormatMoney(String money) {
		StringBuilder sb = new StringBuilder(money);
		if (sb.length() > 2) {
			for (int i = sb.length() - 2; i > 0; i -= 4) {
				sb.insert(i, ",");
			}
		}
		return sb.toString();
	}

	public static String getChineseMoney(String money) {
		StringBuilder sb = new StringBuilder(money);
		int length = sb.length();
		if (length > 4) {
			sb.insert(length - 4, "万");
		}
		if (length > 8) {
			sb.insert(length - 8, "亿");
		}
		return sb.toString();
	}

	public static String redPacketState2String(int state) {
		switch (state) {
			case 1:
				return "可领取";
			case 2:
				return "已领取";
			case 3:
				return "已过期";
			default:
				return "";
		}
	}
}
