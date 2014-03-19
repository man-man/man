package com.app.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;
import com.app.util.DensityUtil;
import com.app.view.InnerScrollView;
import com.app.view.NetImageView;

/**
 * 男人帮
 * 
 * @author 王灵
 * 
 */
public class Man extends Activity {

	private ScrollView parentScroll; // 父scroll
	private ViewGroup manListView; // 文章列表容器

	ManHttpHandler manHttpHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.man);

		parentScroll = (ScrollView) findViewById(R.id.man_list_scroll);
		manListView = (ViewGroup) findViewById(R.id.man_list);

		manHttpHandler = new ManHttpHandler();
		rankReqSend();
	}

	/**
	 * 发送http请求
	 */
	private void rankReqSend() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = manHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT
								+ "GetArticle.shtml?userId=22"
								// + BaseUtils.CUR_USER_MAP.get("id")
								+ "&pageNumber=1&pageLine=15");
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
	}

	private void rendItems(JSONArray articles) {

		for (int i = 0; i < articles.length(); i++) {
			View itemView = null;
			try {
				itemView = getView(articles.getJSONObject(i));
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}
			if (itemView != null) {
				manListView.addView(itemView);
			}
		}
	}

	public View getView(JSONObject article) {
		LinearLayout convertView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.man_item,
				null);

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

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 0, 10);
		convertView.setLayoutParams(params);
		return convertView;
	}

	/**
	 * 渲染每条数据图片
	 */
	private void rendItemImgs(JSONArray imgs, ViewGroup imagesContainer) {
		for (int i = 0; i < imgs.length(); i++) {
			try {
				JSONObject img = imgs.getJSONObject(i);
				NetImageView imgView = new NetImageView(this);

				LayoutParams params = new LayoutParams(DensityUtil.dip2px(90),
						DensityUtil.dip2px(90));
				params.setMargins(0, 0, DensityUtil.dip2px(10), 0);
				imgView.setLayoutParams(params);

				imgView.setScaleType(ScaleType.CENTER_CROP);
				imgView.setCornerRadius(3);
				imgView.setNetUrl(img.getString("url"));
				imagesContainer.addView(imgView);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	private String getDate(String dateStr) {
		String[] arr = dateStr.split(" ");
		return arr != null && arr.length >= 2 ? arr[0] : dateStr;
	}

	/**
	 * 请求数据内部类
	 * 
	 * @author XH
	 * 
	 */
	class ManHttpHandler extends HttpCallBackHandler {

		public ManHttpHandler(Looper looper) {
			super(looper);
		}

		public ManHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");

				if (success) {
					JSONObject data = (JSONObject) resultObj.get("data");
					JSONArray articles = null;
					try {
						articles = data.getJSONArray("articles");
					} catch (Exception e) {
						Toast.makeText(Man.this, "暂无数据", Toast.LENGTH_SHORT)
								.show();
						return;
					}
					rendItems(articles);

				} else {
					Toast.makeText(Man.this,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(Man.this, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	//
	// /**
	// * 模拟数据
	// */
	// private void initDatas() {
	// List<ManItemModel> list = new ArrayList<ManItemModel>();
	//
	// final int size = 11;
	// final int imgSize = 6;
	//
	// for (int i = 0; i < size; i++) {
	// ManItemModel m = new ManItemModel();
	// m.setArticleId(i);
	// m.setAuthorName("王鹏");
	// m.setAuthorImg("http://a.hiphotos.baidu.com/image/w%3D2048/sign=fc61311395dda144da096bb2868fd1a2/fcfaaf51f3deb48f7ce234def21f3a292df57823.jpg");
	// m.setAuthorId(i);
	// m.setDate("2014-3-3");
	// m.setSummary("支付宝（alipay）最初作为淘宝网公司为了解决网络交易安全所设的一个功能，该功能为首先使用的“第三方担保交易模式”，由买家将货款打到支付宝账户，由支付宝向卖家通知发货，买家收到商品确认后指令支付宝将货款放于卖家，至此完成一笔网络交易。");
	//
	// List<ManItemImgsModel> imgs = new ArrayList<ManItemImgsModel>();
	// for (int j = 0; j < imgSize; j++) {
	// ManItemImgsModel imgModel = new ManItemImgsModel();
	// imgModel.setId(j);
	// imgModel.setUrl("http://img1.6544.cc/mn123/201201/1052074404.jpg");
	// imgModel.setSummary("色情吧，呵呵~~~");
	// imgs.add(imgModel);
	// }
	//
	// m.setImages(imgs);
	//
	// list.add(m);
	// }
	//
	// manListAdapter.setDatas(list);
	// manListAdapter.notifyDataSetChanged();
	// }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// for (int i = 0; i < NetImageView.NET_IMAGE_LOOPER_LIST.size(); i++) {
		// MyImgHandler h = NetImageView.NET_IMAGE_LOOPER_LIST.get(i);
		// h.setStart(false);
		// NetImageView.NET_IMAGE_LOOPER_LIST.remove(h);
		// }
		// for (int i = 0; i < NetImageView.NET_IMAGE_BITMAP_LIST.size(); i++) {
		// Bitmap h = NetImageView.NET_IMAGE_BITMAP_LIST.get(i);
		// if (!h.isRecycled()) {
		// h.recycle();
		// System.out.println("bitmap加入回收");
		// h = null;
		// System.gc();
		// }
		// NetImageView.NET_IMAGE_BITMAP_LIST.remove(h);
		// }
	}

	Handler mainHandler;
}
