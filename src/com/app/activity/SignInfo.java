package com.app.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

	private String small_img_base64 = "";
	private String big_img_base64 = "";

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
					byte[] bytes2 = getByteByBitmap(bitmap);
					small_img_base64 = Base64Utils.encode(bytes2);
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
					byte[] bytes2 = getByteByBitmap(bitmap);
					big_img_base64 = Base64Utils.encode(bytes2);
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
				sign_info_sanwei_text.setText(sanwei1 + "  " + sanwei2 + "  "
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
					// Uri uri = data.getData();
					// BaseUtils.COMMON_PICASSO.load(getFilePath(uri)).into(
					// sign_info_photo);
					// Bitmap bitmap = getBitmapFromUri(data.getData());
					// small_img = bitmap;
					// // sign_info_photo.setImageBitmap(bitmap);

					Uri uri = data.getData();
					int tmparr[] = {
							new Float(BaseUtils.getScreenHeight(this) * 0.3f)
									.intValue(),
							new Float(BaseUtils.getScreenWidth(this) * 0.3f)
									.intValue() };
					Bitmap tmp = createNewBitmapAndCompressByFile(
							getFilePath(uri), tmparr);
					System.out.println(tmp.getWidth());
					System.out.println(tmp.getHeight());
					sign_info_photo.setImageBitmap(tmp);
					// BaseUtils.COMMON_PICASSO.load(getFilePath(uri)).into(
					// sign_info_bigimg);
					byte[] bytes2 = getByteByBitmap(tmp);
					small_img_base64 = Base64Utils.encode(bytes2);
				}
				alertDialogWindowSmall.getDialog().hide();
			}
		}
		if (requestCode == 6) {
			if (data != null && data.getData() != null) {
				if (data.getData() != null) {
					Uri uri = data.getData();
					int tmparr[] = { 480, 800 };
					Bitmap tmp = createNewBitmapAndCompressByFile(
							getFilePath(uri), tmparr);
					System.out.println(tmp.getWidth());
					System.out.println(tmp.getHeight());
					sign_info_bigimg.setImageBitmap(tmp);
					// BaseUtils.COMMON_PICASSO.load(getFilePath(uri)).into(
					// sign_info_bigimg);
					byte[] bytes2 = getByteByBitmap(tmp);
					big_img_base64 = Base64Utils.encode(bytes2);
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

	public Bitmap createNewBitmapAndCompressByFile(String filePath, int wh[]) {
		int offset = 100;
		File file = new File(filePath);
		long fileSize = file.length();
		if (200 * 1024 < fileSize && fileSize <= 1024 * 1024)
			offset = 90;
		else if (1024 * 1024 < fileSize)
			offset = 85;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 为true里只读图片的信息，如果长宽，返回的bitmap为null
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inDither = false;
		/**
		 * 计算图片尺寸 //TODO 按比例缩放尺寸
		 */
		BitmapFactory.decodeFile(filePath, options);

		int bmpheight = options.outHeight;
		int bmpWidth = options.outWidth;
		int inSampleSize = bmpheight / wh[1] > bmpWidth / wh[0] ? bmpheight
				/ wh[1] : bmpWidth / wh[0];
		// if(bmpheight / wh[1] < bmpWidth / wh[0]) inSampleSize = inSampleSize
		// * 2 / 3;//TODO 如果图片太宽而高度太小，则压缩比例太大。所以乘以2/3
		if (inSampleSize > 1)
			options.inSampleSize = inSampleSize;// 设置缩放比例
		options.inJustDecodeBounds = false;

		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			return null;
		}
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(is, null, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
			bitmap = null;
		}
		if (offset == 100)
			return bitmap;// 缩小质量
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, offset, baos);
		byte[] buffer = baos.toByteArray();
		options = null;
		if (buffer.length >= fileSize)
			return bitmap;
		return BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
	}

	/**
	 * 第一：我们先看下质量压缩方法：
	 * 
	 * @param image
	 * @return
	 */
	private Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 第二：图片按比例大小压缩方法（根据路径获取图片并压缩）：
	 * 
	 * @param srcPath
	 * @return
	 */
	private Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 第三：图片按比例大小压缩方法（根据Bitmap图片压缩）：
	 * 
	 * @param image
	 * @return
	 */
	private Bitmap comp(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	public String getFilePath(Uri uri) {
		// String[] imgs = { MediaStore.Images.Media.DATA };// 将图片URI转换成存储路径
		// Cursor cursor = this.managedQuery(uri, imgs, null, null, null);
		// int index =
		// cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		// cursor.moveToFirst();
		// String img_url = cursor.getString(index);
		String[] proj = { MediaStore.Images.Media.DATA };
		String imagePath = "";
		Cursor cursor = managedQuery(uri, proj,// 查哪一列
				null, null, null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				imagePath = cursor.getString(column_index);
			}
		}
		System.out.println(imagePath);
		return imagePath;
	}

	private Bitmap getBitmapFromUri(Uri uri) {
		try {
			// 读取uri所在的图片
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), uri);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 报名提交
	 */
	private void submitReq() {

		new Thread(new Runnable() {

			@Override
			public void run() {

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
					jsonObject.put("image", big_img_base64);
					jsonObject.put("icon", small_img_base64);
					String tmp = sign_info_sanwei_text.getText().toString();
					String[] arr = tmp.split("  ");
					jsonObject.put("bwh", sign_info_sanwei_text.getText()
							.toString());
					try {
						jsonObject.put("bust", arr[0]);
						jsonObject.put("waistline", arr[1]);
						jsonObject.put("hip", arr[2]);
					} catch (Exception e) {
					}

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
							R.string.girl_apply_submit_success);
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
