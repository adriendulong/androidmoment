package com.moment.classes;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.StringEntity;

public class MomentApi {
    final public static String BASE_URL = "http://apidev.appmoment.fr/";
    final public static String CREATION_MOMENT = BASE_URL + "newmoment";
    final public static String MODIF_MOMENT = BASE_URL + "moment/";
    final public static String GET_MOMENT = BASE_URL + "moment/";
	
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
      client.setTimeout(20*1000);
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