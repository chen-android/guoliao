package com.GuoGuo.JuicyChat.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.SealAppContext;
import com.GuoGuo.JuicyChat.SealUserInfoManager;
import com.GuoGuo.JuicyChat.db.Friend;
import com.GuoGuo.JuicyChat.server.broadcast.BroadcastManager;
import com.GuoGuo.JuicyChat.server.event.UpdateFriendDeal;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.BaseResponse;
import com.GuoGuo.JuicyChat.server.response.GetFriendListResponse;
import com.GuoGuo.JuicyChat.server.utils.CommonUtils;
import com.GuoGuo.JuicyChat.server.utils.NToast;
import com.GuoGuo.JuicyChat.server.widget.DialogWithYesOrNoUtils;
import com.GuoGuo.JuicyChat.server.widget.LoadDialog;
import com.GuoGuo.JuicyChat.ui.adapter.NewFriendListAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;


public class NewFriendListActivity extends BaseActivity implements NewFriendListAdapter.OnItemButtonClick, View.OnClickListener {
    
    private static final int GET_ALL = 11;
    private static final int AGREE_FRIENDS = 12;
    private static final int REQUEST_DELETE = 549;
    public static final int FRIEND_LIST_REQUEST_CODE = 1001;
    private ListView shipListView;
    private NewFriendListAdapter adapter;
    private String friendId;
    private int selectPositionDelete = -1;
    private TextView isData;
    private GetFriendListResponse userRelationshipResponse;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friendlist);
        initView();
        if (!CommonUtils.isNetworkConnected(mContext)) {
            NToast.shortToast(mContext, R.string.check_network);
            return;
        }
        LoadDialog.show(mContext);
        request(GET_ALL);
        adapter = new NewFriendListAdapter(mContext);
        shipListView.setAdapter(adapter);
        shipListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend item = (Friend) adapter.getItem(position);
                if (item.getState() == 1) {//已同意
                    RongIM.getInstance().startPrivateChat(mContext, item.getFriendid(), SealUserInfoManager.getInstance().getDiaplayName(item));
                }
            }
        });
    }
    
    protected void initView() {
        setTitle(R.string.new_friends);
        shipListView = (ListView) findViewById(R.id.shiplistview);
        isData = (TextView) findViewById(R.id.isData);
        Button rightButton = getHeadRightButton();
        rightButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.de_address_new_friend));
        rightButton.setOnClickListener(this);
    }
    
    
    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case GET_ALL:
                return action.getAllUserRelationship();
            case AGREE_FRIENDS:
                return action.agreeFriends(friendId);
            case REQUEST_DELETE:
                return action.deleteFriendsRequestMsg(friendId);
        }
        return super.doInBackground(requestCode, id);
    }
    
    
    @Override
    @SuppressWarnings("unchecked")
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case GET_ALL:
                    userRelationshipResponse = (GetFriendListResponse) result;
                    List<Friend> friends = new ArrayList<>();
                    if (userRelationshipResponse.getData().size() == 0) {
                        isData.setVisibility(View.VISIBLE);
                        LoadDialog.dismiss(mContext);
                        return;
                    } else {
//	                    for (Friend friend : userRelationshipResponse.getData()) {
//		                    if (friend.getState() != 1) {
//			                    friends.add(friend);
//		                    }
//	                    }
                        List<Friend> data = userRelationshipResponse.getData();
                        int n = 0;//未操作个数
                        for (Friend friend : data) {
                            if (friend.getIsvisible() == 1) {
                                friends.add(friend);
                            }
                            if (friend.getState() == 2) {
                                n++;
                            }
                        }
                        
                        if (n > 0) {
                            EventBus.getDefault().post(new UpdateFriendDeal(UpdateFriendDeal.UpdateAction.NUM, n));
                        }
                        if (friends.size() == 0) {
                            isData.setVisibility(View.VISIBLE);
                            LoadDialog.dismiss(mContext);
                            return;
                        }
                    }
                    
                    Collections.sort(friends, new Comparator<Friend>() {
                        
                        @Override
                        public int compare(Friend lhs, Friend rhs) {
                            Date date1 = stringToDate(lhs);
                            Date date2 = stringToDate(rhs);
                            if (date1.before(date2)) {
                                return 1;
                            }
                            return -1;
                        }
                    });
                    
                    adapter.removeAll();
                    adapter.addData(friends);
                    
                    adapter.notifyDataSetChanged();
                    adapter.setOnItemButtonClick(this);
                    LoadDialog.dismiss(mContext);
                    break;
                case AGREE_FRIENDS:
                    BaseResponse afres = (BaseResponse) result;
                    if (afres.getCode() == 200) {
                        Friend bean = userRelationshipResponse.getData().get(index);
                        bean.setState(1);
                        SealUserInfoManager.getInstance().addFriend(bean);
                        // 通知好友列表刷新数据
                        NToast.shortToast(mContext, R.string.agreed_friend);
                        LoadDialog.dismiss(mContext);
                        BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.UPDATE_FRIEND);
                        EventBus.getDefault().post(new UpdateFriendDeal(UpdateFriendDeal.UpdateAction.REDUCE));
                        request(GET_ALL); //刷新 UI 按钮
                    }
                    break;
                case REQUEST_DELETE:
                    LoadDialog.dismiss(this);
                    BaseResponse deleResp = (BaseResponse) result;
                    if (deleResp.getCode() == 200) {
                        if (selectPositionDelete >= 0 && selectPositionDelete < adapter.getCount()) {
                            adapter.remove(selectPositionDelete);
                            adapter.notifyDataSetChanged();
                            selectPositionDelete = -1;
                        }
                    }
                    break;
                default:
                    break;
                
            }
        }
    }
    
    
    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case GET_ALL:
                break;
            
        }
    }
    
    
    @Override
    protected void onDestroy() {
        if (adapter != null) {
            adapter = null;
        }
        super.onDestroy();
    }
    
    private int index;
    
    @Override
    public boolean onButtonClick(final int position, View view, int status) {
        index = position;
        switch (status) {
            case 2: //收到了好友邀请
                if (!CommonUtils.isNetworkConnected(mContext)) {
                    NToast.shortToast(mContext, R.string.check_network);
                    break;
                }
                LoadDialog.show(mContext);
                friendId = userRelationshipResponse.getData().get(position).getFriendid();
                request(AGREE_FRIENDS);
                break;
            case 3: // 发出了好友邀请
                break;
            case 99://删除好友
                DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "确认删除此消息？", new DialogWithYesOrNoUtils.DialogCallBack() {
                    
                    @Override
                    public void executeEvent() {
                        LoadDialog.show(mContext);
                        friendId = ((Friend) adapter.getItem(position)).getFriendid();
                        selectPositionDelete = position;
                        request(REQUEST_DELETE);
                    }
                    
                    @Override
                    public void executeEditEvent(String editText) {
                    
                    }
                    
                    @Override
                    public void updatePassword(String oldPassword, String newPassword) {
                    
                    }
                });
                break;
        }
        return false;
    }
    
    private Date stringToDate(Friend resultEntity) {
        String updatedAt = resultEntity.getCreatetime();
        updatedAt = updatedAt.replace("T", " ").substring(0, 19);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date updateAtDate = null;
        try {
            updateAtDate = simpleDateFormat.parse(updatedAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return updateAtDate;
    }
    
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(NewFriendListActivity.this, SearchFriendActivity.class);
        startActivityForResult(intent, FRIEND_LIST_REQUEST_CODE);
    }
}
