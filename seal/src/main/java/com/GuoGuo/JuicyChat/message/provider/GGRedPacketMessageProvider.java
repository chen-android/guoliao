package com.GuoGuo.JuicyChat.message.provider;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.model.GGRedPacketMessage;
import com.GuoGuo.JuicyChat.model.GGRedPacketNotifyMessage;
import com.GuoGuo.JuicyChat.server.SealAction;
import com.GuoGuo.JuicyChat.server.network.async.AsyncTaskManager;
import com.GuoGuo.JuicyChat.server.network.async.OnDataListener;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.BaseResponse;
import com.GuoGuo.JuicyChat.server.response.CheckRedPacketCountResponse;
import com.GuoGuo.JuicyChat.server.response.GetMoneyResponse;
import com.GuoGuo.JuicyChat.server.response.GetRedPacketDetailResponse;
import com.GuoGuo.JuicyChat.server.response.GetRedPacketUsersResponse;
import com.GuoGuo.JuicyChat.server.utils.NToast;
import com.GuoGuo.JuicyChat.server.widget.LoadDialog;
import com.GuoGuo.JuicyChat.ui.activity.RedPacketDetailActivity;
import com.GuoGuo.JuicyChat.ui.widget.RedPacketOpenDialog;
import com.GuoGuo.JuicyChat.utils.SharedPreferencesContext;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * Created by cs on 2017/5/12.
 */
@ProviderTag(
		messageContent = GGRedPacketMessage.class,
		showReadState = true
)
public class GGRedPacketMessageProvider extends IContainerItemProvider.MessageProvider<GGRedPacketMessage> implements OnDataListener {
	private static final int REQUEST_CHECK_HAS_OPENED = 416;
	private static final int REQUEST_CHECK_RED_PACKET_REMAIN = 773;
	private static final int REQUEST_USER_MONEY = 499;
	private static final int REQUEST_OPEN_RED_PACKET = 772;
	private static final int REQUEST_DETAIL = 79;
	private static final int REQUEST_MEMBERS = 919;
	private Context context;
	private GGRedPacketMessage message;
	private SealAction action;
	private RedPacketOpenDialog dialog;
	private GetRedPacketDetailResponse.ResultEntity detailEntity;
	private ArrayList<GetRedPacketUsersResponse.ResultEntity> members;
	private boolean isForDetail = false;//跳过过程，直接请求成员，进入红包详情界面
	private boolean isAfterOpen = false;
	private boolean isSingle = false;
	private boolean isLate = false;
	private long targetId;
	
	public GGRedPacketMessageProvider() {
	}
    
    @Override
    public void bindView(View var1, int var2, GGRedPacketMessage var3, UIMessage var4) {
        GGRedPacketMessageProvider.ViewHolder var5 = (GGRedPacketMessageProvider.ViewHolder) var1.getTag();
        if (var4.getMessageDirection() == Message.MessageDirection.SEND) {
            var5.bri_bg.setBackgroundResource(R.drawable.bg_from_hongbao);
            var5.tv_bri_target.setText("查看红包");
            var5.tv_bri_name.setPadding(28, 0, 0, 0);
        } else {
            var5.bri_bg.setBackgroundResource(R.drawable.bg_to_hongbao);
            var5.tv_bri_target.setText("领取红包");
            var5.tv_bri_name.setPadding(48, 0, 0, 0);
        }
        String content = var3.getContent();
        if (!TextUtils.isEmpty(content) && content.startsWith(GGRedPacketMessage.CONTENT_PREFIX)) {
            content = content.replace(GGRedPacketMessage.CONTENT_PREFIX, "");
        }
        var5.tv_bri_mess.setText(content);
    }
    
    @Override
    public Spannable getContentSummary(GGRedPacketMessage var1) {
        return var1 != null && !TextUtils.isEmpty(var1.getContent()) ? new SpannableString(var1.getContent()) : null;
    }
    
    @Override
    public void onItemClick(View var1, int var2, final GGRedPacketMessage var3, final UIMessage var4) {
//		SendUser.sendUserId = var4.getSenderUserId();
//		SendUser.conversationType = var4.getConversationType();
        this.targetId = Long.valueOf(var4.getTargetId());
        LoadDialog.show(context);
        if (var4.getMessageDirection() == Message.MessageDirection.SEND) {
            if (var4.getConversationType() == Conversation.ConversationType.GROUP) {
                this.message = var3;
                isSingle = false;
                isForDetail = false;
                AsyncTaskManager.getInstance(context).request(REQUEST_CHECK_HAS_OPENED, this);
            }
            if (var4.getConversationType() == Conversation.ConversationType.PRIVATE) {
                this.message = var3;
                this.isSingle = true;
                isForDetail = true;
                AsyncTaskManager.getInstance(context).request(REQUEST_DETAIL, this);
            }
        } else if (var4.getMessageDirection() == Message.MessageDirection.RECEIVE) {
            if (var4.getConversationType() == Conversation.ConversationType.PRIVATE) {
                this.message = var3;
                this.isSingle = true;
                this.isForDetail = false;
                AsyncTaskManager.getInstance(context).request(REQUEST_CHECK_HAS_OPENED, this);
            }
            if (var4.getConversationType() == Conversation.ConversationType.GROUP) {
                this.message = var3;
                this.isSingle = false;
                this.isForDetail = false;
                AsyncTaskManager.getInstance(context).request(REQUEST_CHECK_HAS_OPENED, this);
            }
        }
        
    }
    
