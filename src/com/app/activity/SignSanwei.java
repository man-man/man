package com.app.activity;

import java.util.LinkedList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.app.man.R;

/**
 * 三围的弹窗
 * 
 * @author 王灵
 * 
 */
public class SignSanwei extends BaseActivity {

	private Spinner sanwei_1;
	private Spinner sanwei_2;
	private Spinner sanwei_3;

	private ArrayAdapter adapter1;
	private ArrayAdapter adapter2;
	private ArrayAdapter adapter3;

	public static String[] sanwei_1_arr = {};

	public static String[] sanwei_2_arr = {};

	public static String[] sanwei_3_arr = {};

	static {
		LinkedList<String> list = new LinkedList<String>();
		for (int i = 10; i < 50; i++) {
			list.add(i + "");
		}
		sanwei_1_arr = list.toArray(sanwei_1_arr);
		sanwei_2_arr = list.toArray(sanwei_2_arr);
		sanwei_3_arr = list.toArray(sanwei_3_arr);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sign_win_sanwei);

		sanwei_1 = (Spinner) findViewById(R.id.sign_sanwei_1);
		sanwei_2 = (Spinner) findViewById(R.id.sign_sanwei_2);
		sanwei_3 = (Spinner) findViewById(R.id.sign_sanwei_3);

		// 将可选内容与ArrayAdapter连接起来
		adapter1 = ArrayAdapter.createFromResource(this,
				R.array.sanwei_arrays_1, android.R.layout.simple_spinner_item);

		// 设置下拉列表的风格
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// 将adapter 添加到spinner中
		sanwei_1.setAdapter(adapter1);
		sanwei_1.setSelection(20, true);

		// 添加事件Spinner事件监听
		sanwei_1.setOnItemSelectedListener(new SpinnerXMLSelectedListener(
				adapter1));

		// 设置默认值
		sanwei_1.setVisibility(View.VISIBLE);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			Integer sanwei1 = bundle.getInt("sanwei1");
			Integer sanwei2 = bundle.getInt("sanwei2");
			Integer sanwei3 = bundle.getInt("sanwei3");
			// if (sanwei1 != null) {
			// sanwei_1.setText(sanwei1.toString());
			// }
			// if (sanwei2 != null) {
			// sanwei_2.setText(sanwei2.toString());
			// }
			// if (sanwei3 != null) {
			// sanwei_3.setText(sanwei3.toString());
			// }
		}

	}

	// 使用XML形式操作
	class SpinnerXMLSelectedListener implements OnItemSelectedListener {
		private ArrayAdapter arrayAdapter;

		public SpinnerXMLSelectedListener(ArrayAdapter arrayAdapter) {
			super();
			this.arrayAdapter = arrayAdapter;
		}

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			System.out.println(arrayAdapter.getItem(arg2));
			// view2.setText("你使用什么样的手机：" + adapter2.getItem(arg2));
		}

		public void onNothingSelected(AdapterView<?> arg0) {

		}

	}

	// // 使用数组形式操作
	// class SpinnerSelectedListener implements OnItemSelectedListener {
	//
	// public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
	// long arg3) {
	// System.out.println(sanwei_1_arr[arg2]);
	// // view.setText("你的血型是：" + m[arg2]);
	// }
	//
	// public void onNothingSelected(AdapterView<?> arg0) {
	// }
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign, menu);
		return true;
	}

}
