package com.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.app.common.AlertDialogWindow;
import com.app.man.R;
import com.app.view.TitleView;

public class SignInfo extends BaseActivity {

	private TitleView titleView; // 页头
	private LinearLayout dateLinear;
	private TextView dateText;

	private ImageView sign_info_photo;
	private ImageView sign_info_bigimg;

	private LinearLayout sign_info_sanwei;
	private TextView sign_info_sanwei_text;
	private TextView sign_info_diqu_text;

	AlertDialogWindow alertDialogWindowSmall;
	AlertDialogWindow alertDialogWindowBig;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sign_info);

		initView();
		initBottomMenu();
	}

	private void initBottomMenu() {
		List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();
		Map<String, Object> btn1 = new HashMap<String, Object>();
		btn1.put("name", getResources().getString(R.string.bottom_menu_photo));
		btn1.put("id", R.id.bottom_menu_photo);
		params.add(btn1);
		Map<String, Object> btn2 = new HashMap<String, Object>();
		btn2.put("name", getResources().getString(R.string.bottom_menu_albums));
		btn2.put("id", R.id.bottom_menu_albums);
		params.add(btn2);
		Map<String, Object> btn3 = new HashMap<String, Object>();
		btn3.put("name", getResources().getString(R.string.bottom_menu_exit));
		btn3.put("id", R.id.bottom_menu_exit);
		params.add(btn3);
		OnClickListener iclSmall = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.bottom_menu_photo) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, 1);
				}
				if (v.getId() == R.id.bottom_menu_albums) {

				}
				if (v.getId() == R.id.bottom_menu_exit) {
					alertDialogWindowSmall.getDialog().hide();
				}
			}
		};
		OnClickListener iclBig = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.bottom_menu_photo) {
					Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent2, 2);
				}
				if (v.getId() == R.id.bottom_menu_albums) {

				}
				if (v.getId() == R.id.bottom_menu_exit) {
					alertDialogWindowSmall.getDialog().hide();
				}
			}
		};
		if (alertDialogWindowSmall == null) {
			alertDialogWindowSmall = new AlertDialogWindow(params, this,
					iclSmall);
		}
		if (alertDialogWindowBig == null) {
			alertDialogWindowBig = new AlertDialogWindow(params, this, iclBig);
		}
	}

	private void initView() {
		titleView = (TitleView) findViewById(R.id.sign_info_title);
		dateText = (TextView) findViewById(R.id.sign_info_date_text);
		dateLinear = (LinearLayout) findViewById(R.id.sign_info_date_linear);
		sign_info_photo = (ImageView) findViewById(R.id.sign_info_photo);
		sign_info_bigimg = (ImageView) findViewById(R.id.sign_info_bigimg);
		sign_info_sanwei = (LinearLayout) findViewById(R.id.sign_info_sanwei);
		sign_info_sanwei_text = (TextView) findViewById(R.id.sign_info_sanwei_text);
		sign_info_diqu_text = (TextView) findViewById(R.id.sign_info_diqu_text);
		sign_info_diqu_text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SignInfo.this, CityChange.class);
				startActivityForResult(intent, 4);

			}
		});

		PhotoOnlickListener listener = new PhotoOnlickListener();
		sign_info_photo.setOnClickListener(listener);
		sign_info_bigimg.setOnClickListener(listener);
		sign_info_sanwei.setOnClickListener(listener);

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
				alertDialogWindowSmall.getDialog().show();
				break;
			case R.id.sign_info_bigimg:
				alertDialogWindowBig.getDialog().show();
				break;
			case R.id.sign_info_sanwei:
				Intent intent3 = new Intent(SignInfo.this, SignSanweiB.class);
				startActivityForResult(intent3, 3);
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
				alertDialogWindowSmall.getDialog().hide();
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
			alertDialogWindowBig.getDialog().hide();
		}
		if (requestCode == 3) {
			if (data != null && data.getExtras() != null) {
				String sanwei1 = data.getExtras().getString("sanwei1");
				String sanwei2 = data.getExtras().getString("sanwei2");
				String sanwei3 = data.getExtras().getString("sanwei3");
				sign_info_sanwei_text.setText(sanwei1 + " " + sanwei2 + " "
						+ sanwei3);
			}
		}
		if (requestCode == 4) {
			if (data != null && data.getExtras() != null) {
				String city = data.getExtras().getString("city");
				sign_info_diqu_text.setText(city);
			}
		}
		// if (requestCode == 2) {相册
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
