package com.app.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.common.BaseUtils;
import com.app.common.Constant;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;
import com.app.util.DensityUtil;
import com.app.view.InnerScrollView;
import com.app.view.ManListView;
import com.app.view.MenuView;
import com.app.view.NetImageView;
import com.app.view.SearchView;
import com.app.view.ViewPagerView;

/**
 * 男人帮
 * 
 * @author 王灵
 * 
 */
public class Man extends BaseActivity {

	private AbsoluteLayout absContainer; // 绝对布局容器
	private ScrollView parentScroll; // 父scroll
	private ManListView manListView; // 文章列表容器
	private ViewPagerView pagerView; // 图片切换组件
	private SearchView searchView; // 搜索框

	private MenuView menuView; // 更多菜单

	ManHttpHandler manHttpHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.man);

		absContainer = (AbsoluteLayout) findViewById(R.id.abs_container);
		parentScroll = (ScrollView) findViewById(R.id.man_list_scroll);
		manListView = (ManListView) findViewById(R.id.man_list);
		pagerView = (ViewPagerView) findViewById(R.id.man_pager_view);
		searchView = (SearchView) findViewById(R.id.man_seach_title);

		if (Integer.valueOf(BaseUtils.CUR_USER_MAP.get("groupId").toString()) == Constant.USER_TYPE_MAN) { // 达人
			searchView.setWrite(true);
		}

		menuView = (MenuView) findViewById(R.id.man_menu_vew);

		absContainer.setOnTouchListener(absCOnTouch);

		manHttpHandler = new ManHttpHandler();
		rankReqSend();
	}

	OnTouchListener absCOnTouch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			menuView.hide();
			return false;
		}
	};

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
								+ "GetArticle.shtml?userId="
								+ BaseUtils.CUR_USER_MAP.get("id")
								+ "&pageNumber=1&pageLine=15");
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
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

					manListView.setupViews(absContainer, parentScroll,
							menuView, pagerView, true).setData(articles);

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
