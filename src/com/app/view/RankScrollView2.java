package com.app.view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.common.ImageLoader;
import com.app.man.R;
import com.app.model.WomanItemModel;

/**
 * 自定义的ScrollView2，修复ScrollView问题
 * 1.ScrollView,根据图片去渲染item，如果某个item没有图片就不会被显示,这样是不行滴
 * 2.排行榜，对排行要求较高，之前是哪个图片先下载下来，先把哪个图片放入容器，这样会影响排行 3.剔除瀑布流的多余逻辑
 * 4.想在该组件中把图片加载逻辑提出来，以便公用
 * 
 */
public class RankScrollView2 extends ScrollView {

	/**
	 * 排行榜数据
	 */
	private List<WomanItemModel> models = new ArrayList<WomanItemModel>();

	/**
	 * 每一列的宽度
	 */
	private int columnWidth;

	/**
	 * 是否已加载过一次layout，这里onLayout中的初始化只需加载一次
	 */
	private boolean loadOnce;

	/**
	 * 对图片进行管理的工具类
	 */
	private ImageLoader imageLoader;

	/**
	 * 第一列的布局
	 */
	private LinearLayout firstColumn;

	/**
	 * 第二列的布局
	 */
	private LinearLayout secondColumn;

	/**
	 * 记录所有正在下载或等待下载的任务。
	 */
	private static Set<LoadImageTask> taskCollection;

	/**
	 * MyScrollView的构造函数。
	 * 
	 * @param context
	 * @param attrs
	 */
	public RankScrollView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		imageLoader = ImageLoader.getInstance();
		taskCollection = new HashSet<LoadImageTask>();
	}

	/**
	 * 填充数据
	 */
	public void setData(List<WomanItemModel> models, String[] imgs) {
		this.models = models;
		rendItems();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		if (changed && !loadOnce) {
			firstColumn = (LinearLayout) findViewById(R.id.rank_first_column);
			secondColumn = (LinearLayout) findViewById(R.id.rank_second_column);
			columnWidth = firstColumn.getWidth();
			loadOnce = true;

			rendItems();
			System.out.println("-----------onLayout---firstColumn------"
					+ firstColumn);
		}
	}

	/**
	 * 渲染元素
	 */
	private void rendItems() {
		int len = models.size();

		if (len == 0) {
			return;
		}
		if (firstColumn == null || secondColumn == null) {
			return;
		}

		System.out.println("----------rendItems len:" + len);
		for (int i = 0; i < len; i++) {
			View item = getItemView(i);

			if (i % 2 == 0) {
				firstColumn.addView(item);
			} else {
				secondColumn.addView(item);
			}
		}

	}

	private View getItemView(int dataIndex) {
		View convertView = LayoutInflater.from(getContext()).inflate(
				R.layout.woman_rank_item, null);
		WomanItemModel model = models.get(dataIndex);
		ImageView imgView = null;
		String imgUrl = model.getImg();

		((TextView) convertView.findViewById(R.id.woman_rank_name))
				.setText(model.getName());
		((RankNumView) convertView.findViewById(R.id.woman_rank_num))
				.setText(model.getRank() + "");
		((TextView) convertView.findViewById(R.id.woman_rank_vote))
				.setText(model.getVote() + "");
		imgView = (ImageView) convertView.findViewById(R.id.woman_rank_img);

		if (!"".equals(imgUrl) && imgUrl != null) { // 异步加载图片
			LoadImageTask task = new LoadImageTask(imgView);
			taskCollection.add(task);
			task.execute(imgUrl);
		}
		return convertView;
	}

	/**
	 * 异步下载图片的任务。
	 * 
	 * @author guolin
	 */
	class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

		/**
		 * 图片的URL地址
		 */
		private String mImageUrl;

		/**
		 * 可重复使用的ImageView
		 */
		private ImageView mImageView;

		public LoadImageTask() {
		}

		/**
		 * 将可重复使用的ImageView传入
		 * 
		 * @param imageView
		 */
		public LoadImageTask(ImageView imageView) {
			mImageView = imageView;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			mImageUrl = params[0];
			Bitmap imageBitmap = imageLoader
					.getBitmapFromMemoryCache(mImageUrl);
			if (imageBitmap == null) {
				imageBitmap = loadImage(mImageUrl);
			}
			return imageBitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap != null) {
				// double ratio = bitmap.getWidth() / (columnWidth * 1.0);
				// int scaledHeight = (int) (bitmap.getHeight() / ratio);
				addImage(bitmap, columnWidth, columnWidth);
			}
			taskCollection.remove(this);
		}

		/**
		 * 根据传入的URL，对图片进行加载。如果这张图片已经存在于SD卡中，则直接从SD卡里读取，否则就从网络上下载。
		 * 
		 * @param imageUrl
		 *            图片的URL地址
		 * @return 加载到内存的图片。
		 */
		private Bitmap loadImage(String imageUrl) {
			File imageFile = new File(getImagePath(imageUrl));
			if (!imageFile.exists()) {
				downloadImage(imageUrl);
			}
			if (imageUrl != null) {
				Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(
						imageFile.getPath(), columnWidth);
				if (bitmap != null) {
					imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
					return bitmap;
				}
			}
			return null;
		}

		/**
		 * 向ImageView中添加一张图片
		 * 
		 * @param bitmap
		 *            待添加的图片
		 * @param imageWidth
		 *            图片的宽度
		 * @param imageHeight
		 *            图片的高度
		 */
		private void addImage(Bitmap bitmap, int imageWidth, int imageHeight) {
			if (mImageView != null) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						imageWidth, imageHeight);

				mImageView.setLayoutParams(params);
				mImageView.setScaleType(ScaleType.CENTER_CROP);
				mImageView.setImageBitmap(bitmap);
			}
		}

		/**
		 * 将图片下载到SD卡缓存起来。
		 * 
		 * @param imageUrl
		 *            图片的URL地址。
		 */
		private void downloadImage(String imageUrl) {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				Log.d("TAG", "monted sdcard");
			} else {
				Log.d("TAG", "has no sdcard");
			}
			HttpURLConnection con = null;
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			BufferedInputStream bis = null;
			File imageFile = null;
			try {
				URL url = new URL(imageUrl);
				con = (HttpURLConnection) url.openConnection();
				con.setConnectTimeout(5 * 1000);
				con.setReadTimeout(15 * 1000);
				con.setDoInput(true);
				con.setDoOutput(true);
				bis = new BufferedInputStream(con.getInputStream());
				imageFile = new File(getImagePath(imageUrl));
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
						imageFile.getPath(), columnWidth);
				if (bitmap != null) {
					imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
				}
			}
		}

		/**
		 * 获取图片的本地存储路径。
		 * 
		 * @param imageUrl
		 *            图片的URL地址。
		 * @return 图片的本地存储路径。
		 */
		private String getImagePath(String imageUrl) {
			int lastSlashIndex = imageUrl.lastIndexOf("/");
			String imageName = imageUrl.substring(lastSlashIndex + 1);
			String imageDir = Environment.getExternalStorageDirectory()
					.getPath() + "/PhotoWallFalls/";
			File file = new File(imageDir);
			if (!file.exists()) {
				file.mkdirs();
			}

			String imagePath = imageDir + imageName;
			Log.d("man", "----------imagePath:" + imagePath);
			return imagePath;
		}
	}

}