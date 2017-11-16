package com.GuoGuo.JuicyChat.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.GuoGuo.JuicyChat.App;
import com.GuoGuo.JuicyChat.GGConst;
import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.SealUserInfoManager;
import com.GuoGuo.JuicyChat.db.Friend;
import com.GuoGuo.JuicyChat.db.GroupMember;
import com.GuoGuo.JuicyChat.server.broadcast.BroadcastManager;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.pinyin.CharacterParser;
import com.GuoGuo.JuicyChat.server.pinyin.PinyinComparator;
import com.GuoGuo.JuicyChat.server.pinyin.SideBar;
import com.GuoGuo.JuicyChat.server.response.AddGroupMemberResponse;
import com.GuoGuo.JuicyChat.server.response.BaseResponse;
import com.GuoGuo.JuicyChat.server.response.DeleteGroupMemberResponse;
import com.GuoGuo.JuicyChat.server.response.GetGroupMemberResponse;
import com.GuoGuo.JuicyChat.server.utils.NLog;
import com.GuoGuo.JuicyChat.server.utils.NToast;
import com.GuoGuo.JuicyChat.server.widget.DialogWithYesOrNoUtils;
import com.GuoGuo.JuicyChat.server.widget.LoadDialog;
import com.GuoGuo.JuicyChat.server.widget.SelectableRoundedImageView;
import com.blankj.utilcode.util.ToastUtils;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class SelectFriendsActivity extends BaseActivity implements View.OnClickListener {
    
    private static final int ADD_GROUP_MEMBER = 21;
    private static final int DELETE_GROUP_MEMBER = 23;
    private static final int SET_GAG_GROUP_MEMEBER = 63;
    private static final int GET_GAG_GROUP_MEMEBER = 851;
    private static final int REMOVE_GAG_GROUP_MEMBER = 579;
    private static final int TRANSFER_LEADER = 322;
    private static final int UNLOCK_MEMBER_LIST = 246;
    private static final int UNLOCK_MEMBER_SUBMIT = 447;
    public static final String DISCUSSION_UPDATE = "DISCUSSION_UPDATE";
    private static final String ALLPERSONFRIENDID = "-10";
    /**
     * 好友列表的 ListView
     */
    private ListView mListView;
    /**
     * 发起讨论组的 adapter
     */
    private StartDiscussionAdapter adapter;
    /**
     * 中部展示的字母提示
     */
    public TextView dialog;
    
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser mCharacterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    private TextView mNoFriends;
    private List<Friend> data_list = new ArrayList<>();
    private List<Friend> sourceDataList = new ArrayList<>();
    //	private LinearLayout mSelectedFriendsLinearLayout;
    private boolean isCrateGroup;
    private boolean isConversationActivityStartDiscussion;
    private boolean isConversationActivityStartPrivate;
    private List<GroupMember> addGroupMemberList;
    private List<GroupMember> deleteGroupMemberList;
    private List<GroupMember> gagGroupMemberList;
    private List<GroupMember> transferLeaderList;
    private List<String> hasGagGroupMemberIdList;//已经被禁的成员id列表
    private String groupId;
    private String conversationStartId;
    private String conversationStartType = "null";
    private ArrayList<String> discListMember;
    private ArrayList<UserInfo> addDisList, deleDisList;
    private boolean isStartPrivateChat;
    //	private List<Friend> mSelectedFriend;
    private boolean isAddGroupMember;
    private boolean isDeleteGroupMember;
    private boolean isUnlockMoneyMember;
    private boolean isGagMember;//是否是禁言
    private boolean isTransferLeader;//群转让
    
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_disc);
        Button rightButton = getHeadRightButton();
        rightButton.setVisibility(View.GONE);
