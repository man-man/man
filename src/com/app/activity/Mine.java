package com.app.activity;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.app.common.BaseUtils;
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
		Map<String, Object> curMap = BaseUtils.CUR_USER_MAP;
		if (curMap == null) {
			Toast.makeText(this, R.string.base_user_session_empty,
					Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, Login.class);
			startActivity(intent);
		}else{
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mine, menu);
		return true;
	}

}
