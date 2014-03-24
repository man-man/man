package com.app.view;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.common.BubbleUtil;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;
import com.app.util.DensityUtil;

@SuppressLint("NewApi")
public class MenuView extends RelativeLayout {

	private final int WIDTH = 95; // 该组件固定宽度(因为getWidth获取，在第一次获取不到（未显示状态下），先在程序里写死吧)

	private Context context;

	private View targetView; // 相对于哪个view弹出
	private ViewGroup parentView; // 放menu的父容器
	private ViewGroup menuView; // 菜单view

	private String dataId; // 数据id

	private TextView commentView; // 评论
	private TextView attenView; // 关注
	private TextView collectView; // 收藏
	private TextView shareView; // 分享

	HttpHandler httpHandler; // http请求
	Bundle bundle; // http请求数据
	String OPT_SUCCESS; // 操作成功提示

	public MenuView(Context context) {
		super(context);
		this.context = context;
		setupViews();
	}

	public MenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setupViews();
	}

	/**
	 * 显示菜单
	 */
	public void show() {
		show(targetView, parentView);
	}

	/**
	 * 显示菜单
	 * 
	 * @param targetView
	 */
	public void show(View v, ViewGroup pv) {
		this.targetView = v;
		this.parentView = pv;

		if (v != null && pv != null) {
			this.dataId = (String) targetView.getTag();

			hide();

			int[] pos = getPos(targetView);
			int[] parentPos = getPos(parentView);

			this.setX(pos[0] - DensityUtil.dip2px(WIDTH)
					+ targetView.getWidth());
			this.setY(pos[1] - parentPos[1] + targetView.getHeight()
					- DensityUtil.dip2px(5));
			this.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 隐藏菜单
	 */
	public void hide() {
		this.setVisibility(View.GONE);
	}

	private void setupViews() {
		LayoutInflater.from(context).inflate(R.layout.menu, this);

		commentView = (TextView) findViewById(R.id.menu_comment);
		attenView = (TextView) findViewById(R.id.menu_atten);
		collectView = (TextView) findViewById(R.id.menu_collect);
		shareView = (TextView) findViewById(R.id.menu_share);
	}

	/**
	 * 获取view在当前窗口的绝对坐标
	 * 
	 * @param view
	 * @return
	 */
	private int[] getPos(View view) {
		int[] location = new int[2];
		view.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
		view.getLocationOnScreen(location);// 获取在整个屏幕内的绝对坐标
		System.out.println("view--->x坐标:" + location[0] + "view--->y坐标:"
				+ location[1]);

		return location;
	}

	/**
	 * 菜单项点击侦听
	 */
	OnClickListener menuItemOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			bundle = new Bundle();
			String params = null;
			boolean isHttp = false;

			switch (v.getId()) {
			case R.id.menu_comment: // 评论
				break;
			case R.id.menu_collect: // 收藏或取消收藏
				isHttp = true;
				params = "Favorite.shtml?articleId=" + dataId + "&value=true"
						+ "&userId=22";
				OPT_SUCCESS = "收藏成功";
				break;
			case R.id.menu_atten: // 关注
				isHttp = true;
				params = "FollowUser.shtml?followUserId=" + dataId
						+ "&value=true" + "&userId=22";
				OPT_SUCCESS = "关注成功";
				break;
			case R.id.menu_share:
				break;
			default:
				break;
			}
			if (isHttp) {
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT + params);
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				reqSend();
			}
		}
	};

	/**
	 * 发送http请求
	 */
	private void reqSend() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = httpHandler.obtainMessage();
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
	class HttpHandler extends HttpCallBackHandler {

		public HttpHandler(Looper looper) {
			super(looper);
		}

		public HttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");

				if (success) {
					BubbleUtil.alertMsg(context,
							OPT_SUCCESS);
				} else {
					BubbleUtil.alertMsg(context,
							resultObj.getString("errorMessage"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				BubbleUtil.alertMsg(context, R.string.base_response_error);
			}
		}

	}

}
