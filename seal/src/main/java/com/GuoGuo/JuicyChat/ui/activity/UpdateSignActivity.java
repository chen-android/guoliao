package com.GuoGuo.JuicyChat.ui.activity;

import android.content.SharedPreferences;
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

public class UpdateSignActivity extends BaseActivity implements View.OnClickListener {
	private static final int UPDATE_WHATUP = 1;
	private ClearWriteEditText signEt;
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_sign);
		setTitle("设置个性签名");
		signEt = (ClearWriteEditText) findViewById(R.id.update_sign_et);
		Button rightButton = getHeadRightButton();
		rightButton.setVisibility(View.GONE);
		mHeadRightText.setVisibility(View.VISIBLE);
		mHeadRightText.setText(getString(R.string.confirm));
		mHeadRightText.setOnClickListener(this);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		signEt.setText(sp.getString(GGConst.GUOGUO_LOGIN_WHATSUP, ""));
		signEt.setSelection(sp.getString(GGConst.GUOGUO_LOGIN_WHATSUP, "").length());
		editor = sp.edit();
	}
	
	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		return action.setWhatsup(signEt.getText().toString());
	}
	
	@Override
	public void onSuccess(int requestCode, Object result) {
		SetNameResponse sRes = (SetNameResponse) result;
		if (sRes.getCode() == 200) {
			editor.putString(GGConst.GUOGUO_LOGIN_WHATSUP, signEt.getText().toString());
			editor.commit();
			
			BroadcastManager.getInstance(mContext).sendBroadcast(GGConst.CHANGEINFO);
			LoadDialog.dismiss(mContext);
			NToast.shortToast(mContext, "签名更改成功");
			finish();
		}
	}
	
	@Override
	public void onClick(View v) {
		String whatup = signEt.getText().toString().trim();
		if (!TextUtils.isEmpty(whatup) && whatup.length() < 50) {
			LoadDialog.show(mContext);
			request(UPDATE_WHATUP, true);
		} else {
			NToast.shortToast(mContext, "个性签名不能多于50个字符或文字");
			signEt.setShakeAnimation();
		}
	}
}
