package com.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class SummaryTextView extends TextView {
	

	public SummaryTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SummaryTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SummaryTextView(Context context) {
		super(context);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

}
