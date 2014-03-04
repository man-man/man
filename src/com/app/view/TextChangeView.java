package com.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.man.R;

public class TextChangeView extends LinearLayout {

	private TypedArray typedArray;

	private String textLable;
	private String[] arr;
	private Integer startIndex;
	private String startValue;
	private int arrSize;

	private Button text_change_up;
	private Button text_change_down;
	private TextView text_change_text;
	private TextView text_change_label;

	public String[] getArr() {
		return arr;
	}

	public void setArr(String[] arr) {
		this.arr = arr;
		if (arr != null) {
			this.arrSize = arr.length;
		}
	}

	public String getTextLable() {
		return textLable;
	}

	public void setTextLable(String textLable) {
		this.textLable = textLable;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public void setStartValue(String startValue) {
		this.startValue = startValue;
	}

	public Integer getStartIndex() {
		return startIndex;
	}

	public String getStartValue() {
		return startValue;
	}

	/**
	 * 供外部调用，获取当前组件的选中值
	 * 
	 * @return
	 */
	public CharSequence getCurTextVal() {
		return text_change_text.getText();
	}

	/**
	 * 供外部调用，修改当前的index索引值
	 * 
	 * @param index
	 */
	public void changeTextByIndex(int index) {
		this.startIndex = index;
		setTextChange();
	}

	/**
	 * 供外部调用，根据传入的要求值,修改当前的index索引值
	 * 
	 * @param value
	 */
	public void changeTextByVal(String value) {
		this.startValue = value;
		validateValue();
		setTextChange();
	}

	public TextChangeView(Context context) {
		super(context);
	}

	public TextChangeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.TextChangeView);
	}

	private void setTextChange() {
		validateStrartIndex();
		if (arr != null && startIndex != null) {
			text_change_text.setText(arr[startIndex]);
		}
	}

	private void validateValue() {
		if (startValue != null && startValue.trim().length() > 0) {
			for (int i = 0; i < arrSize; i++) {
				String tmp = arr[i];
				if (tmp.equals(startValue)) {
					startIndex = i;
					break;
				}
			}
		}
	}

	private void validateStrartIndex() {
		if (startIndex == null || startIndex.intValue() < 0) {
			startIndex = 0;
		}
		if (startIndex != null && startIndex.intValue() >= arrSize) {
			startIndex = arrSize - 1;
		}
	}

	private void setValue() {
		if (typedArray != null) {
			textLable = typedArray
					.getString(R.styleable.TextChangeView_textLable);
			text_change_label.setText(textLable);
			startIndex = typedArray.getInteger(
					R.styleable.TextChangeView_textArraysStartIndex, 0);
			startValue = typedArray
					.getString(R.styleable.TextChangeView_textArraysStartValue);
			int r = typedArray.getResourceId(
					R.styleable.TextChangeView_textArrays,
					R.array.sanwei_arrays_1);
			if (r == 0) {
				throw new IllegalArgumentException(
						"The content attribute is required and must refer "
								+ "to a valid child.");
			}
			// 这里用intArray的结果是一堆0，不知道为什么，但是StringArray就可以
			arr = getResources().getStringArray(r);
			arrSize = arr.length;
			typedArray.recycle();
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		setupViews();
	}

	private void setupViews() {
		LayoutInflater.from(getContext()).inflate(R.layout.text_change, this);
		LinearLayout linearLayout = (LinearLayout) this.getChildAt(0);

		text_change_up = (Button) linearLayout.getChildAt(1);
		text_change_down = (Button) linearLayout.getChildAt(3);
		text_change_text = (TextView) linearLayout.getChildAt(2);
		text_change_label = (TextView) linearLayout.getChildAt(0);

		setValue();
		validateValue();
		setTextChange();
		text_change_up.setOnClickListener(new TextChangeOnClickListener(0));
		text_change_down.setOnClickListener(new TextChangeOnClickListener(1));

	}

	class TextChangeOnClickListener implements OnClickListener {

		public TextChangeOnClickListener(int index) {
			super();
			this.index = index;
		}

		private int index;

		@Override
		public void onClick(View v) {
			if (index == 0) {
				startIndex++;
				setTextChange();
			}
			if (index == 1) {
				startIndex--;
				setTextChange();
			}
		}

	}
}
