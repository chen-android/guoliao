package com.kuaishou.hb.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kuaishou.hb.R;
import com.kuaishou.hb.SealAppContext;
import com.kuaishou.hb.SealUserInfoManager;
import com.kuaishou.hb.db.Friend;
import com.kuaishou.hb.server.broadcast.BroadcastManager;
import com.kuaishou.hb.server.network.http.HttpException;
import com.kuaishou.hb.server.response.SetFriendDisplayNameResponse;
import com.kuaishou.hb.server.widget.LoadDialog;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/8/10.
 * Company RongCloud
 */
@SuppressWarnings("deprecation")
public class NoteInformationActivity extends BaseActivity {

	private static final int SET_DISPLAYNAME = 12;
	private Friend mFriend;
	private EditText mNoteEdit;
	private TextView mNoteSave;
	private static final int CLICK_CONTACT_FRAGMENT_FRIEND = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_noteinfo);
		setHeadVisibility(View.GONE);
		mNoteEdit = (EditText) findViewById(R.id.notetext);
		mNoteSave = (TextView) findViewById(R.id.notesave);
		mFriend = getIntent().getParcelableExtra("friend");
		if (mFriend != null) {
			mNoteSave.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					LoadDialog.show(mContext);
					request(SET_DISPLAYNAME);
				}
			});
			mNoteSave.setClickable(false);
			mNoteEdit.setText(SealUserInfoManager.getInstance().getDiaplayName(mFriend));
			mNoteEdit.setSelection(mNoteEdit.getText().length());
			mNoteEdit.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (!TextUtils.isEmpty(mFriend.getUsername())) {
						mNoteSave.setClickable(true);
						mNoteSave.setTextColor(getResources().getColor(R.color.white));
					} else {
						if (TextUtils.isEmpty(s.toString())) {
							mNoteSave.setClickable(false);
							mNoteSave.setTextColor(Color.parseColor("#9fcdfd"));
						} else if (s.toString().equals(mFriend.getUsername())) {
							mNoteSave.setClickable(false);
							mNoteSave.setTextColor(Color.parseColor("#9fcdfd"));
						} else {
							mNoteSave.setClickable(true);
							mNoteSave.setTextColor(getResources().getColor(R.color.white));
						}
					}

				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			});


		}
	}

	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		if (requestCode == SET_DISPLAYNAME) {
			return action.setFriendDisplayName(mFriend.getFriendid(), mNoteEdit.getText().toString().trim());
		}
		return super.doInBackground(requestCode, id);
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		if (result != null) {
			if (requestCode == SET_DISPLAYNAME) {
				SetFriendDisplayNameResponse response = (SetFriendDisplayNameResponse) result;
				if (response.getCode() == 200) {
					String displayName = mNoteEdit.getText().toString();
					if (displayName != null) {
						displayName = displayName.trim();
					}
					mFriend.setRemark(displayName);
					SealUserInfoManager.getInstance().addFriend(mFriend);
					if (TextUtils.isEmpty(displayName)) {
						RongIM.getInstance().refreshUserInfoCache(new UserInfo(mFriend.getFriendid(), mFriend.getNickname(), Uri.parse(mFriend.getHeadico())));
					} else {
						RongIM.getInstance().refreshUserInfoCache(new UserInfo(mFriend.getFriendid(), displayName, Uri.parse(mFriend.getHeadico())));
					}
					BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.UPDATE_FRIEND);
					Intent intent = new Intent(mContext, UserDetailActivity.class);
					intent.putExtra("type", CLICK_CONTACT_FRAGMENT_FRIEND);
					intent.putExtra("displayName", mNoteEdit.getText().toString().trim());
					setResult(155, intent);
					LoadDialog.dismiss(mContext);
					finish();
				}
			}
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		if (requestCode == SET_DISPLAYNAME) {
			LoadDialog.dismiss(mContext);
		}
		super.onFailure(requestCode, state, result);
	}

	public void finishPage(View view) {
		this.finish();
	}
}
