package com.GuoGuo.JuicyChat.ui.activity;

import android.os.Bundle;

import com.GuoGuo.JuicyChat.utils.SealJavaScriptInterface;

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
		try {
			Field field = this.getClass().getSuperclass().getDeclaredField("mWebView");
			field.setAccessible(true);
			Method method = field.get(this).getClass().getMethod("addJavascriptInterface", Object.class, String.class);
			method.invoke(field.get(this), new SealJavaScriptInterface(), "SharePage");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
