package com.app.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;

import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;
import com.app.model.WomanItemModel;
import com.app.view.RankScrollView2;

/**
 * 装女郎 排行榜
 * 
 * @author 王灵
 * 
 */
public class Woman extends Activity {

	/**
	 * 排行榜数据
	 */
	private List<WomanItemModel> models = new ArrayList<WomanItemModel>();

	/**
	 * 图片数组
	 */
	private String[] imageUrlArr = null;

	/**
	 * 排行榜容器
	 */
	private RankScrollView2 rankScrollView;
	
	private RankListHttpHandler rankQ = new RankListHttpHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.woman);

		rankScrollView = (RankScrollView2) findViewById(R.id.rank_scroll_view);

		rankReqSend();
	}

	/**
	 * 发送http请求
	 */
	private void rankReqSend() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = rankQ.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT
								+ "GetGirlRank.shtml?type=1&days=30");
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
	}

	/**
	 * 模拟数据
	 */
	private void rankReqSend2() {
		int len = 9;
		imageUrlArr = new String[len];

		for (int i = 0; i < len; i++) {

			WomanItemModel model = new WomanItemModel();
			model.setDefaultImg(R.drawable.default_img);
			model.setImg(imageUrls[i]);
			model.setName("装女郎" + i);
			model.setVote(100 * i);
			model.setRank(i + 1);

			imageUrlArr[i] = model.getImg();

			models.add(model);
		}
		rankScrollView.setData(models, imageUrlArr);
	}

	/**
	 * 请求数据内部类
	 * 
	 * @author XH
	 * 
	 */
	class RankListHttpHandler extends HttpCallBackHandler {

		public RankListHttpHandler(Looper looper) {
			super(looper);
		}

		public RankListHttpHandler() {
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
					JSONArray users = null;

					try {
						users = data.getJSONArray("users");
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(Woman.this, R.string.rank_empty,
								Toast.LENGTH_SHORT).show();
						return;
					}
					int len = users.length();
					imageUrlArr = new String[len];

					for (int i = 0; i < len; i++) {
						JSONObject userObj = users.getJSONObject(i);

						WomanItemModel model = new WomanItemModel();
						try {
							model.setImg(userObj.getString("imageUrl"));
							model.setName(userObj.getString("name"));
							model.setVote(userObj.getInt("votes"));
							model.setRank(i + 1);

						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}

						imageUrlArr[i] = model.getImg();

						models.add(model);
					}

					Log.d("test", "--------back-imageUrlArr:"
							+ imageUrlArr.length);
					rankScrollView.setData(models, imageUrlArr);

				} else {
					Toast.makeText(Woman.this,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(Woman.this, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	public final static String[] imageUrls = new String[] {
			"http://img.my.csdn.net/uploads/201309/01/1378037235_3453.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037235_9280.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037234_3539.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037234_6318.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037194_2965.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037193_1687.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037193_1286.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037192_8379.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037178_9374.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037177_1254.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037177_6203.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037152_6352.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037151_9565.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037151_7904.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037148_7104.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037129_8825.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037128_5291.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037128_3531.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037127_1085.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037095_7515.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037094_8001.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037093_7168.jpg",
			"http://img.my.csdn.net/uploads/201309/01/1378037091_4950.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949643_6410.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949642_6939.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949630_4505.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949630_4593.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949629_7309.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949629_8247.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949615_1986.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949614_8482.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949614_3743.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949614_4199.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949599_3416.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949599_5269.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949598_7858.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949598_9982.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949578_2770.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949578_8744.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949577_5210.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949577_1998.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949482_8813.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949481_6577.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949480_4490.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949455_6792.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949455_6345.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949442_4553.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949441_8987.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949441_5454.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949454_6367.jpg",
			"http://img.my.csdn.net/uploads/201308/31/1377949442_4562.jpg" };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.woman, menu);
		return true;
	}

}
