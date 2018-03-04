package com.kuaishou.hb.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blankj.utilcode.util.ToastUtils;
import com.kuaishou.hb.R;
import com.kuaishou.hb.server.event.ShareMsg;
import com.kuaishou.hb.server.network.http.HttpException;
import com.kuaishou.hb.server.response.ShareLinkResponse;
import com.kuaishou.hb.server.widget.LoadDialog;
import com.kuaishou.hb.utils.SealJavaScriptInterface;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import io.rong.eventbus.EventBus;

/**
 *
 */
public class ShareWebActivity extends BaseActivity {
	private static final int REQUEST_LINK = 386;
	private static final int REQUEST_LINK_PUBLIC = 993;
	private WebView wv;
	private String url;
	private SendMessageToWX.Req req;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_web);
		EventBus.getDefault().register(this);
		setTitle("分享赚快豆");
		wv = (WebView) findViewById(R.id.share_wv);
		url = getIntent().getStringExtra("url");
		wv.addJavascriptInterface(new SealJavaScriptInterface(), "SharePage");
		WebSettings settings = wv.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setLoadWithOverviewMode(true);
		settings.setBuiltInZoomControls(true);
		if (Build.VERSION.SDK_INT > 11) {
			settings.setDisplayZoomControls(false);
		}
		settings.setSupportZoom(true);
		settings.setUseWideViewPort(true);
		wv.loadUrl(url);
		wv.setWebViewClient(new WebViewClient() {
			ProgressDialog progressDialog;

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {//网页页面开始加载的时候
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(ShareWebActivity.this);
					progressDialog.setMessage("Please wait...");
					progressDialog.show();
					wv.setEnabled(false);// 当加载网页的时候将网页进行隐藏
				}
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {//网页加载结束的时候
				//super.onPageFinished(view, url);
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
					progressDialog = null;
					wv.setEnabled(true);
				}
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) { //网页加载时的连接的网址
				view.loadUrl(url);
				return false;
			}
		});
	}

	public void onEventMainThread(ShareMsg msg) {
		LoadDialog.show(this);
		if (msg.getType() == 1) {
			request(REQUEST_LINK);
		} else if (msg.getType() == 2) {
			request(REQUEST_LINK_PUBLIC);
		}
	}

	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		if (requestCode == REQUEST_LINK) {
			return action.getShareLink();
		} else if (requestCode == REQUEST_LINK_PUBLIC) {
			return action.getShareLink();
		}
		return super.doInBackground(requestCode, id);
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		LoadDialog.dismiss(ShareWebActivity.this);
		if (requestCode == REQUEST_LINK_PUBLIC) {
			final ShareLinkResponse rep = (ShareLinkResponse) result;
			if (rep.getCode() == 200) {
				ShareLinkResponse.ShareLinkData data = rep.getData();
				UMImage thumb = new UMImage(this, data.getImgUrl());
				UMWeb web = new UMWeb(data.getLinkUrl());
				web.setThumb(thumb);
				web.setDescription(data.getContent());
				web.setTitle(data.getTitle());
				new ShareAction(this).withMedia(web)
						.setDisplayList(SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QZONE)
						.setCallback(new ShareListener())
						.open();
			}
		} else if (requestCode == REQUEST_LINK) {
			final ShareLinkResponse rep = (ShareLinkResponse) result;
			if (rep.getCode() == 200) {
				ShareLinkResponse.ShareLinkData data = rep.getData();
				UMImage thumb = new UMImage(this, data.getImgUrl());
				UMWeb web = new UMWeb(data.getLinkUrl());
				web.setThumb(thumb);
				web.setDescription(data.getContent());
				web.setTitle(data.getTitle());
				new ShareAction(this).withMedia(web)
						.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.QQ)
						.setCallback(new ShareListener())
						.open();
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (wv.canGoBack()) {
			wv.goBack();
			return;
		}
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	}

	private class ShareListener implements UMShareListener {

		@Override
		public void onStart(SHARE_MEDIA share_media) {

		}

		@Override
		public void onResult(SHARE_MEDIA share_media) {
			ToastUtils.showShort("分享成功");
		}

		@Override
		public void onError(SHARE_MEDIA share_media, Throwable throwable) {
			ToastUtils.showShort("分享失败");
		}

		@Override
		public void onCancel(SHARE_MEDIA share_media) {
			ToastUtils.showShort("分享取消");
		}
	}

}
