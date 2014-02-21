package com.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.app.adapter.WomanAdapter;
import com.app.man.R;
import com.app.model.WomanItemModel;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.widget.ListView;

public class Woman extends Activity {

	private ListView womanList;
	private WomanAdapter womanAdapter;
	private List<Map<String, WomanItemModel>> models;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.woman);

		womanList = (ListView) findViewById(R.id.woman_list);
		womanAdapter = new WomanAdapter(this);
		womanList.setAdapter(womanAdapter);

		initWomanList();
	}

	private void initWomanList() {
		models = new ArrayList<Map<String,WomanItemModel>>();
		
		Map<String, WomanItemModel> map = null;
		int len = 15;
		
		for (int i = 0; i < len; i=i+2) {
			map = new HashMap<String, WomanItemModel>();
			
			WomanItemModel model = new WomanItemModel();
			model.setImg(R.drawable.default_img);
			model.setName("装女郎" + i);
			model.setVote(100 * i);
			model.setRank(i + 1);
			map.put("left", model);
			
			if(i + 1 < len){
				WomanItemModel model2 = new WomanItemModel();
				model2.setImg(R.drawable.default_img);
				model2.setName("装女郎" + (i+1));
				model2.setVote(100 * (i+1));
				model2.setRank(i + 2);
				map.put("right", model2);
			}
			
			models.add(map);
		}

		womanAdapter.setDatas(models);
		womanAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.woman, menu);
		return true;
	}

}
