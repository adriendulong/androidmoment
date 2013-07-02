package com.moment.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.MomentApi;

import org.json.JSONException;
import org.json.JSONObject;

public class InscriptionActivityStep2 extends SherlockActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription_2);
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

        RequestParams params = new RequestParams();
        params.put("phone", phone);

        MomentApi.post("user", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    AppMoment.getInstance().user.setNumTel(response.getString("phone"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Intent intent = new Intent(getApplication(), TimelineActivity.class);
        startActivity(intent);
    }

}
