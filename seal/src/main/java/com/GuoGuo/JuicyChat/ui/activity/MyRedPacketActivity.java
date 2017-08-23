package com.GuoGuo.JuicyChat.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.App;
import com.GuoGuo.JuicyChat.GGConst;
import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.GetRedPacketStatisticResponse;
import com.GuoGuo.JuicyChat.server.response.RedPacketReceiveData;
import com.GuoGuo.JuicyChat.server.response.RedPacketReceiveResponse;
import com.GuoGuo.JuicyChat.server.response.RedPacketSendData;
import com.GuoGuo.JuicyChat.server.response.RedPacketSendResponse;
import com.GuoGuo.JuicyChat.server.utils.ColorPhrase;
import com.GuoGuo.JuicyChat.server.utils.NToast;
import com.GuoGuo.JuicyChat.server.utils.StringUtils;
import com.GuoGuo.JuicyChat.server.widget.LoadDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import io.rong.imageloader.core.ImageLoader;

/**
 * Created by cs on 2017/5/19.
 */

public class MyRedPacketActivity extends BaseActivity implements View.OnClickListener {
	private static final int REQUEST_REDPACK_DETAIL = 728;
	private static final int REQUEST_REDPACK_SEND_LIST_REFRESH = 761;
	private static final int REQUEST_REDPACK_SEND_LIST_MORE = 826;
	private static final int REQUEST_REDPACK_RECEIVE_LIST_REFRESH = 158;
	private static final int REQUEST_REDPACK_RECEIVE_LIST_MORE = 343;
	
