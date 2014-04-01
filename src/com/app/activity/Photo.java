package com.app.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.activity.PhotoAlbum.AlbumInfoHttpHandler;
import com.app.activity.PhotoAlbum.DelAlbumHttpHandler;
import com.app.common.AlertDialogWindow;
import com.app.common.Base64Utils;
import com.app.common.BaseUtils;
import com.app.common.BubbleUtil;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.common.ImageUtil;
import com.app.common.JSONObjectSerializalble;
import com.app.man.R;
import com.app.util.DensityUtil;
import com.app.view.NetImageView;
import com.app.view.ViewPagerView;

public class Photo extends BaseActivity {

	final String WIN_MENU_TITLE = "更换或删除照片";
	final String[] WIN_MENU_DATAS = new String[] { "预览照片", "编辑描述文字", "删除照片" };
	final String WIN_MENU_TITLE2 = "上传照片";
	final String[] WIN_MENU_DATAS2 = new String[] { "图库相册", "拍照" };

	Context context = this;

	// 数据
	private JSONArray photoDatas; // 相册中photo数据
	private String albumId; // 相册id
	private String imgBase64; // 上传照片数据
	private JSONArray imgArr;
	private JSONObject imgBase64Arr; // 上传照片数组
	private String type = null; // 相册类型

	// 控件
	private ViewPagerView pagerView; // 图片切换组件
	private ViewGroup photoCol_1; // 第一列
	private ViewGroup photoCol_2; // 第二列
	private ViewGroup photoCol_3; // 第三列
	private ViewGroup photoCol_4; // 第四列
	private View createBtn; // 创建按钮
	private ViewGroup photoDesContainer; // 照片描述container
	private EditText photoDesView; // 照片描述view
	private Button summaryYesBtn; // 确定描述
	private View curClickPhoto; // 当前点击图片

	AlbumInfoHttpHandler albumInfoHttpHandler = new AlbumInfoHttpHandler();
	AddPhotoHttpHandler addPhotoHttpHandler = new AddPhotoHttpHandler();
	DelPhotoHttpHandler delHttpHandler = new DelPhotoHttpHandler();

