package com.kuaishou.hb.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaishou.hb.GGConst;
import com.kuaishou.hb.R;
import com.kuaishou.hb.server.broadcast.BroadcastManager;
import com.kuaishou.hb.server.utils.NToast;
import com.kuaishou.hb.server.widget.DialogWithYesOrNoUtils;
import com.kuaishou.hb.utils.SharedPreferencesContext;

import java.io.File;

/**
 * Created by AMing on 16/6/23.
 * Company RongCloud
 */
public class AccountSettingActivity extends BaseActivity implements View.OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_set);
		setTitle(R.string.account_setting);
		initViews();
	}
	
	private void initViews() {
		RelativeLayout mPassword = (RelativeLayout) findViewById(R.id.ac_set_change_pswd);
		RelativeLayout mPrivacy = (RelativeLayout) findViewById(R.id.ac_set_privacy);
		RelativeLayout mNewMessage = (RelativeLayout) findViewById(R.id.ac_set_new_message);
		RelativeLayout mClean = (RelativeLayout) findViewById(R.id.ac_set_clean);
		RelativeLayout mExit = (RelativeLayout) findViewById(R.id.ac_set_exit);
		TextView mPayPwdEdit = (TextView) findViewById(R.id.pay_pwd_edit_tv);
		TextView mPayPwdForget = (TextView) findViewById(R.id.pay_pwd_forget_tv);
		
		if (SharedPreferencesContext.getInstance().isSetPayPwd()) {
			mPayPwdEdit.setText("修改支付密码");
			mPayPwdForget.setVisibility(View.VISIBLE);
		} else {
			mPayPwdEdit.setText("设置支付密码");
			mPayPwdForget.setVisibility(View.GONE);
		}
		
		mPassword.setOnClickListener(this);
		mPrivacy.setOnClickListener(this);
		mNewMessage.setOnClickListener(this);
		mClean.setOnClickListener(this);
		mExit.setOnClickListener(this);
		mPayPwdEdit.setOnClickListener(this);
		mPayPwdForget.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ac_set_change_pswd:
				startActivity(new Intent(this, UpdatePasswordActivity.class));
				break;
			case R.id.ac_set_privacy:
				startActivity(new Intent(this, PrivacyActivity.class));
				break;
			case R.id.ac_set_new_message:
				startActivity(new Intent(this, NewMessageRemindActivity.class));
				break;
			case R.id.ac_set_clean:
				DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "是否清除缓存?", new DialogWithYesOrNoUtils.DialogCallBack() {
					@Override
					public void executeEvent() {
						File file = new File(Environment.getExternalStorageDirectory().getPath() + getPackageName());
						deleteFile(file);
						
						NToast.shortToast(mContext, "清除成功");
					}
					
					@Override
					public void executeEditEvent(String editText) {
					
					}
					
					@Override
					public void updatePassword(String oldPassword, String newPassword) {
					
					}
				});
				break;
			case R.id.ac_set_exit:
				DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "是否退出登录?", new DialogWithYesOrNoUtils.DialogCallBack() {
					@Override
					public void executeEvent() {
						BroadcastManager.getInstance(mContext).sendBroadcast(GGConst.EXIT);
					}
					
					@Override
					public void executeEditEvent(String editText) {
					
					}
					
					@Override
					public void updatePassword(String oldPassword, String newPassword) {
					
					}
				});
				break;
			case R.id.pay_pwd_edit_tv:
				Intent intent = new Intent(this, PayPwdEditActivity.class);
				intent.putExtra("isSet", SharedPreferencesContext.getInstance().isSetPayPwd());
				startActivity(intent);
				break;
			case R.id.pay_pwd_forget_tv:
				startActivity(new Intent(this, ForgetPayPasswordActivity.class));
				break;
			default:
				break;
		}
	}
	
	
	public void deleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				deleteFile(f);
			}
			file.delete();
		}
	}
	
}
