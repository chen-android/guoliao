package com.kuaishou.hb.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kuaishou.hb.R;

/**
 * Created by cs on 2017/5/12.
 */

public class PayPwdSettingActivity extends BaseActivity implements View.OnClickListener {
	private TextView editTv;
	private TextView forgetTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_pwd_setting);
		initView();
		setTitle("安全设置");
	}

	private void initView() {
		editTv = (TextView) findViewById(R.id.pay_pwd_edit_tv);
		forgetTv = (TextView) findViewById(R.id.pay_pwd_forget_tv);
		editTv.setOnClickListener(this);
		forgetTv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == editTv) {
			Intent intent = new Intent(this, PayPwdEditActivity.class);
			intent.putExtra("isSet", true);
			startActivity(intent);
		} else if (v == forgetTv) {
			startActivity(new Intent(this, ForgetPayPasswordActivity.class));
		}
	}
}