	AlertDialogWindow alertDialogWindow; // 点击照片弹出
	AlertDialogWindow addAlertDialogWindow; // 点击添加照片弹出

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.photo);

		Bundle b = getIntent().getExtras();
		albumId = b.getString("albumId");
		type = b.getString("type");
		try {
			photoDatas = new JSONArray(b.getString("imgs"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		setupViews();
		rendItems();

	}

	private void setupViews() {
		pagerView = (ViewPagerView) findViewById(R.id.photo_pager_view);
		photoCol_1 = (ViewGroup) findViewById(R.id.photo_col_1);
		photoCol_2 = (ViewGroup) findViewById(R.id.photo_col_2);
		photoCol_3 = (ViewGroup) findViewById(R.id.photo_col_3);
		photoCol_4 = (ViewGroup) findViewById(R.id.photo_col_4);
		photoDesContainer = (ViewGroup) findViewById(R.id.photo_des_container);
		photoDesView = (EditText) findViewById(R.id.photo_des_input);
		summaryYesBtn = (Button) findViewById(R.id.photo_des_submit);
		createBtn = LayoutInflater.from(this).inflate(
				R.layout.photo_album_create_btn, null);

		if ("other".equals(type)) {
			createBtn.setVisibility(View.GONE);
		}

		createBtn.setOnClickListener(addImgOnClick);

		summaryYesBtn.setOnClickListener(summarySubmitClick);

		if (alertDialogWindow == null) {
			alertDialogWindow = new AlertDialogWindow(WIN_MENU_TITLE,
					WIN_MENU_DATAS, this, alertItemOnClick);
		}
		if (addAlertDialogWindow == null) {
			addAlertDialogWindow = new AlertDialogWindow(WIN_MENU_TITLE2,
					WIN_MENU_DATAS2, this, alertItemOnClick2);
		}
	}

	OnClickListener summarySubmitClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			photoDesContainer.setVisibility(View.GONE);

		}
	};

	/**
	 * 点击添加照片
	 */
	OnClickListener addImgOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			addAlertDialogWindow.getDialog().show();
		}
	};

	/**
	 * 点击照片
	 */
	OnClickListener imgOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			curClickPhoto = v;

			JSONObject curPhotoData = (JSONObject) curClickPhoto.getTag();

			if ("other".equals(type)) {
				try {
					pagerView.setData(curPhotoData.getInt("id"), photoDatas);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				pagerView.setVisibility(View.VISIBLE);
			} else {
				alertDialogWindow.getDialog().show();
			}
		}
	};

	/**
	 * 底部弹窗选择（照片）
	 */
	OnClickListener alertItemOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String txt = ((TextView) v).getText().toString();
			JSONObject curPhotoData = (JSONObject) curClickPhoto.getTag();

			if (WIN_MENU_DATAS[0].equals(txt)) { // 预览照片
				try {
					pagerView.setData(curPhotoData.getInt("id"), photoDatas);
					pagerView.setVisibility(View.VISIBLE);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (WIN_MENU_DATAS[1].equals(txt)) { // 填写描述
				photoDesView.setText("");
				photoDesContainer.setVisibility(View.VISIBLE);

				photoDesView.setFocusable(true);
				photoDesView.requestFocus();
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				// 接受软键盘输入的编辑文本或其它视图
				inputMethodManager.showSoftInput(photoDesView,
						InputMethodManager.SHOW_FORCED);
			} else if (WIN_MENU_DATAS[2].equals(txt)) { // 删除照片
				delPhotoReq();
			}

			alertDialogWindow.getDialog().hide();
		}
	};

	/**
	 * 底部弹窗选择（添加）
	 */
	OnClickListener alertItemOnClick2 = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String txt = ((TextView) v).getText().toString();

			if (WIN_MENU_DATAS2[0].equals(txt)) { // 图库相册
				Intent picture = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(picture, 1);
			} else if (WIN_MENU_DATAS2[1].equals(txt)) { // 相机
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 2);
			}
			addAlertDialogWindow.getDialog().hide();
		}
	};

	/**
	 * 相册选完图片 或 相机拍完照之后回调
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 2:
			if (data != null && data.getExtras() != null) {
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				if (bitmap != null) {
					// sign_info_photo.setDrawingCacheEnabled(true);
					byte[] bytes2 = ImageUtil.getByteByBitmap(bitmap);
					imgBase64 = Base64Utils.encode(bytes2);
					// headLogoView.setImageBitmap(bitmap);
				}
			}
			break;
		case 1:
			if (data != null && data.getData() != null) {
				if (data.getData() != null) {
					Uri uri = data.getData();
					int tmparr[] = { 480, 800 };
					Bitmap tmp = ImageUtil.createNewBitmapAndCompressByFile(
							ImageUtil.getFilePath(context, uri), tmparr);
					// headLogoView.setImageBitmap(tmp);
					byte[] bytes2 = ImageUtil.getByteByBitmap(tmp);
					imgBase64 = Base64Utils.encode(bytes2);
				}
			}
			break;
		default:
			break;
		}
		addPhotoReq();
		addAlertDialogWindow.getDialog().hide();
	}

	private void rendItems() {
		photoCol_1.removeAllViews();
		photoCol_2.removeAllViews();
		photoCol_3.removeAllViews();
		photoCol_4.removeAllViews();

		photoCol_1.addView(addMarginBottom(createBtn));
		for (int i = 0; i < photoDatas.length(); i++) {
			JSONObject itemData = null;
			try {
				itemData = photoDatas.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if ("other".equals(type)) {
				switch (i % 4) {
				case 0:
					photoCol_1.addView(addMarginBottom(getView(itemData)));
					break;
				case 1:
					photoCol_2.addView(addMarginBottom(getView(itemData)));
					break;
				case 2:
					photoCol_3.addView(addMarginBottom(getView(itemData)));
					break;
				case 3:
					photoCol_4.addView(addMarginBottom(getView(itemData)));
					break;
				default:
					break;
				}
			} else {
				switch (i % 4) {
				case 0:
					photoCol_2.addView(addMarginBottom(getView(itemData)));
					break;
				case 1:
					photoCol_3.addView(addMarginBottom(getView(itemData)));
					break;
				case 2:
					photoCol_4.addView(addMarginBottom(getView(itemData)));
					break;
				case 3:
					photoCol_1.addView(addMarginBottom(getView(itemData)));
					break;
				default:
					break;
				}
			}
		}
	}

	private View getView(JSONObject obj) {
		View view = LayoutInflater.from(this)
				.inflate(R.layout.photo_item, null);

		NetImageView img = (NetImageView) view
				.findViewById(R.id.photo_item_img);
		try {
			img.setNetUrl(obj.getString("imageUrl"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		img.setTag(obj);
		img.setOnClickListener(imgOnClick);
		return view;
	}

	/**
	 * 为view添加marginBottom
	 */
	private View addMarginBottom(View view) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		params.setMargins(0, 0, 0, DensityUtil.dip2px(5));
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
	 * 添加照片请求
	 */
	private void addPhotoReq() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				imgArr = new JSONArray();
				// 假如有多个图，要循环
				// for(){
				imgBase64Arr = new JSONObject();
				try {
					imgBase64Arr.put("image", imgBase64);
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				imgArr.put(imgBase64Arr);
				// }

				Message msg = addPhotoHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT
								+ "UploadImage.shtml?");
				JSONObjectSerializalble jsonObject = new JSONObjectSerializalble();
				try {
					jsonObject.put("userId",
							BaseUtils.CUR_USER_MAP.get("userId"));
					jsonObject.put("albumId", albumId);
					jsonObject.put("summary", "");
					jsonObject.put("images", imgArr);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				bundle.putSerializable(HttpRequestUtils.BUNDLE_KEY_PARAMS,
						jsonObject);
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, true);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
	}

	/**
	 * 删除照片请求
	 */
	private void delPhotoReq() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject obj = (JSONObject) curClickPhoto.getTag();
				String photoId = null;

				try {
					photoId = obj.getString("id");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				Message msg = delHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT
								+ "RemoveImage.shtml?userId="
								+ BaseUtils.CUR_USER_MAP.get("userId")
								+ "&imageId=" + photoId);
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
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
					JSONArray albums = data.getJSONArray("albums");

					for (int i = 0; i < albums.length(); i++) {
						JSONObject albumItem = albums.getJSONObject(i);

						if (albumId.equals(albumItem.getString("id"))) {
							photoDatas = albumItem.getJSONArray("images");
							break;
						}
					}

					rendItems();

				} else {
					Toast.makeText(context,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(context, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	class AddPhotoHttpHandler extends HttpCallBackHandler {

		public AddPhotoHttpHandler(Looper looper) {
			super(looper);
		}

		public AddPhotoHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");
				if (success) {
					BubbleUtil.alertMsg(context, R.string.add_photo_success);
					getAlbumReq();
				} else {
					Toast.makeText(context,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(context, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	class DelPhotoHttpHandler extends HttpCallBackHandler {

		public DelPhotoHttpHandler(Looper looper) {
			super(looper);
		}

		public DelPhotoHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");
				if (success) {
					BubbleUtil.alertMsg(context, R.string.del_album_success);
					getAlbumReq();
				} else {
					Toast.makeText(context,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(context, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

}
