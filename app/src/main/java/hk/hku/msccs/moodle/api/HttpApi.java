package hk.hku.msccs.moodle.api;
/**
 * @author airwizard@sina.com
 * @since 2015年6月23日17:04:43
 */

import android.app.Activity;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import hk.hku.msccs.moodle.activity.R;
import hk.hku.msccs.moodle.util.Constant;
import hk.hku.msccs.moodle.util.L;
import hk.hku.msccs.moodle.util.PreferenceUtils;
import hk.hku.msccs.moodle.util.Reader;
import hk.hku.msccs.moodle.util.T;
import hk.hku.msccs.moodle.util.VolleyUtil;


public class HttpApi {
	
	/**
	 * 获取token
	 * @param context
	 * @param listener
	 * @param errorListener
	 */
	/*public static void getToken(Context context, Listener<String> listener, ErrorListener errorListener){
		
		StringRequest request= new StringRequest(Method.POST, Constant.DEFAULT_XIAN_REQUEST_URL + "/sip/authz/gettoken", listener, errorListener) {
			
			//重写getParams设置post请求的参数
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date = d.format(new Date());
				String authSign = Constant.SIP_SECRECT_KEY + Constant.SIP_USERNAME + MD5.encryptToMD5(Constant.SIP_PASSWORD) + date;
				authSign = MD5.encryptToMD5(authSign.replaceAll(" ", "")).toUpperCase();
				
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put(Constant.USERNAME, Constant.SIP_USERNAME);
				paramMap.put(Constant.PASSWORD, MD5.encryptToMD5(Constant.SIP_PASSWORD));
				paramMap.put("requestTime", date);
				paramMap.put("authSign", authSign);
				return paramMap;
			}
			
		};
		
		// 请求加上Tag,用于取消请求
		request.setTag(context);
		
		if (!((Activity) context).isFinishing()) {
			VolleyUtil.getQueue(context).add(request);
		} else {
			VolleyUtil.getQueue(context).cancelAll(context);
		}
	}*/
	
	
/*
	public static void login(final Context context, Listener<String> listener, ErrorListener errorListener){
		
		StringRequest request= new StringRequest(Method.POST, Constant.DEFAULT_LOGIN_REQUEST_URL, listener, errorListener) {
			
			//重写getParams设置post请求的参数
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {

				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("Portal UID", Constant.MOODLE_USERNAME);
				//paramMap.put(Constant.PASSWORD, MD5.encryptToMD5(Constant.SIP_PASSWORD));
				paramMap.put("PIN", Constant.MOODEL_PASSWORD);
				return paramMap;
			}
			
		};
		
		// 请求加上Tag,用于取消请求
		request.setTag(context);

		if (!((Activity) context).isFinishing()) {
			VolleyUtil.getQueue(context).add(request);
		} else {
			VolleyUtil.getQueue(context).cancelAll(context);
		}
		
	}*/
	
	
	/**
	 * 首页广告列表
	 * @param context
	 * @param token
	 * @param listener
	 * @param errorListener
	 */
	public static void getHomeAds(Context context, String token, Listener<String> listener, ErrorListener errorListener) {
		get(context, "/mainapi/main/service/getHomeAds", token, listener, errorListener);
		
	}

	/**
	 * 首页商品列表 
	 * @param context
	 * @param token
	 * @param listener
	 * @param errorListener
	 */
	public static void getProductList(Context context, String token, Listener<String> listener, ErrorListener errorListener) {
		get(context, "/mainapi/main/service/getProductList", token, listener, errorListener);
	}
	
	/**
	 * 首页促销商品列表
	 * @param context
	 * @param token
	 * @param listener
	 * @param errorListener
	 */
	public static void getPromotionList(Context context, String token, Listener<String> listener, ErrorListener errorListener) {
		get(context, "/mainapi/main/service/getPromotionList", token, listener, errorListener);
	}
	
	
	/**
	 * 分类列表
	 * @param context
	 * @param token
	 * @param listener
	 * @param errorListener
	 */
	public static void getCategoryList(Context context, String token, Listener<String> listener, ErrorListener errorListener) {
		get(context, "/generalapi/general/service/getCategoryList", token, listener, errorListener);
	}
	
	
	/**
	 * 购物车商品列表
	 * @param context
	 * @param token
	 * @param listener
	 * @param errorListener
	 */
	public static void getCartDataList(Context context, String token, Listener<String> listener, ErrorListener errorListener) {
		get(context, "/cartapi/cart/service/getCartDataList", token, listener, errorListener);
	}
	
	
	
/*	*//**
	 * 登录
	 * @param context
	 * @param token
	 * @param uname
	 * @param pwd
	 * @param listener
	 * @param errorListener
	 *//*
	public static void login(Context context, String uname, String pwd, Listener<String> listener, ErrorListener errorListener) {
		try {
			String jsonStr = getJsonStr(new String[][] {
					{"uname", uname},{"pwd", pwd}
			});
			post(context, "/ucapi/uc/service/login", token, jsonStr, listener, errorListener);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}*/
	
	
	
