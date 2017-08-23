package com.GuoGuo.JuicyChat.ui.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.pinyin.SideBar;
import com.GuoGuo.JuicyChat.server.response.GetGroupMemberResponse;

/**
 * 群主解除冻结
 * Created by cs on 2017/5/29.
 */

public class UnlockMoneyActivity extends BaseActivity {
	private static final int REQUEST_LIST = 678;
	private static final int UNLOCK = 535;
	private TextView noTv;
	private ListView lv;
	private SideBar sb;
	private String groupId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_unlock_money);
		initView();
		groupId = getIntent().getStringExtra("groupId");
	}
	
	private void initView() {
		noTv = (TextView) findViewById(R.id.unlocak_money_show_no_friend_tv);
		lv = (ListView) findViewById(R.id.unlocak_money_friendlistview_iv);
		sb = (SideBar) findViewById(R.id.unlocak_money_sidrbar_sb);
	}
	
	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		switch (requestCode) {
			case REQUEST_LIST:
				return action.getLockedGroupMembers(groupId);
		}
		return super.doInBackground(requestCode, id);
	}
	
	@Override
	public void onSuccess(int requestCode, Object result) {
		switch (requestCode) {
			case REQUEST_LIST:
				GetGroupMemberResponse groupMemberResponse = (GetGroupMemberResponse) result;
				if (groupMemberResponse.getCode() == 200) {
					
				}
				break;
		}
	}
}