//		mSelectedFriend = new ArrayList<>();
//		mSelectedFriendsLinearLayout = (LinearLayout) findViewById(R.id.ll_selected_friends);
        isCrateGroup = getIntent().getBooleanExtra("createGroup", false);
        isConversationActivityStartDiscussion = getIntent().getBooleanExtra("CONVERSATION_DISCUSSION", false);
        isConversationActivityStartPrivate = getIntent().getBooleanExtra("CONVERSATION_PRIVATE", false);
        groupId = getIntent().getStringExtra("GroupId");
        isAddGroupMember = getIntent().getBooleanExtra("isAddGroupMember", false);
        isDeleteGroupMember = getIntent().getBooleanExtra("isDeleteGroupMember", false);
        isUnlockMoneyMember = getIntent().getBooleanExtra("isUnlockMoneyMember", false);
        isGagMember = getIntent().getBooleanExtra("isGagMember", false);
        isTransferLeader = getIntent().getBooleanExtra("isTransferLeader", false);
        
        mHeadRightText.setVisibility(View.VISIBLE);
        mHeadRightText.setText("确定");
        mHeadRightText.setOnClickListener(this);
        
        if (isAddGroupMember || isDeleteGroupMember || isTransferLeader) {
            initGroupMemberList();
        }
        if (isGagMember) {
            request(GET_GAG_GROUP_MEMEBER);
        }
        if (isUnlockMoneyMember) {
            LoadDialog.show(this);
            initUnlockMoneyMemberList();
        }
        addDisList = (ArrayList<UserInfo>) getIntent().getSerializableExtra("AddDiscuMember");
        deleDisList = (ArrayList<UserInfo>) getIntent().getSerializableExtra("DeleteDiscuMember");
        
        setTitle();
        initView();
        
        /**
         * 根据进行的操作初始化数据,添加删除群成员和获取好友信息是异步操作,所以做了很多额外的处理
         * 数据添加后还需要过滤已经是群成员,讨论组成员的用户
         * 最后设置adapter显示
         * 后两个操作全都根据异步操作推后
         */
        initData();
    }
    
    private void initGroupMemberList() {
        SealUserInfoManager.getInstance().getGroupMembers(groupId, new SealUserInfoManager.ResultCallback<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                if (isAddGroupMember) {
                    addGroupMemberList = groupMembers;
                    fillSourceDataListWithFriendsInfo();
                } else if (isDeleteGroupMember) {
                    deleteGroupMemberList = groupMembers;
                    fillSourceDataListForDeleteGroupMember();
                } else if (isGagMember) {
                    gagGroupMemberList = groupMembers;
                    fillSourceDataListForGagGroupMember();
                } else if (isTransferLeader) {
                    transferLeaderList = groupMembers;
                    fillSourceDataListForTransferLeader();
                }
            }
            
            @Override
            public void onError(String errString) {
            
            }
        });
    }
    
    private void initUnlockMoneyMemberList() {
        request(UNLOCK_MEMBER_LIST);
    }
    
    private void setTitle() {
        if (isConversationActivityStartPrivate) {
            conversationStartType = "PRIVATE";
            conversationStartId = getIntent().getStringExtra("DEMO_FRIEND_TARGETID");
            setTitle("选择讨论组成员");
        } else if (isConversationActivityStartDiscussion) {
            conversationStartType = "DISCUSSION";
            conversationStartId = getIntent().getStringExtra("DEMO_FRIEND_TARGETID");
            discListMember = getIntent().getStringArrayListExtra("DISCUSSIONMEMBER");
            setTitle("选择讨论组成员");
        } else if (isDeleteGroupMember) {
            setTitle(getString(R.string.remove_group_member));
        } else if (isAddGroupMember) {
            setTitle(getString(R.string.add_group_member));
        } else if (isCrateGroup) {
            setTitle(getString(R.string.select_group_member));
        } else if (addDisList != null) {
            setTitle("增加讨论组成员");
        } else if (deleDisList != null) {
            setTitle("移除讨论组成员");
        } else if (isGagMember) {
            setTitle("禁言管理");
        } else if (isTransferLeader) {
            setTitle("群组转让");
        } else {
            setTitle(getString(R.string.select_contact));
            if (!getSharedPreferences("config", MODE_PRIVATE).getBoolean("isDebug", false)) {
                isStartPrivateChat = true;
            }
        }
    }
    
    private void initView() {
        //实例化汉字转拼音类
        mCharacterParser = CharacterParser.getInstance();
        pinyinComparator = PinyinComparator.getInstance();
        mListView = (ListView) findViewById(R.id.dis_friendlistview);
        mNoFriends = (TextView) findViewById(R.id.dis_show_no_friend);
        SideBar mSidBar = (SideBar) findViewById(R.id.dis_sidrbar);
        dialog = (TextView) findViewById(R.id.dis_dialog);
        mSidBar.setTextView(dialog);
        //设置右侧触摸监听
        mSidBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s);
                if (position != -1) {
                    mListView.setSelection(position);
                }
            }
        });
        
        adapter = new StartDiscussionAdapter(mContext, sourceDataList);
        mListView.setAdapter(adapter);
    }
    
    private void initData() {
        if (deleDisList != null && deleDisList.size() > 0) {
            for (int i = 0; i < deleDisList.size(); i++) {
                if (deleDisList.get(i).getUserId().contains(getSharedPreferences("config", MODE_PRIVATE).getString(GGConst.GUOGUO_LOGIN_ID, ""))) {
                    continue;
                }
            }
            /**
             * 以下3步是标准流程
             * 1.填充数据sourceDataList
             * 2.过滤数据,邀请新成员时需要过滤掉已经是成员的用户,但做删除操作时不需要这一步
             * 3.设置adapter显示
             */
            fillSourceDataList();
            filterSourceDataList();
            updateAdapter();
        } else if (!isDeleteGroupMember && !isAddGroupMember && !isUnlockMoneyMember && !isGagMember && !isTransferLeader) {
            fillSourceDataListWithFriendsInfo();
        }
    }
    
    private void fillSourceDataList() {
        if (data_list != null && data_list.size() > 0) {
            sourceDataList = filledData(data_list); //过滤数据为有字母的字段  现在有字母 别的数据没有
        } else {
            mNoFriends.setVisibility(View.VISIBLE);
        }
        
        //还原除了带字母字段的其他数据
//        for (int i = 0; i < data_list.size(); i++) {
//            sourceDataList.get(i).setName(data_list.get(i).getName());
//            sourceDataList.get(i).setUserId(data_list.get(i).getUserId());
//            sourceDataList.get(i).setPortraitUri(data_list.get(i).getPortraitUri());
//            sourceDataList.get(i).setDisplayName(data_list.get(i).getDisplayName());
//        }
        // 根据a-z进行排序源数据
        Collections.sort(sourceDataList, pinyinComparator);
    }
    
    //讨论组群组邀请新成员时需要过滤掉已经是成员的用户
    private void filterSourceDataList() {
//        if (addDisList != null && addDisList.size() > 0) {
//            for (UserInfo u : addDisList) {
//                for (int i = 0; i < sourceDataList.size(); i++) {
//                    if (sourceDataList.get(i).getUserId().contains(u.getUserId())) {
//                        sourceDataList.remove(sourceDataList.get(i));
//                    }
//                }
//            }
//        } else if (addGroupMemberList != null && addGroupMemberList.size() > 0) {
//            for (GroupMember addMember : addGroupMemberList) {
//                for (int i = 0; i < sourceDataList.size(); i++) {
//                    if (sourceDataList.get(i).getUserId().contains(addMember.getUserId())) {
//                        sourceDataList.remove(sourceDataList.get(i));
//                    }
//                }
//            }
//        } else if (conversationStartType.equals("DISCUSSION")) {
//            if (discListMember != null && discListMember.size() > 1) {
//                for (String s : discListMember) {
//                    for (int i = 0; i < sourceDataList.size(); i++) {
//                        if (sourceDataList.get(i).getUserId().contains(s)) {
//                            sourceDataList.remove(sourceDataList.get(i));
//                        }
//                    }
//                }
//            }
//        } else if (conversationStartType.equals("PRIVATE")) {
//            for (int i = 0; i < sourceDataList.size(); i++) {
//                if (sourceDataList.get(i).getUserId().contains(conversationStartId)) {
//                    sourceDataList.remove(sourceDataList.get(i));
//                }
//            }
//        }
    }
    
    private void updateAdapter() {
        adapter.setData(sourceDataList);
        adapter.notifyDataSetChanged();
    }
    
    private void fillSourceDataListWithFriendsInfo() {
        SealUserInfoManager.getInstance().getFriends(new SealUserInfoManager.ResultCallback<List<Friend>>() {
            @Override
            public void onSuccess(List<Friend> friendList) {
                if (mListView != null) {
                    if (friendList != null && friendList.size() > 0) {
                        data_list.clear();
                        data_list.addAll(friendList);
                        if (isAddGroupMember) {
                            for (GroupMember groupMember : addGroupMemberList) {
                                for (int i = 0; i < data_list.size(); i++) {
                                    if (groupMember.getUserid().equals(data_list.get(i).getFriendid()) || data_list.get(i).getFriendid().equals("10001")) {//剔除已经是群成员和果聊客服
                                        data_list.remove(i);
                                    }
                                }
                            }
                        }
                        fillSourceDataList();
                        filterSourceDataList();
                        updateAdapter();
                    }
                }
            }
            
            @Override
            public void onError(String errString) {
            
            }
        });
    }
    
    //删除群成员
    private void fillSourceDataListForDeleteGroupMember() {
        
        if (deleteGroupMemberList != null && deleteGroupMemberList.size() > 0) {
            for (GroupMember deleteMember : deleteGroupMemberList) {
                if (deleteMember.getUserid().contains(getSharedPreferences("config", MODE_PRIVATE).getString(GGConst.GUOGUO_LOGIN_ID, ""))) {
                    continue;
                }
                data_list.add(new Friend(deleteMember.getUserid(), SealUserInfoManager.getInstance().getGroupMenberDisplayName(deleteMember), deleteMember.getUserhead()));
            }
            fillSourceDataList();
            updateAdapter();
        }
    }
    
    //禁言群成员
    private void fillSourceDataListForGagGroupMember() {
        
        if (gagGroupMemberList != null && gagGroupMemberList.size() > 0) {
            for (GroupMember gagMember : gagGroupMemberList) {
                if (gagMember.getUserid().contains(getSharedPreferences("config", MODE_PRIVATE).getString(GGConst.GUOGUO_LOGIN_ID, ""))) {
                    continue;
                }
                data_list.add(new Friend(gagMember.getUserid(), SealUserInfoManager.getInstance().getGroupMenberDisplayName(gagMember), gagMember.getUserhead()));
            }
            fillSourceDataList();
            //添加全部成员项
            
            Friend friend = new Friend(ALLPERSONFRIENDID, "全部成员", "");
            friend.setLetter("全部成员");
            sourceDataList.add(0, friend);
            
            updateAdapter();
        }
    }
    
    /**
     * 群转让
     */
    private void fillSourceDataListForTransferLeader() {
        if (transferLeaderList != null && transferLeaderList.size() > 0) {
            for (GroupMember deleteMember : transferLeaderList) {
                if (deleteMember.getUserid().contains(getSharedPreferences("config", MODE_PRIVATE).getString(GGConst.GUOGUO_LOGIN_ID, ""))) {
                    continue;
                }
                data_list.add(new Friend(deleteMember.getUserid(), SealUserInfoManager.getInstance().getGroupMenberDisplayName(deleteMember), deleteMember.getUserhead()));
            }
            fillSourceDataList();
            updateAdapter();
        }
    }
    
    //用于存储CheckBox选中状态
    public Map<Integer, Boolean> mCBFlag;
    
    public List<Friend> adapterList;
    
    
    class StartDiscussionAdapter extends BaseAdapter {
        private int lastPosition = -1;
        
        private Context context;
        private ArrayList<CheckBox> checkBoxList = new ArrayList<>();
        
        private Map<String, Integer> letterIndexMap = new HashMap<>();
        
        public StartDiscussionAdapter(Context context, List<Friend> list) {
            this.context = context;
            adapterList = list;
            mCBFlag = new HashMap<>();
            setSectionsFromList(list);
            init();
        }
        
        public void setData(List<Friend> friends) {
            adapterList = friends;
            setSectionsFromList(friends);
            init();
        }
        
        private void setSectionsFromList(List<Friend> friends) {
            letterIndexMap.clear();
            for (int i = 0; i < friends.size(); i++) {
                Friend f = friends.get(i);
                if (!letterIndexMap.containsKey(f.getLetter())) {
                    letterIndexMap.put(f.getLetter(), i);
                }
            }
        }
        
        void init() {
            for (int i = 0; i < adapterList.size(); i++) {
                mCBFlag.put(i, false);
            }
            if (hasGagGroupMemberIdList != null && !hasGagGroupMemberIdList.isEmpty()) {
                for (int i = 0, l = adapterList.size(); i < l; i++) {
                    if (hasGagGroupMemberIdList.contains(adapterList.get(i).getFriendid())) {
                        mCBFlag.put(i, true);
                    }
                }
                if (hasGagGroupMemberIdList.size() == adapterList.size() - 1) {//全部被禁言
                    mCBFlag.put(0, true);
                }
                updateSelectedSizeView(mCBFlag);
            }
        }
        
        private void checkAll(boolean isCheck) {
            for (int i = 0; i < adapterList.size(); i++) {
                mCBFlag.put(i, isCheck);
            }
        }
        
        @Override
        public int getCount() {
            return adapterList.size();
        }
        
        @Override
        public Object getItem(int position) {
            return adapterList.get(position);
        }
        
        @Override
        public long getItemId(int position) {
            return position;
        }
        
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            
            final ViewHolder viewHolder;
            final Friend friend = adapterList.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_start_discussion, parent, false);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.dis_friendname);
                viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.dis_catalog);
                viewHolder.mImageView = (SelectableRoundedImageView) convertView.findViewById(R.id.dis_frienduri);
                viewHolder.isSelect = (CheckBox) convertView.findViewById(R.id.dis_select);
                
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            
            if (position == letterIndexMap.get(friend.getLetter())) {
                viewHolder.tvLetter.setVisibility(View.VISIBLE);
                viewHolder.tvLetter.setText(friend.getLetter());
            } else {
                viewHolder.tvLetter.setVisibility(View.GONE);
            }
            
            if (isStartPrivateChat) {
                viewHolder.isSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        if (cb != null) {
                            if (cb.isChecked()) {
                                for (CheckBox c : checkBoxList) {
                                    c.setChecked(false);
                                }
                                checkBoxList.clear();
                                checkBoxList.add(cb);
                            } else {
                                checkBoxList.clear();
                            }
                        }
                    }
                });
                viewHolder.isSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mCBFlag.put(position, viewHolder.isSelect.isChecked());
                    }
                });
            } else {
                viewHolder.isSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isGagMember) {
                            if (position == 0) {
                                checkAll(viewHolder.isSelect.isChecked());
                                adapter.notifyDataSetChanged();
                            } else {
                                mCBFlag.put(position, viewHolder.isSelect.isChecked());
                            }
                            updateSelectedSizeView(mCBFlag);
                            
                        } else if (isTransferLeader) {
                            if (lastPosition != position) {
                                mCBFlag.put(lastPosition, false);
                                lastPosition = position;
                            }
                            mCBFlag.put(position, viewHolder.isSelect.isChecked());
                            notifyDataSetChanged();
                        } else {
                            mCBFlag.put(position, viewHolder.isSelect.isChecked());
                            updateSelectedSizeView(mCBFlag);
//							if (mSelectedFriend.contains(friend)) {
//								int index = mSelectedFriend.indexOf(friend);
//								if (index > -1) {
////									mSelectedFriendsLinearLayout.removeViewAt(index);
//								}
//								mSelectedFriend.remove(friend);
//							} else {
//								mSelectedFriend.add(friend);
//								LinearLayout view = (LinearLayout) View.inflate(SelectFriendsActivity.this, R.layout.item_selected_friends, null);
//								SelectableRoundedImageView asyncImageView = (SelectableRoundedImageView) view.findViewById(R.id.iv_selected_friends);
//								String portraitUri = SealUserInfoManager.getInstance().getPortraitUri(new UserInfo(friend.getFriendid(), friend.getNickname(), Uri.parse(friend.getHeadico())));
//								ImageLoader.getInstance().displayImage(portraitUri, asyncImageView);
//								view.removeView(asyncImageView);
////								mSelectedFriendsLinearLayout.addView(asyncImageView);
//							}
                        }
                    }
                });
            }
            viewHolder.isSelect.setChecked(mCBFlag.get(position));
            
            if (!TextUtils.isEmpty(friend.getNickname())) {
                viewHolder.tvTitle.setText(friend.getNickname());
            } else {
                viewHolder.tvTitle.setText(friend.getUsername());
            }

