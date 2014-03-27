package com.app.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.activity.WriteArticle.SubmitHttpHandler;
import com.app.common.AlertDialogWindow;
import com.app.common.Base64Utils;
import com.app.common.BaseUtils;
import com.app.common.BubbleUtil;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.common.JSONObjectSerializalble;
import com.app.common.MyDateUtils;
import com.app.man.R;
import com.app.view.TitleView;

public class SignInfo extends BaseActivity {

	final String WIN_MENU_TITLE = "设置报名图片";
	final String[] WIN_MENU_DATAS = new String[] { "图库相册", "拍照" };

	Context context = this;

	// 控件
	private TitleView titleView; // 页头
	private ImageView sign_info_photo; // 头像
	private ImageView sign_info_bigimg; // 大图
	private LinearLayout sign_info_sanwei;
	private TextView sign_info_sanwei_text; // 三围
	private TextView sign_info_diqu_text; // 地区
	private LinearLayout dateLinear;
	private TextView dateText; // 日期

	AlertDialogWindow alertDialogWindowSmall;
	AlertDialogWindow alertDialogWindowBig;

	private Bitmap small_img = null;
	private Bitmap big_img = null;

	private SubmitHttpHandler submitHttpHandle = new SubmitHttpHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sign_info);

		initView();
		initBottomMenu();
	}

	private void initBottomMenu() {
		OnClickListener iclSmall = new OnClickListener() {

			@Override
			public void onClick(View v) {
				String txt = ((TextView) v).getText().toString();

				if (WIN_MENU_DATAS[0].equals(txt)) {
					Intent picture = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(picture, 5);
				} else if (WIN_MENU_DATAS[1].equals(txt)) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, 1);
				}
			}
		};
		OnClickListener iclBig = new OnClickListener() {

			@Override
			public void onClick(View v) {
				String txt = ((TextView) v).getText().toString();

				if (WIN_MENU_DATAS[0].equals(txt)) {
					Intent picture = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(picture, 6);
				} else if (WIN_MENU_DATAS[1].equals(txt)) {
					Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent2, 2);
				}
			}
		};
		if (alertDialogWindowSmall == null) {
			alertDialogWindowSmall = new AlertDialogWindow(WIN_MENU_TITLE,
					WIN_MENU_DATAS, this, iclSmall);
		}
		if (alertDialogWindowBig == null) {
			alertDialogWindowBig = new AlertDialogWindow(WIN_MENU_TITLE,
					WIN_MENU_DATAS, this, iclBig);
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

		titleView.setmRightBtClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				submitReq();
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
		if (requestCode == 5) {
			if (data != null && data.getData() != null) {
				if (data.getData() != null) {
					sign_info_photo.setImageURI(data.getData());
					System.gc();
				}
				alertDialogWindowSmall.getDialog().hide();
			}
		}
		if (requestCode == 6) {
			if (data != null && data.getData() != null) {
				if (data.getData() != null) {
					sign_info_bigimg.setImageURI(data.getData());
					System.gc();
				}
				alertDialogWindowBig.getDialog().hide();
			}
		}
		// if (requestCode == 2) {相册
		// // 获取图片并显示
		// // Picasso.with(this).load(getPathBUri(data.getData()))
		// // .into(imageView);
		// imageView.setImageURI(data.getData());
		// }

	}

	/**
	 * 报名提交
	 */
	private void submitReq() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				String imgs = null;
				if (big_img != null) {
					byte[] bytes = getByteByBitmap(big_img);
					imgs = Base64Utils.encode(bytes);
					big_img = null;
				} else {
					// Toast.makeText(parentView.getContext(),
					// "请拍一张大图", Toast.LENGTH_SHORT)
					// .show();
					return;
				}

				String imgs2 = null;
				if (small_img != null) {
					byte[] bytes2 = getByteByBitmap(small_img);
					imgs2 = Base64Utils.encode(bytes2);
					small_img = null;
				} else {
					// Toast.makeText(parentView.getContext(),
					// "请拍一张小图", Toast.LENGTH_SHORT)
					// .show();
					return;
				}

				Message msg = submitHttpHandle.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT + "ApplyGirl.shtml");
				JSONObjectSerializalble jsonObject = new JSONObjectSerializalble();
				try {
					// BaseUtils.CUR_USER_MAP.get("userId");
					// 8
					jsonObject.put("userId",
							BaseUtils.CUR_USER_MAP.get("userId"));
					jsonObject.put("image", imgs);
					jsonObject.put("icon", imgs2);
					jsonObject.put("bwh", sign_info_sanwei_text.getText()
							.toString());
					jsonObject.put("age", MyDateUtils.getAge(MyDateUtils
							.parseStrToDate(dateText.getText().toString(),
									"yyyy-MM-dd")));
					jsonObject.put("constellation", MyDateUtils
							.getConstellation(dateText.getText().toString()));
					jsonObject.put("girlCity", sign_info_diqu_text.getText()
							.toString());
					jsonObject.put("birthday", dateText.getText().toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				bundle.putSerializable(HttpRequestUtils.BUNDLE_KEY_PARAMS,
						jsonObject);
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, true);
				msg.setData(bundle);
				msg.sendToTarget();

				imgs = null;
				imgs2 = null;
			}
		}).start();
	}

	class SubmitHttpHandler extends HttpCallBackHandler {

		public SubmitHttpHandler(Looper looper) {
			super(looper);
		}

		public SubmitHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");
				if (success) {
					BubbleUtil.alertMsg(context,
							R.string.write_article_submite_success);
					redirect();
				} else {
					BubbleUtil.alertMsg(context,
							resultObj.getString("errorMessage"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				BubbleUtil.alertMsg(context, R.string.base_response_error);
			}
		}

	}

	private byte[] getByteByBitmap(Bitmap bmp) {
		byte[] compressData = null;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
		// bmp.
		compressData = outStream.toByteArray();
		try {
			outStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return compressData;
	}

	/**
	 * 转换到其它activity
	 */
	private void redirect() {
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_info, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (alertDialogWindowBig != null
				&& alertDialogWindowBig.getDialog() != null) {
			alertDialogWindowBig.getDialog().dismiss();
		}
		if (alertDialogWindowSmall != null
				&& alertDialogWindowSmall.getDialog() != null) {
			alertDialogWindowSmall.getDialog().dismiss();
		}
	}

}
