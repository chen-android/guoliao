package com.GuoGuo.JuicyChat.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.ui.activity.SendRedPackGroupActivity;
import com.GuoGuo.JuicyChat.ui.activity.SendRedPackPersonalActivity;

import cn.rongcloud.contactcard.ContactCardPlugin;
import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

/**
 * Created by cs on 2017/5/12.
 */

public class RedPackPlugin implements IPluginModule {
	private String targetId;
	
	public static RedPackPlugin redPackPlugin;
	
	public static RedPackPlugin getInstance() {
		if (redPackPlugin == null) {
			synchronized (ContactCardPlugin.class) {
				if (redPackPlugin == null) {
					redPackPlugin = new RedPackPlugin();
				}
			}
		}
		return redPackPlugin;
	}
	
	@Override
	public Drawable obtainDrawable(Context context) {
		return ContextCompat.getDrawable(context, R.drawable.selector_hongbao);
	}
	
	@Override
	public String obtainTitle(Context context) {
		return "红包";
	}
	
	@Override
	public void onClick(Fragment fragment, RongExtension rongExtension) {
		this.targetId = rongExtension.getTargetId();
		if (rongExtension.getConversationType() == Conversation.ConversationType.GROUP) {
			Intent var3 = new Intent(rongExtension.getContext(), SendRedPackGroupActivity.class);
			var3.putExtra("TargetId", rongExtension.getTargetId());
			var3.putExtra("isGroup", true);
			rongExtension.startActivityForPluginResult(var3, 52, this);
		}
		if (rongExtension.getConversationType() == Conversation.ConversationType.PRIVATE) {
			Intent var3 = new Intent(rongExtension.getContext(), SendRedPackPersonalActivity.class);
			var3.putExtra("TargetId", rongExtension.getTargetId());
			rongExtension.startActivityForPluginResult(var3, 52, this);
		}
		if (rongExtension.getConversationType() == Conversation.ConversationType.CHATROOM) {
			Intent var3 = new Intent(rongExtension.getContext(), SendRedPackGroupActivity.class);
			var3.putExtra("TargetId", rongExtension.getTargetId());
			var3.putExtra("isChatRoom", true);
			rongExtension.startActivityForPluginResult(var3, 52, this);
		}
		
	}
	
	@Override
	public void onActivityResult(int i, int i1, Intent intent) {
		
	}
}
