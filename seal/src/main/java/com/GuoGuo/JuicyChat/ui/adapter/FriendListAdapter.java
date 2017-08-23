package com.GuoGuo.JuicyChat.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.App;
import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.SealUserInfoManager;
import com.GuoGuo.JuicyChat.db.Friend;
import com.GuoGuo.JuicyChat.server.widget.SelectableRoundedImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imageloader.core.ImageLoader;

/**
 * Created by AMing on 16/1/14.
 * Company RongCloud
 */
public class FriendListAdapter extends BaseAdapter {
	
	private Context context;
	
	private List<Friend> list;
	
	private Map<String, Integer> map = new HashMap<>();
	
	public FriendListAdapter(Context context, List<Friend> list) {
		this.context = context;
		this.list = list;
		setSectionsFromList(list);
	}
	
	
	/**
	 * 传入新的数据 刷新UI的方法
	 */
	public void updateListView(List<Friend> list) {
		this.list = list;
		setSectionsFromList(list);
		notifyDataSetChanged();
	}
	
	private void setSectionsFromList(List<Friend> friends) {
		map.clear();
		for (int i = 0; i < friends.size(); i++) {
			Friend f = friends.get(i);
			if (!map.containsKey(f.getLetter())) {
				map.put(f.getLetter(), i);
			}
		}
	}
	
	@Override
	public int getCount() {
		if (list != null) return list.size();
		return 0;
	}
	
	@Override
	public Friend getItem(int position) {
		if (list == null)
			return null;
		
		if (position >= list.size())
			return null;
		
		return list.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		final Friend mContent = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.friend_item, parent, false);
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.friendname);
			viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
			viewHolder.mImageView = (SelectableRoundedImageView) convertView.findViewById(R.id.frienduri);
			viewHolder.tvUserId = (TextView) convertView.findViewById(R.id.friend_id);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (position == map.get(mContent.getLetter())) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getLetter());
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		viewHolder.tvTitle.setText(SealUserInfoManager.getInstance().getDiaplayName(this.list.get(position)));
		
		String portraitUri = mContent.getHeadico();
		ImageLoader.getInstance().displayImage(portraitUri, viewHolder.mImageView, App.getOptions());
		if (context.getSharedPreferences("config", Context.MODE_PRIVATE).getBoolean("isDebug", false)) {
			viewHolder.tvUserId.setVisibility(View.VISIBLE);
			viewHolder.tvUserId.setText(list.get(position).getMyid());
		}
		return convertView;
	}
	
	public int getPositionForSection(String ch) {
		if (map.containsKey(ch)) {
			return map.get(ch);
		}
		return -1;
	}
	
	public int getSectionForPosition(int position) {
		return 0;
	}
	
	
	final static class ViewHolder {
		/**
		 * 首字母
		 */
		TextView tvLetter;
		/**
		 * 昵称
		 */
		TextView tvTitle;
		/**
		 * 头像
		 */
		SelectableRoundedImageView mImageView;
		/**
		 * userid
		 */
		TextView tvUserId;
	}
}
