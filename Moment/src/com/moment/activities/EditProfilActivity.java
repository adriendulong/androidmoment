package com.moment.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.analytics.tracking.android.EasyTracker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.Images;
import com.moment.classes.MomentApi;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class EditProfilActivity extends SherlockActivity implements View.OnClickListener {

    private EditText modif_prenom;
    private EditText modif_nom;
    private EditText phone;
    private EditText secondPhone;
    private EditText secondEmail;
    private EditText description;
    private ImageView profil_picture;
    private Uri mImageCaptureUri;
    private ProgressDialog progressDialog;
    private String facebookId;
    private Session session;
    private AlertDialog alertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modif_profile);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button modif = (Button) findViewById(R.id.modif);
        Button valider = (Button) findViewById(R.id.btn_valider);

        ImageButton facebook = (ImageButton) findViewById(R.id.edit_profil_fb);

        profil_picture = (ImageView) findViewById(R.id.profil_picture_edit);

        modif_prenom = (EditText) findViewById(R.id.modif_prenom);
        modif_prenom.setText(AppMoment.getInstance().user.getFirstName());

        modif_nom = (EditText) findViewById(R.id.modif_nom);
        modif_nom.setText(AppMoment.getInstance().user.getLastName());

        EditText email = (EditText) findViewById(R.id.email);
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

                progressDialog = ProgressDialog.show(EditProfilActivity.this, "Informations utilisateur", "Mise à jour");

                RequestParams requestParams = new RequestParams();
                if(modif_prenom.getText() != modif_prenom.getHint() && modif_prenom.getText() != null)
                    requestParams.put("firstname",   modif_prenom.getText().toString());
                if(modif_nom.getText() != modif_nom.getHint() && modif_nom.getText() != null)
                    requestParams.put("lastname",    modif_nom.getText().toString());
                if(phone.getText() != phone.getHint() && phone.getText() != null)
                    requestParams.put("phone",       phone.getText().toString());
                if(secondPhone.getText() != secondPhone.getHint() && secondPhone.getText() != null)
                    requestParams.put("secondPhone", secondPhone.getText().toString());
                if(secondEmail.getText() != secondEmail.getHint() && secondEmail.getText() != null)
                    requestParams.put("secondEmail", secondEmail.getText().toString());
                if(description.getText() != description.getHint() && description.getText() != null)
                    requestParams.put("description", description.getText().toString());
                if(facebookId != null)
                    requestParams.put("facebookId", facebookId);
                if(mImageCaptureUri != null) {
                    File photo = new File(mImageCaptureUri.getPath());
                    try {
                        requestParams.put("photo", photo);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                MomentApi.post("user", requestParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        AppMoment.getInstance().user.setUserFromJson(response);
                        AppMoment.getInstance().userDao.update(AppMoment.getInstance().user);
                        progressDialog.cancel();
                    }

                    @Override
                    public void onFailure(Throwable e, JSONObject response){
                        e.printStackTrace();
                        progressDialog.cancel();
                        Toast.makeText(EditProfilActivity.this, "Une erreur s'est produite", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(EditProfilActivity.this, "Facebook", "Recuperation des informations");
                openActiveSession(EditProfilActivity.this, true, fbStatusCallback, Arrays.asList(
                        new String[]{"email"}), new Bundle());
            }
        });
    }

    private Session openActiveSession(Activity activity, boolean allowLoginUI,
                                      Session.StatusCallback callback, List<String> permissions, Bundle savedInstanceState) {
        Session.OpenRequest openRequest = new Session.OpenRequest(activity).
                setPermissions(permissions).setLoginBehavior(SessionLoginBehavior.
                SSO_WITH_FALLBACK).setCallback(callback).
                setDefaultAudience(SessionDefaultAudience.FRIENDS);

//        session = null;

        if (session == null) {
            Log.d("", "" + savedInstanceState);
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, fbStatusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED) || allowLoginUI) {
                session.openForRead(openRequest);
                return session;
            }
        }
        return null;
    }

    private Session.StatusCallback fbStatusCallback = new Session.StatusCallback() {
        public void call(Session session, SessionState state, Exception exception) {
            if (state.isOpened()) {
                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                    public void onCompleted(GraphUser user, Response response) {
                        if (response != null) {
                            try {
                                facebookId = user.getId();
                                RequestParams params = new RequestParams();
                                params.put("facebookId", facebookId);
                                MomentApi.post("user", params, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        Log.d("User mod", response.toString());
                                        progressDialog.cancel();
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditProfilActivity.this);
                                        alertDialogBuilder
                                                .setTitle("Compte Facebook")
                                                .setMessage("Compte Facebook Lié")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        alertDialog.dismiss();
                                                    }
                                                });
                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("", "Exception e");
                            }

                        }
                    }
                });
            }
        }
    };

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

    public void touchedPhoto(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mImageCaptureUri = null;
        takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                mImageCaptureUri);
        startActivityForResult(takePictureIntent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            Bundle extras = data.getExtras();
            Bitmap mImageBitmap;
            if (extras != null) {
                mImageBitmap = (Bitmap) extras.get("data");
                profil_picture.setImageBitmap(Images.getRoundedCornerBitmap(mImageBitmap));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            Session.getActiveSession()
                    .onActivityResult(this, requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {

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
