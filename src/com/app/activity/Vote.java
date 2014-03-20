package com.app.activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.Window;
import android.widget.Toast;

import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;
import com.app.view.VoteView;

/**
 * 评选
 * 
 * @author 王灵
 * 
 */
public class Vote extends BaseActivity {

	private VoteView voteContainer;

	private PkListHttpHandler rankQ = new PkListHttpHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.vote);
		
		voteContainer = (VoteView) findViewById(R.id.vote_container);
		
		rankReqSend();
	}

	/**
	 * 发送http请求
	 */
	private void rankReqSend() {
		new Thread(new Runnable() {
			public void run() {
				Message msg = rankQ.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT + "GetGirlPK.shtml");
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
	class PkListHttpHandler extends HttpCallBackHandler {

		public PkListHttpHandler(Looper looper) {
			super(looper);
		}

		public PkListHttpHandler() {
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

					voteContainer.setData(data);
				} else {
					Toast.makeText(Vote.this,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(Vote.this, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
