package com.GuoGuo.JuicyChat.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.GuoGuo.JuicyChat.GGConst;
import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.SealUserInfoManager;
import com.GuoGuo.JuicyChat.db.Friend;
import com.GuoGuo.JuicyChat.db.Groups;
import com.GuoGuo.JuicyChat.server.broadcast.BroadcastManager;
import com.GuoGuo.JuicyChat.server.network.async.AsyncTaskManager;
import com.GuoGuo.JuicyChat.server.network.async.OnDataListener;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.CreateGroupResponse;
import com.GuoGuo.JuicyChat.server.response.QiNiuTokenResponse;
import com.GuoGuo.JuicyChat.server.response.SetGroupPortraitResponse;
import com.GuoGuo.JuicyChat.server.utils.NToast;
import com.GuoGuo.JuicyChat.server.utils.photo.PhotoUtils;
import com.GuoGuo.JuicyChat.server.widget.BottomMenuDialog;
import com.GuoGuo.JuicyChat.server.widget.ClearWriteEditText;
import com.GuoGuo.JuicyChat.server.widget.LoadDialog;
import com.GuoGuo.JuicyChat.utils.BitmapUtils;
import com.GuoGuo.JuicyChat.utils.SharedPreferencesContext;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.model.Conversation;

/**
 * Created by AMing on 16/1/25.
 * Company RongCloud
 */
public class CreateGroupActivity extends BaseActivity implements View.OnClickListener {
	
	private static final int GET_QI_NIU_TOKEN = 131;
	private static final int CREATE_GROUP = 16;
	private static final int SET_GROUP_PORTRAIT_URI = 17;
	public static final String REFRESH_GROUP_UI = "REFRESH_GROUP_UI";
	private AsyncImageView asyncImageView;
	private PhotoUtils photoUtils;
	private BottomMenuDialog dialog;
	private String mGroupName, mGroupId;
	private ClearWriteEditText mGroupNameEdit;
	private List<Integer> groupIds = new ArrayList<>();
	private Uri selectUri;
	private UploadManager uploadManager;
	private String imageUrl = "";
	
