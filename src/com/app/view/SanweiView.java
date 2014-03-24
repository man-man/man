package com.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.app.man.R;

public class SanweiView extends LinearLayout {

	public SanweiView(Context context) {
		super(context);
	}

	public SanweiView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private TextChangeView xiongweiView;
	private TextChangeView yaoweiView;
	private TextChangeView tunweiView;

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		LayoutInflater.from(getContext()).inflate(R.layout.sanwei_change, this);
		LinearLayout linearLayout = (LinearLayout) this.getChildAt(0);
		xiongweiView = (TextChangeView) linearLayout.getChildAt(0);
		yaoweiView = (TextChangeView) linearLayout.getChildAt(1);
		tunweiView = (TextChangeView) linearLayout.getChildAt(2);
	}

	public TextChangeView getXiongweiView() {
		return xiongweiView;
	}

	public TextChangeView getYaoweiView() {
		return yaoweiView;
	}

	public TextChangeView getTunweiView() {
		return tunweiView;
	}

}
