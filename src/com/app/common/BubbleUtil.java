package com.app.common;

import android.content.Context;
import android.widget.Toast;

public class BubbleUtil {

	/**
	 * 弹出提示信息
	 * 
	 * @param context
	 * @param msg
	 */
	public static void alertMsg(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	public static void alertMsg(Context context, int msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

}
