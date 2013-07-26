package com.moment.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;

import java.util.ArrayList;

/**
 * Created by swann on 23/07/13.
 */
public class FacebookAppRequestActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        ArrayList<String> fbids = intent.getStringArrayListExtra("fbids");
        String fbidlist = "";
        for(String fbid: fbids)
        {
            fbidlist = fbidlist + fbid + ",";
        }
        fbidlist = fbidlist.substring(0, fbidlist.length()-1);
        sendRequest(fbidlist);
    }

    public void sendRequest(String facebookId){
        Bundle params = new Bundle();
        params.putString("title", "Send a Request");
        params.putString("message", "Learn how to make your Android apps social");
        params.putString("to", facebookId);

        WebDialog requestsDialog = (
                new WebDialog.RequestsDialogBuilder(this, Session.getActiveSession(), params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values, FacebookException error) {
                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK,returnIntent);
                        finish();
                    }
                })
                .build();
        requestsDialog.show();
    }
}
