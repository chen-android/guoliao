package com.GuoGuo.JuicyChat.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.App;
import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.server.SealAction;
import com.GuoGuo.JuicyChat.server.network.async.AsyncTaskManager;
import com.GuoGuo.JuicyChat.server.network.async.OnDataListener;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.BaseResponse;
import com.GuoGuo.JuicyChat.server.response.OpenRedPacketResponse;
import com.GuoGuo.JuicyChat.server.utils.NToast;
import com.GuoGuo.JuicyChat.server.widget.LoadDialog;

import io.rong.imageloader.core.ImageLoader;

/**
 * 打开红包界面
 * Created by cs on 2017/5/17.
 */

public class RedPacketOpenDialog extends Dialog implements View.OnClickListener, OnDataListener {
	private static final int REQUEAT_OPEN_REDPACKET = 82;
	private static final int REQUEST_CHECK_LOCK = 214;
	private ImageView closeIv;
	private ImageView headIv;
	private ImageView openIv;
	private TextView nameTv;
	private TextView noteTv;
	private TextView tipTv;
	private TextView detailTv;
	private String name;
	private String note;
	private String headIco;
	private String redpacketId;
	private boolean isSingle;//是否是个人红包
	private long groupId;
	
	private Context mContext;
	private OnDetailClickListener mClickListener;
	
	public RedPacketOpenDialog(@NonNull Context context) {
		super(context, R.style.WinDialog);
		setContentView(R.layout.dialog_red_packet_open);
		mContext = context;
		initView();
	}
	
	private void initView() {
		closeIv = (ImageView) findViewById(R.id.dialog_red_packet_open_exit_iv);
		headIv = (ImageView) findViewById(R.id.dialog_red_packet_open_head_iv);
		openIv = (ImageView) findViewById(R.id.dialog_red_packet_open_action_iv);
		nameTv = (TextView) findViewById(R.id.dialog_red_packet_name_tv);
		noteTv = (TextView) findViewById(R.id.dialog_red_packet_note_tv);
		tipTv = (TextView) findViewById(R.id.dialog_red_packet_open_tip_tv);
		detailTv = (TextView) findViewById(R.id.dialog_red_packet_detail_tv);
		closeIv.setOnClickListener(this);
		openIv.setOnClickListener(this);
		detailTv.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if (v == closeIv) {
			this.dismiss();
		} else if (v == openIv) {
			LoadDialog.show(mContext);
			if (isSingle) {
				AsyncTaskManager.getInstance(mContext).request(REQUEAT_OPEN_REDPACKET, this);
				return;
			}
			AsyncTaskManager.getInstance(mContext).request(REQUEST_CHECK_LOCK, this);
		} else if (v == detailTv) {
			gotoDetail(false);
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	
	public void setHeadIco(String headIco) {
		this.headIco = headIco;
	}
	
	public void setRedpacketId(String redpacketId) {
		this.redpacketId = redpacketId;
	}
	
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	
	public void setSingle(boolean single) {
		isSingle = single;
	}
	
	public void setClickListener(OnDetailClickListener clickListener) {
		mClickListener = clickListener;
	}
	
	@Override
	public Object doInBackground(int requestCode, String parameter) throws HttpException {
		switch (requestCode) {
			case REQUEAT_OPEN_REDPACKET:
				return new SealAction(mContext).openRedPacket(this.redpacketId);
			case REQUEST_CHECK_LOCK:
				return new SealAction(mContext).checkLockMoney(this.groupId);
		}
		return null;
	}
	
	@Override
	public void onSuccess(int requestCode, Object result) {
		switch (requestCode) {
			case REQUEAT_OPEN_REDPACKET:
				
				OpenRedPacketResponse redPacketResponse = (OpenRedPacketResponse) result;
				if (redPacketResponse.getCode() == 200) {
					gotoDetail(true);
				} else if (redPacketResponse.getCode() == 66003) {//抢光了
					LoadDialog.dismiss(mContext);
					setLateView();
				} else if (redPacketResponse.getCode() == 66004) {//已经领取过了
					gotoDetail(true);
					NToast.shortToast(mContext, "您已领取过该红包");
				} else if (redPacketResponse.getCode() == 66102) {//红包过期
					gotoDetail(true);
					NToast.shortToast(mContext, "红包已过期");
				}
				break;
			case REQUEST_CHECK_LOCK:
				BaseResponse response = (BaseResponse) result;
				if (response.getCode() == 200) {
					AsyncTaskManager.getInstance(mContext).request(REQUEAT_OPEN_REDPACKET, this);
				} else if (response.getCode() == 66201) {
					LoadDialog.dismiss(mContext);
					NToast.shortToast(mContext, "锁定定额不足，不能开启红包");
					dismiss();
				} else if (response.getCode() == 600) {
					LoadDialog.dismiss(mContext);
					NToast.shortToast(mContext, "服务器开小差，请稍后再试。");
					dismiss();
				} else {
					AsyncTaskManager.getInstance(mContext).request(REQUEAT_OPEN_REDPACKET, this);
				}
				break;
		}
	}
	
	@Override
	public void onFailure(int requestCode, int state, Object result) {
		
	}
	
	/**
	 * 手慢了的界面
	 */
	private void setLateView() {
		tipTv.setVisibility(View.INVISIBLE);
		noteTv.setText("手慢了，红包派完了");
		openIv.setVisibility(View.INVISIBLE);
	}
	
	private void setOverTimeView() {
		tipTv.setVisibility(View.INVISIBLE);
		noteTv.setText("来晚了，红包已过期。");
		openIv.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * 跳转详情
	 *
	 * @param isAfterOpen 是否是点击打开红包，否（就是点击详情按钮）
	 */
	private void gotoDetail(boolean isAfterOpen) {
		if (mClickListener != null) {
			mClickListener.click(isAfterOpen);
		}
	}
	
	public void show(REDPACKET_STATE state) {
		super.show();
		nameTv.setText(name);
		noteTv.setText(note);
		ImageLoader.getInstance().displayImage(headIco, this.headIv, App.getOptions());
		if (state == REDPACKET_STATE.LATE) {
			setLateView();
			return;
		}
		if (state == REDPACKET_STATE.OVERTIME) {
			setOverTimeView();
			return;
		}
		if (state == REDPACKET_STATE.NORMAL) {
			detailTv.setVisibility(View.INVISIBLE);
		}
	}
	
	public interface OnDetailClickListener {
		void click(boolean isAfterOpen);
	}
	
	public enum REDPACKET_STATE {
		NORMAL,//正常
		LATE,//手慢了
		OVERTIME//红包过期了
	}
}
