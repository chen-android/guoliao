package com.GuoGuo.JuicyChat.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.GetUserInfoByTokenResponse;
import com.GuoGuo.JuicyChat.server.widget.LoadDialog;
import com.GuoGuo.JuicyChat.utils.SharedPreferencesContext;

/**
 * 冻结金额
 * Created by cs on 2017/5/31.
 */

public class LockMoneyActivity extends BaseActivity {
	private TextView moneyTv;
	private Button detailBt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_money);
		setTitle("冻结果币");
		moneyTv = (TextView) findViewById(R.id.lock_money_tv);
		detailBt = (Button) findViewById(R.id.lock_money_bt);
		LoadDialog.show(mContext);
		request(1);
		detailBt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LockMoneyActivity.this, LockMoneyDetailActivity.class));
			}
		});
	}
	
	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		return action.getUserInfoByToken(SharedPreferencesContext.getInstance().getToken());
	}
	
	@Override
	public void onSuccess(int requestCode, Object result) {
		LoadDialog.dismiss(mContext);
		GetUserInfoByTokenResponse response = (GetUserInfoByTokenResponse) result;
		if (response.getCode() == 200) {
			moneyTv.setText(response.getData().getLockmoney());
		}
	}
	
	@Override
	public void onFailure(int requestCode, int state, Object result) {
		LoadDialog.dismiss(mContext);
	}
}
