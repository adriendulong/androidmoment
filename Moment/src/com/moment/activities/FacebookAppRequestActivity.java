package com.moment.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.models.Moment;

import java.util.ArrayList;

public class FacebookAppRequestActivity extends Activity {
    private Moment moment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        ArrayList<String> fbids = intent.getStringArrayListExtra("fbids");
        moment = AppMoment.getInstance().user.getMomentById(intent.getLongExtra("momentId", -1));

        String fbidlist = "";
        for (String fbid : fbids) {
            fbidlist = fbidlist + fbid + ",";
        }
        fbidlist = fbidlist.substring(0, fbidlist.length() - 1);
        sendRequest(fbidlist);
    }

    public void sendRequest(String facebookId) {
        Bundle params = new Bundle();
        params.putString("title", getString(R.string.app_facebook_invit_title));
        params.putString("message", AppMoment.getInstance().user.getFirstName() + " " + AppMoment.getInstance().user.getLastName() + getString(R.string.app_fb_text1) + moment.getName() + getString(R.string.app_fb_text2));
        params.putString("to", facebookId);

        WebDialog requestsDialog = (
                new WebDialog.RequestsDialogBuilder(this, Session.getActiveSession(), params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values, FacebookException error) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", true);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                })
                .build();
        requestsDialog.show();
    }
}
