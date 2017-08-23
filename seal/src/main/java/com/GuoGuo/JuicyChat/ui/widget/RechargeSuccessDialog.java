package com.GuoGuo.JuicyChat.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.R;

/**
 * Created by cs on 2017/7/31.
 */

public class RechargeSuccessDialog extends Dialog {
	
	private TextView fromTv, toTv, moneyTv, dateTv, closeTv;
	
	private String from, to, money, date;
	
	private Activity mActivity;
	
	public RechargeSuccessDialog(@NonNull Activity context, String from, String to, String money, String date) {
		super(context, R.style.WinDialog);
		this.mActivity = context;
		this.from = from;
		this.to = to;
		this.money = money;
		this.date = date;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_recharge_success);
		initView();
		fromTv.setText(from);
		toTv.setText(to);
		moneyTv.setText(money);
		dateTv.setText(date);
	}
	
	private void initView() {
		fromTv = (TextView) findViewById(R.id.dialog_recharge_from_user_tv);
		toTv = (TextView) findViewById(R.id.dialog_recharge_to_user_tv);
		moneyTv = (TextView) findViewById(R.id.dialog_recharge_money_tv);
		dateTv = (TextView) findViewById(R.id.dialog_recharge_date_tv);
		closeTv = (TextView) findViewById(R.id.dialog_recharge_close_tv);
		closeTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				mActivity.finish();
			}
		});
	}
	
}
