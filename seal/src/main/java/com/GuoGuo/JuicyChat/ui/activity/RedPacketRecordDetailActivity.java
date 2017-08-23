package com.GuoGuo.JuicyChat.ui.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.R;

public class RedPacketRecordDetailActivity extends BaseActivity {
	private ImageView headIv;
	private TextView typeTv, sendNameTv, reviewTv, descriptionTv, dateTv, statusTv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_red_packet_record_detail);
		initView();
	}
	
	private void initView() {
		headIv = (ImageView) findViewById(R.id.redpacket_record_detail_head_iv);
		typeTv = (TextView) findViewById(R.id.redpacket_record_detail_type_tv);
		sendNameTv = (TextView) findViewById(R.id.redpacket_record_detail_send_name_tv);
		reviewTv = (TextView) findViewById(R.id.redpacket_record_detail_review_tv);
		descriptionTv = (TextView) findViewById(R.id.redpacket_record_detail_description_tv);
		dateTv = (TextView) findViewById(R.id.redpacket_record_detail_date_tv);
		statusTv = (TextView) findViewById(R.id.redpacket_record_detail_status_tv);
	}
}
