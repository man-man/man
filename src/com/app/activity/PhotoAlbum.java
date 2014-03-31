package com.app.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.common.AlertDialogWindow;
import com.app.common.BaseUtils;
import com.app.common.BubbleUtil;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;
import com.app.man.R.id;
import com.app.man.R.layout;
import com.app.man.R.menu;
import com.app.man.R.string;
import com.app.util.DensityUtil;
import com.app.view.NetImageView;

/**
 * 相册
 * 
 * @author XH
 * 
 */
public class PhotoAlbum extends Activity {

	final String[] WIN_MENU_DATAS = new String[] { "进入相册", "删除相册" };

	// 控件
	private View createBtn; // 创建按钮
	private Button createYesBtn; // 输完名字后，创建按钮
	private ViewGroup albumNameContainer; // 相册名字container
	private EditText albumNameView; // 相册名字view
	private ViewGroup rightCol; // 右列
	private ViewGroup leftCol; // 左列

	private View curClickAlbum; // 当前点击相册

	AlbumInfoHttpHandler albumInfoHttpHandler = new AlbumInfoHttpHandler();
	CreateAlbumHttpHandler createHttpHandler = new CreateAlbumHttpHandler();
	DelAlbumHttpHandler delHttpHandler = new DelAlbumHttpHandler();

