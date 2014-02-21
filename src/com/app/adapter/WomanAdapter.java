package com.app.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.man.R;
import com.app.model.WomanItemModel;
import com.app.util.DensityUtil;

public class WomanAdapter extends BaseAdapter {

	private List<Map<String, WomanItemModel>> womanDatas;
	private LayoutInflater inflater;

	public WomanAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}

	public void setDatas(List<Map<String, WomanItemModel>> list) {
		womanDatas = list;
	}

	@Override
	public int getCount() {
		return womanDatas == null ? 0 : womanDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return womanDatas == null ? null : womanDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.woman_item, null);

			holder = new ViewHolder();
			holder.womanItem = (RelativeLayout)convertView.findViewById(R.id.woman_item_1);
			holder.headImg = (ImageView) convertView
					.findViewById(R.id.woman_head_img);
			holder.name = (TextView) convertView.findViewById(R.id.woman_name);
			holder.vote = (TextView) convertView.findViewById(R.id.woman_vote);
			holder.rank = (TextView) convertView.findViewById(R.id.woman_pos);
			
			holder.womamItem2 = (RelativeLayout)convertView.findViewById(R.id.woman_item_2);
			holder.headImg2 = (ImageView) convertView
					.findViewById(R.id.woman_head_img2);
			holder.name2 = (TextView) convertView
					.findViewById(R.id.woman_name2);
			holder.vote2 = (TextView) convertView
					.findViewById(R.id.woman_vote2);
			holder.rank2 = (TextView) convertView.findViewById(R.id.woman_pos2);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		HashMap<String, WomanItemModel> map = (HashMap<String, WomanItemModel>) getItem(position);
		WomanItemModel left = map.get("left");
		WomanItemModel right = map.get("right");
		
		formatImg(holder.headImg, holder.womanItem).setImageResource(left.getImg());
		holder.name.setText(left.getName());
		holder.vote.setText(left.getVote() + "票");
		holder.rank.setText(left.getRank() + "");
		
		if(right == null){
			holder.womamItem2.setVisibility(View.INVISIBLE);
		}else{
			holder.womamItem2.setVisibility(View.VISIBLE);
			
			formatImg(holder.headImg2, holder.womamItem2).setImageResource(right.getImg());
			holder.name2.setText(right.getName());
			holder.vote2.setText(right.getVote() + "票");
			holder.rank2.setText(right.getRank() + "");
		}
		
		return convertView;
	}
	
	/**
	 * 计算女人装头像图片大小
	 * @param imgView
	 * @param parent
	 * @return
	 */
	private ImageView formatImg(ImageView imgView, RelativeLayout parent){
		int size = (DensityUtil.SCREEN_WIDTH - DensityUtil.dip2px(5*2) - DensityUtil.dip2px(6)) / 2;
		
		System.out.println("---------size:" + size);
		imgView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
		return imgView;
	}

	private static class ViewHolder {
		public RelativeLayout womanItem;
		public ImageView headImg;
		public TextView name;
		public TextView vote;
		public TextView rank;
		
		public RelativeLayout womamItem2;
		public ImageView headImg2;
		public TextView name2;
		public TextView vote2;
		public TextView rank2;
	}

}
