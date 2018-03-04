package com.kuaishou.hb.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kuaishou.hb.R;
import com.kuaishou.hb.server.network.http.HttpException;
import com.kuaishou.hb.server.response.BaseResponse;
import com.kuaishou.hb.server.utils.NToast;
import com.kuaishou.hb.server.widget.LoadDialog;
import com.kuaishou.hb.utils.SharedPreferencesContext;

import static com.kuaishou.hb.R.id.pay_pwd_edit_confirm_et;

/**
 * Created by cs on 2017/5/12.
 */

public class PayPwdEditActivity extends BaseActivity {
	private static final int SET_NEW = 6;
	private static final int EDIT_OLD = 7;
	private EditText oldEt;
	private EditText newEt;
	private EditText confirmEt;
	private Button bt;
	private LinearLayout oldLl;
	private boolean isSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_pwd_edit);
		isSet = getIntent().getBooleanExtra("isSet", false);
		initView();
		setTitle(isSet ? "修改支付密码" : "设置支付密码");
	}

	private void initView() {
		oldLl = (LinearLayout) findViewById(R.id.pay_pwd_edit_old_ll);
		oldLl.setVisibility(isSet ? View.VISIBLE : View.GONE);
		oldEt = (EditText) findViewById(R.id.pay_pwd_edit_old_et);
		newEt = (EditText) findViewById(R.id.pay_pwd_edit_new_et);
		confirmEt = (EditText) findViewById(pay_pwd_edit_confirm_et);
		bt = (Button) findViewById(R.id.pay_pwd_edit_bt);
		bt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!newEt.getText().toString().equals(confirmEt.getText().toString())) {
					NToast.shortToast(mContext, "两次输入密码不一致");
					return;
				}
				if (newEt.getText().toString().length() != 6) {
					NToast.shortToast(mContext, "密码不等于6位");
					return;
				}
				LoadDialog.show(mContext);
				if (!isSet) {
					request(SET_NEW);
				} else {
					request(EDIT_OLD);
				}
			}
		});
	}

	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		switch (requestCode) {
			case SET_NEW:
				return action.setNewPayPwd(newEt.getText().toString());
			case EDIT_OLD:
				return action.editPayPwd(oldEt.getText().toString(), newEt.getText().toString());

		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (result != null) {
			switch (requestCode) {
				case SET_NEW:
					LoadDialog.dismiss(mContext);
					BaseResponse response1 = (BaseResponse) result;
					if (response1.getCode() == 200) {
						NToast.shortToast(mContext, "密码设置成功");
						SharedPreferencesContext.getInstance().setPayPwd();
						finish();
					}
					break;
				case EDIT_OLD:
					LoadDialog.dismiss(mContext);
					BaseResponse response2 = (BaseResponse) result;
					if (response2.getCode() == 200) {
						NToast.shortToast(mContext, "密码修改成功");
						finish();
					}
					break;
			}
		}
	}
}
