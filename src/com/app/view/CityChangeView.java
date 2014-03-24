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

public class CityChangeView extends LinearLayout {

	private TypedArray typedArray;

	private String textLable;
	private String[] arr;
	private Integer startIndex;
	private String startValue;
	private int arrSize;

	private Button city_change_btn_before;
	private Button city_change_btn_next;
	private TextView city_change_city;

	public String[] getArr() {
		return arr;
	}

	public TypedArray getTypedArray() {
		return typedArray;
	}

	public void setTypedArray(TypedArray typedArray) {
		this.typedArray = typedArray;
	}

	public int getArrSize() {
		return arrSize;
	}

	public void setArrSize(int arrSize) {
		this.arrSize = arrSize;
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
		return city_change_city.getText();
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

	public Button getCity_change_btn_before() {
		return city_change_btn_before;
	}

	public void setCity_change_btn_before(Button city_change_btn_before) {
		this.city_change_btn_before = city_change_btn_before;
	}

	public Button getCity_change_btn_next() {
		return city_change_btn_next;
	}

	public void setCity_change_btn_next(Button city_change_btn_next) {
		this.city_change_btn_next = city_change_btn_next;
	}

	public TextView getCity_change_city() {
		return city_change_city;
	}

	public void setCity_change_city(TextView city_change_city) {
		this.city_change_city = city_change_city;
	}

	public CityChangeView(Context context) {
		super(context);
	}

	public CityChangeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.TextChangeView);
	}

	private void setTextChange() {
		validateStrartIndex();
		if (arr != null && startIndex != null) {
			city_change_city.setText(arr[startIndex]);
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
		LayoutInflater.from(getContext()).inflate(R.layout.city_change, this);
		LinearLayout linearLayout = (LinearLayout) this.getChildAt(0);

		city_change_btn_before = (Button) linearLayout
				.findViewById(R.id.city_change_btn_before);
		city_change_btn_next = (Button) linearLayout
				.findViewById(R.id.city_change_btn_next);
		city_change_city = (TextView) linearLayout
				.findViewById(R.id.city_change_city);

		setValue();
		validateValue();
		setTextChange();
		city_change_btn_before
				.setOnClickListener(new TextChangeOnClickListener(0));
		city_change_btn_next
				.setOnClickListener(new TextChangeOnClickListener(1));

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
