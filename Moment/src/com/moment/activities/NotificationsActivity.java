package com.moment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
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
    private int positionTab;
    private DisplayMetrics metrics;
    private TextView defaultNotifs;

    private int NOTIFICATIONS = 0;
    private int INVITATIONS = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_activity);

        //init position
        positionTab = NOTIFICATIONS;

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
        notifsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notification selectedNotifs = new Notification();
                if (positionTab==NOTIFICATIONS) selectedNotifs = notifications.get(position);
                else selectedNotifs = invitations.get(position);
                Intent intent = new Intent(NotificationsActivity.this, MomentInfosActivity.class);
                intent.putExtra("precedente", "notifs");
                intent.putExtra("type_id", selectedNotifs.getTypeNotif());
                intent.putExtra("moment_id", selectedNotifs.getMoment().getId());
                startActivity(intent);
            }
        });

        //We also create the invit adapter
        invitations = new ArrayList<Notification>();
        adapterInvits = new NotificationsAdapter(this, R.layout.notifs_invit, invitations, INVITATIONS);

        //Get graphic elements
        orangeIndicator = (ImageView)findViewById(R.id.orange_indicator);
        orangeIndicator.getLayoutParams().width = metrics.widthPixels/2;

        defaultNotifs = (TextView)findViewById(R.id.default_notifs);
    }

    @Override
    public void onStart(){
        super.onStart();
        EasyTracker.getInstance().activityStart(this);

        if(notifications.size()==0){
            //Init list
            for(int i=0;i<AppMoment.getInstance().user.getNotifications().size();i++){
                notifications.add(AppMoment.getInstance().user.getNotifications().get(i));
            }
            adapterNotifs.notifyDataSetChanged();

            if(notifications.size()==0){
                notifsListView.setVisibility(View.GONE);
            }
        }


        if(invitations.size()==0){
            //We init with the invitations otherwise we go to get it
            if(AppMoment.getInstance().user.getInvitations()!=null){
                for(int i=0;i<AppMoment.getInstance().user.getInvitations().size();i++){
                    invitations.add(AppMoment.getInstance().user.getInvitations().get(i));
                }
            }
            else{
                MomentApi.get("invitations", null, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            JSONArray notifsObject = response.getJSONArray("invitations");

                            for (int i = 0; i < notifsObject.length(); i++) {

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
        if(positionTab!=NOTIFICATIONS){
            positionTab = NOTIFICATIONS;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)orangeIndicator.getLayoutParams();
            params.setMargins(0,0,0,0);
            orangeIndicator.setLayoutParams(params);

            notifsListView.setAdapter(adapterNotifs);
        }

        if(notifications.size()==0){
            notifsListView.setVisibility(View.GONE);
            defaultNotifs.setText(R.string.notifs_vide);
        }
        else{
            notifsListView.setVisibility(View.VISIBLE);
        }

    }

    public void invitations(View view){
        Log.e("INVITS", "HERE INVITS");
        if(positionTab!=INVITATIONS){
            positionTab = INVITATIONS;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)orangeIndicator.getLayoutParams();
            params.setMargins(metrics.widthPixels/2,0,0,0);
            orangeIndicator.setLayoutParams(params);

            notifsListView.setAdapter(adapterInvits);
            /*
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
            */

        }
        if(invitations.size()==0){
            notifsListView.setVisibility(View.GONE);
            defaultNotifs.setText(R.string.invits_vide);
        }
        else{
            notifsListView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this); // Add this method.
    }
}