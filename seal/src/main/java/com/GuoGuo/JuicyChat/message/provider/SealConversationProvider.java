package com.GuoGuo.JuicyChat.message.provider;

import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;

import com.GuoGuo.JuicyChat.model.GGRedPacketNotifyMessage;
import com.GuoGuo.JuicyChat.utils.SharedPreferencesContext;
import com.blankj.utilcode.util.EmptyUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.widget.provider.PrivateConversationProvider;
import io.rong.imlib.model.MessageContent;

/**
 * Created by chenshuai12619 on 2017-08-31.
 */
@ConversationProviderTag(conversationType = "group")
public class SealConversationProvider extends PrivateConversationProvider {
    private static Map<String, Spannable> lastData = new HashMap<>();
    
    public void bindView(View view, int position, UIConversation data) {
    
        MessageContent messageContent = data.getMessageContent();
        if (messageContent instanceof GGRedPacketNotifyMessage) {
            GGRedPacketNotifyMessage messageContent1 = (GGRedPacketNotifyMessage) messageContent;
            String showuserids = messageContent1.getShowuserids();
            if (EmptyUtils.isNotEmpty(showuserids)) {
                String[] ids = showuserids.split(",");
                if (Arrays.asList(ids).contains(SharedPreferencesContext.getInstance().getUserId())) {
                    lastData.put(data.getConversationTargetId(), new SpannableString(messageContent1.getTipmessage()));
                }
            } else if (SharedPreferencesContext.getInstance().getUserId().equals(messageContent1.getTouserid())) {
                lastData.put(data.getConversationTargetId(), data.getConversationContent());
            }
        } else {
            lastData.put(data.getConversationTargetId(), data.getConversationContent());
        }
    
        if (lastData.containsKey(data.getConversationTargetId())) {
            data.setConversationContent(lastData.get(data.getConversationTargetId()));
        } else {
            data.setConversationContent(new SpannableString(""));
        }
        super.bindView(view, position, data);
    }
    
    @Override
    public String getTitle(String groupId) {
        String name;
        if (RongUserInfoManager.getInstance().getGroupInfo(groupId) == null) {
            name = "";
        } else {
            name = RongUserInfoManager.getInstance().getGroupInfo(groupId).getName();
        }
        
        return name;
    }
    
    @Override
    public Uri getPortraitUri(String id) {
        Uri uri;
        if (RongUserInfoManager.getInstance().getGroupInfo(id) == null) {
            uri = null;
        } else {
            uri = RongUserInfoManager.getInstance().getGroupInfo(id).getPortraitUri();
        }
        return uri;
    }
}
