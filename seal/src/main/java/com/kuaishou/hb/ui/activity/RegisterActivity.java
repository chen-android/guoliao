package com.kuaishou.hb.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaishou.hb.R;
import com.kuaishou.hb.model.LoginSuccessBroa;
import com.kuaishou.hb.server.broadcast.BroadcastManager;
import com.kuaishou.hb.server.network.http.HttpException;
import com.kuaishou.hb.server.response.CheckPhoneResponse;
import com.kuaishou.hb.server.response.RegisterResponse;
import com.kuaishou.hb.server.response.SendCodeResponse;
import com.kuaishou.hb.server.response.VerifyCodeResponse;
import com.kuaishou.hb.server.utils.AMUtils;
import com.kuaishou.hb.server.utils.MD5;
import com.kuaishou.hb.server.utils.NToast;
import com.kuaishou.hb.server.utils.downtime.DownTimer;
import com.kuaishou.hb.server.utils.downtime.DownTimerListener;
import com.kuaishou.hb.server.widget.ClearWriteEditText;
import com.kuaishou.hb.server.widget.LoadDialog;

/**
 * Created by AMing on 16/1/14.
 * Company RongCloud
 */
@SuppressWarnings("deprecation")
public class RegisterActivity extends BaseActivity implements View.OnClickListener, DownTimerListener {
	
