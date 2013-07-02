package com.moment.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gcm.GCMRegistrar;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.CommonUtilities;
import com.moment.classes.Images;
import com.moment.classes.MomentApi;
import com.moment.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InscriptionActivity extends SherlockActivity {

    private Uri outputFileUri;
    private int YOUR_SELECT_PICTURE_REQUEST_CODE = 0;
    private Bitmap profile_picture;
    String sex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
        } else if (GCMRegistrar.isRegisteredOnServer(this)) {
            Log.v("GCM", "Already registered and on server");
        } else {
            Log.v("GCM", "Not registered and on server");
        }
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

    public void selectImage(View view){
        openImageIntent();
    }

    public void inscription(View view) throws JSONException {
        EditText nomEdit = (EditText)findViewById(R.id.inscription_nom);
        String nom = nomEdit.getText().toString();
        EditText prenomEdit = (EditText)findViewById(R.id.inscription_prenom);
        String prenom = prenomEdit.getText().toString();
        EditText emailEdit = (EditText)findViewById(R.id.inscription_email);
        String email = emailEdit.getText().toString();
        EditText mdpEdit = (EditText)findViewById(R.id.inscription_mdp);
        String mdp = mdpEdit.getText().toString();
        EditText birthdate = (EditText) findViewById(R.id.birthdate);
        String bdate = birthdate.getText().toString();

        Button male = (Button) findViewById(R.id.btn_male);
        Button female = (Button) findViewById(R.id.btn_female);



        RequestParams params = new RequestParams();
        params.put("firstname", prenom);
        params.put("lastname", nom);
        params.put("password", mdp);
        params.put("email", email);
        params.put("birth_date", bdate);
        params.put("sex", sex);

        if (!GCMRegistrar.getRegistrationId(this).equals("")) params.put("notif_id", GCMRegistrar.getRegistrationId(this));
        params.put("os", "1");
        params.put("os_version", android.os.Build.VERSION.RELEASE);
        params.put("model", CommonUtilities.getDeviceName());
        params.put("device_id", AppMoment.getInstance().tel_id);


        File image = getApplicationContext().getFileStreamPath("profile_picture");
        if (image!=null){
            try {
                params.put("photo", image);
                image.delete();
            } catch(FileNotFoundException e) {}
        }

        AppMoment.getInstance().user = new User();
        AppMoment.getInstance().user.setEmail(email);

        AppMoment.getInstance().user.setFirstName(prenom);
        AppMoment.getInstance().user.setLastName(nom);

        MomentApi.initialize(getApplicationContext());
        MomentApi.post("register", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                Long id = Long.parseLong("-1");

                try {
                    id = response.getLong("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AppMoment.getInstance().user.setId(id);

                MomentApi.get("user", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            String firstname = response.getString("firstname");
                            AppMoment.getInstance().user.setFirstName(firstname);

                            String lastname = response.getString("lastname");
                            AppMoment.getInstance().user.setLastName(lastname);

                            if(response.has("profile_picture_url")){
                                String profile_picture_url = response.getString("profile_picture_url");
                                AppMoment.getInstance().user.setPictureProfileUrl(profile_picture_url);
                            }

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InscriptionActivity.this);

                            alertDialogBuilder.setTitle("Compte crééŽŽ");

                            alertDialogBuilder
                                    .setMessage("Le compte a ŽtŽ crŽŽ :)")
                                    .setCancelable(false)
                                    .setPositiveButton("Cool",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,int id) {
                                            Intent intent = new Intent(InscriptionActivity.this, TimelineActivity.class);
                                            startActivity(intent);
                                        }
                                    });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }


            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                System.out.println(errorResponse.toString());

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InscriptionActivity.this);

                alertDialogBuilder.setTitle("Compte crŽŽ");

                alertDialogBuilder
                        .setMessage(errorResponse.toString())
                        .setCancelable(false)
                        .setPositiveButton("Mince !",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();

                AppMoment.getInstance().user = null;
            }
        });

        Intent intent = new Intent(getApplication(), InscriptionActivityStep2.class);
        startActivity(intent);

    }


    /**
     * Retour vers le premier Žcran
     */

    public void retour(View view){
        Intent intent = new Intent(this, MomentActivity.class);
        startActivity(intent);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
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

                    ImageButton profile_picture_button = (ImageButton)findViewById(R.id.profile_picture);
                    profile_picture_button.setImageBitmap(Images.getRoundedCornerBitmap(bitmap));
                    profile_picture = Images.resizeBitmap(bitmap, 300);
                    Images.saveImageToInternalStorage(profile_picture, getApplicationContext(), "profile_picture", 100);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setMale(View view){
        sex = "M";
    }

    public void setFemale(View view){
        sex = "F";
    }

}
