package com.moment;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.v4.util.LruCache;
import android.telephony.TelephonyManager;

import android.util.Log;
import android.widget.Toast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.activities.TimelineActivity;
import com.moment.classes.DatabaseHelper;
import com.moment.classes.MomentApi;
import com.moment.models.*;
import org.apache.http.cookie.Cookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.List;

public class AppMoment extends Application {
	
	public User user;
	public LruCache<String, Bitmap> mMemoryCache;

	private static AppMoment sInstance;

	public static final String APP_FB_ID = "445031162214877";
	public static final String[] PERMS_FB = new String[] { "user_events", "read_friendlists", "user_about_me", "friends_about_me" };
    public static final String PREFS_NAME = "MomentPrefs";
	public String tel_id;

    public DaoMaster.DevOpenHelper helper;
    public SQLiteDatabase db;
    public DaoMaster daoMaster;
    public DaoSession daoSession;
    public MomentDao momentDao;
    public UserDao userDao;
    public ChatDao chatDao;

    @Override
    public void onCreate() {
        super.onCreate();

        helper = new DaoMaster.DevOpenHelper(this, "db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        userDao = daoSession.getUserDao();
        momentDao = daoSession.getMomentDao();
        chatDao = daoSession.getChatDao();

        sInstance = this;
        this.initializeInstance();

        tel_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

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
    }

    public boolean checkInternet()
    {
        ConnectivityManager connection = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = connection.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo mobile = connection.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isConnected()) {
            return true;
        }else if(mobile!=null){
            if (mobile.isConnected()) {
            return true;
            }
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

    public void getUser(){
        //We try to get the user
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if(!DatabaseHelper.getUsersFromDataBase().isEmpty()){
            Long savedUserID = sharedPreferences.getLong("userID", -1);
            user = DatabaseHelper.getUserByIdFromDataBase(savedUserID);
        }

        if(user!=null){
            if(!DatabaseHelper.getMomentsFromDataBase().isEmpty()){
                List<Moment> momentList = AppMoment.getInstance().momentDao.loadAll();
                for (Moment moment : momentList){
                    user.getMoments().add(moment);
                }
            }
        }
    }

    public void disconnect(){

        for(Moment moment:user.getMoments()){
            DatabaseHelper.removeMoment(moment);
        }


        SharedPreferences sharedPreferences = getSharedPreferences(AppMoment.PREFS_NAME, MODE_PRIVATE);
        sharedPreferences.edit().remove("userID").commit();
        //Finally remove the user
        DatabaseHelper.removeUser(user);
        user = null;

    }

}