	private static final int CHECK_PHONE = 1;
	private static final int SEND_CODE = 2;
	private static final int VERIFY_CODE = 3;
	private static final int REGISTER = 4;
	private static final int REGISTER_BACK = 1001;
	boolean isBright = true;
	private ImageView mImgBackground;
	private ClearWriteEditText mPhoneEdit, mCodeEdit, mPasswordEdit;
	private Button mGetCode, mConfirm;
	private String mPhone, mCode, mNickName, mPassword, mCodeToken;
	private boolean isRequestCode = false;
	private String unionid;//从微信登陆过来
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		setHeadVisibility(View.GONE);
		unionid = getIntent().getStringExtra("unionid");
		initView();
	}
	
	private void initView() {
		mPhoneEdit = (ClearWriteEditText) findViewById(R.id.reg_phone);
		mCodeEdit = (ClearWriteEditText) findViewById(R.id.reg_code);
		mPasswordEdit = (ClearWriteEditText) findViewById(R.id.reg_password);
		mGetCode = (Button) findViewById(R.id.reg_getcode);
		mConfirm = (Button) findViewById(R.id.reg_button);
		
		mGetCode.setOnClickListener(this);
		mGetCode.setClickable(false);
		mConfirm.setOnClickListener(this);
		
		TextView goLogin = (TextView) findViewById(R.id.reg_login);
		TextView goForget = (TextView) findViewById(R.id.reg_forget);
		goLogin.setOnClickListener(this);
		goForget.setOnClickListener(this);
		
		mImgBackground = (ImageView) findViewById(R.id.rg_img_backgroud);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Animation animation = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.translate_anim);
				mImgBackground.startAnimation(animation);
			}
		}, 200);
		
		addEditTextListener();
		
		mGetCode.setClickable(true);
		mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
		if (!TextUtils.isEmpty(unionid)) {
			mPasswordEdit.setVisibility(View.GONE);
		}
		
	}
	
	private void addEditTextListener() {
		mPhoneEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mPhone = s.toString().trim();
//                if (s.length() == 11 && isBright) {
//                    if (AMUtils.isMobile(s.toString().trim())) {
//                        request(CHECK_PHONE, true);
//                        AMUtils.onInactive(mContext, mPhoneEdit);
//                    } else {
//                        Toast.makeText(mContext, R.string.Illegal_phone_number, Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    mGetCode.setClickable(false);
//                    mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
//                }
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			
			}
		});
		mCodeEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 5) {
					AMUtils.onInactive(mContext, mCodeEdit);
					if (!TextUtils.isEmpty(unionid)) {
						mConfirm.setClickable(true);
						mConfirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
						return;
					}
				}
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			
			}
		});
		mPasswordEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() > 5) {
					mConfirm.setClickable(true);
					mConfirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
				} else {
					mConfirm.setClickable(false);
					mConfirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
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
			case CHECK_PHONE:
				return action.checkPhoneAvailable("86", mPhone);
			case SEND_CODE:
				return action.sendCode(mPhone);
			case VERIFY_CODE:
				return action.verifyCode("86", mPhone, mCode);
			case REGISTER:
				return action.register(mPhone, mPassword, mCodeEdit.getText().toString(), unionid);
		}
		return super.doInBackground(requestCode, id);
	}
	
	@Override
	public void onSuccess(int requestCode, Object result) {
		if (result != null) {
			switch (requestCode) {
				case CHECK_PHONE:
					CheckPhoneResponse cprres = (CheckPhoneResponse) result;
					if (cprres.getCode() == 200) {
						if (cprres.isResult()) {
							mGetCode.setClickable(true);
							mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
							Toast.makeText(mContext, R.string.phone_number_available, Toast.LENGTH_SHORT).show();
						} else {
							mGetCode.setClickable(false);
							mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
							Toast.makeText(mContext, R.string.phone_number_has_been_registered, Toast.LENGTH_SHORT).show();
						}
					}
					break;
				case SEND_CODE:
					SendCodeResponse scrres = (SendCodeResponse) result;
					if (scrres.getCode() == 200) {
						NToast.shortToast(mContext, R.string.messge_send);
					} else if (scrres.getCode() == 5000) {
						NToast.shortToast(mContext, R.string.message_frequency);
					}
					break;
				
				case VERIFY_CODE:
					VerifyCodeResponse vcres = (VerifyCodeResponse) result;
					switch (vcres.getCode()) {
						case 200:
							mCodeToken = vcres.getResult().getVerification_token();
							if (!TextUtils.isEmpty(mCodeToken)) {
								request(REGISTER);
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
						default:
							break;
					}
					break;
				
				case REGISTER:
					RegisterResponse rres = (RegisterResponse) result;
					LoadDialog.dismiss(mContext);
					if (rres.getCode() == 200) {
						NToast.shortToast(mContext, R.string.register_success);
						BroadcastManager.getInstance(mContext).sendBroadcast("login_success", new LoginSuccessBroa(mPhone, mPassword, rres.getData().getUserId(), unionid));
//						Intent data = new Intent();
//						data.putExtra("phone", mPhone);
//						data.putExtra("password", mPassword);
//						data.putExtra("userId", rres.getData().getUserId());
//						setResult(RESULT_OK, data);
						this.finish();
					} else if (rres.getCode() == 62002) {
						Toast.makeText(mContext, "账号格式不正确，请用手机号或邮箱注册", Toast.LENGTH_SHORT).show();
					} else if (rres.getCode() == 62003) {
						Toast.makeText(mContext, "验证码错误", Toast.LENGTH_SHORT).show();
					} else if (rres.getCode() == 62001) {
						Toast.makeText(mContext, "账号已存在", Toast.LENGTH_SHORT).show();
					} else if (rres.getCode() == 600) {
						Toast.makeText(mContext, "服务器繁忙，请稍后。", Toast.LENGTH_SHORT).show();
					}
					break;
				default:
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
			case VERIFY_CODE:
				LoadDialog.dismiss(mContext);
				NToast.shortToast(mContext, "验证码是否可用请求失败");
				break;
			case REGISTER:
				LoadDialog.dismiss(mContext);
				NToast.shortToast(mContext, "注册请求失败");
				break;
		}
	}
	
	@Override
	public android.support.v4.app.FragmentManager getSupportFragmentManager() {
		return null;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.reg_login:
				startActivity(new Intent(this, LoginActivity.class));
				break;
			case R.id.reg_forget:
				startActivity(new Intent(this, ForgetPasswordActivity.class));
				break;
			case R.id.reg_getcode:
				if (TextUtils.isEmpty(mPhoneEdit.getText().toString().trim())) {
					NToast.longToast(mContext, R.string.phone_number_is_null);
				} else {
					isRequestCode = true;
					DownTimer downTimer = new DownTimer();
					downTimer.setListener(this);
					downTimer.startDown(60 * 1000);
					request(SEND_CODE);
				}
				break;
			case R.id.reg_button:
				mPhone = mPhoneEdit.getText().toString().trim();
				mCode = mCodeEdit.getText().toString().trim();
				if (TextUtils.isEmpty(unionid)) {
					mPassword = mPasswordEdit.getText().toString().trim();
				} else {
					mPassword = MD5.encrypt(System.currentTimeMillis() + "");
				}
				
				if (TextUtils.isEmpty(mPhone)) {
					NToast.shortToast(mContext, getString(R.string.phone_number_is_null));
					mPhoneEdit.setShakeAnimation();
					return;
				}
				if (TextUtils.isEmpty(mCode)) {
					NToast.shortToast(mContext, getString(R.string.code_is_null));
					mCodeEdit.setShakeAnimation();
					return;
				}
				if (TextUtils.isEmpty(mPassword)) {
					NToast.shortToast(mContext, getString(R.string.password_is_null));
					mPasswordEdit.setShakeAnimation();
					return;
				}
				if (mPassword.contains(" ")) {
					NToast.shortToast(mContext, getString(R.string.password_cannot_contain_spaces));
					mPasswordEdit.setShakeAnimation();
					return;
				}
				
				if (!isRequestCode) {
					NToast.shortToast(mContext, getString(R.string.not_send_code));
					return;
				}
				
				LoadDialog.show(mContext);
				request(REGISTER, true);
				
				break;
			default:
				break;
		}
	}
	
	@Override
	public void onTick(long millisUntilFinished) {
		mGetCode.setText(String.valueOf(millisUntilFinished / 1000) + "s");
		mGetCode.setClickable(false);
		mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
		isBright = false;
	}
	
	@Override
	public void onFinish() {
		mGetCode.setText(R.string.get_code);
		mGetCode.setClickable(true);
		mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
		isBright = true;
	}
	
}
