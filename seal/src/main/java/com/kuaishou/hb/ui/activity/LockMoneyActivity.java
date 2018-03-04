package com.kuaishou.hb.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kuaishou.hb.R;
import com.kuaishou.hb.server.network.http.HttpException;
import com.kuaishou.hb.server.response.GetUserInfoByTokenResponse;
import com.kuaishou.hb.server.utils.StringUtils;
import com.kuaishou.hb.server.widget.LoadDialog;
import com.kuaishou.hb.utils.SharedPreferencesContext;

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
		setTitle("冻结快豆");
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
			moneyTv.setText(StringUtils.getFormatMoney(response.getData().getLockmoney()));
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		LoadDialog.dismiss(mContext);
	}
}
