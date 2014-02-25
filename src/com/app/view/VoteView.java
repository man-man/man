package com.app.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.app.activity.Sign;
import com.app.activity.Vote;
import com.app.activity.Woman;
import com.app.man.R;

/**
 * 评选
 * @author 王灵
 *
 */
@SuppressLint("NewApi")
public class VoteView extends LinearLayout {
	
	/**
	 * 评选信息左侧
	 */
	private RelativeLayout voteInfoLeft;
	
	/**
	 * 评选信息右侧
	 */
	private RelativeLayout voteInfoRight;
	
	/**
	 * 评选按钮容器
	 */
	private LinearLayout voteBts;

	public VoteView(Context context) {
		super(context);
	}

	public VoteView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
	}

	public VoteView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		setupViews();
	}

	private void setupViews() {
		
		voteInfoLeft = (RelativeLayout) findViewById(R.id.vote_info_left);
		voteInfoRight = (RelativeLayout) findViewById(R.id.vote_info_right);
		
		voteInfoLeft.addView(getItemView());
		voteInfoRight.addView(getItemView());
	}
	
	private View getItemView(){
		System.out.println("-----------voteInfoLeft" + voteInfoLeft);
		
		View itemView = LayoutInflater.from(getContext()).inflate(R.layout.vote_item, null);
		
		LinearLayout itemContent = (LinearLayout) itemView.findViewById(R.id.vote_item);
		itemContent.setLayoutParams(new LinearLayout.LayoutParams(voteInfoLeft.getWidth(), voteInfoLeft.getHeight()));
		
		ImageView img = (ImageView) itemView.findViewById(R.id.vote_item_img);
		
//		LayoutParams params = (LayoutParams) img.getLayoutParams();
//		params.width = voteInfoLeft.getWidth();
//		params.height = (int) (272/152f * params.width);
		
//		img.setLayoutParams(params);
		
		return itemView;
	}
	
	class RankOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getContext(), Woman.class);
			getContext().startActivity(intent);
		}
		
	}
	
	class VoteOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getContext(), Vote.class);
			getContext().startActivity(intent);
		}
		
	}
	
	class SignOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getContext(), Sign.class);
			getContext().startActivity(intent);
		}
		
	}


}
