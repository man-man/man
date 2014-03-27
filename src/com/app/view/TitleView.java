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
					if (mRightBtClickListener != null) {
						mRightBtClickListener.onClick(v);
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
