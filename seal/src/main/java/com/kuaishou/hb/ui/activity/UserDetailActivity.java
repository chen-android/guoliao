package com.kuaishou.hb.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kuaishou.hb.App;
import com.kuaishou.hb.GGConst;
import com.kuaishou.hb.R;
import com.kuaishou.hb.SealAppContext;
import com.kuaishou.hb.SealUserInfoManager;
import com.kuaishou.hb.db.Friend;
import com.kuaishou.hb.server.broadcast.BroadcastManager;
import com.kuaishou.hb.server.network.http.HttpException;
import com.kuaishou.hb.server.response.GetFriendInfoByIDResponse;
import com.kuaishou.hb.server.response.GetUserInfoByIdResponse;
import com.kuaishou.hb.server.utils.ColorPhrase;
import com.kuaishou.hb.server.utils.NToast;
import com.kuaishou.hb.ui.AddFriendConfirmActivity;
import com.kuaishou.hb.ui.widget.SinglePopWindow;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

import io.rong.callkit.RongCallAction;
import io.rong.callkit.RongVoIPIntent;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallSession;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class UserDetailActivity extends BaseActivity implements View.OnClickListener {

	private static final int SYNC_FRIEND_INFO = 129;
	private ImageView mUserPortrait;
	private TextView mUserNickName;
	private TextView mUserDisplayName;
	private TextView mUserPhone;
	private TextView mUserLineStatus;
	private TextView mWhatsup;
	private LinearLayout mChatButtonGroupLinearLayout;
	private Button mAddFriendButton;
	private Button mRechargeBt;
	private LinearLayout mNoteNameLinearLayout;

	private static final int SYN_USER_INFO = 10087;
	private Friend mFriend;
	private String addMessage;
	private String mGroupName;
	private String mPhoneString;
	private int conversationType;
	private boolean mIsFriendsRelationship;
//	private MyImageLoaderTarget mMyImageLoaderTarget;

	private int mType;
	private static final int CLICK_CONVERSATION_USER_PORTRAIT = 1;
	private static final int CLICK_CONTACT_FRAGMENT_FRIEND = 2;


	private UserDetailActivityHandler mHandler = new UserDetailActivityHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_detail);
		initView();
		initData();
		initBlackListStatusView();
	}

	private void initView() {
		setTitle(R.string.user_details);
		mUserNickName = (TextView) findViewById(R.id.contact_below);
		mUserDisplayName = (TextView) findViewById(R.id.contact_top);
		mUserPhone = (TextView) findViewById(R.id.contact_phone);
		mUserLineStatus = (TextView) findViewById(R.id.user_online_status);
		mUserPortrait = (ImageView) findViewById(R.id.ac_iv_user_portrait);
		mWhatsup = (TextView) findViewById(R.id.user_detail_whatsup_tv);
		mChatButtonGroupLinearLayout = (LinearLayout) findViewById(R.id.ac_ll_chat_button_group);
		mAddFriendButton = (Button) findViewById(R.id.ac_bt_add_friend);
		mNoteNameLinearLayout = (LinearLayout) findViewById(R.id.ac_ll_note_name);
		mRechargeBt = (Button) findViewById(R.id.user_detail_recharge_bt);

		mAddFriendButton.setOnClickListener(this);
//		mUserPhone.setOnClickListener(this);
	}

	private void initData() {
		mType = getIntent().getIntExtra("type", 0);
		conversationType = getIntent().getIntExtra("conversationType", 0);
		if (mType == CLICK_CONVERSATION_USER_PORTRAIT) {
			SealAppContext.getInstance().pushActivity(this);
		}
		mGroupName = getIntent().getStringExtra("groupName");
		mFriend = getIntent().getParcelableExtra("friend");

		if (mFriend != null) {
//			mUserNickName.setVisibility(View.VISIBLE);
//			mUserNickName.setText(getString(R.string.ac_contact_nick_name) + " " + mFriend.getNickname());
			mUserDisplayName.setText(getDisplayNameWithId(SealUserInfoManager.getInstance().getDiaplayName(mFriend)));
			mWhatsup.setText(TextUtils.isEmpty(mFriend.getWhatsup()) ? "暂未设置" : mFriend.getWhatsup());
			final String portraitUri = mFriend.getHeadico();
//			ImageLoader.getInstance().displayImage(portraitUri, mUserPortrait, App.getOptions());
//			mMyImageLoaderTarget = new MyImageLoaderTarget();
			Picasso.with(UserDetailActivity.this).load(Uri.parse(portraitUri)).into(mUserPortrait);
			mUserPortrait.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(UserDetailActivity.this, ImageReviewActivity.class);
					intent.putExtra("bitmapUrl", Uri.parse(portraitUri));
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivity(intent);
					overridePendingTransition(0, 0);
				}
			});
		}
		if (getSharedPreferences("config", MODE_PRIVATE).getBoolean("isDebug", false)) {
//            RongIMClient.getInstance().getUserOnlineStatus(mFriend.getUserId(), new IRongCallback.IGetUserOnlineStatusCallback() {
//                @Override
//                public void onSuccess(final ArrayList<UserOnlineStatusInfo> userOnlineStatusInfoList) {
//                    if (userOnlineStatusInfoList != null) {
//                        if (userOnlineStatusInfoList.size() > 1) {
//                            Message message = mHandler.obtainMessage();
//                            message.arg1 = 0;
//                            mHandler.sendMessage(message);
//                        } else if (userOnlineStatusInfoList.size() == 1) {
//                            Message message = mHandler.obtainMessage();
//                            message.arg1 = userOnlineStatusInfoList.get(0).getPlatform().getValue();
//                            mHandler.sendMessage(message);
//                        }
//                    } else {
//                        Message message = mHandler.obtainMessage();
//                        message.arg1 = 5;
//                        mHandler.sendMessage(message);
//                    }
//                }
//
//                @Override
//                public void onError(int errorCode) {
//
//                }
//            });
		}
		syncPersonalInfo();

		if (!TextUtils.isEmpty(mFriend.getFriendid())) {
			String mySelf = getSharedPreferences("config", MODE_PRIVATE).getString(GGConst.GUOGUO_LOGIN_ID, "");
			if (mySelf.equals(mFriend.getFriendid())) {
				mChatButtonGroupLinearLayout.setVisibility(View.GONE);
				mAddFriendButton.setVisibility(View.GONE);
				mNoteNameLinearLayout.setVisibility(View.GONE);
				mRechargeBt.setVisibility(View.GONE);
				return;
			}
			if (mIsFriendsRelationship) {
				mChatButtonGroupLinearLayout.setVisibility(View.VISIBLE);
				mAddFriendButton.setVisibility(View.GONE);
			} else {
				mAddFriendButton.setVisibility(View.VISIBLE);
				mChatButtonGroupLinearLayout.setVisibility(View.GONE);
				mNoteNameLinearLayout.setVisibility(View.GONE);
				if (conversationType == Conversation.ConversationType.CHATROOM.getValue()) {
					mAddFriendButton.setVisibility(View.GONE);
					mRechargeBt.setVisibility(View.GONE);
				}
			}
		}
	}

