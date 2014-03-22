package com.app.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.man.R;
import com.app.view.TitleView;

public class SignInfo extends BaseActivity {

	private TitleView titleView; // 页头
	private LinearLayout dateLinear;
	private TextView dateText;

	private ImageView sign_info_photo;
	private ImageView sign_info_bigimg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sign_info);

		initView();
	}

	private void initView() {
		titleView = (TitleView) findViewById(R.id.sign_info_title);
		dateText = (TextView) findViewById(R.id.sign_info_date_text);
		dateLinear = (LinearLayout) findViewById(R.id.sign_info_date_linear);
		sign_info_photo = (ImageView) findViewById(R.id.sign_info_photo);
		sign_info_bigimg = (ImageView) findViewById(R.id.sign_info_bigimg);

		PhotoOnlickListener listener = new PhotoOnlickListener();
		sign_info_photo.setOnClickListener(listener);
		sign_info_bigimg.setOnClickListener(listener);

		dateLinear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String date = (String) dateText.getText();
				String[] dateArr = date.split("-");

				// Calendar c = Calendar.getInstance();
				// 直接创建一个DatePickerDialog对话框实例，并将它显示出来
				new DatePickerDialog(SignInfo.this,
				// 绑定监听器
						new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								dateText.setText(year + "-" + (monthOfYear + 1)
										+ "-" + dayOfMonth);
							}
						}
						// 设置初始日期
						, Integer.parseInt(dateArr[0]), Integer
								.parseInt(dateArr[1]) - 1, Integer
								.parseInt(dateArr[2])).show();
			}
		});
	}

	class PhotoOnlickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.sign_info_photo:
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 1);
				break;
			case R.id.sign_info_bigimg:
				Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent2, 2);
				break;
			default:
				break;
			}

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (data != null && data.getExtras() != null) {
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				if (bitmap != null) {
					// sign_info_photo.setDrawingCacheEnabled(true);
					small_img = bitmap;
					sign_info_photo.setImageBitmap(bitmap);
				}
			}
		}
		if (requestCode == 2) {
			if (data != null && data.getExtras() != null) {
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				if (bitmap != null) {
					// sign_info_bigimg.setDrawingCacheEnabled(true);
					big_img = bitmap;
					sign_info_bigimg.setImageBitmap(bitmap);
				}
			}
		}
		// if (requestCode == 2) {
		// // 获取图片并显示
		// // Picasso.with(this).load(getPathBUri(data.getData()))
		// // .into(imageView);
		// imageView.setImageURI(data.getData());
		// }

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_info, menu);
		return true;
	}

	public static Bitmap small_img = null;
	public static Bitmap big_img = null;

}
