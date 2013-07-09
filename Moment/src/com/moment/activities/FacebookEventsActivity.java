package com.moment.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.moment.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FacebookEventsActivity extends SherlockActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_events);
        
        Bundle extras = getIntent().getExtras();
        try {
            JSONArray events = new JSONArray(getIntent().getStringExtra("events"));
            Log.d("events", events.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }



        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
