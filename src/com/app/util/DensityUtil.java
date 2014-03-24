package com.app.util;

import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DensityUtil {
	
	public static int SCREEN_WIDTH = 0;
	public static int SCREEN_HEIGHT = 0;
	public static float SCREEN_DENSITY = 0;
	
	public static void initWindow(WindowManager wm){
		DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        SCREEN_WIDTH = metric.widthPixels;  // ��Ļ��ȣ����أ�
        SCREEN_HEIGHT = metric.heightPixels;  // ��Ļ�߶ȣ����أ�
        SCREEN_DENSITY = metric.density;  // ��Ļ�ܶȣ�0.75 / 1.0 / 1.5��
	}
	
	public static int dip2px(float dpValue) {
		System.out.println("----------------SCREEN_DENSITY:" + SCREEN_DENSITY);
		return (int) (dpValue * SCREEN_DENSITY);
	}
	
}
