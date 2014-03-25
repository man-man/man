package com.app.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.common.BaseUtils;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;
import com.app.util.DensityUtil;

public class Comment extends Activity {

	private ViewGroup commentList;
	private String articleId; // 文章id

	private EditText inputView; // 评论发布输入框
	private TextView submitButton; // 发表按钮

	private HttpHandler httpHandler = new HttpHandler();
	private SubmitHttpHandler submitHttpHandle = new SubmitHttpHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.comment);

		articleId = getIntent().getStringExtra("articleId");

		commentList = (ViewGroup) findViewById(R.id.comment_list);
		inputView = (EditText) findViewById(R.id.comment_input);
		submitButton = (TextView) findViewById(R.id.comment_submit);

		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				reqSendSubmitMsg();
			}
		});
		reqSend();

	}

	/**
	 * 发送请求(评论列表)
	 */
	private void reqSend() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = httpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT
								+ "GetComment.shtml?articleId=" + articleId
								// + articleId
								+ "&pageNumber=1&pageLine=15");
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
	}

	/**
	 * 发送请求(发表评论)
	 */
	private void reqSendSubmitMsg() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = submitHttpHandle.obtainMessage();
				Bundle bundle = new Bundle();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(HttpRequestUtils.BASE_HTTP_CONTEXT);
				stringBuilder.append("CommentArticle.shtml?userId="
						+ BaseUtils.CUR_USER_MAP.get("id") + "&articleId="
						+ articleId);
				stringBuilder.append("&content=");
				try {
					stringBuilder.append(URLEncoder.encode(inputView.getText()
							.toString(), "UTF-8"));
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

	/**
	 * 渲染数据
	 * 
	 * @throws JSONException
	 */
	private void rendItems(JSONArray datas) {
		commentList.removeAllViews();

		for (int i = 0; i < datas.length(); i++) {

			JSONObject obj;
			try {
				obj = datas.getJSONObject(i);
				LinearLayout item = getView(obj);
				commentList.addView(item);

				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) item
						.getLayoutParams();
				params.setMargins(0, 0, 0, DensityUtil.dip2px(10));
				item.setLayoutParams(params);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * 返回每一条view
	 */
	private LinearLayout getView(JSONObject obj) {
		LinearLayout convertView = (LinearLayout) LayoutInflater.from(this)
				.inflate(R.layout.comment_item, null);

		TextView content = (TextView) convertView
				.findViewById(R.id.comment_item_content);
		TextView author = (TextView) convertView
				.findViewById(R.id.comment_item_author);
		TextView date = (TextView) convertView
				.findViewById(R.id.comment_item_date);

		try {
			content.setText(obj.getString("content"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			author.setText(obj.getString("userName"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			date.setText(obj.getString("crateDate"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return convertView;
	}

	class HttpHandler extends HttpCallBackHandler {

		public HttpHandler(Looper looper) {
			super(looper);
		}

		public HttpHandler() {
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
					JSONArray comments = data.getJSONArray("comments");

					rendItems(comments);
				} else {
					Toast.makeText(Comment.this,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(Comment.this, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

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
					Toast.makeText(Comment.this, "评论发表成功", Toast.LENGTH_SHORT)
							.show();
					inputView.setText("");
					reqSend();
				} else {
					Toast.makeText(Comment.this,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(Comment.this, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.comment, menu);
		return true;
	}

}
