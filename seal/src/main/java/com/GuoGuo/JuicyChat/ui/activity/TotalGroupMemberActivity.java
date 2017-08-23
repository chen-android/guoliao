package com.GuoGuo.JuicyChat.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.App;
import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.SealUserInfoManager;
import com.GuoGuo.JuicyChat.db.Friend;
import com.GuoGuo.JuicyChat.db.GroupMember;
import com.GuoGuo.JuicyChat.server.pinyin.CharacterParser;
import com.GuoGuo.JuicyChat.server.utils.RongGenerate;
import com.GuoGuo.JuicyChat.server.widget.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.List;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/7/1.
 * Company RongCloud
 */
public class TotalGroupMemberActivity extends BaseActivity {
	private static final int CLICK_CONVERSATION_USER_PORTRAIT = 1;
	
	private List<GroupMember> mGroupMember;
	
	private ListView mTotalListView;
	private TotalGroupMember adapter;
	private EditText mSearch;
	private String mGroupID;
	
	@Override
	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toatl_member);
		setTitle(R.string.total_member);
		initViews();
		mGroupID = getIntent().getStringExtra("targetId");
		SealUserInfoManager.getInstance().getGroupMembers(mGroupID, new SealUserInfoManager.ResultCallback<List<GroupMember>>() {
			@Override
			public void onSuccess(List<GroupMember> groupMembers) {
				mGroupMember = groupMembers;
				if (mGroupMember != null && mGroupMember.size() > 0) {
					setTitle(getString(R.string.total_member) + "(" + mGroupMember.size() + ")");
					adapter = new TotalGroupMember(mGroupMember, mContext);
					mTotalListView.setAdapter(adapter);
					mTotalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							GroupMember bean = (GroupMember) adapter.getItem(position);
							UserInfo userInfo = new UserInfo(bean.getUserid(), bean.getNickname(),
									TextUtils.isEmpty(bean.getUserhead())
											? Uri.parse(RongGenerate.generateDefaultAvatar(bean.getUsername(), bean.getUserid())) : Uri.parse(bean.getUserhead()));
							Intent intent = new Intent(mContext, UserDetailActivity.class);
							Friend friend = CharacterParser.getInstance().generateFriendFromUserInfo(userInfo);
							intent.putExtra("friend", friend);
							intent.putExtra("type", CLICK_CONVERSATION_USER_PORTRAIT);
							intent.putExtra("conversationType", Conversation.ConversationType.GROUP.getValue());
							startActivity(intent);
						}
					});
					mSearch.addTextChangedListener(new TextWatcher() {
						@Override
						public void beforeTextChanged(CharSequence s, int start, int count, int after) {
							
						}
						
						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {
							filterData(s.toString());
						}
						
						@Override
						public void afterTextChanged(Editable s) {
							
						}
					});
				}
			}
			
			@Override
			public void onError(String errString) {
				
			}
		});
	}
	
	private void filterData(String s) {
		List<GroupMember> filterDateList = new ArrayList<>();
		if (TextUtils.isEmpty(s)) {
			filterDateList = mGroupMember;
		} else {
			for (GroupMember groupMember : mGroupMember) {
				if (groupMember.getUsername().contains(s) || groupMember.getNickname().contains(s)) {
					filterDateList.add(groupMember);
				}
			}
		}
		adapter.updateListView(filterDateList);
	}
	
	private void initViews() {
		mTotalListView = (ListView) findViewById(R.id.total_listview);
		mSearch = (EditText) findViewById(R.id.group_member_search);
	}
	
	
	class TotalGroupMember extends BaseAdapter {
		
		private List<GroupMember> list;
		
		private Context context;
		
		private ViewHolder holder;
		
		
		public TotalGroupMember(List<GroupMember> list, Context mContext) {
			this.list = list;
			this.context = mContext;
		}
		
		@Override
		public int getCount() {
			return list.size();
		}
		
		@Override
		public Object getItem(int position) {
			return list.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.share_item, parent, false);
				holder.mImageView = (SelectableRoundedImageView) convertView.findViewById(R.id.share_icon);
				holder.title = (TextView) convertView.findViewById(R.id.share_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			GroupMember bean = list.get(position);
			Friend friend = SealUserInfoManager.getInstance().getFriendByID(bean.getUserid());
			if (friend != null && !TextUtils.isEmpty(friend.getNickname())) {
				holder.title.setText(friend.getNickname());
			} else {
				holder.title.setText(bean.getRemark());
			}
			String portraitUri = bean.getUserhead();
			ImageLoader.getInstance().displayImage(portraitUri, holder.mImageView, App.getOptions());
			return convertView;
		}
		
		
		public void updateListView(List<GroupMember> list) {
			this.list = list;
			notifyDataSetChanged();
		}
	}
	
	
	final static class ViewHolder {
		/**
		 * 头像
		 */
		SelectableRoundedImageView mImageView;
		
		TextView title;
	}
}
