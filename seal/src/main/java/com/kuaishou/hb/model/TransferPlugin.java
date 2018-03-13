package com.kuaishou.hb.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.kuaishou.hb.R;
import com.kuaishou.hb.SealUserInfoManager;
import com.kuaishou.hb.ui.activity.TransferActivity;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

/**
 * Created by chenshuai12619 on 2017-11-06.
 */

public class TransferPlugin implements IPluginModule {

	public static TransferPlugin transferPlugin;

	public static TransferPlugin getInstance() {
		if (transferPlugin == null) {
			synchronized (TransferPlugin.class) {
				if (transferPlugin == null) {
					transferPlugin = new TransferPlugin();
				}
			}
		}
		return transferPlugin;
	}

	@Override
	public Drawable obtainDrawable(Context context) {
		return ContextCompat.getDrawable(context, R.drawable.selector_transfer_account);
	}

	@Override
	public String obtainTitle(Context context) {
		return "转账";
	}

	@Override
	public void onClick(Fragment fragment, RongExtension rongExtension) {
		if (rongExtension.getConversationType() == Conversation.ConversationType.PRIVATE) {
			Intent var3 = new Intent(rongExtension.getContext(), TransferActivity.class);
			var3.putExtra("friend", SealUserInfoManager.getInstance().getFriendByID(rongExtension.getTargetId()));
			rongExtension.startActivityForPluginResult(var3, 52, this);
		}
	}

	@Override
	public void onActivityResult(int i, int i1, Intent intent) {

	}
}
