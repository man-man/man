//package com.app.data;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.json.JSONTokener;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Looper;
//import android.os.Message;
//import android.widget.Toast;
//
//import com.app.activity.PhotoAlbum;
//import com.app.common.BaseUtils;
//import com.app.common.HttpCallBackHandler;
//import com.app.common.HttpRequestUtils;
//import com.app.man.R;
//
//public class PhotoData {
//	
//	Context context;
//	
//	public PhotoData(Context context) {
//		this.context = context;
//	}
//	
//	
//	
//	AlbumInfoHttpHandler albumInfoHttpHandler = new AlbumInfoHttpHandler();
//	
//	
//	/**
//	 * 获取相册请求
//	 */
//	private void getAlbumReq() {
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				Message msg = albumInfoHttpHandler.obtainMessage();
//				Bundle bundle = new Bundle();
//				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
//						HttpRequestUtils.BASE_HTTP_CONTEXT
//								+ "GetUserInfo.shtml?userId="
//								+ BaseUtils.CUR_USER_MAP.get("userId"));
//				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
//				msg.setData(bundle);
//				msg.sendToTarget();
//			}
//		}).start();
//	}
//
//	class AlbumInfoHttpHandler extends HttpCallBackHandler {
//
//		public AlbumInfoHttpHandler(Looper looper) {
//			super(looper);
//		}
//
//		public AlbumInfoHttpHandler() {
//		}
//
//		@Override
//		public void callAfterResponseStr(String resultStr) {
//			JSONTokener jsonParser = new JSONTokener(resultStr);
//			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
//			try {
//				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
//				Boolean success = resultObj.getBoolean("success");
//				if (success) {
//					JSONObject data = resultObj.getJSONObject("data");
//
//					rendItems(data.getJSONArray("albums"));
//
//				} else {
//					Toast.makeText(context,
//							resultObj.getString("errorMessage"),
//							Toast.LENGTH_SHORT).show();
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//				Toast.makeText(context, R.string.base_response_error,
//						Toast.LENGTH_SHORT).show();
//			}
//		}
//
//	}
//}
