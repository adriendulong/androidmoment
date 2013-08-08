package com.moment.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

/**
 * Created by adriendulong on 27/05/13.
 */
public class CreationPopUp extends Activity {

    private Long momentId;

    private int PRIVATE_MOMENT=0;
    private int PUBLIC_MOMENT = 2;
    private int VISIBLE_MOMENT = 1;

    //Variables to send to the server
    private int privacy;
    private Boolean isInvit = false;

    //View elements
    private ImageButton private_button;
    private ImageButton public_button;
    private ImageButton visible_button;
    private TextView labelPrivacy;
    private TextView contentPrivacy;
    private ToggleButton isInvitToggle;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creation_pop_up);

        //We get the moment id from the previous intent
        momentId = getIntent().getLongExtra("momentId", -1);

        //We initialize privacy to visible
        privacy = VISIBLE_MOMENT;


        //We get labels
        labelPrivacy = (TextView)findViewById(R.id.label_type_privacy);
        contentPrivacy = (TextView)findViewById(R.id.content_type_privacy);
        //We get buttons
        private_button = (ImageButton)findViewById(R.id.pop_up_private_btn);
        visible_button = (ImageButton)findViewById(R.id.pop_up_visible_btn);
        public_button = (ImageButton)findViewById(R.id.pop_up_public_btn);
        //We get the toggle
        isInvitToggle = (ToggleButton)findViewById((R.id.toggle_invit));
        //Middle selected
        visible_button.setSelected(true);
    }


    /**
     * Click on the private button of the pop up
     * Change the label and the privacy variable
     * @param view
     */

    public void clickPrivate(View view){
        EasyTracker.getTracker().sendEvent("Create", "button_press", "Choose Private", null);

        //We see if the moment is already private or not
        if(privacy!=PRIVATE_MOMENT){
            private_button.setSelected(true);
            private_button.setImageResource(R.drawable.picto_lock_down);

            //We change the label
            labelPrivacy.setText(getString(R.string.label_private));
            contentPrivacy.setText(getString(R.string.expl_moment_private));

            if (privacy==VISIBLE_MOMENT){
                visible_button.setSelected(false);
                visible_button.setImageResource(R.drawable.picto_friend_up);
            }
            else{
                public_button.setSelected(false);
                public_button.setImageResource(R.drawable.picto_public_up);
            }

            privacy = PRIVATE_MOMENT;
            //We also put isInvit to false
            isInvit = false;
            isInvitToggle.setChecked(false);
            isInvitToggle.setEnabled(true);
        }
    }


    /**
     * Click on the visible (middle) button of the pop up
     * Change the label and the privacy variable
     * @param view
     */

    public void clickVisible(View view){
        EasyTracker.getTracker().sendEvent("Create", "button_press", "Choose Visible", null);

        //We see if the moment is already private or not
        if(privacy!=VISIBLE_MOMENT){
            visible_button.setSelected(true);
            visible_button.setImageResource(picto_friend_down);

            //We change the label
            labelPrivacy.setText(getString(R.string.label_visible));
            contentPrivacy.setText(getString(R.string.expl_moment_visible));

            if (privacy==PRIVATE_MOMENT){
                private_button.setSelected(false);
                private_button.setImageResource(R.drawable.picto_lock_up);
            }
            else{
                public_button.setSelected(false);
                public_button.setImageResource(R.drawable.picto_public_up);
            }

            privacy = VISIBLE_MOMENT;

            //We also put isInvit to false
            isInvit = false;
            isInvitToggle.setChecked(false);
            isInvitToggle.setEnabled(true);
        }
    }


    /**
     * Click on the visible (middle) button of the pop up
     * Change the label and the privacy variable
     * @param view
     */

    public void clickPublic(View view){
        EasyTracker.getTracker().sendEvent("Create", "button_press", "Choose Public", null);

        //We see if the moment is already private or not
        if(privacy!=PUBLIC_MOMENT){
            public_button.setSelected(true);
            public_button.setImageResource(picto_public_down);

            //We change the label
            labelPrivacy.setText(getString(R.string.label_public));
            contentPrivacy.setText(getString(R.string.expl_moment_public));

            if (privacy==PRIVATE_MOMENT){
                private_button.setSelected(false);
                private_button.setImageResource(R.drawable.picto_lock_up);
            }
            else{
                visible_button.setSelected(false);
                visible_button.setImageResource(R.drawable.picto_friend_up);
            }

            privacy = PUBLIC_MOMENT;

            //We also put isInvit to yes
            isInvit = true;
            isInvitToggle.setChecked(true);
            isInvitToggle.setEnabled(false);
        }
    }


    /**
     * Change the isInvit parameter
     * @param view
     */

    public void invitChange(View view){
        EasyTracker.getTracker().sendEvent("Create", "button_press", "Change isOpenInvit", null);
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            // Enable invit
            isInvit = true;
        } else {
            // Disable invit
            isInvit = false;
        }
    }


    /**
     * Click on "Invit friends". Should lead to the invit view and send modif to the server
     * @param view
     */

    public void inivitFriends(View view){
        EasyTracker.getTracker().sendEvent("Create", "button_press", "Go to Invite Friends", null);

        //We build the request parameters
        RequestParams params = new RequestParams();
        params.put("privacy", String.valueOf(privacy));
        if(isInvit) params.put("isOpenInvit", "1");
        else params.put("isOpenInvit", "0");

        //We send the informations to the server
        MomentApi.post("moment/"+momentId, params , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {

                //We set this two parameter in the moment
                AppMoment.getInstance().user.getMomentById(momentId).setPrivacy(privacy);
                AppMoment.getInstance().user.getMomentById(momentId).setIsOpenInvit(isInvit);


                Intent intent = new Intent();
                intent.putExtra("privacy", privacy);
                intent.putExtra("isOpenInvit", isInvit);
                setResult(RESULT_OK, intent);
                //We close the pop up and come back to the creation screen
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
        EasyTracker.getInstance().activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this); // Add this method.
    }
}