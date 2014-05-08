package com.app.activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.app.common.BaseUtils;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;
import com.app.view.NetImageView;

public class VoteAfter extends BaseActivity {

	private String giveScore; // 赠送积分

	private TextView vote_after_name;
	private TextView vote_after_age;
	private TextView vote_after_xingzuo;
	private TextView vote_after_sanwei;
	private TextView vote_after_more;
	private TextView vote_after_givescore;
	private TextView vote_after_next;
	private NetImageView vote_after_img;

	GirlHttpHandler girlHttpHandler = new GirlHttpHandler();
	GiveScoreHttpHandler giveScoreHttpHandler = new GiveScoreHttpHandler();
	String girlId = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.vote_after);

		vote_after_name = (TextView) findViewById(R.id.vote_after_name);
		vote_after_age = (TextView) findViewById(R.id.vote_after_age);
		vote_after_xingzuo = (TextView) findViewById(R.id.vote_after_xingzuo);
		vote_after_sanwei = (TextView) findViewById(R.id.vote_after_sanwei);
		vote_after_more = (TextView) findViewById(R.id.vote_after_more);
		vote_after_givescore = (TextView) findViewById(R.id.vote_after_givescore);
		vote_after_next = (TextView) findViewById(R.id.vote_after_next);
		vote_after_img = (NetImageView) findViewById(R.id.vote_after_img);

		TextViewOnClick textViewOnClick = new TextViewOnClick();
		vote_after_next.setOnClickListener(textViewOnClick);
		vote_after_more.setOnClickListener(textViewOnClick);
		vote_after_givescore.setOnClickListener(textViewOnClick);

		girlId = getIntent().getStringExtra("girlId");

		vote_after_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				redrectPerson();
			}
		});

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = girlHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT
								+ "GetUserInfo.shtml?userId=" + girlId);
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
	}

	/**
	 * 跳转到个人中心
	 */
	private void redrectPerson() {
		Intent intentPerson = new Intent();
		intentPerson.setClass(VoteAfter.this, PersonMan.class);
		intentPerson.putExtra("userId", girlId);
		startActivity(intentPerson);
	}

	class TextViewOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.vote_after_next: // 下一组
				Intent intent = new Intent();
				intent.setClass(VoteAfter.this, Vote.class);
				startActivity(intent);
				finish();
				break;
			case R.id.vote_after_more: // 更多照片
				redrectPerson();
				break;
			case R.id.vote_after_givescore: // 送她积分
				final EditText et = new EditText(VoteAfter.this);
				et.setRawInputType(InputType.TYPE_CLASS_NUMBER);
				et.setBackgroundResource(R.drawable.tree_input_bg);

				// LinearLayout.LayoutParams params = new
				// LinearLayout.LayoutParams(
				// LinearLayout.LayoutParams.MATCH_PARENT,
				// LinearLayout.LayoutParams.WRAP_CONTENT);
				// params.setMargins(20, 0, 0, 20);
				// et.setLayoutParams(params);

				new AlertDialog.Builder(VoteAfter.this)
						.setTitle("请输入积分")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(et)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										giveScoreReq(et.getText().toString());
									}
								}).setNegativeButton("取消", null).show();

				break;
			}
		}
	}

	private void giveScoreReq(String score) {
		if (score == null || "".equals(score)) {
			return;
		}

		giveScore = score;

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = giveScoreHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(
						HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT
								+ "PresentScore.shtml?userId="
								+ BaseUtils.CUR_USER_MAP.get("userId")
								+ "&girlId=" + girlId + "&score="
								+ Integer.parseInt(giveScore));
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
	}

	/**
	 * 请求数据内部类
	 * 
	 * @author XH
	 * 
	 */
	class GirlHttpHandler extends HttpCallBackHandler {

		public GirlHttpHandler(Looper looper) {
			super(looper);
		}

		public GirlHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");

				if (success) {
					JSONObject userObj = (JSONObject) resultObj.get("data");
					try {
						vote_after_name.setText(getResources().getString(
								R.string.girlname)
								+ userObj.getString("name"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						vote_after_age.setText(getResources().getString(
								R.string.girlage)
								+ userObj.getString("age"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						vote_after_xingzuo.setText(getResources().getString(
								R.string.girlxingzuo)
								+ userObj.getString("constellation"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						vote_after_sanwei.setText(getResources().getString(
								R.string.girlsanwei)
								+ userObj.getString("bwh"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						vote_after_img.setNetUrl(userObj.getString("imageUrl"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					Toast.makeText(VoteAfter.this, R.string.req_vote_success,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(VoteAfter.this,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(VoteAfter.this, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 赠送积分内部类
	 * 
	 * @author XH
	 * 
	 */
	class GiveScoreHttpHandler extends HttpCallBackHandler {

		public GiveScoreHttpHandler(Looper looper) {
			super(looper);
		}

		public GiveScoreHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");

				if (success) {

					Toast.makeText(VoteAfter.this, R.string.give_score_success,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(VoteAfter.this,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(VoteAfter.this, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
