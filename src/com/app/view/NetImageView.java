package com.app.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.app.man.R;

/**
 * 自定义imageview，可显示网络图片，可定义圆角
 */
public class NetImageView extends ImageView {

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

	public NetImageView(Context context) {
		this(context, null);
	}

	public NetImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NetImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.NetImageView);

		// 获取自定义属性和默认值
		cornerType = mTypedArray.getInteger(
				R.styleable.NetImageView_cornerType, CORNER_TYPES[0]);
		cornerRadius = mTypedArray.getDimension(
				R.styleable.NetImageView_cornerRadius, 0);
		netUrl = mTypedArray.getString(R.styleable.NetImageView_netUrl);
		isRect = mTypedArray.getBoolean(R.styleable.NetImageView_isRect, false);
		heightOfWidth = mTypedArray.getFloat(R.styleable.NetImageView_heightOfWidth, 0);

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
		
		//图片为矩形
		if (isRect) {
			params.height = getWidth();
		}
		
		if(heightOfWidth != 0){
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

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			byte[] data = (byte[]) msg.obj;
			if (data != null) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				// setImageBitmap(toRoundCorner(bitmap, 10));// 显示图片
				setImageBitmap(bitmap);
			}
		}

	};

	/**
	 * 启动加载图片线程
	 */
	private void startLoadImg() {
		new Thread(new Runnable() {
			public void run() {
				handler.sendMessage(handler.obtainMessage(21, httpServer()));
			}
		}).start();
	}

	private byte[] httpServer() {
		HttpURLConnection conn = null;
		InputStream is = null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			conn = (HttpURLConnection) new URL(netUrl).openConnection();
			conn.setConnectTimeout(5000);
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

}