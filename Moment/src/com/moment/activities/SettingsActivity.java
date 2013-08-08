package com.moment.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.MomentApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends SherlockActivity implements View.OnClickListener {

    static int cpt;
    private int INVITATION = 0, NEW_PHOTO = 2, NEW_CHAT = 3, MODIF_MOMENT = 1;
    private ImageButton invitPush, photoPush, modifPush, chatPush, invitMail, photoMail, chatMail, modifMail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_params);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        invitPush = (ImageButton) findViewById(R.id.invitPush);
        photoPush = (ImageButton) findViewById(R.id.photoPush);
        modifPush = (ImageButton) findViewById(R.id.modifPush);
        chatPush = (ImageButton) findViewById(R.id.chatPush);
        invitMail = (ImageButton) findViewById(R.id.invitMail);
        photoMail = (ImageButton) findViewById(R.id.photoMail);
        chatMail = (ImageButton) findViewById(R.id.chatMail);
        modifMail = (ImageButton) findViewById(R.id.modifMail);

        cpt = 0;

        ImageButton facebook = (ImageButton) findViewById(R.id.btn_fb);

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyTracker.getTracker().sendEvent("Settings", "button_press", "Go FB Fan Page", null);
                Intent intent;
                try {
                    getApplication().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/277911125648059"));
                } catch (PackageManager.NameNotFoundException e) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/appmoment"));
                }

                startActivity(intent);
            }
        });

        ImageButton twitter = (ImageButton) findViewById(R.id.btn_twit);

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyTracker.getTracker().sendEvent("Settings", "button_press", "Go Twitter Page", null);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/appmoment"));
                startActivity(intent);
            }
        });

        ImageButton coeur = (ImageButton) findViewById(R.id.coeur);

        coeur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyTracker.getTracker().sendEvent("Settings", "button_press", "Love", null);
                cpt++;
                if (cpt == 6) {
                    cpt = 0;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.appmoment.fr"));
                    startActivity(intent);
                }
            }
        });

        Button contact = (Button) findViewById(R.id.btn_contact);

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyTracker.getTracker().sendEvent("Settings", "button_press", "Contact Moment", null);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mailto:hello@appmoment.fr"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Hello Moment, un petit mot");
                intent.putExtra(Intent.EXTRA_TEXT, "C'était juste pour vous dire");

                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

        Button feedback = (Button) findViewById(R.id.feedback);

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyTracker.getTracker().sendEvent("Settings", "button_press", "Feedback", null);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mailto:hello@appmoment.fr"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Moment - Feedback");
                intent.putExtra(Intent.EXTRA_TEXT, "Une petite remarque : \n");

                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

        Button cgu = (Button) findViewById(R.id.cgu);

        cgu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyTracker.getTracker().sendEvent("Settings", "button_press", "CGU", null);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.appmoment.fr/cgu"));
                startActivity(intent);
            }
        });
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

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onStart() {
        super.onStart();
        updatePush();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    public void disconnect(View view) {
        EasyTracker.getTracker().sendEvent("Settings", "button_press", "Disconnect", null);
        AppMoment.getInstance().disconnect();

        MomentApi.get("logout/" + AppMoment.getInstance().tel_id, null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject response) {
                Log.d("DISCONNECT", "Disonnected");

                MomentApi.myCookieStore.clear();
                Intent startIntent = new Intent(SettingsActivity.this, MomentActivity.class);
                startActivity(startIntent);

            }

            @Override
            public void onFailure(Throwable error, String content) {
                Log.d("DISCONNECT", "Pb :" + content);


                MomentApi.myCookieStore.clear();
                Intent startIntent = new Intent(SettingsActivity.this, MomentActivity.class);
                startActivity(startIntent);

            }

        });


    }

    private void updatePush() {
        MomentApi.get("paramsnotifs", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject response) {

                try {
                    JSONArray notifs = response.getJSONArray("params_notifs");
                    for (int i = 0; i < notifs.length(); i++) {
                        JSONObject tempNotif = notifs.getJSONObject(i);

                        if (tempNotif.getInt("type_notif") == INVITATION) {
                            if (tempNotif.getBoolean("push")) invitPush.setSelected(true);
                            if (tempNotif.getBoolean("mail")) invitMail.setSelected(true);

                        } else if (tempNotif.getInt("type_notif") == NEW_PHOTO) {
                            if (tempNotif.getBoolean("push")) photoPush.setSelected(true);
                            if (tempNotif.getBoolean("mail")) photoMail.setSelected(true);

                        } else if (tempNotif.getInt("type_notif") == NEW_CHAT) {
                            if (tempNotif.getBoolean("push")) chatPush.setSelected(true);
                            if (tempNotif.getBoolean("mail")) chatMail.setSelected(true);

                        } else if (tempNotif.getInt("type_notif") == MODIF_MOMENT) {
                            if (tempNotif.getBoolean("push")) modifPush.setSelected(true);
                            if (tempNotif.getBoolean("mail")) modifMail.setSelected(true);

                        }
                    }

                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                }


            }

            @Override
            public void onFailure(Throwable error, String content) {


            }

        });
    }

    public void modifParamsPush(final View viewPush) {
        EasyTracker.getTracker().sendEvent("Settings", "button_press", "Update Push Params", null);

        if (viewPush.isSelected()) viewPush.setSelected(false);
        else viewPush.setSelected(true);

        MomentApi.get("paramsnotifs/1/" + viewPush.getTag(), null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject response) {
            }

            @Override
            public void onFailure(Throwable error, String content) {
                if (viewPush.isSelected()) viewPush.setSelected(false);
                else viewPush.setSelected(true);

            }

        });

    }

    public void modifParamsMail(final View viewPush) {
        EasyTracker.getTracker().sendEvent("Settings", "button_press", "Update Mail Params", null);

        if (viewPush.isSelected()) viewPush.setSelected(false);
        else viewPush.setSelected(true);

        MomentApi.get("paramsnotifs/0/" + viewPush.getTag(), null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject response) {
            }

            @Override
            public void onFailure(Throwable error, String content) {
                if (viewPush.isSelected()) viewPush.setSelected(false);
                else viewPush.setSelected(true);

            }

        });

    }

    public void modifProfile(View view) {
        Intent intentProfile = new Intent(SettingsActivity.this, EditProfilActivity.class);
        startActivity(intentProfile);
    }
}
