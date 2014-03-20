package com.app.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.app.man.R;

public class ViewPagerItemView extends FrameLayout {

	private NetImageView mAlbumImageView;

	private TextView mALbumNameTextView;

	/**
	 * ͼƬ��Bitmap.
	 */
	private Bitmap mBitmap;

	private JSONObject mObject;

	public ViewPagerItemView(Context context) {
		super(context);
		setupViews();
	}

	public ViewPagerItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupViews();
	}

	// ��ʼ��View.
	private void setupViews() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.viewpager_itemview, null);

		mAlbumImageView = (NetImageView) view.findViewById(R.id.album_imgview);
		mALbumNameTextView = (TextView) view.findViewById(R.id.album_name);

		addView(view);
		
		setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ViewGroup parent = (ViewGroup) v.getParent();
				parent.setVisibility(View.GONE);
			}
		});
	}

	public void setData(JSONObject object) {
		this.mObject = object;
		try {
			String imgUrl = object.getString("url");
			String summary = object.getString("summary");
			
			mAlbumImageView.setNetUrl(imgUrl);
			mALbumNameTextView.setText(summary);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void recycle() {
		mAlbumImageView.setImageBitmap(null);
		if ((this.mBitmap == null) || (this.mBitmap.isRecycled()))
			return;
		this.mBitmap.recycle();
		this.mBitmap = null;
	}

	public void reload() {
		try {
			String netUrl = mObject.getString("url");
			mAlbumImageView.setNetUrl(netUrl);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
