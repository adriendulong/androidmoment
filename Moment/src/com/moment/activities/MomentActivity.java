package com.moment.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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

public class MomentActivity extends Activity {

	public static Typeface fontNumans;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment);

        fontNumans = Typeface.createFromAsset(getAssets(), "fonts/Numans-Regular.otf");

        MomentApi.initialize(getApplicationContext());

        if (MomentApi.myCookieStore.getCookies().size()>0){

            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

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
        }

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

    private void connectionserveur(View v) {
        connectionserveur();
    }

    private void connectionserveur(){

        String email = ((EditText)findViewById(R.id.email_login)).getText().toString();
        String password = ((EditText)findViewById(R.id.password_login)).getText().toString();
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);

        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals(""))
            GCMRegistrar.register(this, CommonUtilities.SENDER_ID);

        //On cr�� notre futur User
        AppMoment.getInstance().user = new User();
        AppMoment.getInstance().user.setEmail(email);

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);
        if (!GCMRegistrar.getRegistrationId(this).equals("")) params.put("notif_id", GCMRegistrar.getRegistrationId(this));
        params.put("os", "1");
        params.put("os_version", android.os.Build.VERSION.RELEASE);
        params.put("model", CommonUtilities.getDeviceName());
        params.put("device_id", AppMoment.getInstance().tel_id);
        //params.put("lang", Locale.getDefault().getDisplayLanguage());

        MomentApi.initialize(getApplicationContext());
        MomentApi.post("login", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
            Long id = null;
                try {
                    id = Long.parseLong(response.getString("id"));
                    AppMoment.getInstance().user.setId(id);

                    SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
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
        });
    }
}
