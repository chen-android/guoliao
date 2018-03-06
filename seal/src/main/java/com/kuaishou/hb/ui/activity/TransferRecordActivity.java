package com.kuaishou.hb.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.kuaishou.hb.R;
import com.kuaishou.hb.server.SealAction;
import com.kuaishou.hb.server.network.http.HttpException;
import com.kuaishou.hb.server.response.TransferRecordData;
import com.kuaishou.hb.server.response.TransferRecordResponse;
import com.kuaishou.hb.server.response.TransferRecordTypesData;
import com.kuaishou.hb.server.response.TransferRecordTypesRes;
import com.kuaishou.hb.server.utils.StringUtils;
import com.kuaishou.hb.server.widget.LoadDialog;
import com.kuaishou.hb.ui.widget.RecordTypesDialog;
import com.kuaishou.hb.utils.DateUtils;
import com.kuaishou.hb.utils.SharedPreferencesContext;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransferRecordActivity extends BaseActivity {
	private static final int REQUEST_REFRESH = 889;
	private static final int REQUEST_MORE = 548;
	private static final int REQUEST_TYPES = 694;
	private SmartRefreshLayout refreshFl;
	private ListView lv;
	private TextView emptyTv;
	private TextView filterTv;
	private TextView typesTv;
	private SealAction mAction;
	private int index = 1;
	private String month;
	private MyAdapter mMyAdapter;
	private TimePickerDialog dialog;
	private Calendar minCalendar = Calendar.getInstance();//时间筛选，最小时间
	private long currentMillSeconds;
	/**
	 * 当前选择的类型  0 为全部
	 */
	private int selectedType = 0;
	private int selectedPosition = 0;
	private List<TransferRecordTypesData> typeList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfer_record);
		setTitle("转账记录");
		initView();
		initEvent();
		mMyAdapter = new MyAdapter();
		lv.setAdapter(mMyAdapter);
		lv.setEmptyView(emptyTv);
		mAction = new SealAction(this);
		LoadDialog.show(this);
		request(REQUEST_TYPES);
	}

	private void initView() {
		refreshFl = (SmartRefreshLayout) findViewById(R.id.transfer_record_refresh_fl);
		refreshFl.setRefreshHeader(new ClassicsHeader(this));
		refreshFl.setRefreshFooter(new ClassicsFooter(this));
		refreshFl.setEnableLoadmore(true);
		lv = (ListView) findViewById(R.id.transfer_record_lv);
		emptyTv = (TextView) findViewById(R.id.transfer_record_no_data_tv);
		filterTv = (TextView) findViewById(R.id.transfer_filter_tv);
		typesTv = getmHeadRightText();
		typesTv.setVisibility(View.VISIBLE);
		typesTv.setText("全部分类");
		typesTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_arrow_down_white, 0);
		typesTv.setCompoundDrawablePadding(10);
		setHeadRightButtonVisibility(View.VISIBLE);
		minCalendar.set(2017, 5, 1);
		currentMillSeconds = System.currentTimeMillis();
	}

	private void initEvent() {
		refreshFl.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
			@Override
			public void onLoadmore(RefreshLayout refreshLayout) {
				index++;
				request(REQUEST_MORE);
			}

			@Override
			public void onRefresh(RefreshLayout refreshLayout) {
				index = 1;
				request(REQUEST_REFRESH);
			}
		});
		filterTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = new TimePickerDialog.Builder()
						.setCallBack(new OnDateSetListener() {
							@Override
							public void onDateSet(TimePickerDialog timePickerDialog, long l) {
								String selectDate = DateUtils.dateToString(new Date(l), "yyyyMM");
								String nowDate = DateUtils.dateToString(new Date(), "yyyyMM");
								month = selectDate.equals(nowDate) ? "" : selectDate;
								currentMillSeconds = l;
								index = 1;
								request(REQUEST_REFRESH);
							}
						})
						.setCancelStringId("取消")
						.setSureStringId("确认")
						.setTitleStringId("时间筛选")
						.setType(Type.YEAR_MONTH)
						.setCyclic(false)
						.setMinMillseconds(minCalendar.getTimeInMillis())
						.setMaxMillseconds(System.currentTimeMillis())
						.setCurrentMillseconds(currentMillSeconds)
						.setThemeColor(getResources().getColor(R.color.de_title_bg))
						.setWheelItemTextSize(14)
						.build();
				dialog.show(getSupportFragmentManager(), "timeFilter");
			}
		});
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(TransferRecordActivity.this, TransferDetailActivity.class);
				intent.putExtra("data", mMyAdapter.getItem(position));
				startActivity(intent);
			}
		});
		typesTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showTypeDialog();
			}
		});
	}

	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		switch (requestCode) {
			case REQUEST_REFRESH:
				return mAction.getTransferRecord(index, month, selectedType);
			case REQUEST_MORE:
				return mAction.getTransferRecord(index, month, selectedType);
			case REQUEST_TYPES:
				return mAction.getTransferRecordTypes();
			default:
				break;
		}
		return super.doInBackground(requestCode, id);
	}

	private void showTypeDialog() {
		if (typeList != null) {
			new RecordTypesDialog(this, typeList, selectedPosition, new RecordTypesDialog.OnTypeSelectListener() {
				@Override
				public void onSelect(int position) {
					selectedPosition = position;
					selectedType = typeList.get(position).getTypeid();
					typesTv.setText(typeList.get(position).getTypename());
					if (position > 0) {
						emptyTv.setText("暂无" + typeList.get(position).getTypename() + "数据");
					} else {
						emptyTv.setText("暂无数据");
					}
					refreshFl.autoRefresh();
				}
			}).show();
		}
	}

	@Override
	public void onSuccess(int requestCode, Object result) {
		switch (requestCode) {
			case REQUEST_REFRESH:
				refreshFl.finishRefresh(500);
				TransferRecordResponse response = (TransferRecordResponse) result;
				if (response.getCount() == response.getSum()) {
					refreshFl.setEnableLoadmore(false);
				} else {
					refreshFl.setEnableLoadmore(true);
				}
				List<TransferRecordData> list = response.getData();
				if (list != null && !list.isEmpty()) {
					mMyAdapter.setDatas(list);
					mMyAdapter.notifyDataSetChanged();
				} else {
					mMyAdapter.clear();
					mMyAdapter.notifyDataSetChanged();
				}
				if (TextUtils.isEmpty(month)) {
					String m = DateUtils.dateToString(new Date(), "yyyyMM");
					filterTv.setText(m.substring(0, 4) + "年" + m.substring(4, 6) + "月");
				} else {
					filterTv.setText(month.substring(0, 4) + "年" + month.substring(4, 6) + "月");
				}
				break;
			case REQUEST_MORE:
				refreshFl.finishLoadmore(500);
				TransferRecordResponse response1 = (TransferRecordResponse) result;
				if (response1.getCount() == response1.getSum()) {
					refreshFl.setEnableLoadmore(false);
				} else {
					refreshFl.setEnableLoadmore(true);
				}
				List<TransferRecordData> list1 = response1.getData();
				if (list1 != null && !list1.isEmpty()) {
					mMyAdapter.addDatas(list1);
					mMyAdapter.notifyDataSetChanged();
				}
				break;
			case REQUEST_TYPES:
				LoadDialog.dismiss(this);
				TransferRecordTypesRes typesRes = (TransferRecordTypesRes) result;
				if (typesRes.getCode() == 200) {
					typeList = typesRes.getData();
					typeList.add(0, new TransferRecordTypesData(0, "全部分类"));
					request(REQUEST_REFRESH);
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void onFailure(int requestCode, int state, Object result) {
		switch (requestCode) {
			case REQUEST_REFRESH:
				refreshFl.finishRefresh(500, false);
				break;
			case REQUEST_MORE:
				refreshFl.finishLoadmore(500, false);
				break;
			default:
				break;
		}
	}

	private class MyAdapter extends BaseAdapter {

		private List<TransferRecordData> mRecordDatas;

		public MyAdapter() {
			this.mRecordDatas = new ArrayList<>();
		}

		public void setDatas(List<TransferRecordData> list) {
			if (list != null && !list.isEmpty()) {
				this.mRecordDatas.clear();
				this.mRecordDatas.addAll(list);
			}
		}

		public void addDatas(List<TransferRecordData> list) {
			if (list != null && !list.isEmpty()) {
				this.mRecordDatas.addAll(list);
			}
		}

		public void clear() {
			this.mRecordDatas.clear();
		}

		@Override
		public int getCount() {
			return this.mRecordDatas.size();
		}

		@Override
		public TransferRecordData getItem(int position) {
			return this.mRecordDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if (convertView == null) {
				convertView = View.inflate(parent.getContext(), R.layout.item_transfer_record, null);
				vh = new ViewHolder();
				vh.title = (TextView) convertView.findViewById(R.id.item_transfer_record_title_tv);
				vh.date = (TextView) convertView.findViewById(R.id.item_transfer_record_date_tv);
				vh.header = (ImageView) convertView.findViewById(R.id.item_transfer_record_header_iv);
				vh.money = (TextView) convertView.findViewById(R.id.item_transfer_record_money_tv);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			TransferRecordData item = getItem(position);
			String headUrl;
			if (item.getFromuserid().equals(SharedPreferencesContext.getInstance().getUserId())) {
				vh.title.setText(item.getOption() + " - " + item.getTouser());
				vh.money.setText("- " + StringUtils.getFormatMoney(item.getMoney()));
				vh.money.setTextColor(Color.BLACK);
				headUrl = item.getTouserheadico();
			} else {
				vh.title.setText(item.getOption() + " - " + item.getFromuser());
				vh.money.setText("+ " + StringUtils.getFormatMoney(item.getMoney()));
				vh.money.setTextColor(getResources().getColor(R.color.color_ffc000));
				headUrl = item.getFromuserheadico();
			}
			Picasso.with(parent.getContext()).load(headUrl).into(vh.header);
			vh.date.setText(StringUtils.sTimeToString(item.getTime()));
			return convertView;
		}

		class ViewHolder {
			TextView title;
			TextView date;
			TextView money;
			ImageView header;
		}
	}
}
