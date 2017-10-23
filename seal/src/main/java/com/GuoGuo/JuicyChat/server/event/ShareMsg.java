package com.GuoGuo.JuicyChat.server.event;

/**
 * Created by chenshuai12619 on 2017-10-23.
 */

public class ShareMsg {
    //1 分享给朋友   2 分享到朋友圈
    private int type;
    
    public ShareMsg() {
    }
    
    public ShareMsg(int type) {
        this.type = type;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
}
