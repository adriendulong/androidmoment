package com.moment.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.CommonUtilities;
import com.moment.classes.DatabaseHelper;
import com.moment.classes.MomentApi;
import com.moment.models.Moment;
import com.moment.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

public class MomentActivity extends Activity {

	public static Typeface fontNumans;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME =
            "onServerExpirationTimeMs";
    /**
     * Default lifespan (7 days) of a reservation until it is considered expired.
     */
    public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;

    /**
     * Substitute you own sender ID here.
     */
    String SENDER_ID = CommonUtilities.SENDER_ID;

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";

    GoogleCloudMessaging gcm;
    Context context;
    private String regid;
    private ProgressDialog dialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment);

        fontNumans = Typeface.createFromAsset(getAssets(), "fonts/Numans-Regular.otf");

        MomentApi.initialize(getApplicationContext());

        if (MomentApi.myCookieStore.getCookies().size()>0){

            SharedPreferences sharedPreferences = getSharedPreferences(AppMoment.PREFS_NAME, MODE_PRIVATE);

            if(!DatabaseHelper.getUsersFromDataBase().isEmpty()){
                Long savedUserID = sharedPreferences.getLong("userID", -1);
                AppMoment.getInstance().user = DatabaseHelper.getUserByIdFromDataBase(savedUserID);
                Intent intent = new Intent(MomentActivity.this, TimelineActivity.class);
                startActivity(intent);
            }

            else {
                AppMoment.getInstance().user = new User();
                MomentApi.get("user", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            Long id = response.getLong("id");
                            AppMoment.getInstance().user.setId(id);

                            String email = response.getString("email");
                            AppMoment.getInstance().user.setEmail(email);

                            String firstname = response.getString("firstname");
                            AppMoment.getInstance().user.setFirstName(firstname);

                            String lastname = response.getString("lastname");
                            AppMoment.getInstance().user.setLastName(lastname);

                            if (response.has("profile_picture_url")) {
                                String profile_picture_url = response.getString("profile_picture_url");
                                AppMoment.getInstance().user.setPictureProfileUrl(profile_picture_url);
                            }

                            if (DatabaseHelper.getUserByIdFromDataBase(id) == null) {
                                AppMoment.getInstance().userDao.insert(AppMoment.getInstance().user);
                                if (DatabaseHelper.getMomentsFromDataBase() != null) {
                                    for (Moment moment : DatabaseHelper.getMomentsFromDataBase()) {
                                        DatabaseHelper.getUserByIdFromDataBase(id).addMoment(moment);
                                    }
                                }
                            }

                            Intent intent = new Intent(MomentActivity.this, TimelineActivity.class);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        //Take care of GCM
        context = getApplicationContext();
        regid = getRegistrationId(context);
        regid = "";

        if (regid.length() == 0) {
            registerBackground();
        }
        Log.v(TAG, "REGID : "+regid);
        gcm = GoogleCloudMessaging.getInstance(this);

        /*
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);

        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
        } else {
            if (GCMRegistrar.isRegisteredOnServer(this)) {
            	Log.v("GCM", "Already registered and on server");
            } else {
            	Log.v("GCM", "Not registered and on server");
            }
        }*/

        EditText password= (EditText) findViewById(R.id.password_login);

        password.setOnEditorActionListener(new OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if(actionId == EditorInfo.IME_ACTION_GO)
                {
                	//On cache le clavier
                	hideKeyboard();
                	connectionserveur();
                    
                   return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_moment, menu);
        return true;
    }

    public void connect(View view) throws JSONException {
        RelativeLayout button_inscription = (RelativeLayout)findViewById(R.id.inscrire_button_login);
        button_inscription.setVisibility(View.INVISIBLE);
        EditText edit_email = (EditText)findViewById(R.id.email_login);
        edit_email.setVisibility(View.VISIBLE);
        EditText edit_password = (EditText)findViewById(R.id.password_login);
        edit_password.setVisibility(View.VISIBLE);

        LinearLayout layout_buttons = (LinearLayout)findViewById(R.id.layout_button_login);

        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f
        );
        animation.setFillAfter(true);
        animation.setDuration(300);
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout connection_finale = (RelativeLayout)findViewById(R.id.connection_finale);
                connection_finale.setVisibility(View.VISIBLE);
                RelativeLayout connection_layout = (RelativeLayout)findViewById(R.id.connection_relative);
                connection_layout.setVisibility(View.INVISIBLE);
            }
        });
        layout_buttons.startAnimation(animation);

        ImageButton fleche_back = (ImageButton)findViewById(R.id.fleche_back_connection);
        fleche_back.setVisibility(View.VISIBLE);
    }

    public void closeConnection(View view){
    	
    	ImageButton fleche_back = (ImageButton)findViewById(R.id.fleche_back_connection);
        fleche_back.setVisibility(View.INVISIBLE);
    	
    	RelativeLayout connection_layout = (RelativeLayout)findViewById(R.id.connection_relative);
	     connection_layout.setVisibility(View.VISIBLE);
         EditText edit_email = (EditText)findViewById(R.id.email_login);
         edit_email.setVisibility(View.INVISIBLE);
         EditText edit_password = (EditText)findViewById(R.id.password_login);
         edit_password.setVisibility(View.INVISIBLE);
         
         
         LinearLayout layout_buttons = (LinearLayout)findViewById(R.id.layout_button_login);

         Animation animation = new TranslateAnimation(
             Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
             Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.0f
         );
         animation.setFillAfter(true);
         animation.setDuration(200);
         animation.setAnimationListener(new AnimationListener() {
  			
  			@Override
  			public void onAnimationStart(Animation animation) {
  				// TODO Auto-generated method stub
  				RelativeLayout connection_finale = (RelativeLayout)findViewById(R.id.connection_finale);
  			     connection_finale.setVisibility(View.GONE);
  				
  			}
  			
  			@Override
  			public void onAnimationRepeat(Animation animation) {
  				// TODO Auto-generated method stub
  				
  			}
  			
  			@Override
  			public void onAnimationEnd(Animation animation) {
  					RelativeLayout button_inscription = (RelativeLayout)findViewById(R.id.inscrire_button_login);
  					button_inscription.setVisibility(View.VISIBLE);
  			}
  			
  		});
         layout_buttons.startAnimation(animation);
    	
    }

    public void inscription(View view) {
       Intent intent = new Intent(this, InscriptionActivity.class);
       startActivity(intent);
    }

    private void hideKeyboard()
    {
    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void connectionserveur(View v) {
        connectionserveur();
    }

    private void connectionserveur(){

        String email = ((EditText)findViewById(R.id.email_login)).getText().toString();
        String password = ((EditText)findViewById(R.id.password_login)).getText().toString();

        //GCMRegistrar.checkDevice(this);
        //GCMRegistrar.checkManifest(this);

        //final String regId = GCMRegistrar.getRegistrationId(this);
        //if (regId.equals(""))
            //GCMRegistrar.register(this, CommonUtilities.SENDER_ID);

        //On cr�� notre futur User
        AppMoment.getInstance().user = new User();
        AppMoment.getInstance().user.setEmail(email);

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);
        if (!regid.equals("")) params.put("notif_id", regid);
        params.put("os", "1");
        params.put("os_version", android.os.Build.VERSION.RELEASE);
        params.put("model", CommonUtilities.getDeviceName());
        params.put("device_id", AppMoment.getInstance().tel_id);
        //params.put("lang", Locale.getDefault().getDisplayLanguage());

        MomentApi.initialize(getApplicationContext());

        dialog = ProgressDialog.show(this, null, "Connexion");
        MomentApi.post("login", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
            Long id = null;
                try {
                    id = Long.parseLong(response.getString("id"));
                    AppMoment.getInstance().user.setId(id);

                    SharedPreferences sharedPreferences = getSharedPreferences(AppMoment.PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong("userID", id);
                    editor.commit();

                    MomentApi.get("user", null, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            try {
                                String firstname = response.getString("firstname");
                                AppMoment.getInstance().user.setFirstName(firstname);

                                String lastname = response.getString("lastname");
                                AppMoment.getInstance().user.setLastName(lastname);

                                if (response.has("profile_picture_url")){
                                    String profile_picture_url = response.getString("profile_picture_url");
                                    AppMoment.getInstance().user.setPictureProfileUrl(profile_picture_url);
                                }

                                dialog.dismiss();

                                Intent intent = new Intent(MomentActivity.this, TimelineActivity.class);
                                startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println(id);
            }

            public void onFailure(Throwable error, String content) {
                // By default, call the deprecated onFailure(Throwable) for compatibility
                Log.e("HTTP", content);
                dialog.dismiss();
            }

        });
    }


    /**
     * GCM Functions
     */

    /**
     * Gets the current registration id for application on GCM service.
     * <p>
     * If result is empty, the registration has failed.
     *
     * @return registration id, or empty string if the registration is not
     *         complete.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.length() == 0) {
            Log.v(TAG, "Registration not found.");
            return "";
        }
        // check if app was updated; if so, it must clear registration id to
        // avoid a race condition if GCM sends a message
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion || isRegistrationExpired()) {
            Log.v(TAG, "App version changed or registration expired.");
            return "";
        }
        return registrationId;
    }


    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(MomentActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            Log.v(TAG, ""+packageInfo.versionCode);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Checks if the registration has expired.
     *
     * <p>To avoid the scenario where the device sends the registration to the
     * server but the server loses it, the app developer may choose to re-register
     * after REGISTRATION_EXPIRY_TIME_MS.
     *
     * @return true if the registration has expired.
     */
    private boolean isRegistrationExpired() {
        final SharedPreferences prefs = getGCMPreferences(context);
        // checks if the information is not stale
        long expirationTime =
                prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, -1);
        return System.currentTimeMillis() > expirationTime;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration id, app versionCode, and expiration time in the
     * application's shared preferences.
     */
    private void registerBackground() {

        Thread thread = new Thread(new Runnable()
        {
            public void run()
            {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration id=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the message
                    // using the 'from' address in the message.

                    // Save the regid - no need to register again.
                    //TODO : Send the new regiid to the server (build the request on the server)
                    setRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                Log.v(TAG, msg);
            }
        }
        );
        thread.start();
    }

    /**
     * Stores the registration id, app versionCode, and expiration time in the
     * application's {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration id
     */
    private void setRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.v(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        long expirationTime = System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;

        Log.v(TAG, "Setting registration expiry time to " +
                new Timestamp(expirationTime));
        editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
        editor.commit();
    }
}
