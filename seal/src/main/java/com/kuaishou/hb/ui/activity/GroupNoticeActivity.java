package com.kuaishou.hb.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kuaishou.hb.R;
import com.kuaishou.hb.server.widget.DialogWithYesOrNoUtils;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

@SuppressWarnings("deprecation")
public class GroupNoticeActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
	EditText mEdit;
	Conversation.ConversationType mConversationType;
	String mTargetId;
	private boolean isCreated;
	private boolean isSendAll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_notice);
		mEdit = (EditText) findViewById(R.id.edit_area);
		Intent intent = getIntent();
		mConversationType = Conversation.ConversationType.setValue(intent.getIntExtra("conversationType", 0));
		mTargetId = getIntent().getStringExtra("targetId");
		isCreated = getIntent().getBooleanExtra("isCreated", false);
		isSendAll = getIntent().getBooleanExtra("isSendAll", false);
		String gonggao = getIntent().getStringExtra("gonggao");
		if (isSendAll) {
			setTitle(R.string.group_notify_all);
			mEdit.setHint("请编辑要通知的内容");
		} else {
			setTitle(R.string.group_announcement);
			mEdit.setText(gonggao);
		}
		Button rightButton = getHeadRightButton();
		rightButton.setVisibility(View.GONE);
		if (isCreated) {
			mHeadRightText.setVisibility(View.VISIBLE);
			mHeadRightText.setText(R.string.Done);
			mHeadRightText.setTextColor(getResources().getColor(android.R.color.darker_gray));
			mHeadRightText.setClickable(false);
			mHeadRightText.setOnClickListener(this);
			mEdit.addTextChangedListener(this);
		} else {
			if (TextUtils.isEmpty(gonggao)) {
				mEdit.setText("暂无公告");
			}
			mEdit.setEnabled(false);
		}
	}

	@Override
	public void onHeadLeftButtonClick(View v) {
		if (!isCreated) {
			finish();
		} else {
			DialogWithYesOrNoUtils.getInstance().showDialog(this, getString(R.string.group_notice_exist_confirm), new DialogWithYesOrNoUtils.DialogCallBack() {
				@Override
				public void executeEvent() {
					finish();
				}

				@Override
				public void executeEditEvent(String editText) {

				}

				@Override
				public void updatePassword(String oldPassword, String newPassword) {

				}
			});
		}
	}

	@Override
	public void onBackPressed() {

		if (!isCreated) {
			finish();
		} else {
			DialogWithYesOrNoUtils.getInstance().showDialog(this, getString(R.string.group_notice_exist_confirm), new DialogWithYesOrNoUtils.DialogCallBack() {
				@Override
				public void executeEvent() {
					finish();
				}

				@Override
				public void executeEditEvent(String editText) {

				}

				@Override
				public void updatePassword(String oldPassword, String newPassword) {

				}
			});
		}

		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.text_right:
				if (isSendAll) {
					TextMessage textMessage = TextMessage.obtain(RongContext.getInstance().getString(R.string.group_notice_prefix) + mEdit.getText().toString());
					MentionedInfo mentionedInfo = new MentionedInfo(MentionedInfo.MentionedType.ALL, null, null);
					textMessage.setMentionedInfo(mentionedInfo);

					RongIM.getInstance().sendMessage(Message.obtain(mTargetId, mConversationType, textMessage), null, null, new IRongCallback.ISendMessageCallback() {
						@Override
						public void onAttached(Message message) {

						}

						@Override
						public void onSuccess(Message message) {

						}

						@Override
						public void onError(Message message, RongIMClient.ErrorCode errorCode) {

						}
					});
					setResult(RESULT_OK);
					finish();
				} else {
					DialogWithYesOrNoUtils.getInstance().showDialog(this, getString(R.string.group_notice_post_confirm), new DialogWithYesOrNoUtils.DialogCallBack() {
						@Override
						public void executeEvent() {
							TextMessage textMessage = TextMessage.obtain(RongContext.getInstance().getString(R.string.group_notice_prefix) + mEdit.getText().toString());
							MentionedInfo mentionedInfo = new MentionedInfo(MentionedInfo.MentionedType.ALL, null, null);
							textMessage.setMentionedInfo(mentionedInfo);

							RongIM.getInstance().sendMessage(Message.obtain(mTargetId, mConversationType, textMessage), null, null, new IRongCallback.ISendMessageCallback() {
								@Override
								public void onAttached(Message message) {

								}

								@Override
								public void onSuccess(Message message) {

								}

								@Override
								public void onError(Message message, RongIMClient.ErrorCode errorCode) {

								}
							});
							Intent intent = new Intent();
							intent.putExtra("notice", mEdit.getText().toString());
							setResult(RESULT_OK, intent);
							finish();
						}

						@Override
						public void executeEditEvent(String editText) {

						}

						@Override
						public void updatePassword(String oldPassword, String newPassword) {

						}
					});
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s.toString().length() > 0) {
			mHeadRightText.setClickable(true);
			mHeadRightText.setTextColor(getResources().getColor(android.R.color.white));
		} else {
			mHeadRightText.setClickable(false);
			mHeadRightText.setTextColor(getResources().getColor(android.R.color.darker_gray));
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (s != null) {
			int start = mEdit.getSelectionStart();
			int end = mEdit.getSelectionEnd();
			mEdit.removeTextChangedListener(this);
			mEdit.setText(AndroidEmoji.ensure(s.toString()));
			mEdit.addTextChangedListener(this);
			mEdit.setSelection(start, end);
		}
	}
}
