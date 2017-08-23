package com.GuoGuo.JuicyChat.message.module;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.GuoGuo.JuicyChat.SealUserInfoManager;
import com.GuoGuo.JuicyChat.db.Friend;
import com.GuoGuo.JuicyChat.model.RedPackPlugin;
import com.GuoGuo.JuicyChat.server.pinyin.CharacterParser;
import com.GuoGuo.JuicyChat.server.utils.RongGenerate;
import com.GuoGuo.JuicyChat.ui.activity.UserDetailActivity;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.contactcard.ContactCardPlugin;
import cn.rongcloud.contactcard.IContactCardClickCallback;
import cn.rongcloud.contactcard.IContactCardInfoProvider;
import cn.rongcloud.contactcard.message.ContactMessage;
import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.RongExtension;
import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;


public class SealExtensionModule extends DefaultExtensionModule {
	private IContactCardInfoProvider.IContactCardInfoCallback mIContactCardInfoCallback;
	
	@Override
	public void onInit(String appKey) {
		super.onInit(appKey);
		ContactCardPlugin.init();
		ContactCardPlugin.getInstance().setContactCardInfoProvider(new IContactCardInfoProvider() {
			@Override
			public void getContactCardInfoProvider(IContactCardInfoCallback contactInfoCallback) {
				mIContactCardInfoCallback = contactInfoCallback;
				SealUserInfoManager.getInstance().getFriends(new SealUserInfoManager.ResultCallback<List<Friend>>() {
					@Override
					public void onSuccess(List<Friend> friendList) {
						List<UserInfo> userInfos = new ArrayList<>();
						for (Friend friend : friendList) {
							UserInfo info = new UserInfo(friend.getFriendid(), SealUserInfoManager.getInstance().getDiaplayName(friend), Uri.parse(friend.getHeadico()));
							userInfos.add(info);
						}
						mIContactCardInfoCallback.getContactCardInfoCallback(userInfos);
					}
					
					@Override
					public void onError(String errString) {
						mIContactCardInfoCallback.getContactCardInfoCallback(null);
					}
				});
			}
		});
		
		ContactCardPlugin.getInstance().setContactCardClickCallback(new IContactCardClickCallback() {
			@Override
			public void onContactCardMessageClick(View view, int position, ContactMessage content, UIMessage message) {
				Intent intent = new Intent(view.getContext(), UserDetailActivity.class);
				Friend friend = SealUserInfoManager.getInstance().getFriendByID(content.getId());
				if (friend == null) {
					UserInfo userInfo = new UserInfo(content.getId(), content.getName(), Uri.parse(TextUtils.isEmpty(content.getImgUrl()) ? RongGenerate.generateDefaultAvatar(content.getName(), content.getId()) : content.getImgUrl()));
					friend = CharacterParser.getInstance().generateFriendFromUserInfo(userInfo);
				}
				intent.putExtra("friend", friend);
				view.getContext().startActivity(intent);
			}
		});
	}
	
	@Override
	public void onAttachedToExtension(RongExtension extension) {
		super.onAttachedToExtension(extension);
		
		
	}
	
	@Override
	public void onDetachedFromExtension() {
		super.onDetachedFromExtension();
	}
	
	@Override
	public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {
		List<IPluginModule> pluginModules = super.getPluginModules(conversationType);
		//图片  位置  语音聊天  视频聊天  文件  语音输入  名片  红包
		pluginModules.remove(2);
		pluginModules.remove(2);
		if (conversationType.equals(Conversation.ConversationType.PRIVATE)
				|| conversationType.equals(Conversation.ConversationType.GROUP)) {
			pluginModules.add(ContactCardPlugin.getInstance());
			pluginModules.add(RedPackPlugin.getInstance());
		}
//	    if (conversationType.equals(Conversation.ConversationType.PRIVATE)) {
//	    }
		return pluginModules;
	}
	
	@Override
	public List<IEmoticonTab> getEmoticonTabs() {
		return super.getEmoticonTabs();
	}
}
