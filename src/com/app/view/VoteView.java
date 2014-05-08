package com.app.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.activity.PersonMan;
import com.app.activity.VoteAfter;
import com.app.man.R;

/**
 * 评选
 * 
 * @author 王灵
 * 
 */
@SuppressLint("NewApi")
public class VoteView extends LinearLayout implements OnClickListener {

	Context context;

	/**
	 * 评选信息左侧
	 */
	private RelativeLayout voteInfoLeft;

	/**
	 * 评选信息右侧
	 */
	private RelativeLayout voteInfoRight;

	/**
	 * 评选左侧按钮
	 */
	private VoteButtonView voteBtLeft;

	/**
	 * 评选右侧按钮
	 */
	private VoteButtonView voteBtRight;

	/**
	 * 左侧用户的json对象
	 */
	private JSONObject leftUserData;
	/**
	 * 右侧用户的json对象
	 */
	private JSONObject rightUserData;

	public VoteView(Context context) {
		super(context);
		this.context = context;
	}

	public VoteView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public VoteView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			setupViews();
		}
	}

	private void setupViews() {
		Log.d("test", "--------------voteview setupViews");
		voteInfoLeft = (RelativeLayout) findViewById(R.id.vote_info_left);
		voteInfoRight = (RelativeLayout) findViewById(R.id.vote_info_right);
		voteBtLeft = (VoteButtonView) findViewById(R.id.vote_bt_left);
		voteBtRight = (VoteButtonView) findViewById(R.id.vote_bt_right);

		voteInfoLeft.setOnClickListener(this);
		voteInfoRight.setOnClickListener(this);
		voteBtLeft.setOnClickListener(this);
		voteBtRight.setOnClickListener(this);

		rendItems();
	}

	public void setData(JSONObject data) {
		JSONArray users;

		Log.d("test", "-------------voteview setData");
		try {
			users = data.getJSONArray("users");

			int len = users.length();
			if (len == 2) {
				leftUserData = users.getJSONObject(0);
				rightUserData = users.getJSONObject(1);
				rendItems();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(VoteView.this.getContext(),
					R.string.base_response_error, Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 渲染元素
	 */
	public void rendItems() {
		if (leftUserData == null || rightUserData == null) {
			return;
		}
		if (voteInfoLeft == null || voteInfoRight == null) {
			return;
		}
		voteInfoLeft.addView(getItemView(leftUserData));
		voteInfoRight.addView(getItemView(rightUserData));
	}

	private View getItemView(JSONObject userObj) {
		View itemView = LayoutInflater.from(getContext()).inflate(
				R.layout.vote_item, null);

		LinearLayout itemContent = (LinearLayout) itemView
				.findViewById(R.id.vote_item);
		itemContent.setLayoutParams(new LinearLayout.LayoutParams(voteInfoLeft
				.getWidth(), voteInfoLeft.getHeight()));

		NetImageView img = (NetImageView) itemView
				.findViewById(R.id.vote_item_img);
		try {
			img.setNetUrl(userObj.getString("imageUrl"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		TextView vote_item_girlname = (TextView) itemView
				.findViewById(R.id.vote_item_girlname);
		TextView vote_item_girlage = (TextView) itemView
				.findViewById(R.id.vote_item_girlage);
		TextView vote_item_girlxingzuo = (TextView) itemView
				.findViewById(R.id.vote_item_girlxingzuo);
		TextView vote_item_girlsanwei = (TextView) itemView
				.findViewById(R.id.vote_item_girlsanwei);
		try {
			vote_item_girlname.setText(getResources()
					.getText(R.string.girlname) + userObj.getString("name"));
			vote_item_girlage.setText(getResources().getText(R.string.girlage)
					+ userObj.getString("age"));
			vote_item_girlxingzuo.setText(getResources().getText(
					R.string.girlxingzuo)
					+ userObj.getString("constellation"));
			vote_item_girlsanwei.setText(getResources().getText(
					R.string.girlsanwei)
					+ userObj.getString("bwh"));
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return itemView;
	}

	/**
	 * 跳转到个人中心
	 */
	private void redirectPerson(String userId) {
		Intent intentPerson = new Intent();
		intentPerson.setClass(context, PersonMan.class);
		intentPerson.putExtra("userId", userId);
		context.startActivity(intentPerson);
	}

	/**
	 * 跳转到招标后页面
	 */
	private void redirectVoteAfter(String userId) {
		Intent intent = new Intent(context, VoteAfter.class);
		intent.putExtra("girlId", userId);
		context.startActivity(intent);
		((Activity) context).finish();
	}

	@Override
	public void onClick(View v) {

		try {
			switch (v.getId()) {
			case R.id.vote_info_left:
				redirectPerson(leftUserData.getString("id"));
				break;
			case R.id.vote_info_right:
				redirectPerson(rightUserData.getString("id"));
				break;
			case R.id.vote_bt_left:
				redirectVoteAfter(leftUserData.getString("id"));
				break;
			case R.id.vote_bt_right:
				redirectVoteAfter(rightUserData.getString("id"));
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
