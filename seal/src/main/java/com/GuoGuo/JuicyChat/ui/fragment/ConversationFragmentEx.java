package com.GuoGuo.JuicyChat.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.GuoGuo.JuicyChat.model.GGRedPacketNotifyMessage;
import com.GuoGuo.JuicyChat.ui.activity.ReadReceiptDetailActivity;
import com.GuoGuo.JuicyChat.utils.SharedPreferencesContext;
import com.blankj.utilcode.util.EmptyUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.adapter.MessageListAdapter;
import io.rong.imlib.model.Conversation;

/**
 * 会话 Fragment 继承自ConversationFragment
 * onResendItemClick: 重发按钮点击事件. 如果返回 false,走默认流程,如果返回 true,走自定义流程
 * onReadReceiptStateClick: 已读回执详情的点击事件.
 * 如果不需要重写 onResendItemClick 和 onReadReceiptStateClick ,可以不必定义此类,直接集成 ConversationFragment 就可以了
 * Created by Yuejunhong on 2016/10/10.
 */
public class ConversationFragmentEx extends ConversationFragment {
    private boolean hasReflect = false;
    
    @Override
    public void onResendItemClick(io.rong.imlib.model.Message message) {
    
    }
    
    @Override
    public void onReadReceiptStateClick(io.rong.imlib.model.Message message) {
        if (message.getConversationType() == Conversation.ConversationType.GROUP) { //目前只适配了群组会话
            Intent intent = new Intent(getActivity(), ReadReceiptDetailActivity.class);
            intent.putExtra("message", message);
            getActivity().startActivity(intent);
        }
    }
    
    @Override
    public void onWarningDialog(String msg) {
        String typeStr = getUri().getLastPathSegment();
        if (!typeStr.equals("chatroom")) {
            super.onWarningDialog(msg);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (!hasReflect) {
            //反射替换系统的messageAdapter  拦截不要的消息
            MessageListAdapter messageListAdapter = new MessageListAdapter(getActivity()) {
                @Override
                public void add(UIMessage uiMessage) {
                    boolean show = false;
                    if (uiMessage.getContent() instanceof GGRedPacketNotifyMessage) {
                        GGRedPacketNotifyMessage content = (GGRedPacketNotifyMessage) uiMessage.getContent();
                        String showuserids = content.getShowuserids();
                        if (EmptyUtils.isNotEmpty(showuserids)) {
                            String[] ids = showuserids.split(",");
                            for (String id : ids) {
                                if (id.equals(SharedPreferencesContext.getInstance().getUserId())) {
                                    show = true;
                                }
                            }
                        }
                        if (SharedPreferencesContext.getInstance().getUserId().equals(content.getTouserid())) {
                            show = true;
                        }
                        if (!show) {
                            return;
                        }
                    }
                    super.add(uiMessage);
                }
                
                @Override
                public void add(UIMessage uiMessage, int position) {
                    boolean show = false;
                    if (uiMessage.getContent() instanceof GGRedPacketNotifyMessage) {
                        GGRedPacketNotifyMessage content = (GGRedPacketNotifyMessage) uiMessage.getContent();
                        String showuserids = content.getShowuserids();
                        if (EmptyUtils.isNotEmpty(showuserids)) {
                            String[] ids = showuserids.split(",");
                            for (String id : ids) {
                                if (id.equals(SharedPreferencesContext.getInstance().getUserId())) {
                                    show = true;
                                }
                            }
                        }
                        if (SharedPreferencesContext.getInstance().getUserId().equals(content.getTouserid())) {
                            show = true;
                        }
                        if (!show) {
                            return;
                        }
                    }
                    
                    super.add(uiMessage, position);
                }
                
                @Override
                public void addCollection(Collection<UIMessage> collection) {
                    Collection<UIMessage> list = new ArrayList<>();
                    for (UIMessage message : collection) {
                        boolean show = false;
                        if (message.getContent() instanceof GGRedPacketNotifyMessage) {
                            GGRedPacketNotifyMessage content = (GGRedPacketNotifyMessage) message.getContent();
                            String showuserids = content.getShowuserids();
                            if (EmptyUtils.isNotEmpty(showuserids)) {
                                String[] ids = showuserids.split(",");
                                for (String id : ids) {
                                    if (id.equals(SharedPreferencesContext.getInstance().getUserId())) {
                                        show = true;
                                    }
                                }
                            }
                            if (SharedPreferencesContext.getInstance().getUserId().equals(content.getTouserid())) {
                                show = true;
                            }
                            if (!show) {
                                return;
                            }
                        }
                    }
                    super.addCollection(list);
                }
                
                @Override
                public void addCollection(UIMessage... collection) {
                    Collection<UIMessage> list = new ArrayList<>();
                    for (UIMessage message : collection) {
                        boolean show = false;
                        if (message.getContent() instanceof GGRedPacketNotifyMessage) {
                            GGRedPacketNotifyMessage content = (GGRedPacketNotifyMessage) message.getContent();
                            String showuserids = content.getShowuserids();
                            if (EmptyUtils.isNotEmpty(showuserids)) {
                                String[] ids = showuserids.split(",");
                                for (String id : ids) {
                                    if (id.equals(SharedPreferencesContext.getInstance().getUserId())) {
                                        show = true;
                                    }
                                }
                            }
                            if (SharedPreferencesContext.getInstance().getUserId().equals(content.getTouserid())) {
                                show = true;
                            }
                            if (!show) {
                                return;
                            }
                        }
                    }
                    super.addCollection(list);
                }
            };
            try {
                Field field1 = this.getClass().getSuperclass().getDeclaredField("mListAdapter");
                field1.setAccessible(true);
                field1.set(this, messageListAdapter);
                
                Field field = this.getClass().getSuperclass().getDeclaredField("mList");
                field.setAccessible(true);
                Object o = field.get(this);
                Method setAdapter = o.getClass().getMethod("setAdapter", ListAdapter.class);
                setAdapter.invoke(o, messageListAdapter);
                hasReflect = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return view;
    }
}
