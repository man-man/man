package com.app.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.activity.VoteAfter;
import com.app.common.BaseUtils;
import com.app.man.R;

/**
 * 评选
 * 
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
	}

	public VoteView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VoteView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
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

		voteBtLeft.setOnClickListener(new VoteBtLeftOnClick());
		voteBtRight.setOnClickListener(new VoteBtRightOnClick());

		rendItems();
	}

	// private View getItemView() {
	// System.out.println("-----------voteInfoLeft" + voteInfoLeft);
	//
	// View itemView = LayoutInflater.from(getContext()).inflate(
	// R.layout.vote_item, null);
	//
	// LinearLayout itemContent = (LinearLayout) itemView
	// .findViewById(R.id.vote_item);
	// itemContent.setLayoutParams(new LinearLayout.LayoutParams(voteInfoLeft
	// .getWidth(), voteInfoLeft.getHeight()));
	//
	// ImageView img = (ImageView) itemView.findViewById(R.id.vote_item_img);

	// LayoutParams params = (LayoutParams) img.getLayoutParams();
	// params.width = voteInfoLeft.getWidth();
	// params.height = (int) (272/152f * params.width);

	// img.setLayoutParams(params);

	// return itemView;
	// }

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
			// img.setImageDrawable(Drawable.createFromPath(BaseUtils
			// .downloadImage(userObj.getString("imageUrl"), BaseUtils
			// .getScreenWidth(VoteView.this.getContext()) / 2)));
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

	class VoteBtLeftOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d("test", "--------------VoteBtLeftOnClick setupViews");

			Intent intent = new Intent(getContext(), VoteAfter.class);
			if (leftUserData != null) {
				try {
					String id = leftUserData.getString("id");
					intent.putExtra("girlId", id);
					// HttpRequestUtils.getResFromHttpUrl(false,
					// HttpRequestUtils.BASE_HTTP_CONTEXT
					// + "Vote.shtml?userId="
					// + BaseUtils.CUR_USER_MAP.get("id")
					// + "&girlId=" + id, null);
					getContext().startActivity(intent);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}

	}

	class VoteBtRightOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d("test", "--------------VoteBtRightOnClick setupViews");
			Activity a = (Activity) getContext();

			Intent intent = new Intent(getContext(), VoteAfter.class);

			if (rightUserData != null) {
				try {
					String id = rightUserData.getString("id");
					intent.putExtra("girlId", id);
					// HttpRequestUtils.getResFromHttpUrl(false,
					// HttpRequestUtils.BASE_HTTP_CONTEXT
					// + "Vote.shtml?userId="
					// + BaseUtils.CUR_USER_MAP.get("id")
					// + "&girlId=" + id, null);
					getContext().startActivity(intent);
					a.finish();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}

	}

}
