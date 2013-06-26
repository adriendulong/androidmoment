package com.moment.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.MomentApi;
import com.moment.classes.NotificationsAdapter;
import com.moment.models.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by adriendulong on 21/06/13.
 */
public class NotificationsActivity extends SherlockActivity {

    private ListView notifsListView;
    private NotificationsAdapter adapterNotifs, adapterInvits;
    private ArrayList<Notification> notifications;
    private ArrayList<Notification> invitations;
    private ImageView orangeIndicator;
    //Position on notif or invit
    private int position;
    private DisplayMetrics metrics;

    private int NOTIFICATIONS = 0;
    private int INVITATIONS = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_activity);

        //init position
        position = NOTIFICATIONS;

        //Get size screen
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //List view of notifs
        notifications = new ArrayList<Notification>();
        notifsListView = (ListView)findViewById(R.id.list_notifs);

        //Notifs adapter
        adapterNotifs = new NotificationsAdapter(this, R.layout.notifs_cell, notifications, NOTIFICATIONS);
        notifsListView.setAdapter(adapterNotifs);

        //We also create the invit adapter
        invitations = new ArrayList<Notification>();
        adapterInvits = new NotificationsAdapter(this, R.layout.notifs_invit, invitations, INVITATIONS);

        //Get graphic elements
        orangeIndicator = (ImageView)findViewById(R.id.orange_indicator);
        orangeIndicator.getLayoutParams().width = metrics.widthPixels/2;
    }

    @Override
    public void onStart(){
        super.onStart();

        //Init list
        for(int i=0;i<AppMoment.getInstance().user.getNotifications().size();i++){
            Log.e("TYPE", ""+AppMoment.getInstance().user.getNotifications().get(i).getTypeNotif());
            Notification notif = new Notification();
            notifications.add(AppMoment.getInstance().user.getNotifications().get(i));
        }
        adapterNotifs.notifyDataSetChanged();


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

    public void notifications(View view){
        Log.e("NOTIFS", "HERE NOTIFS");
        if(position!=NOTIFICATIONS){
            position = NOTIFICATIONS;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)orangeIndicator.getLayoutParams();
            params.setMargins(0,0,0,0);
            orangeIndicator.setLayoutParams(params);

            notifsListView.setAdapter(adapterNotifs);
        }

    }

    public void invitations(View view){
        Log.e("INVITS", "HERE INVITS");
        if(position!=INVITATIONS){
            position = INVITATIONS;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)orangeIndicator.getLayoutParams();
            params.setMargins(metrics.widthPixels/2,0,0,0);
            orangeIndicator.setLayoutParams(params);

            notifsListView.setAdapter(adapterInvits);
            //We get the invitations
            if(AppMoment.getInstance().user.getInvitations()==null){
                MomentApi.get("invitations", null, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            JSONArray notifsObject = response.getJSONArray("invitations");

                            for (int i = 0; i < notifsObject.length(); i++) {
                                Log.e("Position", ""+i);
                                Log.e("Invit", notifsObject.getJSONObject(i).toString());

                                Notification notif = new Notification();
                                notif.setFromJson(notifsObject.getJSONObject(i));
                                invitations.add(notif);
                            }

                            AppMoment.getInstance().user.setInvitations(invitations);

                            Log.e("NB INVITATIONS", ""+AppMoment.getInstance().user.getInvitations().size());


                            adapterInvits.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String content) {
                        System.out.println(content);
                    }
                });
            }


        }

    }
}