package com.app.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.common.BaseUtils;
import com.app.common.HttpCallBackHandler;
import com.app.common.HttpRequestUtils;
import com.app.man.R;

public class TreeInfo extends BaseActivity {

	Context context = this;

	JSONObject questionData;

	private ViewGroup treeInfoList;
	private TextView questionView;
	private TextView questionAuthorView;
	private TextView questionDateView;
	private TextView questionReplyNumView;
	private EditText answerInputView;
	private Button answerSubmitBtn;
	private ViewGroup curSupportView;

	boolean isSupport = true;

	TreeInfoHttpHandler treeInfoHttpHandler = new TreeInfoHttpHandler();
	WriteAnswerHttpHandler writeAnswerHttpHandler = new WriteAnswerHttpHandler();
	SupportAnswerHttpHandler supportAnswerHttpHandler = new SupportAnswerHttpHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tree_info);

		Bundle b = getIntent().getExtras();
		try {
			questionData = new JSONObject(b.getString("question"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		setupViews();
		getTreeInfoReq();
	}

	private void setupViews() {
		treeInfoList = (ViewGroup) findViewById(R.id.tree_info_list);
		questionView = (TextView) findViewById(R.id.tree_info_question);
		questionAuthorView = (TextView) findViewById(R.id.tree_info_question_author);
		questionDateView = (TextView) findViewById(R.id.tree_info_question_date);
		questionReplyNumView = (TextView) findViewById(R.id.tree_info_question_replyNum);
		answerInputView = (EditText) findViewById(R.id.tree_answer_input);
		answerSubmitBtn = (Button) findViewById(R.id.tree_answer_submit);

		answerSubmitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				writeAnwserReq();
			}
		});
	}

	private void rendItems(JSONArray arr) {

		treeInfoList.removeAllViews();

		for (int i = 0; i < arr.length(); i++) {
			try {
				LinearLayout view = (LinearLayout) BaseUtils.addMarginBottom(
						getView(arr.getJSONObject(i)), 10);

				treeInfoList.addView(view);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		try {
			questionView.setText(questionData.getString("content"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			questionAuthorView.setText(questionData.getString("userName"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			questionDateView.setText(questionData.getString("createDate"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			questionReplyNumView.setText(questionData.getString("replys")
					+ "条回复");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private LinearLayout getView(JSONObject obj) {
		LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.tree_info_item, null);

		TextView summaryView = (TextView) view
				.findViewById(R.id.tree_info_item_summary);
		TextView dateView = (TextView) view
				.findViewById(R.id.tree_info_item_date);
		TextView supportNumView = (TextView) view
				.findViewById(R.id.tree_info_supportNum);
		TextView authorView = (TextView) view
				.findViewById(R.id.tree_info_item_name);
		View supportBtn = view.findViewById(R.id.tree_support_btn);
		View disSupportBtn = view.findViewById(R.id.tree_dissupport_btn);

		ViewGroup parent = (ViewGroup) supportBtn.getParent();
		parent.setTag(obj);

		supportBtn.setOnClickListener(supportOnClick);
		disSupportBtn.setOnClickListener(supportOnClick);

		try {
			summaryView.setText(obj.getString("content"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			dateView.setText(obj.getString("createDate"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			supportNumView.setText(obj.getString("supportCount"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			authorView.setText(obj.getString("userName"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return view;
	}

	OnClickListener supportOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			curSupportView = (ViewGroup) v.getParent();

			switch (v.getId()) {
			case R.id.tree_support_btn:
				isSupport = true;
				break;
			case R.id.tree_dissupport_btn:
				isSupport = false;
				break;
			}
			supportAnwserReq();
		}
	};

	/**
	 * 重置支持num
	 */
	private void resetSupportNum() {
		TextView supportNumView = (TextView) curSupportView
				.findViewById(R.id.tree_info_supportNum);
		int num = Integer.valueOf(supportNumView.getText().toString());

		supportNumView.setText(num + (isSupport ? 1 : -1) + "");

	}
	
	/**
	 * 清空回答input
	 */
	private void clearAnswerInput(){
		answerInputView.setText("");
		answerInputView.setFocusable(false);
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.hideSoftInputFromWindow(answerInputView.getWindowToken(), 0); //强制隐藏键盘  
	}

	/**
	 * 获取树洞回答
	 */
	private void getTreeInfoReq() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = treeInfoHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				try {
					bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
							HttpRequestUtils.BASE_HTTP_CONTEXT
									+ "GetAnswer.shtml?questionId="
									+ questionData.getString("id")
									+ "&pageNumber=1&pageLine=15");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();
			}
		}).start();
	}

	/**
	 * 发表回复
	 */
	private void writeAnwserReq() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 登陆的例子，get
				Message msg = writeAnswerHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				try {
					try {
						bundle.putString(
								HttpRequestUtils.BUNDLE_KEY_HTTPURL,
								HttpRequestUtils.BASE_HTTP_CONTEXT
										+ "Answer.shtml?userId="
										+ BaseUtils.CUR_USER_MAP.get("id")
										+ "&questionId="
										+ questionData.getString("id")
										+ "&content="
										+ URLEncoder.encode(answerInputView
												.getText().toString(), "UTF-8")
										+ "&pageNumber=1&pageLine=15");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();

				// answerInputView.setText("");
			}
		}).start();
	}

	/**
	 * 支持请求
	 */
	private void supportAnwserReq() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject curAnwser = (JSONObject) curSupportView.getTag();
				String answerId = null;
				try {
					answerId = curAnwser.getString("id");
				} catch (JSONException e) {
					e.printStackTrace();
				}

				Message msg = supportAnswerHttpHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(HttpRequestUtils.BUNDLE_KEY_HTTPURL,
						HttpRequestUtils.BASE_HTTP_CONTEXT
								+ "SupportAnswer.shtml?userId="
								+ BaseUtils.CUR_USER_MAP.get("id")
								+ "&answerId=" + answerId + "&valueNum="
								+ (isSupport ? 1 : -1)
								+ "&pageNumber=1&pageLine=15");
				bundle.putBoolean(HttpRequestUtils.BUNDLE_KEY_ISPOST, false);
				msg.setData(bundle);
				msg.sendToTarget();

				// answerInputView.setText("");
			}
		}).start();
	}

	class TreeInfoHttpHandler extends HttpCallBackHandler {

		public TreeInfoHttpHandler(Looper looper) {
			super(looper);
		}

		public TreeInfoHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");
				if (success) {
					JSONObject map = (JSONObject) resultObj.get("data");
					JSONArray answers = map.getJSONArray("answers");

					rendItems(answers);

				} else {
					Toast.makeText(context,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(context, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	class WriteAnswerHttpHandler extends HttpCallBackHandler {

		public WriteAnswerHttpHandler(Looper looper) {
			super(looper);
		}

		public WriteAnswerHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");
				if (success) {
					Toast.makeText(context, R.string.tree_write_answer,
							Toast.LENGTH_SHORT).show();
					getTreeInfoReq();
					clearAnswerInput();
				} else {
					Toast.makeText(context,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(context, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	class SupportAnswerHttpHandler extends HttpCallBackHandler {

		public SupportAnswerHttpHandler(Looper looper) {
			super(looper);
		}

		public SupportAnswerHttpHandler() {
		}

		@Override
		public void callAfterResponseStr(String resultStr) {
			JSONTokener jsonParser = new JSONTokener(resultStr);
			// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
			try {
				JSONObject resultObj = (JSONObject) jsonParser.nextValue();
				Boolean success = resultObj.getBoolean("success");
				if (success) {
					Toast.makeText(
							context,
							isSupport ? R.string.tree_support_answer_success
									: R.string.tree_dissupport_success,
							Toast.LENGTH_SHORT).show();
					resetSupportNum();
				} else {
					Toast.makeText(context,
							resultObj.getString("errorMessage"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(context, R.string.base_response_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

}
