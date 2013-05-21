package com.moment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.google.android.gcm.GCMRegistrar;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moment.classes.AppMoment;
import com.moment.classes.CommonUtilities;
import com.moment.classes.MomentApi;
import com.moment.classes.User;

public class MomentActivity extends Activity {

	public static Typeface fontNumans;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment);
        fontNumans = Typeface.createFromAsset(getAssets(),
                "fonts/Numans-Regular.otf");
        
        
        //Test de connexion auto au dŽbut si on a les cookies
        MomentApi.initialize(getApplicationContext());
        Log.d("Cookie", MomentApi.myCookieStore.getCookies().toString());
        
        //Si on a dŽjˆ les cookie on a pas besoin de demander la connexion
        if (MomentApi.myCookieStore.getCookies().size()>0){
        	
        	//On va chercher les infos concernant le user (pas ˆ faire normalement on l'a en base
        	AppMoment.getInstance().user = new User();
        	
        	
        	MomentApi.get("user", null, new JsonHttpResponseHandler() {
	            @Override
	            public void onSuccess(JSONObject response) {
	            	try {
	            		int id = response.getInt("id");
	            		AppMoment.getInstance().user.setId(id);
	            		
	            		
	            		String email = response.getString("email");
	            		AppMoment.getInstance().user.setEmail(email);
	            		
						String firstname = response.getString("firstname");
						AppMoment.getInstance().user.setFirstname(firstname);
						
						String lastname = response.getString("lastname");
						AppMoment.getInstance().user.setLastname(lastname);
						
						if (response.has("profile_picture_url")){
							String profile_picture_url = response.getString("profile_picture_url");
							AppMoment.getInstance().user.setPicture_profile_url(profile_picture_url);
						}
						
						
						Intent intent = new Intent(MomentActivity.this, TimelineActivity.class);
					    startActivity(intent);
				    	
						
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        });
        	
        }
        
        
        
        /**
         * On enregistre le device pour les notifs push
         */
        
        GCMRegistrar.checkDevice(this);
        
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);

        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
            	Log.v("GCM", "Already registered and on server");

            } else {
                
            	Log.v("GCM", "Not registered and on server");

            }
        }
        
        //PAssword edit text when done
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
    
    
    /** Appeler lorsque l'utilisateur clique sur le bouton de connection */
    public void connect(View view) throws JSONException {
       Log.d("Connection", "conncetion reussi");
       
       
       //On fait disparaitre ce qu'il faut ou apparaitre ce qu'il faut
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
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				//On link le bouton avec la bonne action
					RelativeLayout connection_finale = (RelativeLayout)findViewById(R.id.connection_finale);
			       connection_finale.setVisibility(View.VISIBLE);
			       RelativeLayout connection_layout = (RelativeLayout)findViewById(R.id.connection_relative);
			       connection_layout.setVisibility(View.INVISIBLE);
			}
		});
       layout_buttons.startAnimation(animation);
       
       
       ImageButton fleche_back = (ImageButton)findViewById(R.id.fleche_back_connection);
       fleche_back.setVisibility(View.VISIBLE);
       
       
       
       /*JSONObject user;
       JSONArray users = new JSONArray();
       for(int i=0;i<10;i++){
    	   user = new JSONObject();
    	   user.put("email", "adrien.dulong"+i+"@gmail.com");
    	   users.put(user);
       }
       
       JSONObject object = new JSONObject();
       object.put("idMoment", 4);
       object.put("users", users);
       
       System.out.println(object.toString());
       
       StringEntity se = null;
		try {
			se = new StringEntity(object.toString());
			//se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
       
       
       //MomentApi.initialize(getApplicationContext());
       MomentApi.postJSON(this, "newguests", se, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
	               System.out.println(response);
	               System.out.println(MomentApi.myCookieStore.getCookies().get(0).toString());
            }
        });
       */
       //Creer et demarrer la timeline
      // Intent intent = new Intent(this, TimelineActivity.class);
      // startActivity(intent);*/
    }
    
    
    public void connectionFinale(View view) throws JSONException {
    	System.out.println("Conneciton finale !");
    	Log.d("Connection", "conncetion FINALE");
    	
    	connectionserveur();
    	
    }
    
    //Pour revenir ˆ l'ecran initiale avec le bouton s'inscrire
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
    
    
    /** Appeler lorsque l'utilisateur clique sur le bouton de inscription */
    public void inscription(View view) {
       Log.d("Connection", "conncetion reussi");
       
       
       //Creer et demarrer la timeline
       Intent intent = new Intent(this, InscriptionActivity.class);
       startActivity(intent);
    }
    
    
    /**
     * Fonction pour cacher le clavier
     */
    
    private void hideKeyboard()
    {
    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
    
    
    /**
     * Fonction qui gre la recuperation de la conncetion avec le serveur
     */
    private void connectionserveur(){
    	
    	String email = ((EditText)findViewById(R.id.email_login)).getText().toString();
    	String password = ((EditText)findViewById(R.id.password_login)).getText().toString();
    	
    	
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);

        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
            	Log.v("GCM", "Already registered and on server");

            } else {
                
            	Log.v("GCM", "Not registered and on server");

            }
        }
           
    	
    	
    	//On crŽŽ notre futur User
		AppMoment.getInstance().user = new User(email);
    	
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
                String id = null;
				try {
					id = response.getString("id");
					
					// Dans les infos du user on rajoute son id
					AppMoment.getInstance().user.setId(Integer.parseInt(id));
					
					System.out.println("Email et id :"+AppMoment.getInstance().user.getEmail()+" / "+AppMoment.getInstance().user.getId());
					Log.d("Cookie", MomentApi.myCookieStore.getCookies().toString());
					
					/**
					 * Test InvitŽs
					 */
					/*

					JSONArray users = new JSONArray();
					
				   	   
				   	JSONObject user2 = new JSONObject();
				   	user2.put("email", "j@j.com");
			   	   users.put(user2);
			   	   
			   	JSONObject user3 = new JSONObject();
			   	user3.put("email", "f@f.com");
		   	   users.put(user3);
			   	   
				       
				       JSONObject object = new JSONObject();
				       object.put("users", users);
				       
				       StringEntity se = null;
						try {
							se = new StringEntity(object.toString());
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
				       
				     MomentApi.postJSON(getApplicationContext(), "newguests/1", se, new JsonHttpResponseHandler() {
				            @Override
				            public void onSuccess(JSONObject response) {
				            		System.out.println(response.toString());
				            	}
				            });
				     */
				     /**
				      * FIN TEST
				      */
				       
					
					// On recupere les infos du user et go la timeline
					MomentApi.get("user", null, new JsonHttpResponseHandler() {
			            @Override
			            public void onSuccess(JSONObject response) {
			            	try {
								String firstname = response.getString("firstname");
								AppMoment.getInstance().user.setFirstname(firstname);
								
								String lastname = response.getString("lastname");
								AppMoment.getInstance().user.setLastname(lastname);
								
								if (response.has("profile_picture_url")){
									String profile_picture_url = response.getString("profile_picture_url");
									AppMoment.getInstance().user.setPicture_profile_url(profile_picture_url);
								}
								
								
								Intent intent = new Intent(MomentActivity.this, TimelineActivity.class);
							    startActivity(intent);
						    	
								
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			            }
			        });
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

                // Do something with the response
                System.out.println(id);
            }
        });
    	
    }
    
}
