package com.GuoGuo.JuicyChat.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.App;
import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.SealUserInfoManager;
import com.GuoGuo.JuicyChat.db.Friend;
import com.GuoGuo.JuicyChat.server.widget.LoadDialog;
import com.GuoGuo.JuicyChat.server.widget.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.List;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class BlackListActivity extends BaseActivity {
	
	private TextView isShowData;
	private ListView mBlackList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_black);
		setTitle(R.string.the_blacklist);
		initView();
		requestData();
	}
	
	private void requestData() {
//        LoadDialog.show(mContext);
//        SealUserInfoManager.getInstance().getBlackList(new SealUserInfoManager.ResultCallback<List<UserInfo>>() {
//            @Override
//            public void onSuccess(List<UserInfo> userInfoList) {
//                LoadDialog.dismiss(mContext);
//                if (userInfoList != null) {
//                    if (userInfoList.size() > 0) {
//                        MyBlackListAdapter adapter = new MyBlackListAdapter(userInfoList);
//                        mBlackList.setAdapter(adapter);
//                    } else {
//                        isShowData.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//
//            @Override
//            public void onError(String errString) {
//                LoadDialog.dismiss(mContext);
//            }
//        });
		LoadDialog.show(mContext);
		RongIM.getInstance().getBlacklist(new RongIMClient.GetBlacklistCallback() {
			@Override
			public void onSuccess(String[] strings) {
				LoadDialog.dismiss(mContext);
				if (strings != null && strings.length > 0) {
					List<UserInfo> userInfoList = new ArrayList<>();
					for (String string : strings) {
						Friend friendByID = SealUserInfoManager.getInstance().getFriendByID(string);
						UserInfo info = new UserInfo(friendByID.getFriendid(), SealUserInfoManager.getInstance().getDiaplayName(friendByID), Uri.parse(friendByID.getHeadico()));
						userInfoList.add(info);
					}
					if (userInfoList.size() > 0) {
						MyBlackListAdapter adapter = new MyBlackListAdapter(userInfoList);
						mBlackList.setAdapter(adapter);
					} else {
						isShowData.setVisibility(View.VISIBLE);
					}
				}
			}
			
			@Override
			public void onError(RongIMClient.ErrorCode errorCode) {
				LoadDialog.dismiss(mContext);
			}
		});
	}
	
	private void initView() {
		isShowData = (TextView) findViewById(R.id.blacklsit_show_data);
		mBlackList = (ListView) findViewById(R.id.blacklsit_list);
	}
	
	class MyBlackListAdapter extends BaseAdapter {
		
		private List<UserInfo> userInfoList;
		
		public MyBlackListAdapter(List<UserInfo> dataList) {
			this.userInfoList = dataList;
		}
		
		@Override
		public int getCount() {
			return userInfoList.size();
		}
		
		@Override
		public Object getItem(int position) {
			return userInfoList.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			UserInfo userInfo = userInfoList.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.black_item_new, parent, false);
				viewHolder.mName = (TextView) convertView.findViewById(R.id.blackname);
				viewHolder.mHead = (SelectableRoundedImageView) convertView.findViewById(R.id.blackuri);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.mName.setText(userInfo.getName());
			String portraitUri = SealUserInfoManager.getInstance().getPortraitUri(userInfo);
			ImageLoader.getInstance().displayImage(portraitUri, viewHolder.mHead, App.getOptions());
			return convertView;
		}
		
		
		class ViewHolder {
			SelectableRoundedImageView mHead;
			TextView mName;
		}
	}
}
