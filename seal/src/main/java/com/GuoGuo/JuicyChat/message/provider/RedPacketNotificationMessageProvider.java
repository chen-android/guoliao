package com.GuoGuo.JuicyChat.message.provider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.model.GGRedPacketNotifyMessage;
import com.GuoGuo.JuicyChat.server.SealAction;
import com.GuoGuo.JuicyChat.server.network.async.AsyncTaskManager;
import com.GuoGuo.JuicyChat.server.network.async.OnDataListener;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.GetRedPacketDetailResponse;
import com.GuoGuo.JuicyChat.server.response.GetRedPacketUsersResponse;
import com.GuoGuo.JuicyChat.server.utils.ColorPhrase;
import com.GuoGuo.JuicyChat.server.widget.LoadDialog;
import com.GuoGuo.JuicyChat.ui.activity.RedPacketDetailActivity;
import com.GuoGuo.JuicyChat.utils.SharedPreferencesContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

/**
 * Created by Bob on 2015/4/17.
 * 如何自定义消息模板
 */
@ProviderTag(messageContent = GGRedPacketNotifyMessage.class, showPortrait = false, centerInHorizontal = true, showProgress = false, showSummaryWithName = false)
public class RedPacketNotificationMessageProvider extends IContainerItemProvider.MessageProvider<GGRedPacketNotifyMessage> implements OnDataListener {
    private static final int REQUEST_DETAIL = 1;
    private static final int REQUEST_MEMBERS = 2;
    private Context context;
    private String redPacketId;
    private SealAction action;
    private GetRedPacketDetailResponse.ResultEntity detailEntity;
    
    @Override
    public void bindView(View v, int position, GGRedPacketNotifyMessage content, UIMessage message) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();
        if (content != null) {
            if (!TextUtils.isEmpty(content.getShowuserids())) {
                List<String> ids = Arrays.asList(content.getShowuserids().split(","));
                if (ids.contains(SharedPreferencesContext.getInstance().getUserId())) {
                    viewHolder.contentTextView.setText(content.getTipmessage());
                    return;
                }
            }
            if (SharedPreferencesContext.getInstance().getUserId().equals(content.getTouserid())) {
                String message1 = content.getMessage();
                if (!TextUtils.isEmpty(message1)) {
                    if (content.getIslink() == 1) {
                        CharSequence format = ColorPhrase.from(message1 + " {点击查看}").innerColor(v.getResources().getColor(R.color.title_bar_color))
                                .outerColor(Color.WHITE)
                                .format();
                        viewHolder.contentTextView.setText(format);
                    } else {
                        viewHolder.contentTextView.setText(message1);
                    }
            
                }
            }
        }
    }
    
    @Override
    public Spannable getContentSummary(GGRedPacketNotifyMessage content) {
        if (content != null) {
            return new SpannableString(content.getMessage());
        }
        return null;
    }
    
    @Override
    public void onItemClick(View view, int position, GGRedPacketNotifyMessage
            content, UIMessage message) {
        if (content.getIslink() == 1) {
            LoadDialog.show(context);
            this.redPacketId = content.getRedpacketId();
            AsyncTaskManager.getInstance(context).request(REQUEST_DETAIL, this);
        }
    }
    
    @Override
    public void onItemLongClick(View view, int position, GGRedPacketNotifyMessage content, final UIMessage message) {
        
    }
    
    @Override
    public View newView(Context context, ViewGroup group) {
        this.context = context;
        action = new SealAction(context);
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_group_information_notification_message, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.contentTextView = (TextView) view.findViewById(R.id.rc_msg);
        viewHolder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        view.setTag(viewHolder);
        return view;
    }
    
    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        switch (requestCode) {
            case REQUEST_DETAIL:
                return action.getRedPacketDetail(redPacketId);
            case REQUEST_MEMBERS:
                return action.getRedPacketMenber(redPacketId);
        }
        return null;
    }
    
    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode) {
            case REQUEST_DETAIL:
                GetRedPacketDetailResponse packetDetailResponse = (GetRedPacketDetailResponse) result;
                if (packetDetailResponse.getCode() == 200) {
                    this.detailEntity = packetDetailResponse.getData();
                    AsyncTaskManager.getInstance(context).request(REQUEST_MEMBERS, this);
                }
                break;
            case REQUEST_MEMBERS:
                LoadDialog.dismiss(context);
                GetRedPacketUsersResponse usersResponse = (GetRedPacketUsersResponse) result;
                if (usersResponse.getCode() == 200) {
                    ArrayList<GetRedPacketUsersResponse.ResultEntity> data = usersResponse.getData();
                    Intent intent = new Intent(context, RedPacketDetailActivity.class);
                    intent.putExtra("id", Integer.valueOf(this.redPacketId));
                    intent.putExtra("detail", this.detailEntity);
                    intent.putExtra("members", data);
                    context.startActivity(intent);
                }
                break;
        }
    }
    
    @Override
    public void onFailure(int requestCode, int state, Object result) {
        
    }
    
    
    private static class ViewHolder {
        TextView contentTextView;
    }
}
