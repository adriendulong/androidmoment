package com.moment;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.telephony.TelephonyManager;
import com.moment.models.User;

public class AppMoment extends Application {
	
	public User user;
	public LruCache<String, Bitmap> mMemoryCache;

	private static AppMoment sInstance;
	public static final String APP_FB_ID = "445031162214877";
	public static final String[] PERMS_FB = new String[] { "user_events", "read_friendlists", "user_about_me", "friends_about_me" };
	public String tel_id;

    @Override
    public void onCreate() {
      super.onCreate();

      sInstance = this;
      this.initializeInstance();

      final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
      tel_id = tm.getDeviceId();

      final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
      final int cacheSize = maxMemory / 8;

      mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
          @Override
          protected int sizeOf(String key, Bitmap bitmap) {
              return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
          }
      };
    }

    public static AppMoment getInstance() {
        return sInstance;
    }

    protected void initializeInstance() {
      // do all you initialization here
    }


    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
        else{
        	mMemoryCache.remove(key);
        	 mMemoryCache.put(key, bitmap);
        }
    }


    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
