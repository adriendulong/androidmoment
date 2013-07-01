package com.moment.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.MomentApi;
import com.moment.models.User;

import org.json.JSONException;
import org.json.JSONObject;

public class EditProfilActivity extends SherlockActivity implements View.OnClickListener {

    EditText modif_prenom;
    EditText modif_nom;
    EditText email;
    EditText phone;
    EditText secondPhone;
    EditText secondEmail;
    EditText description;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modif_profile);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button modif = (Button) findViewById(R.id.modif);
        Button valider = (Button) findViewById(R.id.btn_valider);

        modif_prenom = (EditText) findViewById(R.id.modif_prenom);
        modif_prenom.setText(AppMoment.getInstance().user.getFirstName());

        modif_nom = (EditText) findViewById(R.id.modif_nom);
        modif_nom.setText(AppMoment.getInstance().user.getLastName());

        email = (EditText) findViewById(R.id.email);
        email.setText(AppMoment.getInstance().user.getEmail());

        phone = (EditText) findViewById(R.id.phone);
        phone.setText(AppMoment.getInstance().user.getNumTel());

        secondPhone = (EditText) findViewById(R.id.secondPhone);
        secondPhone.setText(AppMoment.getInstance().user.getSecondNumTel());

        secondEmail = (EditText) findViewById(R.id.secondEmail);
        secondEmail.setText(AppMoment.getInstance().user.getSecondEmail());

        description = (EditText) findViewById(R.id.description);
        description.setText(AppMoment.getInstance().user.getDescription());

        valider.setOnClickListener(new View.OnClickListener() {

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
                        AppMoment.getInstance().user.setFirstName(modif_prenom.getText().toString());
                        AppMoment.getInstance().user.setLastName(modif_nom.getText().toString());
                        AppMoment.getInstance().userDao.update(AppMoment.getInstance().user);
                    }

                    @Override
                    public void onFailure(Throwable e, JSONObject response){
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
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
}
