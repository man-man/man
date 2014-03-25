package com.app.common;

import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.man.R;

public class AlertDialogWindow {
	private List<Map<String, Object>> params;
	private Context context;
	private Map<String, Object> cacheObj;
	private OnClickListener clickListener;

	private AlertDialog.Builder builder;
	private AlertDialog dialog;

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

	public List<Map<String, Object>> getParams() {
		return params;
	}

	public void setParams(List<Map<String, Object>> params) {
		this.params = params;
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

	public AlertDialogWindow(List<Map<String, Object>> params, Context context,
			OnClickListener clickListener) {
		super();
		this.params = params;
		this.context = context;
		this.clickListener = clickListener;
		createMenuWin();
	}

	/**
	 * 构造出菜单并显示
	 */
	public void createMenuWin() {
		// 高度的基准值，这里没法用自动适应，所以设置了一个值。
		int baseHeight = 80;
		LayoutParams layoutParams2 = new LayoutParams(
				BaseUtils.getScreenWidth(context), baseHeight * params.size());
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setLayoutParams(layoutParams2);

		LayoutParams layoutParams = new LayoutParams(new Float(
				BaseUtils.getScreenWidth(context) * 0.9f).intValue(),
				baseHeight);

		for (int i = 0; i < params.size(); i++) {
			Map<String, Object> param = params.get(i);
			Button btn = new Button(context);
			btn.setText(param.get("name").toString());
			btn.setId(Integer.valueOf(param.get("id").toString()));
			btn.setLayoutParams(layoutParams);
			linearLayout.addView(btn);
			btn.setOnClickListener(clickListener);
		}

		builder = new AlertDialog.Builder(context);
		dialog = builder.setView(linearLayout).create();

		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.mystyle); // 添加动画
		// dialog.show();
	}
}