	@Override
	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);
		setTitle(R.string.rc_item_create_group);
		List<Friend> memberList = (List<Friend>) getIntent().getSerializableExtra("GroupMember");
		initView();
		setPortraitChangeListener();
		if (memberList != null && memberList.size() > 0) {
			groupIds.add(Integer.valueOf(getSharedPreferences("config", MODE_PRIVATE).getString(GGConst.GUOGUO_LOGIN_ID, "0")));
			for (Friend f : memberList) {
				groupIds.add(Integer.valueOf(f.getFriendid()));
			}
		}
	}
	
	private void setPortraitChangeListener() {
		photoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
			@Override
			public void onPhotoResult(Uri uri) {
				if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
					selectUri = uri;
					AsyncTaskManager.getInstance(mContext).request(5, new OnDataListener() {
						@Override
						public Object doInBackground(int requestCode, String parameter) throws HttpException {
							Bitmap bm = BitmapUtils.createBitmap(BitmapFactory.decodeFile(selectUri.getPath()), BitmapFactory.decodeResource(getResources(), R.drawable.icon_shuiyin));
							try {
								File file = new File(selectUri.getPath());
								if (file.exists()) {
									file.delete();
								}
								FileOutputStream out = new FileOutputStream(file);
								bm.compress(Bitmap.CompressFormat.PNG, 90, out);
								out.flush();
								out.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
							return null;
						}
						
						@Override
						public void onSuccess(int requestCode, Object result) {
							LoadDialog.show(mContext);
							request(GET_QI_NIU_TOKEN);
						}
						
						@Override
						public void onFailure(int requestCode, int state, Object result) {
							
						}
					});
					
					
				}
			}
			
			@Override
			public void onPhotoCancel() {
				
			}
		});
	}
	
	private void initView() {
		asyncImageView = (AsyncImageView) findViewById(R.id.img_Group_portrait);
		asyncImageView.setOnClickListener(this);
		Button mButton = (Button) findViewById(R.id.create_ok);
		mButton.setOnClickListener(this);
		mGroupNameEdit = (ClearWriteEditText) findViewById(R.id.create_groupname);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.img_Group_portrait:
				showPhotoDialog();
				break;
			case R.id.create_ok:
				mGroupName = mGroupNameEdit.getText().toString().trim();
				if (TextUtils.isEmpty(mGroupName)) {
					NToast.shortToast(mContext, getString(R.string.group_name_not_is_null));
					break;
				}
				if (mGroupName.length() < 2 || mGroupName.length() > 20) {
					NToast.shortToast(mContext, getString(R.string.group_name_size_is_one));
					return;
				}
				if (AndroidEmoji.isEmoji(mGroupName)) {
					if (mGroupName.length() <= 2) {
						NToast.shortToast(mContext, getString(R.string.group_name_size_is_one));
						return;
					}
				}
				if (groupIds.size() > 1) {
					LoadDialog.show(mContext);
					request(CREATE_GROUP, true);
				}
				
				break;
		}
	}
	
	
	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		switch (requestCode) {
			case CREATE_GROUP:
				return action.createGroup(mGroupName, imageUrl, groupIds);
			case SET_GROUP_PORTRAIT_URI:
				return action.setGroupPortrait(mGroupId, imageUrl);
			case GET_QI_NIU_TOKEN:
				return action.getQiNiuToken();
		}
		return null;
	}
	
	@Override
	public void onSuccess(int requestCode, Object result) {
		if (result != null) {
			switch (requestCode) {
				case CREATE_GROUP:
					CreateGroupResponse createGroupResponse = (CreateGroupResponse) result;
					if (createGroupResponse.getCode() == 200) {
						mGroupId = createGroupResponse.getData().getGroupid(); //id == null
						SealUserInfoManager.getInstance().getGroups(mGroupId);
						SealUserInfoManager.getInstance().getGroupMember(mGroupId);
						BroadcastManager.getInstance(mContext).sendBroadcast(REFRESH_GROUP_UI);
						LoadDialog.dismiss(mContext);
						NToast.shortToast(mContext, getString(R.string.create_group_success));
						RongIM.getInstance().startConversation(mContext, Conversation.ConversationType.GROUP, mGroupId, mGroupName);
						finish();
					}
					break;
				case SET_GROUP_PORTRAIT_URI:
					SetGroupPortraitResponse groupPortraitResponse = (SetGroupPortraitResponse) result;
					if (groupPortraitResponse.getCode() == 200) {
						SealUserInfoManager.getInstance().addGroup(new Groups());
						BroadcastManager.getInstance(mContext).sendBroadcast(REFRESH_GROUP_UI);
						LoadDialog.dismiss(mContext);
						NToast.shortToast(mContext, getString(R.string.create_group_success));
						RongIM.getInstance().startConversation(mContext, Conversation.ConversationType.GROUP, mGroupId, mGroupName);
						finish();
					}
				case GET_QI_NIU_TOKEN:
					QiNiuTokenResponse response = (QiNiuTokenResponse) result;
					if (response.getCode() == 200) {
						uploadImage("", response.getData().getQiniutoken(), selectUri);
					}
					break;
			}
		}
	}
	
	@Override
	public void onFailure(int requestCode, int state, Object result) {
		switch (requestCode) {
			case CREATE_GROUP:
				LoadDialog.dismiss(mContext);
				NToast.shortToast(mContext, getString(R.string.group_create_api_fail));
				break;
			case GET_QI_NIU_TOKEN:
				LoadDialog.dismiss(mContext);
				NToast.shortToast(mContext, getString(R.string.upload_portrait_failed));
				break;
			case SET_GROUP_PORTRAIT_URI:
				LoadDialog.dismiss(mContext);
				NToast.shortToast(mContext, getString(R.string.group_header_api_fail));
				break;
		}
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		hintKbTwo();
		finish();
		return super.onOptionsItemSelected(item);
	}
	
	
	/**
	 * 弹出底部框
	 */
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
					photoUtils.takePicture(CreateGroupActivity.this);
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
					photoUtils.selectPicture(CreateGroupActivity.this);
				}
			}
		});
		dialog.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case PhotoUtils.INTENT_CROP:
			case PhotoUtils.INTENT_TAKE:
			case PhotoUtils.INTENT_SELECT:
				photoUtils.onActivityResult(CreateGroupActivity.this, requestCode, resultCode, data);
				break;
		}
	}
	
	
	public void uploadImage(final String domain, String imageToken, final Uri imagePath) {
		if (TextUtils.isEmpty(domain) && TextUtils.isEmpty(imageToken) && TextUtils.isEmpty(imagePath.toString())) {
			throw new RuntimeException("upload parameter is null!");
		}
		final File imageFile = new File(imagePath.getPath());
		
		if (this.uploadManager == null) {
			this.uploadManager = new UploadManager();
		}
		this.uploadManager.put(imageFile, new Date().getTime() + SharedPreferencesContext.getInstance().getToken(), imageToken, new UpCompletionHandler() {
			
			@Override
			public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
				if (responseInfo.isOK()) {
					try {
						String key = (String) jsonObject.get("key");
						imageUrl = GGConst.QINIU_URL + key;
						Log.e("uploadImage", imageUrl);
						if (!TextUtils.isEmpty(imageUrl)) {
							Picasso.with(mContext).load(imageFile).into(asyncImageView);
							LoadDialog.dismiss(mContext);
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
	
	private void hintKbTwo() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive() && getCurrentFocus() != null) {
			if (getCurrentFocus().getWindowToken() != null) {
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}
}
