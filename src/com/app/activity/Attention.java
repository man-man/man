package com.app.activity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.common.BaseUtils;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;
import com.app.util.DensityUtil;
import com.app.view.NetImageView;

public class Attention extends BaseActivity {

	private ViewGroup attList; // 关注的人列表

	AttHttpHandler AttHttpHandler = new AttHttpHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.attention);

		attList = (ViewGroup) findViewById(R.id.atten_list);

		new Thread(new Runnable() {

			@Override
			public void run() {
				// 登陆的例子，get
				Message msg = AttHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT
								+ "GetFollowUser.shtml?userId="
								+ BaseUtils.CUR_USER_MAP.get("userId"));
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
	}

	private void rendItems(JSONArray datas) {

		attList.removeAllViews();

		for (int i = 0; i < datas.length(); i++) {
			try {
				View item = getView(datas.getJSONObject(i));
				attList.addView(item);

				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) item
						.getLayoutParams();
				params.setMargins(0, 0, 0, DensityUtil.dip2px(10));
				item.setLayoutParams(params);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private View getView(JSONObject obj) {
		LinearLayout convertView = (LinearLayout) LayoutInflater.from(this)
				.inflate(R.layout.attention_item, null);

		NetImageView headImg = (NetImageView) convertView
				.findViewById(R.id.atten_item_img);
		TextView name = (TextView) convertView
				.findViewById(R.id.atten_item_name);

		try {
			headImg.setNetUrl(obj.getString("imageUrl"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			name.setText(obj.getString("name"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return convertView;
	}

	class AttHttpHandler extends HttpCallBackHandler {

		public AttHttpHandler(Looper looper) {
			super(looper);
		}

		public AttHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");
				Map<String, Object> resultMap = new HashMap<String, Object>();
				if (success) {
					JSONObject data = (JSONObject) resultObj.get("data");
					JSONArray users = data.getJSONArray("users");

					rendItems(users);

				} else {
					Toast.makeText(Attention.this,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(Attention.this, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task, menu);
		return true;
	}

}