	AlertDialogWindow alertDialogWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.photo_album);

		setupViews();
		getAlbumReq();
	}

	private void setupViews() {
		createBtn = LayoutInflater.from(this).inflate(
				R.layout.photo_album_create_btn, null);
		createYesBtn = (Button) findViewById(R.id.album_name_submit);
		albumNameContainer = (ViewGroup) findViewById(R.id.album_name_container);
		albumNameView = (EditText) findViewById(R.id.album_name_input);
		rightCol = (ViewGroup) findViewById(R.id.album_right_col);
		leftCol = (ViewGroup) findViewById(R.id.album_left_col);

		createBtn.setOnClickListener(createOnClick);
		createYesBtn.setOnClickListener(createYesOnClick);

		if (alertDialogWindow == null) {
			alertDialogWindow = new AlertDialogWindow("", WIN_MENU_DATAS, this,
					alertItemOnClick);
		}
	}

	/**
	 * 底部弹窗选择
	 */
	OnClickListener alertItemOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String txt = ((TextView) v).getText().toString();
			JSONObject curAlbumData = (JSONObject) curClickAlbum.getTag();

			if (WIN_MENU_DATAS[0].equals(txt)) { // 进入相册
				Intent intent = new Intent(PhotoAlbum.this, Photo.class);
				Bundle b = new Bundle();
				try {
					b.putString("albumId", curAlbumData.getString("id"));
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				try {
					b.putString("imgs", curAlbumData.getJSONArray("images")
							.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				intent.putExtras(b);
				PhotoAlbum.this.startActivity(intent);
			} else if (WIN_MENU_DATAS[1].equals(txt)) { // 删除相册
				// ((ViewGroup) curClickAlbum.getParent())
				// .removeView(curClickAlbum);
				delAlbumReq();
			}

			alertDialogWindow.getDialog().hide();
		}
	};

	/**
	 * 创建相册侦听
	 */
	OnClickListener createOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			albumNameView.setText("");
			albumNameContainer.setVisibility(View.VISIBLE);
			albumNameView.setFocusable(true);
			albumNameView.requestFocus();

			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			// 接受软键盘输入的编辑文本或其它视图
			inputMethodManager.showSoftInput(albumNameView,
					InputMethodManager.SHOW_FORCED);

		}
	};

	/**
	 * 创建相册，填完名字，确定侦听
	 */
	OnClickListener createYesOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if ("".equals(albumNameView.getText())) {
				BubbleUtil.alertMsg(PhotoAlbum.this, R.string.album_name_null);
				return;
			}
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(PhotoAlbum.this
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			// 接受软键盘输入的编辑文本或其它视图
			inputMethodManager.showSoftInput(albumNameView,
					InputMethodManager.SHOW_FORCED);
			albumNameView.setFocusable(false);
			albumNameView.clearFocus();
			albumNameContainer.setVisibility(View.GONE);
			createAlbumReq();
		}
	};

	private void rendItems(JSONArray arr) {
		leftCol.removeAllViews();
		rightCol.removeAllViews();

		leftCol.addView(addMarginBottom(createBtn));
		for (int i = 0; i < arr.length(); i++) {
			JSONObject item = null;
			try {
				item = arr.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}
			if (i % 2 == 0) {
				rightCol.addView(addMarginBottom(getView(item)));
			} else {
				leftCol.addView(addMarginBottom(getView(item)));
			}
		}
	}

	private View getView(JSONObject obj) {
		View view = LayoutInflater.from(this).inflate(
				R.layout.photo_album_item, null);
		NetImageView imgView = (NetImageView) view
				.findViewById(R.id.photo_album_item_img);
		TextView nameView = (TextView) view
				.findViewById(R.id.photo_album_item_name);

		try {
			JSONObject imgData = obj.getJSONArray("images").getJSONObject(0);
			imgView.setNetUrl(imgData.getString("imageUrl"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			nameView.setText(obj.getString("title"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		view.setTag(obj);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				JSONObject obj = (JSONObject) v.getTag();

				curClickAlbum = v;
				try {
					alertDialogWindow.setTitleData(obj.getString("title"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				alertDialogWindow.getDialog().show();
			}
		});

		return view;
	}

	/**
	 * 为view添加marginBottom
	 */
	private View addMarginBottom(View view) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		params.setMargins(0, 0, 0, DensityUtil.dip2px(10));
		((ViewGroup) view).setLayoutParams(params);
		return view;
	}

	/**
	 * 获取相册请求
	 */
	private void getAlbumReq() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = albumInfoHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT
								+ "GetUserInfo.shtml?userId="
								+ BaseUtils.CUR_USER_MAP.get("userId"));
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
	}

	/**
	 * 创建相册请求
	 */
	private void createAlbumReq() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String albumName = albumNameView.getText().toString();

				Message msg = createHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				try {
					bundle.putString(
							HttpRequestUtils.BUNDLE_KEY_HTTPURL,
							HttpRequestUtils.BASE_HTTP_CONTEXT
									+ "SaveAlbum.shtml?userId="
									+ BaseUtils.CUR_USER_MAP.get("userId")
									+ "&title="
									+ URLEncoder.encode(albumName, "UTF-8")
									+ "&private=1");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
	}

	/**
	 * 删除相册请求
	 */
	private void delAlbumReq() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject obj = (JSONObject) curClickAlbum.getTag();
				String albumId = null;

				try {
					albumId = obj.getString("id");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				Message msg = delHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT
								+ "RemoveAlbum.shtml?userId="
								+ BaseUtils.CUR_USER_MAP.get("userId")
								+ "&albumId=" + albumId);
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
	}

	class CreateAlbumHttpHandler extends HttpCallBackHandler {

		public CreateAlbumHttpHandler(Looper looper) {
			super(looper);
		}

		public CreateAlbumHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");
				if (success) {
					BubbleUtil.alertMsg(PhotoAlbum.this,
							R.string.create_album_success);
					getAlbumReq();
				} else {
					Toast.makeText(PhotoAlbum.this,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(PhotoAlbum.this, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	class AlbumInfoHttpHandler extends HttpCallBackHandler {

		public AlbumInfoHttpHandler(Looper looper) {
			super(looper);
		}

		public AlbumInfoHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");
				if (success) {
					JSONObject data = resultObj.getJSONObject("data");

					rendItems(data.getJSONArray("albums"));

				} else {
					Toast.makeText(PhotoAlbum.this,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(PhotoAlbum.this, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	class DelAlbumHttpHandler extends HttpCallBackHandler {

		public DelAlbumHttpHandler(Looper looper) {
			super(looper);
		}

		public DelAlbumHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");
				if (success) {
					BubbleUtil.alertMsg(PhotoAlbum.this,
							R.string.del_album_success);
					getAlbumReq();
				} else {
					Toast.makeText(PhotoAlbum.this,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(PhotoAlbum.this, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

}
