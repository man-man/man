package com.app.common;

import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.man.R;

public class AlertDialogWindow {
	//数据
	private String titleData; //标题数据
	private String[] menuDatas; //动态menu数据
	
	private Context context;
	private Map<String, Object> cacheObj;
	private OnClickListener clickListener;

	private AlertDialog.Builder builder;
	private AlertDialog dialog;

	// 控件
	private LinearLayout winMenuViewGroup; // winMenu最外层容器
	private LinearLayout btnsContainer; // 动态button容器
	private TextView titleView; // 标题
	private TextView cancleView; // 取消按钮

	public AlertDialogWindow(String title, String[] strs, Context context,
			OnClickListener clickListener) {
		super();
		this.titleData = title;
		this.menuDatas = strs;
		this.context = context;
		this.clickListener = clickListener;

		setupViews();
	}

	private void setupViews() {
		winMenuViewGroup = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.win_menu, null);
		btnsContainer = (LinearLayout) winMenuViewGroup
				.findViewById(R.id.win_menu_bt_container);
		titleView = (TextView) winMenuViewGroup.findViewById(R.id.win_menu_title);
		cancleView = (TextView) winMenuViewGroup.findViewById(R.id.win_menu_cancle);
		
		titleView.setText(titleData);
		createMenuWin();
		cancleView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getDialog().hide();
			}
		});
		
		builder = new AlertDialog.Builder(context);
		dialog = builder.setView(winMenuViewGroup).create();

		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.mystyle); // 添加动画
	}

	/**
	 * 构造出菜单并显示
	 */
	private void createMenuWin() {
		for (int i = 0; i < menuDatas.length; i++) {
			TextView btn = (TextView) LayoutInflater.from(context).inflate(
					R.layout.win_menu_item_bt, null);
			btn.setText(menuDatas[i]);
			btnsContainer.addView(btn);
			btn.setOnClickListener(clickListener);
		}
	}

	public AlertDialog.Builder getBuilder() {
		return builder;
	}

	public void setBuilder(AlertDialog.Builder builder) {
		this.builder = builder;
	}

	public AlertDialog getDialog() {
		return dialog;
	}

	public void setDialog(AlertDialog dialog) {
		this.dialog = dialog;
	}

	public Map<String, Object> getCacheObj() {
		return cacheObj;
	}

	public void setCacheObj(Map<String, Object> cacheObj) {
		this.cacheObj = cacheObj;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public OnClickListener getClickListener() {
		return clickListener;
	}

	public void setClickListener(OnClickListener clickListener) {
		this.clickListener = clickListener;
	}
}
