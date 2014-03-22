package com.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.app.man.R;
import com.app.view.TextChangeView;

/**
 * 三围的弹窗
 * 
 * @author 王灵
 * 
 */
public class SignSanweiB extends BaseActivity {

	private TextChangeView sanwei_sanwei1;
	private TextChangeView sanwei_sanwei2;
	private TextChangeView sanwei_sanwei3;
	private Button sanwei_submit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sign_win_sanwei_b);

		sanwei_sanwei1 = (TextChangeView) findViewById(R.id.sanwei_sanwei1);
		sanwei_sanwei2 = (TextChangeView) findViewById(R.id.sanwei_sanwei2);
		sanwei_sanwei3 = (TextChangeView) findViewById(R.id.sanwei_sanwei3);
		sanwei_submit = (Button) findViewById(R.id.sanwei_submit);
		sanwei_submit.setOnClickListener(new SubmitOnclickListener());
	}

	class SubmitOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String sanwei1 = sanwei_sanwei1.getText_change_text().getText()
					.toString();
			String sanwei2 = sanwei_sanwei2.getText_change_text().getText()
					.toString();
			String sanwei3 = sanwei_sanwei3.getText_change_text().getText()
					.toString();
			Intent intent = new Intent();
			intent.putExtra("sanwei1", sanwei1);
			intent.putExtra("sanwei2", sanwei2);
			intent.putExtra("sanwei3", sanwei3);
			// intent.setClass(SignSanweiB.this, SignInfo.class);
			setResult(3, intent);
			SignSanweiB.this.finish();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign, menu);
		return true;
	}

}
