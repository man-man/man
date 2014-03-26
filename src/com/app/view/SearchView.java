package com.app.view;

import com.app.activity.WriteArticle;
import com.app.man.R;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SearchView extends LinearLayout {

	private Context context;

	private boolean isWrite; // 是否能撰写文章

	private View writeBt; // 跳转到撰写文章页button

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

	private void setupViews() {
		ViewGroup viewG = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.search, this);

		writeBt = viewG.findViewById(R.id.search_view_write);

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

}
