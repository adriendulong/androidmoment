package com.moment.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.analytics.tracking.android.EasyTracker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.MomentApi;

import org.json.JSONObject;

import static com.moment.R.drawable.picto_friend_down;
import static com.moment.R.drawable.picto_public_down;

public class CreationPopUp extends Activity {

    private Long momentId;
    private int PRIVATE_MOMENT = 0;
    private int PUBLIC_MOMENT = 2;
    private int VISIBLE_MOMENT = 1;
    private int privacy;
    private Boolean isInvit = false;
    private ImageButton private_button;
    private ImageButton public_button;
    private ImageButton visible_button;
    private TextView labelPrivacy;
    private TextView contentPrivacy;
    private ToggleButton isInvitToggle;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creation_pop_up);

        momentId = getIntent().getLongExtra("momentId", -1);
        privacy = VISIBLE_MOMENT;

        labelPrivacy = (TextView) findViewById(R.id.label_type_privacy);
        contentPrivacy = (TextView) findViewById(R.id.content_type_privacy);

        private_button = (ImageButton) findViewById(R.id.pop_up_private_btn);
        visible_button = (ImageButton) findViewById(R.id.pop_up_visible_btn);
        public_button = (ImageButton) findViewById(R.id.pop_up_public_btn);

        isInvitToggle = (ToggleButton) findViewById((R.id.toggle_invit));

        visible_button.setSelected(true);
    }

    public void clickPrivate(View view) {
        EasyTracker.getTracker().sendEvent("Create", "button_press", "Choose Private", null);

        if (privacy != PRIVATE_MOMENT) {
            private_button.setSelected(true);
            private_button.setImageResource(R.drawable.picto_lock_down);

            labelPrivacy.setText(getString(R.string.label_private));
            contentPrivacy.setText(getString(R.string.expl_moment_private));

            if (privacy == VISIBLE_MOMENT) {
                visible_button.setSelected(false);
                visible_button.setImageResource(R.drawable.picto_friend_up);
            } else {
                public_button.setSelected(false);
                public_button.setImageResource(R.drawable.picto_public_up);
            }

            privacy = PRIVATE_MOMENT;

            isInvit = false;
            isInvitToggle.setChecked(false);
            isInvitToggle.setEnabled(true);
        }
    }

    public void clickVisible(View view) {
        EasyTracker.getTracker().sendEvent("Create", "button_press", "Choose Visible", null);


        if (privacy != VISIBLE_MOMENT) {
            visible_button.setSelected(true);
            visible_button.setImageResource(picto_friend_down);


            labelPrivacy.setText(getString(R.string.label_visible));
            contentPrivacy.setText(getString(R.string.expl_moment_visible));

            if (privacy == PRIVATE_MOMENT) {
                private_button.setSelected(false);
                private_button.setImageResource(R.drawable.picto_lock_up);
            } else {
                public_button.setSelected(false);
                public_button.setImageResource(R.drawable.picto_public_up);
            }

            privacy = VISIBLE_MOMENT;


            isInvit = false;
            isInvitToggle.setChecked(false);
            isInvitToggle.setEnabled(true);
        }
    }

    public void clickPublic(View view) {
        EasyTracker.getTracker().sendEvent("Create", "button_press", "Choose Public", null);


        if (privacy != PUBLIC_MOMENT) {
            public_button.setSelected(true);
            public_button.setImageResource(picto_public_down);


            labelPrivacy.setText(getString(R.string.label_public));
            contentPrivacy.setText(getString(R.string.expl_moment_public));

            if (privacy == PRIVATE_MOMENT) {
                private_button.setSelected(false);
                private_button.setImageResource(R.drawable.picto_lock_up);
            } else {
                visible_button.setSelected(false);
                visible_button.setImageResource(R.drawable.picto_friend_up);
            }

            privacy = PUBLIC_MOMENT;


            isInvit = true;
            isInvitToggle.setChecked(true);
            isInvitToggle.setEnabled(false);
        }
    }

    public void invitChange(View view) {
        EasyTracker.getTracker().sendEvent("Create", "button_press", "Change isOpenInvit", null);

        boolean on = ((ToggleButton) view).isChecked();

        if (on) {

            isInvit = true;
        } else {

            isInvit = false;
        }
    }

    public void inivitFriends(View view) {
        EasyTracker.getTracker().sendEvent("Create", "button_press", "Go to Invite Friends", null);


        RequestParams params = new RequestParams();
        params.put("privacy", String.valueOf(privacy));
        if (isInvit) params.put("isOpenInvit", "1");
        else params.put("isOpenInvit", "0");


        MomentApi.post("moment/" + momentId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {


                AppMoment.getInstance().user.getMomentById(momentId).setPrivacy(privacy);
                AppMoment.getInstance().user.getMomentById(momentId).setIsOpenInvit(isInvit);


                Intent intent = new Intent();
                intent.putExtra("privacy", privacy);
                intent.putExtra("isOpenInvit", isInvit);
                setResult(RESULT_OK, intent);

                finish();

            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                System.out.println(errorResponse.toString());

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }
}