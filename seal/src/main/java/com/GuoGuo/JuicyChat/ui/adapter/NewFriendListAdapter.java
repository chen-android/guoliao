package com.GuoGuo.JuicyChat.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.App;
import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.db.Friend;
import com.GuoGuo.JuicyChat.server.widget.SelectableRoundedImageView;

import io.rong.imageloader.core.ImageLoader;

public class NewFriendListAdapter extends BaseAdapters {
	
	public NewFriendListAdapter(Context context) {
		super(context);
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.rs_ada_user_ship, parent, false);
			holder.mName = (TextView) convertView.findViewById(R.id.ship_name);
			holder.mMessage = (TextView) convertView.findViewById(R.id.ship_message);
			holder.mHead = (SelectableRoundedImageView) convertView.findViewById(R.id.new_header);
			holder.mState = (TextView) convertView.findViewById(R.id.ship_state);
			holder.mDel = (Button) convertView.findViewById(R.id.ship_del_bt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Friend bean = (Friend) dataSet.get(position);
		holder.mName.setText(bean.getNickname());
		String portraitUri = bean.getHeadico();
		ImageLoader.getInstance().displayImage(portraitUri, holder.mHead, App.getOptions());
		holder.mState.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnItemButtonClick != null) {
					mOnItemButtonClick.onButtonClick(position, v, bean.getState());
				}
			}
		});
		holder.mDel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnItemButtonClick != null) {
					mOnItemButtonClick.onButtonClick(position, v, 99);
				}
			}
		});
		
		switch (bean.getState()) {
			case 1://同意了好友请求
				holder.mState.setText("已同意");
				holder.mState.setBackgroundDrawable(null);
				break;
			case 2: //收到了好友邀请
				holder.mState.setText(R.string.agree);
				holder.mState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.de_add_friend_selector));
				break;
			case 3: // 发出了好友邀请
				holder.mState.setText(R.string.request);
				holder.mState.setBackgroundDrawable(null);
				break;
		}
		return convertView;
	}
	
	class ViewHolder {
		SelectableRoundedImageView mHead;
		TextView mName;
		TextView mState;
		//        TextView mtime;
		TextView mMessage;
		Button mDel;
	}
	
	OnItemButtonClick mOnItemButtonClick;
	
	
	public void setOnItemButtonClick(OnItemButtonClick onItemButtonClick) {
		this.mOnItemButtonClick = onItemButtonClick;
	}
	
	public interface OnItemButtonClick {
		boolean onButtonClick(int position, View view, int status);
		
	}
}
