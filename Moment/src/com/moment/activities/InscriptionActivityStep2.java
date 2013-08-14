package com.moment.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.MomentApi;
import com.moment.util.CommonUtilities;

import org.json.JSONException;
import org.json.JSONObject;

public class InscriptionActivityStep2 extends SherlockActivity {

    private EditText phoneEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription_2);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        phoneEdit = (EditText)findViewById(R.id.phone_num);
        phoneEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    inscription(v);
                    handled = true;
                }
                return handled;
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
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void inscription(View view){
        EditText phoneEdit = (EditText)findViewById(R.id.phone_num);
        String phone = phoneEdit.getText().toString();

        if(phone.length()>0){
            RequestParams params = new RequestParams();

            if(CommonUtilities.isValidTel(phone))
            {
                params.put("phone", phone);
                MomentApi.post("user", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            AppMoment.getInstance().user.setNumTel(response.getString("phone"));

                            if(AppMoment.getInstance().user != null)
                            {
                                AppMoment.getInstance().userDao.update(AppMoment.getInstance().user);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Intent intent = new Intent(getApplication(), TimelineActivity.class);
                startActivity(intent);
            } else {
                editPhoneAlert();
            }
        }
        else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InscriptionActivityStep2.this);
            alertDialogBuilder.setTitle(getString(R.string.phone_null));
            alertDialogBuilder
                    .setMessage(getString(R.string.alert_phone_null))
                    .setCancelable(false)
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.dismiss();
                            Intent returnIntent = new Intent();
                            setResult(RESULT_OK, returnIntent);
                        }
                    })
                    .setNegativeButton(getString(R.string.tant_pis), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.dismiss();
                            Intent returnIntent = new Intent();
                            setResult(RESULT_OK, returnIntent);
                            Intent intent = new Intent(getApplication(), TimelineActivity.class);
                            startActivity(intent);
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }


    }

    public void editPhoneAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InscriptionActivityStep2.this);

        alertDialogBuilder.setTitle(getString(R.string.phone_incorrect));

        alertDialogBuilder
                .setMessage(getString(R.string.corriger_phone))
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.dismiss();
                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK, returnIntent);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
