package com.app.activity;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.common.BaseUtils;
import com.app.man.R;

/**
 * 我的
 * 
 * @author 王灵
 * 
 */
public class Mine extends Activity {

	private RelativeLayout mine_btn1;
	private RelativeLayout mine_btn2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mine2);

		// mine_btn1 = (RelativeLayout) findViewById(R.id.mine_btn1);
		// mine_btn2 = (RelativeLayout) findViewById(R.id.mine_btn2);
		// RelativeLayoutOnClickListener clickListener = new
		// RelativeLayoutOnClickListener();
		// mine_btn1.setOnClickListener(clickListener);
		// mine_btn2.setOnClickListener(clickListener);
		// Map<String, Object> curMap = BaseUtils.CUR_USER_MAP;
		// if (curMap == null) {
		// Toast.makeText(this, R.string.base_user_session_empty,
		// Toast.LENGTH_SHORT).show();
		// Intent intent = new Intent(this, Login.class);
		// startActivity(intent);
		// }else{
		//
		// }

		// Intent intent = new Intent();
		// Bundle bundle = new Bundle();
		// bundle.putInt("sanwei1", 33);
		// intent.setClass(this, Test.class);
		// startActivity(intent);
	}

	public class RelativeLayoutOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Toast.makeText(Mine.this, "点击了菜单", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mine, menu);
		return true;
	}

}
