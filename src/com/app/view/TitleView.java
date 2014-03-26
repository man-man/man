package com.app.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.activity.SignInfo;
import com.app.common.Base64Utils;
import com.app.common.BaseUtils;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.common.JSONObjectSerializalble;
import com.app.common.MyDateUtils;
import com.app.man.R;

@SuppressLint("NewApi")
public class TitleView extends LinearLayout {

	// 控件
	private ImageView backView; // 左侧返回按钮
	private TextView titleView; // 中间title
	private TextView submitView; // 左侧提交按钮(装女郎报名)
	private TextView releaseView; // 左侧发布按钮（发布秘密）

	// 是否显示控件
	private boolean isSubmit; // 是否显示提交按钮
	private boolean isRelease; // 是否显示发布按钮

	private String titleName; // 标题

	private OnClickListener mRightBtClickListener; // 左侧按钮点击监听

	SignInfoHttpHandler signInfoHttpHandler = new SignInfoHttpHandler();

	ImageView big;
	ImageView small;
	TextView birthday;
	TextView sanwei;
	TextView diqu;

	LinearLayout parentView;

	public TitleView(Context context) {
		super(context, null);
		setupViews();
	}

	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttrs(context, attrs);
		setupViews();
	}

	public TitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(context, attrs);
		setupViews();
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.Title);

		// 获取自定义属性和默认值
		isSubmit = mTypedArray.getBoolean(R.styleable.Title_isSubmit, false);
		isRelease = mTypedArray.getBoolean(R.styleable.Title_isRelease, false);
		titleName = mTypedArray.getString(R.styleable.Title_titleName);

		mTypedArray.recycle();
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

	private void setupViews() {
		LayoutInflater.from(getContext()).inflate(R.layout.title, this);

		backView = (ImageView) findViewById(R.id.title_back);
		titleView = (TextView) findViewById(R.id.title_name);
		submitView = (TextView) findViewById(R.id.title_submit);
		releaseView = (TextView) findViewById(R.id.title_release);

		titleView.setText(titleName);

		// 返回上一个activity
		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((Activity) getContext()).finish();
			}
		});

		// 提交按钮
		if (isSubmit) {
			submitView.setVisibility(VISIBLE);

			submitView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					parentView = (LinearLayout) TitleView.this.getParent();
					big = (ImageView) parentView
							.findViewById(R.id.sign_info_bigimg);
					small = (ImageView) parentView
							.findViewById(R.id.sign_info_photo);
					if (big == null) {
						if (mRightBtClickListener != null) {
							mRightBtClickListener.onClick(v);
						}
					} else {
						birthday = (TextView) parentView
								.findViewById(R.id.sign_info_date_text);
						sanwei = (TextView) parentView
								.findViewById(R.id.sign_info_sanwei_text);
						diqu = (TextView) parentView
								.findViewById(R.id.sign_info_diqu_text);

						new Thread(new Runnable() {

							@Override
							public void run() {
								// Bitmap bitmap = big.getDrawingCache();
								// if (bitmap == null) {
								// Toast.makeText(TitleView.this.getContext(),
								// "请拍一张大图", Toast.LENGTH_SHORT)
								// .show();
								// return;
								// }
								// byte[] bytes = getByteByBitmap(bitmap);
								// String imgs = Base64Utils.encode(bytes);
								// big.setDrawingCacheEnabled(false);
								//
								// Bitmap bitmap2 = small.getDrawingCache();
								// if (bitmap2 == null) {
								// Toast.makeText(TitleView.this.getContext(),
								// "请拍一张小图", Toast.LENGTH_SHORT)
								// .show();
								// return;
								// }
								// byte[] bytes2 = getByteByBitmap(bitmap2);
								// String imgs2 = Base64Utils.encode(bytes2);
								// small.setDrawingCacheEnabled(false);

								Bitmap bitmap = SignInfo.big_img;
								String imgs;
								if (SignInfo.big_img != null) {
									byte[] bytes = getByteByBitmap(bitmap);
									imgs = Base64Utils.encode(bytes);
									SignInfo.big_img = null;
								} else {
									// Toast.makeText(parentView.getContext(),
									// "请拍一张大图", Toast.LENGTH_SHORT)
									// .show();
									return;
								}

								Bitmap bitmap2 = SignInfo.small_img;
								String imgs2;
								if (SignInfo.small_img != null) {
									byte[] bytes2 = getByteByBitmap(bitmap2);
									imgs2 = Base64Utils.encode(bytes2);
									SignInfo.small_img = null;
								} else {
									// Toast.makeText(parentView.getContext(),
									// "请拍一张小图", Toast.LENGTH_SHORT)
									// .show();
									return;
								}

								Message msg = signInfoHttpHandler
										.obtainMessage();
								Bundle bundle = new Bundle();
								bundle.putString(
										HttpRequestUtils.BUNDLE_KEY_HTTPURL,
										HttpRequestUtils.BASE_HTTP_CONTEXT
												+ "ApplyGirl.shtml");
								JSONObjectSerializalble jsonObject = new JSONObjectSerializalble();
								try {
									// BaseUtils.CUR_USER_MAP.get("userId");
									// 8
									jsonObject.put("userId",
											BaseUtils.CUR_USER_MAP
													.get("userId"));
									jsonObject.put("image", imgs);
									jsonObject.put("icon", imgs2);
									jsonObject.put("bwh", sanwei.getText()
											.toString());
									jsonObject.put("age", MyDateUtils
											.getAge(MyDateUtils.parseStrToDate(
													birthday.getText()
															.toString(),
													"yyyy-MM-dd")));
									jsonObject.put("constellation", MyDateUtils
											.getConstellation(birthday
													.getText().toString()));
									jsonObject.put("girlCity", diqu.getText()
											.toString());
									jsonObject.put("birthday", birthday
											.getText().toString());
								} catch (JSONException e) {
									e.printStackTrace();
								}
								bundle.putSerializable(
										HttpRequestUtils.BUNDLE_KEY_PARAMS,
										jsonObject);
								bundle.putBoolean(
										HttpRequestUtils.BUNDLE_KEY_ISPOST,
										true);
								msg.setData(bundle);
								msg.sendToTarget();
							}
						}).start();
					}
				}
			});
		}

		// 发布按钮
		if (isRelease) {
			releaseView.setVisibility(VISIBLE);
			releaseView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mRightBtClickListener != null) {
						mRightBtClickListener.onClick(v);
					}
				}
			});
		}
	}

	class SignInfoHttpHandler extends HttpCallBackHandler {

		public SignInfoHttpHandler(Looper looper) {
			super(looper);
		}

		public SignInfoHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");

				if (success) {
					Toast.makeText(parentView.getContext(), "已成功提交申请，请等待审批",
							Toast.LENGTH_SHORT).show();

				} else {
					Toast.makeText(parentView.getContext(),
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				// Toast.makeText(parentView.getContext(),
				// R.string.base_response_error, Toast.LENGTH_SHORT)
				// .show();
			}
		}

	}

	public boolean isSubmit() {
		return isSubmit;
	}

	public void setSubmit(boolean isSubmit) {
		this.isSubmit = isSubmit;
	}

	public boolean isRelease() {
		return isRelease;
	}

	public void setRelease(boolean isRelease) {
		this.isRelease = isRelease;
	}

	public OnClickListener getmRightBtClickListener() {
		return mRightBtClickListener;
	}

	public void setmRightBtClickListener(OnClickListener mRightBtClickListener) {
		this.mRightBtClickListener = mRightBtClickListener;
	}

	public String getTitleName() {
		return titleName;
	}

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

}
