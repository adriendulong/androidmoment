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
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.MomentApi;
import com.moment.models.FbEvent;
import com.moment.models.Moment;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
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
                try {

                    eventToMoment(events.getJSONObject(i));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void eventToMoment(JSONObject event) throws JSONException, ParseException {

        FbEvent fbEvent = new FbEvent();

        if(event.has("id")) { fbEvent.setFacebookId(event.getString("id")); }
        if(event.has("name")) { fbEvent.setName(event.getString("name")); }
        if(event.has("location")) { fbEvent.setAddress(event.getString("location")); }
        if(event.has("description")) { fbEvent.setDescription(event.getString("description")); }
        if(event.has("name")) { fbEvent.setName(event.getString("name")); }
        if(event.has("picture")) { fbEvent.setCover_photo_url(event.getJSONObject("picture").getJSONObject("data").getString("url")); }

        if(event.has("owner")) {
            if(event.getJSONObject("owner").getString("id").equals(AppMoment.getInstance().user.getFacebookId().toString()))
            {
                fbEvent.setState("0");
            } else {
                fbEvent.setOwner_facebookId(event.getJSONObject("owner").getString("id"));
                fbEvent.setOwner_firstname(event.getJSONObject("owner").getString("name"));
                getUserInfo(fbEvent.getOwner_facebookId(), fbEvent);
            }
        }

        if(event.has("rsvp_status") && fbEvent.getState() == null) {
            if(event.getString("rsvp_status").equals("attending")) { fbEvent.setState("2"); }
            if(event.getString("rsvp_status").equals("maybe")) { fbEvent.setState("5"); }
            if(event.getString("rsvp_status").equals("not answer")) { fbEvent.setState("4"); }
        }

        DateTimeFormatter parser = ISODateTimeFormat.dateTimeNoMillis();

        if(event.has("start_time"))
        {
            if(event.getString("is_date_only").equals("false"))
            {
                DateTime start = parser.parseDateTime(event.getString("start_time"));
                String start_date = start.getYear() + "-" + start.getMonthOfYear() + "-" +start.getDayOfMonth();
                String start_time = start.getHourOfDay() + ":" + start.getMinuteOfHour();

                fbEvent.setStartDate(start_date);
                fbEvent.setStartTime(start_time);
            } else {
                fbEvent.setStartDate(event.getString("start_time"));
            }
        }

        if(event.has("end_time"))
        {
            if(event.getString("is_date_only").equals("false"))
            {
                DateTime end = parser.parseDateTime(event.getString("end_time"));
                String end_date = end.getYear() + "-" + end.getMonthOfYear() + "-" + end.getDayOfMonth();
                String end_time = end.getHourOfDay() + ":" + end.getMinuteOfHour();

                fbEvent.setEndDate(end_date);
                fbEvent.setEndTime(end_time);
            } else {
                fbEvent.setEndDate(event.getString("end_time"));
            }
        } else {
            if(event.getString("is_date_only").equals("false"))
            {
                DateTime end = parser.parseDateTime(event.getString("start_time"));
                String end_date = end.getYear() + "-" + end.getMonthOfYear() + "-" + (end.getDayOfMonth() + 1);
                String end_time = end.getHourOfDay() + ":" + end.getMinuteOfHour();

                fbEvent.setEndDate(end_date);
                fbEvent.setEndTime(end_time);
            } else {
                DateTime dt = DateTime.parse(event.getString("start_time"));
                String year = String.valueOf(dt.getYear());
                String month = String.valueOf(dt.getMonthOfYear());
                String day = String.valueOf(dt.getDayOfMonth() + 1);
                fbEvent.setEndTime(year + "-" + month + "-" +day);
            }
        }

        uploadEvent(fbEvent);
    }

    public void getUserInfo(String userFacebookId, final FbEvent fbEvent) {
        String fqlQuery = "SELECT pic FROM user WHERE uid='"+ userFacebookId +"'";
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
