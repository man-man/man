package com.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;

import com.app.man.R;
import com.app.util.DensityUtil;

public class LoginB extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// setContentView(R.layout.login_b);

		DensityUtil.initWindow(getWindowManager());

		final View startView = View.inflate(this, R.layout.login_b, null);
		setContentView(startView);
		// 渐变
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(1000);
		startView.setAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				goMain();
			}
		});

		Button loginBt = (Button) findViewById(R.id.goddess_bt);
		Button weiboBt = (Button) findViewById(R.id.weibo_bt);
		Button registerBt = (Button) findViewById(R.id.register_bt);

		loginBt.setOnClickListener(this);
		weiboBt.setOnClickListener(this);
		registerBt.setOnClickListener(this);
	}

	private void goMain() {
		Intent intent = new Intent(this, Login.class);
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
}