//            String portraitUri = SealUserInfoManager.getInstance().getPortraitUri(adapterList.get(position));
            if (ALLPERSONFRIENDID.equals(friend.getFriendid())) {
                Picasso.with(context).load(R.drawable.icon_gag_select_all).into(viewHolder.mImageView);
            } else {
                String portraitUri = friend.getHeadico();
                ImageLoader.getInstance().displayImage(portraitUri, viewHolder.mImageView, App.getOptions());
            }
            return convertView;
        }
        
        private void updateSelectedSizeView(Map<Integer, Boolean> mCBFlag) {
            if (!isStartPrivateChat && mCBFlag != null) {
                int size = 0;
                for (int i = 0; i < mCBFlag.size(); i++) {
                    if (mCBFlag.get(i)) {
                        size++;
                    }
                }
                if (size == 0) {
                    mHeadRightText.setText("确定");
//					mSelectedFriendsLinearLayout.setVisibility(View.GONE);
                } else {
                    mHeadRightText.setText("确定(" + size + ")");
//					List<Friend> selectedList = new ArrayList<>();
//					for (int i = 0; i < sourceDataList.size(); i++) {
//						if (mCBFlag.get(i)) {
//							selectedList.add(sourceDataList.get(i));
//						}
//					}
//					mSelectedFriendsLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        }
        
        public Object[] getSections() {
            return new Object[0];
        }
        
        
        public int getPositionForSection(String ch) {
            if (letterIndexMap.containsKey(ch)) {
                return letterIndexMap.get(ch);
            }
            return -1;
        }
        
        
        public int getSectionForPosition(int position) {
            return 0;
//            return adapterList.get(position).getLetters().charAt(0);
        }
        
        
        final class ViewHolder {
            /**
             * 首字母
             */
            TextView tvLetter;
            /**
             * 昵称
             */
            TextView tvTitle;
            /**
             * 头像
             */
            SelectableRoundedImageView mImageView;
            /**
             * userid
             */
//            TextView tvUserId;
            /**
             * 是否被选中的checkbox
             */
            CheckBox isSelect;
        }
        
    }
    
    
    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case ADD_GROUP_MEMBER:
                return action.addGroupMember(groupId, startDisList);
            case DELETE_GROUP_MEMBER:
                return action.deleGroupMember(groupId, startDisList);
            case SET_GAG_GROUP_MEMEBER:
                return action.setGagGroupMember(groupId, startDisList);
            case GET_GAG_GROUP_MEMEBER:
                return action.getGagGroupMember(groupId);
            case REMOVE_GAG_GROUP_MEMBER:
                return action.removeGagGroupMember(groupId, startDisList);
            case UNLOCK_MEMBER_LIST:
                return action.getLockedGroupMembers(groupId);
            case UNLOCK_MEMBER_SUBMIT:
                return action.unLockedGroupMembers(startDisList, groupId);
            case TRANSFER_LEADER:
                return action.transferLeader(groupId, startDisList.get(0));
            default:
                break;
        }
        return super.doInBackground(requestCode, id);
    }
    
    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case ADD_GROUP_MEMBER:
                    AddGroupMemberResponse res = (AddGroupMemberResponse) result;
                    if (res.getCode() == 200) {
                        Intent data = new Intent();
                        data.putExtra("newAddMember", (Serializable) createGroupList);
                        setResult(101, data);
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, getString(R.string.add_successful));
                        finish();
                    } else {
                        Toast.makeText(SelectFriendsActivity.this, "添加失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case DELETE_GROUP_MEMBER:
                    DeleteGroupMemberResponse response = (DeleteGroupMemberResponse) result;
                    if (response.getCode() == 200) {
                        Intent intent = new Intent();
                        intent.putExtra("deleteMember", (Serializable) createGroupList);
                        setResult(102, intent);
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, getString(R.string.remove_successful));
                        finish();
                    } else if (response.getCode() == 400) {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, "创建者不能将自己移除");
                    }
                    break;
                case SET_GAG_GROUP_MEMEBER:
                    BaseResponse baseResponse = (BaseResponse) result;
                    if (baseResponse.getCode() == 200) {
                        NToast.shortToast(this, "操作完成");
                        finish();
                    } else {
                        NToast.shortToast(this, baseResponse.getMessage());
                    }
                    break;
                case GET_GAG_GROUP_MEMEBER:
                    LoadDialog.dismiss(this);
                    GetGroupMemberResponse gagGroupMemberResponse = (GetGroupMemberResponse) result;
                    if (gagGroupMemberResponse.getCode() == 200) {
                        hasGagGroupMemberIdList = new ArrayList<>();
                        for (GroupMember groupMember : gagGroupMemberResponse.getData()) {
                            hasGagGroupMemberIdList.add(groupMember.getUserid());
                        }
                    } else {
                        NToast.shortToast(this, "获取禁言成员失败");
                    }
                    initGroupMemberList();
                    break;
                
                case UNLOCK_MEMBER_LIST:
                    LoadDialog.dismiss(this);
                    GetGroupMemberResponse groupMemberResponse = (GetGroupMemberResponse) result;
                    if (groupMemberResponse.getCode() == 200) {
                        List<GroupMember> list = groupMemberResponse.getData();
                        for (GroupMember deleteMember : list) {
                            data_list.add(new Friend(deleteMember.getUserid(),
                                    SealUserInfoManager.getInstance().getGroupMenberDisplayName(deleteMember),
                                    deleteMember.getUserhead()));
                        }
                        fillSourceDataList();
                        updateAdapter();
                    }
                    break;
                case UNLOCK_MEMBER_SUBMIT:
                    LoadDialog.dismiss(mContext);
                    BaseResponse resp = (BaseResponse) result;
                    if (resp.getCode() == 200) {
                        NToast.shortToast(mContext, "解除成功");
                        finish();
                    }
                    break;
                case TRANSFER_LEADER:
                    LoadDialog.dismiss(mContext);
                    BaseResponse resp1 = (BaseResponse) result;
                    if (resp1.getCode() == 200) {
                        NToast.shortToast(mContext, "转让成功");
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        ToastUtils.showShort(resp1.getMessage());
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
            case ADD_GROUP_MEMBER:
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, "添加群组成员请求失败");
                break;
            case DELETE_GROUP_MEMBER:
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, "移除群组成员请求失败");
                break;
        }
    }
    
    private List<String> startDisList;
    private List<Friend> createGroupList;
    
    
    /**
     * 为ListView填充数据
     */
    private List<Friend> filledData(List<Friend> list) {
        List<Friend> mFriendList = new ArrayList<>();
        
        for (Friend friend : list) {
            //汉字转换成拼音
            String pinyin = null;
            String diaplayName = SealUserInfoManager.getInstance().getDiaplayName(friend);
            if (!TextUtils.isEmpty(diaplayName)) {
                pinyin = PinyinComparator.getLetter(diaplayName);
                friend.setLetter(pinyin);
            }
            mFriendList.add(friend);
        }
        return mFriendList;
        
    }
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListView = null;
        adapter = null;
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_right:
                if (mCBFlag != null && sourceDataList != null && sourceDataList.size() > 0) {
                    startDisList = new ArrayList<>();
                    List<String> disNameList = new ArrayList<>();
                    createGroupList = new ArrayList<>();
                    for (int i = 0; i < sourceDataList.size(); i++) {
                        if (mCBFlag.get(i)) {
                            String friendid = sourceDataList.get(i).getFriendid();
                            if (ALLPERSONFRIENDID.equals(friendid)) {
                                continue;
                            }
                            startDisList.add(friendid);
                            disNameList.add(sourceDataList.get(i).getNickname());
                            createGroupList.add(sourceDataList.get(i));
                        }
                    }
                    
                    if (isConversationActivityStartDiscussion) {
                        if (RongIM.getInstance() != null) {
                            RongIM.getInstance().addMemberToDiscussion(conversationStartId, startDisList, new RongIMClient.OperationCallback() {
                                @Override
                                public void onSuccess() {
                                    NToast.shortToast(SelectFriendsActivity.this, getString(R.string.add_successful));
                                    BroadcastManager.getInstance(mContext).sendBroadcast(DISCUSSION_UPDATE);
                                    finish();
                                }
                                
                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                
                                }
                            });
                        }
                    } else if (isConversationActivityStartPrivate) {
                        if (RongIM.getInstance() != null) { // 没有被调用 二人讨论组时候
                            RongIM.getInstance().addMemberToDiscussion(conversationStartId, startDisList, new RongIMClient.OperationCallback() {
                                @Override
                                public void onSuccess() {
                                    NToast.shortToast(SelectFriendsActivity.this, getString(R.string.add_successful));
                                    finish();
                                }
                                
                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                
                                }
                            });
                        }
                    } else if (deleteGroupMemberList != null && startDisList != null && sourceDataList.size() > 0) {
                        mHeadRightText.setClickable(true);
                        DialogWithYesOrNoUtils.getInstance().showDialog(mContext, getString(R.string.remove_group_members), new DialogWithYesOrNoUtils.DialogCallBack() {
                            
                            @Override
                            public void executeEvent() {
                                LoadDialog.show(mContext);
                                request(DELETE_GROUP_MEMBER);
                            }
                            
                            @Override
                            public void executeEditEvent(String editText) {
                            
                            }
                            
                            @Override
                            public void updatePassword(String oldPassword, String newPassword) {
                            
                            }
                        });
                    } else if (gagGroupMemberList != null && startDisList != null && sourceDataList.size() > 0) {
                        mHeadRightText.setClickable(true);
                        DialogWithYesOrNoUtils.getInstance().showDialog(mContext, getString(R.string.gag_group_members), new DialogWithYesOrNoUtils.DialogCallBack() {
                            
                            @Override
                            public void executeEvent() {
                                LoadDialog.show(mContext);
                                request(SET_GAG_GROUP_MEMEBER);
                            }
                            
                            @Override
                            public void executeEditEvent(String editText) {
                            
                            }
                            
                            @Override
                            public void updatePassword(String oldPassword, String newPassword) {
                            
                            }
                        });
                    } else if (deleDisList != null && startDisList != null && startDisList.size() > 0) {
                        Intent intent = new Intent();
                        intent.putExtra("deleteDiscuMember", (Serializable) startDisList);
                        setResult(RESULT_OK, intent);
                        finish();
                        
                    } else if (addGroupMemberList != null && startDisList != null && startDisList.size() > 0) {
                        LoadDialog.show(mContext);
                        request(ADD_GROUP_MEMBER);
                        
                    } else if (addDisList != null && startDisList != null && startDisList.size() > 0) {
                        Intent intent = new Intent();
                        intent.putExtra("addDiscuMember", (Serializable) startDisList);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (isCrateGroup) {
                        if (createGroupList.size() > 0) {
                            mHeadRightText.setClickable(true);
                            Intent intent = new Intent(SelectFriendsActivity.this, CreateGroupActivity.class);
                            intent.putExtra("GroupMember", (Serializable) createGroupList);
                            startActivity(intent);
                            finish();
                        } else {
                            NToast.shortToast(mContext, "请至少邀请一位好友创建群组");
                            mHeadRightText.setClickable(true);
                        }
                    } else if (isUnlockMoneyMember) {
                        if (startDisList != null && !startDisList.isEmpty()) {
                            LoadDialog.show(mContext);
                            request(UNLOCK_MEMBER_SUBMIT);
                        }
                    } else if (isTransferLeader) {
                        if (startDisList != null && !startDisList.isEmpty()) {
                            request(TRANSFER_LEADER);
                        }
                    } else {
                        
                        if (startDisList != null && startDisList.size() == 1) {
                            RongIM.getInstance().startPrivateChat(mContext, startDisList.get(0),
                                    SealUserInfoManager.getInstance().getFriendByID(startDisList.get(0)).getNickname());
                        } else if (startDisList.size() > 1) {
                            
                            String disName;
                            if (disNameList.size() < 2) {
                                disName = disNameList.get(0) + "和我的讨论组";
                            } else {
                                StringBuilder sb = new StringBuilder();
                                for (String s : disNameList) {
                                    sb.append(s);
                                    sb.append(",");
                                }
                                String str = sb.toString();
                                disName = str.substring(0, str.length() - 1);
                                disName = disName + "和我的讨论组";
                            }
                            RongIM.getInstance().createDiscussion(disName, startDisList, new RongIMClient.CreateDiscussionCallback() {
                                @Override
                                public void onSuccess(String s) {
                                    NLog.e("disc", "onSuccess" + s);
                                    RongIM.getInstance().startDiscussionChat(SelectFriendsActivity.this, s, "");
                                }
                                
                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    NLog.e("disc", errorCode.getValue());
                                }
                            });
                        } else {
                            mHeadRightText.setClickable(true);
                            NToast.shortToast(mContext, getString(R.string.least_one_friend));
                        }
                    }
                } else {
                    Toast.makeText(SelectFriendsActivity.this, "无数据", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
