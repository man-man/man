package com.app.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

@SuppressLint("NewApi")
public class ViewGroupIntercept extends LinearLayout {

	public ViewGroupIntercept(Context context) {
		super(context);
	}

	public ViewGroupIntercept(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ViewGroupIntercept(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.onInterceptTouchEvent(ev);
	}
}
