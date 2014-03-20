package com.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.app.man.R;

public class LoginB extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_b);

		Button loginBt = (Button) findViewById(R.id.goddess_bt);
		Button weiboBt = (Button) findViewById(R.id.weibo_bt);
		Button registerBt = (Button) findViewById(R.id.register_bt);

		loginBt.setOnClickListener(this);
		weiboBt.setOnClickListener(this);
		registerBt.setOnClickListener(this);

		goMain();
	}

	private void goMain() {
		Intent intent = new Intent(this, Mine.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		Class<?> cls = null;

		switch (v.getId()) {
		case R.id.goddess_bt: // 女神登陆
			cls = Login.class;
			break;
		case R.id.weibo_bt: // 微博登陆
			cls = Login.class;
			break;
		case R.id.register_bt: // 注册
			cls = Login.class;
			break;
		}
		if (cls != null) {
			startActivity(new Intent(this, cls));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_b, menu);
		return true;
	}

}
