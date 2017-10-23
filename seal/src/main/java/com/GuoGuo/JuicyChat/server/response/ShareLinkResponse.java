package com.GuoGuo.JuicyChat.server.response;

/**
 * Created by chenshuai12619 on 2017-10-23.
 */

public class ShareLinkResponse {
    private int code;
    private String message;
    private ShareLinkData data;
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public ShareLinkData getData() {
        return data;
    }
    
    public void setData(ShareLinkData data) {
        this.data = data;
    }
    
    public static class ShareLinkData {
        private String linkUrl;
        private String imgUrl;
        private String title;
        private String content;
        
        public String getLinkUrl() {
            return linkUrl;
        }
        
        public void setLinkUrl(String linkUrl) {
            this.linkUrl = linkUrl;
        }
        
        public String getImgUrl() {
            return imgUrl;
        }
        
        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
}