    @Override
    public void onItemLongClick(View var1, int var2, GGRedPacketMessage var3, final UIMessage var4) {
    
    }
    
    @Override
    public View newView(Context var1, ViewGroup var2) {
        this.context = var1;
        action = new SealAction(context);
        View var3 = LayoutInflater.from(var1).inflate(R.layout.item_red_packet, null);
        GGRedPacketMessageProvider.ViewHolder var4 = new GGRedPacketMessageProvider.ViewHolder();
        var4.layout = (RelativeLayout) var3.findViewById(R.id.layout);
        var4.tv_bri_mess = (TextView) var3.findViewById(R.id.tv_bri_mess);
        var4.tv_bri_target = (TextView) var3.findViewById(R.id.tv_bri_target);
        var4.tv_bri_name = (TextView) var3.findViewById(R.id.tv_bri_name);
        var4.bri_bg = (RelativeLayout) var3.findViewById(R.id.bri_bg);
        var3.setTag(var4);
        return var3;
    }
	
	@Override
	public Object doInBackground(int requestCode, String parameter) throws HttpException {
		switch (requestCode) {
			case REQUEST_CHECK_HAS_OPENED:
				return action.checkRedPacketHasOpened(this.message.getRedpacketId());
			case REQUEST_CHECK_RED_PACKET_REMAIN:
				return action.checkRedPacketHasRemain(this.message.getRedpacketId());
			case REQUEST_USER_MONEY:
				return action.getRemainMoney();
			case REQUEST_DETAIL:
				return action.getRedPacketDetail(this.message.getRedpacketId());
			case REQUEST_MEMBERS:
				return action.getRedPacketMenber(this.message.getRedpacketId());
		}
		return null;
	}
	
