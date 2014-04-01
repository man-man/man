package com.app.view;

import com.app.common.Constant;
import com.app.man.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class SexView extends LinearLayout {

	Context context;

	// 控件
	private TextView girlView;
	private TextView boyView;

	private int curSex = Constant.USER_SEX_BOY;

	public SexView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		setupViews();
	}

	public SexView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setupViews();
	}

	public SexView(Context context) {
		super(context);
		this.context = context;
		setupViews();
	}

	private void setupViews() {
		LayoutInflater.from(context).inflate(R.layout.sex, this);

		girlView = (TextView) findViewById(R.id.role_sex_girl);
		boyView = (TextView) findViewById(R.id.role_sex_boy);

		girlView.setOnClickListener(sexOnClick);
		boyView.setOnClickListener(sexOnClick);
	}

	OnClickListener sexOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.role_sex_boy:
				curSex = Constant.USER_SEX_BOY;
				boyView.setBackgroundColor(0xff007FFA);
				boyView.setTextColor(Color.WHITE);
				girlView.setBackgroundColor(Color.WHITE);
				girlView.setTextColor(0xff007FFA);
				break;
			case R.id.role_sex_girl:
				curSex = Constant.USER_SEX_GIRL;
				girlView.setBackgroundColor(0xff007FFA);
				girlView.setTextColor(Color.WHITE);
				boyView.setBackgroundColor(Color.WHITE);
				boyView.setTextColor(0xff007FFA);
				break;
			default:
				break;
			}
		}
	};

	public int getCurSex() {
		return curSex;
	}

	public void setCurSex(int curSex) {
		this.curSex = curSex;
	}

}