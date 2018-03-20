package com.kuaishou.hb.ui.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuaishou.hb.GGConst;
import com.kuaishou.hb.R;
import com.kuaishou.hb.SealUserInfoManager;
import com.kuaishou.hb.server.broadcast.BroadcastManager;
import com.kuaishou.hb.server.network.http.HttpException;
import com.kuaishou.hb.server.response.GetTokenResponse;
import com.kuaishou.hb.server.response.GetUserInfoByTokenResponse;
import com.kuaishou.hb.server.response.LoginResponse;
import com.kuaishou.hb.server.utils.CommonUtils;
import com.kuaishou.hb.server.utils.NLog;
import com.kuaishou.hb.server.utils.NToast;
import com.kuaishou.hb.server.utils.RongGenerate;
import com.kuaishou.hb.server.widget.ClearWriteEditText;
import com.kuaishou.hb.server.widget.LoadDialog;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/1/15.
 * Company RongCloud
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

	private final static String TAG = "LoginActivity";
	private static final int LOGIN = 5;
	private static final int WX_LOGIN = 4;
	private static final int QQ_LOGIN = 7;
	private static final int GET_TOKEN = 6;
	private static final int SYNC_USER_INFO = 9;

	private ImageView mImg_Background;
	private ImageView wechatLoginIv;
	private ImageView qqLoginIv;
	private ClearWriteEditText mPhoneEdit, mPasswordEdit;
	private String phoneString;
	private String passwordString;
	private String connectResultId;
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private String loginToken;
	private String loginId;
	private Button mConfirm;
	private String unionid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setHeadVisibility(View.GONE);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		editor = sp.edit();
		initView();
	}

	private void initView() {
		mPhoneEdit = (ClearWriteEditText) findViewById(R.id.de_login_phone);
		mPasswordEdit = (ClearWriteEditText) findViewById(R.id.de_login_password);
		mConfirm = (Button) findViewById(R.id.de_login_sign);
		TextView mRegister = (TextView) findViewById(R.id.de_login_register);
		TextView forgetPassword = (TextView) findViewById(R.id.de_login_forgot);
		wechatLoginIv = (ImageView) findViewById(R.id.login_wechat_iv);
		qqLoginIv = (ImageView) findViewById(R.id.login_qq_iv);
		wechatLoginIv.setOnClickListener(this);
		qqLoginIv.setOnClickListener(this);
		forgetPassword.setOnClickListener(this);
		mConfirm.setOnClickListener(this);
		mRegister.setOnClickListener(this);
		mImg_Background = (ImageView) findViewById(R.id.de_img_backgroud);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.translate_anim);
				mImg_Background.startAnimation(animation);
			}
		}, 200);

		String oldPhone = sp.getString(GGConst.GUOGUO_LOGING_PHONE, "");
		String oldPassword = sp.getString(GGConst.GUOGUO_LOGING_PASSWORD, "");

		if (!TextUtils.isEmpty(oldPhone) && !TextUtils.isEmpty(oldPassword)) {
			mPhoneEdit.setText(oldPhone);
			mPasswordEdit.setText(oldPassword);
		}

		if (getIntent().getBooleanExtra("kickedByOtherClient", false)) {
			final AlertDialog dlg = new AlertDialog.Builder(LoginActivity.this).create();
			dlg.show();
			Window window = dlg.getWindow();
			window.setContentView(R.layout.other_devices);
			TextView text = (TextView) window.findViewById(R.id.ok);
			text.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dlg.cancel();
				}
			});
		}

		BroadcastManager.getInstance(mContext).addAction("login_success", new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String result = intent.getStringExtra("result");
				try {
					JSONObject object = new JSONObject(result);
					String phone = object.getString("phone");
					String password = object.getString("pwd");
					String id = object.getString("userId");
					if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(id)) {
						mPhoneEdit.setText(phone);
						mPasswordEdit.setText(password);
						editor.putString(GGConst.GUOGUO_LOGING_PHONE, phone);
						editor.putString(GGConst.GUOGUO_LOGING_PASSWORD, password);
						editor.putString(GGConst.GUOGUO_LOGIN_ID, id);
						editor.apply();
						mConfirm.performClick();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private UMAuthListener umAuthListener = new UMAuthListener() {
		@Override
		public void onStart(SHARE_MEDIA share_media) {

		}

		@Override
		public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
			if (share_media == SHARE_MEDIA.WEIXIN) {
				unionid = map.get("uid");
				if (!TextUtils.isEmpty(unionid)) {
					LoadDialog.show(mContext);
					request(WX_LOGIN);
				}
			} else if (share_media == SHARE_MEDIA.QQ) {
				unionid = map.get("uid");
				if (!TextUtils.isEmpty(unionid)) {
					LoadDialog.show(mContext);
					request(QQ_LOGIN);
				}
			}
		}

		@Override
		public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

		}

		@Override
		public void onCancel(SHARE_MEDIA share_media, int i) {

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.de_login_sign:
				phoneString = mPhoneEdit.getText().toString().trim();
				passwordString = mPasswordEdit.getText().toString().trim();

				if (TextUtils.isEmpty(phoneString)) {
					NToast.shortToast(mContext, R.string.phone_number_is_null);
					mPhoneEdit.setShakeAnimation();
					return;
				}

//                if (!AMUtils.isMobile(phoneString)) {
//                    NToast.shortToast(mContext, R.string.Illegal_phone_number);
//                    mPhoneEdit.setShakeAnimation();
//                    return;
//                }

				if (TextUtils.isEmpty(passwordString)) {
					NToast.shortToast(mContext, R.string.password_is_null);
					mPasswordEdit.setShakeAnimation();
					return;
				}
				if (passwordString.contains(" ")) {
					NToast.shortToast(mContext, R.string.password_cannot_contain_spaces);
					mPasswordEdit.setShakeAnimation();
					return;
				}
				LoadDialog.show(mContext);
				editor.putBoolean("exit", false);
				editor.apply();
				String oldPhone = sp.getString(GGConst.GUOGUO_LOGING_PHONE, "");
				request(LOGIN, true);
				break;
			case R.id.de_login_register:
				startActivityForResult(new Intent(this, RegisterActivity.class), 1);
				break;
			case R.id.de_login_forgot:
				startActivityForResult(new Intent(this, ForgetPasswordActivity.class), 2);
				break;
			case R.id.login_wechat_iv:
				UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, umAuthListener);
				break;
			case R.id.login_qq_iv:
				UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.QQ, umAuthListener);
				break;
			default:
				break;
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 2 && data != null) {
			String phone = data.getStringExtra("phone");
			String password = data.getStringExtra("password");
			mPhoneEdit.setText(phone);
			mPasswordEdit.setText(password);
		} else if (data != null && requestCode == 1) {


		}
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		switch (requestCode) {
			case LOGIN:
				return action.login("86", phoneString, passwordString);
			case GET_TOKEN:
				return action.getToken();
			case SYNC_USER_INFO:
				return action.getUserInfoByToken(loginToken);
			case WX_LOGIN:
				return action.wxLogin(unionid);
			case QQ_LOGIN:
				return action.qqLogin(unionid);
		}
		return null;
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (result != null) {
			switch (requestCode) {
				case LOGIN:
					LoginResponse loginResponse = (LoginResponse) result;
					if (loginResponse.getCode() == 200) {
						loginToken = loginResponse.getData().getToken();
						loginId = loginResponse.getData().getUserId();
						if (!TextUtils.isEmpty(loginToken)) {
							RongIM.connect(loginToken, new RongIMClient.ConnectCallback() {
								@Override
								public void onTokenIncorrect() {
									NLog.e("connect", "onTokenIncorrect");
								}

								@Override
								public void onSuccess(String s) {
									connectResultId = s;
									editor.putString(GGConst.GUOGUO_LOGIN_TOKEN, loginToken);
									editor.putString(GGConst.GUOGUO_LOGIN_ID, connectResultId);
									editor.apply();
									SealUserInfoManager.getInstance().openDB();
									request(SYNC_USER_INFO, true);
								}

								@Override
								public void onError(RongIMClient.ErrorCode errorCode) {
									NLog.e("connect", "onError errorcode:" + errorCode.getValue());
								}
							});
						}
					} else if (loginResponse.getCode() == 61001) {
						LoadDialog.dismiss(mContext);
						NToast.shortToast(mContext, R.string.phone_or_psw_error);
					} else if (loginResponse.getCode() == 61002) {
						LoadDialog.dismiss(mContext);
						NToast.shortToast(mContext, R.string.login_no_register);
					} else {
						LoadDialog.dismiss(mContext);
						NToast.shortToast(mContext, R.string.server_err_unknown);
					}
					break;
				case SYNC_USER_INFO:
					GetUserInfoByTokenResponse userInfoByIdResponse = (GetUserInfoByTokenResponse) result;
					if (userInfoByIdResponse.getCode() == 200) {
						GetUserInfoByTokenResponse.ResultEntity data = userInfoByIdResponse.getData();
						if (TextUtils.isEmpty(data.getHeadico())) {
							data.setHeadico(RongGenerate.generateDefaultAvatar(data.getNickname(), data.getId()));
						}
						String nickName = data.getNickname();
						String portraitUri = data.getHeadico();
						String sex = data.getSex();
						editor.putString(GGConst.GUOGUO_LOGIN_NAME, nickName);
						editor.putString(GGConst.GUOGUO_LOGING_PORTRAIT, portraitUri);
						editor.putString(GGConst.GUOGUO_LOGIN_SEX, sex);
						editor.putString(GGConst.GUOGUO_LOGING_PHONE, data.getUsername());
						editor.putString(GGConst.GUOGUO_LOGIN_WHATSUP, data.getWhatsup());
						editor.putInt(GGConst.GUOGUO_LOGIN_VIDEOLIMIT, data.getVideolimit());
						editor.apply();
						RongIM.getInstance().refreshUserInfoCache(new UserInfo(data.getId(), nickName, Uri.parse(portraitUri)));
					}
					//不继续在login界面同步好友,群组,群组成员信息
					SealUserInfoManager.getInstance().getAllUserInfo();
					goToMain();
					break;
				case GET_TOKEN:
					GetTokenResponse tokenResponse = (GetTokenResponse) result;
					if (tokenResponse.getCode() == 200) {
						String token = tokenResponse.getResult().getToken();
						if (!TextUtils.isEmpty(token)) {
							RongIM.connect(token, new RongIMClient.ConnectCallback() {
								@Override
								public void onTokenIncorrect() {
									Log.e(TAG, "reToken Incorrect");
								}

								@Override
								public void onSuccess(String s) {
									connectResultId = s;
									NLog.e("connect", "onSuccess userid:" + s);
									editor.putString(GGConst.GUOGUO_LOGIN_ID, s);
									editor.apply();
									SealUserInfoManager.getInstance().openDB();
									request(SYNC_USER_INFO, true);
								}

								@Override
								public void onError(RongIMClient.ErrorCode e) {

								}
							});
						}
					}
					break;
				case WX_LOGIN:
					LoginResponse loginR = (LoginResponse) result;
					LoadDialog.dismiss(mContext);
					if (loginR.getCode() == 200) {
						loginToken = loginR.getData().getToken();
						loginId = loginR.getData().getUserId();
						if (!TextUtils.isEmpty(loginToken)) {
							RongIM.connect(loginToken, new RongIMClient.ConnectCallback() {
								@Override
								public void onTokenIncorrect() {
								}

								@Override
								public void onSuccess(String s) {
									connectResultId = s;
									editor.putString(GGConst.GUOGUO_LOGIN_TOKEN, loginToken);
									editor.putString(GGConst.GUOGUO_LOGIN_ID, connectResultId);
									editor.apply();
									SealUserInfoManager.getInstance().openDB();
									request(SYNC_USER_INFO, true);
								}

								@Override
								public void onError(RongIMClient.ErrorCode errorCode) {
									NLog.e("connect", "onError errorcode:" + errorCode.getValue());
								}
							});
						}
					} else if (loginR.getCode() == 61003) {
						Intent intent = new Intent(this, RegisterActivity.class);
						intent.putExtra("unionid", unionid);
						startActivity(intent);
					}
					break;
				case QQ_LOGIN:
					LoginResponse loginR1 = (LoginResponse) result;
					LoadDialog.dismiss(mContext);
					if (loginR1.getCode() == 200) {
						loginToken = loginR1.getData().getToken();
						loginId = loginR1.getData().getUserId();
						if (!TextUtils.isEmpty(loginToken)) {
							RongIM.connect(loginToken, new RongIMClient.ConnectCallback() {
								@Override
								public void onTokenIncorrect() {
								}

								@Override
								public void onSuccess(String s) {
									connectResultId = s;
									editor.putString(GGConst.GUOGUO_LOGIN_TOKEN, loginToken);
									editor.putString(GGConst.GUOGUO_LOGIN_ID, connectResultId);
									editor.apply();
									SealUserInfoManager.getInstance().openDB();
									request(SYNC_USER_INFO, true);
								}

								@Override
								public void onError(RongIMClient.ErrorCode errorCode) {
									NLog.e("connect", "onError errorcode:" + errorCode.getValue());
								}
							});
						}
					} else if (loginR1.getCode() == 61003) {
						Intent intent = new Intent(this, RegisterActivity.class);
						intent.putExtra("unionid", unionid);
						startActivity(intent);
					}
					break;
				default:
					break;
			}
		}
	}

	private void reGetToken() {
		request(GET_TOKEN);
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (!CommonUtils.isNetworkConnected(mContext)) {
			LoadDialog.dismiss(mContext);
			NToast.shortToast(mContext, getString(R.string.network_not_available));
			return;
		}
		switch (requestCode) {
			case LOGIN:
				LoadDialog.dismiss(mContext);
				NToast.shortToast(mContext, R.string.login_api_fail);
				break;
			case SYNC_USER_INFO:
				LoadDialog.dismiss(mContext);
				NToast.shortToast(mContext, R.string.sync_userinfo_api_fail);
				break;
			case GET_TOKEN:
				LoadDialog.dismiss(mContext);
				NToast.shortToast(mContext, R.string.get_token_api_fail);
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void goToMain() {
		editor.putString(GGConst.GUOGUO_LOGING_PHONE, phoneString);
		editor.putString(GGConst.GUOGUO_LOGING_PASSWORD, passwordString);
		editor.apply();
		LoadDialog.dismiss(mContext);
		NToast.shortToast(mContext, R.string.login_success);
		startActivity(new Intent(LoginActivity.this, MainActivity.class));
		finish();
	}
}