//	private class MyImageLoaderTarget implements Target {
//
//		@Override
//		public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//			mUserPortrait.setImageBitmap(bitmap);
//			mUserPortrait.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(UserDetailActivity.this, ImageReviewActivity.class);
//					intent.putExtra("bitmap", bitmap);
//					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//					startActivity(intent);
//					overridePendingTransition(0, 0);
//				}
//			});
//		}
//
//		@Override
//		public void onBitmapFailed(Drawable errorDrawable) {
//
//		}
//
//		@Override
//		public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//		}
//	}


	private void syncPersonalInfo() {
		mIsFriendsRelationship = SealUserInfoManager.getInstance().isFriendsRelationship(mFriend.getFriendid());
		if (mIsFriendsRelationship) {
			String userId = mFriend.getFriendid();
			mFriend = SealUserInfoManager.getInstance().getFriendByID(userId);
			request(SYNC_FRIEND_INFO, true);
		} else {
			request(SYN_USER_INFO, true);
		}
	}

	private void initBlackListStatusView() {
		if (mIsFriendsRelationship) {
			Button rightButton = getHeadRightButton();
			rightButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_activity_contact_more));
			rightButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					RongIM.getInstance().getBlacklistStatus(mFriend.getFriendid(), new RongIMClient.ResultCallback<RongIMClient.BlacklistStatus>() {
						@Override
						public void onSuccess(RongIMClient.BlacklistStatus blacklistStatus) {
							SinglePopWindow morePopWindow = new SinglePopWindow(UserDetailActivity.this, mFriend, blacklistStatus);
							morePopWindow.setListener(new SinglePopWindow.OnDeleteFriendListener() {
								@Override
								public void onDelete() {
									BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.UPDATE_FRIEND);
									RongIM.getInstance().removeConversation(Conversation.ConversationType.PRIVATE, mFriend.getFriendid(), null);
									BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.DELETE_FRIEND, mFriend.getFriendid(), "no");
									finish();
								}
							});
							morePopWindow.showPopupWindow(v);
						}

						@Override
						public void onError(RongIMClient.ErrorCode e) {

						}
					});
				}
			});
		}
	}

	public void startChat(View view) {
		RongIM.getInstance().startPrivateChat(mContext, mFriend.getFriendid(), SealUserInfoManager.getInstance().getDiaplayName(mFriend));
		finish();
	}

	public void startRecharge(View view) {
		Intent intent = new Intent(this, TransferActivity.class);
		intent.putExtra("friend", mFriend);
		startActivity(intent);
	}

	//CallKit start 2
	public void startVoice(View view) {
		RongCallSession profile = RongCallClient.getInstance().getCallSession();
		if (profile != null && profile.getActiveTime() > 0) {
			Toast.makeText(mContext, getString(io.rong.callkit.R.string.rc_voip_call_audio_start_fail), Toast.LENGTH_SHORT).show();
			return;
		}
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
			Toast.makeText(mContext, getString(io.rong.callkit.R.string.rc_voip_call_network_error), Toast.LENGTH_SHORT).show();
			return;
		}

		Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO);
		intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE.getName().toLowerCase());
		intent.putExtra("targetId", mFriend.getFriendid());
		intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setPackage(getPackageName());
		getApplicationContext().startActivity(intent);
	}

	public void startVideo(View view) {
		RongCallSession profile = RongCallClient.getInstance().getCallSession();
		if (profile != null && profile.getActiveTime() > 0) {
			Toast.makeText(mContext, getString(io.rong.callkit.R.string.rc_voip_call_audio_start_fail), Toast.LENGTH_SHORT).show();
			return;
		}
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
			Toast.makeText(mContext, getString(io.rong.callkit.R.string.rc_voip_call_network_error), Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEVIDEO);
		intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE.getName().toLowerCase());
		intent.putExtra("targetId", mFriend.getMyid());
		intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setPackage(getPackageName());
		getApplicationContext().startActivity(intent);
	}
	//CallKit end 2

	public void finishPage(View view) {
		this.finish();
	}

	public void setDisplayName(View view) {
		Intent intent = new Intent(mContext, NoteInformationActivity.class);
		intent.putExtra("friend", mFriend);
		startActivityForResult(intent, 99);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ac_bt_add_friend:
				startActivity(new Intent(this, AddFriendConfirmActivity.class)
						.putExtra("friendId", mFriend.getId()));
				break;
			case R.id.contact_phone:
				if (!TextUtils.isEmpty(mPhoneString)) {
					Uri telUri = Uri.parse("tel:" + mPhoneString);
					Intent intent = new Intent(Intent.ACTION_DIAL, telUri);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
				break;
			default:
				break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 155 && data != null) {
			String displayName = data.getStringExtra("displayName");
			if (!TextUtils.isEmpty(displayName)) {
				mUserDisplayName.setText(getDisplayNameWithId(displayName));
				mFriend.setRemark(displayName);
			} else {
				mUserDisplayName.setText(getDisplayNameWithId(mFriend.getNickname()));
				mFriend.setRemark("");
			}
		}
	}

	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		switch (requestCode) {
			case SYN_USER_INFO:
				return action.getUserInfoById(mFriend.getFriendid());
			case SYNC_FRIEND_INFO:
				return action.getFriendInfoByID(mFriend.getFriendid());
		}
		return super.doInBackground(requestCode, id);
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (result != null) {
			switch (requestCode) {
				case SYN_USER_INFO:
					GetUserInfoByIdResponse userInfoByIdResponse = (GetUserInfoByIdResponse) result;
					if (userInfoByIdResponse.getCode() == 200 && userInfoByIdResponse.getData() != null &&
							mFriend.getFriendid().equals(userInfoByIdResponse.getData().getUserid())) {
						String nickName = userInfoByIdResponse.getData().getNickname();
						String portraitUri = userInfoByIdResponse.getData().getHeadico();
//						if (hasNickNameChanged(nickName) || hasPortraitUriChanged(portraitUri)) {
//							if (hasNickNameChanged(nickName)) {
//								mUserNickName.setText(nickName);
//							}
//							if (hasPortraitUriChanged(portraitUri)) {
//								ImageLoader.getInstance().displayImage(portraitUri, mUserPortrait, App.getOptions());
//							} else {
//								portraitUri = mFriend.getHeadico();
//							}
//
//							UserInfo userInfo = new UserInfo(userInfoByIdResponse.getData().getId(), nickName, Uri.parse(portraitUri));
//							RongIM.getInstance().refreshUserInfoCache(userInfo);
//						}
//						mUserNickName.setText(nickName);
						mUserDisplayName.setText(nickName);
//						mUserPhone.setText("账号：" + userInfoByIdResponse.getData().getAccount());
						ImageLoader.getInstance().displayImage(portraitUri, mUserPortrait, App.getOptions());
					} else {
						NToast.shortToast(mContext, "同步信息失败");
					}
					break;
				case SYNC_FRIEND_INFO:
					GetFriendInfoByIDResponse friendInfoByIDResponse = (GetFriendInfoByIDResponse) result;
					if (friendInfoByIDResponse.getCode() == 200) {
//						mUserPhone.setVisibility(View.VISIBLE);
						mPhoneString = friendInfoByIDResponse.getData().getUsername();
						String text = "账号：" + friendInfoByIDResponse.getData().getUsername();
//						mUserPhone.setText(text);

						Friend friend = friendInfoByIDResponse.getData();
						if (mFriend.getFriendid().equals(friend.getFriendid())) {
							if (hasFriendInfoChanged(friend)) {
								String nickName = friend.getNickname();
								String portraitUri = friend.getHeadico();
								//当前app server返回的displayName为空,先不使用
								String displayName = friend.getRemark();
								//如果没有设置头像,好友数据库的头像地址和用户信息提供者的头像处理不一致,这个不一致是seal app代码处理的问题,未来应该矫正回来
								String userInfoPortraitUri = mFriend.getHeadico();
								//更新UI
								//if (TextUtils.isEmpty(displayName) && hasDisplayNameChanged(displayName)) {
								if (!TextUtils.isEmpty(nickName)) {
//									mUserNickName.setVisibility(View.VISIBLE);
//									mUserNickName.setText(getString(R.string.ac_contact_nick_name) + " " + nickName);
									mUserDisplayName.setText(getDisplayNameWithId(SealUserInfoManager.getInstance().getDiaplayName(friend)));
								}
								if (hasPortraitUriChanged(portraitUri)) {
									ImageLoader.getInstance().displayImage(portraitUri, mUserPortrait, App.getOptions());
									userInfoPortraitUri = portraitUri;
								}
								SealUserInfoManager.getInstance().addFriend(friend);
								//更新好友列表
								BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.UPDATE_FRIEND);
								//更新用户信息提供者
								UserInfo newUserInfo = new UserInfo(friend.getFriendid(),
										SealUserInfoManager.getInstance().getDiaplayName(friend),
										Uri.parse(userInfoPortraitUri));
								RongIM.getInstance().refreshUserInfoCache(newUserInfo);
								mFriend = friend;
							}
						}
					} else {
						NToast.shortToast(mContext, "同步信息失败");
					}
					break;
			}
		}
	}

	private CharSequence getDisplayNameWithId(String displayName) {
		String tt = displayName + "  {(ID" + mFriend.getFriendid() + ")}";
		return ColorPhrase.from(tt).innerColor(Color.GRAY).format();
	}

	private boolean hasNickNameChanged(String nickName) {
		if (mFriend.getUsername() == null) {
			return nickName != null;
		} else {
			return !mFriend.getUsername().equals(nickName);
		}
	}

	private boolean hasPortraitUriChanged(String portraitUri) {
		if (mFriend.getHeadico() == null) {
			return portraitUri != null;
		} else {
			if (mFriend.getHeadico().equals(portraitUri)) {
				return false;
			} else {
				return !TextUtils.isEmpty(portraitUri);
			}
		}
	}

	private boolean hasDisplayNameChanged(String displayName) {
		if (mFriend.getRemark() == null) {
			return displayName != null;
		} else {
			return !mFriend.getRemark().equals(displayName);
		}
	}

	private boolean hasFriendInfoChanged(Friend resultEntity) {
		String nickName = resultEntity.getNickname();
		String portraitUri = resultEntity.getHeadico();
		String displayName = resultEntity.getRemark();
		return hasNickNameChanged(nickName) ||
				hasPortraitUriChanged(portraitUri) ||
				hasDisplayNameChanged(displayName);
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {

	}

	@Override
	public void onBackPressed() {
		if (mType == CLICK_CONVERSATION_USER_PORTRAIT) {
			SealAppContext.getInstance().popActivity(this);
		}
		super.onBackPressed();
	}

	@Override
	public void onHeadLeftButtonClick(View v) {
		if (mType == CLICK_CONVERSATION_USER_PORTRAIT) {
			SealAppContext.getInstance().popActivity(this);
		}
		super.onHeadLeftButtonClick(v);
	}

	private static class UserDetailActivityHandler extends Handler {
		private final WeakReference<UserDetailActivity> mActivity;

		public UserDetailActivityHandler(UserDetailActivity activity) {
			mActivity = new WeakReference<UserDetailActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg != null) {
				UserDetailActivity activity = mActivity.get();
				if (activity != null) {
					activity.mUserLineStatus.setVisibility(View.VISIBLE);
					switch (msg.arg1) {
						case 0:
						case 4:
							activity.mUserLineStatus.setText(R.string.pc_online);
							activity.mUserLineStatus.setTextColor(Color.parseColor("#60E23F"));
							break; //PC
						case 1:
						case 2:
							activity.mUserLineStatus.setText(R.string.phone_online);
							activity.mUserLineStatus.setTextColor(Color.parseColor("#60E23F"));
							break; //phone
						case 3:
							activity.mUserLineStatus.setText(R.string.pc_online);
							activity.mUserLineStatus.setTextColor(Color.parseColor("#60E23F"));
							break; //web
						case 5:
							activity.mUserLineStatus.setTextColor(Color.parseColor("#666666"));
							activity.mUserLineStatus.setText(R.string.offline);
							break; // offline
					}
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		mHandler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}
}
