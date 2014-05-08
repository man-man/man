package com.app.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.activity.PersonMan;
import com.app.man.R;
import com.app.util.DensityUtil;

@SuppressLint("NewApi")
public class ManListView extends LinearLayout {

	Context context;

	private boolean isMan = false; // 是否是男人装

	private JSONArray articles; // 文章数据

	private AbsoluteLayout absContainer; // 绝对布局容器
	private ScrollView parentScroll; // 父scroll
	private MenuView menuView; // 更多菜单
	private ViewPagerView pagerView; // 图片切换组件

	public ManListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public ManListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public ManListView(Context context) {
		super(context);
		this.context = context;
	}

	/**
	 * 设置该组件依赖控件
	 * 
	 * @param absContainer
	 * @param parentScroll
	 * @param menuView
	 * @param pagerView
	 * @return
	 */
	public ManListView setupViews(AbsoluteLayout absContainer,
			ScrollView parentScroll, MenuView menuView,
			ViewPagerView pagerView, boolean isMan) {
		this.absContainer = absContainer;
		this.parentScroll = parentScroll;
		this.menuView = menuView;
		this.pagerView = pagerView;
		this.isMan = isMan;
		return this;
	}

	/**
	 * 设置数据 ，必须先调用setupViews
	 * 
	 * @param articles
	 */
	public void setData(JSONArray articles) {
		this.articles = articles;
		if (articles == null) {
			return;
		}
		rendItems();
	}

	/**
	 * 渲染数据
	 */
	private void rendItems() {
		removeAllViews();
		for (int i = 0; i < articles.length(); i++) {
			View itemView = null;
			try {
				itemView = getView(articles.getJSONObject(i));
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}
			if (itemView != null) {
				addView(itemView);
				upDownSummary((ViewGroup) itemView);
			}
		}
	}
	
	/**
	 * 展开 收藏 文章内容
	 * @param itemView
	 */
	private void upDownSummary(ViewGroup itemView) {
		// 展开 收起
		final TextView summaryView = (TextView) itemView
				.findViewById(R.id.man_item_summary);
		final TextView upDownView = (TextView) itemView
				.findViewById(R.id.man_item_up_down);
		upDownView.setTag(summaryView);

		summaryView.post(new Runnable() {
			@Override
			public void run() {
				System.out.println("-------------summaryView.getLineCount():"
						+ summaryView.getLineCount());
				if (summaryView.getLineCount() >= 3) {
					upDownView.setVisibility(View.VISIBLE);
					upDownView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							TextView tv = (TextView) v;
							TextView summaryView = (TextView) v.getTag();
							String text = tv.getText().toString();

							if (getResources().getString(R.string.man_show_all)
									.equals(text)) { // 显示全部
								summaryView.setSingleLine(false);
								summaryView.setEllipsize(null);
								tv.setText(R.string.man_txt_up);
							} else if (getResources().getString(
									R.string.man_txt_up).equals(text)) { // 收起文字
								summaryView.setLines(3);
								summaryView
										.setEllipsize(TextUtils.TruncateAt.END);
								tv.setText(R.string.man_show_all);
							}
						}
					});
				}
			}
		});
	}

	public View getView(JSONObject article) {
		LinearLayout convertView = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.man_item, null);

		if (isMan) {
			try {
				ViewGroup userView = (ViewGroup) convertView
						.findViewById(R.id.man_item_user);
				userView.setTag(article.getString("authorId"));
				userView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context, PersonMan.class);
						intent.putExtra("userId", v.getTag().toString());
						context.startActivity(intent);
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		try {
			((TextView) convertView.findViewById(R.id.man_item_name))
					.setText(article.getString("userName"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			((NetImageView) convertView.findViewById(R.id.man_item_head))
					.setNetUrl(article.getString("authorImageUrl"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			((TextView) convertView.findViewById(R.id.man_item_date))
					.setText(getDate(article.getString("createDate")));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			((TextView) convertView.findViewById(R.id.man_item_summary))
					.setText(article.getString("summary"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// 渲染图片
		try {
			rendItemImgs(article.getJSONArray("images"),
					(LinearLayout) convertView.findViewById(R.id.man_item_imgs));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// 设置父scroll，避免子scroll与父scroll冲突
		((InnerScrollView) convertView.findViewById(R.id.man_item_imgs_scroll))
				.setParentScrollView(parentScroll);

		// 为more按钮添加侦听
		ImageView moreBt = (ImageView) convertView
				.findViewById(R.id.man_item_more);
		moreBt.setTag(article);
		moreBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				menuView.show(v, absContainer);
			}
		});

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 0, 10);
		convertView.setLayoutParams(params);

		convertView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				menuView.hide();
				return false;
			}
		});
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("-----------------------setOnClickListener");
				menuView.hide();
			}
		});
		return convertView;
	}

	/**
	 * 渲染每条数据图片
	 */
	private void rendItemImgs(JSONArray imgs, ViewGroup imagesContainer) {
		JSONObject curImg = new JSONObject();

		try {
			curImg.put("imgs", imgs);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		for (int i = 0; i < imgs.length(); i++) {
			try {
				JSONObject img = imgs.getJSONObject(i);
				NetImageView imgView = new NetImageView(context);

				LayoutParams params = new LayoutParams(DensityUtil.dip2px(90),
						DensityUtil.dip2px(90));
				params.setMargins(0, 0, DensityUtil.dip2px(10), 0);
				imgView.setLayoutParams(params);

				imgView.setScaleType(ScaleType.CENTER_CROP);
				imgView.setCornerRadius(3);
				imgView.setNetUrl(img.getString("url"));

				curImg.put("index", i);
				imgView.setTag(curImg);
				imagesContainer.addView(imgView);

				imgView.setOnClickListener(imgOnClick);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	OnClickListener imgOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			JSONObject curImg = (JSONObject) v.getTag();

			try {
				pagerView.setData(curImg.getInt("index"),
						curImg.getJSONArray("imgs"));
				pagerView.setVisibility(View.VISIBLE);
				// pagerViewContainer.setVisibility(View.VISIBLE);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	};

	/**
	 * 获取日期
	 * 
	 * @param dateStr
	 * @return
	 */
	private String getDate(String dateStr) {
		String[] arr = dateStr.split(" ");
		return arr != null && arr.length >= 2 ? arr[0] : dateStr;
	}

}
