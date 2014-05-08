package com.app.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.app.common.BaseUtils;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;
import com.app.view.TitleView;

public class TreeWrite extends BaseActivity {

	// 控件
	private TitleView titleView; // 标题
	private EditText nickView; // 昵称
	private EditText contentView; // 内容控件

	TreeHttpHandler treeHttpHandler = new TreeHttpHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tree_write);

		titleView = (TitleView) findViewById(R.id.title);
		nickView = (EditText) findViewById(R.id.tree_nick);
		contentView = (EditText) findViewById(R.id.tree_content);

		titleView.setmRightBtClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				writeTreeReq();
			}
		});
	}

	private void redirect() {
//		Intent intent = new Intent(TreeWrite.this, Tree.class);
//		startActivity(intent);
		finish();
	}

	/**
	 * 发布秘密
	 */
	private void writeTreeReq() {
		if ("".equals(nickView.getText().toString())) {
			Toast.makeText(TreeWrite.this, R.string.tree_write_nick_tip,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if ("".equals(contentView.getText().toString())) {
			Toast.makeText(TreeWrite.this, R.string.tree_write_content_tip,
					Toast.LENGTH_SHORT).show();
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = treeHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(HttpRequestUtils.BASE_HTTP_CONTEXT);
				stringBuilder.append("Question.shtml?userId=");
				stringBuilder.append(BaseUtils.CUR_USER_MAP.get("id"));
				stringBuilder.append("&name=");
				try {
					stringBuilder.append(URLEncoder.encode(nickView.getText()
							.toString(), "UTF-8"));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				stringBuilder.append("&content=");
				try {
					stringBuilder.append(URLEncoder.encode(contentView
							.getText().toString(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				stringBuilder.append("&pageNumber=1&pageLine=15");
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						stringBuilder.toString());
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
	}

	class TreeHttpHandler extends HttpCallBackHandler {

		public TreeHttpHandler(Looper looper) {
			super(looper);
		}

		public TreeHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");
				if (success) {
					Toast.makeText(TreeWrite.this, R.string.tree_write_success,
							Toast.LENGTH_SHORT).show();
					redirect();
				} else {
					Toast.makeText(TreeWrite.this,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(TreeWrite.this, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

}
