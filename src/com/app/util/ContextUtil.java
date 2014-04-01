package com.app.util;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import android.app.Application;

public class ContextUtil extends Application {
	private static ContextUtil instance;

	public static Picasso COMMON_PICASSO;

	public static ContextUtil getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// int maxMemory = (int) Runtime.getRuntime().maxMemory();
		// int cacheSize = maxMemory / 2;
		LruCache lruCache = new LruCache(this);
		COMMON_PICASSO = new Picasso.Builder(getApplicationContext())
				.memoryCache(lruCache).build();
//		COMMON_PICASSO.setDebugging(true);
		instance = this;
	}
}