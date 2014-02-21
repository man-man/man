package com.app.common;

import android.os.HandlerThread;
import android.os.Looper;

public class HandlerUtils {
	
	public static HandlerThread BASE_HANDLERTHREAD = new HandlerThread(
			"my_common_handlerthread");
	static {
		BASE_HANDLERTHREAD.start();
	}
	
	public static Looper getLooper(){
		return BASE_HANDLERTHREAD.getLooper();
	}
}
