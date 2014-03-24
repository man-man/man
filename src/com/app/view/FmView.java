package com.app.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.common.BaseUtils;
import com.app.man.R;

public class FmView {

	private JSONArray dataList; // 节目单数据
	private JSONObject curDataObj; // 当前播放节目数据
	private JSONArray otherDataList; // 其它节目 （除当前节目外）

	private ViewGroup curFmContainer; // 当前正在播放节目容器
	private ViewGroup fmListContainer; // 节目单容器
	private ViewGroup curFmPanel;

	private NetImageView curFmIcon;
	private TextView curFmTitle;
	private NetImageView curFmState;

	private Context context;

	public FmView(ViewGroup curFmContainer, ViewGroup fmListContainer) {
		this.curFmContainer = curFmContainer;
		this.fmListContainer = fmListContainer;

		context = this.curFmContainer.getContext();

		setupViews();
	}

	/**
	 * 设置数据
	 * 
	 * @param datas
	 */
	public void setData(JSONArray datas) {
		if (datas == null || datas.length() == 0) {
			return;
		}
		dataList = datas;
		try {
			curDataObj = datas.getJSONObject(0);
			//String url = curDataObj.getString("url");
			//BaseUtils.CUR_PLAY_MP3_URL = url;
			resetOtherData();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		rendItems();
	}

	/**
	 * 重置其它节目数据
	 * 
	 * @param curDataObj
	 * @param dataList
	 */
	private void resetOtherData() {
		otherDataList = new JSONArray();
		for (int i = 0; i < dataList.length(); i++) {
			JSONObject dataItem = null;
			try {
				dataItem = dataList.getJSONObject(i);
				if (curDataObj.getString("url").equals(
						dataItem.getString("url"))) {
					System.out.println("url----------"
							+ curDataObj.getString("url"));
					continue;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}
			otherDataList.put(dataItem);
		}
	}

	private void setupViews() {
		curFmPanel = (ViewGroup) curFmContainer
				.findViewById(R.id.cur_mine_fm_panel);
		curFmIcon = (NetImageView) curFmContainer
				.findViewById(R.id.cur_mine_fm_icon);
		curFmTitle = (TextView) curFmContainer
				.findViewById(R.id.cur_mine_fm_title);
		curFmState = (NetImageView) curFmContainer
				.findViewById(R.id.cur_mine_fm_state);

		curFmPanel.setOnClickListener(curPanelOnClick);

	}

	/**
	 * 渲染节目
	 */
	private void rendItems() {
		// 渲染当前播放节目
		try {
			curFmIcon.setNetUrl(curDataObj.getString("iconUrl"));
			curFmTitle.setText(curDataObj.getString("title"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		fmListContainer.removeAllViews();
		// 渲染未播放节目
		for (int i = 0; i < otherDataList.length(); i++) {
			try {
				JSONObject itemData = otherDataList.getJSONObject(i);

				View view = getOtherItem(itemData);
				view.setTag(itemData);
				fmListContainer.addView(view);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 得到other节目view
	 * 
	 * @param itemData
	 * @return
	 */
	private View getOtherItem(JSONObject itemData) {
		View view = LayoutInflater.from(context)
				.inflate(R.layout.fm_item, null);

		try {
			((NetImageView) view.findViewById(R.id.fm_item_icon))
					.setNetUrl(itemData.getString("iconUrl"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			((TextView) view.findViewById(R.id.fm_item_title)).setText(itemData
					.getString("title"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		view.setOnClickListener(fmItemOnClick);
		return view;
	}

	/**
	 * 当前节目点击事件侦听
	 */
	OnClickListener curPanelOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int showType = View.VISIBLE;

			switch (fmListContainer.getVisibility()) {
			case View.VISIBLE:
				showType = View.GONE;
				break;
			case View.GONE:
				showType = View.VISIBLE;
				break;
			}
			fmListContainer.setVisibility(showType);
		}
	};

	OnClickListener fmItemOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			curDataObj = (JSONObject) v.getTag();
			resetOtherData();
			rendItems();
			fmListContainer.setVisibility(View.GONE);
		}
	};

}
