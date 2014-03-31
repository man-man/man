package com.app.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.app.util.DensityUtil;
import com.squareup.picasso.Picasso;

public class BaseUtils {

	public static Boolean IS_PLAY_MP3 = false;

	public static String CUR_PLAY_MP3_URL = "";

	public static Picasso COMMON_PICASSO;

	public static Map<String, Object> CUR_USER_MAP = null;

	private static ImageLoader imageLoader = ImageLoader.getInstance();

	/**
	 * 获取屏幕的宽度
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getWidth();
	}

	/**
	 * 获取屏幕的高度
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight();
	}

	/**
	 * 将图片下载到SD卡缓存起来。
	 * 
	 * @param imageUrl
	 *            图片的URL地址。
	 */
	public static String downloadImage(String imageUrl, int width) {
		String filePath = null;
		HttpURLConnection con = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		File imageFile = null;
		try {
			URL url = new URL(imageUrl);
			con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(5 * 1000);
			con.setReadTimeout(5 * 1000);
			con.setDoInput(true);
			con.setDoOutput(true);
			bis = new BufferedInputStream(con.getInputStream());
			imageFile = new File(getImagePath(imageUrl));
			filePath = imageFile.getPath();
			fos = new FileOutputStream(imageFile);
			bos = new BufferedOutputStream(fos);
			byte[] b = new byte[1024];
			int length;
			while ((length = bis.read(b)) != -1) {
				bos.write(b, 0, length);
				bos.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
				if (con != null) {
					con.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (imageFile != null) {
			Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(
					filePath, width);
			if (bitmap != null) {
				imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
			}
		}
		// System.out.println(filePath);
		return filePath;
	}

	/**
	 * 获取图片的本地存储路径。
	 * 
	 * @param imageUrl
	 *            图片的URL地址。
	 * @return 图片的本地存储路径。
	 */
	public static String getImagePath(String imageUrl) {
		int lastSlashIndex = imageUrl.lastIndexOf(".");
		String httpPath = imageUrl.substring(0, lastSlashIndex);
		String md5Key = MD5Util.MD5(httpPath);
		String imageName = md5Key + "_" + httpPath.hashCode() + "."
				+ imageUrl.substring(lastSlashIndex + 1);
		String imageDir = Environment.getExternalStorageDirectory().getPath()
				+ "/ImageCache/";
		File file = new File(imageDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		String imagePath = imageDir + imageName;
		return imagePath;
	}

	/**
	 * 为view添加marginBottom
	 */
	public static ViewGroup addMarginBottom(ViewGroup view, float dp) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		params.setMargins(0, 0, 0, DensityUtil.dip2px(dp));
		view.setLayoutParams(params);
		return view;
	}

}
