package com.app.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.activity.Man;
import com.app.activity.Mine;
import com.app.activity.Woman;
import com.app.man.R;

@SuppressLint("NewApi")
public class FootView extends LinearLayout {

	public final static String[] TABS = new String[] { "woman", "man", "mine" };
	public final static int[] COLORS = new int[] { 0xffffffff, 0xff555555 };

	/**
	 * 当前tab
	 */
	private String currentTab = TABS[0];

	/**
	 * 装女郎
	 */
	private TextView womanText;

	/**
	 * 男人帮
	 */
	private TextView manText;

	/**
	 * 我的
	 */
	private TextView mineText;

	public FootView(Context context) {
		super(context);
	}

	public FootView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FootView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.FootView);

		// 获取自定义属性和默认值
		currentTab = mTypedArray.getString(R.styleable.FootView_currentTab);

		mTypedArray.recycle();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		setupViews();
	}

	private void setupViews() {
		LayoutInflater.from(getContext()).inflate(R.layout.foot, this);

		womanText = (TextView) findViewById(R.id.foot_woman);
		manText = (TextView) findViewById(R.id.foot_man);
		mineText = (TextView) findViewById(R.id.foot_mine);

		womanText.setOnClickListener(new WomanOnClick());
		manText.setOnClickListener(new ManOnClick());
		mineText.setOnClickListener(new MineOnClick());

		if (TABS[0].equals(currentTab)) {
			System.out.println(womanText.getTextColors());
			womanText.setTextColor(COLORS[0]);
			womanText.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.woman_on_bar, 0, 0);
		} else {
			womanText.setTextColor(COLORS[1]);
			womanText.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.woman_bar, 0, 0);
		}

		if (TABS[1].equals(currentTab)) {
			System.out.println(manText.getTextColors());
			manText.setTextColor(COLORS[0]);
			manText.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.man_on_bar, 0, 0);
		} else {
			System.out.println(manText.getTextColors());
			manText.setTextColor(COLORS[1]);
			manText.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.man_bar, 0, 0);
		}

		if (TABS[2].equals(currentTab)) {
			mineText.setTextColor(COLORS[0]);
			mineText.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.mine_on_bar, 0, 0);
		} else {
			mineText.setTextColor(COLORS[1]);
			mineText.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.mine_bar, 0, 0);
		}

	}

	class WomanOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (!TABS[0].equals(currentTab)) {
				Intent intent = new Intent(getContext(), Woman.class);
				getContext().startActivity(intent);
			}
		}

	}

	class ManOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (!TABS[1].equals(currentTab)) {
				Intent intent = new Intent(getContext(), Man.class);
				getContext().startActivity(intent);
			}
		}

	}

	class MineOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (!TABS[2].equals(currentTab)) {
				Intent intent = new Intent(getContext(), Mine.class);
				getContext().startActivity(intent);
			}
		}

	}

	public String getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(String currentTab) {
		this.currentTab = currentTab;
	}

}
