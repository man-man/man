package com.app.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.man.R;
import com.app.service.Mp3Service;

public class FmView {

	private JSONArray dataList; // 节目单数据
	private JSONObject curDataObj; // 当前播放节目数据
	private JSONArray otherDataList; // 其它节目 （除当前节目外）

	private ViewGroup curFmContainer; // 当前正在播放节目容器
	private ViewGroup otherFmContainer; // 其它节目容器
	private ViewGroup fmList; // 节目单容器
	private ViewGroup curFmPanel;

	private NetImageView curFmIcon;
	private TextView curFmTitle;
	private ImageView curFmState;

	private Context context;

	public FmView(Context context, ViewGroup curFmContainer,
			ViewGroup fmListContainer) {
		this.curFmContainer = curFmContainer;
		this.otherFmContainer = fmListContainer;
		this.context = context;

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
		curFmState = (ImageView) curFmContainer
				.findViewById(R.id.cur_mine_fm_state);
		curFmState.setTag(false); // 未播放

		fmList = (ViewGroup) otherFmContainer.findViewById(R.id.mine_fm_list);

		curFmPanel.setOnClickListener(curPanelOnClick);
		curFmState.setOnClickListener(fmStateOnClick);
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

		fmList.removeAllViews();
		// 渲染未播放节目
		for (int i = 0; i < otherDataList.length(); i++) {
			try {
				JSONObject itemData = otherDataList.getJSONObject(i);

				View view = getOtherItem(itemData);
				view.setTag(itemData);
				fmList.addView(view);
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
	 * 控制mp3
	 * 
	 * @param state
	 */
	private void controlMp3(String state) {
		Intent intent = new Intent(context, Mp3Service.class);

		context.stopService(intent);

		if (Mp3Service.STATE_STOP.equals(state)) {
			switchPlayBt(false);
		} else if (Mp3Service.STATE_PLAY.equals(state)) {
			intent.putExtra("url", "");
			intent.putExtra("MSG", state);
			context.startService(intent); // 启动服务
			switchPlayBt(true);
		}
	}

	private void switchPlayBt(boolean isPlay) {
		if (isPlay) {
			curFmState.setImageResource(R.drawable.mine_redio_stop);
			curFmState.setTag(true);
		} else {
			curFmState.setImageResource(R.drawable.mine_redio_play);
			curFmState.setTag(false);
		}
	}

	/**
	 * 当前节目点击事件侦听
	 */
	OnClickListener curPanelOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int showType = View.VISIBLE;

			switch (otherFmContainer.getVisibility()) {
			case View.VISIBLE:
				showType = View.GONE;
				break;
			case View.GONE:
				showType = View.VISIBLE;
				break;
			}
			otherFmContainer.setVisibility(showType);
		}
	};

	/**
	 * 节目单中节目点击
	 */
	OnClickListener fmItemOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			curDataObj = (JSONObject) v.getTag();
			resetOtherData();
			rendItems();
			otherFmContainer.setVisibility(View.GONE);

			controlMp3(Mp3Service.STATE_PLAY);
		}
	};

	/**
	 * 播放暂停按钮
	 */
	OnClickListener fmStateOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			boolean isPlaying = (Boolean) v.getTag();

			switchPlayBt(!isPlaying);
			if (isPlaying) {
				controlMp3(Mp3Service.STATE_STOP);
			} else {
				controlMp3(Mp3Service.STATE_PLAY);
			}

		}
	};

	private void dispatchTouchEvent() {

	}

	private void onInterceptTouchEvent() {

	}

}
