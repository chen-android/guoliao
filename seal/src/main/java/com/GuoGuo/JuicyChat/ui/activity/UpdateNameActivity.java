package com.GuoGuo.JuicyChat.ui.activity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.GuoGuo.JuicyChat.GGConst;
import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.server.broadcast.BroadcastManager;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.SetNameResponse;
import com.GuoGuo.JuicyChat.server.utils.NToast;
import com.GuoGuo.JuicyChat.server.widget.ClearWriteEditText;
import com.GuoGuo.JuicyChat.server.widget.LoadDialog;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/6/23.
 * Company RongCloud
 */
public class UpdateNameActivity extends BaseActivity implements View.OnClickListener {
	
	private static final int UPDATE_NAME = 7;
	private ClearWriteEditText mNameEditText;
	private String newName;
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_name);
		setTitle(getString(R.string.update_name));
		Button rightButton = getHeadRightButton();
		rightButton.setVisibility(View.GONE);
		mHeadRightText.setVisibility(View.VISIBLE);
		mHeadRightText.setText(getString(R.string.confirm));
		mHeadRightText.setOnClickListener(this);
		mNameEditText = (ClearWriteEditText) findViewById(R.id.update_name);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		mNameEditText.setText(sp.getString(GGConst.GUOGUO_LOGIN_NAME, ""));
		mNameEditText.setSelection(sp.getString(GGConst.GUOGUO_LOGIN_NAME, "").length());
		editor = sp.edit();
		
	}
	
	
	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		return action.setName(newName);
	}
	
	@Override
	public void onSuccess(int requestCode, Object result) {
		SetNameResponse sRes = (SetNameResponse) result;
		if (sRes.getCode() == 200) {
			editor.putString(GGConst.GUOGUO_LOGIN_NAME, newName);
			editor.commit();
			
			BroadcastManager.getInstance(mContext).sendBroadcast(GGConst.CHANGEINFO);
			
			RongIM.getInstance().refreshUserInfoCache(new UserInfo(sp.getString(GGConst.GUOGUO_LOGIN_ID, ""), newName, Uri.parse(sp.getString(GGConst.GUOGUO_LOGING_PORTRAIT, ""))));
			RongIM.getInstance().setCurrentUserInfo(new UserInfo(sp.getString(GGConst.GUOGUO_LOGIN_ID, ""), newName, Uri.parse(sp.getString(GGConst.GUOGUO_LOGING_PORTRAIT, ""))));
			LoadDialog.dismiss(mContext);
			NToast.shortToast(mContext, "昵称更改成功");
			finish();
		}
	}
	
	@Override
	public void onClick(View v) {
		newName = mNameEditText.getText().toString().trim();
		if (!TextUtils.isEmpty(newName) && newName.length() > 2 && newName.length() < 20) {
			LoadDialog.show(mContext);
			request(UPDATE_NAME, true);
		} else {
			NToast.shortToast(mContext, "昵称必须为2-20个字符或文字");
			mNameEditText.setShakeAnimation();
		}
	}
}
