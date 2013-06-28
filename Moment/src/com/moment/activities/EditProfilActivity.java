package com.moment.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.MomentApi;
import com.moment.models.User;

import org.json.JSONObject;

public class EditProfilActivity extends SherlockActivity implements View.OnClickListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modif_profile);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button modif = (Button) findViewById(R.id.modif);
        Button valider = (Button) findViewById(R.id.btn_valider);

        EditText modif_prenom = (EditText) findViewById(R.id.modif_prenom);
        modif_prenom.setText(AppMoment.getInstance().user.getFirstName());

        EditText modif_nom = (EditText) findViewById(R.id.modif_nom);
        modif_nom.setText(AppMoment.getInstance().user.getLastName());

        EditText phone = (EditText) findViewById(R.id.phone);
        phone.setText(AppMoment.getInstance().user.getNumTel());

        EditText secondPhone = (EditText) findViewById(R.id.secondPhone);
        secondPhone.setText(AppMoment.getInstance().user.getSecondNumTel());

        EditText secondEmail = (EditText) findViewById(R.id.secondEmail);
        secondEmail.setText(AppMoment.getInstance().user.getSecondEmail());

        EditText description = (EditText) findViewById(R.id.description);
        description.setText(AppMoment.getInstance().user.getDescription());

        /*valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestParams requestParams = new RequestParams();
                requestParams.put("firstname",   modif_prenom.getText().toString());
                requestParams.put("lastname",    modif_nom.getText().toString());
                requestParams.put("phone",       phone.getText().toString());
                requestParams.put("secondPhone", secondPhone.getText().toString());
                requestParams.put("secondEmail", secondEmail.getText().toString());
                requestParams.put("description", description.getText().toString());

                // TODO Photo

                MomentApi.post("/user", requestParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        AppMoment.getInstance().user.setUserFromJson(response);
                    }
                });
            }
        });*/
    }

    @Override
    public void onClick(View v) {

    }
}
