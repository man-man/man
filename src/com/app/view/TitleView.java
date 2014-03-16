package com.app.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.activity.Sign;
import com.app.activity.SignInfo;
import com.app.activity.Vote;
import com.app.activity.Woman;
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

	private OnClickListener mLeftBtClickListener; // 左侧按钮点击监听

	public TitleView(Context context) {
		this(context, null);
	}

	public TitleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.Title);

		// 获取自定义属性和默认值
		isSubmit = mTypedArray.getBoolean(R.styleable.Title_isSubmit, false);
		isRelease = mTypedArray.getBoolean(R.styleable.Title_isRelease, false);
		titleName = mTypedArray.getString(R.styleable.Title_titleName);

		mTypedArray.recycle();
		setupViews();
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
					if (mLeftBtClickListener != null) {
						mLeftBtClickListener.onClick(v);
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
					if (mLeftBtClickListener != null) {
						mLeftBtClickListener.onClick(v);
					}
				}
			});
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

	public OnClickListener getmLeftBtClickListener() {
		return mLeftBtClickListener;
	}

	public void setmLeftBtClickListener(OnClickListener mLeftBtClickListener) {
		this.mLeftBtClickListener = mLeftBtClickListener;
	}

	public String getTitleName() {
		return titleName;
	}

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

}
