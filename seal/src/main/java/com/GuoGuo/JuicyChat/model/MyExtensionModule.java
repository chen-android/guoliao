package com.GuoGuo.JuicyChat.model;

import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

/**
 * Created by cs on 2017/5/12.
 */

public class MyExtensionModule extends DefaultExtensionModule {
	@Override
	public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {
		List<IPluginModule> pluginModules = super.getPluginModules(conversationType);
		if (conversationType.equals(Conversation.ConversationType.PRIVATE)) {
			pluginModules.add(RedPackPlugin.getInstance());
		}
		
		return pluginModules;
	}
}
