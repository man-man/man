package com.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.app.man.R;
import com.app.util.DensityUtil;

/**
 * 评选按钮
 * 
 * @author 王灵
 * 
 */
public class VoteButtonView extends View {

	/**
	 * 外圆边框宽度（dp）
	 */
	private final static float BORDER_WIDTH = 4;

	/**
	 * 外圆与内圆距离（dp）
	 */
	private final static float GAP = 2;

	/**
	 * 字体大小（dp）
	 */
	private final static float TEXT_SIZE = 13;

	/**
	 * 画笔对象的引用
	 */
	private Paint paint;

	public VoteButtonView(Context context) {
		super(context, null);
	}

	public VoteButtonView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
	}

	public VoteButtonView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.WomanRankNum);

		// 获取自定义属性和默认值
		// bgColor = mTypedArray.getColor(R.styleable.WomanRankNum_bgColor,
		// Color.BLACK);
		// text = mTypedArray.getString(R.styleable.WomanRankNum_text);
		// textSize =
		// mTypedArray.getDimension(R.styleable.WomanRankNum_textSize,
		// 12);
		// textColor = mTypedArray.getColor(R.styleable.WomanRankNum_textColor,
		// Color.WHITE);
		//
		// mTypedArray.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		float width = Math.min(getWidth(), getHeight()); // 组件的大小
		float borderWid = DensityUtil.dip2px(BORDER_WIDTH); // 外圆的边框宽度（像素）
		float gapPx = DensityUtil.dip2px(GAP); // 空隙（像素）
		float wR = width / 2f - borderWid / 2f; // 外圆半径
		float nR = width / 2f - borderWid - gapPx; // 内圆半径
		float textX = (width - DensityUtil.dip2px(TEXT_SIZE) * 3) / 2f;

		// 外圆渐变
		LinearGradient lg = new LinearGradient(width / 2f, width, width / 2f,
				0, 0xffffffff, 0xffe9e9e9, Shader.TileMode.MIRROR);

		paint = new Paint();
		paint.setShader(lg);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(borderWid);
		paint.setAntiAlias(true); // 去掉画笔的锯齿效果
		canvas.drawCircle(width / 2f, width / 2f, wR, paint);

		// 内圆渐变
		LinearGradient lg2 = new LinearGradient(width / 2f, 0, width / 2f,
				width, 0xffffffff, 0xffe9e9e9, Shader.TileMode.MIRROR);

		paint.setShader(lg2);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(width / 2f, width / 2f, nR, paint);

		// 画文本
		Paint pp = new Paint();
		pp.setColor(0xff2f2f2f);
		pp.setTextSize(DensityUtil.dip2px(TEXT_SIZE));
		canvas.drawText("喜欢她", textX, width / 2f + 6, pp);

	}

}
