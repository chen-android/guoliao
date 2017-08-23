package com.GuoGuo.JuicyChat.message.provider;

import android.content.Context;
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
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.utils.json.JsonMananger;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.widget.provider.IContainerItemProvider;

/**
 * Created by Bob on 2015/4/17.
 * 如何自定义消息模板
 */
@ProviderTag(messageContent = GGRedPacketNotifyMessage.class, showPortrait = false, centerInHorizontal = true, showProgress = false, showSummaryWithName = false)
public class RedPacketNotificationMessageProvider extends IContainerItemProvider.MessageProvider<GGRedPacketNotifyMessage> {
	@Override
	public void bindView(View v, int position, GGRedPacketNotifyMessage content, UIMessage message) {
		ViewHolder viewHolder = (ViewHolder) v.getTag();
		if (content != null) {
			if (!TextUtils.isEmpty(content.getContent())) {
				ContentMessage bean = null;
				try {
					try {
						bean = JsonMananger.jsonToBean(content.getContent(), ContentMessage.class);
					} catch (HttpException e) {
						e.printStackTrace();
					}
				} finally {
					if (bean != null && !TextUtils.isEmpty(bean.getMessage()))
						viewHolder.contentTextView.setText(bean.getMessage());
				}
			}

		}
	}

	@Override
	public Spannable getContentSummary(GGRedPacketNotifyMessage content) {
		if (content != null && !TextUtils.isEmpty(content.getContent())) {
			ContentMessage bean = null;
			try {
				try {
					bean = JsonMananger.jsonToBean(content.getContent(), ContentMessage.class);
				} catch (HttpException e) {
					e.printStackTrace();
				}
			} finally {
				if (bean != null && !TextUtils.isEmpty(bean.getMessage())) {
					return new SpannableString(bean.getMessage());
				}
			}
		}
		return null;
	}

	@Override
	public void onItemClick(View view, int position, GGRedPacketNotifyMessage
			content, UIMessage message) {
	}

	@Override
	public void onItemLongClick(View view, int position, GGRedPacketNotifyMessage content, final UIMessage message) {
		String[] items;

		items = new String[]{view.getContext().getResources().getString(R.string.de_dialog_item_message_delete)};

		OptionsPopupDialog.newInstance(view.getContext(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
			@Override
			public void onOptionsItemClicked(int which) {
				if (which == 0)
					RongIM.getInstance().deleteMessages(new int[]{message.getMessageId()}, null);
			}
		}).show();
	}

	@Override
	public View newView(Context context, ViewGroup group) {
		View view = LayoutInflater.from(context).inflate(R.layout.rc_item_group_information_notification_message, null);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.contentTextView = (TextView) view.findViewById(R.id.rc_msg);
		viewHolder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
		view.setTag(viewHolder);

		return view;
	}


	private static class ViewHolder {
		TextView contentTextView;
	}

	public static class ContentMessage {
		private int redpacketId;
		private String message;

		public int getRedpacketId() {
			return redpacketId;
		}

		public void setRedpacketId(int redpacketId) {
			this.redpacketId = redpacketId;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
}
