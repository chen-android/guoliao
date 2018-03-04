package com.kuaishou.hb.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuaishou.hb.R;
import com.kuaishou.hb.server.utils.AMUtils;
import com.kuaishou.hb.server.utils.StringUtils;

/**
 * Created by cs on 2017/5/13.
 */

public class PayPwdDialog extends Dialog {
	private TextView moneyTv;
	private TextView remainTv;
	private EditText pwdEt;
	private TextView closeTv;
	private LinearLayout pwdLl;
	private TextView[] pwdList = new TextView[6];
	private StringBuffer stringBuffer;
	private InputCompleteListener mInputCompleteListener;
	private Context mContext;

	private String money;
	private String remainMoney;

	public PayPwdDialog(@NonNull Context context) {
		super(context, R.style.WinDialog);
		setContentView(R.layout.dialog_red_packet_pay);
		mContext = context;
		stringBuffer = new StringBuffer();
		initView();
	}

	private void initView() {
		moneyTv = (TextView) findViewById(R.id.dialog_red_packet_pay_money_tv);
		remainTv = (TextView) findViewById(R.id.dialog_red_packet_pay_remain_money_tv);
		pwdEt = (EditText) findViewById(R.id.dialog_red_packet_pay_et);
		closeTv = (TextView) findViewById(R.id.dialog_red_packet_close_tv);
		pwdLl = (LinearLayout) findViewById(R.id.dialog_red_packet_pwd_ll);
		for (int i = 0; i < 6; i++) {
			pwdList[i] = (TextView) pwdLl.getChildAt(i);
		}

		pwdEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (!editable.toString().equals("")) {
					if (stringBuffer.length() > 5) {
						pwdEt.setText("");
						return;
					} else {
						//将文字添加到StringBuffer中
						stringBuffer.append(editable);
						pwdEt.setText("");//添加后将EditText置空  造成没有文字输入的错局
						if (stringBuffer.length() == 6) {
							//文字长度位6   则调用完成输入的监听
							if (mInputCompleteListener != null) {
								AMUtils.onInactive(mContext, pwdEt);
								mInputCompleteListener.onInputComplete(stringBuffer.toString());
							}
						}
					}
					refreshPwdView();
				}
			}
		});

		pwdEt.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
					return keyDelete();
				}
				return false;
			}
		});
		closeTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PayPwdDialog.this.dismiss();
			}
		});
	}

	public interface InputCompleteListener {
		void onInputComplete(String pwd);
	}

	public void setInputCompleteListener(InputCompleteListener inputCompleteListener) {
		mInputCompleteListener = inputCompleteListener;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public void setRemain(String remain) {
		this.remainMoney = remain;
	}

	private void refreshPwdView() {
		for (int i = 0; i < 6; i++) {
			if (i < stringBuffer.length()) {
				pwdList[i].setText("●");
			} else {
				pwdList[i].setText("");
			}
		}
	}

	@Override
	public void show() {
		super.show();
		moneyTv.setText(StringUtils.getFormatMoney(money));
		remainTv.setText(StringUtils.getFormatMoney(remainMoney));
		pwdEt.setText("");
		stringBuffer.delete(0, stringBuffer.length());
		refreshPwdView();
		pwdEt.post(new Runnable() {
			@Override
			public void run() {
				AMUtils.onActive(mContext, pwdEt);
			}
		});

	}

	private boolean keyDelete() {
		if (stringBuffer.length() > 0) {
			stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());
			refreshPwdView();
			return true;
		}
		return false;
	}
}
