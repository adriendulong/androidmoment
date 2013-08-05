package com.moment.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
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
import com.moment.classes.CommonUtilities;
import com.moment.classes.DatabaseHelper;
import com.moment.classes.RoundTransformation;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.Images;
import com.moment.classes.MomentApi;
import com.moment.models.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfilActivity extends SherlockActivity implements View.OnClickListener {

    private static final int GALLERY_PICTURE = 1;
    private static final int CAMERA_PICTURE = 0;
    private static final int YOUR_SELECT_PICTURE_REQUEST_CODE = 2;
    private EditText modif_prenom;
    private EditText modif_nom;
    private EditText phone;
    private EditText adress;
    private EditText secondPhone;
    private EditText secondEmail;
    private EditText description;
    private ImageView profil_picture;
    private Uri mImageCaptureUri;
    private ProgressDialog progressDialog;
    private String facebookId;
    private Session session;
    private AlertDialog alertDialog;
    private final Transformation roundTrans = new RoundTransformation();
    private Uri outputFileUri;
    private Intent intent;
    private File file;

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
        float pxBitmap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
        Picasso.with(this).load(AppMoment.getInstance().user.getPictureProfileUrl()).resize((int) pxBitmap, (int) pxBitmap).transform(roundTrans).into(profil_picture);

        modif_prenom = (EditText) findViewById(R.id.modif_prenom);
        modif_prenom.setText(AppMoment.getInstance().user.getFirstName());

        modif_nom = (EditText) findViewById(R.id.modif_nom);
        modif_nom.setText(AppMoment.getInstance().user.getLastName());

        adress = (EditText) findViewById(R.id.adress);
        adress.setText(AppMoment.getInstance().user.getAddress());

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
                valider();
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

    private void valider() {
        progressDialog = ProgressDialog.show(EditProfilActivity.this, "Informations utilisateur", "Mise à jour");

        RequestParams requestParams = new RequestParams();
        if(!modif_prenom.getText().equals(modif_prenom.getHint()) && !modif_prenom.getText().equals(null))
        {
            requestParams.put("firstname", modif_prenom.getText().toString());
            AppMoment.getInstance().user.setFirstName(modif_prenom.getText().toString());
        }
        if(!modif_nom.getText().equals(modif_nom.getHint()) && !modif_nom.getText().equals(null))
        {
            requestParams.put("lastname",    modif_nom.getText().toString());
            AppMoment.getInstance().user.setLastName(modif_nom.getText().toString());
        }
        if(!adress.getText().equals(adress.getHint()) && !adress.getText().equals(null))
        {
            requestParams.put("phone",       adress.getText().toString());
            AppMoment.getInstance().user.setAddress(adress.getText().toString());
        }
        if(!phone.getText().equals(modif_nom.getHint()) && !phone.getText().equals(null))
        {
            if(CommonUtilities.isValidTel(String.valueOf(phone.getText())))
            {
                requestParams.put("phone", phone.getText().toString());
                AppMoment.getInstance().user.setNumTel(phone.getText().toString());
            } else {
                Toast.makeText(EditProfilActivity.this, "Numero de telephone invalide", Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
                return;
            }
        }
        if(!secondPhone.getText().equals(modif_nom.getHint()) && !secondPhone.getText().equals(null))
        {
            if(CommonUtilities.isValidTel(String.valueOf(secondPhone.getText())))
            {
                requestParams.put("phone",       secondPhone.getText().toString());
                AppMoment.getInstance().user.setSecondNumTel(secondPhone.getText().toString());
            } else {
                Toast.makeText(EditProfilActivity.this, "Second numero de telephone invalide", Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
                return;
            }
        }
        if(!secondEmail.getText().equals(modif_nom.getHint()) && !secondEmail.getText().equals(null))
        {
            if(isEmailAdress(String.valueOf(secondEmail.getText())))
            {
                requestParams.put("secondEmail", secondEmail.getText().toString());
                AppMoment.getInstance().user.setSecondEmail(secondEmail.getText().toString());
            } else {
                Toast.makeText(EditProfilActivity.this, "Adresse email invalide", Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
                return;
            }
        }
        if(!description.getText().equals(modif_nom.getHint()) && !description.getText().equals(null))
        {
            requestParams.put("description", description.getText().toString());
            AppMoment.getInstance().user.setDescription(description.getText().toString());
        }
        if(facebookId != null)
        {
            requestParams.put("facebookId", facebookId);
            AppMoment.getInstance().user.setFacebookId(Long.valueOf(facebookId));
        }

        File image = getApplicationContext().getFileStreamPath("profile_picture");
        if (image != null){
            try {
                Log.v("FILE", ""+image.getTotalSpace());
                requestParams.put("photo", image);
            } catch(FileNotFoundException e) { e.printStackTrace(); }
        }

        MomentApi.post("user", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {

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
                                AppMoment.getInstance().user.setFacebookId(Long.valueOf(facebookId));
                                RequestParams params = new RequestParams();
                                params.put("facebookId", facebookId);
                                MomentApi.post("user", params, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        Log.d("User mod", response.toString());
                                        AppMoment.getInstance().userDao.update(AppMoment.getInstance().user);
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
        getSupportMenuInflater().inflate(R.menu.activity_custom_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.validate:
                valider();
                 return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void touchedPhoto(View view){
        openImageIntent();
    }

    private void openImageIntent() {

        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Moment" + File.separator + "Images");
        System.out.println(Environment.getExternalStorageDirectory() + File.separator + "Moment" + File.separator + "Images");
        root.mkdirs();
        final String fname = "profile_picture.jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, YOUR_SELECT_PICTURE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (Session.getActiveSession()!=null) Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);


        if(resultCode == RESULT_OK)
        {
            if(requestCode == YOUR_SELECT_PICTURE_REQUEST_CODE)
            {
                final boolean isCamera;
                if(data == null)
                {
                    isCamera = true;
                }
                else
                {
                    final String action = data.getAction();
                    if(action == null)
                    {
                        isCamera = false;
                    }
                    else
                    {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if(isCamera)
                {
                    selectedImageUri = outputFileUri;
                }
                else
                {
                    selectedImageUri = data == null ? null : data.getData();
                }

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    float pxBitmap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
                    Picasso.with(this).load(outputFileUri).resize((int) pxBitmap, (int) pxBitmap).transform(roundTrans).into(profil_picture);
                    bitmap = Images.resizeBitmap(bitmap, 300);
                    Images.saveImageToInternalStorage(bitmap, getApplicationContext(), "profile_picture", 100);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
                Session.getActiveSession()
                        .onActivityResult(this, requestCode, resultCode, data);
            }
        }
    }

    public static boolean isEmailAdress(String email){
        Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$");
        Matcher m = p.matcher(email.toUpperCase());
        return m.matches();
    }

    public static boolean isPhoneNumber(String phone){
        Pattern p = Pattern.compile("(0|0033|\\\\+33)[1-9]((([0-9]{2}){4})|((\\\\s[0-9]{2}){4})|((-[0-9]{2}){4}))");
        Matcher m = p.matcher(phone.toUpperCase());
        return m.matches();
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
