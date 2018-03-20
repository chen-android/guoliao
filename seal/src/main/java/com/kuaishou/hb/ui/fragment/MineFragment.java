package com.kuaishou.hb.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.kuaishou.hb.GGConst;
import com.kuaishou.hb.R;
import com.kuaishou.hb.SealUserInfoManager;
import com.kuaishou.hb.server.BaseAction;
import com.kuaishou.hb.server.SealAction;
import com.kuaishou.hb.server.broadcast.BroadcastManager;
import com.kuaishou.hb.server.network.async.AsyncTaskManager;
import com.kuaishou.hb.server.network.async.OnDataListener;
import com.kuaishou.hb.server.network.http.HttpException;
import com.kuaishou.hb.server.response.BaseResponse;
import com.kuaishou.hb.server.response.ConfigResponse;
import com.kuaishou.hb.server.widget.SelectableRoundedImageView;
import com.kuaishou.hb.ui.activity.AccountSettingActivity;
import com.kuaishou.hb.ui.activity.FeedBackActivity;
import com.kuaishou.hb.ui.activity.ImageReviewActivity;
import com.kuaishou.hb.ui.activity.MyAccountActivity;
import com.kuaishou.hb.ui.activity.MyWalletActivity;
import com.kuaishou.hb.ui.activity.ShareWebActivity;
import com.kuaishou.hb.ui.widget.BuyVideoDialog;
import com.kuaishou.hb.ui.widget.NewPayPwdDialog;
import com.squareup.picasso.Picasso;

import io.rong.imkit.RongIM;
import io.rong.imkit.utilities.PromptPopupDialog;
import io.rong.imlib.model.CSCustomServiceInfo;
import io.rong.imlib.model.UserInfo;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * Created by AMing on 16/6/21.
 * Company RongCloud
 */
public class MineFragment extends Fragment implements View.OnClickListener {
	private static final int GET_CONFIG = 54;
	private static final int BUY_VIDEO = 23;
	public static final String SHOW_RED = "SHOW_RED";
	private SharedPreferences sp;
	private SelectableRoundedImageView imageView;
	private TextView mName;
	private TextView mId;
	private TextView videoLimitTv;
	private boolean isDebug;
//	private SendMessageToWX.Req req;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.seal_mine_fragment, container, false);
		isDebug = getContext().getSharedPreferences("config", Context.MODE_PRIVATE).getBoolean("isDebug", false);
		initViews(mView);
		initData();
		BroadcastManager.getInstance(getActivity()).addAction(GGConst.CHANGEINFO, new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				updateUserInfo();
			}
		});
		return mView;
	}
	
	private void requestConfig() {
		AsyncTaskManager.getInstance(getActivity()).request(GET_CONFIG, new OnDataListener() {
			@Override
			public Object doInBackground(int requestCode, String parameter) throws HttpException {
				return new SealAction(getActivity()).getConfig();
			}
			
			@Override
			public void onSuccess(int requestCode, Object result) {
				if (result != null) {
					ConfigResponse c = (ConfigResponse) result;
					BuyVideoDialog dialog = BuyVideoDialog.Companion.getInstance(c.getData().getVideoprice());
					dialog.setOnConfirmListener(new Function2<Double, Integer, Unit>() {
						@Override
						public Unit invoke(Double price, final Integer count) {
							NewPayPwdDialog dialog1 = NewPayPwdDialog.Companion.getInstance(price);
							dialog1.setMInputCompletedListener(new Function1<String, Unit>() {
								@Override
								public Unit invoke(final String s) {
									AsyncTaskManager.getInstance(getActivity()).request(BUY_VIDEO, new OnDataListener() {
										@Override
										public Object doInBackground(int requestCode, String parameter) throws HttpException {
											return new SealAction(getActivity()).buyVideoLimit(count, s);
										}
										
										@Override
										public void onSuccess(int requestCode, Object result) {
											if (result != null) {
												BaseResponse br = ((BaseResponse) result);
												if (br.getCode() == 200) {
													ToastUtils.showShort("购买成功");
													int oldNum = sp.getInt(GGConst.GUOGUO_LOGIN_VIDEOLIMIT, 0);
													int newNum = oldNum + count;
													sp.edit().putInt(GGConst.GUOGUO_LOGIN_VIDEOLIMIT, newNum).apply();
													videoLimitTv.setText(getString(R.string.video_account, newNum));
												} else {
													ToastUtils.showShort(br.getMessage());
												}
											}
										}
										
										@Override
										public void onFailure(int requestCode, int state, Object result) {
										
										}
									});
									return null;
								}
							});
							dialog1.show(getActivity().getSupportFragmentManager(), "pay_pwd_dialog");
							return null;
						}
					});
					dialog.show(getActivity().getSupportFragmentManager(), "buy_video_dialog");
				}
			}
			
			@Override
			public void onFailure(int requestCode, int state, Object result) {
			
			}
		});
	}

	private void initData() {
		sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
		updateUserInfo();
	}

	private void initViews(View mView) {
		imageView = (SelectableRoundedImageView) mView.findViewById(R.id.mine_header);
		mId = (TextView) mView.findViewById(R.id.mine_userid);
		mName = (TextView) mView.findViewById(R.id.mine_name);
		videoLimitTv = (TextView) mView.findViewById(R.id.mine_video_limit_tv);
		LinearLayout mUserProfile = (LinearLayout) mView.findViewById(R.id.start_user_profile);
		LinearLayout mMineSetting = (LinearLayout) mView.findViewById(R.id.mine_setting);
		LinearLayout mMineService = (LinearLayout) mView.findViewById(R.id.mine_service);
		LinearLayout mMineShare = (LinearLayout) mView.findViewById(R.id.my_share);
		LinearLayout mMineUrl = (LinearLayout) mView.findViewById(R.id.mine_url);
		LinearLayout mMineXN = (LinearLayout) mView.findViewById(R.id.mine_xiaoneng);
		RelativeLayout mMineBuyVideo = (RelativeLayout) mView.findViewById(R.id.mine_buy_video);
		if (isDebug) {
			mMineXN.setVisibility(View.VISIBLE);
		} else {
			mMineXN.setVisibility(View.GONE);
		}
		mUserProfile.setOnClickListener(this);
		mMineSetting.setOnClickListener(this);
		mMineService.setOnClickListener(this);
		mMineXN.setOnClickListener(this);
		mMineUrl.setOnClickListener(this);
		mView.findViewById(R.id.my_wallet).setOnClickListener(this);
		mMineShare.setOnClickListener(this);
		mMineBuyVideo.setOnClickListener(this);
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
				break;
			case R.id.mine_url:
				PromptPopupDialog urlDialog = PromptPopupDialog.newInstance(getActivity(), "", GGConst.HOME_URL, "复制");
				urlDialog.setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
					@Override
					public void onPositiveButtonClicked() {
						ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
						cmb.setText(GGConst.HOME_URL);
						ToastUtils.showShort("已复制到剪贴板");
					}
				});
				urlDialog.show();
				break;
			case R.id.mine_buy_video:
				requestConfig();
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
		int videoLimit = sp.getInt(GGConst.GUOGUO_LOGIN_VIDEOLIMIT, 0);
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
			
			videoLimitTv.setText(getString(R.string.video_account, videoLimit));
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
