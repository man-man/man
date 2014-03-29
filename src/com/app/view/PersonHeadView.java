package com.app.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.common.AlertDialogWindow;
import com.app.common.Base64Utils;
import com.app.common.BaseUtils;
import com.app.common.BubbleUtil;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.common.ImageUtil;
import com.app.common.JSONObjectSerializalble;
import com.app.man.R;

@SuppressLint("NewApi")
public class PersonHeadView extends LinearLayout {

	final String WIN_MENU_TITLE = "设置图片";
	final String[] WIN_MENU_DATAS = new String[] { "图库相册", "拍照" };

	/**
	 * 小图相机
	 */
	final int SMALL_PHOTO_PHOTO = 1;

	/**
	 * 大图相机
	 */
	final int BIG_PHOTO_PHOTO = 2;

	/**
	 * 小图相册
	 */
	final int SMALL_PHOTO_PICTURE = 3;

	/**
	 * 大图相册
	 */
	final int BIG_PHOTO_PICTURE = 4;

	/**
	 * 我的
	 */
	public static final int TYPE_MINE = 1;

	/**
	 * 他人
	 */
	public static final int TYPE_MAN = 2;

	Context context;

	Activity activity;

	/**
	 * 当前组件类型（1.我的2.他人 默认我的）
	 */
	private int curType;

	// 控件
	private NetImageView bigBgView; // 大背景图
	private NetImageView headLogoView; // 用户头像
	private Button signBtn; // 签到
	private TextView attenBtn; // 关注
	private TextView nameView; // 姓名 性别
	private TextView scoreView; // 积分
	private TextView ageView; // 年龄
	private TextView cityView; // 城市

	private JSONObject data; // 数据

	AlertDialogWindow alertDialogWindowSmall;
	AlertDialogWindow alertDialogWindowBig;

	private String small_img_base64 = "";
	private String big_img_base64 = "";

	private UploadHttpHandler submitHttpHandle = new UploadHttpHandler(); // 上传图片
	private SignHttpHandler signHttpHandler = new SignHttpHandler(); // 签到
	private AttenHttpHandler attenHttpHandler = new AttenHttpHandler(); // 关注

	private int attenStatus = 1; // 1关注 0取消关注

