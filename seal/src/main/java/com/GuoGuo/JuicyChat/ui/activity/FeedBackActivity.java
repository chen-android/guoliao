package com.GuoGuo.JuicyChat.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.GuoGuo.JuicyChat.GGConst;
import com.GuoGuo.JuicyChat.R;
import com.GuoGuo.JuicyChat.server.network.http.HttpException;
import com.GuoGuo.JuicyChat.server.response.BaseResponse;
import com.GuoGuo.JuicyChat.server.response.QiNiuTokenResponse;
import com.GuoGuo.JuicyChat.server.utils.NToast;
import com.GuoGuo.JuicyChat.server.utils.photo.PhotoUtils;
import com.GuoGuo.JuicyChat.server.widget.DialogWithYesOrNoUtils;
import com.GuoGuo.JuicyChat.server.widget.LoadDialog;
import com.GuoGuo.JuicyChat.ui.widget.DemoGridView;
import com.GuoGuo.JuicyChat.utils.CommonUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by cs on 2017/5/21.
 */

public class FeedBackActivity extends BaseActivity {
	private static final int GET_QI_NIU_TOKEN = 23;
	private static final int SUBMIT_DATA = 124;
	private EditText contentEt;
	private DemoGridView dgv;
	private Button submitBt;
	private PhotoUtils mPhotoUtils;
	private UploadManager uploadManager;
	private int imgWh;
	private List<Uri> imgUrlList = new ArrayList<>();//本地图片地址集
	private List<String> imgQiNiuUrlList = new ArrayList<>();//七牛图片地址集
	private GridAdapter mAdapter;
	private int uploadIndex;
	private final int maxCount = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		initView();
		setTitle("意见反馈");
		LinearLayout linearLayout = new LinearLayout(this);
		mPhotoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
			@Override
			public void onPhotoResult(Uri uri) {
			}
			
			@Override
			public void onPhotoCancel() {
				
			}
		});
		
		submitBt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (imgUrlList.size() > 0) {
					uploadIndex = 0;
					imgQiNiuUrlList.clear();
					LoadDialog.show(mContext);
					request(GET_QI_NIU_TOKEN);
				} else {
					request(SUBMIT_DATA);
				}
			}
		});
		imgWh = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
		
	}
	
	private void initView() {
		contentEt = (EditText) findViewById(R.id.feedback_et);
		submitBt = (Button) findViewById(R.id.feedback_submit_bt);
		dgv = (DemoGridView) findViewById(R.id.gridview);
		mAdapter = new GridAdapter(mContext);
		dgv.setAdapter(mAdapter);
		dgv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				if (position == imgUrlList.size()) {//如果点击到添加按钮
					return false;
				}
				DialogWithYesOrNoUtils.getInstance().showDialog(FeedBackActivity.this, "确认删除此图片？", new DialogWithYesOrNoUtils.DialogCallBack() {
					@Override
					public void executeEvent() {
						imgUrlList.remove(position);
						mAdapter.notifyDataSetChanged();
					}
					
					@Override
					public void executeEditEvent(String editText) {
						
					}
					
					@Override
					public void updatePassword(String oldPassword, String newPassword) {
						
					}
				});
				return false;
			}
		});
		dgv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == imgUrlList.size()) {
					mPhotoUtils.selectPicture(FeedBackActivity.this);
				}
			}
		});
//		iv1.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mPhotoUtils.selectPicture(FeedBackActivity.this);
//			}
//		});
		
	}
	
	@Override
	public Object doInBackground(int requestCode, String id) throws HttpException {
		switch (requestCode) {
			case GET_QI_NIU_TOKEN:
				return action.getQiNiuToken();
			case SUBMIT_DATA:
				return action.submitFeedBack(contentEt.getText().toString(), imgQiNiuUrlList);
		}
		return super.doInBackground(requestCode, id);
	}
	
	@Override
	public void onSuccess(int requestCode, Object result) {
		if (result != null) {
			switch (requestCode) {
				case GET_QI_NIU_TOKEN:
					QiNiuTokenResponse response = (QiNiuTokenResponse) result;
					if (response.getCode() == 200) {
						uploadImage("", response.getData().getQiniutoken(), CommonUtils.getRealFilePath(mContext, imgUrlList.get(uploadIndex)));
					}
					break;
				case SUBMIT_DATA:
					LoadDialog.dismiss(mContext);
					BaseResponse re = (BaseResponse) result;
					if (re.getCode() == 200) {
						NToast.shortToast(mContext, "感谢您的反馈！");
						finish();
					}
					break;
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case PhotoUtils.INTENT_SELECT:
				Uri uri = data.getData();
				if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
					String path = CommonUtils.getRealFilePath(mContext, uri);
					imgUrlList.add(uri);
					mAdapter.notifyDataSetChanged();
				}
				break;
		}
	}
	
	public void uploadImage(final String domain, String imageToken, String imagePath) {
		if (TextUtils.isEmpty(domain) && TextUtils.isEmpty(imageToken) && TextUtils.isEmpty(imagePath.toString())) {
			throw new RuntimeException("upload parameter is null!");
		}
		File imageFile = new File(imagePath);
		
		if (this.uploadManager == null) {
			this.uploadManager = new UploadManager();
		}
		this.uploadManager.put(imageFile, new Date().getTime() + imageFile.getName(), imageToken, new UpCompletionHandler() {
			
			@Override
			public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
				if (responseInfo.isOK()) {
					try {
						String key = (String) jsonObject.get("key");
						String netUrl = GGConst.QINIU_URL + key;
						imgQiNiuUrlList.add(netUrl);
						if (uploadIndex < imgUrlList.size() - 1) {
							uploadIndex++;
							request(GET_QI_NIU_TOKEN);
							return;
						}
						request(SUBMIT_DATA);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					NToast.shortToast(mContext, "图片上传失败");
					LoadDialog.dismiss(mContext);
				}
			}
		}, null);
	}
	
	private class GridAdapter extends BaseAdapter {
		
		Context context;
		
		
		public GridAdapter(Context context) {
			this.context = context;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView iv = new ImageView(parent.getContext());
			iv.setLayoutParams(new ViewGroup.LayoutParams(imgWh, imgWh));
			// 最后一个item，添加按钮
			if (position == getCount() - 1 && imgUrlList.size() < 3) {
				iv.setImageResource(R.drawable.jy_drltsz_btn_addperson);
			} else {
				Picasso.with(mContext).load(getItem(position)).into(iv);
			}
			return iv;
		}
		
		@Override
		public int getCount() {
			if (imgUrlList.size() < 3) {
				return imgUrlList.size() + 1;
			}
			return 3;
		}
		
		@Override
		public Uri getItem(int position) {
			return imgUrlList.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
	}
}
