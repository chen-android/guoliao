package com.GuoGuo.JuicyChat.ui.activity;

import android.os.Bundle;
import android.util.Log;

import com.GuoGuo.JuicyChat.R;

public class PublicServiceInfoActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("", "---------PublicServiceInfoActivity------------");
		setContentView(R.layout.pub_account_info);
		setTitle("公众号信息");
		
	}
	
}
