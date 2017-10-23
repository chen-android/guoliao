package com.GuoGuo.JuicyChat.utils;

import android.webkit.JavascriptInterface;

import com.GuoGuo.JuicyChat.server.event.ShareMsg;

import io.rong.eventbus.EventBus;

/**
 * Created by chenshuai12619 on 2017-10-22.
 */

public class SealJavaScriptInterface {
    
    /**
     * 分享链接到微信好友
     */
    @JavascriptInterface
    public void Invite() {
        EventBus.getDefault().post(new ShareMsg(1));
    }
    
    /**
     * 分享到朋友圈
     */
    @JavascriptInterface
    public void Share() {
        EventBus.getDefault().post(new ShareMsg(2));
    }
}