	@Override
	public void onSuccess(int requestCode, Object result) {
		switch (requestCode) {
			case REQUEST_CHECK_HAS_OPENED:
				BaseResponse response = (BaseResponse) result;
				if (response.getCode() == 200) {//未拆过
					if (isSingle) {
						AsyncTaskManager.getInstance(context).request(REQUEST_DETAIL, this);
					} else {
						AsyncTaskManager.getInstance(context).request(REQUEST_CHECK_RED_PACKET_REMAIN, this);
					}
				} else if (response.getCode() == 66004) {//已经开过
					isForDetail = true;
					AsyncTaskManager.getInstance(context).request(REQUEST_DETAIL, this);
				}
				break;
			case REQUEST_CHECK_RED_PACKET_REMAIN:
				CheckRedPacketCountResponse checkRedPacketCountResponse = (CheckRedPacketCountResponse) result;
				if (checkRedPacketCountResponse.getCode() == 200) {
					if (checkRedPacketCountResponse.getData().getCount() > 0) {//红包还有剩余
						isLate = false;
					} else {//显示手慢了
						isLate = true;
					}
					AsyncTaskManager.getInstance(context).request(REQUEST_DETAIL, this);
				} else if (checkRedPacketCountResponse.getCode() == 66102) {
					showOpenRedPacketDialog(RedPacketOpenDialog.REDPACKET_STATE.OVERTIME);
					LoadDialog.dismiss(context);
				}
				break;
			case REQUEST_USER_MONEY:
				GetMoneyResponse getMoneyResponse = (GetMoneyResponse) result;
				if (getMoneyResponse.getCode() == 200) {
					if (getMoneyResponse.getData().getMoney() > 0) {//如果用户余额组够,显示抢红包
						AsyncTaskManager.getInstance(context).request(REQUEST_DETAIL, this);
					} else {
						NToast.shortToast(context, "余额不足，不能抢红包");
						LoadDialog.dismiss(context);
					}
				}
				break;
			case REQUEST_DETAIL:
				GetRedPacketDetailResponse packetDetailResponse = (GetRedPacketDetailResponse) result;
				if (packetDetailResponse.getCode() == 200) {
					this.detailEntity = packetDetailResponse.getData();
					if (isForDetail) {
						LoadDialog.show(context);
						AsyncTaskManager.getInstance(context).request(REQUEST_MEMBERS, this);
						return;
					}
					if (this.detailEntity.getState() == 3) {//过期
						showOpenRedPacketDialog(RedPacketOpenDialog.REDPACKET_STATE.OVERTIME);
						LoadDialog.dismiss(context);
						return;
					}
					LoadDialog.dismiss(context);
					if (isSingle) {
						showOpenRedPacketDialog(RedPacketOpenDialog.REDPACKET_STATE.NORMAL);
						return;
					}
					showOpenRedPacketDialog(isLate ? RedPacketOpenDialog.REDPACKET_STATE.LATE : RedPacketOpenDialog.REDPACKET_STATE.NORMAL);
				}
				break;
			case REQUEST_MEMBERS:
				LoadDialog.dismiss(context);
				GetRedPacketUsersResponse usersResponse = (GetRedPacketUsersResponse) result;
				if (usersResponse.getCode() == 200) {
					members = usersResponse.getData();
					if (!isSingle && isAfterOpen) {//如果是群红包点击打开
						String message = SharedPreferencesContext.getInstance().getName() + " 领取了您的红包";
						StringBuilder iosmessage = new StringBuilder(message);
						if (message.length() > 25) {
							iosmessage.insert(24, "\n");
						}
						String fromnickname = String.valueOf(this.message.getFromuserid()).equals(SharedPreferencesContext.getInstance().getUserId()) ?
								"自己" : this.detailEntity.getFromnickname();
						String tipmessage = "您领取了" + fromnickname + "的红包";
						RongIM.getInstance().sendMessage(Message.obtain(this.message.getTomemberid() + "", Conversation.ConversationType.GROUP,
								GGRedPacketNotifyMessage.obtain(this.message.getRedpacketId(), message, iosmessage.toString(),
										this.message.getFromuserid() + "", SharedPreferencesContext.getInstance().getUserId(), tipmessage, 0)), null, null,
								new IRongCallback.ISendMessageCallback() {
									@Override
									public void onAttached(Message message) {
										
									}
									
									@Override
									public void onSuccess(Message message) {
										isAfterOpen = false;
									}
									
									@Override
									public void onError(Message message, RongIMClient.ErrorCode errorCode) {
										
									}
								});
					} else if (isSingle && isAfterOpen) {
						
						String message = "您领取了" + this.detailEntity.getFromnickname() + "的红包";
						
						String message1 = SharedPreferencesContext.getInstance().getName() + "领取了您的红包";
						//发给发包人
						RongIM.getInstance().sendMessage(Message.obtain(this.message.getFromuserid() + "", Conversation.ConversationType.PRIVATE,
								GGRedPacketNotifyMessage.obtain(this.message.getRedpacketId(), message1, message1,
										this.message.getFromuserid() + "", SharedPreferencesContext.getInstance().getUserId(), message, 0)), null, null,
								new IRongCallback.ISendMessageCallback() {
									@Override
									public void onAttached(Message message) {
									
									}
									
									@Override
									public void onSuccess(Message message) {
										isAfterOpen = false;
									}
									
									@Override
									public void onError(Message message, RongIMClient.ErrorCode errorCode) {
									
									}
								});
					}
					gotoDetail();
				}
				break;
			default:
				break;
		}
		
	}
	
	@Override
	public void onFailure(int requestCode, int state, Object result) {
		
	}
	
	class ViewHolder {
		RelativeLayout layout;
		RelativeLayout bri_bg;
		TextView tv_bri_mess;
		TextView tv_bri_target;
		TextView tv_bri_name;
		
		ViewHolder() {
		}
	}
	
	private void gotoDetail() {
		Intent intent = new Intent(context, RedPacketDetailActivity.class);
		intent.putExtra("id", Integer.valueOf(this.message.getRedpacketId()));
		intent.putExtra("detail", this.detailEntity);
		intent.putExtra("members", this.members);
		context.startActivity(intent);
	}
	
	private void showOpenRedPacketDialog(RedPacketOpenDialog.REDPACKET_STATE state) {
		dialog = new RedPacketOpenDialog(context);
		dialog.setHeadIco(this.detailEntity.getFromheadico());
		dialog.setRedpacketId(this.message.getRedpacketId());
		dialog.setName(this.detailEntity.getFromnickname());
		dialog.setNote(this.detailEntity.getNote());
		dialog.setSingle(isSingle);
		dialog.setGroupId(this.targetId);//最好用this.message.getTomemeberid();
		dialog.setClickListener(new RedPacketOpenDialog.OnDetailClickListener() {
			@Override
			public void click(boolean isAfterOpen) {
				GGRedPacketMessageProvider.this.isAfterOpen = isAfterOpen;
				GGRedPacketMessageProvider.this.isForDetail = true;//不需要"是否是点击开启"这个判断标记了，只要是红包对话框点击事件，都是直接进去详情了。
				AsyncTaskManager.getInstance(context).request(REQUEST_DETAIL, GGRedPacketMessageProvider.this);
				dialog.dismiss();
			}
		});
		dialog.show(state);
	}
	
	
}
