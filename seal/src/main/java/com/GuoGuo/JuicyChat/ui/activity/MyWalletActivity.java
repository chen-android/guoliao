package com.GuoGuo.JuicyChat.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.GetMoneyResponse;
import com.GuoGuo.JuicyChat.server.utils.StringUtils;
import com.GuoGuo.JuicyChat.utils.SharedPreferencesContext;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

/**
 * Created by cs on 2017/5/11.
 */

public class MyWalletActivity extends BaseActivity implements View.OnClickListener {
	private static final int REQUEST_BALANCE = 676;
	private TextView moneyTv;
	private TextView chineseMoneyTv;
	private TextView couponTv;
	private TextView transferRecordTv;
	private TextView lockTv;
	private TextView settingTv;
	private SmartRefreshLayout srl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_wallet);
		setTitle("我的果币");
		initView();
		srl.autoRefresh();
	}
	
	private void initView() {
		moneyTv = (TextView) findViewById(R.id.my_wallet_money_tv);
		couponTv = (TextView) findViewById(R.id.my_wallet_coupon_tv);
		lockTv = (TextView) findViewById(R.id.my_wallet_lock_money_tv);
		transferRecordTv = (TextView) findViewById(R.id.my_wallet_transfer_record_tv);
		settingTv = (TextView) findViewById(R.id.my_wallet_setting_tv);
		chineseMoneyTv = (TextView) findViewById(R.id.my_wallet_money_chinese_tv);
		srl = (SmartRefreshLayout) findViewById(R.id.my_wallet_srl);
		srl.setRefreshHeader(new ClassicsHeader(this));
		srl.setRefreshFooter(new ClassicsFooter(this));
		srl.setEnableLoadmore(false);
		srl.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(RefreshLayout refreshlayout) {
				request(REQUEST_BALANCE);
			}
		});
		couponTv.setOnClickListener(this);
		transferRecordTv.setOnClickListener(this);
		lockTv.setOnClickListener(this);
		settingTv.setOnClickListener(this);
	}
	
	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		if (requestCode == REQUEST_BALANCE) {
			return action.getRemainMoney();
		}
		return super.doInBackground(requestCode, id);
	}
	
	@Override
	public void onSuccess(int requestCode, Object result) {
		if (result != null) {
			if (requestCode == REQUEST_BALANCE) {
				GetMoneyResponse response = (GetMoneyResponse) result;
				srl.finishRefresh(500);
				if (response.getCode() == 200) {
					String money = response.getData().getMoney() + "";
					moneyTv.setText(StringUtils.getFormatMoney(money));
					chineseMoneyTv.setText(StringUtils.getChineseMoney(money));
				}
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		if (v == couponTv) {
			startActivity(new Intent(this, MyRedPacketActivity.class));
		} else if (v == lockTv) {
			startActivity(new Intent(this, LockMoneyActivity.class));
		} else if (v == settingTv) {
			if (SharedPreferencesContext.getInstance().isSetPayPwd()) {
				startActivity(new Intent(this, PayPwdSettingActivity.class));
			} else {
				Intent in = new Intent(this, PayPwdEditActivity.class);
				in.putExtra("isSet", false);
				startActivity(in);
			}
		} else if (v == transferRecordTv) {
			startActivity(new Intent(this, TransferRecordActivity.class));
		}
	}
}
