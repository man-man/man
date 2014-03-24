package com.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.app.man.R;
import com.app.view.CityChangeView;
import com.app.view.TextChangeView;

/**
 * 城市的切换
 * 
 * @author 王灵
 * 
 */
public class CityChange extends BaseActivity {

	private Button city_change_btn_ok;
	private Button city_change_btn_cancel;
	private CityChangeView city_change_v1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.city_activi_call);
		city_change_btn_ok = (Button) findViewById(R.id.city_change_btn_ok);
		city_change_v1 = (CityChangeView) findViewById(R.id.city_change_v1);
		city_change_btn_cancel = (Button) findViewById(R.id.city_change_btn_cancel);
		city_change_btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String city = city_change_v1.getCity_change_city().getText()
						.toString();
				Intent intent = new Intent();
				intent.putExtra("city", city);
				setResult(4, intent);
				CityChange.this.finish();
			}
		});

		city_change_btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(4, intent);
				CityChange.this.finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign, menu);
		return true;
	}

}
