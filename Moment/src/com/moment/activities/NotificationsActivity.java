package com.moment.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.NotificationsAdapter;
import com.moment.models.Notification;

import java.util.ArrayList;

/**
 * Created by adriendulong on 21/06/13.
 */
public class NotificationsActivity extends SherlockActivity {

    private ListView notifsListView;
    private NotificationsAdapter adapter;
    private ArrayList<Notification> notifications;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_activity);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //List view of notifs
        notifications = new ArrayList<Notification>();
        notifsListView = (ListView)findViewById(R.id.list_notifs);
        adapter = new NotificationsAdapter(this, R.layout.notifs_cell, notifications);
        notifsListView.setAdapter(adapter);
    }

    @Override
    public void onStart(){
        super.onStart();

        Log.e("NB NOTIF", ".... "+ AppMoment.getInstance().user.getNotifications().size());

        for(int i=0;i<20;i++){
            Notification notif = new Notification();
            notifications.add(notif);
        }
        adapter.notifyDataSetChanged();
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
}