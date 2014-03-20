package com.app.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.app.adapter.ViewPagerAdapter;
import com.app.man.R;

public class ViewPagerView extends ViewPager {

	private ViewPagerAdapter mViewPagerAdapter;

	private JSONArray mJsonArray;

	private int curIndex;

	public ViewPagerView(Context context) {
		super(context);
	}

	public ViewPagerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setData(int index, JSONArray arr) {
		this.mJsonArray = arr;
		this.curIndex = index;
		setupViews();
	}

	private void setupViews() {
		if (mJsonArray == null) {
			return;
		}

		// mJsonArray = new JSONArray();
		// for (int i = 0; i < ALBUM_COUNT; i++) {
		// JSONObject object = new JSONObject();
		// try {
		// object.put("resid", ALBUM_RES[i % ALBUM_RES.length]);
		// object.put("name", "Album " + i);
		// mJsonArray.put(object);
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		//
		// }
		mViewPagerAdapter = new ViewPagerAdapter(getContext(), mJsonArray,
				curIndex);
		this.setAdapter(mViewPagerAdapter);
	}

}
