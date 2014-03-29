package com.app.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.view.KeyEvent;
import android.widget.Toast;

public class BaseActivity extends Activity {
	// 用于双击退出
	private static boolean waitForExitFlag = false;
	private static final int WAIT_FOR_EXIT_DURATION = 2000; // 毫秒
	private static long waitForExitLastClickTs = -1; // 上次点击时间
	private static Timer waitForExitTaskTimer = new Timer();

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (this.getClass() == Woman.class || this.getClass() == Man.class
				|| this.getClass() == Mine.class) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				doDoubleClickExist();
				return false;
			}
		}
		super.onKeyDown(keyCode, event);
		return true;
	}

	public void doDoubleClickExist() {

		if (System.currentTimeMillis() - waitForExitLastClickTs < WAIT_FOR_EXIT_DURATION
				&& waitForExitFlag) {
			this.finish();
			System.exit(0);
		} else {
			Toast.makeText(this, "再点一次退出程序", Toast.LENGTH_SHORT).show();
			waitForExitLastClickTs = System.currentTimeMillis();
			waitForExitFlag = true;
			waitForExitTaskTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					waitForExitFlag = false;
				}
			}, WAIT_FOR_EXIT_DURATION);
		}
	}
}
