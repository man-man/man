package com.app.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.activity.VoteAfter;
import com.app.common.BaseUtils;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
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
	 * 评选按钮容器
	 */
	private LinearLayout voteBts;
	
	private PkListHttpHandler rankQ = new PkListHttpHandler();

	public VoteView(Context context) {
		super(context);
	}

	public VoteView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
	}

	public VoteView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private View getItemView(JSONObject userObj) {
		View itemView = LayoutInflater.from(getContext()).inflate(
				R.layout.vote_item, null);

		LinearLayout itemContent = (LinearLayout) itemView
				.findViewById(R.id.vote_item);
		itemContent.setLayoutParams(new LinearLayout.LayoutParams(voteInfoLeft
				.getWidth(), voteInfoLeft.getHeight()));

		ImageView img = (ImageView) itemView.findViewById(R.id.vote_item_img);
		try {
			img.setImageDrawable(Drawable.createFromPath(BaseUtils
					.downloadImage(userObj.getString("imageUrl"), BaseUtils
							.getScreenWidth(VoteView.this.getContext()) / 2)));
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
	 * 请求数据内部类
	 * 
	 * @author XH
	 * 
	 */
	class PkListHttpHandler extends HttpCallBackHandler {

		public PkListHttpHandler(Looper looper) {
			super(looper);
		}

		public PkListHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");

				if (success) {
					JSONObject data = (JSONObject) resultObj.get("data");
					JSONArray users = data.getJSONArray("users");

					int len = users.length();
					if (len == 2) {
						voteInfoLeft.addView(getItemView(users.getJSONObject(0)));// users.getJSONObject(0)
						voteInfoRight.addView(getItemView(users.getJSONObject(1)));
					}

				} else {
					Toast.makeText(VoteView.this.getContext(),
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(VoteView.this.getContext(),
						R.string.base_response_error, Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		setupViews();
		// rankReqSend();
		new Thread(new Runnable() {
			public void run() {
//				String resultStr = HttpRequestUtils.getResFromHttpUrl(false,
//						HttpRequestUtils.BUNDLE_KEY_HTTPURL,
//						HttpRequestUtils.BASE_HTTP_CONTEXT + "GetGirlPK.shtml");
//				
//				JSONTokener jsonParser = new JSONTokener(resultStr);
//				// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
//				try {
//					JSONObject resultObj = (JSONObject) jsonParser.nextValue();
//					Boolean success = resultObj.getBoolean("success");
//
//					if (success) {
//						JSONObject data = (JSONObject) resultObj.get("data");
//						JSONArray users = data.getJSONArray("users");
//
//						int len = users.length();
//						if (len == 2) {
//							voteInfoLeft.addView(getItemView(users.getJSONObject(0)));// users.getJSONObject(0)
//							voteInfoRight.addView(getItemView());
//						}
//
//					} else {
//						Toast.makeText(VoteView.this.getContext(),
//								resultObj.getString("errorMessage"),
//								Toast.LENGTH_SHORT).show();
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//					Toast.makeText(VoteView.this.getContext(),
//							R.string.base_response_error, Toast.LENGTH_SHORT)
//							.show();
//				}
				Message msg = rankQ.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT + "GetGirlPK.shtml");
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
		System.out.println("onLayout");
	}

	/**
	 * 发送http请求
	 */
	private void rankReqSend() {
		System.out.println("rankReqSend");
		PkListHttpHandler rankQ = new PkListHttpHandler();
		Message msg = rankQ.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
				HttpRequestUtils.BASE_HTTP_CONTEXT + "GetGirlPK.shtml");
		bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
		msg.setData(bundle);
		msg.sendToTarget();
	}

	private void setupViews() {

		voteInfoLeft = (RelativeLayout) findViewById(R.id.vote_info_left);
		voteInfoRight = (RelativeLayout) findViewById(R.id.vote_info_right);
		voteBtLeft = (VoteButtonView) findViewById(R.id.vote_bt_left);
		voteBtRight = (VoteButtonView) findViewById(R.id.vote_bt_right);

		voteInfoLeft.addView(getItemView());
		voteInfoRight.addView(getItemView());

		voteBtLeft.setOnClickListener(new VoteBtLeftOnClick());
		voteBtRight.setOnClickListener(new VoteBtRightOnClick());
	}

	private View getItemView() {
		System.out.println("-----------voteInfoLeft" + voteInfoLeft);

		View itemView = LayoutInflater.from(getContext()).inflate(
				R.layout.vote_item, null);

		LinearLayout itemContent = (LinearLayout) itemView
				.findViewById(R.id.vote_item);
		itemContent.setLayoutParams(new LinearLayout.LayoutParams(voteInfoLeft
				.getWidth(), voteInfoLeft.getHeight()));

		ImageView img = (ImageView) itemView.findViewById(R.id.vote_item_img);

		// LayoutParams params = (LayoutParams) img.getLayoutParams();
		// params.width = voteInfoLeft.getWidth();
		// params.height = (int) (272/152f * params.width);

		// img.setLayoutParams(params);

		return itemView;
	}

	class VoteBtLeftOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getContext(), VoteAfter.class);
			getContext().startActivity(intent);
		}

	}

	class VoteBtRightOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getContext(), VoteAfter.class);
			getContext().startActivity(intent);
		}

	}

}
