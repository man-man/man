package com.app.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.app.common.BaseUtils;
import com.app.common.HandlerUtils;
import com.app.man.R;
import com.app.util.ContextUtil;

/**
 * 自定义imageview，可显示网络图片，可定义圆角
 */
public class NetImageView extends ImageView {

	// public static List<MyImgHandler> NET_IMAGE_LOOPER_LIST = new
	// ArrayList<MyImgHandler>();
	// public static List<Bitmap> NET_IMAGE_BITMAP_LIST = new
	// ArrayList<Bitmap>();

	private Integer imgResizeWidth = BaseUtils
			.getScreenWidth(this.getContext()) / 4;
	private Integer imgResizeHeight = BaseUtils.getScreenHeight(this
			.getContext()) / 4;

	public static Object IMAGE_URL_LOCK = new Object();

	private static int NET_TIMEOUT = 30000;

	/**
	 * 圆角类型常量值
	 */
	public static final int[] CORNER_TYPES = new int[] { 0, // 所有角均为圆角
			1 // 上面两个角为圆角
	};

	/**
	 * 圆角类型
	 */
	private int cornerType;

	/**
	 * 圆角半径(dp)
	 */
	private float cornerRadius;

	/**
	 * 图片是否为方形（高随宽）
	 */
	private boolean isRect;

	/**
	 * 图片高占宽的比例
	 */
	private float heightOfWidth;

	/**
	 * 网络url
	 */
	private String netUrl;

	MyImgHandler handler;

	public class MyImgHandler extends Handler {
		private boolean isStart = true;

		public MyImgHandler(boolean isStart) {
			super();
			this.isStart = isStart;
		}

		public MyImgHandler(Looper looper, boolean isStart) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			if (isStart = false) {
				System.out
						.println(Thread.currentThread().getId() + "无需执行，直接退出");
				return;
			}
			super.handleMessage(msg);
		}

		public boolean isStart() {
			return isStart;
		}

