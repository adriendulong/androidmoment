package com.moment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import com.moment.util.CommonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class NotificationsActivity extends SherlockActivity {

    private ListView notifsListView;
    private NotificationsAdapter adapterNotifs, adapterInvits;
    private ArrayList<Notification> notifications;
    private ArrayList<Notification> invitations;
    private ImageView orangeIndicator;
    private int positionTab;
    private DisplayMetrics metrics;
    private TextView defaultNotifs;
    private int NOTIFICATIONS = 0;
    private int INVITATIONS = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_activity);


        positionTab = NOTIFICATIONS;


        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        notifications = new ArrayList<Notification>();
        notifsListView = (ListView) findViewById(R.id.list_notifs);


        adapterNotifs = new NotificationsAdapter(this, R.layout.notifs_cell, notifications, NOTIFICATIONS);
        notifsListView.setAdapter(adapterNotifs);
        notifsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notification selectedNotifs = new Notification();
                if (positionTab == NOTIFICATIONS) selectedNotifs = notifications.get(position);
                else selectedNotifs = invitations.get(position);
                Intent intent = new Intent(NotificationsActivity.this, MomentInfosActivity.class);
                intent.putExtra("precedente", "notifs");
                intent.putExtra("type_id", selectedNotifs.getTypeNotif());
                intent.putExtra("moment_id", selectedNotifs.getMoment().getId());
                startActivity(intent);
            }
        });


        invitations = new ArrayList<Notification>();
        adapterInvits = new NotificationsAdapter(this, R.layout.notifs_invit, invitations, INVITATIONS);


        orangeIndicator = (ImageView) findViewById(R.id.orange_indicator);
        orangeIndicator.getLayoutParams().width = metrics.widthPixels / 2;

        defaultNotifs = (TextView) findViewById(R.id.default_notifs);
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);

        if (notifications.size() == 0) {

            for (int i = 0; i < AppMoment.getInstance().user.getNotifications().size(); i++) {
                notifications.add(AppMoment.getInstance().user.getNotifications().get(i));
            }
            adapterNotifs.notifyDataSetChanged();

            if (notifications.size() == 0) {
                notifsListView.setVisibility(View.GONE);
            }
        }


        if (invitations.size() == 0) {

            if (AppMoment.getInstance().user.getInvitations() != null) {
                for (int i = 0; i < AppMoment.getInstance().user.getInvitations().size(); i++) {
                    invitations.add(AppMoment.getInstance().user.getInvitations().get(i));
                }
            } else {

                if(AppMoment.getInstance().checkInternet() == true)
                {
                    MomentApi.get("invitations", null, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(JSONObject response) {
                            try {
                                JSONArray notifsObject = response.getJSONArray("invitations");

                                for (int i = 0; i < notifsObject.length(); i++) {

                                    Notification notif = new Notification();
                                    notif.setFromJson(notifsObject.getJSONObject(i));
                                    invitations.add(notif);

                                    AppMoment.getInstance().notificationDao.insertOrReplace(notif);
                                }

                                AppMoment.getInstance().user.setNotifications(invitations);


                                if(AppMoment.getInstance().user != null)
                                {
                                    AppMoment.getInstance().userDao.update(AppMoment.getInstance().user);
                                }

                                Log.e("NB INVITATIONS", "" + AppMoment.getInstance().user.getInvitations().size());

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
        } else {
            invitations = new ArrayList<Notification>(AppMoment.getInstance().notificationDao.loadAll());
            ArrayList<Notification> invitationsTemp = new ArrayList<Notification>();
            for(Notification invit : invitations)
            {
                if(invit.getTypeNotif() == 0)
                {
                    invitationsTemp.add(invit);
                }
            }
            invitations = new ArrayList<Notification>(invitationsTemp);
            invitationsTemp.clear();
            AppMoment.getInstance().user.setNotifications(invitations);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

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

    public void notifications(View view) {

        if (positionTab != NOTIFICATIONS) {
            positionTab = NOTIFICATIONS;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) orangeIndicator.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            orangeIndicator.setLayoutParams(params);

            notifsListView.setAdapter(adapterNotifs);
        }

        if (notifications.size() == 0) {
            notifsListView.setVisibility(View.GONE);
            defaultNotifs.setText(R.string.notifs_vide);
        } else {
            notifsListView.setVisibility(View.VISIBLE);
        }

    }

    public void invitations(View view) {

        if (positionTab != INVITATIONS) {
            positionTab = INVITATIONS;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) orangeIndicator.getLayoutParams();
            params.setMargins(metrics.widthPixels / 2, 0, 0, 0);
            orangeIndicator.setLayoutParams(params);

            notifsListView.setAdapter(adapterInvits);


        }
        if (invitations.size() == 0) {
            notifsListView.setVisibility(View.GONE);
            defaultNotifs.setText(R.string.invits_vide);
        } else {
            notifsListView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }
}