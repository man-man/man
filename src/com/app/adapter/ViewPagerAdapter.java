package com.app.adapter;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.app.view.ViewPagerItemView;

/**
 * @author frankiewei
 * ����������.
 */
public class ViewPagerAdapter extends PagerAdapter {

	/**
	 * ������
	 */
	private Context mContext;
	
	/**
	 * ���Դ,������JSONARRAY
	 */
	private JSONArray mJsonArray;
	
	/**
	 * Hashmap������Ƭ��λ���Լ�ItemView.
	 */
	private HashMap<Integer, ViewPagerItemView> mHashMap;
	
	public ViewPagerAdapter(Context context,JSONArray arrays) {
		this.mContext = context;
		this.mJsonArray = arrays;
		mHashMap = new HashMap<Integer, ViewPagerItemView>();
	}
	
	//������л��գ����������һ�����ʱ�򣬻�����ڵ�ͼƬ���յ�.
	@Override
	public void destroyItem(View container, int position, Object object) {
		ViewPagerItemView itemView = (ViewPagerItemView)object;
		itemView.recycle();
	}
	
	@Override
	public void finishUpdate(View view) {

	}

	//���ﷵ������ж�����,��BaseAdapterһ��.
	@Override
	public int getCount() {
		return mJsonArray.length();
	}

	//������ǳ�ʼ��ViewPagerItemView.���ViewPagerItemView�Ѿ�����,
	//����reload��������newһ������������.
	@Override
	public Object instantiateItem(View container, int position) {	
		ViewPagerItemView itemView;
		if(mHashMap.containsKey(position)){
			itemView = mHashMap.get(position);
			itemView.reload();
		}else{
			itemView = new ViewPagerItemView(mContext);
			try {
				JSONObject dataObj = (JSONObject) mJsonArray.get(position);
				itemView.setData(dataObj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mHashMap.put(position, itemView);
			((ViewPager) container).addView(itemView);
		}
		
		return itemView;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View view) {

	}
}
