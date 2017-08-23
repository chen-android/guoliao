package com.GuoGuo.JuicyChat.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.App;
import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.db.DBManager;
import com.GuoGuo.JuicyChat.db.GroupMember;
import com.GuoGuo.JuicyChat.db.Groups;
import com.GuoGuo.JuicyChat.db.GroupsDao;
import com.GuoGuo.JuicyChat.server.pinyin.CharacterParser;
import com.GuoGuo.JuicyChat.server.widget.SelectableRoundedImageView;

import java.util.List;
import java.util.Map;

import io.rong.imageloader.core.ImageLoader;

/**
 * Created by tiankui on 16/10/7.
 */

public class GroupListAdapter extends android.widget.BaseAdapter {
	private Map<String, List<GroupMember>> filterGroupNameListMap;
	private Map<String, List<GroupMember>> filterGroupMemberNameListMap;
	private List<String> filterGroupId;
	private Context mContext;
	private String mFilterString;
	
	public GroupListAdapter(Context context, List<String> filterGroupId, Map<String, List<GroupMember>> filterGroupNameListMap, Map<String, List<GroupMember>> filterGroupMemberNameListMap, String filterStr) {
		this.mContext = context;
		this.filterGroupId = filterGroupId;
		this.filterGroupNameListMap = filterGroupNameListMap;
		this.filterGroupMemberNameListMap = filterGroupMemberNameListMap;
		this.mFilterString = filterStr;
	}
	
	@Override
	public int getCount() {
		if (filterGroupId != null) {
			return filterGroupId.size();
		}
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GroupViewHolder viewHolder;
		String groupId = (String) getItem(position);
		Groups groupInfo = DBManager.getInstance().getDaoSession().getGroupsDao().queryBuilder().where(GroupsDao.Properties.Id.eq(groupId)).unique();
		if (convertView == null) {
			viewHolder = new GroupViewHolder();
			convertView = View.inflate(mContext, R.layout.item_filter_group_list, null);
			viewHolder.portraitImageView = (SelectableRoundedImageView) convertView.findViewById(R.id.item_iv_group_image);
			viewHolder.nameDisplayNameLinearLayout = (LinearLayout) convertView.findViewById(R.id.item_ll_group_contains_member);
			viewHolder.displayNameTextView = (TextView) convertView.findViewById(R.id.item_tv_group_name);
			viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.item_tv_friend_display_name);
			viewHolder.nameSingleTextView = (TextView) convertView.findViewById(R.id.item_tv_group_name_single);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (GroupViewHolder) convertView.getTag();
		}
		if (groupInfo != null) {
//            String portraitUri = SealUserInfoManager.getInstance().getPortraitUri(groupInfo);
			String portraitUri = "";
			ImageLoader.getInstance().displayImage(portraitUri, viewHolder.portraitImageView, App.getOptions());
			List<GroupMember> filterGroupMemberNameList = filterGroupMemberNameListMap.get(groupId);
			if (filterGroupNameListMap.get(groupId) != null) {
				viewHolder.nameSingleTextView.setVisibility(View.VISIBLE);
				viewHolder.nameDisplayNameLinearLayout.setVisibility(View.GONE);
				viewHolder.nameSingleTextView.setText(CharacterParser.getInstance().getColoredGroupName(mFilterString, groupInfo.getGroupname()));
			} else if (filterGroupMemberNameList != null) {
				viewHolder.nameDisplayNameLinearLayout.setVisibility(View.VISIBLE);
				viewHolder.nameSingleTextView.setVisibility(View.GONE);
				viewHolder.displayNameTextView.setText(groupInfo.getGroupname());
				viewHolder.nameTextView.setText(CharacterParser.getInstance().getColoredNameList(mFilterString, filterGroupMemberNameList));
			}
		}
		return convertView;
	}
	
	@Override
	public Object getItem(int position) {
		if (filterGroupId == null)
			return null;
		
		if (position >= filterGroupId.size())
			return null;
		
		return filterGroupId.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	class GroupViewHolder {
		SelectableRoundedImageView portraitImageView;
		LinearLayout nameDisplayNameLinearLayout;
		TextView nameTextView;
		TextView displayNameTextView;
		TextView nameSingleTextView;
	}
}
