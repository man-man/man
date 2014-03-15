package com.app.adapter;

import java.util.List;

import com.app.common.HandlerUtils;
import com.app.man.R;
import com.app.model.ManItemImgsModel;
import com.app.model.ManItemModel;
import com.app.util.DensityUtil;
import com.app.view.InnerScrollView;
import com.app.view.NetImageView;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class ManListAdapter extends BaseAdapter {

	private List<ManItemModel> articleList;
	private LayoutInflater mInflater;

	private Context context;
	private ListView listView;

	public ManListAdapter(Context context, ListView listView) {
		this.context = context;
		this.listView = listView;
		mInflater = LayoutInflater.from(context);
	}

	public void setDatas(List<ManItemModel> datas) {
		this.articleList = datas;
	}

	@Override
	public int getCount() {
		return articleList == null ? 0 : articleList.size();
	}

	@Override
	public ManItemModel getItem(int position) {
		return articleList == null ? null : articleList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.man_item, null);

			holder = new ViewHolder();
			holder.authorName = (TextView) convertView
					.findViewById(R.id.man_item_name);
			holder.authorImg = (NetImageView) convertView
					.findViewById(R.id.man_item_head);
			holder.date = (TextView) convertView
					.findViewById(R.id.man_item_date);
			holder.imagesContainer = (LinearLayout) convertView
					.findViewById(R.id.man_item_imgs);
			holder.summary = (TextView) convertView
					.findViewById(R.id.man_item_summary);
			holder.innerScrollView = (InnerScrollView) convertView
					.findViewById(R.id.man_item_imgs_scroll);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ManItemModel model = getItem(position);
		if (model == null) {
			return convertView;
		}
		holder.authorName.setText(model.getAuthorName());
		holder.authorImg.setNetUrl(model.getAuthorImg());
		holder.date.setText(model.getDate());

		holder.innerScrollView.setParentScrollView(listView);

		List<ManItemImgsModel> imgs = model.getImages();
		if (holder.imagesContainer.getChildCount() == 0) {
			for (int i = 0; i < imgs.size(); i++) {
				Looper looper = HandlerUtils.getLooper();
				ManItemImgsModel imgModel = imgs.get(i);

				NetImageView imgView = new NetImageView(this.context);

				LayoutParams params = new LayoutParams(DensityUtil.dip2px(90),
						DensityUtil.dip2px(90));
				params.setMargins(0, 0, DensityUtil.dip2px(10), 0);
				imgView.setLayoutParams(params);

				imgView.setScaleType(ScaleType.CENTER_CROP);
				imgView.setCornerRadius(3);
				imgView.setNetUrl(imgModel.getUrl());
				holder.imagesContainer.addView(imgView);
			}
		}

		holder.summary.setText(model.getSummary());

		return convertView;
	}

	private static class ViewHolder {
		public TextView authorName;
		public NetImageView authorImg;
		public TextView date;
		public LinearLayout imagesContainer;
		public TextView summary;
		public InnerScrollView innerScrollView;

	}
}
