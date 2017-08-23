package com.GuoGuo.JuicyChat.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.GuoGuo.JuicyChat.R;

/**
 * Created by cs on 2017/5/25.
 */

public class ShareDialog extends Dialog {
	private OnItemClickListener mOnItemClickListener;
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
	}
	
	public ShareDialog(@NonNull Context context) {
		super(context, R.style.WinDialog);
	}
	
	public ShareDialog(@NonNull Context context, OnItemClickListener listener) {
		super(context, R.style.WinDialog);
		this.mOnItemClickListener = listener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_share);
		Window window = getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.ShareDialogAnimation);
		WindowManager.LayoutParams attributes = window.getAttributes();
		attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
		attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(attributes);
		findViewById(R.id.dialog_share_wechat_ll).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnItemClickListener.click(ShareDialog.this, 0);
				dismiss();
			}
		});
		findViewById(R.id.dialog_share_wechat_friends_ll).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnItemClickListener.click(ShareDialog.this, 1);
				dismiss();
			}
		});
		findViewById(R.id.share_cancel_tv).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	public interface OnItemClickListener {
		void click(Dialog dialog, int index);
	}
}
