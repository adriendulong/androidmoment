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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        if(isPhoneNumber(phone))
        {
            params.put("phone", phone);
        } else {
            editPhoneAlert();
        }

        MomentApi.post("user", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    AppMoment.getInstance().user.setNumTel(response.getString("phone"));
                    AppMoment.getInstance().userDao.update(AppMoment.getInstance().user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Intent intent = new Intent(getApplication(), TimelineActivity.class);
        startActivity(intent);
    }

    public static boolean isPhoneNumber(String phone){
        Pattern p = Pattern.compile("(0|0033|\\\\+33)[1-9]((([0-9]{2}){4})|((\\\\s[0-9]{2}){4})|((-[0-9]{2}){4}))");
        Matcher m = p.matcher(phone.toUpperCase());
        return m.matches();
    }

    public void editPhoneAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InscriptionActivityStep2.this);

        alertDialogBuilder.setTitle("Num�ro de t�l�phone incorrect");

        alertDialogBuilder
                .setMessage("Corriger le num�ro")
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
}