	/**
	 * 通用参数
	 * @param context
	 * @param token
	 * @return
	 */
	public static String getCommonParam(final Context context, String token){
		return "?" + "jsonpCallback=" + "&" + "token=" + token + "&" + "jsonStr=" ;
	}
	
	
	
	/**
	 * jsonStr
	 * @param arr
	 * @return
	 * @throws JSONException
	 */
	public static String getJsonStr(String[][] arr) throws JSONException {
		JSONObject json = new JSONObject();
		if (arr != null && arr.length > 0) {
			for (int i = arr.length - 1; i >= 0; i--) {
				json.put(arr[i][0], arr[i][1]);
			}
		}
		return json.toString();
	}

	/**
	 * token逻辑get
	 * @param context
	 * @param uri
	 * @param token
	 * @param listener
	 * @param errorListener
	 */
	public static void get(final Context context, final String uri, String token, final Listener<String> listener, final ErrorListener errorListener){
		if(System.currentTimeMillis() > PreferenceUtils.getPrefLong(context, Constant.TOKEN_EXPIRE_TIME, 0)){
			/*refreshToken(context, token, true, new Listener<String>() {

				@Override
				public void onResponse(String response) {
					try {
						L.e(response);
						JSONObject json = new JSONObject(response);
						String statusCode = json.getString("statusCode");
						if(statusCode.equals("1")){
							PreferenceUtils.setPrefString(context, Constant.TOKEN, json.getString("token").substring(0, 32));
							PreferenceUtils.setPrefLong(context, Constant.TOKEN_EXPIRE_TIME, System.currentTimeMillis() + Long.valueOf(json.getString("expire")) * 1000);
							String param = uri + getCommonParam(context, PreferenceUtils.getPrefString(context, Constant.TOKEN, ""));	
							volleyGet(context, param, listener, errorListener);
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}
			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					T.showLong(context, context.getResources().getString(R.string.request_refresh_token_fail_text));
				}
			});*/

		}else{
			String param = uri + getCommonParam(context, token);	
			//volleyGet(context, param, listener, errorListener);
		}
	}

	/**
	 * token逻辑post
	 * @param context
	 * @param uri
	 * @param token
	 * @param jsonStr
	 * @param listener
	 * @param errorListener
	 */
	public static void post(final Context context, final String uri, String token, final String jsonStr, 
			final Listener<String> listener, final ErrorListener errorListener){
		if(System.currentTimeMillis() > PreferenceUtils.getPrefLong(context, Constant.TOKEN_EXPIRE_TIME, 0)){
			/*refreshToken(context, token, true, new Listener<String>() {

				@Override
				public void onResponse(String response) {
					try {
						L.e(response);
						JSONObject json = new JSONObject(response);
						String statusCode = json.getString("statusCode");
						if(statusCode.equals("1")){
							PreferenceUtils.setPrefString(context, Constant.TOKEN, json.getString("token").substring(0, 32));
							PreferenceUtils.setPrefLong(context, Constant.TOKEN_EXPIRE_TIME, System.currentTimeMillis() + Long.valueOf(json.getString("expire")) * 1000);
							volleyPost(context, Constant.DEFAULT_XIAN_REQUEST_URL + uri, PreferenceUtils.getPrefString(context, Constant.TOKEN, ""), jsonStr, 
									listener, errorListener);
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}
			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					T.showLong(context, context.getResources().getString(R.string.request_refresh_token_fail_text));
				}
			});*/

		}else{
			volleyPost(context, Constant.DEFAULT_XIAN_REQUEST_URL + uri, token, jsonStr, listener, errorListener);
		}
	}
	
	
	
	
	
	
	
	/**
	 * volleyGet请求
	 * @param context
	 * @param param
	 * @param listener
	 * @param errorListener
	 */
	/*public static void volleyGet(Context context, String param, Listener<String> listener, ErrorListener errorListener){
		L.e(Constant.DEFAULT_XIAN_REQUEST_URL + param);

		StringRequest request= new StringRequest(Constant.DEFAULT_LOGIN_REQUEST_URL + param, listener, errorListener);
		// 请求加上Tag,用于取消请求
		request.setTag(context);
		//request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		if (!((Activity) context).isFinishing()) {
			VolleyUtil.getQueue(context).add(request);
		} else {
			VolleyUtil.getQueue(context).cancelAll(context);
		}
	}*/




	
	
