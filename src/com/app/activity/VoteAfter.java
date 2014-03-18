package com.app.activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.common.BaseUtils;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;
import com.app.view.VoteView;

public class VoteAfter extends Activity {

	private TextView vote_after_name;
	private TextView vote_after_age;
	private TextView vote_after_xingzuo;
	private TextView vote_after_sanwei;
	private TextView vote_after_more;
	private TextView vote_after_givescore;
	private TextView vote_after_next;
	private ImageView vote_after_img;

	GirlHttpHandler girlHttpHandler = new GirlHttpHandler();
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
		vote_after_img = (ImageView) findViewById(R.id.vote_after_img);

		TextViewOnClick textViewOnClick = new TextViewOnClick();
		vote_after_next.setOnClickListener(textViewOnClick);
		vote_after_more.setOnClickListener(textViewOnClick);
		vote_after_givescore.setOnClickListener(textViewOnClick);

		girlId = getIntent().getStringExtra("girlId");

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

	class TextViewOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.vote_after_next) {
				Intent intent = new Intent();
				intent.setClass(VoteAfter.this, Vote.class);
				startActivity(intent);
				finish();
			}
			if (v.getId() == R.id.vote_after_more) {

			}
			if (v.getId() == R.id.vote_after_givescore) {

			}
		}
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
					vote_after_name.setText(getResources().getString(
							R.string.girlname)
							+ userObj.getString("name"));
					vote_after_age.setText(getResources().getString(
							R.string.girlage)
							+ userObj.getString("age"));
					vote_after_xingzuo.setText(getResources().getString(
							R.string.girlxingzuo)
							+ userObj.getString("constellation"));
					vote_after_sanwei.setText(getResources().getString(
							R.string.girlsanwei)
							+ userObj.getString("bwh"));
					vote_after_img
							.setImageDrawable(Drawable.createFromPath(BaseUtils.downloadImage(
									userObj.getString("imageUrl"),
									BaseUtils.getScreenWidth(VoteAfter.this) / 2)));

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
}
