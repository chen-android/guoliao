package com.GuoGuo.JuicyChat.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.GuoGuo.JuicyChat.GGConst;
import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.BaseResponse;
import com.GuoGuo.JuicyChat.server.response.SendCodeResponse;
import com.GuoGuo.JuicyChat.server.response.VerifyCodeResponse;
import com.GuoGuo.JuicyChat.server.utils.AMUtils;
import com.GuoGuo.JuicyChat.server.utils.NToast;
import com.GuoGuo.JuicyChat.server.utils.downtime.DownTimer;
import com.GuoGuo.JuicyChat.server.utils.downtime.DownTimerListener;
import com.GuoGuo.JuicyChat.server.widget.ClearWriteEditText;
import com.GuoGuo.JuicyChat.server.widget.LoadDialog;
import com.GuoGuo.JuicyChat.utils.SharedPreferencesContext;

public class ForgetPayPasswordActivity extends BaseActivity implements View.OnClickListener, DownTimerListener {
	
	private static final int CHECK_PHONE = 31;
	private static final int SEND_CODE = 32;
	private static final int CHANGE_PASSWORD = 33;
	private static final int VERIFY_CODE = 34;
	private static final int CHANGE_PASSWORD_BACK = 1002;
	private ClearWriteEditText mPhone, mCode, mPassword1, mPassword2;
	private Button mGetCode, mOK;
	private String phone, mCodeToken;
	private boolean available;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget);
		setTitle("忘记支付密码");
		initView();
		
	}
	
	private void initView() {
		mPhone = (ClearWriteEditText) findViewById(R.id.forget_phone);
		mCode = (ClearWriteEditText) findViewById(R.id.forget_code);
		mPassword1 = (ClearWriteEditText) findViewById(R.id.forget_password);
		mPassword2 = (ClearWriteEditText) findViewById(R.id.forget_password1);
		mGetCode = (Button) findViewById(R.id.forget_getcode);
		mOK = (Button) findViewById(R.id.forget_button);
		mGetCode.setOnClickListener(this);
		mOK.setOnClickListener(this);
		String string = SharedPreferencesContext.getInstance().getSharedPreferences().getString(GGConst.GUOGUO_LOGING_PHONE, "");
		if (TextUtils.isEmpty(string)) {
			mPhone.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					
				}
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					phone = mPhone.getText().toString().trim();
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					
				}
			});
			mPhone.setEnabled(true);
		} else {
			mPhone.setEnabled(false);
			phone = string;
			mPhone.setText(phone);
		}
		
		
		mCode.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 5) {
					AMUtils.onInactive(mContext, mCode);
					mOK.setClickable(true);
					mOK.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
				} else {
					mOK.setClickable(false);
					mOK.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
				}
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		switch (requestCode) {
			case SEND_CODE:
				return action.sendCode(phone);
			case CHANGE_PASSWORD:
				return action.restPayPassword(mPassword1.getText().toString(), mCode.getText().toString());
			case VERIFY_CODE:
				return action.verifyCode("86", phone, mCode.getText().toString());
		}
		return super.doInBackground(requestCode, id);
	}
	
	@Override
	public void onSuccess(int requestCode, Object result) {
		if (result != null) {
			switch (requestCode) {
				case SEND_CODE:
					SendCodeResponse scrres = (SendCodeResponse) result;
					if (scrres.getCode() == 200) {
						NToast.shortToast(mContext, R.string.messge_send);
					}
					break;
				case VERIFY_CODE:
					VerifyCodeResponse vcres = (VerifyCodeResponse) result;
					switch (vcres.getCode()) {
						case 200:
							mCodeToken = vcres.getResult().getVerification_token();
							if (!TextUtils.isEmpty(mCodeToken)) {
								request(CHANGE_PASSWORD);
							} else {
								NToast.shortToast(mContext, "code token is null");
								LoadDialog.dismiss(mContext);
							}
							break;
						case 1000:
							//验证码错误
							NToast.shortToast(mContext, R.string.verification_code_error);
							LoadDialog.dismiss(mContext);
							break;
						case 2000:
							//验证码过期
							NToast.shortToast(mContext, R.string.captcha_overdue);
							LoadDialog.dismiss(mContext);
							break;
					}
					break;
				
				case CHANGE_PASSWORD:
					BaseResponse response1 = (BaseResponse) result;
					if (response1.getCode() == 200) {
						LoadDialog.dismiss(mContext);
						NToast.shortToast(mContext, getString(R.string.update_success));
						this.finish();
					}
					break;
			}
		}
	}
	
	@Override
	public void onFailure(int requestCode, int state, Object result) {
		switch (requestCode) {
			case CHECK_PHONE:
				Toast.makeText(mContext, "手机号可用请求失败", Toast.LENGTH_SHORT).show();
				break;
			case SEND_CODE:
				NToast.shortToast(mContext, "获取验证码请求失败");
				break;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.forget_getcode:
				if (TextUtils.isEmpty(mPhone.getText().toString().trim())) {
					NToast.longToast(mContext, getString(R.string.phone_number_is_null));
				} else {
					DownTimer downTimer = new DownTimer();
					downTimer.setListener(this);
					downTimer.startDown(60 * 1000);
					request(SEND_CODE);
				}
				break;
			case R.id.forget_button:
				if (TextUtils.isEmpty(mPhone.getText().toString())) {
					NToast.shortToast(mContext, getString(R.string.phone_number_is_null));
					mPhone.setShakeAnimation();
					return;
				}
				
				if (TextUtils.isEmpty(mCode.getText().toString())) {
					NToast.shortToast(mContext, getString(R.string.code_is_null));
					mCode.setShakeAnimation();
					return;
				}
				
				if (TextUtils.isEmpty(mPassword1.getText().toString())) {
					NToast.shortToast(mContext, getString(R.string.password_is_null));
					mPassword1.setShakeAnimation();
					return;
				}
				
				if (mPassword1.length() != 6) {
					NToast.shortToast(mContext, "支付密码必须是6位数字");
					return;
				}
				
				if (TextUtils.isEmpty(mPassword2.getText().toString())) {
					NToast.shortToast(mContext, getString(R.string.confirm_password));
					mPassword2.setShakeAnimation();
					return;
				}
				
				if (!mPassword2.getText().toString().equals(mPassword1.getText().toString())) {
					NToast.shortToast(mContext, getString(R.string.passwords_do_not_match));
					return;
				}
				
				LoadDialog.show(mContext);
				request(CHANGE_PASSWORD);
				break;
		}
	}
	
	
	@Override
	public void onTick(long millisUntilFinished) {
		mGetCode.setText(String.valueOf(millisUntilFinished / 1000));
		mGetCode.setClickable(false);
		mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
	}
	
	@Override
	public void onFinish() {
		mGetCode.setText(R.string.get_code);
		mGetCode.setClickable(true);
		mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
	}
}
