package com.GuoGuo.JuicyChat.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.server.SealAction;
import com.GuoGuo.JuicyChat.server.network.async.AsyncTaskManager;
import com.GuoGuo.JuicyChat.server.network.async.OnDataListener;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.ChatroomListResponse;
import com.GuoGuo.JuicyChat.server.utils.NToast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;


public class DiscoverFragment extends Fragment implements AdapterView.OnItemClickListener, OnDataListener {
	
	private static final int GETDEFCONVERSATION = 333;
	private AsyncTaskManager atm = AsyncTaskManager.getInstance(getActivity());
	private ListView lv;
	private MyAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_chatroom_list, container, false);
		initViews(view);
		atm.request(GETDEFCONVERSATION, this);
		return view;
	}
	
	private void initViews(View view) {
		lv = (ListView) view.findViewById(R.id.chatroom_lv);
		adapter = new MyAdapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		//回调时的线程并不是UI线程，不能在回调中直接操作UI
		RongIMClient.setChatRoomActionListener(new RongIMClient.ChatRoomActionListener() {
			@Override
			public void onJoining(String chatRoomId) {
				
			}
			
			@Override
			public void onJoined(String chatRoomId) {
				
			}
			
			@Override
			public void onQuited(String chatRoomId) {
				
			}
			
			@Override
			public void onError(String chatRoomId, final RongIMClient.ErrorCode code) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (code == RongIMClient.ErrorCode.RC_NET_UNAVAILABLE || code == RongIMClient.ErrorCode.RC_NET_CHANNEL_INVALID) {
							NToast.shortToast(getActivity(), getString(R.string.network_not_available));
						} else {
							NToast.shortToast(getActivity(), getString(R.string.fr_chat_room_join_failure));
						}
					}
				});
			}
		});
	}
	
	
	@Override
	public Object doInBackground(int requestCode, String parameter) throws HttpException {
		return new SealAction(getActivity()).getDefaultConversation();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void onSuccess(int requestCode, Object result) {
		ChatroomListResponse response = (ChatroomListResponse) result;
		if (response.getCode() == 200) {
			List<ChatroomListResponse.ChatroomData> data = response.getData();
			adapter.setData(data);
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onFailure(int requestCode, int state, Object result) {
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.CHATROOM, adapter.getItem(position).getId(), adapter.getItem(position).getName());
	}
	
	private class MyAdapter extends BaseAdapter {
		
		private List<ChatroomListResponse.ChatroomData> list;
		
		public MyAdapter() {
			list = new ArrayList<>();
		}
		
		public void setData(List<ChatroomListResponse.ChatroomData> list) {
			this.list.clear();
			this.list.addAll(list);
		}
		
		@Override
		public int getCount() {
			return list.size();
		}
		
		@Override
		public ChatroomListResponse.ChatroomData getItem(int position) {
			return list.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if (convertView == null) {
				convertView = View.inflate(parent.getContext(), R.layout.item_chatroom, null);
				vh = new ViewHolder();
				vh.head = (ImageView) convertView.findViewById(R.id.item_chatroom_header_iv);
				vh.name = (TextView) convertView.findViewById(R.id.item_chatroom_name_tv);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			ChatroomListResponse.ChatroomData item = getItem(position);
			Picasso.with(parent.getContext()).load(item.getHeadico()).into(vh.head);
			vh.name.setText(item.getName());
			return convertView;
		}
		
		class ViewHolder {
			ImageView head;
			TextView name;
		}
	}
	
	
}
