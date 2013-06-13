package com.moment.classes;

import org.apache.http.entity.StringEntity;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class MomentApi {
	//private static final String BASE_URL = "http://192.168.0.13:5000/";
	private static final String BASE_URL = "http://api.appmoment.fr/";
	
  private static AsyncHttpClient client = new AsyncHttpClient();
  public static PersistentCookieStore myCookieStore;
  
  public static void initialize(Context appContext){
	  myCookieStore = new PersistentCookieStore(appContext);
	  client.setCookieStore(myCookieStore);
  }

  public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	  System.out.println(getAbsoluteUrl(url));
      client.get(getAbsoluteUrl(url), params, responseHandler);
  }
  
  public static void getOutside(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	  String goodURL = "http://"+url;
      client.get(goodURL, params, responseHandler);
  }

  public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	  System.out.println(getAbsoluteUrl(url));
	  System.out.println(params);
      client.post(getAbsoluteUrl(url), params, responseHandler);
  }
  
  public static void postJSON(Context context, String url, StringEntity content, AsyncHttpResponseHandler responseHandler) {
	  System.out.println("POST");
	  client.post(context, getAbsoluteUrl(url), content, "application/json", responseHandler);

      
  }

  private static String getAbsoluteUrl(String relativeUrl) {
      return BASE_URL + relativeUrl;
  }
}