		public void setStart(boolean isStart) {
			this.isStart = isStart;
		}

	}

	public NetImageView(Context context) {
		this(context, null);
	}

	public NetImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NetImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		HandlerThread tmp = new HandlerThread(new Date().getTime() + "");
		tmp.start();
		handler = new MyImgHandler(tmp.getLooper(), true) {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				byte[] data = (byte[]) msg.obj;
				if (data != null) {
					Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
							data.length);
					System.out.println(Thread.currentThread().getId()
							+ "该图片压缩前： " + Bitmap2BytesPng(bitmap).length);
					// setImageBitmap(toRoundCorner(bitmap, 10));// 显示图片
					float bitMapWidth = bitmap.getWidth();
					float bitMapHeight = bitmap.getHeight();

					Options bitmapFactoryOptions = new BitmapFactory.Options();

					Context fatherContext = NetImageView.this.getContext();
					int halfScreenWidth = BaseUtils
							.getScreenWidth(fatherContext) / 2;
					if (halfScreenWidth < bitMapWidth) {
						// bitmap = bitmapToScaleBitmap(bitmap, halfScreenWidth,
						// new Float(bitMapHeight
						// * (halfScreenWidth / bitMapWidth))
						// .intValue());
						System.out.println(halfScreenWidth / bitMapWidth);
						bitmap = small(
								bitmap,
								halfScreenWidth / bitMapWidth < 1 ? halfScreenWidth
										/ bitMapWidth
										: 1);
						System.out.println(Thread.currentThread().getId()
								+ "该图片压缩后： " + Bitmap2BytesPng(bitmap).length);
						// if(!bitmap.isRecycled()){
						// bitmap.recycle();
						// System.gc();
						// }
					}
					// NET_IMAGE_BITMAP_LIST.add(bitmap);
					mainhandler.sendMessage(handler.obtainMessage(21, bitmap));

					// setImageBitmap(bitmap);
				}

			}

		};

		// NET_IMAGE_LOOPER_LIST.add(handler);

		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.NetImageView);

		// 获取自定义属性和默认值
		cornerType = mTypedArray.getInteger(
				R.styleable.NetImageView_cornerType, CORNER_TYPES[0]);
		cornerRadius = mTypedArray.getDimension(
				R.styleable.NetImageView_cornerRadius, 0);
		netUrl = mTypedArray.getString(R.styleable.NetImageView_netUrl);
		isRect = mTypedArray.getBoolean(R.styleable.NetImageView_isRect, false);
		heightOfWidth = mTypedArray.getFloat(
				R.styleable.NetImageView_heightOfWidth, 0);

		mTypedArray.recycle();

		if (netUrl != null) {
			startLoadImg();
		}
	}

	@Override
	public void layout(int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.layout(l, t, r, b);
		LayoutParams params = (LayoutParams) this.getLayoutParams();

		// 图片为矩形
		if (isRect) {
			params.height = getWidth();
		}

		if (heightOfWidth != 0) {
			params.height = (int) (getWidth() * heightOfWidth + 0.5);
		}

		this.setLayoutParams(params);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable maiDrawable = getDrawable();
		float mCornerRadius = cornerRadius
				* getContext().getResources().getDisplayMetrics().density; // 圆角半径
		if (maiDrawable instanceof BitmapDrawable && mCornerRadius > 0) {
			Paint paint = ((BitmapDrawable) maiDrawable).getPaint();
			final int color = 0xff000000;

			float wid = getWidth();
			float hei = getHeight();

			switch (cornerType) {
			case 0:
				break;
			case 1:
				hei *= 2;
			default:
				break;
			}

			final RectF rectF = new RectF(0, 0, wid, hei);
			int saveCount = canvas.saveLayer(rectF, null,
					Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
							| Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
							| Canvas.FULL_COLOR_LAYER_SAVE_FLAG
							| Canvas.CLIP_TO_LAYER_SAVE_FLAG);

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, mCornerRadius, mCornerRadius, paint);

			Xfermode oldMode = paint.getXfermode();
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			super.onDraw(canvas);
			paint.setXfermode(oldMode);
			canvas.restoreToCount(saveCount);
		} else {
			super.onDraw(canvas);
		}
	}

	public static byte[] Bitmap2BytesPng(Bitmap bm) {
		byte[] a = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (bm != null) {
			try {
				bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
				a = baos.toByteArray();
			} catch (OutOfMemoryError v) {
				v.printStackTrace();
			}
		}

		try {
			if (baos != null)
				baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return a;
	}

	Handler mainhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Bitmap data = (Bitmap) msg.obj;
			if (data != null) {
				try {
					setImageBitmap(data);
				} catch (Exception e) {
					System.out.println("image add error");
				}

			}

		}

	};

	private static Bitmap small(Bitmap bitmap, float scale) {
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	public static Bitmap bitmapToScaleBitmap(Bitmap bm, int newWidth,
			int newHeight) {

		if (bm == null)
			return null;

		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();

		// 计算缩放比例
		float scaleWidth = (float) newWidth / Math.max(width, height);
		float scaleHeight = (float) newHeight / Math.max(width, height);
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);
		return newbm;
	}

	/**
	 * 启动加载图片线程
	 */
	private void startLoadImg() {
		// new Thread(new Runnable() {
		// public void run() {
		// handler.sendMessage(handler.obtainMessage(21, httpServer()));
		// }
		// }).start();
		ContextUtil.COMMON_PICASSO.load(netUrl)
				.resize(imgResizeWidth, imgResizeHeight).centerCrop()
				.into(this);
	}

	private byte[] httpServer() {
		HttpURLConnection conn = null;
		InputStream is = null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			conn = (HttpURLConnection) new URL(netUrl).openConnection();
			// TODO 这里为每个图片的http的超时设置为了5s
			conn.setConnectTimeout(5000);
			// conn.setConnectTimeout(NET_TIMEOUT*4);

			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == 200) {
				is = ((URLConnection) conn).getInputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					os.write(buffer, 0, len);
				}
				return os.toByteArray();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public int getCornerType() {
		return cornerType;
	}

	public void setCornerType(int cornerType) {
		this.cornerType = cornerType;
	}

	public float getCornerRadius() {
		return cornerRadius;
	}

	public void setCornerRadius(float cornerRadius) {
		this.cornerRadius = cornerRadius;
	}

	public String getNetUrl() {
		return netUrl;
	}

	public void setNetUrl(String netUrl) {
		this.netUrl = netUrl;
		startLoadImg();
	}

	public boolean isRect() {
		return isRect;
	}

	public void setRect(boolean isRect) {
		this.isRect = isRect;
	}

	public Integer getImgResizeWidth() {
		return imgResizeWidth;
	}

	public void setImgResizeWidth(Integer imgResizeWidth) {
		this.imgResizeWidth = imgResizeWidth;
	}

	public Integer getImgResizeHeight() {
		return imgResizeHeight;
	}

	public void setImgResizeHeight(Integer imgResizeHeight) {
		this.imgResizeHeight = imgResizeHeight;
	}

}