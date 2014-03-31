package com.app.activity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.app.common.BaseUtils;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;

/**
 * 任务
 * 
 * @author XH
 * 
 */
public class Task extends BaseActivity {

	private ViewGroup taskList;

	TaskHttpHandler taskHttpHandler = new TaskHttpHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.task);

		setupViews();

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = taskHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT
								+ "GetQuest.shtml?userId="
								+ BaseUtils.CUR_USER_MAP.get("userId"));
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
	}

	private void setupViews() {
		taskList = (ViewGroup) findViewById(R.id.task_list);
	}

	/**
	 * 渲染列表
	 * 
	 * @param arr
	 */
	private void rendItems(JSONArray arr) {
		taskList.removeAllViews();

		for (int i = 0; i < arr.length(); i++) {
			try {
				taskList.addView(getView(arr.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 得到一个任务 view
	 * 
	 * @param obj
	 * @return
	 */
	private View getView(JSONObject obj) {
		View view = LayoutInflater.from(this).inflate(R.layout.task_item, null);
		TextView title = (TextView) view.findViewById(R.id.task_item_title);
		TextView summary = (TextView) view.findViewById(R.id.task_item_des);
		TextView step = (TextView) view.findViewById(R.id.task_item_num);

		try {
			title.setText(obj.getString("title"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			summary.setText(obj.getString("summary"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			step.setText(obj.getString("currentStep") + "/"
					+ obj.getString("needStep"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return view;
	}

	class TaskHttpHandler extends HttpCallBackHandler {

		public TaskHttpHandler(Looper looper) {
			super(looper);
		}

		public TaskHttpHandler() {
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
					JSONArray quests = map.getJSONArray("quests");

					rendItems(quests);
					// fmView.setData(channels);
				} else {
					Toast.makeText(Task.this,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(Task.this, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task, menu);
		return true;
	}

}
