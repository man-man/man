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
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;

import com.app.common.BaseUtils;
import com.app.common.BubbleUtil;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;
import com.app.view.TitleView;

public class WriteArticle extends Activity {

	private Context context = this;

	private TitleView titleView; // 标题view
	private EditText articleTitleView; // 文章标题输入框
	private EditText articleView; // 文章内容输入框

	private SubmitHttpHandler submitHttpHandle = new SubmitHttpHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.write_article);

		titleView = (TitleView) findViewById(R.id.write_article_title);
		articleView = (EditText) findViewById(R.id.write_article_input);
		articleTitleView = (EditText) findViewById(R.id.write_article_title_input);

		titleView.setmRightBtClickListener(submitOnClick);

	}

	OnClickListener submitOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			BubbleUtil.alertMsg(context, "点击了发布");
			reqSend();
		}
	};

	/**
	 * 转换到其它activity
	 */
	private void redirect() {
		startActivity(new Intent(this, Man.class));
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
				stringBuilder.append("WriteArticle.shtml?userId="
						+ BaseUtils.CUR_USER_MAP.get("id"));
				stringBuilder.append("&title=");
				try {
					stringBuilder.append(URLEncoder.encode(articleTitleView
							.getText().toString(), "UTF-8"));
					stringBuilder.append("&summary=");
					stringBuilder.append(URLEncoder.encode(articleView
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
					BubbleUtil.alertMsg(context,
							R.string.write_article_submite_success);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.write_article, menu);
		return true;
	}

}
