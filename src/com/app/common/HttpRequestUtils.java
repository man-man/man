package com.app.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;

import com.app.man.R;

public class HttpRequestUtils {

	public static String BASE_HTTP_CONTEXT = "http://115.28.152.215/manWear/phone/";

	public static String BUNDLE_KEY_ISPOST = "isPost";
	public static String BUNDLE_KEY_HTTPURL = "httpUrl";
	public static String BUNDLE_KEY_PARAMS = "params";

	public static String getResFromHttpUrl(Message msg) {
		// 先获取传入的参数
		Bundle bundle = msg.getData();
		Boolean isPost = bundle.getBoolean(BUNDLE_KEY_ISPOST);
		String httpUrl = bundle.getString(BUNDLE_KEY_HTTPURL);
		Object params = bundle.get(BUNDLE_KEY_PARAMS);
		return getResFromHttpUrl(isPost, httpUrl, params);
	}

	public static String getNetworkWrongStr() {
		// JSONObject jsonObject = new JSONObject();
		// try {
		// jsonObject.put("success", false);
		// jsonObject.put("errorCode", -1001);
		// jsonObject.put("errorMessage", R.string.base_response_error);
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// return jsonObject.toString();
		return "{" + "\"success\": false" + ",\"errorCode\": \"-1001\""
				+ ",\"errorMessage\": \"" + R.string.base_response_error + "\""
				+ "}";

	}

	public static void main(String[] args) {
		System.out.println(getNetworkWrongStr());
	}

	/**
	 * 根据参数，获取对应的http返回的内容
	 * 
	 * @param isPost
	 * @param httpUrl
	 * @param params
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String getResFromHttpUrl(Boolean isPost, String httpUrl,
			Object params) {
		String resultStr;
		// 校验关键参数的非空
		if (isPost == null) {
			return null;
		}
		if (httpUrl == null || httpUrl.trim().length() == 0) {
			return null;
		}
		// 目前测试，在2.2以上的模拟器里，不加下面这段，会报错，主要意思就是在主线程中不能去执行这种可能引起堵塞的http的请求
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		if (isPost) {
			try {
				JSONObject jsonObject = (JSONObject) params;
				jsonObject
						.put("curUserId",
								Long.valueOf(BaseUtils.CUR_USER_MAP
										.get("userId") + ""));
			} catch (Exception e) {
			}
			resultStr = HttpRequestUtils.sendPost(httpUrl, params);
		} else {
			if (httpUrl.contains("?")) {
				httpUrl += "&curUserId=" + BaseUtils.CUR_USER_MAP.get("userId");
			} else {
				httpUrl += "?curUserId=" + BaseUtils.CUR_USER_MAP.get("userId");
			}
			resultStr = HttpRequestUtils.sendGet(httpUrl);
		}
		return resultStr;
	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url) {
		StringBuilder result = new StringBuilder();
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			connection.setReadTimeout(60 * 1000);
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// // 获取所有响应头字段
			// Map<String, List<String>> map = connection.getHeaderFields();
			// // 遍历所有的响应头字段
			// for (String key : map.keySet()) {
			// System.out.println(key + "--->" + map.get(key));
			// }
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return getNetworkWrongStr();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result.toString();
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, Object param) {
		PrintWriter out = null;
		// DataOutputStream out2 = null;
		BufferedReader in = null;
		StringBuilder result = new StringBuilder();
		try {
			URL realUrl = new URL(url);
			if (param instanceof String) {
				// 打开和URL之间的连接
				URLConnection conn = realUrl.openConnection();
				conn.setReadTimeout(60 * 1000);
				// 设置通用的请求属性
				conn.setRequestProperty("accept", "*/*");
				conn.setRequestProperty("connection", "Keep-Alive");
				conn.setRequestProperty("user-agent",
						"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
				// 发送POST请求必须设置如下两行
				conn.setDoOutput(true);
				conn.setDoInput(true);

				// 获取URLConnection对象对应的输出流
				out = new PrintWriter(conn.getOutputStream());
				// 发送请求参数
				out.print(param);
				// flush输出流的缓冲
				out.flush();
				// 定义BufferedReader输入流来读取URL的响应
				in = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				String line;
				while ((line = in.readLine()) != null) {
					result.append(line);
				}
			}
			if (param instanceof JSONObjectSerializalble) {
				// TODO Auto-generated method stub
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
				String encoderJson = param.toString();
				encoderJson = URLEncoder.encode(encoderJson, HTTP.UTF_8);
				StringEntity se = new StringEntity(encoderJson);
				se.setContentType(CONTENT_TYPE_TEXT_JSON);
				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						APPLICATION_JSON));
				httpPost.setEntity(se);
				// 返回服务器响应
				HttpResponse response = httpClient.execute(httpPost);
				HttpEntity entity = response.getEntity();
				try {
					// System.out
					// .println("----------------------------------------");
					// System.out.println(response.getStatusLine()); // 服务器返回状态
					// Header[] headers = response.getAllHeaders(); //
					// 返回的HTTP头信息
					// for (int i = 0; i < headers.length; i++) {
					// System.out.println(headers[i]);
					// }
					// System.out
					// .println("----------------------------------------");
					String responseString = null;
					if (response.getEntity() != null) {
						responseString = EntityUtils.toString(response
								.getEntity()); // 返回服务器响应的HTML代码
						return responseString;
						// System.out.println(responseString); //
						// 打印出服务器响应的HTML代码
					}
				} finally {
					if (entity != null)
						entity.consumeContent(); // release connection
													// gracefully
				}

				// HttpURLConnection connection = (HttpURLConnection) realUrl
				// .openConnection();
				// connection.setReadTimeout(60 * 1000);
				// connection.setDoOutput(true);
				// connection.setDoInput(true);
				// connection.setUseCaches(false);
				// connection.setRequestMethod("POST");
				// connection.setRequestProperty("Content-type",
				// "application/x-www-form-urlencoded");
				// connection.connect();
				//
				// // POST请求
				// out2 = new DataOutputStream(connection.getOutputStream());
				// out2.writeBytes(param.toString());
				// out2.flush();
				// out2.close();
				// // 读取响应
				// BufferedReader reader = new BufferedReader(
				// new InputStreamReader(connection.getInputStream()));
				// String lines;
				// while ((lines = reader.readLine()) != null) {
				// lines = new String(lines.getBytes(), "utf-8");
				// result.append(lines);
				// }
				// reader.close();
				// // 断开连接
				// connection.disconnect();
			}
		} catch (Exception e) {
			// System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
			return getNetworkWrongStr();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result.toString();
	}

	private static final String APPLICATION_JSON = "application/json";

	private static final String CONTENT_TYPE_TEXT_JSON = "text/json";

}