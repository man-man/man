package com.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.app.man.R;

/**
 * 排行榜数字view
 * 
 * @author 王灵
 * 
 */
public class RankNumView extends View {

	/**
	 * 背景色
	 */
	private int bgColor;
	
	/**
	 * 文本
	 */
	private String text;

	/**
	 * 字体颜色
	 */
	private int textColor;
	
	/**
	 * 字体大小
	 */
	private float textSize;

	/**
	 * 画笔对象的引用
	 */
	private Paint paint;

	public RankNumView(Context context) {
		this(context, null);
	}

	public RankNumView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RankNumView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		paint = new Paint();

		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.WomanRankNum);

		// 获取自定义属性和默认值
		bgColor = mTypedArray.getColor(R.styleable.WomanRankNum_bgColor,
				Color.BLACK);
		text = mTypedArray.getString(R.styleable.WomanRankNum_text);
		textSize = mTypedArray.getDimension(R.styleable.WomanRankNum_textSize, 12);
		textColor = mTypedArray.getColor(R.styleable.WomanRankNum_textColor,
				Color.WHITE);

		mTypedArray.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 画三角形
		paint.setColor(bgColor);
		paint.setStyle(Paint.Style.FILL);
		Path path = new Path();
		path.moveTo(0, 0);// 此点为多边形的起点
		path.lineTo(getWidth(), 0);
		path.lineTo(0, getHeight());
		path.close(); // 使这些点构成封闭的多边形
		canvas.drawPath(path, paint);

		// 画文本
		paint.setColor(textColor);
		paint.setTextSize(textSize);
		canvas.drawText(text, 5, getHeight() / 2, paint);

	}
	
	public int getBgColor() {
		return bgColor;
	}

	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

}