	private TextView tabReceiveTv;
	private TextView tabSendTv;
	private RelativeLayout tabReceiveRl;
	private RelativeLayout tabSendRl;
	private LinearLayout receiveHeadLl;
	private LinearLayout sendHeadLl;
	private SmartRefreshLayout receiveSfl;
	private SmartRefreshLayout sendSfl;
	private ListView receiveLv;
	private ListView sendLv;
	private MyReceiveAdapter receiveAdapter;
	private MySendAdapter sendAdapter;
	private boolean isReceive = true;
	private GetRedPacketStatisticResponse.ResultEntity data;
	private String headIcoUrl;
	private String username;
	private int receiveIndex = 1;
	private int sendIndex = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_red_packet);
		setTitle("我的红包");
		mHeadLayout.setBackgroundResource(R.color.red);
		initView();
		initEvent();
		LoadDialog.show(this);
		request(REQUEST_REDPACK_DETAIL);
		headIcoUrl = getSharedPreferences("config", MODE_PRIVATE).getString(GGConst.GUOGUO_LOGING_PORTRAIT, "");
		username = getSharedPreferences("config", MODE_PRIVATE).getString(GGConst.GUOGUO_LOGIN_NAME, "");
	}
	
	private void initView() {
		tabReceiveTv = (TextView) findViewById(R.id.my_red_packet_tab_receive_tv);
		tabSendTv = (TextView) findViewById(R.id.my_red_packet_tab_send_tv);
		
		tabReceiveRl = (RelativeLayout) findViewById(R.id.my_red_packet_tab_receive_rl);
		tabSendRl = (RelativeLayout) findViewById(R.id.my_red_packet_tab_send_rl);
		
		receiveHeadLl = (LinearLayout) View.inflate(this, R.layout.item_header_redpack_receive, null);
		ImageLoader.getInstance().displayImage(headIcoUrl, (ImageView) receiveHeadLl.findViewById(R.id.my_red_packet_head_iv), App.getOptions());
		
		
		sendHeadLl = (LinearLayout) View.inflate(this, R.layout.item_header_redpack_send, null);
		ImageLoader.getInstance().displayImage(headIcoUrl, (ImageView) sendHeadLl.findViewById(R.id.my_red_packet_head_iv), App.getOptions());
		
		tabReceiveTv.setOnClickListener(this);
		tabSendTv.setOnClickListener(this);
		
		receiveSfl = (SmartRefreshLayout) findViewById(R.id.my_red_packet_receive_sfl);
		receiveSfl.setRefreshHeader(new ClassicsHeader(this));
		receiveSfl.setRefreshFooter(new ClassicsFooter(this));
		receiveSfl.setEnableLoadmore(true);
		
		sendSfl = (SmartRefreshLayout) findViewById(R.id.my_red_packet_send_sfl);
		sendSfl.setRefreshHeader(new ClassicsHeader(this));
		sendSfl.setRefreshFooter(new ClassicsFooter(this));
		sendSfl.setEnableLoadmore(true);
		
		receiveLv = (ListView) findViewById(R.id.my_red_packet_receive_lv);
		receiveAdapter = new MyReceiveAdapter();
		receiveLv.setAdapter(receiveAdapter);
		receiveLv.addHeaderView(receiveHeadLl);
		
		sendLv = (ListView) findViewById(R.id.my_red_packet_send_lv);
		sendAdapter = new MySendAdapter();
		sendLv.setAdapter(sendAdapter);
		sendLv.addHeaderView(sendHeadLl);
	}
	
	private void initEvent() {
		receiveSfl.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
			@Override
			public void onLoadmore(RefreshLayout refreshLayout) {
				request(REQUEST_REDPACK_RECEIVE_LIST_MORE);
			}
			
			@Override
			public void onRefresh(RefreshLayout refreshLayout) {
				request(REQUEST_REDPACK_RECEIVE_LIST_REFRESH);
			}
		});
		
		sendSfl.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
			@Override
			public void onLoadmore(RefreshLayout refreshLayout) {
				request(REQUEST_REDPACK_SEND_LIST_MORE);
			}
			
			@Override
			public void onRefresh(RefreshLayout refreshLayout) {
				request(REQUEST_REDPACK_SEND_LIST_REFRESH);
			}
		});
		
		receiveLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(MyRedPacketActivity.this, RedPacketRecordDetailActivity.class);
				intent.putExtra("type", 1);
				intent.putExtra("data", receiveAdapter.getItem(position - 1));
				startActivity(intent);
			}
		});
		
		sendLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(MyRedPacketActivity.this, RedPacketRecordDetailActivity.class);
				intent.putExtra("type", 2);
				intent.putExtra("data", sendAdapter.getItem(position - 1));
				startActivity(intent);
			}
		});
	}
	
	private void initData() {
		((TextView) receiveHeadLl.findViewById(R.id.my_red_packet_state_tv)).setText(username + "  共收到");
		((TextView) receiveHeadLl.findViewById(R.id.my_red_packet_money_tv)).setText(StringUtils.getFormatMoney(data.getMoneyreceive()));
		((TextView) receiveHeadLl.findViewById(R.id.my_red_packet_receive_money_tv)).setText(data.getReceivecount());
		((TextView) receiveHeadLl.findViewById(R.id.my_red_packet_best_times_tv)).setText(data.getBestluckcount());
		
		((TextView) sendHeadLl.findViewById(R.id.my_red_packet_state_tv)).setText(username + "  共发出");
		((TextView) sendHeadLl.findViewById(R.id.my_red_packet_money_tv)).setText(StringUtils.getFormatMoney(data.getMoneysend()));
		float v = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30, getResources().getDisplayMetrics());
		((TextView) sendHeadLl.findViewById(R.id.my_red_packet_send_tv)).setText(ColorPhrase.from("发出红包{" + data.getSendcount() + "}个").innerColor(Color.RED).innerSize((int) v).format());
	}
	
	@Override
	public void onClick(View v) {
		if (v == tabReceiveTv) {
			isReceive = true;
			changeTab();
		} else if (v == tabSendTv) {
			isReceive = false;
			changeTab();
		}
	}
	
	private void changeTab() {
		if (isReceive) {
			//tab
			tabReceiveRl.setBackgroundResource(R.color.red);
			tabSendRl.setBackgroundResource(R.color.gray);
			tabReceiveTv.setTextColor(Color.RED);
			tabSendTv.setTextColor(Color.GRAY);
			receiveSfl.setVisibility(View.VISIBLE);
			sendSfl.setVisibility(View.GONE);
			LoadDialog.show(this);
			request(REQUEST_REDPACK_RECEIVE_LIST_REFRESH);
		} else {
			tabReceiveRl.setBackgroundResource(R.color.gray);
			tabSendRl.setBackgroundResource(R.color.red);
			tabSendTv.setTextColor(Color.RED);
			tabReceiveTv.setTextColor(Color.GRAY);
			receiveSfl.setVisibility(View.GONE);
			sendSfl.setVisibility(View.VISIBLE);
			LoadDialog.show(this);
			request(REQUEST_REDPACK_SEND_LIST_REFRESH);
		}
	}
	
	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		switch (requestCode) {
			case REQUEST_REDPACK_DETAIL:
				return action.getMyRedPacketInfo();
			case REQUEST_REDPACK_RECEIVE_LIST_REFRESH:
				receiveIndex = 1;
				return action.getRedPacketReceiveList(receiveIndex);
			case REQUEST_REDPACK_RECEIVE_LIST_MORE:
				receiveIndex++;
				return action.getRedPacketReceiveList(receiveIndex);
			case REQUEST_REDPACK_SEND_LIST_REFRESH:
				sendIndex = 1;
				return action.getRedPacketSendList(sendIndex);
			case REQUEST_REDPACK_SEND_LIST_MORE:
				sendIndex++;
				return action.getRedPacketSendList(sendIndex);
		}
		return super.doInBackground(requestCode, id);
	}
	
	@Override
	public void onSuccess(int requestCode, Object result) {
		switch (requestCode) {
			case REQUEST_REDPACK_DETAIL:
				GetRedPacketStatisticResponse re = (GetRedPacketStatisticResponse) result;
				if (re.getCode() == 200) {
					data = re.getData();
					initData();
					changeTab();
				} else {
					NToast.shortToast(this, "服务器开小差");
				}
				break;
			case REQUEST_REDPACK_RECEIVE_LIST_REFRESH:
				LoadDialog.dismiss(this);
				RedPacketReceiveResponse redPacketReceiveResponse = (RedPacketReceiveResponse) result;
				if (redPacketReceiveResponse.getCount() == redPacketReceiveResponse.getSum()) {
					receiveSfl.setEnableLoadmore(false);
				} else {
					receiveSfl.setEnableLoadmore(true);
				}
				List<RedPacketReceiveData> datas = redPacketReceiveResponse.getData();
				if (datas != null && !datas.isEmpty()) {
					receiveAdapter.setDatas(datas);
					receiveAdapter.notifyDataSetChanged();
				}
				break;
			case REQUEST_REDPACK_RECEIVE_LIST_MORE:
				RedPacketReceiveResponse response = (RedPacketReceiveResponse) result;
				if (response.getCount() == response.getSum()) {
					receiveSfl.setEnableLoadmore(false);
				} else {
					receiveSfl.setEnableLoadmore(true);
				}
				List<RedPacketReceiveData> datas1 = response.getData();
				if (datas1 != null && !datas1.isEmpty()) {
					receiveAdapter.addDatas(datas1);
					receiveAdapter.notifyDataSetChanged();
				}
				break;
			case REQUEST_REDPACK_SEND_LIST_REFRESH:
				LoadDialog.dismiss(this);
				RedPacketSendResponse redPacketSendResponse = (RedPacketSendResponse) result;
				if (redPacketSendResponse.getCount() == redPacketSendResponse.getSum()) {
					sendSfl.setEnableLoadmore(false);
				} else {
					sendSfl.setEnableLoadmore(true);
				}
				List<RedPacketSendData> datas2 = redPacketSendResponse.getData();
				if (datas2 != null && !datas2.isEmpty()) {
					sendAdapter.setDatas(datas2);
					sendAdapter.notifyDataSetChanged();
				}
				break;
			case REQUEST_REDPACK_SEND_LIST_MORE:
				RedPacketSendResponse sendResponse = (RedPacketSendResponse) result;
				if (sendResponse.getCount() == sendResponse.getSum()) {
					sendSfl.setEnableLoadmore(false);
				} else {
					sendSfl.setEnableLoadmore(true);
				}
				List<RedPacketSendData> datas3 = sendResponse.getData();
				if (datas3 != null && !datas3.isEmpty()) {
					sendAdapter.addDatas(datas3);
					sendAdapter.notifyDataSetChanged();
				}
				break;
		}
	}
	
	private class MyReceiveAdapter extends BaseAdapter {
		
		private List<RedPacketReceiveData> datas;
		
		public MyReceiveAdapter() {
			datas = new ArrayList<>();
		}
		
		public void setDatas(List<RedPacketReceiveData> datas) {
			this.clear();
			this.addDatas(datas);
		}
		
		public void addDatas(List<RedPacketReceiveData> datas) {
			this.datas.addAll(datas);
		}
		
		public void clear() {
			this.datas.clear();
		}
		
		@Override
		public int getCount() {
			return this.datas.size();
		}
		
		@Override
		public RedPacketReceiveData getItem(int position) {
			return this.datas.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if (convertView == null) {
				convertView = View.inflate(parent.getContext(), R.layout.item_red_packet_receive, null);
				vh = new ViewHolder();
				vh.name = (TextView) convertView.findViewById(R.id.item_red_packet_receive_name_tv);
				vh.group = (TextView) convertView.findViewById(R.id.item_red_packet_receive_groupe_tv);
				vh.money = (TextView) convertView.findViewById(R.id.item_red_packet_receive_money_tv);
				vh.date = (TextView) convertView.findViewById(R.id.item_red_packet_receive_date_tv);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			RedPacketReceiveData item = getItem(position);
			vh.name.setText(item.getFromuser());
			vh.money.setText(StringUtils.getFormatMoney(item.getUnpackmoney() + ""));
			vh.date.setText(StringUtils.sTimeToString(item.getCreatetime()));
			vh.group.setVisibility(item.getType() == 2 ? View.VISIBLE : View.GONE);
			return convertView;
		}
		
		class ViewHolder {
			TextView name;
			TextView group;
			TextView money;
			TextView date;
		}
	}
	
	private class MySendAdapter extends BaseAdapter {
		
		private List<RedPacketSendData> datas;
		
		public MySendAdapter() {
			datas = new ArrayList<>();
		}
		
		public void setDatas(List<RedPacketSendData> datas) {
			this.clear();
			this.addDatas(datas);
		}
		
		public void addDatas(List<RedPacketSendData> datas) {
			this.datas.addAll(datas);
		}
		
		public void clear() {
			this.datas.clear();
		}
		
		@Override
		public int getCount() {
			return this.datas.size();
		}
		
		@Override
		public RedPacketSendData getItem(int position) {
			return this.datas.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MySendAdapter.ViewHolder vh;
			if (convertView == null) {
				convertView = View.inflate(parent.getContext(), R.layout.item_red_packet_send, null);
				vh = new MySendAdapter.ViewHolder();
				vh.name = (TextView) convertView.findViewById(R.id.item_red_packet_send_name_tv);
				vh.status = (TextView) convertView.findViewById(R.id.item_red_packet_send_status_tv);
				vh.money = (TextView) convertView.findViewById(R.id.item_red_packet_send_money_tv);
				vh.date = (TextView) convertView.findViewById(R.id.item_red_packet_send_date_tv);
				convertView.setTag(vh);
			} else {
				vh = (MySendAdapter.ViewHolder) convertView.getTag();
			}
			RedPacketSendData item = getItem(position);
			vh.name.setText(item.getFromuser());
			vh.money.setText(StringUtils.getFormatMoney(item.getMoney() + ""));
			vh.date.setText(StringUtils.sTimeToString(item.getCreatetime()));
			String status;
			if (item.getCount() == item.getUnpackcount()) {
				status = "已领完" + item.getUnpackcount() + "/" + item.getCount() + "个";
				vh.status.setTextColor(getResources().getColor(R.color.rc_text_color_secondary));
			} else {
				status = "已领取" + item.getUnpackcount() + "/" + item.getCount() + "个";
				vh.status.setTextColor(getResources().getColor(R.color.red));
			}
			vh.status.setText(status);
			return convertView;
		}
		
		class ViewHolder {
			TextView name;
			TextView status;
			TextView money;
			TextView date;
		}
	}
}
