package com.app.common;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * 封装一个类，通过对传入Message对象的解析，获取相关的http的基础类型和参数，在非activity线程中，执行http请求，获取返回的内容，
 * 并执行后续的操作。 用户需重构callAfterResponseStr这个抽象方法，在里面根据传入的返回内容，进行后续的操作
 * 
 * @author samoin
 * 
 */
public abstract class HttpCallBackHandler extends Handler {

	@Override
	public void handleMessage(Message msg) {

		System.out.println("---------网络请求2 线程："
				+ Thread.currentThread().getId());

		String resultStr = HttpRequestUtils.getResFromHttpUrl(msg);
		// 根据http请求的返回结果，进行后续的操作
		callAfterResponseStr(resultStr);
	}

	/**
	 * 定义一个用户需实现的逻辑，在获取到http返回的内容后做相关的处理
	 * 
	 * @param resultStr
	 */
	public abstract void callAfterResponseStr(String resultStr);

	/**
	 * 需要保证另起一个线程处理的时候，调用此构造函数
	 * 
	 * @param looper
	 */
	public HttpCallBackHandler(Looper looper) {
		super(looper);
	}

	/**
	 * 需要保证在同一线程内的时候，调用此构造函数，如对layout中布局控件的操作，必须保证再同一县城内，否则会报错：<br/>
	 * <font color=red>android.view.ViewRootImpl$CalledFromWrongThreadException:
	 * Only the original thread that created a view hierarchy can touch its
	 * views.</font>
	 */
	public HttpCallBackHandler() {
	}
}
