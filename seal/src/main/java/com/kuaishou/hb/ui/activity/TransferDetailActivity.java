package com.kuaishou.hb.ui.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ImageUtils;
import com.kuaishou.hb.R;
import com.kuaishou.hb.server.response.TransferRecordData;
import com.kuaishou.hb.utils.SharedPreferencesContext;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class TransferDetailActivity extends BaseActivity {
	private ImageView headIv;
	private TextView toUserNameTv, moneyTv, receiveNameTv, descriptionTv, statusTv, dateTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfer_detail);
		initView();
		setTitle("转账详情");
		TransferRecordData data = getIntent().getParcelableExtra("data");
		if (data != null) {
			String headUrl, toUserName;
			if (data.getFromuserid().equals(SharedPreferencesContext.getInstance().getUserId())) {
				headUrl = data.getTouserheadico();
				toUserName = data.getTouser();
				moneyTv.setText("- " + data.getMoney() + "快豆");
			} else {
				headUrl = data.getFromuserheadico();
				toUserName = data.getFromuser();
				moneyTv.setText("+ " + data.getMoney() + "快豆");
			}
			Picasso.with(this).load(headUrl).into(new Target() {
				@Override
				public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
					headIv.setImageBitmap(ImageUtils.toRound(bitmap));
				}

				@Override
				public void onBitmapFailed(Drawable errorDrawable) {

				}

				@Override
				public void onPrepareLoad(Drawable placeHolderDrawable) {

				}
			});
			toUserNameTv.setText(toUserName);
			receiveNameTv.setText(data.getTouser());
			descriptionTv.setText(TextUtils.isEmpty(data.getNote()) ? "无" : data.getNote());
			statusTv.setText("已收钱");
			dateTv.setText(data.getTime().replace("T", " ").substring(0, 19));
		}
	}

	private void initView() {
		headIv = (ImageView) findViewById(R.id.transfer_detail_touser_head_iv);
		toUserNameTv = (TextView) findViewById(R.id.transfer_detail_touser_name_tv);
		moneyTv = (TextView) findViewById(R.id.transfer_detail_money_tv);
		receiveNameTv = (TextView) findViewById(R.id.transfer_detail_receive_name_tv);
		descriptionTv = (TextView) findViewById(R.id.transfer_detail_description_tv);
		statusTv = (TextView) findViewById(R.id.transfer_detail_status_tv);
		dateTv = (TextView) findViewById(R.id.transfer_detail_date_tv);
	}
}
