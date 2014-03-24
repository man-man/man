package com.app.activity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.app.common.BaseUtils;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;
import com.app.util.ContextUtil;
import com.app.view.FmView;
import com.app.view.NetImageView;

/**
 * 我的
 * 
 * @author 王灵
 * 
 */
public class Mine extends BaseActivity {

	private ViewGroup taskBt; // 我的任务
	private ViewGroup photosBt; // 我的相册
	private ViewGroup collectBt; // 我的收藏
	private ViewGroup attBt; // 我关注的人
	private ViewGroup treeBt; // 树洞

	private ViewGroup curFmContainer; // 当前正在播放节目容器
	private ViewGroup fmListContainer; // 节目单容器
	private FmView fmView;

	private NetImageView mine_user_img;
	private NetImageView mine_bg_img;
	private TextView mine_score;
	private TextView mine_age;
	private TextView mine_city;
	private TextView mine_username;

	// mine_username android:drawableRight="@drawable/sex_girl"
	UserInfoHttpHandler userInfoHttpHandler = new UserInfoHttpHandler();
	FmHttpHandler fmHttpHandler = new FmHttpHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mine2);

		setupViews();
	}

	private void setupViews() {
		mine_user_img = (NetImageView) findViewById(R.id.mine_user_img);
		mine_bg_img = (NetImageView) findViewById(R.id.mine_bg_img);
		mine_score = (TextView) findViewById(R.id.mine_score);
		mine_age = (TextView) findViewById(R.id.mine_age);
		mine_city = (TextView) findViewById(R.id.mine_city);
		mine_username = (TextView) findViewById(R.id.mine_username);

		taskBt = (ViewGroup) findViewById(R.id.mine_task_panel);
		photosBt = (ViewGroup) findViewById(R.id.mine_photo_panel);
		collectBt = (ViewGroup) findViewById(R.id.mine_collect_panel);
		attBt = (ViewGroup) findViewById(R.id.mine_att_panel);
		treeBt = (ViewGroup) findViewById(R.id.mine_tree_panel);

		curFmContainer = (ViewGroup) findViewById(R.id.mine_group_3);
		fmListContainer = (ViewGroup) findViewById(R.id.mine_group_4);

		taskBt.setOnClickListener(btClickListener);
		photosBt.setOnClickListener(btClickListener);
		collectBt.setOnClickListener(btClickListener);
		attBt.setOnClickListener(btClickListener);
		treeBt.setOnClickListener(btClickListener);

		fmView = new FmView(this, curFmContainer, fmListContainer); // 初始化电台

		new Thread(new Runnable() {

			@Override
			public void run() {
				// 登陆的例子，get
				Message msg = userInfoHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT
								+ "GetUserInfo.shtml?" + "userId="
								+ BaseUtils.CUR_USER_MAP.get("userId"));
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();

		setFmData();
	}

	class UserInfoHttpHandler extends HttpCallBackHandler {

		public UserInfoHttpHandler(Looper looper) {
			super(looper);
		}

		public UserInfoHttpHandler() {
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
					JSONObject map = (JSONObject) resultObj.get("data");
					Iterator<String> it = map.keys();
					while (it.hasNext()) {
						String key = it.next();
						Object resultTmp = map.get(key);
						resultMap.put(key, resultTmp);
					}
					mine_score.setText(resultMap.get("score") + "");
					mine_age.setText(resultMap.get("age") + "");
					mine_city.setText(resultMap.get("girlCity") + "");
					if (resultMap.get("imageUrl") != null
							&& resultMap.get("imageUrl").toString().length() > 0) {
						ContextUtil.COMMON_PICASSO.load(
								resultMap.get("imageUrl") + "").into(
								mine_user_img);
					}
					if (resultMap.get("backgroundImageUrl") != null
							&& resultMap.get("backgroundImageUrl").toString()
									.length() > 0) {
						ContextUtil.COMMON_PICASSO.load(
								resultMap.get("backgroundImageUrl") + "").into(
								mine_bg_img);
					}
					Drawable sexImg;
					Resources res = getResources();
					if ((resultMap.get("gender") + "").equals("1")) {
						sexImg = res.getDrawable(R.drawable.sex_boy);
					} else {
						sexImg = res.getDrawable(R.drawable.sex_girl);
					}
					// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
					sexImg.setBounds(0, 0, sexImg.getMinimumWidth(),
							sexImg.getMinimumHeight());
					mine_username
							.setCompoundDrawables(null, null, sexImg, null);
					mine_username.setText(resultMap.get("name") + "");
				} else {
					Toast.makeText(Mine.this,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(Mine.this, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	class FmHttpHandler extends HttpCallBackHandler {

		public FmHttpHandler(Looper looper) {
			super(looper);
		}

		public FmHttpHandler() {
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
					JSONObject map = (JSONObject) resultObj.get("data");
					Iterator<String> it = map.keys();
					while (it.hasNext()) {
						String key = it.next();
						Object resultTmp = map.get(key);
						resultMap.put(key, resultTmp);
					}
					JSONArray channels = map.getJSONArray("channels");

					fmView.setData(channels);
				} else {
					Toast.makeText(Mine.this,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(Mine.this, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	private void setFmData() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// 登陆的例子，get
				Message msg = fmHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT + "GetFM.shtml");
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();

	}

	OnClickListener btClickListener = new OnClickListener() {
		Class<?> cls = null;

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.mine_task_panel:
				cls = Task.class;
				break;
			case R.id.mine_photo_panel:
				cls = Photo.class;
				break;
			case R.id.mine_collect_panel:
				cls = Collect.class;
				break;
			case R.id.mine_att_panel:
				cls = Attention.class;
				break;
			case R.id.mine_tree_panel:
				cls = Tree.class;
				break;
			default:
				break;
			}
			if (cls != null) {
				goOther(cls);
			}
		}
	};

	private void goOther(Class<?> cls) {
		startActivity(new Intent(this, cls));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mine, menu);
		return true;
	}

}
