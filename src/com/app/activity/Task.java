package com.app.activity;

import com.app.man.R;
import com.app.man.R.layout;
import com.app.man.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;

public class Task extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.task);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task, menu);
		return true;
	}

}
