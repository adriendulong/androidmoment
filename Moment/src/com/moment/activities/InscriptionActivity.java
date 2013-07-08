package com.moment.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.facebook.internal.SessionTracker;
import com.facebook.internal.Utility;
import com.facebook.model.GraphUser;
import com.google.android.gcm.GCMRegistrar;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.CommonUtilities;
import com.moment.classes.Images;
import com.moment.classes.MomentApi;
import com.moment.models.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InscriptionActivity extends SherlockActivity {

    private Uri outputFileUri;
    private int YOUR_SELECT_PICTURE_REQUEST_CODE = 0;
    private Bitmap profile_picture;
    private URL user_pic;
    private File pictureDir;
    private File pictureOut;
    private EditText nomEdit;
    private EditText prenomEdit;
    private EditText emailEdit;
    private EditText mdpEdit ;
    private EditText birthdate;
    private Button male;
    private Button female;
    private ImageButton user_picture;

    private List<NameValuePair> params = new ArrayList<NameValuePair>(2);

    private String gender;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        pictureDir = getApplication().getCacheDir();
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

        nomEdit = (EditText)findViewById(R.id.inscription_nom);
        prenomEdit = (EditText)findViewById(R.id.inscription_prenom);
        emailEdit = (EditText)findViewById(R.id.inscription_email);
        mdpEdit = (EditText)findViewById(R.id.inscription_mdp);
        birthdate = (EditText) findViewById(R.id.birthdate);
        male = (Button) findViewById(R.id.btn_male);
        female = (Button) findViewById(R.id.btn_female);
        user_picture = (ImageButton)findViewById(R.id.profile_picture);
        params = new ArrayList<NameValuePair>(2);

        SessionTracker mSessionTracker = new SessionTracker(getBaseContext(), new Session.StatusCallback() {

            @Override
            public void call(Session session, SessionState state, Exception exception) {
            }
        }, null, false);

        String applicationId = Utility.getMetadataApplicationId(getBaseContext());
        Session mCurrentSession = mSessionTracker.getSession();

        if (mCurrentSession == null || mCurrentSession.getState().isClosed()) {
            mSessionTracker.setSession(null);
            Session session = new Session.Builder(getBaseContext()).setApplicationId(applicationId).build();
            Session.setActiveSession(session);
            mCurrentSession = session;
        }

        if (!mCurrentSession.isOpened()) {
            Session.OpenRequest openRequest = null;
            openRequest = new Session.OpenRequest(InscriptionActivity.this);

            if (openRequest != null) {
                openRequest.setDefaultAudience(SessionDefaultAudience.FRIENDS);
                openRequest.setPermissions(Arrays.asList("user_birthday", "email"));
                openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);

                mCurrentSession.openForRead(openRequest);

                Request.executeMeRequestAsync(mCurrentSession, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        ProfilePictureTask profilePictureTask = new ProfilePictureTask(user);
                        profilePictureTask.execute();

                        Log.e("myConsultant", user.getId() + " " + user.getName() + " " + user.getInnerJSONObject());
                    }
                });

            }

        }else {
            Request.executeMeRequestAsync(mCurrentSession, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    ProfilePictureTask profilePictureTask = new ProfilePictureTask(user);
                    profilePictureTask.execute();
                    Log.e("myConsultant", user.getId() + " " + user.getName() + " " + user.getInnerJSONObject());
                }
            });
        }
    }

    public void inscription(View view) throws JSONException, FileNotFoundException {

        String nom = nomEdit.getText().toString();
        String prenom = prenomEdit.getText().toString();
        String email = emailEdit.getText().toString();
        String mdp = mdpEdit.getText().toString();
        String bdate = birthdate.getText().toString();


        RequestParams params = new RequestParams();
        params.put("firstname", prenom);
        params.put("lastname", nom);
        params.put("password", mdp);

        if(isEmailAdress(email))
        {
            params.put("email", email);
        } else {
            editEmailAlert();
        }

        params.put("birth_date", bdate);
        params.put("sex", gender);

        if (!GCMRegistrar.getRegistrationId(this).equals("")) params.put("notif_id", GCMRegistrar.getRegistrationId(this));

        params.put("os", "1");
        params.put("os_version", android.os.Build.VERSION.RELEASE);
        params.put("model", CommonUtilities.getDeviceName());
        params.put("device_id", AppMoment.getInstance().tel_id);

        if(pictureOut != null) {
            params.put("photo", pictureOut);
            pictureOut.deleteOnExit();
        } else {
            File image = getApplicationContext().getFileStreamPath("profile_picture");
            if (image != null){
                try {
                    params.put("photo", image);
                    image.delete();
                } catch(FileNotFoundException e) { e.printStackTrace(); }
            }
        }

        MomentApi.initialize(getApplicationContext());
        MomentApi.post("register", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject response) {

                System.out.print("Inscription Step 1 OK");

                Long id = Long.parseLong("-1");

                try {
                    id = response.getLong("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AppMoment.getInstance().user.setId(id);

            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                System.out.println(errorResponse.toString());
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

    public void selectImage(View view){
        openImageIntent();
    }


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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession()
                .onActivityResult(this, requestCode, resultCode, data);

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
        gender = "M";
    }

    public void setFemale(View view){
        gender = "F";
    }

    public static boolean isEmailAdress(String email){
        Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$");
        Matcher m = p.matcher(email.toUpperCase());
        return m.matches();
    }

    public void editEmailAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InscriptionActivity.this);

        alertDialogBuilder.setTitle("Adresse mail incorrect");

        alertDialogBuilder
                .setMessage("Corriger")
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private class ProfilePictureTask extends AsyncTask<Void, Void, Bitmap> {

        private GraphUser user;

        public ProfilePictureTask(GraphUser user) {
            this.user = user;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            try {
                user_pic = new URL("http://graph.facebook.com/"+user.getId()+"/picture?width=200&height=200");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            assert user_pic != null;
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(user_pic.openConnection().getInputStream());
                pictureOut = File.createTempFile("usr_profil", ".jpg", pictureDir);
                FileOutputStream fileOutputStream = new FileOutputStream(pictureOut);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap){
            if(user != null)
            {
                prenomEdit.setText(user.getFirstName());
                nomEdit.setText(user.getLastName());
                birthdate.setText(user.getBirthday());
                emailEdit.setText(user.asMap().get("email").toString());
                if(user.asMap().get("gender").toString().equals("male")) {
                    gender = "M"; } else { gender = "F"; }
            }

            if(bitmap != null)
                user_picture.setImageBitmap(Images.getRoundedCornerBitmap(bitmap));
        }
    }
}
