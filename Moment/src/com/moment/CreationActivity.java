package com.moment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.moment.classes.FbEvent;

public class CreationActivity extends SherlockActivity {

	//Intent du choix des evenements Facebook
	Intent intentFacebookEvents;
	
	//Evenements
	ArrayList<FbEvent> events  = new ArrayList<FbEvent>();
	
	// application id from facebook.com/developers
	public static final String APP_ID = "445031162214877";
	// log tag for any log.x statements
	public static final String TAG = "FACEBOOK CONNECT";
	// permissions array
	private static final String[] PERMS = new String[] { "user_events" };
	// facebook vars
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;
	
	//La police
	public static Typeface fontNumans;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);
        
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayOptions(ActionBar.)
        
        
        
        // setup the facebook session
        mFacebook = new Facebook(APP_ID);
        mAsyncRunner = new AsyncFacebookRunner(mFacebook);
        
        //On recupere la police custom
        fontNumans = Typeface.createFromAsset(getAssets(),
                "fonts/Numans-Regular.otf");
        
        //On recupere les elements dont on veut changer la police
        Button fb =(Button)findViewById(R.id.button_facebook);
        fb.setTypeface(fontNumans);
        TextView Tfb =(TextView)findViewById(R.id.text_creation_moment);
        Tfb.setTypeface(fontNumans);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getSupportMenuInflater().inflate(R.menu.activity_creation, menu);
        return true;
    }

    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Fonction appeler lorsqu'on appuie sur le bouton Facebook
     * On recupere alors les évènements FB que l'utilisateurs a créé
     * @param view
     */
    
    public void facebook(View view) {
       Log.d("Connection", "conncetion reussi");
       
       
       if(mFacebook.isSessionValid()){
    	   mAsyncRunner.request("me/events", new EventRequestListener()); 
       }
       else mFacebook.authorize(this, PERMS, new LoginDialogListener());
       
    }
    

   /**
    * Class utilise lorsque l'utilisateur se connecte à Facebook
    * @author adriendulong
    *
    */
   
    private class LoginDialogListener implements DialogListener {
    	 
    	@Override
		public void onComplete(Bundle values) {
    		Log.d("FACEBOOK", "OK");
    		mAsyncRunner.request("me/events", new EventRequestListener());
    	}
     
    	@Override
		public void onFacebookError(FacebookError e) {
    	}
     
    	@Override
		public void onError(DialogError e) {

    	}
     
    	@Override
		public void onCancel() {
    		// TODO Auto-generated method stub
     
    	}
    }

    private class EventRequestListener implements RequestListener {
    	
    	String fbReponse;
    	 
    	@Override
		public void onComplete(String response, Object state) {
    		try {
    			// process the response here: executed in background thread
    			Log.d(TAG, "Response: " + response.toString());
    			fbReponse = response;
    			
    			final JSONObject json = new JSONObject(response);
    			JSONArray d = json.getJSONArray("data");
     
    			for (int i = 0; i < d.length(); i++) {
    				JSONObject event = d.getJSONObject(i);
    				FbEvent newEvent = new FbEvent(event.getString("id"),
    						event.getString("name"),
    						event.getString("start_time"),
    						event.getString("location"));
    				events.add(newEvent);
     
    			}
     
    			//Lorsque l'on a les evenements on les affiche dans une nouvelle activité
    			CreationActivity.this.runOnUiThread(new Runnable() {
    				@Override
					public void run() {
    					//Creer et demarrer l'activité de choix des evenemts
    		    	    Intent intent = new Intent(CreationActivity.this, FacebookEventsActivity.class);
    		    	    
    		    	   
    		    	    Bundle bundle = new Bundle();  
    		    	    bundle.putParcelableArrayList("events", events);
    		    	    
    		    	    intent.putExtras(bundle);
    		    	    startActivity(intent);
    				}
    			});
    		} catch (JSONException e) {
    			Log.w(TAG, "JSON Error in response");
    		}
    	}
     
    	@Override
		public void onIOException(IOException e, Object state) {
    		// TODO Auto-generated method stub
     
    	}
     
    	@Override
		public void onFileNotFoundException(FileNotFoundException e,
    			Object state) {
    		// TODO Auto-generated method stub
     
    	}
     
    	@Override
		public void onMalformedURLException(MalformedURLException e,
    			Object state) {
    		// TODO Auto-generated method stub
     
    	}
     
    	@Override
		public void onFacebookError(FacebookError e, Object state) {
    		// TODO Auto-generated method stub
     
    	}
    }
    
    
    /**
     * L'utilisateur valide le nom du moment qu'il a créé
     * Si un nom est bien rentré on passe à la création du moment
     * @param view
     */
    
    public void valideNom(View view) {
        Log.d("Connection", "conncetion reussi");
        
        EditText nomMoment = (EditText)findViewById(R.id.edit_nom_moment);
        
        
        //On verifie qu'un nom a été rentré
        if(!nomMoment.getText().toString().matches("")){
        	Log.d("edit", nomMoment.getText().toString());
        	
        	Bundle bundle = new Bundle();  
    	    bundle.putString("nomMoment", nomMoment.getText().toString());
        	
        	//Creer et demarrer l'activité de création d'un moment
    	    Intent intent = new Intent(CreationActivity.this, CreationDetailsActivity.class);
    	    intent.putExtras(bundle);
    	    startActivity(intent);
        }
        else{
        	//On previent l'utilisateur qu'il doit rentrer un nom afin de continuer la création
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage(R.string.alert_nom_creation_moment)
        	       .setCancelable(false)
        	       .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
        	           @Override
					public void onClick(DialogInterface dialog, int id) {
        	                dialog.cancel();
        	           }
        	       });
        	AlertDialog alert = builder.create();
        	alert.show();
        }
        
        
     }
    

}
