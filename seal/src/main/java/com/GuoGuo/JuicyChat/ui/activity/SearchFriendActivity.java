package com.GuoGuo.JuicyChat.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.server.network.async.AsyncTaskManager;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.FriendInvitationResponse;
import com.GuoGuo.JuicyChat.server.response.GetUserInfoByPhoneResponse;
import com.GuoGuo.JuicyChat.server.utils.NToast;
import com.GuoGuo.JuicyChat.server.widget.LoadDialog;
import com.GuoGuo.JuicyChat.ui.adapter.BaseAdapter;
import com.GuoGuo.JuicyChat.utils.SharedPreferencesContext;
import com.GuoGuo.JuicyChat.utils.transformation.RoundedTransformation;
import com.squareup.picasso.Picasso;

public class SearchFriendActivity extends BaseActivity {
	
	private static final int CLICK_CONVERSATION_USER_PORTRAIT = 1;
	private static final int SEARCH_PHONE = 10;
	private static final int ADD_FRIEND = 11;
	private EditText mEtSearch;
	private String mPhone;
	private ListView mSearchLv;
	private MyAdapter mAdapter;
	private String selectedFriendId;
	private AlertDialog addFriendDialog;
	private TextView emptyTv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setTitle(R.string.search_friend);
		
		mEtSearch = (EditText) findViewById(R.id.search_edit);
		mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					mPhone = v.getText().toString();
					if (!TextUtils.isEmpty(mPhone)) {
						hintKbTwo();
						LoadDialog.show(mContext);
						request(SEARCH_PHONE, true);
					} else {
						NToast.shortToast(mContext, "请输入搜索内容");
					}
					return true;
				}
				return false;
			}
		});
		mSearchLv = (ListView) findViewById(R.id.search_lv);
		emptyTv = (TextView) findViewById(R.id.empty_view);
		mAdapter = new MyAdapter(this);
		mSearchLv.setAdapter(mAdapter);
		mSearchLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				if (mAdapter.getItem(position).getMemberId().equals(SharedPreferencesContext.getInstance().getUserId())) {
					startActivity(new Intent(SearchFriendActivity.this, MyAccountActivity.class));
					return;
				}
				addFriendDialog = new AlertDialog.Builder(SearchFriendActivity.this).setTitle("添加好友")
						.setMessage("添加 " + mAdapter.getItem(position).getNickName() + " 为好友？")
						.setPositiveButton("添加", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								selectedFriendId = mAdapter.getItem(position).getMemberId();
								request(ADD_FRIEND, true);
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}).create();
				addFriendDialog.show();
			}
		});
	}
	
	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		switch (requestCode) {
			case SEARCH_PHONE:
				return action.getUserInfoFromPhone(mPhone);
			case ADD_FRIEND:
				return action.sendFriendInvitation(selectedFriendId);
		}
		return super.doInBackground(requestCode, id);
	}
	
	@Override
	public void onSuccess(int requestCode, Object result) {
		if (result != null) {
			switch (requestCode) {
				case SEARCH_PHONE:
					final GetUserInfoByPhoneResponse userInfoByPhoneResponse = (GetUserInfoByPhoneResponse) result;
					if (userInfoByPhoneResponse.getCode() == 200) {
						LoadDialog.dismiss(mContext);
						if (userInfoByPhoneResponse.getData() != null) {
							mAdapter.clear();
							mAdapter.addCollection(userInfoByPhoneResponse.getData());
							mAdapter.notifyDataSetChanged();
							emptyTv.setVisibility(View.GONE);
						} else {
							emptyTv.setVisibility(View.VISIBLE);
						}
					}
					break;
				case ADD_FRIEND:
					if (addFriendDialog != null)
						addFriendDialog.dismiss();
					FriendInvitationResponse fres = (FriendInvitationResponse) result;
					if (fres.getCode() == 200) {
						NToast.shortToast(mContext, getString(R.string.request_success));
						LoadDialog.dismiss(mContext);
					} else {
						NToast.shortToast(mContext, fres.getMessage());
						LoadDialog.dismiss(mContext);
					}
					break;
			}
		}
	}
	
	@Override
	public void onFailure(int requestCode, int state, Object result) {
		switch (requestCode) {
			case ADD_FRIEND:
				NToast.shortToast(mContext, "你们已经是好友");
				LoadDialog.dismiss(mContext);
				break;
			case SEARCH_PHONE:
				if (state == AsyncTaskManager.HTTP_ERROR_CODE || state == AsyncTaskManager.HTTP_NULL_CODE) {
					super.onFailure(requestCode, state, result);
				} else {
					NToast.shortToast(mContext, "用户不存在");
				}
				LoadDialog.dismiss(mContext);
				break;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		hintKbTwo();
		finish();
		return super.onOptionsItemSelected(item);
	}
	
	private void hintKbTwo() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive() && getCurrentFocus() != null) {
			if (getCurrentFocus().getWindowToken() != null) {
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}
	
	private class MyAdapter extends BaseAdapter<GetUserInfoByPhoneResponse.FriendData> {
		
		public MyAdapter(Context context) {
			super(context);
		}
		
		@Override
		protected View newView(Context context, int position, ViewGroup group) {
			return LayoutInflater.from(context).inflate(R.layout.list_item_friend, null);
		}
		
		@Override
		protected void bindView(View v, int position, GetUserInfoByPhoneResponse.FriendData data) {
			ImageView iv = (ImageView) v.findViewById(R.id.list_item_friend_iv);
			TextView tv = (TextView) v.findViewById(R.id.list_item_friend_name_tv);
			Picasso.with(v.getContext()).load(data.getHeadIco()).placeholder(R.drawable.rc_default_portrait)
					.transform(new RoundedTransformation(5, 0)).fit().centerCrop().into(iv);
			tv.setText(data.getNickName());
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
	}
}
