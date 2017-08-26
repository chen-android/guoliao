package com.GuoGuo.JuicyChat.server.response;

import java.util.List;

/**
 * Created by chenshuai12619 on 2017-08-26.
 */

public class ChatroomListResponse {
    private int code;
    private String message;
    private List<ChatroomData> data;
    
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
    
    public List<ChatroomData> getData() {
        return data;
    }
    
    public void setData(List<ChatroomData> data) {
        this.data = data;
    }
    
    public static class ChatroomData {
        private String id;
        private String name;
        private long limitmoney;
        private String createtime;
        private String headico;
        private String limitipstart;
        private String limitipend;
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public long getLimitmoney() {
            return limitmoney;
        }
        
        public void setLimitmoney(long limitmoney) {
            this.limitmoney = limitmoney;
        }
        
        public String getCreatetime() {
            return createtime;
        }
        
        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }
        
        public String getHeadico() {
            return headico;
        }
        
        public void setHeadico(String headico) {
            this.headico = headico;
        }
        
        public String getLimitipstart() {
            return limitipstart;
        }
        
        public void setLimitipstart(String limitipstart) {
            this.limitipstart = limitipstart;
        }
        
        public String getLimitipend() {
            return limitipend;
        }
        
        public void setLimitipend(String limitipend) {
            this.limitipend = limitipend;
        }
    }
}
