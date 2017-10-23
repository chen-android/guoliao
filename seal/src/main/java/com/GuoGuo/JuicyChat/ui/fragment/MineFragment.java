package com.GuoGuo.JuicyChat.ui.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.GGConst;
import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.SealUserInfoManager;
import com.GuoGuo.JuicyChat.server.BaseAction;
import com.GuoGuo.JuicyChat.server.broadcast.BroadcastManager;
import com.GuoGuo.JuicyChat.server.response.GetShareRewardResponse;
import com.GuoGuo.JuicyChat.server.widget.SelectableRoundedImageView;
import com.GuoGuo.JuicyChat.ui.activity.AccountSettingActivity;
import com.GuoGuo.JuicyChat.ui.activity.FeedBackActivity;
import com.GuoGuo.JuicyChat.ui.activity.ImageReviewActivity;
import com.GuoGuo.JuicyChat.ui.activity.MyAccountActivity;
import com.GuoGuo.JuicyChat.ui.activity.MyWalletActivity;
import com.GuoGuo.JuicyChat.ui.activity.ShareWebActivity;
import com.alibaba.fastjson.JSON;
import com.squareup.picasso.Picasso;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.CSCustomServiceInfo;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/6/21.
 * Company RongCloud
 */
public class MineFragment extends Fragment implements View.OnClickListener {
	private static final int COMPARE_VERSION = 54;
	public static final String SHOW_RED = "SHOW_RED";
	private SharedPreferences sp;
	private SelectableRoundedImageView imageView;
	private TextView mName;
	private TextView mId;
	private boolean isDebug;
	private SendMessageToWX.Req req;
//	private MyImageLoaderTarget mImageLoaderTarget;
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.seal_mine_fragment, container, false);
		isDebug = getContext().getSharedPreferences("config", getContext().MODE_PRIVATE).getBoolean("isDebug", false);
		initViews(mView);
		initData();
		BroadcastManager.getInstance(getActivity()).addAction(GGConst.CHANGEINFO, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				updateUserInfo();
			}
		});
		BroadcastManager.getInstance(getActivity()).addAction(GGConst.SHARE_REWARD, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				GetShareRewardResponse result = JSON.parseObject(intent.getStringExtra("result"), GetShareRewardResponse.class);
				if (result.getCode() == 200) {
					new AlertDialog.Builder(getActivity())
							.setMessage("分享成功！获得" + result.getData().getMoney() + "果币奖励！")
							.setPositiveButton("确认", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}).show();
				} else {
					new AlertDialog.Builder(getActivity())
							.setMessage(result.getMessage())
							.setPositiveButton("确认", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}).show();
				}
			}
		});
//		compareVersion();
		return mView;
	}

