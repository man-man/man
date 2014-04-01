package com.app.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.app.common.BaseUtils;
import com.app.common.BubbleUtil;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;
import com.app.view.SexView;

public class CreateRole extends Activity {
	Context context = this;

	// 控件
	private EditText roleNameView;
	private SexView roleSexView;
	private Button submitBtn;

	private SubmitHttpHandler submitHttpHandle = new SubmitHttpHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.create_role);
		setupViews();
	}

	private void setupViews() {
		roleNameView = (EditText) findViewById(R.id.role_name_input);
		roleSexView = (SexView) findViewById(R.id.role_sex_sel);
		submitBtn = (Button) findViewById(R.id.role_submit);

		submitBtn.setOnClickListener(submitOnClick);
	}

	OnClickListener submitOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			reqSend();
		}
	};

	/**
	 * 转换到其它activity
	 */
	private void redirect() {
		startActivity(new Intent(this, Woman.class));
		finish();
	}

	/**
	 * 发送请求(发表评论)
	 */
	private void reqSend() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = submitHttpHandle.obtainMessage();
				Bundle bundle = new Bundle();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(HttpRequestUtils.BASE_HTTP_CONTEXT);
				stringBuilder.append("SaveUser.shtml?userId=");
				stringBuilder.append(BaseUtils.CUR_USER_MAP.get("id"));
				stringBuilder.append("&gender=");
				stringBuilder.append(roleSexView.getCurSex());
				stringBuilder.append("&name=");
				try {
					stringBuilder.append(URLEncoder.encode(roleNameView
							.getText().toString(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						stringBuilder.toString());
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
	}

	class SubmitHttpHandler extends HttpCallBackHandler {

		public SubmitHttpHandler(Looper looper) {
			super(looper);
		}

		public SubmitHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");
				if (success) {
					BubbleUtil.alertMsg(context, R.string.role_create_success);
					redirect();
				} else {
					BubbleUtil.alertMsg(context,
							resultObj.getString("errorMessage"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				BubbleUtil.alertMsg(context, R.string.base_response_error);
			}
		}

	}

}
