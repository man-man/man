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

//	final String userName = "18611128524";
//	final String pwd = "123456";
	
	final String userName = "daren";
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

			// /**
			// * 测试图片上传的post的例子
			// */
			// Bundle bundle = new Bundle();
			// JSONObjectSerializalble object = new JSONObjectSerializalble();
			// JSONArraySerializalble arraySerializalble = new
			// JSONArraySerializalble();
			// try {
			// JSONObject jsonObject = new JSONObject();
			// jsonObject
			// .put("image",
			// "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");
			// jsonObject.put("summary", "23");
			// arraySerializalble.put(jsonObject);
			// JSONObject jsonObject2 = new JSONObject();
			// jsonObject2
			// .put("image",
			// "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");
			// jsonObject2.put("summary", "123");
			// arraySerializalble.put(jsonObject2);
			// object.put("title", "android test");
			// object.put("userId", 9);
			// object.put("albumId", 3);
			// object.put("summary", "summary");
			// object.put("images", arraySerializalble);
			// } catch (JSONException e) {
			// e.printStackTrace();
			// }
			// bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
			// HttpRequestUtils.BASE_HTTP_CONTEXT + "UploadImage.shtml");
			// bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, true);
			// bundle.putSerializable(HttpRequestUtils.BUNDLE_KEY_PARAMS,
			// object);
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
									+ "&platform=1");
					bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
					msg.setData(bundle);
					msg.sendToTarget();
				}
			}).start();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

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
					Intent intent = new Intent(Login.this, Woman.class);
					Login.this.startActivity(intent);
					Login.this.finish();
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