//	private void compareVersion() {
//		AsyncTaskManager.getInstance(getActivity()).request(COMPARE_VERSION, new OnDataListener() {
//			@Override
//			public Object doInBackground(int requestCode, String parameter) throws HttpException {
//				return new SealAction(getActivity()).getSealTalkVersion();
//			}
//
//			@Override
//			public void onSuccess(int requestCode, Object result) {
//				if (result != null) {
//					VersionResponse response = (VersionResponse) result;
//					String[] s = response.getAndroid().getVersion().split("\\.");
//					StringBuilder sb = new StringBuilder();
//					for (int i = 0; i < s.length; i++) {
//						sb.append(s[i]);
//					}
//
//					String[] s2 = GGConst.SEALTALKVERSION.split("\\.");
//					StringBuilder sb2 = new StringBuilder();
//					for (int i = 0; i < s2.length; i++) {
//						sb2.append(s2[i]);
//					}
//					if (Integer.parseInt(sb.toString()) > Integer.parseInt(sb2.toString())) {
//						mNewVersionView.setVisibility(View.VISIBLE);
//						url = response.getAndroid().getUrl();
//						isHasNewVersion = true;
//						BroadcastManager.getInstance(getActivity()).sendBroadcast(SHOW_RED);
//					}
//				}
//			}
//
//			@Override
//			public void onFailure(int requestCode, int state, Object result) {
//
//			}
//		});
//	}
	
	private void initData() {
		sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
		updateUserInfo();
	}
	
	private void initViews(View mView) {
		imageView = (SelectableRoundedImageView) mView.findViewById(R.id.mine_header);
		mId = (TextView) mView.findViewById(R.id.mine_userid);
		mName = (TextView) mView.findViewById(R.id.mine_name);
		LinearLayout mUserProfile = (LinearLayout) mView.findViewById(R.id.start_user_profile);
		LinearLayout mMineSetting = (LinearLayout) mView.findViewById(R.id.mine_setting);
		LinearLayout mMineService = (LinearLayout) mView.findViewById(R.id.mine_service);
		LinearLayout mMineShare = (LinearLayout) mView.findViewById(R.id.my_share);
		LinearLayout mMineXN = (LinearLayout) mView.findViewById(R.id.mine_xiaoneng);
		if (isDebug) {
			mMineXN.setVisibility(View.VISIBLE);
		} else {
			mMineXN.setVisibility(View.GONE);
		}
		mUserProfile.setOnClickListener(this);
		mMineSetting.setOnClickListener(this);
		mMineService.setOnClickListener(this);
		mMineXN.setOnClickListener(this);
		mView.findViewById(R.id.my_wallet).setOnClickListener(this);
		mMineShare.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.start_user_profile:
				startActivity(new Intent(getActivity(), MyAccountActivity.class));
				break;
			case R.id.mine_setting:
				startActivity(new Intent(getActivity(), AccountSettingActivity.class));
				break;
			case R.id.mine_service:
				startActivity(new Intent(getActivity(), FeedBackActivity.class));
				break;
			case R.id.mine_xiaoneng:
				CSCustomServiceInfo.Builder builder1 = new CSCustomServiceInfo.Builder();
				builder1.province("北京");
				builder1.city("北京");
				RongIM.getInstance().startCustomerServiceChat(getActivity(), "zf_1000_1481459114694", "在线客服", builder1.build());
				break;
			case R.id.my_wallet:
				startActivity(new Intent(getActivity(), MyWalletActivity.class));
				break;
			case R.id.my_share:
                Intent intent = new Intent(getActivity(), ShareWebActivity.class);
                intent.putExtra("url", BaseAction.getBaseUrl() + "/app/Share.aspx");
				startActivity(intent);
				/**
				AsyncTaskManager.getInstance(getContext()).request(1, new OnDataListener() {
					@Override
					public Object doInBackground(int requestCode, String parameter) throws HttpException {
						return new SealAction(getContext()).getShareUrl();
					}
					
					@Override
					public void onSuccess(int requestCode, Object result) {
						String url = (String) result;
						if (!TextUtils.isEmpty(url)) {
							
							LoadDialog.show(getActivity());
							Picasso.with(getContext()).load(url).into(new Target() {
								@Override
								public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
									LoadDialog.dismiss(getActivity());
									ByteArrayOutputStream out1 = new ByteArrayOutputStream();
									bitmap.compress(Bitmap.CompressFormat.PNG, 100, out1);
									WXImageObject object = new WXImageObject(out1.toByteArray());
									WXMediaMessage msg = new WXMediaMessage();
									msg.mediaObject = object;
									Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
									ByteArrayOutputStream out = new ByteArrayOutputStream();
									thumbBmp.compress(Bitmap.CompressFormat.PNG, 100, out);
									msg.thumbData = out.toByteArray();
									try {
										out1.close();
										thumbBmp.recycle();
										out.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
									
									req = new SendMessageToWX.Req();
									req.message = msg;
									new ShareDialog(getActivity(), new ShareDialog.OnItemClickListener() {
										@Override
										public void click(Dialog dialog, int index) {
											if (index == 0) {//发送给朋友
												req.transaction = "shareImgSession";
												req.scene = SendMessageToWX.Req.WXSceneSession;
											} else if (index == 1) {//发送给朋友圈
												req.transaction = "shareImgTimeLine";
												req.scene = SendMessageToWX.Req.WXSceneTimeline;
											}
											App.instance.getIwxapi().sendReq(req);
											dialog.dismiss();
										}
									}).show();
								}
								
								@Override
								public void onBitmapFailed(Drawable errorDrawable) {
									LoadDialog.dismiss(getActivity());
								}
								
								@Override
								public void onPrepareLoad(Drawable placeHolderDrawable) {
									
								}
							});
						} else {
							NToast.shortToast(getActivity(), "获取分享链接失败");
						}
					}
					
					@Override
					public void onFailure(int requestCode, int state, Object result) {
						
					}
				});
				 */
				break;
			default:
				break;
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private void updateUserInfo() {
		String userId = sp.getString(GGConst.GUOGUO_LOGIN_ID, "");
		String username = sp.getString(GGConst.GUOGUO_LOGIN_NAME, "");
		String userPortrait = sp.getString(GGConst.GUOGUO_LOGING_PORTRAIT, "");
		mName.setText(username);
		mId.setText("用户ID: " + userId);
		if (!TextUtils.isEmpty(userId)) {
			final String portraitUri = SealUserInfoManager.getInstance().getPortraitUri
					(new UserInfo(userId, username, Uri.parse(userPortrait)));
//			ImageLoader.getInstance().displayImage(portraitUri, imageView, App.getOptions());
//			mImageLoaderTarget = new MyImageLoaderTarget();
			Picasso.with(getActivity()).load(Uri.parse(portraitUri)).into(imageView);
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), ImageReviewActivity.class);
					intent.putExtra("bitmapUrl", Uri.parse(portraitUri));
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivity(intent);
					getActivity().overridePendingTransition(0, 0);
				}
			});
		}
	}

//	private class MyImageLoaderTarget implements Target {
//
//		@Override
//		public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//			imageView.setImageBitmap(bitmap);
//			imageView.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(getActivity(), ImageReviewActivity.class);
//					intent.putExtra("bitmap", bitmap);
//					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//					startActivity(intent);
//					getActivity().overridePendingTransition(0, 0);
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
}
