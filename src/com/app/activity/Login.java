package com.app.activity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.common.BaseUtils;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;

/**
 * 登陆
 * 
 * @author 王灵
 * 
 */
public class Login extends BaseActivity {

	// final String userName = "13691141126";
	// final String pwd = "123456";

	final String userName = "darens";
	final String pwd = "123456";

	private Button loginBt = null;
	private EditText passport;
	private EditText password;
	private MyListViewHttpHandler myListViewHttpHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		passport = (EditText) findViewById(R.id.login_passport);
		password = (EditText) findViewById(R.id.login_password);

		passport.setText(userName);
		password.setText(pwd);

		loginBt = (Button) findViewById(R.id.login_bt);
		loginBt.setOnClickListener(loginOnclickListener);

	}

	OnClickListener loginOnclickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			myListViewHttpHandler = new MyListViewHttpHandler();
			new Thread(new Runnable() {

				@Override
				public void run() {
					// 登陆的例子，get
					Message msg = myListViewHttpHandler.obtainMessage();
					Bundle bundle = new Bundle();
					bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
							HttpRequestUtils.BASE_HTTP_CONTEXT + "Login.shtml?"
									+ "phoneNumber="
									+ passport.getText().toString()
									+ "&password="
									+ password.getText().toString()
									+ "&platform=0");
					bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
					msg.setData(bundle);
					msg.sendToTarget();
				}
			}).start();
		}
	};

	class MyListViewHttpHandler extends HttpCallBackHandler {

		public MyListViewHttpHandler(Looper looper) {
			super(looper);
		}

		public MyListViewHttpHandler() {
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
					BaseUtils.CUR_USER_MAP = resultMap;

					String roleName = resultMap.get("name").toString();

					if ("".equals(roleName)) {
						Intent intent = new Intent(Login.this, CreateRole.class);
						Login.this.startActivity(intent);
					} else {
						Intent intent = new Intent(Login.this, Woman.class);
						Login.this.startActivity(intent);
						Login.this.finish();
					}
				} else {
					Toast.makeText(Login.this,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(Login.this, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

}
