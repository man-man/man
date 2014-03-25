package com.app.view;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import com.app.activity.Comment;
import com.app.common.BaseUtils;
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

	private JSONObject targetData; // 目标数据

	private TextView commentView; // 评论
	private TextView attenView; // 关注
	private TextView collectView; // 收藏
	private TextView shareView; // 分享

	HttpHandler httpHandler = new HttpHandler(); // http请求
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
		targetView = v;
		parentView = pv;

		if (v != null && pv != null) {
			targetData = (JSONObject) targetView.getTag();

			hide();

			int[] pos = getPos(targetView);
			int[] parentPos = getPos(parentView);

			this.setX(pos[0] - DensityUtil.dip2px(WIDTH)
					+ targetView.getWidth());
			this.setY(pos[1] - parentPos[1] + targetView.getHeight()
					- DensityUtil.dip2px(5));

			// 关注或取消关注
			attenView.setText(true ? "关注" : "取消关注");

			// 收藏或取消收藏
			collectView.setText(true ? "收藏" : "取消收藏");

			// 男性则显示收藏,女性不显示收藏
			collectView.setVisibility(Integer.valueOf(BaseUtils.CUR_USER_MAP
					.get("gender").toString()) == 1 ? View.VISIBLE : View.GONE);

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

		commentView.setOnClickListener(menuItemOnClick);
		attenView.setOnClickListener(menuItemOnClick);
		collectView.setOnClickListener(menuItemOnClick);
		shareView.setOnClickListener(menuItemOnClick);
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
			String articleId = null;
			String fllowUserId = null;
			int value = 0; // 收藏，关注默认取消状态

			try {
				articleId = targetData.getString("id");
				fllowUserId = targetData.getString("authorId");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			switch (v.getId()) {
			case R.id.menu_comment: // 评论
				Intent intent = new Intent(context, Comment.class);
				intent.putExtra("articleId", articleId);
				context.startActivity(intent);
				break;
			case R.id.menu_collect: // 收藏或取消收藏
				isHttp = true;
				OPT_SUCCESS = "取消收藏成功";

				if ("收藏".equals(((TextView) v).getText().toString())) {
					value = 1;
					OPT_SUCCESS = "收藏成功";
				}
				params = "Favorite.shtml?articleId=" + articleId + "&value="
						+ value + "&userId=" + BaseUtils.CUR_USER_MAP.get("id");
				break;
			case R.id.menu_atten: // 关注
				isHttp = true;
				OPT_SUCCESS = "取消关注成功";

				if ("关注".equals(((TextView) v).getText().toString())) {
					value = 1;
					OPT_SUCCESS = "关注成功";
				}
				params = "FollowUser.shtml?followUserId=" + fllowUserId
						+ "&value=" + value + "&userId="
						+ BaseUtils.CUR_USER_MAP.get("id");
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
			hide();
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

					// 更新当前 target数据 TODO

					BubbleUtil.alertMsg(context, OPT_SUCCESS);
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
