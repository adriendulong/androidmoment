package com.moment.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.analytics.tracking.android.EasyTracker;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.CommonUtilities;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.List;

public class CreationActivity extends SherlockActivity {

	public static Typeface fontNumans;

    private Bundle bundle;
    private Session session;
    private String facebookUserId;
    private ProgressDialog dialog;
    private EditText edit_nom_moment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);

        bundle = savedInstanceState;

        dialog = new ProgressDialog(this);
        dialog.setMessage(getResources().getString(R.string.dialog_import_fb));

        CommonUtilities.disableHardwareRendering(getWindow().getDecorView());
        
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edit_nom_moment = (EditText)findViewById(R.id.edit_nom_moment);

        edit_nom_moment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    valideNom(v);
                    handled = true;
                }
                return handled;
            }
        });

        fontNumans = Typeface.createFromAsset(getAssets(),
                "fonts/Numans-Regular.otf");
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    private Session openActiveSession(Activity activity, boolean allowLoginUI,
                                      Session.StatusCallback callback, List<String> permissions, Bundle savedInstanceState) {
        Session.OpenRequest openRequest = new Session.OpenRequest(activity).
                setPermissions(permissions).setLoginBehavior(SessionLoginBehavior.
                SSO_WITH_FALLBACK).setCallback(callback).
                setDefaultAudience(SessionDefaultAudience.FRIENDS);

        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, fbStatusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED) || allowLoginUI) {
                session.openForRead(openRequest);
                return session;
            }
        }
        return null;
    }

    private Session.StatusCallback fbStatusCallback = new Session.StatusCallback() {
        public void call(Session session, SessionState state, Exception exception) {
            if (state.isOpened()) {
                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                    public void onCompleted(GraphUser user, Response response) {
                        if (response != null) {
                            try {
                                facebookUserId = user.getId();
                                getUserEvents();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
            }
        }
    };

    public void getUserEvents() {
        Bundle params = new Bundle();
        params.putString("fields","id,cover,description,is_date_only,name,owner,location,privacy,rsvp_status,start_time,end_time,admins,picture");

        if(AppMoment.getInstance().user.getFacebookId() == null)
        {
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    AppMoment.getInstance().user.setFacebookId(Long.parseLong(user.getId()));
                }
            });
        }

        Request request = new Request(session, "me/events", params, HttpMethod.GET,
                new Request.Callback() {

                    @Override
                    public void onCompleted(Response response) {
                        JSONArray events = null;
                        try {
                            events = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
                            Log.d("EVENTS", events.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                        Intent intent = new Intent(getApplication(), FacebookEventsActivity.class);
                        intent.putExtra("events", events.toString());
                        intent.putExtra("session", session);
                        startActivity(intent);
                    }
                });
        Request.executeBatchAsync(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void facebook(View view) {
        try {
            dialog.show();
            openActiveSession(this, true, fbStatusCallback, Arrays.asList(
                    new String[]{"user_events"}), bundle);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession()
                .onActivityResult(this, requestCode, resultCode, data);
    }

    public void valideNom(View view) {
        EditText nomMoment = (EditText)findViewById(R.id.edit_nom_moment);

        if(!nomMoment.getText().toString().matches("")){
        	
        	Bundle bundle = new Bundle();  
    	    bundle.putString("nomMoment", nomMoment.getText().toString());

    	    Intent intent = new Intent(CreationActivity.this, CreationDetailsActivity.class);
    	    intent.putExtras(bundle);
    	    startActivity(intent);
        } else {
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
