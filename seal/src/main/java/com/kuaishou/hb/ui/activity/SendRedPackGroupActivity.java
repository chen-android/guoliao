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
import com.kuaishou.hb.SealUserInfoManager;
import com.kuaishou.hb.db.GroupMember;
import com.kuaishou.hb.model.GGRedPacketMessage;
import com.kuaishou.hb.model.RedPacketMessage;
import com.kuaishou.hb.server.network.http.HttpException;
import com.kuaishou.hb.server.response.GetMoneyResponse;
import com.kuaishou.hb.server.response.SendRedPacketResponse;
import com.kuaishou.hb.server.utils.NToast;
import com.kuaishou.hb.server.utils.StringUtils;
import com.kuaishou.hb.server.widget.LoadDialog;
import com.kuaishou.hb.ui.widget.PayPwdDialog;
import com.kuaishou.hb.utils.SharedPreferencesContext;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * 发红包(群)
 * Created by cs on 2017/5/12.
 */

public class SendRedPackGroupActivity extends BaseActivity {
	private static final int GET_MONEY = 938;
	private static final int SEND_RED_PACKET = 270;
	private EditText moneyEt;
	private EditText countEt;
	private EditText msgEt;
	private TextView moneyTv;
	private TextView gCountTv;
	private Button submitBt;
	private String targetId;
	private String userId;
	private String userName;
	private String userHead;

	private PayPwdDialog dialog;
	private String payPwd;
	private int memCount;
	private boolean isGroup;
	private boolean isChatRoom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_redpack_group);
		setTitle("发红包");
		initView();
		targetId = getIntent().getStringExtra("TargetId");
		isGroup = getIntent().getBooleanExtra("isGroup", false);
		isChatRoom = getIntent().getBooleanExtra("isChatRoom", false);
	}

	private void initView() {
		moneyEt = (EditText) findViewById(R.id.et_amount);
		countEt = (EditText) findViewById(R.id.et_count);
		msgEt = (EditText) findViewById(R.id.et_message);
		moneyTv = (TextView) findViewById(R.id.tv_amount);
		submitBt = (Button) findViewById(R.id.btn_putin);
		gCountTv = (TextView) findViewById(R.id.tv_group_menber_count);
		moneyEt.requestFocus();
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
				if (s.length() == 0) {
					moneyTv.setText("0");
				} else {
					moneyTv.setText(StringUtils.getFormatMoney(s.toString() + "00"));
				}
				checkCanSubmitClick();
			}
		});
		countEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				checkCanSubmitClick();
			}
		});
		SealUserInfoManager.getInstance().getGroupMembers(targetId, new SealUserInfoManager.ResultCallback<List<GroupMember>>() {
			@Override
			public void onSuccess(List<GroupMember> groupMembers) {
				LoadDialog.dismiss(mContext);
				if (groupMembers != null && groupMembers.size() > 0) {
					gCountTv.setText("本群一共有" + groupMembers.size() + "人");
				}
			}

			@Override
			public void onError(String errString) {
				LoadDialog.dismiss(mContext);
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
				if (Long.valueOf(moneyEt.getText().toString()) * 100 < Long.valueOf(countEt.getText().toString())) {
					NToast.shortToast(mContext, "每个红包至少一个快豆哦~");
					return;
				}
				LoadDialog.show(mContext);
				dialog.setMoney(Long.valueOf(moneyEt.getText().toString()) * 100 + "");
				request(GET_MONEY, true);
			}
		});
	}

	private void checkCanSubmitClick() {
		String money = moneyEt.getText().toString();
		String count = countEt.getText().toString();
		if (!TextUtils.isEmpty(money) && Long.valueOf(money) > 0 && !TextUtils.isEmpty(count) && Long.valueOf(count) > 0) {
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
				int type = 0;
				if (isGroup) {
					type = 2;
				}
				if (isChatRoom) {
					type = 3;
				}
				return action.sendRedPacket(Integer.valueOf(targetId), Long.valueOf(moneyEt.getText().toString()) * 100,
						payPwd, TextUtils.isEmpty(msgEt.getText().toString()) ? "恭喜发财，大吉大利" : msgEt.getText().toString(),
						1, type, Integer.valueOf(countEt.getText().toString()));

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
						dialog.setRemain(response.getData().getMoney() + "");
						dialog.show();
					}
					break;
				case SEND_RED_PACKET:
					SendRedPacketResponse redPacketResponse = (SendRedPacketResponse) result;
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					if (redPacketResponse.getCode() == 200) {
						RedPacketMessage data = redPacketResponse.getData();
						Conversation.ConversationType type = null;
						if (isGroup) {
							type = Conversation.ConversationType.GROUP;
						} else if (isChatRoom) {
							type = Conversation.ConversationType.CHATROOM;
						}
						RongIM.getInstance().sendMessage(Message.obtain(targetId, type,
								GGRedPacketMessage.Companion.obtain(data.getId() + "", data.getTomemberid(), data.getFromuserid(),
										data.getType(), data.getMoney(), GGRedPacketMessage.Companion.getCONTENT_PREFIX() + data.getNote(), data.getSort(), data.getCount(),
										data.getState(), data.getCreatetime())), GGRedPacketMessage.Companion.getCONTENT_PREFIX() + data.getNote(), null,
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
										NToast.shortToast(mContext, message.getExtra());
									}
								});
					} else if (redPacketResponse.getCode() == 66006) {
						NToast.shortToast(mContext, "余额不足");
					} else if (redPacketResponse.getCode() == 66002) {
						NToast.shortToast(mContext, "支付密码不正确");
					} else if (redPacketResponse.getCode() == 66005) {
						NToast.shortToast(mContext, "金额低于群最低限额");
					}
					break;
			}
		}
	}
}