	public PersonHeadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		this.activity = (Activity) context;
		initAttrs(context, attrs);
		setupViews();
	}

	public PersonHeadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.activity = (Activity) context;
		initAttrs(context, attrs);
		setupViews();
	}

	public PersonHeadView(Context context) {
		super(context);
		this.context = context;
		this.activity = (Activity) context;
		setupViews();
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.PersonHeadView);

		// 获取自定义属性和默认值
		curType = mTypedArray.getInt(R.styleable.PersonHeadView_curType,
				TYPE_MINE);
		mTypedArray.recycle();
	}

	private void setupViews() {
		LayoutInflater.from(context).inflate(R.layout.person_head, this);

		bigBgView = (NetImageView) findViewById(R.id.person_bg_img);
		headLogoView = (NetImageView) findViewById(R.id.person_user_img);
		signBtn = (Button) findViewById(R.id.person_sign);
		attenBtn = (TextView) findViewById(R.id.person_atten);
		nameView = (TextView) findViewById(R.id.person_username);
		scoreView = (TextView) findViewById(R.id.person_score);
		ageView = (TextView) findViewById(R.id.person_age);
		cityView = (TextView) findViewById(R.id.person_city);

		if (curType == TYPE_MINE) {
			bigBgView.setOnClickListener(photoOnClick);
			headLogoView.setOnClickListener(photoOnClick);

			if (alertDialogWindowSmall == null) {
				alertDialogWindowSmall = new AlertDialogWindow(WIN_MENU_TITLE,
						WIN_MENU_DATAS, context, iclSmall);
			}
			if (alertDialogWindowBig == null) {
				alertDialogWindowBig = new AlertDialogWindow(WIN_MENU_TITLE,
						WIN_MENU_DATAS, context, iclBig);
			}
			signBtn.setVisibility(View.VISIBLE);
			signBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					signReq();
				}
			});
		} else {
			attenBtn.setVisibility(View.VISIBLE);
			attenBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (attenBtn.getText().equals(
							getResources().getString(R.string.atten_has_txt))) { // 已关注
						attenStatus = 0; // 取消关注
					} else {
						attenStatus = 1; // 关注
					}
					attenReq();
				}
			});
		}
	}

	OnClickListener photoOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.person_user_img:
				alertDialogWindowSmall.getDialog().show();
				break;
			case R.id.person_bg_img:
				alertDialogWindowBig.getDialog().show();
				break;
			default:
				break;
			}
		}
	};

	OnClickListener iclSmall = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String txt = ((TextView) v).getText().toString();

			if (WIN_MENU_DATAS[0].equals(txt)) { // 图库相册
				Intent picture = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				activity.startActivityForResult(picture, SMALL_PHOTO_PICTURE);
			} else if (WIN_MENU_DATAS[1].equals(txt)) { // 相机
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				activity.startActivityForResult(intent, SMALL_PHOTO_PHOTO);
			}
			alertDialogWindowSmall.getDialog().hide();
		}
	};
	OnClickListener iclBig = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String txt = ((TextView) v).getText().toString();

			if (WIN_MENU_DATAS[0].equals(txt)) { // 图库相册
				Intent picture = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				activity.startActivityForResult(picture, BIG_PHOTO_PICTURE);
			} else if (WIN_MENU_DATAS[1].equals(txt)) { // 相机
				Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				activity.startActivityForResult(intent2, BIG_PHOTO_PHOTO);
			}
			alertDialogWindowSmall.getDialog().hide();
		}
	};

	/**
	 * 相册选完图片 或 相机拍完照之后回调
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void activityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case SMALL_PHOTO_PHOTO:
			if (data != null && data.getExtras() != null) {
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				if (bitmap != null) {
					// sign_info_photo.setDrawingCacheEnabled(true);
					byte[] bytes2 = ImageUtil.getByteByBitmap(bitmap);
					small_img_base64 = Base64Utils.encode(bytes2);
					headLogoView.setImageBitmap(bitmap);
				}
				submitHeadImg();
				alertDialogWindowSmall.getDialog().hide();
			}
			break;
		case SMALL_PHOTO_PICTURE:
			if (data != null && data.getData() != null) {
				if (data.getData() != null) {
					Uri uri = data.getData();
					int tmparr[] = { 480, 800 };
					Bitmap tmp = ImageUtil.createNewBitmapAndCompressByFile(
							ImageUtil.getFilePath(context, uri), tmparr);
					headLogoView.setImageBitmap(tmp);
					// BaseUtils.COMMON_PICASSO.load(getFilePath(uri)).into(
					// sign_info_bigimg);
					byte[] bytes2 = ImageUtil.getByteByBitmap(tmp);
					small_img_base64 = Base64Utils.encode(bytes2);
				}
				submitHeadImg();
				alertDialogWindowSmall.getDialog().hide();
			}
			break;
		case BIG_PHOTO_PHOTO:
			if (data != null && data.getExtras() != null) {
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				if (bitmap != null) {
					// sign_info_photo.setDrawingCacheEnabled(true);
					byte[] bytes2 = ImageUtil.getByteByBitmap(bitmap);
					big_img_base64 = Base64Utils.encode(bytes2);
					bigBgView.setImageBitmap(bitmap);
				}
				alertDialogWindowBig.getDialog().hide();
				submitBigImg();
			}
			break;
		case BIG_PHOTO_PICTURE:
			if (data != null && data.getData() != null) {
				if (data.getData() != null) {
					Uri uri = data.getData();
					int tmparr[] = { 960, 480 };
					Bitmap tmp = ImageUtil.createNewBitmapAndCompressByFile(
							ImageUtil.getFilePath(context, uri), tmparr);
					System.out.println(tmp.getWidth());
					System.out.println(tmp.getHeight());
					bigBgView.setImageBitmap(tmp);
					// BaseUtils.COMMON_PICASSO.load(getFilePath(uri)).into(
					// sign_info_bigimg);
					byte[] bytes2 = ImageUtil.getByteByBitmap(tmp);
					big_img_base64 = Base64Utils.encode(bytes2);
				}
				alertDialogWindowBig.getDialog().hide();
				submitBigImg();
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 渲染数据
	 */
	private void rendData() {
		try {
			bigBgView.setNetUrl(data.getString("backgroundImageUrl"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			headLogoView.setNetUrl(data.getString("imageUrl"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			signBtn.setText(data.getBoolean("isSigned") ? R.string.sign_has_txt
					: R.string.sign_txt);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		try {
			attenBtn.setText(data.getBoolean("isAttention") ? R.string.atten_has_txt
					: R.string.atten_txt);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		try {
			scoreView.setText(data.getString("score"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			ageView.setText(data.getString("age"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			cityView.setText(data.getString("girlCity"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			Drawable sexImg;
			Resources res = getResources();
			if ((data.getString("gender")).equals("1")) {
				sexImg = res.getDrawable(R.drawable.sex_boy);
			} else {
				sexImg = res.getDrawable(R.drawable.sex_girl);
			}
			// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
			sexImg.setBounds(0, 0, sexImg.getMinimumWidth(),
					sexImg.getMinimumHeight());
			nameView.setCompoundDrawables(null, null, sexImg, null);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			nameView.setText(data.getString("name"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
		if (data != null) {
			rendData();
		}
	}

	/**
	 * 上传用户头像图片
	 */
	private void submitHeadImg() {

		new Thread(new Runnable() {

			@Override
			public void run() {

				Message msg = submitHttpHandle.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT
								+ "UploadUserImage.shtml?");

				JSONObjectSerializalble jsonObject = new JSONObjectSerializalble();
				try {
					jsonObject.put("userId",
							BaseUtils.CUR_USER_MAP.get("userId"));
					jsonObject.put("image", small_img_base64);
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

	/**
	 * 上传用户背景图片
	 */
	private void submitBigImg() {

		new Thread(new Runnable() {

			@Override
			public void run() {

				Message msg = submitHttpHandle.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT
								+ "UploadUserBackgroundImage.shtml?");

				JSONObjectSerializalble jsonObject = new JSONObjectSerializalble();
				try {
					jsonObject.put("userId",
							BaseUtils.CUR_USER_MAP.get("userId"));
					jsonObject.put("image", big_img_base64);
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

	class UploadHttpHandler extends HttpCallBackHandler {

		public UploadHttpHandler(Looper looper) {
			super(looper);
		}

		public UploadHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");
				if (success) {
					BubbleUtil.alertMsg(context, R.string.upload_img_success);
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

	/**
	 * 签到请求
	 */
	private void signReq() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = signHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(HttpRequestUtils.BASE_HTTP_CONTEXT);
				stringBuilder.append("Sign.shtml?userId="
						+ BaseUtils.CUR_USER_MAP.get("id"));
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						stringBuilder.toString());
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
	}

	class SignHttpHandler extends HttpCallBackHandler {

		public SignHttpHandler(Looper looper) {
			super(looper);
		}

		public SignHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");
				if (success) {
					signBtn.setText(R.string.sign_has_txt);
					BubbleUtil.alertMsg(context, R.string.sign_req_success);
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

	/**
	 * 关注请求
	 */
	private void attenReq() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = attenHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(HttpRequestUtils.BASE_HTTP_CONTEXT);
				stringBuilder.append("FollowUser.shtml?userId=");
				stringBuilder.append(BaseUtils.CUR_USER_MAP.get("id"));
				stringBuilder.append("&followUserId=");
				try {
					stringBuilder.append(data.getString("id"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				stringBuilder.append("&value=");
				stringBuilder.append(attenStatus);
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						stringBuilder.toString());
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
	}

	class AttenHttpHandler extends HttpCallBackHandler {

		public AttenHttpHandler(Looper looper) {
			super(looper);
		}

		public AttenHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");
				if (success) {

					if (attenStatus == 1) { // 关注请求
						attenBtn.setText(R.string.atten_has_txt);
						BubbleUtil
								.alertMsg(context, R.string.atten_req_success);
					} else { // 取消关注请求
						attenBtn.setText(R.string.atten_txt);
						BubbleUtil.alertMsg(context,
								R.string.atten_cancel_req_success);
					}
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
}
