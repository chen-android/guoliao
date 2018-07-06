package com.kuaishou.hb.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.bj.paysdk.utils.TrPay;
import com.kuaishou.hb.R;
import com.kuaishou.hb.db.Friend;
import com.kuaishou.hb.server.HomeWatcherReceiver;
import com.kuaishou.hb.server.SealAction;
import com.kuaishou.hb.server.broadcast.BroadcastManager;
import com.kuaishou.hb.server.event.UpdateFriendDeal;
import com.kuaishou.hb.server.network.async.AsyncTaskManager;
import com.kuaishou.hb.server.network.async.OnDataListener;
import com.kuaishou.hb.server.network.http.HttpException;
import com.kuaishou.hb.server.response.GetFriendListResponse;
import com.kuaishou.hb.server.response.GetVersionResponse;
import com.kuaishou.hb.server.utils.NToast;
import com.kuaishou.hb.server.widget.LoadDialog;
import com.kuaishou.hb.ui.adapter.ConversationListAdapterEx;
import com.kuaishou.hb.ui.fragment.ContactsFragment;
import com.kuaishou.hb.ui.fragment.DiscoverFragment;
import com.kuaishou.hb.ui.fragment.MineFragment;
import com.kuaishou.hb.ui.widget.DragPointView;
import com.kuaishou.hb.ui.widget.MorePopWindow;

import java.util.ArrayList;
import java.util.List;

import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.ContactNotificationMessage;

