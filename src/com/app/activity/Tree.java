package com.app.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
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
import com.app.view.TitleView;

public class Tree extends BaseActivity {

	Context context = this;

	JSONObject curTreeData;

	private TitleView titleView;
	private ViewGroup treeList;

	TreeHttpHandler treeHttpHandler = new TreeHttpHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tree);

		setupViews();
		getTreeReq();
	}

	private void setupViews() {
		treeList = (ViewGroup) findViewById(R.id.tree_list);
		titleView = (TitleView) findViewById(R.id.title);
		
		if(titleView != null){
			titleView.setmRightBtClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Tree.this, TreeWrite.class);
					startActivity(intent);
				}
			});
		}
	}

	private void rendItems(JSONArray arr) {
		treeList.removeAllViews();

		for (int i = 0; i < arr.length(); i++) {
			try {
				LinearLayout view = (LinearLayout) BaseUtils.addMarginBottom(
						getView(arr.getJSONObject(i)), 10);

				treeList.addView(view);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	private LinearLayout getView(JSONObject obj) {
		LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.tree_item, null);

		TextView summaryView = (TextView) view
				.findViewById(R.id.tree_item_summary);
		TextView dateView = (TextView) view.findViewById(R.id.tree_item_date);
		TextView commentNumView = (TextView) view
				.findViewById(R.id.tree_item_comment_num);

		try {
			summaryView.setText(obj.getString("content"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			dateView.setText(obj.getString("createDate"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			commentNumView.setText(obj.getString("replys"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		view.setTag(obj);
		view.setOnClickListener(itemOnClick);

		return view;
	}

	OnClickListener itemOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			curTreeData = (JSONObject) v.getTag();

			Intent intent = new Intent(context, TreeInfo.class);
			Bundle b = new Bundle();
			b.putString("question", curTreeData.toString());
			intent.putExtras(b);
			context.startActivity(intent);
		}
	};

	/**
	 * 获取树洞
	 */
	private void getTreeReq() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 登陆的例子，get
				Message msg = treeHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT
								+ "GetQuestion.shtml?pageNumber=1&pageLine=15");
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
					JSONObject map = (JSONObject) resultObj.get("data");
					JSONArray questions = map.getJSONArray("questions");

					rendItems(questions);

				} else {
					Toast.makeText(Tree.this,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(Tree.this, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

}
