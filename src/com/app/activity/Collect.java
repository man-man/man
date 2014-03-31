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
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.app.common.BaseUtils;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;
import com.app.view.ManListView;
import com.app.view.MenuView;
import com.app.view.ViewPagerView;

/**
 * 我的收藏
 * 
 * @author XH
 * 
 */
public class Collect extends BaseActivity {

	private AbsoluteLayout absContainer; // 绝对布局容器
	private ScrollView parentScroll; // 父scroll
	private ManListView manListView; // 文章列表容器
	private ViewPagerView pagerView; // 图片切换组件
	private MenuView menuView; // 更多菜单

	CollectHttpHandler CollectHttpHandler = new CollectHttpHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.collect);

		setupViews();
		getCollectReq();
	}

	private void setupViews() {
		absContainer = (AbsoluteLayout) findViewById(R.id.collect_abs_container);
		parentScroll = (ScrollView) findViewById(R.id.collect_list_scroll);
		manListView = (ManListView) findViewById(R.id.collect_list);
		pagerView = (ViewPagerView) findViewById(R.id.collect_pager_view);
		menuView = (MenuView) findViewById(R.id.collect_menu_vew);

		absContainer.setOnTouchListener(absCOnTouch);
	}

	OnTouchListener absCOnTouch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			menuView.hide();
			return false;
		}
	};

	/**
	 * 获取收藏列表
	 */
	private void getCollectReq() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 登陆的例子，get
				Message msg = CollectHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT
								+ "GetFavorite.shtml?userId="
								+ BaseUtils.CUR_USER_MAP.get("userId")
								+ "&pageNumber=1&pageLine=15");
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
	}

	class CollectHttpHandler extends HttpCallBackHandler {

		public CollectHttpHandler(Looper looper) {
			super(looper);
		}

		public CollectHttpHandler() {
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
					JSONArray articles = map.getJSONArray("articles");

					manListView.setupViews(absContainer, parentScroll,
							menuView, pagerView, true).setData(articles);
				} else {
					Toast.makeText(Collect.this,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(Collect.this, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}
}
