package com.app.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

public class ImageUtil {

	public static Bitmap createNewBitmapAndCompressByFile(String filePath,
			int wh[]) {
		int offset = 100;
		File file = new File(filePath);
		long fileSize = file.length();
		if (200 * 1024 < fileSize && fileSize <= 1024 * 1024)
			offset = 90;
		else if (1024 * 1024 < fileSize)
			offset = 85;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 为true里只读图片的信息，如果长宽，返回的bitmap为null
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inDither = false;
		/**
		 * 计算图片尺寸 //TODO 按比例缩放尺寸
		 */
		BitmapFactory.decodeFile(filePath, options);

		int bmpheight = options.outHeight;
		int bmpWidth = options.outWidth;
		int inSampleSize = bmpheight / wh[1] > bmpWidth / wh[0] ? bmpheight
				/ wh[1] : bmpWidth / wh[0];
		// if(bmpheight / wh[1] < bmpWidth / wh[0]) inSampleSize = inSampleSize
		// * 2 / 3;//TODO 如果图片太宽而高度太小，则压缩比例太大。所以乘以2/3
		if (inSampleSize > 1)
			options.inSampleSize = inSampleSize;// 设置缩放比例
		options.inJustDecodeBounds = false;

		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			return null;
		}
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(is, null, options);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
			bitmap = null;
		}
		if (offset == 100)
			return bitmap;// 缩小质量
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, offset, baos);
		byte[] buffer = baos.toByteArray();
		options = null;
		if (buffer.length >= fileSize)
			return bitmap;
		return BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
	}

	/**
	 * 第一：我们先看下质量压缩方法：
	 * 
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 第二：图片按比例大小压缩方法（根据路径获取图片并压缩）：
	 * 
	 * @param srcPath
	 * @return
	 */
	public static Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 第三：图片按比例大小压缩方法（根据Bitmap图片压缩）：
	 * 
	 * @param image
	 * @return
	 */
	public static Bitmap comp(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	public static String getFilePath(Context context, Uri uri) {
		// String[] imgs = { MediaStore.Images.Media.DATA };// 将图片URI转换成存储路径
		// Cursor cursor = this.managedQuery(uri, imgs, null, null, null);
		// int index =
		// cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		// cursor.moveToFirst();
		// String img_url = cursor.getString(index);
		String[] proj = { MediaStore.Images.Media.DATA };
		String imagePath = "";
		Cursor cursor = ((Activity) context).managedQuery(uri, proj,// 查哪一列
				null, null, null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				imagePath = cursor.getString(column_index);
			}
		}
		System.out.println(imagePath);
		return imagePath;
	}

	public static Bitmap getBitmapFromUri(Context context, Uri uri) {
		try {
			// 读取uri所在的图片
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(
					context.getContentResolver(), uri);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] getByteByBitmap(Bitmap bmp) {
		byte[] compressData = null;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
		// bmp.
		compressData = outStream.toByteArray();
		try {
			outStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return compressData;
	}
}