public class MainActivity extends FragmentActivity implements
		ViewPager.OnPageChangeListener,
		View.OnClickListener,
		DragPointView.OnDragListencer,
		IUnReadMessageObserver {

	public static ViewPager mViewPager;
	private List<Fragment> mFragment = new ArrayList<>();
	private ImageView moreImage, mImageChats, mImageContact, mImageFind, mImageMe, mMineRed;
	private TextView mTextChats, mTextContact, mTextFind, mTextMe;
	private DragPointView mUnreadNumView, mTextContactNum;
	private ImageView mSearchImageView;
	private static final int REQUEST_VERSION = 77;
	private static final int REQUEST_UNDEALNUM = 688;
	private int unDealNum = 0;
	/**
	 * 会话列表的fragment
	 */
	private ConversationListFragment mConversationListFragment = null;
	private boolean isDebug;
	private Context mContext;
	private Conversation.ConversationType[] mConversationsTypes = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		EventBus.getDefault().register(this);
		TrPay.getInstance(this).initPaySdk("43ef569ab3d9445ab63db92633d86535", "local");
		mContext = this;
		isDebug = getSharedPreferences("config", MODE_PRIVATE).getBoolean("isDebug", false);
		initPermission();
		initViews();
		changeTextViewColor();
		changeSelectedTabState(0);
		initMainViewPager();
		registerHomeKeyReceiver(this);
		AsyncTaskManager.getInstance(mContext).request(REQUEST_VERSION, onDataListener);
		AsyncTaskManager.getInstance(mContext).request(REQUEST_UNDEALNUM, onDataListener);
	}

	private OnDataListener onDataListener = new OnDataListener() {
		@Override
		public Object doInBackground(int requestCode, String parameter) throws HttpException {
			switch (requestCode) {
				case REQUEST_VERSION:
					return new SealAction(mContext).getVersion();
				case REQUEST_UNDEALNUM:
					return new SealAction(mContext).getAllUserRelationship();
				default:
					break;
			}
			return doInBackground(requestCode, parameter);

		}

		@Override
		public void onSuccess(int requestCode, Object result) {
			if (requestCode == REQUEST_VERSION) {
				if (result != null) {
					GetVersionResponse response = (GetVersionResponse) result;
					if (response.getCode() == 200) {
						final GetVersionResponse.GetVersionData data = response.getData();
						if (data != null) {
							if (data.getUpdateState() == 1) {
								new AlertDialog.Builder(mContext).setTitle("升级提示").setMessage("发现新的版本" + data.getAndroid_version())
										.setPositiveButton("去下载", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												if (!TextUtils.isEmpty(data.getUpdateUrl())) {
													Uri uri = Uri.parse(data.getUpdateUrl());
													Intent it = new Intent(Intent.ACTION_VIEW, uri);
													startActivity(it);
												} else {
													NToast.shortToast(mContext, "获取下载地址失败");
												}
											}
										})
										.setNegativeButton("取消", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {

											}
										}).show();
							} else if (data.getUpdateState() == 2) {
								new AlertDialog.Builder(mContext).setTitle("升级提示").setMessage("发现新的版本" + data.getAndroid_version())
										.setPositiveButton("去下载", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												if (!TextUtils.isEmpty(data.getUpdateUrl())) {
													Uri uri = Uri.parse(data.getUpdateUrl());
													Intent it = new Intent(Intent.ACTION_VIEW, uri);
													startActivity(it);
												} else {
													NToast.shortToast(mContext, "获取下载地址失败");
												}
											}
										}).setCancelable(false).show();
							}
						}
					}
				}
			} else if (requestCode == REQUEST_UNDEALNUM) {
				GetFriendListResponse response = (GetFriendListResponse) result;
				List<Friend> data = response.getData();
				if (data != null && data.size() > 0) {
					int n = 0;
					for (Friend datum : data) {
						if (datum.getState() == 2) {
							n++;
						}
					}
					unDealNum = n;
					if (n > 0) {
						mTextContactNum.setVisibility(View.VISIBLE);
						mTextContactNum.setText(n + "");
						EventBus.getDefault().post(new UpdateFriendDeal(UpdateFriendDeal.UpdateAction.NUM, n));
					} else {
						mTextContactNum.setVisibility(View.GONE);
					}
				} else {
					mTextContactNum.setVisibility(View.GONE);
				}
			}
		}

		@Override
		public void onFailure(int requestCode, int state, Object result) {

		}
	};

	private void initPermission() {
		if (Build.VERSION.SDK_INT >= 23) {
			String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
			ActivityCompat.requestPermissions(this, mPermissionList, 123);
		}
	}

	private void initViews() {
		RelativeLayout chatRLayout = (RelativeLayout) findViewById(R.id.seal_chat);
		RelativeLayout contactRLayout = (RelativeLayout) findViewById(R.id.seal_contact_list);
		RelativeLayout foundRLayout = (RelativeLayout) findViewById(R.id.seal_find);
		RelativeLayout mineRLayout = (RelativeLayout) findViewById(R.id.seal_me);
		mImageChats = (ImageView) findViewById(R.id.tab_img_chats);
		mImageContact = (ImageView) findViewById(R.id.tab_img_contact);
		mImageFind = (ImageView) findViewById(R.id.tab_img_find);
		mImageMe = (ImageView) findViewById(R.id.tab_img_me);
		mTextChats = (TextView) findViewById(R.id.tab_text_chats);
		mTextContact = (TextView) findViewById(R.id.tab_text_contact);
		mTextContactNum = (DragPointView) findViewById(R.id.tab_num_contact);
		mTextFind = (TextView) findViewById(R.id.tab_text_find);
		mTextMe = (TextView) findViewById(R.id.tab_text_me);
		mMineRed = (ImageView) findViewById(R.id.mine_red);
		moreImage = (ImageView) findViewById(R.id.seal_more);
		mSearchImageView = (ImageView) findViewById(R.id.ac_iv_search);

		chatRLayout.setOnClickListener(this);
		contactRLayout.setOnClickListener(this);
		foundRLayout.setOnClickListener(this);
		mineRLayout.setOnClickListener(this);
		moreImage.setOnClickListener(this);
		mSearchImageView.setOnClickListener(this);
		BroadcastManager.getInstance(mContext).addAction(MineFragment.SHOW_RED, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				mMineRed.setVisibility(View.VISIBLE);
			}
		});
	}

	public void onEventMainThread(UpdateFriendDeal deal) {
		if (deal.getAction() == UpdateFriendDeal.UpdateAction.ADD) {
			unDealNum++;
			mTextContactNum.setVisibility(View.VISIBLE);
			mTextContactNum.setText(unDealNum > 99 ? "99" : unDealNum + "");
		} else if (deal.getAction() == UpdateFriendDeal.UpdateAction.REDUCE) {
			unDealNum--;
			if (unDealNum <= 0) {
				unDealNum = 0;
				mTextContactNum.setVisibility(View.GONE);
			} else {
				mTextContactNum.setVisibility(View.VISIBLE);
				mTextContactNum.setText(unDealNum + "");
			}
		} else if (deal.getAction() == UpdateFriendDeal.UpdateAction.NUM) {
			unDealNum = deal.getNum();
			if (unDealNum > 0) {
				mTextContactNum.setVisibility(View.VISIBLE);
				mTextContactNum.setText(unDealNum > 99 ? "99" : unDealNum + "");
			} else {
				unDealNum = 0;
				mTextContactNum.setVisibility(View.GONE);
			}
		}
	}

	private void initMainViewPager() {
		Fragment conversationList = initConversationList();
		mViewPager = (ViewPager) findViewById(R.id.main_viewpager);

		mUnreadNumView = (DragPointView) findViewById(R.id.seal_num);
		mUnreadNumView.setOnClickListener(this);
		mUnreadNumView.setDragListencer(this);

		mFragment.add(conversationList);
		mFragment.add(new ContactsFragment());
		mFragment.add(new DiscoverFragment());
		mFragment.add(new MineFragment());
		FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public Fragment getItem(int position) {
				return mFragment.get(position);
			}

			@Override
			public int getCount() {
				return mFragment.size();
			}
		};
		mViewPager.setAdapter(fragmentPagerAdapter);
		mViewPager.setOffscreenPageLimit(4);
		mViewPager.setOnPageChangeListener(this);
		initData();
	}


	private Fragment initConversationList() {
		if (mConversationListFragment == null) {
			ConversationListFragment listFragment = new ConversationListFragment();
			listFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
			Uri uri;
			if (isDebug) {
				uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
						.appendPath("conversationlist")
						.appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "true") //设置私聊会话是否聚合显示
						.appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//群组
						.appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
						.appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
						.appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//系统
						.appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "true")
						.build();
				mConversationsTypes = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
						Conversation.ConversationType.GROUP,
						Conversation.ConversationType.PUBLIC_SERVICE,
						Conversation.ConversationType.APP_PUBLIC_SERVICE,
						Conversation.ConversationType.SYSTEM,
						Conversation.ConversationType.DISCUSSION
				};

			} else {
				uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
						.appendPath("conversationlist")
						.appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
						.appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
						.appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
						.appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
						.appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//系统
						.build();
				mConversationsTypes = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
						Conversation.ConversationType.GROUP,
						Conversation.ConversationType.PUBLIC_SERVICE,
						Conversation.ConversationType.APP_PUBLIC_SERVICE,
						Conversation.ConversationType.SYSTEM
				};
			}
			listFragment.setUri(uri);
			mConversationListFragment = listFragment;
			return listFragment;
		} else {
			return mConversationListFragment;
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		changeTextViewColor();
		changeSelectedTabState(position);
	}

	private void changeTextViewColor() {
		mImageChats.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_chat));
		mImageContact.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_contacts));
		mImageFind.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_found));
		mImageMe.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_me));
		mTextChats.setTextColor(Color.parseColor("#abadbb"));
		mTextContact.setTextColor(Color.parseColor("#abadbb"));
		mTextFind.setTextColor(Color.parseColor("#abadbb"));
		mTextMe.setTextColor(Color.parseColor("#abadbb"));
	}

	private void changeSelectedTabState(int position) {
		switch (position) {
			case 0:
				mTextChats.setTextColor(Color.parseColor("#0099ff"));
				mImageChats.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_chat_hover));
				break;
			case 1:
				mTextContact.setTextColor(Color.parseColor("#0099ff"));
				mImageContact.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_contacts_hover));
				break;
			case 2:
				mTextFind.setTextColor(Color.parseColor("#0099ff"));
				mImageFind.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_found_hover));
				break;
			case 3:
				mTextMe.setTextColor(Color.parseColor("#0099ff"));
				mImageMe.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_me_hover));
				break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}


	long firstClick = 0;
	long secondClick = 0;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.seal_chat:
				if (mViewPager.getCurrentItem() == 0) {
					if (firstClick == 0) {
						firstClick = System.currentTimeMillis();
					} else {
						secondClick = System.currentTimeMillis();
					}
					RLog.i("MainActivity", "time = " + (secondClick - firstClick));
					if (secondClick - firstClick > 0 && secondClick - firstClick <= 800) {
						mConversationListFragment.focusUnreadItem();
						firstClick = 0;
						secondClick = 0;
					} else if (firstClick != 0 && secondClick != 0) {
						firstClick = 0;
						secondClick = 0;
					}
				}
				mViewPager.setCurrentItem(0, false);
				break;
			case R.id.seal_contact_list:
				mViewPager.setCurrentItem(1, false);
				break;
			case R.id.seal_find:
				mViewPager.setCurrentItem(2, false);
				break;
			case R.id.seal_me:
				mViewPager.setCurrentItem(3, false);
				mMineRed.setVisibility(View.GONE);
				break;
			case R.id.seal_more:
				MorePopWindow morePopWindow = new MorePopWindow(MainActivity.this);
				morePopWindow.showPopupWindow(moreImage);
				break;
			case R.id.ac_iv_search:
				startActivity(new Intent(MainActivity.this, SealSearchActivity.class));
				break;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getBooleanExtra("systemconversation", false)) {
			mViewPager.setCurrentItem(0, false);
		}
	}

	protected void initData() {

		final Conversation.ConversationType[] conversationTypes = {
				Conversation.ConversationType.PRIVATE,
				Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,
				Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE
		};

		RongIM.getInstance().addUnReadMessageCountChangedObserver(this, conversationTypes);
		getConversationPush();// 获取 push 的 id 和 target
		getPushMessage();
	}

	private void getConversationPush() {
		if (getIntent() != null && getIntent().hasExtra("PUSH_CONVERSATIONTYPE") && getIntent().hasExtra("PUSH_TARGETID")) {

			final String conversationType = getIntent().getStringExtra("PUSH_CONVERSATIONTYPE");
			final String targetId = getIntent().getStringExtra("PUSH_TARGETID");


			RongIM.getInstance().getConversation(Conversation.ConversationType.valueOf(conversationType), targetId, new RongIMClient.ResultCallback<Conversation>() {
				@Override
				public void onSuccess(Conversation conversation) {

					if (conversation != null) {

						if (conversation.getLatestMessage() instanceof ContactNotificationMessage) { //好友消息的push
							startActivity(new Intent(MainActivity.this, NewFriendListActivity.class));
						} else {
							Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation")
									.appendPath(conversationType).appendQueryParameter("targetId", targetId).build();
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(uri);
							startActivity(intent);
						}
					}
				}

				@Override
				public void onError(RongIMClient.ErrorCode e) {

				}
			});
		}
	}

	/**
	 * 得到不落地 push 消息
	 */
	private void getPushMessage() {
		Intent intent = getIntent();
		if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {
			String path = intent.getData().getPath();
			if (path.contains("push_message")) {
				SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
				String cacheToken = sharedPreferences.getString("loginToken", "");
				if (TextUtils.isEmpty(cacheToken)) {
					startActivity(new Intent(MainActivity.this, LoginActivity.class));
				} else {
					if (!RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
						LoadDialog.show(mContext);
						RongIM.connect(cacheToken, new RongIMClient.ConnectCallback() {
							@Override
							public void onTokenIncorrect() {
								LoadDialog.dismiss(mContext);
							}

							@Override
							public void onSuccess(String s) {
								LoadDialog.dismiss(mContext);
							}

							@Override
							public void onError(RongIMClient.ErrorCode e) {
								LoadDialog.dismiss(mContext);
							}
						});
					}
				}
			}
		}
	}

	@Override
	public void onCountChanged(int count) {
		if (count == 0) {
			mUnreadNumView.setVisibility(View.GONE);
		} else if (count > 0 && count < 100) {
			mUnreadNumView.setVisibility(View.VISIBLE);
			mUnreadNumView.setText(String.valueOf(count));
		} else {
			mUnreadNumView.setVisibility(View.VISIBLE);
			mUnreadNumView.setText(R.string.no_read_message);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	private void hintKbTwo() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive() && getCurrentFocus() != null) {
			if (getCurrentFocus().getWindowToken() != null) {
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (null != this.getCurrentFocus()) {
			InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		RongIM.getInstance().removeUnReadMessageCountChangedObserver(this);
		if (mHomeKeyReceiver != null) {
			this.unregisterReceiver(mHomeKeyReceiver);
		}
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void onDragOut() {
		mUnreadNumView.setVisibility(View.GONE);
		NToast.shortToast(mContext, getString(R.string.clear_success));
		RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
			@Override
			public void onSuccess(List<Conversation> conversations) {
				if (conversations != null && conversations.size() > 0) {
					for (Conversation c : conversations) {
						RongIM.getInstance().clearMessagesUnreadStatus(c.getConversationType(), c.getTargetId(), null);
					}
				}
			}

			@Override
			public void onError(RongIMClient.ErrorCode e) {

			}
		}, mConversationsTypes);

	}

	private HomeWatcherReceiver mHomeKeyReceiver = null;

	//如果遇见 Android 7.0 系统切换到后台回来无效的情况 把下面注册广播相关代码注释或者删除即可解决。下面广播重写 home 键是为了解决三星 note3 按 home 键花屏的一个问题
	private void registerHomeKeyReceiver(Context context) {
		if (mHomeKeyReceiver == null) {
			mHomeKeyReceiver = new HomeWatcherReceiver();
			final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
			try {
				context.registerReceiver(mHomeKeyReceiver, homeFilter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
