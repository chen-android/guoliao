package com.GuoGuo.JuicyChat.ui.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.GuoGuo.JuicyChat.App;
import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.server.event.ShareMsg;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.ShareLinkResponse;
import com.GuoGuo.JuicyChat.server.utils.NToast;
import com.GuoGuo.JuicyChat.server.widget.LoadDialog;
import com.GuoGuo.JuicyChat.utils.SealJavaScriptInterface;
import com.blankj.utilcode.util.ConvertUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.rong.eventbus.EventBus;

/**
 *
 */
public class ShareWebActivity extends BaseActivity {
    private static final int REQUEST_LINK = 386;
    private static final int REQUEST_IMG = 993;
    private WebView wv;
    private String url;
    private SendMessageToWX.Req req;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_web);
        EventBus.getDefault().register(this);
        setTitle("分享赚果币");
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
    }
    
    public void onEventMainThread(ShareMsg msg) {
        LoadDialog.show(this);
        if (msg.getType() == 1) {
            request(REQUEST_LINK);
        } else if (msg.getType() == 2) {
            request(REQUEST_IMG);
        }
    }
    
    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        if (requestCode == REQUEST_LINK) {
            return action.getShareLink();
        } else if (requestCode == REQUEST_IMG) {
            return action.getShareUrl();
        }
        return super.doInBackground(requestCode, id);
    }
    
    @Override
    public void onSuccess(int requestCode, Object result) {
        LoadDialog.dismiss(ShareWebActivity.this);
        if (requestCode == REQUEST_IMG) {
            String url = (String) result;
            if (!TextUtils.isEmpty(url)) {
                
                Picasso.with(ShareWebActivity.this).load(url).into(new Target() {
                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                        LoadDialog.dismiss(ShareWebActivity.this);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
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
                                
                                req.transaction = "shareImgTimeLine";
                                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                                
                                App.instance.getIwxapi().sendReq(req);
                            }
                        }).start();
                        
                    }
                    
                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        LoadDialog.dismiss(ShareWebActivity.this);
                    }
                    
                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        
                    }
                });
            } else {
                NToast.shortToast(ShareWebActivity.this, "获取分享链接失败");
            }
        } else if (requestCode == REQUEST_LINK) {
            final ShareLinkResponse rep = (ShareLinkResponse) result;
            if (rep.getCode() == 200) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ShareLinkResponse.ShareLinkData data = rep.getData();
                        WXWebpageObject webpage = new WXWebpageObject();
                        webpage.webpageUrl = data.getLinkUrl();
                        WXMediaMessage msg = new WXMediaMessage(webpage);
                        msg.title = data.getTitle();
                        msg.description = data.getContent();
                        try {
                            msg.thumbData = ConvertUtils.bitmap2Bytes(Picasso.with(ShareWebActivity.this).load(data.getImgUrl()).get(), Bitmap.CompressFormat.PNG);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        req = new SendMessageToWX.Req();
                        req.message = msg;
                        req.transaction = "shareImgSession";
                        req.scene = SendMessageToWX.Req.WXSceneSession;
                        App.instance.getIwxapi().sendReq(req);
                    }
                }).start();
                
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
}
