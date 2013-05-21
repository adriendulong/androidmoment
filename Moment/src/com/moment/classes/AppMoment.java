package com.moment.classes;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.telephony.TelephonyManager;

public class AppMoment extends Application {
	
	public User user;
	public LruCache<String, Bitmap> mMemoryCache;

	private static AppMoment sInstance;
	public static final String APP_FB_ID = "445031162214877";
	public static final String[] PERMS_FB = new String[] { "user_events", "read_friendlists", "user_about_me", "friends_about_me" };
	public String tel_id;
	 
    public static AppMoment getInstance() {
      return sInstance;
    }
 
    @Override
    public void onCreate() {
      super.onCreate(); 
      sInstance = this;
      sInstance.initializeInstance();
      
      /**
       * On recupere l'id du tel
       */
      
      final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
      tel_id = tm.getDeviceId();
      
      
      /**
       * On initialise le cache 
       */
     
      // Get max available VM memory, exceeding this amount will throw an
      // OutOfMemory exception. Stored in kilobytes as LruCache takes an
      // int in its constructor.
      final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

      // Use 1/8th of the available memory for this memory cache.
      final int cacheSize = maxMemory / 8;

      mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
          @Override
          protected int sizeOf(String key, Bitmap bitmap) {
              // The cache size will be measured in kilobytes rather than
              // number of items.
              return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
          }
      };
      
      
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
