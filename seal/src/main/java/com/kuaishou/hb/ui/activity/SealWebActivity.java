package com.kuaishou.hb.ui.activity;

import android.os.Bundle;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.rong.imkit.tools.RongWebviewActivity;

/**
 * Created by AMing on 16/9/6.
 * Company RongCloud
 */
public class SealWebActivity extends RongWebviewActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onPause() {
		try {
			Field field = this.getClass().getSuperclass().getDeclaredField("mWebView");
			field.setAccessible(true);
			Method method = field.get(this).getClass().getMethod("onPause");
			method.invoke(field.get(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPause();
	}
}