	/**
	 * volleyPost请求
	 * @param context
	 * @param url
	 * @param token
	 * @param jsonStr
	 * @param listener
	 * @param errorListener
	 */
	public static void volleyPost(Context context, String url, final String token, final String jsonStr, Listener<String> listener, ErrorListener errorListener) {
		L.e(url + "[" + "token = " + token + " jsonStr = " + jsonStr + "]");
		
		StringRequest request= new StringRequest(Method.POST, url, listener, errorListener) {
			
			//重写getParams设置post请求的参数
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put(Constant.TOKEN, token);
				paramMap.put(Constant.JSONSTR, jsonStr);
				return paramMap;
			}
			
		};
		
		// 请求加上Tag,用于取消请求
		request.setTag(context);	
		
		if (!((Activity) context).isFinishing()) {
			VolleyUtil.getQueue(context).add(request);
		} else {
			VolleyUtil.getQueue(context).cancelAll(context);
		}

	}




	public static void login(final Context context, String username, String password, final Listener<String> listener, final ErrorListener errorListener){
		loginPortal(context, username, password, new Listener<String>() {
			@Override
			public void onResponse(String response) {
				//L.e(response);
				int ticketIDStartPosition = response.indexOf("ticket=") + 7;
				String ticketID = response.substring(ticketIDStartPosition, response.indexOf("\";", ticketIDStartPosition));
				//L.e(ticketID);
				loginMoodle(context, ticketID, listener, errorListener);

			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				T.showLong(context, context.getResources().getString(R.string.request_fail_text));
			}
		});
	}


	public static void loginPortal(Context context, String username, String password, Listener<String> listener, ErrorListener errorListener){

		StringRequest request= new StringRequest(Constant.DEFAULT_PORTAL_LOGIN_REQUEST_URL + "?authCAS=CAS&username="
				+ Constant.MOODLE_USERNAME + "&password=" + Constant.MOODEL_PASSWORD, listener, errorListener);
		// 请求加上Tag,用于取消请求
		request.setTag(context);
		//request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		if (!((Activity) context).isFinishing()) {
			VolleyUtil.getQueue(context).add(request);
		} else {
			VolleyUtil.getQueue(context).cancelAll(context);
		}
	}
	public static void loginMoodle(Context context, String ticketID, Listener<String> listener, ErrorListener errorListener){
		L.e(Constant.DEFAULT_MOODLE_LOGIN_REQUEST_URL + "?authCAS=CAS&ticket="
				+ ticketID);
		StringRequest request= new StringRequest(Constant.DEFAULT_MOODLE_LOGIN_REQUEST_URL + "?authCAS=CAS&ticket="
				+ ticketID, listener, errorListener);
		// 请求加上Tag,用于取消请求
		request.setTag(context);
		//request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		if (!((Activity) context).isFinishing()) {
			VolleyUtil.getQueue(context).add(request);
		} else {
			VolleyUtil.getQueue(context).cancelAll(context);
		}
	}




	public String getMoodleFirstPage( String userName, String userPW )
	{
		HttpsURLConnection conn_portal = null;
		HttpURLConnection conn_moodle = null;
		final int HTML_BUFFER_SIZE = 2*1024*1024;
		char htmlBuffer[] = new char[HTML_BUFFER_SIZE];
		try {
			/////////////////////////////////// HKU portal //////////////////////////////////////
			URL url_portal = new
					URL("https://hkuportal.hku.hk/cas/login?service=http://moodle.hku.hk/login/index.php?authCAS=CAS&username="
					+ userName + "&password=" + userPW);
			conn_portal = (HttpsURLConnection) url_portal.openConnection();
			BufferedReader reader_portal = new BufferedReader(new InputStreamReader(conn_portal.getInputStream()));
			String HTMLSource = Reader.ReadBufferedHTML(reader_portal, htmlBuffer, HTML_BUFFER_SIZE);
			int ticketIDStartPosition = HTMLSource.indexOf("ticket=") + 7;
			String ticketID = HTMLSource.substring(ticketIDStartPosition, HTMLSource.indexOf("\";", ticketIDStartPosition));
			reader_portal.close();
			/////////////////////////////////// HKU portal //////////////////////////////////////

			/////////////////////////////////// Moodle //////////////////////////////////////
			URL url_moodle = new URL("http://moodle.hku.hk/login/index.php?authCAS=CAS&ticket=" + ticketID);
			conn_moodle = (HttpURLConnection) url_moodle.openConnection();
			conn_moodle.setInstanceFollowRedirects(true);
			BufferedReader reader_moodle = new BufferedReader(new InputStreamReader(conn_moodle.getInputStream()));
			HTMLSource = Reader.ReadBufferedHTML(reader_moodle, htmlBuffer, HTML_BUFFER_SIZE);
			reader_moodle.close();
			return HTMLSource;
			/////////////////////////////////// Moodle //////////////////////////////////////
		} catch(Exception e) {
			return "Fail to login";
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			if(conn_portal != null){
				conn_portal.disconnect();
			}
			if(conn_moodle != null){
				conn_moodle.disconnect();
			}
		}
	}



	

}
