package com.GuoGuo.JuicyChat;

import android.content.Context;
import android.text.TextUtils;

import com.GuoGuo.JuicyChat.db.GroupMember;
import com.GuoGuo.JuicyChat.server.SealAction;
import com.GuoGuo.JuicyChat.server.network.async.AsyncTaskManager;
import com.GuoGuo.JuicyChat.server.network.async.OnDataListener;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.GetGroupMemberResponse;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.GroupUserInfo;

/**
 * Created by AMing on 16/2/29.
 * Company RongCloud
 */
public class GroupUserInfoEngine implements OnDataListener {
	
	private String groupId, userId;
	
	private static final int REQUEST_GROUP_USER_INFO = 50;
	private static GroupUserInfoEngine instance;
	
	private Context context;
	
	private GroupUserInfoEngine(Context context) {
		this.context = context;
	}
	
	
	private GroupUserInfo groupUserInfo;
	
	public GroupUserInfo getGroupUserInfo() {
		return groupUserInfo;
	}
	
	public void setGroupUserInfo(GroupUserInfo groupUserInfo) {
		this.groupUserInfo = groupUserInfo;
	}
	
	public static GroupUserInfoEngine getInstance(Context context) {
		if (instance == null) {
			instance = new GroupUserInfoEngine(context);
		}
		return instance;
	}
	
	public GroupUserInfo startEngine(String groupId, String userid) {
		if (!TextUtils.isEmpty(groupId) && !TextUtils.isEmpty(userid)) {
			this.groupId = groupId;
			this.userId = userid;
			AsyncTaskManager.getInstance(context).request(REQUEST_GROUP_USER_INFO, this);
		}
		return getGroupUserInfo();
	}
	
	
	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		return new SealAction(context).getGroupMember(id);
	}
	
	
	@Override
	public void onSuccess(int requestCode, Object result) {
		if (result != null) {
			GetGroupMemberResponse res = (GetGroupMemberResponse) result;
			if (res.getCode() == 200) {
				List<GroupMember> mGroupMember = res.getData();
				for (GroupMember g : mGroupMember) {
					if (g.getUserid().equals(userId)) {
						if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {
							RongIM.getInstance().refreshGroupUserInfoCache(new GroupUserInfo(groupId, userId, g.getRemark()));
							setGroupUserInfo(new GroupUserInfo(groupId, userId, g.getRemark()));
						}
					}
				}
			}
		}
	}
	
	@Override
	public void onFailure(int requestCode, int state, Object result) {
		
	}
}
