package com.app.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ListView;

import com.app.man.R;

@SuppressLint("NewApi")
public class InnerScrollView extends HorizontalScrollView {

	/**
	 * 父辈滚动条
	 */
	private ListView parentScrollView = null;

	public InnerScrollView(Context context) {
		this(context, null);
	}

	public InnerScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public InnerScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.InnerScrollView);

		int parentScrollViewId = mTypedArray.getResourceId(
				R.styleable.InnerScrollView_parentScrollView, 0);

		Log.d("test", "-----id:" + parentScrollViewId);
		if (parentScrollViewId != 0) {
			parentScrollView = null;
		}
		mTypedArray.recycle();
	}

	private int lastScrollDelta = 0;

	public void resume() {
		overScrollBy(0, -lastScrollDelta, 0, getScrollY(), 0, getScrollRange(),
				0, 0, true);
		lastScrollDelta = 0;
	}

	int mTop = 10;

	/**
	 * 将targetView滚到最顶端
	 */
	public void scrollTo(View targetView) {

		int oldScrollY = getScrollY();
		int top = targetView.getTop() - mTop;
		int delatY = top - oldScrollY;
		lastScrollDelta = delatY;
		overScrollBy(0, delatY, 0, getScrollY(), 0, getScrollRange(), 0, 0,
				true);
	}

	private int getScrollRange() {
		int scrollRange = 0;
		if (getChildCount() > 0) {
			View child = getChildAt(0);
			scrollRange = Math.max(0, child.getHeight() - (getHeight()));
		}
		return scrollRange;
	}

	int currentY;
	int currentX;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (parentScrollView == null) {
			return super.onInterceptTouchEvent(ev);
		} else {
			if (ev.getAction() == MotionEvent.ACTION_DOWN) {
				// 将父scrollview的滚动事件拦截
				currentY = (int) ev.getY();
				currentX = (int) ev.getX();
				setParentScrollAble(false);
				return super.onInterceptTouchEvent(ev);
			} else if (ev.getAction() == MotionEvent.ACTION_UP) {
				// 把滚动事件恢复给父Scrollview
				setParentScrollAble(true);
			} else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			}
		}
		return super.onInterceptTouchEvent(ev);

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		View child = getChildAt(0);
		Log.d("test", "----------parent:" + parentScrollView);
		if (parentScrollView != null) {
			if (ev.getAction() == MotionEvent.ACTION_MOVE) {

				int height = child.getMeasuredHeight();
				height = height - getMeasuredHeight();

				// System.out.println("height=" + height);
				int scrollY = getScrollY();
				// System.out.println("scrollY" + scrollY);
				int y = (int) ev.getY();
				int x = (int) ev.getX();

				Log.d("test", "---------y:" + Math.abs(currentY - y));
				Log.d("test", "---------x:" + Math.abs(currentX - x));
				if (Math.abs(currentX - x) >= 2) { // 横滑
					Log.d("test", "-------------就把滚动拦截");
					setParentScrollAble(false);
				} else {
					Log.d("test", "-------------就把滚动交给父Scrollview");
					setParentScrollAble(true);
				}

				// // 手指向下滑动
				// if (currentY < y) {
				// if (scrollY <= 0) {
				// // 如果向下滑动到头，就把滚动交给父Scrollview
				// setParentScrollAble(true);
				// return false;
				// } else {
				// setParentScrollAble(false);
				//
				// }
				// } else if (currentY > y) {
				// if (scrollY >= height) {
				// // 如果向上滑动到头，就把滚动交给父Scrollview
				// setParentScrollAble(true);
				// return false;
				// } else {
				// setParentScrollAble(false);
				//
				// }
				//
				// }
				currentY = y;
				currentX = x;
			}
		}

		return super.onTouchEvent(ev);
	}

	/**
	 * 是否把滚动事件交给父scrollview
	 * 
	 * @param flag
	 */
	private void setParentScrollAble(boolean flag) {

		parentScrollView.requestDisallowInterceptTouchEvent(!flag);
	}

	public ListView getParentScrollView() {
		return parentScrollView;
	}

	public void setParentScrollView(ListView parentScrollView) {
		this.parentScrollView = parentScrollView;
	}

}