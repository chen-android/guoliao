package com.kuaishou.hb.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kuaishou.hb.R;
import com.kuaishou.hb.model.GGRedPacketMessage;
import com.kuaishou.hb.model.RedPacketMessage;
import com.kuaishou.hb.server.network.http.HttpException;
import com.kuaishou.hb.server.response.GetMoneyResponse;
import com.kuaishou.hb.server.response.SendRedPacketResponse;
import com.kuaishou.hb.server.utils.NToast;
import com.kuaishou.hb.server.widget.LoadDialog;
import com.kuaishou.hb.ui.widget.PayPwdDialog;
import com.kuaishou.hb.utils.SharedPreferencesContext;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * 发红包(个人)
 * Created by cs on 2017/5/12.
 */

public class SendRedPackPersonalActivity extends BaseActivity {
	private static final int GET_MONEY = 938;
	private static final int SEND_RED_PACKET = 270;
	private EditText moneyEt;
	private EditText msgEt;
	private TextView moneyTv;
	private Button submitBt;
	private String targetId;
	private String userId;
	private String userName;
	private String userHead;
	
	private PayPwdDialog dialog;
	private String payPwd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_redpack_personal);
		setTitle("发红包");
		initView();
		targetId = getIntent().getStringExtra("TargetId");
//		userId = getIntent().getStringExtra("user_id");
//		userName = getIntent().getStringExtra("user_name");
//		userHead = getIntent().getStringExtra("user_icon");
	}
	
	private void initView() {
		moneyEt = (EditText) findViewById(R.id.et_amount);
		msgEt = (EditText) findViewById(R.id.et_message);
		moneyTv = (TextView) findViewById(R.id.tv_amount);
		submitBt = (Button) findViewById(R.id.btn_putin);
		checkCanSubmitClick();
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
		dialog = new PayPwdDialog(this);
		dialog.setInputCompleteListener(new PayPwdDialog.InputCompleteListener() {
			@Override
			public void onInputComplete(String pwd) {
				payPwd = pwd;
				request(SEND_RED_PACKET, true);
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
				LoadDialog.show(mContext);
				dialog.setMoney(Double.valueOf(moneyEt.getText().toString()));
				request(GET_MONEY, true);
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
		switch (requestCode) {
			case GET_MONEY:
				return action.getRemainMoney();
			case SEND_RED_PACKET:
				return action.sendRedPacket(Integer.valueOf(targetId), Double.valueOf(moneyEt.getText().toString()), payPwd, TextUtils.isEmpty(msgEt.getText().toString()) ? "恭喜发财，大吉大利" : msgEt.getText().toString(), 1, 1, 1);
			default:
				break;
		}
		return null;
	}
	
	@Override
	public void onSuccess(int requestCode, Object result) {
		if (result != null) {
			switch (requestCode) {
				case GET_MONEY:
					LoadDialog.dismiss(mContext);
					GetMoneyResponse response = (GetMoneyResponse) result;
					if (response.getCode() == 200) {
						dialog.setRemain(response.getData().getMoney());
						dialog.show();
					}
					break;
				case SEND_RED_PACKET:
					SendRedPacketResponse redPacketResponse = (SendRedPacketResponse) result;
					if (redPacketResponse.getCode() == 200) {
						if (dialog != null && dialog.isShowing()) {
							dialog.dismiss();
						}
						RedPacketMessage data = redPacketResponse.getData();
						RongIM.getInstance().sendMessage(
								Message.obtain(targetId, Conversation.ConversationType.PRIVATE,
										GGRedPacketMessage.Companion.obtain(data.getId() + "", data.getTomemberid(), data.getFromuserid(),
												data.getType(), data.getMoney(), GGRedPacketMessage.Companion.getCONTENT_PREFIX() + data.getNote(), data.getSort(), data.getCount(),
												data.getState(), data.getCreatetime())),
								GGRedPacketMessage.Companion.getCONTENT_PREFIX() + data.getNote(),
								null,
								new IRongCallback.ISendMessageCallback() {
									@Override
									public void onAttached(Message message) {
									
									}
									
									@Override
									public void onSuccess(Message message) {
										finish();
									}
									
									@Override
									public void onError(Message message, RongIMClient.ErrorCode errorCode) {
									
									}
								});
					} else if (redPacketResponse.getCode() == 66006) {
						NToast.shortToast(mContext, "余额不足");
					} else if (redPacketResponse.getCode() == 66002) {
						NToast.shortToast(mContext, "支付密码不正确");
					}
					break;
				default:
					break;
			}
		}
	}
}
