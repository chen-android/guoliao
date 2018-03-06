package com.kuaishou.hb.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.TimeUtils;
import com.kuaishou.hb.GGConst;
import com.kuaishou.hb.R;
import com.kuaishou.hb.SealUserInfoManager;
import com.kuaishou.hb.db.Friend;
import com.kuaishou.hb.server.SealAction;
import com.kuaishou.hb.server.network.http.HttpException;
import com.kuaishou.hb.server.response.BaseResponse;
import com.kuaishou.hb.server.response.GetMoneyResponse;
import com.kuaishou.hb.server.utils.NToast;
import com.kuaishou.hb.server.utils.StringUtils;
import com.kuaishou.hb.server.widget.LoadDialog;
import com.kuaishou.hb.server.widget.SelectableRoundedImageView;
import com.kuaishou.hb.ui.widget.PayPwdDialog;
import com.kuaishou.hb.ui.widget.RechargeSuccessDialog;
import com.kuaishou.hb.utils.SharedPreferencesContext;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class RechargeActivity extends BaseActivity {

	private static final int REQUEST_RECHARGE = 66;
	private static final int REQUEST_REMAIN_MONEY = 234;

	private Friend mFriend;
	private SelectableRoundedImageView headIv;
	private TextView nameTv;
	private EditText moneyEt;
	private TextView moneyTv;
	private EditText noteEt;
	private Button submitBt;
	private PayPwdDialog dialog;
	private String payPwd;

	protected SealAction action;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge);
		setTitle("转账");
		mFriend = getIntent().getParcelableExtra("friend");
		if (mFriend == null) {
			Toast.makeText(this, "用户信息有误", Toast.LENGTH_SHORT).show();
			finish();
		}
		action = new SealAction(this);
		initView();
		Picasso.with(this).load(Uri.parse(mFriend.getHeadico())).into(headIv);
		nameTv.setText(SealUserInfoManager.getInstance().getDiaplayName(mFriend));
		checkCanSubmitClick();
		dialog = new PayPwdDialog(this);
		dialog.setInputCompleteListener(new PayPwdDialog.InputCompleteListener() {
			@Override
			public void onInputComplete(String pwd) {
				payPwd = pwd;
				dialog.dismiss();
				LoadDialog.show(RechargeActivity.this);
				request(REQUEST_RECHARGE, true);
			}
		});
	}

	private void initView() {
		headIv = (SelectableRoundedImageView) findViewById(R.id.recharge_head_iv);
		nameTv = (TextView) findViewById(R.id.recharge_username_iv);
		moneyEt = (EditText) findViewById(R.id.recharge_money_et);
		moneyTv = (TextView) findViewById(R.id.tv_amount);
		noteEt = (EditText) findViewById(R.id.recharge_note_et);
		submitBt = (Button) findViewById(R.id.recharge_bt);
		moneyEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				moneyTv.setText(s.toString());
				checkCanSubmitClick();
			}
		});
		submitBt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!SharedPreferencesContext.getInstance().isSetPayPwd()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
					builder.setTitle("提示").setMessage("您还未设置支付密码").setPositiveButton("去设置", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent in = new Intent(mContext, PayPwdEditActivity.class);
							in.putExtra("isSet", false);
							startActivity(in);
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
					return;
				}
				LoadDialog.show(RechargeActivity.this);
				request(REQUEST_REMAIN_MONEY);
			}
		});
	}

	private void checkCanSubmitClick() {
		String money = moneyEt.getText().toString();
		if (!TextUtils.isEmpty(money) && Double.valueOf(money) > 0) {
			submitBt.setEnabled(true);
		} else {
			submitBt.setEnabled(false);
		}
	}

	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		if (requestCode == REQUEST_RECHARGE) {
			return action.requestRecharge(Double.valueOf(moneyEt.getText().toString()), Long.valueOf(mFriend.getFriendid()), payPwd, noteEt.getText().toString());
		} else if (requestCode == REQUEST_REMAIN_MONEY) {
			return action.getRemainMoney();
		}
		return super.doInBackground(requestCode, id);
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		switch (requestCode) {
			case REQUEST_REMAIN_MONEY:
				LoadDialog.dismiss(mContext);
				GetMoneyResponse response = (GetMoneyResponse) result;
				if (response.getCode() == 200) {
					dialog.setMoney(Double.valueOf(moneyEt.getText().toString()));
					dialog.setRemain(response.getData().getMoney());
					dialog.show();
				} else {
					NToast.shortToast(mContext, "服务器开小差了");
				}
				break;
			case REQUEST_RECHARGE:
				LoadDialog.dismiss(mContext);
				BaseResponse response1 = (BaseResponse) result;
				if (response1.getCode() == 200) {
					SharedPreferences config = getSharedPreferences("config", MODE_PRIVATE);
					String from = config.getString(GGConst.GUOGUO_LOGIN_NAME, "") + "(ID:" + config.getString(GGConst.GUOGUO_LOGIN_ID, "") + ")";
					String to = SealUserInfoManager.getInstance().getDiaplayName(mFriend) + "(ID:" + mFriend.getFriendid() + ")";
					String money = StringUtils.getFormatMoney(Double.valueOf(moneyEt.getText().toString()));
					String date = TimeUtils.date2String(new Date());
					new RechargeSuccessDialog(RechargeActivity.this, from, to, money, date).show();
				} else if (response1.getCode() == 68001) {
					NToast.shortToast(mContext, "余额不足");
				} else if (response1.getCode() == 66002) {
					NToast.shortToast(mContext, "支付密码错误");
				} else {
					NToast.shortToast(mContext, "服务器出错");
				}
				break;
			default:
				break;
		}
	}
}
