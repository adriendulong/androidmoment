package com.moment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.R;
import com.moment.classes.MomentApi;
import com.moment.models.FbEvent;
import com.moment.models.Moment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

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

        fbEvent.setCover_photo_url(event.getJSONObject("picture").getJSONObject("data").getString("url"));

        fbEvent.setFacebookId(event.getString("id"));

        fbEvent.setAddress(event.getString("location"));
        fbEvent.setDescription(event.getString("description"));
        fbEvent.setName(event.getString("name"));
        fbEvent.setPrivacy(event.getString("privacy"));

        fbEvent.setStartDate(event.getString("start_time"));
        fbEvent.setEndDate(event.getString("end_time"));

        fbEvent.setOwner_facebookId(event.getJSONObject("owner").getString("id"));

        getUserInfo(event.getJSONObject("owner").getString("id"), fbEvent);
    }

    public void getUserInfo(String userFacebookId, final FbEvent fbEvent) {
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
                            fbEvent.setOwner_firstname(eventOwner.getString("first_name"));
                            fbEvent.setOwner_picture_url(eventOwner.getString("pic"));
                            try {
                                uploadEvent(fbEvent);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Log.e("Final Event", fbEvent.toString());
                        } catch (JSONException e) {
                            Log.e("Failure", e.toString());
                        }
                    }
                });
        Request.executeBatchAsync(request);
    }

    public void uploadEvent(FbEvent fbEvent) throws JSONException, ParseException {

        MomentApi.post("newmoment", fbEvent.getMomentRequestParams(getApplicationContext()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                Moment moment = new Moment();
                try {
                    moment.setMomentFromJson(response);

                    Intent intent = new Intent(getApplicationContext(), TimelineActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                Log.e("Failure",errorResponse.toString());
            }
        });
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
