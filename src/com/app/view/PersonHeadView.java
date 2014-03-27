package com.app.view;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.man.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class PersonHeadView extends LinearLayout {

	Context context;

	// 控件
	private NetImageView bigBgView; // 大背景图
	private NetImageView headLogoView; // 用户头像
	private Button signBtn; // 签到
	private TextView nameView; // 姓名 性别
	private TextView scoreView; // 积分
	private TextView ageView; // 年龄
	private TextView cityView; // 城市

	private JSONObject data; // 数据

	public PersonHeadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		setupViews();
	}

	public PersonHeadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setupViews();
	}

	public PersonHeadView(Context context) {
		super(context);
		this.context = context;
		setupViews();
	}

	private void setupViews() {
		LayoutInflater.from(context).inflate(R.layout.person_head, this);

		bigBgView = (NetImageView) findViewById(R.id.person_bg_img);
		headLogoView = (NetImageView) findViewById(R.id.person_user_img);
		signBtn = (Button) findViewById(R.id.person_sign);
		nameView = (TextView) findViewById(R.id.person_username);
		scoreView = (TextView) findViewById(R.id.person_score);
		ageView = (TextView) findViewById(R.id.person_age);
		cityView = (TextView) findViewById(R.id.person_city);

	}

	/**
	 * 渲染数据
	 */
	private void rendData() {
		try {
			bigBgView.setNetUrl(data.getString("backgroundImageUrl"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			headLogoView.setNetUrl(data.getString("imageUrl"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			scoreView.setText(data.getString("score"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			ageView.setText(data.getString("age"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			cityView.setText(data.getString("girlCity"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			Drawable sexImg;
			Resources res = getResources();
			if ((data.getString("gender")).equals("1")) {
				sexImg = res.getDrawable(R.drawable.sex_boy);
			} else {
				sexImg = res.getDrawable(R.drawable.sex_girl);
			}
			// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
			sexImg.setBounds(0, 0, sexImg.getMinimumWidth(),
					sexImg.getMinimumHeight());
			nameView.setCompoundDrawables(null, null, sexImg, null);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			nameView.setText(data.getString("name"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
		if (data != null) {
			rendData();
		}
	}

}
