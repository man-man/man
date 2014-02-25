package com.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.app.man.R;

/**
 * 我的
 * @author 王灵
 *
 */
public class Mine extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mine);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mine, menu);
		return true;
	}

}
