package com.GuoGuo.JuicyChat.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.utils.CommonUtils;
import com.squareup.picasso.Picasso;

public class ImageReviewActivity extends Activity {
	private ImageView iv;
	private Uri imageBmUri;
	private RelativeLayout bgRl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_review);
		iv = (ImageView) findViewById(R.id.image_review_iv);
		bgRl = (RelativeLayout) findViewById(R.id.image_review_rl);
		imageBmUri = getIntent().getParcelableExtra("bitmapUrl");
		if (imageBmUri == null) {
			Toast.makeText(this, "头像加载有误", Toast.LENGTH_SHORT).show();
			finish();
		}
		Picasso.with(this).load(imageBmUri).into(iv);
		int screenWidth = CommonUtils.getScreenWidth(this);
		float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, getResources().getDisplayMetrics());
		float marginLeft = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
		
		iv.setPivotX(0f);
		iv.setPivotY(0f);
		AnimatorSet set = new AnimatorSet();
		float v = screenWidth / width;
		set.playTogether(
				ObjectAnimator.ofFloat(iv, "scaleX", 1f, v),
				ObjectAnimator.ofFloat(iv, "scaleY", 1f, v),
				ObjectAnimator.ofFloat(iv, "translationX", 0, -marginLeft),
				ObjectAnimator.ofFloat(iv, "translationY", 0, 100),
				ObjectAnimator.ofFloat(bgRl, "alpha", 0f, 1f)
		
		);
		set.setDuration(300).start();
		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	protected void onPause() {
		overridePendingTransition(0, 0);
		super.onPause();
	}
}
