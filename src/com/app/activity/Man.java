package com.app.activity;

import com.app.man.R;
import com.app.man.R.layout;
import com.app.man.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Man extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.man);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.man, menu);
		return true;
	}

}
