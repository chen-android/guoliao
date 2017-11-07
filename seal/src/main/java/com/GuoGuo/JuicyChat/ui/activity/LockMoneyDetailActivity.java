package com.GuoGuo.JuicyChat.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.server.network.async.AsyncTaskManager;
import com.GuoGuo.JuicyChat.server.network.async.OnDataListener;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.LockMoneyListResponse;
import com.GuoGuo.JuicyChat.server.utils.StringUtils;
import com.GuoGuo.JuicyChat.server.widget.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by cs on 2017/5/31.
 */

public class LockMoneyDetailActivity extends BaseActivity {
	private ListView lv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_money_detail);
		setTitle("冻结明细");
		lv = (ListView) findViewById(R.id.lock_money_detail_lv);
		AsyncTaskManager.getInstance(mContext).request(1, new OnDataListener() {
			@Override
			public Object doInBackground(int requestCode, String parameter) throws HttpException {
				return action.getLockMoneyList();
			}
			
			@Override
			public void onSuccess(int requestCode, Object result) {
				LockMoneyListResponse response = (LockMoneyListResponse) result;
				if (response.getCode() == 200) {
					List<LockMoneyListResponse.ResultEntity> data = response.getData();
					if (data != null && !data.isEmpty()) {
						lv.setAdapter(new MyAdapter(mContext, data));
					}
				}
			}
			
			@Override
			public void onFailure(int requestCode, int state, Object result) {
				
			}
		});
	}
	
	private class MyAdapter extends BaseAdapter {
		
		private List<LockMoneyListResponse.ResultEntity> list;
		private Context mContext;
		
		public MyAdapter(Context context, List<LockMoneyListResponse.ResultEntity> list) {
			this.mContext = context;
			this.list = list;
		}
		
		@Override
		public int getCount() {
			return list.size();
		}
		
		@Override
		public LockMoneyListResponse.ResultEntity getItem(int position) {
			return list.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh = null;
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = View.inflate(mContext, R.layout.item_lock_money_detail, null);
				vh.iv = (SelectableRoundedImageView) convertView.findViewById(R.id.item_lock_head_iv);
				vh.name = (TextView) convertView.findViewById(R.id.item_lock_name_tv);
				vh.time = (TextView) convertView.findViewById(R.id.item_lock_time_tv);
				vh.money = (TextView) convertView.findViewById(R.id.item_lock_money_tv);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			LockMoneyListResponse.ResultEntity item = getItem(position);
			Picasso.with(mContext).load(item.getGroupheadico()).placeholder(R.drawable.rc_default_portrait).into(vh.iv);
			vh.name.setText(item.getGroupname());
			vh.time.setText(StringUtils.sTimeToTimeStr(item.getLocktime()));
			vh.money.setText(StringUtils.getFormatMoney(item.getLockmoney()));
			return convertView;
		}
		
		final class ViewHolder {
			SelectableRoundedImageView iv;
			TextView name;
			TextView time;
			TextView money;
		}
	}
}
