package com.GuoGuo.JuicyChat.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.App;
import com.GuoGuo.JuicyChat.GGConst;
import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.SealAppContext;
import com.GuoGuo.JuicyChat.SealUserInfoManager;
import com.GuoGuo.JuicyChat.db.DBManager;
import com.GuoGuo.JuicyChat.db.Friend;
import com.GuoGuo.JuicyChat.db.GroupMember;
import com.GuoGuo.JuicyChat.db.Groups;
import com.GuoGuo.JuicyChat.db.GroupsDao;
import com.GuoGuo.JuicyChat.model.SealSearchConversationResult;
import com.GuoGuo.JuicyChat.server.broadcast.BroadcastManager;
import com.GuoGuo.JuicyChat.server.network.async.AsyncTaskManager;
import com.GuoGuo.JuicyChat.server.network.async.OnDataListener;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.pinyin.CharacterParser;
import com.GuoGuo.JuicyChat.server.response.BaseResponse;
import com.GuoGuo.JuicyChat.server.response.DismissGroupResponse;
import com.GuoGuo.JuicyChat.server.response.GetGroupInfoResponse;
import com.GuoGuo.JuicyChat.server.response.QiNiuTokenResponse;
import com.GuoGuo.JuicyChat.server.response.QuitGroupResponse;
import com.GuoGuo.JuicyChat.server.utils.CommonUtils;
import com.GuoGuo.JuicyChat.server.utils.NToast;
import com.GuoGuo.JuicyChat.server.utils.OperationRong;
import com.GuoGuo.JuicyChat.server.utils.RongGenerate;
import com.GuoGuo.JuicyChat.server.utils.StringUtils;
import com.GuoGuo.JuicyChat.server.utils.json.JsonMananger;
import com.GuoGuo.JuicyChat.server.utils.photo.PhotoUtils;
import com.GuoGuo.JuicyChat.server.widget.BottomMenuDialog;
import com.GuoGuo.JuicyChat.server.widget.DialogWithYesOrNoUtils;
import com.GuoGuo.JuicyChat.server.widget.LoadDialog;
import com.GuoGuo.JuicyChat.server.widget.SelectableRoundedImageView;
import com.GuoGuo.JuicyChat.ui.widget.DemoGridView;
import com.GuoGuo.JuicyChat.ui.widget.switchbutton.SwitchButton;
import com.GuoGuo.JuicyChat.utils.BitmapUtils;
import com.GuoGuo.JuicyChat.utils.SharedPreferencesContext;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.utilities.PromptPopupDialog;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/1/27.
 * Company RongCloud
 */
