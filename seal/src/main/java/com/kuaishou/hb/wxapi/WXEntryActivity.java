package com.kuaishou.hb.wxapi;

import com.umeng.socialize.weixin.view.WXCallbackActivity;

public class WXEntryActivity extends WXCallbackActivity {
//	private static final int GETACCESS_TOKEN = 557;
//	private static final int LOGIN = 503;
//	private static final int GET_SHARE_REWARD = 723;
//	private static final String APP_SECRET = "76ac3b24ad657d11ba34160106457c6a";
//	private IWXAPI mWeixinAPI;
//	public static final String WEIXIN_APP_ID = "wx0da4cc3e5489d38e";
//	private static String unionid;
//	private String code;
//
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		mWeixinAPI = WXAPIFactory.createWXAPI(this, WEIXIN_APP_ID, true);
//		mWeixinAPI.handleIntent(this.getIntent(), this);
//	}
//
//	@Override
//	protected void onNewIntent(Intent intent) {
//		super.onNewIntent(intent);
//		setIntent(intent);
//		mWeixinAPI.handleIntent(intent, this);//必须调用此句话
//	}
//
//	//微信发送的请求将回调到onReq方法
//	@Override
//	public void onReq(BaseReq req) {
//	}
//
//	//发送到微信请求的响应结果
//	@Override
//	public void onResp(BaseResp resp) {
////		switch (resp.errCode) {
////			case BaseResp.ErrCode.ERR_OK:
////				if ("shareImgTimeLine".equals(resp.transaction)) {//分享朋友圈
////					request(GET_SHARE_REWARD);
////					return;
////				}
////				if ("shareImgSession".equals(resp.transaction)) {//分享给朋友
////					finish();
////				}
////				if (resp instanceof SendAuth.Resp) {//登陆
////					SendAuth.Resp sendResp = (SendAuth.Resp) resp;
////					if (sendResp != null) {
////						code = sendResp.code;
////						request(GETACCESS_TOKEN);
////					}
////				}
////				break;
////			case BaseResp.ErrCode.ERR_USER_CANCEL:
////				//发送取消
////				break;
////			case BaseResp.ErrCode.ERR_AUTH_DENIED:
////				//发送被拒绝
////				break;
////			default:
////				//发送返回
////				break;
////		}
//
//	}
//
//	/**
//	 * 获取openid accessToken值用于后期操作
//	 *
//	 * @param code 请求码
//	 */
//	private void getAccess_token(final String code) {
//		final String path = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
//				+ WEIXIN_APP_ID
//				+ "&secret="
//				+ APP_SECRET
//				+ "&code="
//				+ code
//				+ "&grant_type=authorization_code";
//
////		request(GETACCESS_TOKEN);
//		//网络请求，根据自己的请求方式
//		AsyncTaskManager.getInstance(this).request(1, new OnDataListener() {
//			@Override
//			public Object doInBackground(int requestCode, String parameter) throws HttpException {
//				return new SealAction(WXEntryActivity.this).getWxLoginAccessToken(path);
//			}
//
//			@Override
//			public void onSuccess(int requestCode, Object result) {
//				String unionid = (String) result;
//				if (!TextUtils.isEmpty(unionid)) {
//					Intent intent = new Intent(WXEntryActivity.this, RegisterActivity.class);
//					intent.putExtra("unionid", unionid);
//					startActivity(intent);
//					finish();
//				} else {
//					NToast.shortToast(WXEntryActivity.this, "微信授权失败");
//				}
//			}
//
//			@Override
//			public void onFailure(int requestCode, int state, Object result) {
//
//			}
//		});
//	}
//
//	@Override
//	public Object doInBackground(int requestCode, String id) throws HttpException {
//		switch (requestCode) {
//			case GETACCESS_TOKEN:
//				String path = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
//						+ WEIXIN_APP_ID
//						+ "&secret="
//						+ APP_SECRET
//						+ "&code="
//						+ code
//						+ "&grant_type=authorization_code";
//				return action.getWxLoginAccessToken(path);
//			case GET_SHARE_REWARD:
//				return action.getShareReward();
//			default:
//				break;
//		}
//
//		return super.doInBackground(requestCode, id);
//	}
//
//	@Override
//	public void onSuccess(int requestCode, Object result) {
//		switch (requestCode) {
//			case GETACCESS_TOKEN:
//				unionid = (String) result;
//
//				if (!TextUtils.isEmpty(unionid)) {
//					Intent intent = new Intent(WXEntryActivity.this, LoginActivity.class);
//					intent.putExtra("unionid", unionid);
//					startActivity(intent);
//					finish();
//				} else {
//					NToast.shortToast(WXEntryActivity.this, "微信授权失败");
//				}
//				break;
//			case GET_SHARE_REWARD:
//				BroadcastManager.getInstance(mContext).sendBroadcast(GGConst.SHARE_REWARD, result);
//				finish();
//				break;
//			default:
//				break;
//		}
//	}
//
//	/**
//	 * 获取微信的个人信息
//	 *
//	 * @param access_token
//	 * @param openid
//	 */
//	private void getUserMesg(final String access_token, final String openid) {
//		String path = "https://api.weixin.qq.com/sns/userinfo?access_token="
//				+ access_token
//				+ "&openid="
//				+ openid;
//	}
}
