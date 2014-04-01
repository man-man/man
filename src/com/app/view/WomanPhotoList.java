package com.app.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.activity.Photo;
import com.app.activity.PhotoAlbum;
import com.app.man.R;
import com.app.util.DensityUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

@SuppressLint("NewApi")
public class WomanPhotoList extends LinearLayout {

	Context context;

	private JSONArray datas;

	private ViewGroup leftCol;
	private ViewGroup rightCol;

	private int colWid;

	public WomanPhotoList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		setupViews();
	}

	public WomanPhotoList(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setupViews();
	}

	public WomanPhotoList(Context context) {
		super(context);
		this.context = context;
		setupViews();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			colWid = leftCol.getWidth();
		}
	}

	private void setupViews() {
		LayoutInflater.from(context).inflate(R.layout.woman_photo_list, this);
		leftCol = (ViewGroup) findViewById(R.id.woman_photo_list_col_left);
		rightCol = (ViewGroup) findViewById(R.id.woman_photo_list_col_right);
	}

	private void rendItems() {
		leftCol.removeAllViews();
		rightCol.removeAllViews();

		for (int i = 0; i < datas.length(); i++) {
			JSONObject dataObj;
			try {
				dataObj = datas.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}
			if (i % 2 == 0) {
				leftCol.addView(getView(dataObj));
			} else {
				rightCol.addView(getView(dataObj));
			}
		}
	}

	private NetImageView getView(JSONObject obj) {
		NetImageView img = new NetImageView(context);
		String imgUrl = null;

		try {
			JSONArray imgs = obj.getJSONArray("images");
			imgUrl = imgs.getJSONObject(0).getString("imageUrl");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 0, DensityUtil.dip2px(10));
		img.setLayoutParams(params);

		img.setScaleType(ScaleType.CENTER_CROP);
		img.setImageResource(R.drawable.default_img);
		img.setRect(true);
		img.setNetUrl(imgUrl);
		
		img.setTag(obj);
		img.setOnClickListener(imgOnClick);
		return img;

	}
	
	OnClickListener imgOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			JSONObject curAlbumData = (JSONObject) v.getTag();
			
			Intent intent = new Intent(context, Photo.class);
			Bundle b = new Bundle();
			try {
				b.putString("albumId", curAlbumData.getString("id"));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			try {
				b.putString("imgs", curAlbumData.getJSONArray("images")
						.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			b.putString("type", "other");
			intent.putExtras(b);
			context.startActivity(intent);
		}
	};

	public JSONArray getDatas() {
		return datas;
	}

	public void setDatas(JSONArray datas) {
		this.datas = datas;
		if (datas != null) {
			rendItems();
		}
	}

}
