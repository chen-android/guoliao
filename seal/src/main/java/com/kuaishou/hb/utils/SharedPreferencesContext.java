package com.kuaishou.hb.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.kuaishou.hb.GGConst;


/**
 * Created by Bob on 2015/1/30.
 */
public class SharedPreferencesContext {

	private static SharedPreferencesContext mSharedPreferencesContext;
	public Context mContext;
	private SharedPreferences mPreferences;

	public static void init(Context context) {
		mSharedPreferencesContext = new SharedPreferencesContext(context);
	}

	public static SharedPreferencesContext getInstance() {

		if (mSharedPreferencesContext == null) {
			mSharedPreferencesContext = new SharedPreferencesContext();
		}
		return mSharedPreferencesContext;
	}

	private SharedPreferencesContext() {

	}

	private SharedPreferencesContext(Context context) {
		mContext = context;
		mSharedPreferencesContext = this;

		mPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
	}

	public SharedPreferences getSharedPreferences() {
		return mPreferences;
	}

	public String getToken() {
		return mPreferences.getString(GGConst.GUOGUO_LOGIN_TOKEN, "");
	}

	public String getUserId() {
		return mPreferences.getString(GGConst.GUOGUO_LOGIN_ID, "");
	}

	public String getName() {
		return mPreferences.getString(GGConst.GUOGUO_LOGIN_NAME, mPreferences.getString(GGConst.GUOGUO_LOGING_PHONE, ""));
	}

	public boolean isSetPayPwd() {
		return mPreferences.getBoolean(GGConst.GUOGUO_IS_SET_PAY_PWD, false);
	}

	public void setPayPwd() {
		mPreferences.edit().putBoolean(GGConst.GUOGUO_IS_SET_PAY_PWD, true).apply();
	}

}
