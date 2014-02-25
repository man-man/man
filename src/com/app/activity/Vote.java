package com.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;

import com.app.man.R;

/**
 * 评选
 * @author 王灵
 *
 */
public class Vote extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.vote);
	}

}