public class GroupDetailActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    
    private static final int CLICK_CONVERSATION_USER_PORTRAIT = 1;
    
    private static final int DISMISS_GROUP = 26;
    private static final int QUIT_GROUP = 27;
    private static final int SET_GROUP_NAME = 29;
    private static final int GET_GROUP_INFO = 30;
    private static final int UPDATE_GROUP_NAME = 31;
    private static final int GET_QI_NIU_TOKEN = 133;
    private static final int UPDATE_GROUP_HEADER = 25;
    private static final int SEARCH_TYPE_FLAG = 1;
    private static final int CHECKGROUPURL = 39;
    private static final int UPDATE_RED_PACKET = 866;
    private static final int UPDATE_LIMIE_MONEY = 832;
    
    private static final int REQUEST_UPDATE_NOTICE = 32;
    private static final int REQUEST_CAN_ADD_USER = 945;
    private static final int INTENT_ADD_AND_DEL_GROUP_MEMBER = 43;
    private static final int INTENT_UPDATE_NOTICE = 805;
    private static final int INTENT_GAG_MEMBER = 918;
    
    
    private boolean isCreated = false;
    private DemoGridView mGridView;
    private List<GroupMember> mGroupMember;
    private TextView mTextViewMemberSize, mGroupDisplayNameText;
    private SelectableRoundedImageView mGroupHeader;
    private SwitchButton messageTop, messageNotification;
    private Groups mGroup;
    private String fromConversationId;
    private Conversation.ConversationType mConversationType;
    private boolean isFromConversation;
    private LinearLayout mGroupAnnouncementDividerLinearLayout;
    private LinearLayout redPacketLL;
    private LinearLayout canAddUserLl;
    private SwitchButton canAddUserSb;
    private LinearLayout limitLl;
    private TextView redPacketTv;
    private TextView limitTv;
    private TextView mGroupName;
    private TextView announcementTv;
    private TextView gagTv;
    private PhotoUtils photoUtils;
    private BottomMenuDialog dialog;
    private UploadManager uploadManager;
    private String imageUrl;
    private Uri selectUri;
    private String newGroupName;
    private String newRedPacket;
    private String newLimitMoney;
    private String newNotice;
    private int newCanAddUser;
    private LinearLayout mGroupNotice;
    private LinearLayout mSearchMessagesLinearLayout;
    private TextView mUnlockMomeyTv;
    private Button mDismissBtn;
    private Button mQuitBtn;
    private SealSearchConversationResult mResult;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_group);
        initViews();
        setTitle(R.string.group_info);
        //群组会话界面点进群组详情
        fromConversationId = getIntent().getStringExtra("TargetId");
        mConversationType = (Conversation.ConversationType) getIntent().getSerializableExtra("conversationType");
        
        if (!TextUtils.isEmpty(fromConversationId)) {
            isFromConversation = true;
        }
        
        if (isFromConversation) {//群组会话页进入
            LoadDialog.show(mContext);
            getGroups();
            getGroupMembers();
        }
        setPortraitChangeListener();
        
        SealAppContext.getInstance().pushActivity(this);
        
        setGroupsInfoChangeListener();
    }
    
    private void getGroups() {
        SealUserInfoManager.getInstance().getGroupsByID(fromConversationId, new SealUserInfoManager.ResultCallback<Groups>() {
            
            @Override
            public void onSuccess(Groups groups) {
                if (groups != null) {
                    mGroup = groups;
                    initGroupData();
                }
            }
            
            @Override
            public void onError(String errString) {
            
            }
        });
    }
    
    private void getGroupMembers() {
        SealUserInfoManager.getInstance().getGroupMembers(fromConversationId, new SealUserInfoManager.ResultCallback<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                LoadDialog.dismiss(mContext);
                if (groupMembers != null && groupMembers.size() > 0) {
                    mGroupMember = groupMembers;
                    initGroupMemberData();
                }
            }
            
            @Override
            public void onError(String errString) {
                LoadDialog.dismiss(mContext);
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        BroadcastManager.getInstance(this).destroy(SealAppContext.UPDATE_GROUP_NAME);
        BroadcastManager.getInstance(this).destroy(SealAppContext.UPDATE_GROUP_MEMBER);
        BroadcastManager.getInstance(this).destroy(SealAppContext.GROUP_DISMISS);
        super.onDestroy();
    }
    
    private void initGroupData() {
        String portraitUri = mGroup.getGrouphead();
//        ImageLoader.getInstance().displayImage(portraitUri, mGroupHeader, App.getOptions());
        if (!TextUtils.isEmpty(portraitUri)) {
            Picasso.with(mContext).load(portraitUri).into(mGroupHeader);
        }
        mGroupName.setText(mGroup.getGroupname());
        limitTv.setText(StringUtils.getFormatMoney(mGroup.getLocklimit() + ""));
        redPacketTv.setText(StringUtils.getFormatMoney(mGroup.getRedpacketlimit() + ""));
        announcementTv.setText(TextUtils.isEmpty(mGroup.getGonggao()) ? "未设置" : isCreated ? "点击修改" : "点击查看");
        canAddUserSb.setChecked(mGroup.getIscanadduser() == 1);
        canAddUserSb.setOnCheckedChangeListener(this);
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP, mGroup.getGroupid(), new RongIMClient.ResultCallback<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {
                    if (conversation == null) {
                        return;
                    }
                    if (conversation.isTop()) {
                        messageTop.setChecked(true);
                    } else {
                        messageTop.setChecked(false);
                    }
                    
                }
                
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                
                }
            });
            
            RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.GROUP, mGroup.getGroupid(), new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                @Override
                public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                    
                    if (conversationNotificationStatus == Conversation.ConversationNotificationStatus.DO_NOT_DISTURB) {
                        messageNotification.setChecked(true);
                    } else {
                        messageNotification.setChecked(false);
                    }
                }
                
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                
                }
            });
        }
        
        if (String.valueOf(mGroup.getLeaderid()).equals(SharedPreferencesContext.getInstance().getUserId())) {
            isCreated = true;
        }
        if (!isCreated) {
            mGroupAnnouncementDividerLinearLayout.setVisibility(View.VISIBLE);
            mGroupNotice.setVisibility(View.VISIBLE);
            canAddUserLl.setVisibility(View.GONE);
//			gagTv.setVisibility(View.GONE);
        } else {
            mGroupAnnouncementDividerLinearLayout.setVisibility(View.VISIBLE);
            mDismissBtn.setVisibility(View.VISIBLE);
            mQuitBtn.setVisibility(View.GONE);
            mGroupNotice.setVisibility(View.VISIBLE);
            canAddUserLl.setVisibility(View.VISIBLE);
//			gagTv.setVisibility(View.VISIBLE);
        }
        if (CommonUtils.isNetworkConnected(mContext)) {
            request(CHECKGROUPURL);
        }
    }
    
    private void initGroupMemberData() {
        if (mGroupMember != null && mGroupMember.size() > 0) {
            setTitle(getString(R.string.group_info) + "(" + mGroupMember.size() + ")");
            mTextViewMemberSize.setText(getString(R.string.group_member_size) + "(" + mGroupMember.size() + ")");
            mGridView.setAdapter(new GridAdapter(mContext, mGroupMember));
        } else {
            return;
        }
        
        for (GroupMember member : mGroupMember) {
            if (member.getGroupid().equals(getSharedPreferences("config", MODE_PRIVATE).getString(GGConst.GUOGUO_LOGIN_ID, ""))) {
                if (!TextUtils.isEmpty(member.getGroupname())) {
                    mGroupDisplayNameText.setText(member.getGroupname());
                } else {
                    mGroupDisplayNameText.setText("无");
                }
            }
        }
    }
    
    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case QUIT_GROUP:
                return action.quitGroup(fromConversationId);
            case DISMISS_GROUP:
                return action.dissmissGroup(fromConversationId);
            case SET_GROUP_NAME:
                return action.updateGroupInfo(fromConversationId, newGroupName, mGroup.getRedpacketlimit() + "", mGroup.getLocklimit() + "", mGroup.getGrouphead(), mGroup.getIsnonotice(), mGroup.getGonggao(), mGroup.getIscanadduser());
            case GET_GROUP_INFO:
                return action.getGroupInfo(fromConversationId);
            case UPDATE_GROUP_HEADER:
                return action.updateGroupInfo(fromConversationId, mGroup.getGroupname(), mGroup.getRedpacketlimit() + "", mGroup.getLocklimit() + "", imageUrl, mGroup.getIsnonotice(), mGroup.getGonggao(), mGroup.getIscanadduser());
            case GET_QI_NIU_TOKEN:
                return action.getQiNiuToken();
            case UPDATE_GROUP_NAME:
                return action.updateGroupInfo(fromConversationId, newGroupName, mGroup.getRedpacketlimit() + "", mGroup.getLocklimit() + "", mGroup.getGrouphead(), mGroup.getIsnonotice(), mGroup.getGonggao(), mGroup.getIscanadduser());
            case CHECKGROUPURL:
                return action.getGroupInfo(fromConversationId);
            case UPDATE_RED_PACKET:
                return action.updateGroupInfo(fromConversationId, mGroup.getGroupname(), newRedPacket, mGroup.getLocklimit() + "", mGroup.getGrouphead(), mGroup.getIsnonotice(), mGroup.getGonggao(), mGroup.getIscanadduser());
            case UPDATE_LIMIE_MONEY:
                return action.updateGroupInfo(fromConversationId, mGroup.getGroupname(), mGroup.getRedpacketlimit() + "", newLimitMoney, mGroup.getGrouphead(), mGroup.getIsnonotice(), mGroup.getGonggao(), mGroup.getIscanadduser());
            case REQUEST_UPDATE_NOTICE:
                return action.updateGroupInfo(fromConversationId, mGroup.getGroupname(), mGroup.getRedpacketlimit() + "", mGroup.getLocklimit() + "", mGroup.getGrouphead(), mGroup.getIsnonotice(), newNotice, mGroup.getIscanadduser());
            case REQUEST_CAN_ADD_USER:
                return action.updateGroupInfo(fromConversationId, mGroup.getGroupname(), mGroup.getRedpacketlimit() + "", mGroup.getLocklimit() + "", mGroup.getGrouphead(), mGroup.getIsnonotice(), mGroup.getGonggao(), newCanAddUser);
            
        }
        return super.doInBackground(requestCode, id);
    }
    
    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case QUIT_GROUP:
                    QuitGroupResponse response = (QuitGroupResponse) result;
                    if (response.getCode() == 200) {
                        
                        RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP, fromConversationId, new RongIMClient.ResultCallback<Conversation>() {
                            @Override
                            public void onSuccess(Conversation conversation) {
                                RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, fromConversationId, new RongIMClient.ResultCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                        RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, fromConversationId, null);
                                    }
                                    
                                    @Override
                                    public void onError(RongIMClient.ErrorCode e) {
                                    
                                    }
                                });
                            }
                            
                            @Override
                            public void onError(RongIMClient.ErrorCode e) {
                            
                            }
                        });
                        SealUserInfoManager.getInstance().deleteGroups(mGroup);
                        SealUserInfoManager.getInstance().deleteGroupMembers(fromConversationId);
                        BroadcastManager.getInstance(mContext).sendBroadcast(GGConst.GROUP_LIST_UPDATE);
                        setResult(501, new Intent());
                        NToast.shortToast(mContext, getString(R.string.quit_success));
                        LoadDialog.dismiss(mContext);
                        finish();
                    }
                    break;
                
                case DISMISS_GROUP:
                    DismissGroupResponse response1 = (DismissGroupResponse) result;
                    if (response1.getCode() == 200) {
                        RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP, fromConversationId, new RongIMClient.ResultCallback<Conversation>() {
                            @Override
                            public void onSuccess(Conversation conversation) {
                                RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, fromConversationId, new RongIMClient.ResultCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                        RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, fromConversationId, null);
                                    }
                                    
                                    @Override
                                    public void onError(RongIMClient.ErrorCode e) {
                                    
                                    }
                                });
                            }
                            
                            @Override
                            public void onError(RongIMClient.ErrorCode e) {
                            
                            }
                        });
                        SealUserInfoManager.getInstance().deleteGroups(mGroup);
                        SealUserInfoManager.getInstance().deleteGroupMembers(fromConversationId);
                        BroadcastManager.getInstance(mContext).sendBroadcast(GGConst.GROUP_LIST_UPDATE);
                        setResult(501, new Intent());
                        NToast.shortToast(mContext, getString(R.string.dismiss_success));
                        LoadDialog.dismiss(mContext);
                        finish();
                    }
                    break;
                
                case SET_GROUP_NAME:
                    BaseResponse response2 = (BaseResponse) result;
                    if (response2.getCode() == 200) {
                        request(GET_GROUP_INFO);
                        mGroupName.setText(newGroupName);
                    }
                    break;
                case GET_GROUP_INFO:
                    GetGroupInfoResponse response3 = (GetGroupInfoResponse) result;
                    if (response3.getCode() == 200) {
                        Groups bean = response3.getData().toGroups();
                        SealUserInfoManager.getInstance().addGroup(bean);
                        mGroup = bean;
                        RongIM.getInstance().refreshGroupInfoCache(new Group(fromConversationId, bean.getGroupname(), Uri.parse(bean.getGrouphead())));
                        LoadDialog.dismiss(mContext);
                        BroadcastManager.getInstance(mContext).sendBroadcast(GGConst.GROUP_LIST_UPDATE);
                        NToast.shortToast(mContext, getString(R.string.update_success));
                    }
                    break;
                case UPDATE_GROUP_HEADER:
                    BaseResponse response5 = (BaseResponse) result;
                    if (response5.getCode() == 200) {
                        ImageLoader.getInstance().displayImage(imageUrl, mGroupHeader, App.getOptions());
                        RongIM.getInstance().refreshGroupInfoCache(new Group(fromConversationId, mGroup.getGroupname(), Uri.parse(imageUrl)));
                        LoadDialog.dismiss(mContext);
                        mGroup.setGrouphead(imageUrl);
                        SealUserInfoManager.getInstance().addGroup(mGroup);
                        BroadcastManager.getInstance(mContext).sendBroadcast(GGConst.GROUP_LIST_UPDATE);
                        NToast.shortToast(mContext, getString(R.string.update_success));
                    }
                    
                    break;
                case GET_QI_NIU_TOKEN:
                    QiNiuTokenResponse response6 = (QiNiuTokenResponse) result;
                    if (response6.getCode() == 200) {
                        uploadImage("", response6.getData().getQiniutoken(), selectUri);
                    }
                    break;
                case UPDATE_GROUP_NAME:
                    BaseResponse response8 = (BaseResponse) result;
                    if (response8.getCode() == 200) {
                        request(GET_GROUP_INFO);
                        mGroupName.setText(newGroupName);
                    }
                    break;
                case CHECKGROUPURL:
                    GetGroupInfoResponse groupInfoResponse = (GetGroupInfoResponse) result;
                    if (groupInfoResponse.getCode() == 200) {
                        if (groupInfoResponse.getData() != null) {
                            mGroupName.setText(groupInfoResponse.getData().getGroupname());
                            String portraitUri = groupInfoResponse.getData().getGroupico();
                            ImageLoader.getInstance().displayImage(portraitUri, mGroupHeader, App.getOptions());
                            RongIM.getInstance().refreshGroupInfoCache(new Group(fromConversationId, groupInfoResponse.getData().getGroupname(), TextUtils.isEmpty(groupInfoResponse.getData().getGroupico()) ? Uri.parse(RongGenerate.generateDefaultAvatar(groupInfoResponse.getData().getGroupname(), groupInfoResponse.getData().getGroupid())) : Uri.parse(groupInfoResponse.getData().getGroupico())));
                        }
                    }
                    break;
                case UPDATE_RED_PACKET:
                    BaseResponse response4 = (BaseResponse) result;
                    LoadDialog.dismiss(mContext);
                    if (response4.getCode() == 200) {
                        request(GET_GROUP_INFO);
                        redPacketTv.setText(newRedPacket);
                    }
                    break;
                case UPDATE_LIMIE_MONEY:
                    BaseResponse response9 = (BaseResponse) result;
                    LoadDialog.dismiss(mContext);
                    if (response9.getCode() == 200) {
                        request(GET_GROUP_INFO);
                        limitTv.setText(newLimitMoney);
                    }
                    break;
                case REQUEST_UPDATE_NOTICE:
                    BaseResponse response10 = (BaseResponse) result;
                    LoadDialog.dismiss(mContext);
                    if (response10.getCode() == 200) {
                        request(GET_GROUP_INFO);
                        announcementTv.setText(newNotice);
                    }
                    break;
                case REQUEST_CAN_ADD_USER:
                    BaseResponse response11 = (BaseResponse) result;
                    LoadDialog.dismiss(mContext);
                    if (response11.getCode() == 200) {
                        request(GET_GROUP_INFO);
                    }
                    break;
                
            }
        }
    }
    
    
    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case QUIT_GROUP:
                NToast.shortToast(mContext, "退出群组请求失败");
                LoadDialog.dismiss(mContext);
                break;
            case DISMISS_GROUP:
                NToast.shortToast(mContext, "解散群组请求失败");
                LoadDialog.dismiss(mContext);
                break;
        }
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.group_quit:
                DialogWithYesOrNoUtils.getInstance().showDialog(mContext, getString(R.string.confirm_quit_group), new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void executeEvent() {
                        LoadDialog.show(mContext);
                        request(QUIT_GROUP);
                    }
                    
                    @Override
                    public void executeEditEvent(String editText) {
                    
                    }
                    
                    @Override
                    public void updatePassword(String oldPassword, String newPassword) {
                    
                    }
                });
                break;
            case R.id.group_dismiss:
                DialogWithYesOrNoUtils.getInstance().showDialog(mContext, getString(R.string.confirm_dismiss_group), new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void executeEvent() {
                        LoadDialog.show(mContext);
                        request(DISMISS_GROUP);
                    }
                    
                    @Override
                    public void executeEditEvent(String editText) {
                    
                    }
                    
                    @Override
                    public void updatePassword(String oldPassword, String newPassword) {
                    
                    }
                });
                break;
            case R.id.ac_ll_search_chatting_records:
                Intent searchIntent = new Intent(GroupDetailActivity.this, SealSearchChattingDetailActivity.class);
                searchIntent.putExtra("filterString", "");
                ArrayList<Message> arrayList = new ArrayList<>();
                searchIntent.putParcelableArrayListExtra("filterMessages", arrayList);
                mResult = new SealSearchConversationResult();
                Conversation conversation = new Conversation();
                conversation.setTargetId(fromConversationId);
                conversation.setConversationType(mConversationType);
                mResult.setConversation(conversation);
                Groups groupInfo = DBManager.getInstance().getDaoSession().getGroupsDao().queryBuilder().where(GroupsDao.Properties.Groupid.eq(fromConversationId)).unique();
                if (groupInfo != null) {
                    String portraitUri = groupInfo.getGrouphead();
                    mResult.setId(groupInfo.getGroupid());
                    
                    if (!TextUtils.isEmpty(portraitUri)) {
                        mResult.setPortraitUri(portraitUri);
                    }
                    if (!TextUtils.isEmpty(groupInfo.getGroupname())) {
                        mResult.setTitle(groupInfo.getGroupname());
                    } else {
                        mResult.setTitle(groupInfo.getGroupid());
                    }
                    
                    searchIntent.putExtra("searchConversationResult", mResult);
                    searchIntent.putExtra("flag", SEARCH_TYPE_FLAG);
                    startActivity(searchIntent);
                }
                break;
            case R.id.group_clean:
                PromptPopupDialog.newInstance(mContext,
                        getString(R.string.clean_group_chat_history)).setLayoutRes(io.rong.imkit.R.layout.rc_dialog_popup_prompt_warning)
                        .setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
                            @Override
                            public void onPositiveButtonClicked() {
                                if (RongIM.getInstance() != null) {
                                    if (mGroup != null) {
                                        RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, mGroup.getGroupid(), new RongIMClient.ResultCallback<Boolean>() {
                                            @Override
                                            public void onSuccess(Boolean aBoolean) {
                                                NToast.shortToast(mContext, getString(R.string.clear_success));
                                            }
                                            
                                            @Override
                                            public void onError(RongIMClient.ErrorCode errorCode) {
                                                NToast.shortToast(mContext, getString(R.string.clear_failure));
                                            }
                                        });
                                    }
                                }
                            }
                        }).show();
                
                break;
            case R.id.group_member_size_item:
                Intent intent = new Intent(mContext, TotalGroupMemberActivity.class);
                intent.putExtra("targetId", fromConversationId);
                startActivity(intent);
                break;
            case R.id.ll_group_port:
                if (isCreated) {
                    showPhotoDialog();
                }
                break;
            case R.id.ll_group_name:
                if (isCreated) {
                    DialogWithYesOrNoUtils.getInstance().showEditDialog(mContext, getString(R.string.new_group_name), getString(R.string.confirm), new DialogWithYesOrNoUtils.DialogCallBack() {
                        @Override
                        public void executeEvent() {
                        
                        }
                        
                        @Override
                        public void executeEditEvent(String editText) {
                            if (TextUtils.isEmpty(editText)) {
                                return;
                            }
                            if (editText.length() < 2 && editText.length() > 20) {
                                NToast.shortToast(mContext, "群名称应为 2-20 字");
                                return;
                            }
                            
                            if (AndroidEmoji.isEmoji(editText) && editText.length() < 4) {
                                NToast.shortToast(mContext, "群名称表情过短");
                                return;
                            }
                            newGroupName = editText;
                            LoadDialog.show(mContext);
                            request(UPDATE_GROUP_NAME);
                        }
                        
                        @Override
                        public void updatePassword(String oldPassword, String newPassword) {
                        
                        }
                    });
                }
                break;
            case R.id.group_announcement://公告
                Intent tempIntent = new Intent(mContext, GroupNoticeActivity.class);
                tempIntent.putExtra("conversationType", Conversation.ConversationType.GROUP.getValue());
                tempIntent.putExtra("targetId", fromConversationId);
                tempIntent.putExtra("gonggao", mGroup.getGonggao());
                tempIntent.putExtra("isCreated", isCreated);
                startActivityForResult(tempIntent, INTENT_UPDATE_NOTICE);
                break;
            case R.id.ll_group_red_packet:
                if (isCreated) {
                    DialogWithYesOrNoUtils.getInstance().showEditDialog(mContext, "红包下限", getString(R.string.confirm), InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED, new DialogWithYesOrNoUtils.DialogCallBack() {
                        @Override
                        public void executeEvent() {
                        
                        }
                        
                        @Override
                        public void executeEditEvent(String editText) {
                            newRedPacket = editText;
                            LoadDialog.show(mContext);
                            request(UPDATE_RED_PACKET);
                        }
                        
                        @Override
                        public void updatePassword(String oldPassword, String newPassword) {
                        
                        }
                    });
                } else {
                    NToast.shortToast(mContext, "只有群主能设置该项");
                }
                break;
            case R.id.group_limit_money_ll:
                if (isCreated) {
                    DialogWithYesOrNoUtils.getInstance().showEditDialog(mContext, "冻结金额", getString(R.string.confirm), InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED, new DialogWithYesOrNoUtils.DialogCallBack() {
                        @Override
                        public void executeEvent() {
                        
                        }
                        
                        @Override
                        public void executeEditEvent(String editText) {
                            newLimitMoney = editText;
                            LoadDialog.show(mContext);
                            request(UPDATE_LIMIE_MONEY);
                        }
                        
                        @Override
                        public void updatePassword(String oldPassword, String newPassword) {
                        
                        }
                    });
                } else {
                    NToast.shortToast(mContext, "只有群主能设置该项");
                }
                break;
            case R.id.group_unlock_limit_money_tv:
                if (isCreated) {
                    Intent in = new Intent(GroupDetailActivity.this, SelectFriendsActivity.class);
                    in.putExtra("isUnlockMoneyMember", true);
                    in.putExtra("GroupId", mGroup.getGroupid());
                    startActivity(in);
                } else {
                    NToast.shortToast(mContext, "只有群主能设置该项");
                }
                break;
            case R.id.group_detail_gag_tv:
                Intent gagIntent = new Intent(GroupDetailActivity.this, SelectFriendsActivity.class);
                gagIntent.putExtra("isGagMember", true);
                gagIntent.putExtra("GroupId", mGroup.getGroupid());
                startActivityForResult(gagIntent, INTENT_GAG_MEMBER);
                break;
            default:
                break;
        }
    }
    
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_group_top:
                if (isChecked) {
                    if (mGroup != null) {
                        OperationRong.setConversationTop(mContext, Conversation.ConversationType.GROUP, mGroup.getGroupid(), true);
                    }
                } else {
                    if (mGroup != null) {
                        OperationRong.setConversationTop(mContext, Conversation.ConversationType.GROUP, mGroup.getGroupid(), false);
                    }
                }
                break;
            case R.id.sw_group_notfaction:
                if (isChecked) {
                    if (mGroup != null) {
                        OperationRong.setConverstionNotif(mContext, Conversation.ConversationType.GROUP, mGroup.getGroupid(), true);
                    }
                } else {
                    if (mGroup != null) {
                        OperationRong.setConverstionNotif(mContext, Conversation.ConversationType.GROUP, mGroup.getGroupid(), false);
                    }
                }
                break;
            case R.id.sw_group_canadduser:
                newCanAddUser = isChecked ? 1 : 0;
                LoadDialog.show(this);
                request(REQUEST_CAN_ADD_USER);
                break;
            
        }
    }
    
    
    private class GridAdapter extends BaseAdapter {
        
        private List<GroupMember> list;
        Context context;
        
        
        public GridAdapter(Context context, List<GroupMember> list) {
            if (list.size() >= 31) {
                this.list = list.subList(0, 30);
            } else {
                this.list = list;
            }
            
            this.context = context;
        }
        
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.social_chatsetting_gridview_item, parent, false);
            }
            SelectableRoundedImageView iv_avatar = (SelectableRoundedImageView) convertView.findViewById(R.id.iv_avatar);
            TextView tv_username = (TextView) convertView.findViewById(R.id.tv_username);
            ImageView badge_delete = (ImageView) convertView.findViewById(R.id.badge_delete);
            
            // 最后一个item，减人按钮
            if (position == getCount() - 1 && isCreated) {
                tv_username.setText("");
                badge_delete.setVisibility(View.GONE);
                iv_avatar.setImageResource(R.drawable.icon_btn_deleteperson);
                
                iv_avatar.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GroupDetailActivity.this, SelectFriendsActivity.class);
                        intent.putExtra("isDeleteGroupMember", true);
                        intent.putExtra("GroupId", mGroup.getGroupid());
                        startActivityForResult(intent, INTENT_ADD_AND_DEL_GROUP_MEMBER);
                    }
                    
                });
            } else if ((position == getCount() - 2) && isCreated) {
                tv_username.setText("");
                badge_delete.setVisibility(View.GONE);
                iv_avatar.setImageResource(R.drawable.jy_drltsz_btn_addperson);
                
                iv_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GroupDetailActivity.this, SelectFriendsActivity.class);
                        intent.putExtra("isAddGroupMember", true);
                        intent.putExtra("GroupId", mGroup.getGroupid());
                        startActivityForResult(intent, INTENT_ADD_AND_DEL_GROUP_MEMBER);
                        
                    }
                });
            } else if ((position == getCount() - 1) && !isCreated && mGroup.getIscanadduser() == 1) {
                tv_username.setText("");
                badge_delete.setVisibility(View.GONE);
                iv_avatar.setImageResource(R.drawable.jy_drltsz_btn_addperson);
                
                iv_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GroupDetailActivity.this, SelectFriendsActivity.class);
                        intent.putExtra("isAddGroupMember", true);
                        intent.putExtra("GroupId", mGroup.getGroupid());
                        startActivityForResult(intent, INTENT_ADD_AND_DEL_GROUP_MEMBER);
                        
                    }
                });
            } else { // 普通成员
                final GroupMember bean = list.get(position);
                Friend friend = SealUserInfoManager.getInstance().getFriendByID(bean.getUserid());
                if (friend != null) {
                    tv_username.setText(SealUserInfoManager.getInstance().getDiaplayName(friend));
                } else {
                    tv_username.setText(TextUtils.isEmpty(bean.getRemark()) ? TextUtils.isEmpty(bean.getNickname()) ? bean.getUsername() : bean.getNickname() : bean.getRemark());
                }
                
                String portraitUri = bean.getUserhead();
                ImageLoader.getInstance().displayImage(portraitUri, iv_avatar, App.getOptions());
                iv_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserInfo userInfo = new UserInfo(bean.getUserid(), TextUtils.isEmpty(bean.getRemark()) ? TextUtils.isEmpty(bean.getNickname()) ? bean.getUsername() : bean.getNickname() : bean.getRemark(), TextUtils.isEmpty(bean.getUserhead()) ? Uri.parse(RongGenerate.generateDefaultAvatar(bean.getNickname(), bean.getUserid())) : Uri.parse(bean.getUserhead()));
                        Intent intent = new Intent(context, UserDetailActivity.class);
                        Friend friend = CharacterParser.getInstance().generateFriendFromUserInfo(userInfo);
                        intent.putExtra("friend", friend);
                        intent.putExtra("conversationType", Conversation.ConversationType.GROUP.getValue());
                        //Groups not Serializable,just need group name
                        intent.putExtra("groupName", mGroup.getGroupname());
                        intent.putExtra("type", CLICK_CONVERSATION_USER_PORTRAIT);
                        context.startActivity(intent);
                    }
                    
                });
                
            }
            
            return convertView;
        }
        
        @Override
        public int getCount() {
            if (isCreated) {
                return list.size() + 2;
            } else {
                if (mGroup.getIscanadduser() == 1) {
                    return list.size() + 1;
                } else {
                    return list.size();
                }
            }
        }
        
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }
        
        @Override
        public long getItemId(int position) {
            return position;
        }
        
        /**
         * 传入新的数据 刷新UI的方法
         */
        public void updateListView(List<GroupMember> list) {
            this.list = list;
            notifyDataSetChanged();
        }
        
    }
    
    
    // 拿到新增的成员刷新adapter
    @Override
    @SuppressWarnings("unchecked")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        switch (requestCode) {
            case INTENT_ADD_AND_DEL_GROUP_MEMBER:
                if (data != null) {
                    List<Friend> newMemberData = (List<Friend>) data.getSerializableExtra("newAddMember");
                    List<Friend> deleMember = (List<Friend>) data.getSerializableExtra("deleteMember");
                    if (newMemberData != null && newMemberData.size() > 0) {
                        for (Friend friend : newMemberData) {
                            GroupMember member = new GroupMember("", mGroup.getGroupid(), friend.getFriendid(),
                                    mGroup.getGroupname(), 0, 0, mGroup.getGrouphead(), friend.getUsername(),
                                    friend.getNickname(), friend.getHeadico(), friend.getSex(), mGroup.getLeaderid(),
                                    mGroup.getIsnonotice(), friend.getRemark());
                            mGroupMember.add(1, member);
                            SealUserInfoManager.getInstance().addGroupMember(member);
                        }
                        initGroupMemberData();
                    } else if (deleMember != null && deleMember.size() > 0) {
                        ArrayList<String> delIds = new ArrayList<>();
                        for (Friend friend : deleMember) {
                            for (GroupMember member : mGroupMember) {
                                if (member.getUserid().equals(friend.getFriendid())) {
                                    mGroupMember.remove(member);
                                    break;
                                }
                            }
                            delIds.add(friend.getFriendid());
                        }
                        SealUserInfoManager.getInstance().deleteGroupMembers(mGroup.getGroupid(), delIds);
                        initGroupMemberData();
                    }
                    
                }
                break;
            case INTENT_UPDATE_NOTICE:
                if (data != null) {
                    newNotice = data.getStringExtra("notice");
                    LoadDialog.show(this);
                    request(REQUEST_UPDATE_NOTICE);
                }
                break;
            case PhotoUtils.INTENT_CROP:
                photoUtils.onActivityResult(GroupDetailActivity.this, requestCode, resultCode, data);
                break;
            case PhotoUtils.INTENT_TAKE:
                photoUtils.onActivityResult(GroupDetailActivity.this, requestCode, resultCode, data);
                break;
            case PhotoUtils.INTENT_SELECT:
                photoUtils.onActivityResult(GroupDetailActivity.this, requestCode, resultCode, data);
                break;
        }
        
    }
    
    private void setGroupsInfoChangeListener() {
        //有些权限只有群主有,比如修改群名称等,已经更新UI不需要再更新
        BroadcastManager.getInstance(this).addAction(SealAppContext.UPDATE_GROUP_NAME, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String result = intent.getStringExtra("result");
                    if (result != null) {
                        try {
                            List<String> nameList = JsonMananger.jsonToBean(result, List.class);
                            if (nameList.size() != 3) {
                                return;
                            }
                            String groupID = nameList.get(0);
                            if (groupID != null && !groupID.equals(fromConversationId)) {
                                return;
                            }
                            if (mGroup != null && String.valueOf(mGroup.getLeaderid()).equals(SharedPreferencesContext.getInstance().getUserId())) {
                                return;
                            }
                            String groupName = nameList.get(1);
                            String operationName = nameList.get(2);
                            mGroupName.setText(groupName);
                            newGroupName = groupName;
                            NToast.shortToast(mContext, operationName + context.getString(R.string.rc_item_change_group_name)
                                    + "\"" + groupName + "\"");
                            RongIM.getInstance().refreshGroupInfoCache(new Group(fromConversationId, newGroupName, TextUtils.isEmpty(mGroup.getGrouphead()) ? Uri.parse(RongGenerate.generateDefaultAvatar(newGroupName, mGroup.getGroupid())) : Uri.parse(mGroup.getGrouphead())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                    }
                }
            }
        });
        BroadcastManager.getInstance(this).addAction(SealAppContext.UPDATE_GROUP_MEMBER, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String groupID = intent.getStringExtra("String");
                    if (groupID != null && groupID.equals(fromConversationId)) {
                        getGroupMembers();
                    }
                }
            }
        });
        BroadcastManager.getInstance(this).addAction(SealAppContext.GROUP_DISMISS, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String groupID = intent.getStringExtra("String");
                    if (groupID != null && groupID.equals(fromConversationId)) {
                        if (!mGroup.getLeaderid().equals(SharedPreferencesContext.getInstance().getUserId())) {
                            backAsGroupDismiss();
                        }
                    }
                }
            }
        });
    }
    
    private void backAsGroupDismiss() {
        this.setResult(501, new Intent());
        finish();
    }
    
    private void setPortraitChangeListener() {
        photoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(Uri uri) {
                if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
                    selectUri = uri;
                    AsyncTaskManager.getInstance(mContext).request(5, new OnDataListener() {
                        @Override
                        public Object doInBackground(int requestCode, String parameter) throws HttpException {
                            Bitmap bm = BitmapUtils.createBitmap(BitmapFactory.decodeFile(selectUri.getPath()), BitmapFactory.decodeResource(getResources(), R.drawable.icon_shuiyin));
                            try {
                                File file = new File(selectUri.getPath());
                                if (file.exists()) {
                                    file.delete();
                                }
                                FileOutputStream out = new FileOutputStream(file);
                                bm.compress(Bitmap.CompressFormat.PNG, 90, out);
                                out.flush();
                                out.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                        
                        @Override
                        public void onSuccess(int requestCode, Object result) {
                            LoadDialog.show(mContext);
                            request(GET_QI_NIU_TOKEN);
                        }
                        
                        @Override
                        public void onFailure(int requestCode, int state, Object result) {
                        
                        }
                    });
                    
                    
                }
            }
            
            @Override
            public void onPhotoCancel() {
            
            }
        });
    }
    
    static public final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    
    /**
     * 弹出底部框
     */
    private void showPhotoDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        
        dialog = new BottomMenuDialog(mContext);
        dialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (checkCameraAndSDPermission()) {
                    photoUtils.takePicture(GroupDetailActivity.this);
                }
            }
        });
        dialog.setMiddleListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (checkCameraAndSDPermission()) {
                    photoUtils.selectPicture(GroupDetailActivity.this);
                }
            }
        });
        dialog.show();
    }
    
    
    public void uploadImage(final String domain, String imageToken, Uri imagePath) {
        if (TextUtils.isEmpty(domain) && TextUtils.isEmpty(imageToken) && TextUtils.isEmpty(imagePath.toString())) {
            throw new RuntimeException("upload parameter is null!");
        }
        File imageFile = new File(imagePath.getPath());
        
        if (this.uploadManager == null) {
            this.uploadManager = new UploadManager();
        }
        this.uploadManager.put(imageFile, System.currentTimeMillis() + Math.random() * 1000 + imageFile.getName(), imageToken, new UpCompletionHandler() {
            
            @Override
            public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                if (responseInfo.isOK()) {
                    try {
                        String key = (String) jsonObject.get("key");
                        imageUrl = GGConst.QINIU_URL + key;
                        Log.e("uploadImage", imageUrl);
                        if (!TextUtils.isEmpty(imageUrl)) {
                            request(UPDATE_GROUP_HEADER);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, null);
    }
    
    
    private void initViews() {
        messageTop = (SwitchButton) findViewById(R.id.sw_group_top);
        messageNotification = (SwitchButton) findViewById(R.id.sw_group_notfaction);
        messageTop.setOnCheckedChangeListener(this);
        messageNotification.setOnCheckedChangeListener(this);
        LinearLayout groupClean = (LinearLayout) findViewById(R.id.group_clean);
        mGridView = (DemoGridView) findViewById(R.id.gridview);
        mTextViewMemberSize = (TextView) findViewById(R.id.group_member_size);
        mGroupHeader = (SelectableRoundedImageView) findViewById(R.id.group_header);
        LinearLayout mGroupDisplayName = (LinearLayout) findViewById(R.id.group_displayname);
        mGroupDisplayNameText = (TextView) findViewById(R.id.group_displayname_text);
        mUnlockMomeyTv = (TextView) findViewById(R.id.group_unlock_limit_money_tv);
        mGroupName = (TextView) findViewById(R.id.group_name);
        mQuitBtn = (Button) findViewById(R.id.group_quit);
        mDismissBtn = (Button) findViewById(R.id.group_dismiss);
        gagTv = (TextView) findViewById(R.id.group_detail_gag_tv);
        RelativeLayout totalGroupMember = (RelativeLayout) findViewById(R.id.group_member_size_item);
        LinearLayout mGroupPortL = (LinearLayout) findViewById(R.id.ll_group_port);
        LinearLayout mGroupNameL = (LinearLayout) findViewById(R.id.ll_group_name);
        mGroupAnnouncementDividerLinearLayout = (LinearLayout) findViewById(R.id.ac_ll_group_announcement_divider);
        mGroupNotice = (LinearLayout) findViewById(R.id.group_announcement);
        mSearchMessagesLinearLayout = (LinearLayout) findViewById(R.id.ac_ll_search_chatting_records);
        redPacketLL = (LinearLayout) findViewById(R.id.ll_group_red_packet);
        limitLl = (LinearLayout) findViewById(R.id.group_limit_money_ll);
        redPacketTv = (TextView) findViewById(R.id.group_red_packet_tv);
        limitTv = (TextView) findViewById(R.id.group_limit_money_tv);
        announcementTv = (TextView) findViewById(R.id.group_announcement_tv);
        canAddUserLl = (LinearLayout) findViewById(R.id.sw_group_canadduser_ll);
        canAddUserSb = (SwitchButton) findViewById(R.id.sw_group_canadduser);
        mGroupPortL.setOnClickListener(this);
        mGroupNameL.setOnClickListener(this);
        redPacketLL.setOnClickListener(this);
        limitLl.setOnClickListener(this);
        totalGroupMember.setOnClickListener(this);
        mGroupDisplayName.setOnClickListener(this);
        mQuitBtn.setOnClickListener(this);
        mDismissBtn.setOnClickListener(this);
        groupClean.setOnClickListener(this);
        mGroupNotice.setOnClickListener(this);
        mSearchMessagesLinearLayout.setOnClickListener(this);
        mUnlockMomeyTv.setOnClickListener(this);
        gagTv.setOnClickListener(this);
    }
    
    @Override
    public void onBackPressed() {
        SealAppContext.getInstance().popActivity(this);
        super.onBackPressed();
    }
}
