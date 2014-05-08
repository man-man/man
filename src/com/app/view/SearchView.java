package com.app.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.activity.WriteArticle;
import com.app.man.R;

public class SearchView extends LinearLayout {

	private Context context;

	private boolean isWrite; // 是否能撰写文章

	/**
	 * 关键字
	 */
	private String keyword;

	/**
	 * 搜索按钮点击事件
	 */
	private OnClickListener searchOnClick;

	// 控件
	private EditText searchInput; // 搜索框
	private View writeBt; // 跳转到撰写文章页button
	private TextView searchBtn; // 搜索按钮

	public SearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setupViews();
	}

	public SearchView(Context context) {
		super(context);
		this.context = context;
		setupViews();
	}

	/**
	 * 清除input焦点
	 */
	public void clearInputFocus() {
		if (searchInput != null) {
			searchInput.clearFocus();
			//关闭软件盘
			InputMethodManager mInputMethodManager = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			mInputMethodManager.hideSoftInputFromWindow(((Activity) context)
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private void setupViews() {
		ViewGroup viewG = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.search, this);

		searchInput = (EditText) viewG.findViewById(R.id.search_input);
		writeBt = viewG.findViewById(R.id.search_view_write);
		searchBtn = (TextView) viewG.findViewById(R.id.search_btn);

		keyword = searchInput.getText().toString();

		searchInput.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					searchBtn.setVisibility(View.VISIBLE);
				} else {
					searchBtn.setVisibility(View.GONE);
				}
			}
		});

		writeBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				context.startActivity(new Intent(context, WriteArticle.class));
			}
		});

	}

	public boolean isWrite() {
		return isWrite;
	}

	public void setWrite(boolean isWrite) {
		this.isWrite = isWrite;
		if (isWrite) {
			writeBt.setVisibility(View.VISIBLE);
		}
	}

	public OnClickListener getSearchOnClick() {
		return searchOnClick;
	}

	public void setSearchOnClick(OnClickListener searchOnClick) {
		this.searchOnClick = searchOnClick;
		if (searchBtn != null) {
			searchBtn.setOnClickListener(searchOnClick);
		}
	}

	public String getKeyword() {
		keyword = searchInput.getText().toString();
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

}
