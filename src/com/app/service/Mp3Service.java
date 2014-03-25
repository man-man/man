package com.app.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;

import com.app.man.R;

public class Mp3Service extends Service {

	public static final String STATE_PLAY = "play";
	public static final String STATE_PAUSE = "pause";
	public static final String STATE_STOP = "stop";

	private MediaPlayer mediaPlayer = new MediaPlayer(); // 媒体播放器对象
	private String path; // 音乐文件路径
	private boolean isPause; // 暂停状态
	private int percent = 0;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("---------service create--------");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mediaPlayer.isPlaying()) {
			stop();
		}
		path = intent.getStringExtra("url");
		String msg = intent.getStringExtra("MSG");

		if (STATE_PLAY.equals(msg)) {
			play(0);
			// playLocal();
		} else if (STATE_PAUSE.equals(msg)) {
			pause();
		} else if (STATE_STOP.equals(msg)) {
			stop();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public void playMusic() {
		mediaPlayer.start();
	}

	public void pauseMusic() {
		mediaPlayer.pause();
	}

	public void stopMusic() {
		mediaPlayer.stop();
		try {
			mediaPlayer.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 播放音乐
	 * 
	 * @param position
	 */
	private void play(int position) {
		try {
			mediaPlayer.reset();// 把各项参数恢复到初始状态
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepare(); // 进行缓冲
			mediaPlayer.setOnPreparedListener(new PreparedListener(position));// 注册一个监听器
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 播放本地音乐
	 * 
	 * @param position
	 */
	private void playLocal() {
		// try {
		// mediaPlayer = MediaPlayer.create(this, R.raw.yhyl);
		// // mediaPlayer.reset();// 把各项参数恢复到初始状态
		//
		// if (mediaPlayer != null) {
		// mediaPlayer.stop();
		// }
		// mediaPlayer.prepare(); // 进行缓冲
		// mediaPlayer.start();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * 暂停音乐
	 */
	private void pause() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			isPause = true;
		}
	}

	/**
	 * 停止音乐
	 */
	private void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			try {
				mediaPlayer.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
	}

	/**
	 * 
	 * 实现一个OnPrepareLister接口,当音乐准备好的时候开始播放
	 * 
	 */
	private final class PreparedListener implements OnPreparedListener {
		private int positon;

		public PreparedListener(int positon) {
			this.positon = positon;
		}

		@Override
		public void onPrepared(MediaPlayer mp) {
			mediaPlayer.start(); // 开始播放
			if (positon > 0) { // 如果音乐不是从头播放
				mediaPlayer.seekTo(positon);
			}
		}
	}

}
