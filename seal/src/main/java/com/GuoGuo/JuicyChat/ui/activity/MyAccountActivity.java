package com.GuoGuo.JuicyChat.ui.activity;


import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.App;
import com.GuoGuo.JuicyChat.GGConst;
import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.SealUserInfoManager;
import com.GuoGuo.JuicyChat.server.broadcast.BroadcastManager;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.QiNiuTokenResponse;
import com.GuoGuo.JuicyChat.server.response.SetPortraitResponse;
import com.GuoGuo.JuicyChat.server.utils.NToast;
import com.GuoGuo.JuicyChat.server.utils.photo.PhotoUtils;
import com.GuoGuo.JuicyChat.server.widget.BottomMenuDialog;
import com.GuoGuo.JuicyChat.server.widget.LoadDialog;
import com.GuoGuo.JuicyChat.server.widget.SelectableRoundedImageView;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;


public class MyAccountActivity extends BaseActivity implements View.OnClickListener {
	
	private static final int UP_LOAD_PORTRAIT = 8;
	private static final int GET_QI_NIU_TOKEN = 128;
	private static final int UPDATE_SEX = 111;
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private SelectableRoundedImageView mImageView;
	private TextView mName;
	private TextView mSex;
	private TextView mId;
	private TextView mAccount;
	private TextView mSign;//个性签名
	private PhotoUtils photoUtils;
	private BottomMenuDialog dialog;
	private BottomMenuDialog sexDialog;
	private UploadManager uploadManager;
	private String imageUrl;
	private String sex;
	private Uri selectUri;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myaccount);
		setTitle(R.string.de_actionbar_myacc);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		editor = sp.edit();
		initView();
	}
	
	private void initView() {
		RelativeLayout portraitItem = (RelativeLayout) findViewById(R.id.rl_my_portrait);
		RelativeLayout nameItem = (RelativeLayout) findViewById(R.id.rl_my_username);
		RelativeLayout sexItem = (RelativeLayout) findViewById(R.id.rl_my_sex);
		mImageView = (SelectableRoundedImageView) findViewById(R.id.img_my_portrait);
		mName = (TextView) findViewById(R.id.tv_my_username);
		mSex = (TextView) findViewById(R.id.tv_my_sex);
		mId = (TextView) findViewById(R.id.tv_my_phone);
		mAccount = (TextView) findViewById(R.id.tv_my_account);
		mSign = (TextView) findViewById(R.id.tv_my_sign);
		portraitItem.setOnClickListener(this);
		nameItem.setOnClickListener(this);
		sexItem.setOnClickListener(this);
		mSign.setOnClickListener(this);
		String cacheName = sp.getString(GGConst.GUOGUO_LOGIN_NAME, "");
		String cachePortrait = sp.getString(GGConst.GUOGUO_LOGING_PORTRAIT, "");
		String cachePhone = sp.getString(GGConst.GUOGUO_LOGING_PHONE, "");
		String cacheSex = sp.getString(GGConst.GUOGUO_LOGIN_SEX, "");
		String whatsup = sp.getString(GGConst.GUOGUO_LOGIN_WHATSUP, "");
		mAccount.setText(cachePhone);
		mSign.setText(TextUtils.isEmpty(whatsup) ? "暂未设置" : whatsup);
		if (!TextUtils.isEmpty(cacheName)) {
			mName.setText(cacheName);
			String cacheId = sp.getString(GGConst.GUOGUO_LOGIN_ID, "a");
			String portraitUri = SealUserInfoManager.getInstance().getPortraitUri(new UserInfo(
					cacheId, cacheName, Uri.parse(cachePortrait)));
			ImageLoader.getInstance().displayImage(portraitUri, mImageView, App.getOptions());
		}
		if (!TextUtils.isEmpty(cacheSex)) {
			if ("1".equals(cacheSex)) {
				mSex.setText("男");
			} else if ("2".equals(cacheSex)) {
				mSex.setText("女");
			} else {
				mSex.setText("保密");
			}
		}
		mId.setText(sp.getString(GGConst.GUOGUO_LOGIN_ID, ""));
		setPortraitChangeListener();
		BroadcastManager.getInstance(mContext).addAction(GGConst.CHANGEINFO, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				mName.setText(sp.getString(GGConst.GUOGUO_LOGIN_NAME, ""));
				mSign.setText(sp.getString(GGConst.GUOGUO_LOGIN_WHATSUP, ""));
			}
		});
	}
	
	private void setPortraitChangeListener() {
		photoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
			@Override
			public void onPhotoResult(Uri uri) {
				if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
					selectUri = uri;
					LoadDialog.show(mContext);
					request(GET_QI_NIU_TOKEN);
				}
			}
			
			@Override
			public void onPhotoCancel() {
				
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.rl_my_portrait:
				showPhotoDialog();
				break;
			case R.id.rl_my_username:
				startActivity(new Intent(this, UpdateNameActivity.class));
				break;
			case R.id.rl_my_sex:
				showSexDialog();
				break;
			case R.id.tv_my_sign:
				startActivity(new Intent(this, UpdateSignActivity.class));
				break;
		}
	}
	
	
	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		switch (requestCode) {
			case UP_LOAD_PORTRAIT:
				return action.setPortrait(imageUrl);
			case GET_QI_NIU_TOKEN:
				return action.getQiNiuToken();
			case UPDATE_SEX:
				return action.setSex(sex);
		}
		return super.doInBackground(requestCode, id);
	}
	
	@Override
	public void onSuccess(int requestCode, Object result) {
		if (result != null) {
			switch (requestCode) {
				case UP_LOAD_PORTRAIT:
					SetPortraitResponse spRes = (SetPortraitResponse) result;
					if (spRes.getCode() == 200) {
						editor.putString(GGConst.GUOGUO_LOGING_PORTRAIT, imageUrl);
						editor.commit();
						ImageLoader.getInstance().displayImage(imageUrl, mImageView, App.getOptions());
						if (RongIM.getInstance() != null) {
							RongIM.getInstance().setCurrentUserInfo(new UserInfo(sp.getString(GGConst.GUOGUO_LOGIN_ID, ""), sp.getString(GGConst.GUOGUO_LOGIN_NAME, ""), Uri.parse(imageUrl)));
						}
						BroadcastManager.getInstance(mContext).sendBroadcast(GGConst.CHANGEINFO);
						NToast.shortToast(mContext, getString(R.string.portrait_update_success));
					}
					LoadDialog.dismiss(mContext);
					break;
				case GET_QI_NIU_TOKEN:
					QiNiuTokenResponse response = (QiNiuTokenResponse) result;
					if (response.getCode() == 200) {
						uploadImage("", response.getData().getQiniutoken(), selectUri);
					}
					break;
				case UPDATE_SEX:
					mSex.setText("1".equals(sex) ? "男" : "女");
					editor.putString(GGConst.GUOGUO_LOGIN_SEX, sex);
					editor.commit();
					break;
			}
		}
	}
	
	
	@Override
	public void onFailure(int requestCode, int state, Object result) {
		switch (requestCode) {
			case GET_QI_NIU_TOKEN:
			case UP_LOAD_PORTRAIT:
				NToast.shortToast(mContext, "设置头像请求失败");
				LoadDialog.dismiss(mContext);
				break;
		}
	}
	
	static public final int REQUEST_CODE_ASK_PERMISSIONS = 101;
	
	/**
	 * 弹出底部框
	 */
	@TargetApi(23)
	private void showPhotoDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		
		dialog = new BottomMenuDialog(mContext);
		dialog.setConfirmListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				if (checkCameraAndSDPermission()) {
					photoUtils.takePicture(MyAccountActivity.this);
				}
			}
		});
		dialog.setMiddleListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				if (checkCameraAndSDPermission()) {
					photoUtils.selectPicture(MyAccountActivity.this);
				}
			}
		});
		dialog.show();
	}
	
	private void showSexDialog() {
		if (sexDialog != null && sexDialog.isShowing()) {
			sexDialog.dismiss();
		}
		
		sexDialog = new BottomMenuDialog(mContext, "男", "女");
		sexDialog.setConfirmListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (sexDialog != null && sexDialog.isShowing()) {
					sexDialog.dismiss();
				}
				sex = "1";
				request(UPDATE_SEX);
			}
		});
		sexDialog.setMiddleListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (sexDialog != null && sexDialog.isShowing()) {
					sexDialog.dismiss();
				}
				sex = "2";
				request(UPDATE_SEX);
			}
		});
		sexDialog.show();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case PhotoUtils.INTENT_CROP:
			case PhotoUtils.INTENT_TAKE:
			case PhotoUtils.INTENT_SELECT:
				photoUtils.onActivityResult(MyAccountActivity.this, requestCode, resultCode, data);
				break;
		}
	}
	
	
	public void uploadImage(final String domain, String imageToken, Uri imagePath) {
		if (TextUtils.isEmpty(domain) && TextUtils.isEmpty(imageToken) && TextUtils.isEmpty(imagePath.toString())) {
			throw new RuntimeException("upload parameter is null!");
		}
		File imageFile = new File(imagePath.getPath());
		
		if (this.uploadManager == null) {
			this.uploadManager = new UploadManager();
		}
		this.uploadManager.put(imageFile, new Date().getTime() + imageFile.getName(), imageToken, new UpCompletionHandler() {
			
			@Override
			public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
				if (responseInfo.isOK()) {
					try {
						String key = (String) jsonObject.get("key");
						imageUrl = GGConst.QINIU_URL + key;
						Log.e("uploadImage", imageUrl);
						if (!TextUtils.isEmpty(imageUrl)) {
							request(UP_LOAD_PORTRAIT);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					NToast.shortToast(mContext, getString(R.string.upload_portrait_failed));
					LoadDialog.dismiss(mContext);
				}
			}
		}, null);
	}
}
