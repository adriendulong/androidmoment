package com.moment;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.support.v4.util.LruCache;
import android.telephony.TelephonyManager;
import com.moment.models.DaoMaster;
import com.moment.models.DaoSession;
import com.moment.models.MomentDao;
import com.moment.models.User;

public class AppMoment extends Application {
	
	public User user;
	public LruCache<String, Bitmap> mMemoryCache;

	private static AppMoment sInstance;

	public static final String APP_FB_ID = "445031162214877";
	public static final String[] PERMS_FB = new String[] { "user_events", "read_friendlists", "user_about_me", "friends_about_me" };
	public String tel_id;

    public DaoMaster.DevOpenHelper helper;
    public SQLiteDatabase db;
    public DaoMaster daoMaster;
    public DaoSession daoSession;
    public MomentDao momentDao;

    @Override
    public void onCreate() {
        super.onCreate();

        helper = new DaoMaster.DevOpenHelper(this, "db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        momentDao = daoSession.getMomentDao();

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

    public boolean checkInternet()
    {
        ConnectivityManager connection = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = connection.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo mobile = connection.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isConnected()) {
            return true;
        } else if (mobile.isConnected()) {
            return true;
        }
        return false;
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
