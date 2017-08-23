package com.GuoGuo.JuicyChat.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.GuoGuo.JuicyChat.App;
import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.server.response.GetRedPacketDetailResponse;
import com.GuoGuo.JuicyChat.server.response.GetRedPacketUsersResponse;
import com.GuoGuo.JuicyChat.server.utils.NToast;
import com.GuoGuo.JuicyChat.server.utils.StringUtils;
import com.GuoGuo.JuicyChat.utils.SharedPreferencesContext;

import java.util.ArrayList;

import io.rong.imageloader.core.ImageLoader;

/**
 * 领取红包详情
 * Created by cs on 2017/5/13.
 */

public class RedPacketDetailActivity extends BaseActivity implements View.OnClickListener {
	
	private TextView backTv;
	private ImageView headIv;
	private TextView namenoteTv;
	private TextView moneyTv;
	private LinearLayout moneyLl;
	private TextView stateTv;
	private LinearLayout detailLl;
	private int redpacketId;
	
	private GetRedPacketDetailResponse.ResultEntity detailEntity;
	private ArrayList<GetRedPacketUsersResponse.ResultEntity> members;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_red_packet_detail);
		setHeadVisibility(View.GONE);
		initView();
		this.redpacketId = getIntent().getIntExtra("id", 0);
		if (redpacketId == 0) {
			NToast.shortToast(this, "红包信息有误,打开失败");
			finish();
		}
		this.detailEntity = (GetRedPacketDetailResponse.ResultEntity) getIntent().getSerializableExtra("detail");
		this.members = (ArrayList<GetRedPacketUsersResponse.ResultEntity>) getIntent().getSerializableExtra("members");
		initData();
	}
	
	private void initView() {
		backTv = (TextView) findViewById(R.id.red_packet_detail_back_tv);
		headIv = (ImageView) findViewById(R.id.red_packet_detail_head_iv);
		namenoteTv = (TextView) findViewById(R.id.red_packet_detail_name_tv);
		moneyTv = (TextView) findViewById(R.id.red_packet_detail_money_tv);
		moneyLl = (LinearLayout) findViewById(R.id.red_packet_detail_money_ll);
		stateTv = (TextView) findViewById(R.id.red_packet_detail_status_tv);
		detailLl = (LinearLayout) findViewById(R.id.red_packet_detail_user_detail_ll);
		backTv.setOnClickListener(this);
	}
	
	private void initData() {
		ImageLoader.getInstance().displayImage(detailEntity.getFromheadico(), headIv, App.getOptions());
		namenoteTv.setText(detailEntity.getFromnickname() + "的红包\n" + detailEntity.getNote());
		if (detailEntity.getType() == 2) {//群红包
			if (detailEntity.getUnpackcount() < detailEntity.getCount()) {
				stateTv.setText("已领取" + detailEntity.getUnpackcount() + "/" + detailEntity.getCount() + "个,共" + detailEntity.getUnpacksummoney() + "/" + detailEntity.getMoney() + "果币");
			} else {
				stateTv.setText(detailEntity.getCount() + "个红包共" + detailEntity.getMoney() + "果币");
			}
		} else {//个人红包
			if (detailEntity.getState() == 1) {//未领取
				stateTv.setText("红包金额" + detailEntity.getMoney() + "果币，等待对方领取");
			} else {
				stateTv.setText("1个红包共" + detailEntity.getMoney() + "果币");
			}
		}
		
		if (members != null) {
			for (GetRedPacketUsersResponse.ResultEntity resultEntity : members) {
				View v = View.inflate(this, R.layout.item_red_packet_member, null);
				ImageView head = (ImageView) v.findViewById(R.id.item_red_packet_member_head_iv);
				TextView name = (TextView) v.findViewById(R.id.item_red_packet_member_name_tv);
				TextView time = (TextView) v.findViewById(R.id.item_red_packet_member_time_tv);
				TextView money = (TextView) v.findViewById(R.id.item_red_packet_member_money_tv);
				TextView best = (TextView) v.findViewById(R.id.item_red_packet_best_tv);
				if (detailEntity.getBestluckuserid() != 0 && detailEntity.getBestluckuserid() == resultEntity.getUserid() && detailEntity.getType() == 2) {//群红包才显示
					best.setVisibility(View.VISIBLE);
				} else {
					best.setVisibility(View.GONE);
				}
				ImageLoader.getInstance().displayImage(resultEntity.getHeadico(), head, App.getOptions());
				name.setText(resultEntity.getNickname());
				time.setText(StringUtils.sTimeToTimeStr(resultEntity.getUnpacktime()));
				money.setText(resultEntity.getUnpackmoney() + "果币");
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				detailLl.addView(v, lp);
				if (String.valueOf(resultEntity.getUserid()).equals(SharedPreferencesContext.getInstance().getUserId())) {
					moneyTv.setText(StringUtils.getFormatMoney(resultEntity.getUnpackmoney() + ""));
					moneyLl.setVisibility(View.VISIBLE);
				}
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		if (v == backTv) {
			finish();
		}
	}
}
