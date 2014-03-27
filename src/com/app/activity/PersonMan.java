package com.app.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.app.activity.Mine.UserInfoHttpHandler;
import com.app.common.BaseUtils;
import com.app.common.BubbleUtil;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;
import com.app.man.R.layout;
import com.app.man.R.menu;
import com.app.view.ManListView;
import com.app.view.MenuView;
import com.app.view.PersonHeadView;
import com.app.view.ViewPagerView;

import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class PersonMan extends Activity {

	private String personId; // 当前person id

	// 控件
	private AbsoluteLayout absContainer; // 绝对布局容器
	private ScrollView parentScroll; // 父scroll
	private MenuView menuView; // 更多菜单
	private ManListView manListView; // 文章列表
	private PersonHeadView personHeadView; // 用戶信息
	private ViewPagerView pagerView; // 图片切换组件

	UserInfoHttpHandler userInfoHttpHandler = new UserInfoHttpHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.person_man);

		personId = getIntent().getStringExtra("userId");

		personHeadView = (PersonHeadView) findViewById(R.id.person_man_head);
		absContainer = (AbsoluteLayout) findViewById(R.id.person_man_abs_container);
		parentScroll = (ScrollView) findViewById(R.id.person_man_list_scroll);
		menuView = (MenuView) findViewById(R.id.person_man_menu_vew);
		manListView = (ManListView) findViewById(R.id.person_man_list);
		pagerView = (ViewPagerView) findViewById(R.id.person_man_pager_view);

		sendReq();
	}

	private void sendReq() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = userInfoHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT
								+ "GetUserInfo.shtml?" + "userId=" + personId);
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
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
				if (success) {
					JSONObject data = (JSONObject) resultObj.get("data");
					personHeadView.setData(data);

					JSONArray articles = data.getJSONArray("articles");
					manListView.setupViews(absContainer, parentScroll,
							menuView, pagerView, false).setData(articles);

				} else {
					BubbleUtil.alertMsg(PersonMan.this,
							resultObj.getString("errorMessage"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				BubbleUtil.alertMsg(PersonMan.this,
						R.string.base_response_error);
			}
		}

	}

}
