package com.app.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.man.R;
import com.app.view.TitleView;

public class SignInfo extends BaseActivity {
	
	private TitleView titleView; //页头
	private LinearLayout dateLinear;
	private TextView dateText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sign_info);

		initView();
	}

	private void initView() {
		titleView = (TitleView) findViewById(R.id.sign_info_title);
		dateText = (TextView) findViewById(R.id.sign_info_date_text);
		dateLinear = (LinearLayout) findViewById(R.id.sign_info_date_linear);
		
		
		dateLinear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String date = (String) dateText.getText();
				String[] dateArr = date.split("-");

				// Calendar c = Calendar.getInstance();
				// 直接创建一个DatePickerDialog对话框实例，并将它显示出来
				new DatePickerDialog(SignInfo.this,
				// 绑定监听器
						new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								dateText.setText(year + "-" + (monthOfYear + 1)
										+ "-" + dayOfMonth);
							}
						}
						// 设置初始日期
						, Integer.parseInt(dateArr[0]), Integer
								.parseInt(dateArr[1]) - 1, Integer
								.parseInt(dateArr[2])).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_info, menu);
		return true;
	}

}
