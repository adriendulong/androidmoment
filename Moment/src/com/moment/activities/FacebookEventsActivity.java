package com.moment.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.moment.R;
import com.moment.models.FbEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FacebookEventsActivity extends SherlockActivity {

    private Session session;
    private JSONArray events;
    private JSONObject eventOwner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_events);
        
        Bundle extras = getIntent().getExtras();
        session = (Session) extras.get("session");

        try {
            events = new JSONArray(getIntent().getStringExtra("events"));
            for(int i = 0; i < events.length(); i++)
            {
                eventToMoment(events.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void eventToMoment(JSONObject event) throws JSONException {

        FbEvent fbEvent = new FbEvent();

        fbEvent.setFacebookId(event.getString("eid"));

        fbEvent.setAddress(event.getString("location"));
        fbEvent.setDescription(event.getString("description"));
        fbEvent.setName(event.getString("name"));
        fbEvent.setPrivacy(event.getString("privacy"));

        fbEvent.setStartDate(event.getString("start_time"));
        fbEvent.setEndDate(event.getString("end_time"));

        fbEvent.setOwner_facebookId(event.getString("creator"));

        Request.executeBatchAndWait(getUserInfo(event.getString("creator")));

        fbEvent.setOwner_firstname(eventOwner.getString("first_name"));
        fbEvent.setOwner_picture_url(eventOwner.getString("pic"));

        Log.e("FbEvent", fbEvent.toString());
    }

    public Request getUserInfo(String userFacebookId) {
        String fqlQuery = "SELECT first_name, pic FROM user WHERE uid='"+ userFacebookId +"'";
        Bundle params = new Bundle();
        params.putString("q", fqlQuery);
        Request request = new Request(session, "/fql", params, HttpMethod.GET,
                new Request.Callback() {

                    @Override
                    public void onCompleted(Response response) {
                        JSONArray user = null;
                        try {
                            user = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
                            eventOwner = user.getJSONObject(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        return request;
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

    private class EventHandleTask extends AsyncTask<Void, Void, Void>
    {
        private JSONObject event;

        public EventHandleTask(JSONObject event) {
            this.event = event;
        }

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }
}
