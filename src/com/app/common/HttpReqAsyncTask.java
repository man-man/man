package com.app.common;

import android.os.AsyncTask;
import android.os.Bundle;

public abstract class HttpReqAsyncTask extends AsyncTask<Bundle, Void, String> {

	@Override
	protected String doInBackground(Bundle... params) {
		Bundle b = params[0];
		return HttpRequestUtils.getResFromHttpUrl(b);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		callAfterResponseStr(result);
	}

	/**
	 * 定义一个用户需实现的逻辑，在获取到http返回的内容后做相关的处理
	 * 
	 * @param resultStr
	 */
	public abstract void callAfterResponseStr(String resultStr);
}
