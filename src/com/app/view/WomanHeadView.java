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

import com.app.activity.Sign;
import com.app.activity.SignInfo;
import com.app.activity.Vote;
import com.app.activity.Woman;
import com.app.man.R;

@SuppressLint("NewApi")
public class WomanHeadView extends LinearLayout {

	public final static String[] TABS = new String[] { "rank", "vote", "sign" };

	/**
	 * 当前tab
	 */
	private String curTab = TABS[0];

	/**
	 * 排行榜
	 */
	private TextView rankText;

	/**
	 * 评选
	 */
	private TextView voteText;

	/**
	 * 报名
	 */
	private TextView signText;

	public WomanHeadView(Context context) {
		super(context);
	}

	public WomanHeadView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WomanHeadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.WomanHeadView);

		// 获取自定义属性和默认值
		curTab = mTypedArray.getString(R.styleable.WomanHeadView_curTab);

		mTypedArray.recycle();
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		setupViews();
	}

	private void setupViews() {
		LayoutInflater.from(getContext()).inflate(R.layout.head, this);

		rankText = (TextView) findViewById(R.id.woman_rank);
		voteText = (TextView) findViewById(R.id.woman_vote);
		signText = (TextView) findViewById(R.id.woman_sign);
		
		rankText.setOnClickListener(new RankOnClick());
		voteText.setOnClickListener(new VoteOnClick());
		signText.setOnClickListener(new SignOnClick());
		
		if(TABS[0].equals(curTab)){
			rankText.setBackgroundResource(R.drawable.head_bottom);
		}else{
			rankText.setBackgroundResource(0);
		}
		
		if(TABS[1].equals(curTab)){
			voteText.setBackgroundResource(R.drawable.head_bottom);
		}else{
			voteText.setBackgroundResource(0);
		}
		
		if(TABS[2].equals(curTab)){
			signText.setBackgroundResource(R.drawable.head_bottom);
		}else{
			signText.setBackgroundResource(0);
		}
		
	}
	
	class RankOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(!TABS[0].equals(curTab)){
				Intent intent = new Intent(getContext(), Woman.class);
				getContext().startActivity(intent);
			}
		}
		
	}
	
	class VoteOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(!TABS[1].equals(curTab)){
				Intent intent = new Intent(getContext(), Vote.class);
				getContext().startActivity(intent);
			}
		}
		
	}
	
	class SignOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(!TABS[2].equals(curTab)){
				Intent intent = new Intent(getContext(), SignInfo.class);
				getContext().startActivity(intent);
			}
		}
		
	}

	public String getCurTab() {
		return curTab;
	}

	public void setCurTab(String curTab) {
		this.curTab = curTab;
	}

}
