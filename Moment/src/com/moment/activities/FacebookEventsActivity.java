package com.moment.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;

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
import com.moment.util.CommonUtilities;

import org.apache.http.entity.StringEntity;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;

public class FacebookEventsActivity extends SherlockActivity {

    private Session session;
    private JSONArray events;
    private JSONObject eventOwner;
    private AlertDialog alertDialog;
    private int cursor;
    private int fail = 0;
    private JSONArray invites;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_events);
        Bundle extras = getIntent().getExtras();
        session = (Session) extras.get("session");

        try {
            events = new JSONArray(getIntent().getStringExtra("events"));
            cursor = events.length();
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

        if(event.has("cover")){
            fbEvent.setCover_photo_url(event.getJSONObject("cover").getString("source"));
        }
        else{
            if(event.has("picture")) { fbEvent.setCover_photo_url(event.getJSONObject("picture").getJSONObject("data").getString("url")); }
        }

        if(event.has("invited"))
        {
            invites = new JSONArray();
            JSONArray invitesJSON = event.getJSONObject("invited").getJSONArray("data");
            for(int i = 0; i < invitesJSON.length(); i++)
            {
                String[] name = invitesJSON.getJSONObject(i).getString("name").split("\\s+");
                JSONObject invitJSON = new JSONObject();
                invitJSON.put("facebookId", invitesJSON.getJSONObject(i).getLong("id"));
                invitJSON.put("firstname", name[0]);
                invitJSON.put("lastname", name[1]);
                invites.put(invitJSON);
            }

        }

        if(event.has("owner")) {
            if(event.getJSONObject("owner").getString("id").equals(String.valueOf(AppMoment.getInstance().user.getFacebookId())))
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
            if(event.getString("rsvp_status").equals("unsure")) { fbEvent.setState("5"); }
            if(event.getString("rsvp_status").equals("not answer")) { fbEvent.setState("4"); }
        }

        if(event.has("start_time"))
        {
            if(event.getString("is_date_only").equals("false"))
            {
                DateTime start = CommonUtilities.dateFormatISONoMillis.parseDateTime(event.getString("start_time"));
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
                DateTime end = CommonUtilities.dateFormatISONoMillis.parseDateTime(event.getString("end_time"));
                String end_date = end.getYear() + "-" + end.getMonthOfYear() + "-" + end.getDayOfMonth();
                String end_time = end.getHourOfDay() + ":" + end.getMinuteOfHour();

                fbEvent.setEndDate(end_date);
                fbEvent.setEndTime(end_time);
            } else {
                fbEvent.setEndDate(event.getString("end_date"));
            }
        } else {
            if(event.getString("is_date_only").equals("false"))
            {
                DateTime end = CommonUtilities.dateFormatISONoMillis.parseDateTime(event.getString("start_time"));
                String end_date = end.getYear() + "-" + end.getMonthOfYear() + "-" + (end.getDayOfMonth() + 1);
                String end_time = end.getHourOfDay() + ":" + end.getMinuteOfHour();

                fbEvent.setEndDate(end_date);
                fbEvent.setEndTime(end_time);
            } else {
                DateTime dt = DateTime.parse(event.getString("start_time"));
                String year = String.valueOf(dt.getYear());
                String month = String.valueOf(dt.getMonthOfYear());
                String day = String.valueOf(dt.getDayOfMonth() + 1);
                fbEvent.setEndDate(year + "-" + month + "-" +day);
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
                    if(AppMoment.getInstance().momentDao.load(moment.getId()) != null)
                    {
                        fail++;
                    }

                    cursor --;
                    if(cursor == 0)
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FacebookEventsActivity.this);
                        alertDialogBuilder
                                .setTitle("Facebook")
                                .setMessage(getResources().getString(R.string.pop_up_end_import_fb))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(getApplicationContext(), TimelineActivity.class);
                                        startActivity(intent);
                                        alertDialog.dismiss();
                                    }
                                });
                        alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(invites != null)
                {
                    StringEntity entity = null;
                    try {
                        entity = new StringEntity(new JSONObject().put("users", invites).toString());
                        entity.setContentType("application/json");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    MomentApi.postJSON(FacebookEventsActivity.this, "newguests/"+ moment.getId(), entity, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            result.toString();
                        }

                        @Override
                        public void onFailure(Throwable e, JSONObject response) {
                            e.printStackTrace();
                            response.toString();
                        }
                    });
                }

            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                cursor --;
                fail ++;

                if(cursor == 0)
                {
                    Intent intent = new Intent(getApplicationContext(), TimelineActivity.class);
                    startActivity(intent);
                }
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
