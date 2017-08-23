package com.GuoGuo.JuicyChat;

import android.content.Context;

import com.GuoGuo.JuicyChat.db.GroupMember;
import com.GuoGuo.JuicyChat.server.SealAction;
import com.GuoGuo.JuicyChat.server.network.async.AsyncTaskManager;
import com.GuoGuo.JuicyChat.server.network.async.OnDataListener;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.GetGroupMemberResponse;

import java.util.ArrayList;
import java.util.List;

import io.rong.common.RLog;

public class GroupMemberEngine implements OnDataListener {
	private static final String TAG = "GroupMemberEngine";
	
	private Context context;
	
	private IGroupMembersCallback groupMembersCallback;
	
	private static final int REQUEST_GROUP_MEMBER = 4235;
	
	public GroupMemberEngine(Context context) {
		this.context = context;
	}
	
	public void startEngine(String groupId, IGroupMembersCallback callback) {
		this.groupMembersCallback = callback;
		AsyncTaskManager.getInstance(context).request(groupId, REQUEST_GROUP_MEMBER, this);
	}
	
	@Override
	public Object doInBackground(int requestCode, String groupId) throws HttpException {
		return new SealAction(context).getGroupMember(groupId);
	}
	
	@Override
	public void onSuccess(int requestCode, Object result) {
		if (result != null) {
			GetGroupMemberResponse response = (GetGroupMemberResponse) result;
			ArrayList<String> memberList = new ArrayList<>();
			if (response.getCode() == 200) {
				List<GroupMember> resultEntityList = response.getData();
				for (GroupMember r : resultEntityList) {
					memberList.add(r.getUserid());
				}
			}
			if (groupMembersCallback != null) {
				groupMembersCallback.onResult(memberList);
			}
		}
	}
	
	@Override
	public void onFailure(int requestCode, int state, Object result) {
		RLog.d(TAG, "onFailure state = " + state);
	}
	
	public interface IGroupMembersCallback {
		void onResult(ArrayList<String> members);
	}
}
