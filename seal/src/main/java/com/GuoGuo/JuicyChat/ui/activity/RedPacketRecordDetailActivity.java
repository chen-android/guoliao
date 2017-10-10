package com.GuoGuo.JuicyChat.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.GetRedPacketDetailResponse;
import com.GuoGuo.JuicyChat.server.response.GetRedPacketUsersResponse;
import com.GuoGuo.JuicyChat.server.response.RedPacketReceiveData;
import com.GuoGuo.JuicyChat.server.response.RedPacketSendData;
import com.GuoGuo.JuicyChat.server.utils.StringUtils;
import com.GuoGuo.JuicyChat.server.widget.LoadDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RedPacketRecordDetailActivity extends BaseActivity {
	private static final int REQUEST_DETAIL = 1;
	private static final int REQUEST_MEMBERS = 2;
	private ImageView headIv;
	private TextView typeTv, sendNameTv, sendTitleTv, reviewTv, descriptionTv, dateTv, statusTv,
			moneyTv, moneyTitleTv;
	private String redPacketId;
	private GetRedPacketDetailResponse.ResultEntity detailEntity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_red_packet_record_detail);
		setTitle("红包详情");
		initView();
		int type = getIntent().getIntExtra("type", 0);
		if (type == 1) {//收红包
			final RedPacketReceiveData receiveData = getIntent().getParcelableExtra("data");
			if (receiveData.getType() == 1) {//个人红包
				headIv.setImageResource(R.drawable.icon_red_packet_receive);
				typeTv.setText("普通红包");
			} else if (receiveData.getType() == 2) {//群红包
				Picasso.with(this).load(receiveData.getFromuserico()).into(headIv);
				typeTv.setText("群红包");
			}
			sendTitleTv.setText("发包人");
			moneyTv.setText("+" + receiveData.getUnpackmoney() + "果币");
			moneyTitleTv.setText("金额");
			sendNameTv.setText(receiveData.getFromuser());
			descriptionTv.setText(TextUtils.isEmpty(receiveData.getNote()) ? "暂无" : receiveData.getNote());
			dateTv.setText(StringUtils.sTimeToString(receiveData.getCreatetime()));
			statusTv.setText(StringUtils.redPacketState2String(receiveData.getState()));
			reviewTv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					redPacketId = receiveData.getId() + "";
					LoadDialog.show(RedPacketRecordDetailActivity.this);
					request(REQUEST_DETAIL);
				}
			});
		} else if (type == 2) {//发红包
			final RedPacketSendData sendData = getIntent().getParcelableExtra("data");
			if (sendData.getType() == 1) {
				headIv.setImageResource(R.drawable.icon_red_packet_receive);
				typeTv.setText("普通红包");
			} else if (sendData.getType() == 2) {//群红包
				headIv.setImageResource(R.drawable.icon_red_packet_group_receive);
				typeTv.setText("群红包");
			}
			sendTitleTv.setText("收包人");
			moneyTv.setText("-" + sendData.getMoney() + "果币");
			moneyTitleTv.setText("付款金额");
			sendNameTv.setText(sendData.getTomember());
			descriptionTv.setText(TextUtils.isEmpty(sendData.getNote()) ? "暂无" : sendData.getNote());
			dateTv.setText(StringUtils.sTimeToString(sendData.getCreatetime()));
			statusTv.setText(StringUtils.redPacketState2String(sendData.getState()));
			reviewTv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					redPacketId = sendData.getId() + "";
					LoadDialog.show(RedPacketRecordDetailActivity.this);
					request(REQUEST_DETAIL);
				}
			});
		}
	}
	
	private void initView() {
		headIv = (ImageView) findViewById(R.id.redpacket_record_detail_head_iv);
		typeTv = (TextView) findViewById(R.id.redpacket_record_detail_type_tv);
		sendNameTv = (TextView) findViewById(R.id.redpacket_record_detail_send_name_tv);
		sendTitleTv = (TextView) findViewById(R.id.redpacket_record_detail_send_title_tv);
		reviewTv = (TextView) findViewById(R.id.redpacket_record_detail_review_tv);
		descriptionTv = (TextView) findViewById(R.id.redpacket_record_detail_description_tv);
		dateTv = (TextView) findViewById(R.id.redpacket_record_detail_date_tv);
		statusTv = (TextView) findViewById(R.id.redpacket_record_detail_status_tv);
		moneyTv = (TextView) findViewById(R.id.redpacket_record_detail_money_tv);
		moneyTitleTv = (TextView) findViewById(R.id.redpacket_record_detail_money_title_tv);
	}
	
	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		switch (requestCode) {
			case REQUEST_DETAIL:
				return action.getRedPacketDetail(redPacketId);
			case REQUEST_MEMBERS:
				return action.getRedPacketMenber(redPacketId);
		}
		return super.doInBackground(requestCode, id);
	}
	
	@Override
	public void onSuccess(int requestCode, Object result) {
		switch (requestCode) {
			case REQUEST_DETAIL:
				GetRedPacketDetailResponse packetDetailResponse = (GetRedPacketDetailResponse) result;
				if (packetDetailResponse.getCode() == 200) {
					this.detailEntity = packetDetailResponse.getData();
					request(REQUEST_MEMBERS);
				}
				break;
			case REQUEST_MEMBERS:
				LoadDialog.dismiss(this);
				GetRedPacketUsersResponse usersResponse = (GetRedPacketUsersResponse) result;
				if (usersResponse.getCode() == 200) {
					ArrayList<GetRedPacketUsersResponse.ResultEntity> data = usersResponse.getData();
					Intent intent = new Intent(this, RedPacketDetailActivity.class);
					intent.putExtra("id", Integer.valueOf(this.redPacketId));
					intent.putExtra("detail", this.detailEntity);
					intent.putExtra("members", data);
					startActivity(intent);
				}
				break;
		}
	}
}
