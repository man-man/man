package com.app.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;

import com.app.man.R;
import com.app.view.FmView;

/**
 * 我的
 * 
 * @author 王灵
 * 
 */
public class Mine extends BaseActivity {

	private ViewGroup taskBt; // 我的任务
	private ViewGroup photosBt; // 我的相册
	private ViewGroup collectBt; // 我的收藏
	private ViewGroup attBt; // 我关注的人
	private ViewGroup treeBt; // 树洞

	private ViewGroup curFmContainer; // 当前正在播放节目容器
	private ViewGroup fmListContainer; // 节目单容器
	private FmView fmView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mine2);

		setupViews();
	}

	private void setupViews() {
		taskBt = (ViewGroup) findViewById(R.id.mine_task_panel);
		photosBt = (ViewGroup) findViewById(R.id.mine_photo_panel);
		collectBt = (ViewGroup) findViewById(R.id.mine_collect_panel);
		attBt = (ViewGroup) findViewById(R.id.mine_att_panel);
		treeBt = (ViewGroup) findViewById(R.id.mine_tree_panel);

		curFmContainer = (ViewGroup) findViewById(R.id.mine_group_3);
		fmListContainer = (ViewGroup) findViewById(R.id.mine_group_4);

		taskBt.setOnClickListener(btClickListener);
		photosBt.setOnClickListener(btClickListener);
		collectBt.setOnClickListener(btClickListener);
		attBt.setOnClickListener(btClickListener);
		treeBt.setOnClickListener(btClickListener);

		fmView = new FmView(curFmContainer, fmListContainer); // 初始化电台
		
		setFmData();
	}

	private void setFmData() {
		JSONArray datas = new JSONArray();

		for (int i = 0; i < 6; i++) {
			JSONObject obj = new JSONObject();

			try {
				obj.put("title", "任逍遥" + i);
				obj.put("iconUrl",
						"http://b248.photo.store.qq.com/psb?/V11mc5aH1jEnan/D8z2lWj6NUccCqsJwZxjHk7Fnu2Af8d498Yx6txKoao!/b/dAMN3pMkDQAA&amp;bo=QABIAAAAAAADAC0!");
				obj.put("url", "aaa" + i);
				
				datas.put(obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		fmView.setData(datas);
	}

	OnClickListener btClickListener = new OnClickListener() {
		Class<?> cls = null;

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.mine_task_panel:
				cls = Task.class;
				break;
			case R.id.mine_photo_panel:
				break;
			case R.id.mine_collect_panel:
				break;
			case R.id.mine_att_panel:
				break;
			case R.id.mine_tree_panel:
				break;
			default:
				break;
			}
			if (cls != null) {
				goOther(cls);
			}
		}
	};

	private void goOther(Class<?> cls) {
		startActivity(new Intent(this, cls));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mine, menu);
		return true;
	}

}
