package com.moment.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.actionbarsherlock.app.SherlockFragmentActivity;
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
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.MomentApi;
import com.moment.models.User;
import com.moment.util.CommonUtilities;
import com.moment.util.Images;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class InscriptionActivity extends SherlockFragmentActivity {

    public static final String PROPERTY_REG_ID = "registration_id";
    public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;
    static final String TAG = "GCMDemo";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME =
            "onServerExpirationTimeMs";
    private static Button birthdate;

    String SENDER_ID = CommonUtilities.SENDER_ID;
    GoogleCloudMessaging gcm;
    Context context;
    private String regid;
    private Uri outputFileUri;
    private int YOUR_SELECT_PICTURE_REQUEST_CODE = 0;
    private Bitmap profile_picture;
    private URL user_pic;
    private File pictureDir;
    private File pictureOut;
    private EditText nomEdit;
    private EditText prenomEdit;
    private EditText emailEdit;
    private EditText mdpEdit;
    private Button male;
    private Button female;
    private ImageButton user_picture;
    private String gender;
    private Bundle bundle;
    private boolean isSuccess = false;
    private ProgressDialog dialog;
    private Session.StatusCallback fbStatusCallback = new Session.StatusCallback() {
        public void call(Session session, SessionState state, Exception exception) {
            if (state.isOpened()) {
                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                    public void onCompleted(GraphUser user, Response response) {
                        if (response != null) {
                            try {

                                prenomEdit.setText(user.getFirstName());
                                nomEdit.setText(user.getLastName());

                                emailEdit.setText(user.getProperty("email").toString());

                                birthdate.setText(user.getBirthday());

                                if (user.getProperty("gender").toString().equals("male")) {
                                    setMale(male);
                                } else {
                                    setFemale(female);
                                }

                                ProfilePictureTask profilePictureTask = new ProfilePictureTask(user);
                                profilePictureTask.execute();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
            }
        }
    };

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            Log.v(TAG, "" + packageInfo.versionCode);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        pictureDir = getApplication().getCacheDir();

        bundle = null;
        //GCM
        //Take care of GCM
        context = getApplicationContext();
        regid = getRegistrationId(context);
        regid = "";

        if (regid.length() == 0) {
            registerBackground();
        }
        Log.v(TAG, "REGID : " + regid);
        gcm = GoogleCloudMessaging.getInstance(this);


        nomEdit = (EditText) findViewById(R.id.inscription_nom);
        prenomEdit = (EditText) findViewById(R.id.inscription_prenom);
        emailEdit = (EditText) findViewById(R.id.inscription_email);
        mdpEdit = (EditText) findViewById(R.id.inscription_mdp);
        birthdate = (Button) findViewById(R.id.birthdate);
        male = (Button) findViewById(R.id.btn_male);
        female = (Button) findViewById(R.id.btn_female);
        user_picture = (ImageButton) findViewById(R.id.profile_picture);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.pop_up_facebook_fill_body))
                .setTitle(getResources().getString(R.string.pop_up_facebook_fill_title));

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                facebookConnect();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();

    }

    public void facebookConnect() {
        try {
            openActiveSession(this, true, fbStatusCallback, Arrays.asList(
                    new String[]{"email", "user_birthday"}), bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Session openActiveSession(Activity activity, boolean allowLoginUI,
                                      Session.StatusCallback callback, List<String> permissions, Bundle savedInstanceState) {
        Session.OpenRequest openRequest = new Session.OpenRequest(activity).
                setPermissions(permissions).setLoginBehavior(SessionLoginBehavior.
                SSO_WITH_FALLBACK).setCallback(callback).
                setDefaultAudience(SessionDefaultAudience.FRIENDS);
        Session session = null;
        if (session == null) {
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

    public void inscription(View view) {

        String nom = nomEdit.getText().toString();
        String prenom = prenomEdit.getText().toString();
        String email = emailEdit.getText().toString();
        String mdp = mdpEdit.getText().toString();
        String bdate = birthdate.getText().toString();

        if ((nom.length() > 0) && (prenom.length() > 0) && (email.length() > 0) && (mdp.length() > 0)) {
            if (CommonUtilities.isValidEmail(email)) {
                java.util.Date d = null;

                try {
                    d = CommonUtilities.dateFormatSlash.parse(bdate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                RequestParams params = new RequestParams();
                params.put("firstname", prenom);
                params.put("lastname", nom);
                params.put("password", mdp);
                params.put("email", email);

                if (d != null) {
                    String birth_date = String.valueOf(d.getTime());
                    params.put("birth_date", "" + d.getTime() / 1000);
                }

                if ((gender != null) && (gender.equals("M") || gender.equals("F")))
                    params.put("sex", gender);

                if (regid.length() != 0) params.put("notif_id", regid);

                params.put("os", "1");
                params.put("os_version", android.os.Build.VERSION.RELEASE);
                params.put("model", CommonUtilities.getDeviceName());
                params.put("device_id", AppMoment.getInstance().tel_id);

                if (pictureOut != null) {
                    try {
                        params.put("photo", pictureOut);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                } else {

                    File image = getApplicationContext().getFileStreamPath("profile_picture");
                    if (image != null) {
                        try {
                            params.put("photo", image);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

                MomentApi.initialize(getApplicationContext());
                dialog = ProgressDialog.show(this, null, "Inscription");

                MomentApi.post("register", params, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        isSuccess = true;

                        Long id = Long.parseLong("-1");

                        try {
                            id = response.getLong("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        AppMoment.getInstance().user = new User();
                        AppMoment.getInstance().user.setId(id);
                        AppMoment.getInstance().userDao.insert(AppMoment.getInstance().user);

                        //We also save in shared prefs
                        SharedPreferences sharedPreferences = getSharedPreferences(AppMoment.PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("userID", id);
                        editor.commit();

                        MomentApi.get("user", null, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                AppMoment.getInstance().user.setUserFromJson(response);

                                if(AppMoment.getInstance().user != null)
                                {
                                    AppMoment.getInstance().userDao.update(AppMoment.getInstance().user);
                                }

                                dialog.dismiss();
                                Intent intent = new Intent(getApplication(), InscriptionActivityStep2.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onFailure(Throwable e, JSONObject errorResponse) {
                                dialog.dismiss();
                            }
                        });
                    }


                    @Override
                    public void onFailure(Throwable e, JSONObject errorResponse) {
                        isSuccess = true;
                        dialog.dismiss();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InscriptionActivity.this);
                        alertDialogBuilder.setTitle(getResources().getString(R.string.title_problem_inscription));
                        alertDialogBuilder
                                .setMessage(getResources().getString(R.string.title_problem_inscription))
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();


                        AppMoment.getInstance().user = null;
                        MomentApi.myCookieStore.clear();
                    }

                    public void onFinish() {

                        if (!isSuccess) {
                            dialog.dismiss();
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InscriptionActivity.this);
                            alertDialogBuilder.setTitle(getResources().getString(R.string.title_problem_inscription));
                            alertDialogBuilder
                                    .setMessage(getResources().getString(R.string.title_problem_inscription))
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();


                            AppMoment.getInstance().user = null;
                            MomentApi.myCookieStore.clear();
                        }
                    }
                });

            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InscriptionActivity.this);
                alertDialogBuilder.setTitle(getResources().getString(R.string.invalid_email_inscription_title));
                alertDialogBuilder
                        .setMessage(getResources().getString(R.string.invalid_email_inscription_body))
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                emailEdit.requestFocus();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

        }

        else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InscriptionActivity.this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.missing_infos_title));
            alertDialogBuilder
                    .setMessage(getResources().getString(R.string.missing_infos_body))
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
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

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void selectImage(View view) {
        openImageIntent();
    }

    public void retour(View view) {
        Intent intent = new Intent(this, MomentActivity.class);
        startActivity(intent);
        finish();
    }

    private void openImageIntent() {

        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Moment" + File.separator + "Images");
        root.mkdirs();
        final String fname = "profile_picture.jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
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

        final Intent chooserIntent = Intent.createChooser(galleryIntent, getResources().getString(R.string.select_source));

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, YOUR_SELECT_PICTURE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (Session.getActiveSession() != null)
            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if (requestCode == YOUR_SELECT_PICTURE_REQUEST_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                    ImageButton profile_picture_button = (ImageButton) findViewById(R.id.profile_picture);
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

    public void setMale(View view) {
        gender = "M";
        male.setBackgroundColor(getResources().getColor(R.color.orange));
        female.setBackgroundColor(getResources().getColor(R.color.white));
    }

    public void setFemale(View view) {
        gender = "F";
        female.setBackgroundColor(getResources().getColor(R.color.orange));
        male.setBackgroundColor(getResources().getColor(R.color.white));
    }

    public void editEmailAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InscriptionActivity.this);

        alertDialogBuilder.setTitle("Adresse mail incorrect");

        alertDialogBuilder
                .setMessage("Corriger")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.length() == 0) {
            Log.v(TAG, "Registration not found.");
            return "";
        }
        // check if app was updated; if so, it must clear registration id to
        // avoid a race condition if GCM sends a message
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion || isRegistrationExpired()) {
            Log.v(TAG, "App version changed or registration expired.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(MomentActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private boolean isRegistrationExpired() {
        final SharedPreferences prefs = getGCMPreferences(context);
        long expirationTime =
                prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, -1);
        return System.currentTimeMillis() > expirationTime;
    }

    private void registerBackground() {

        Thread thread = new Thread(new Runnable() {
            public void run() {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration id=" + regid;

                    setRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                Log.v(TAG, msg);
            }
        }
        );
        thread.start();
    }

    private void setRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        long expirationTime = System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;

        editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
        editor.commit();
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

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            birthdate.setText(day + "/" + (month + 1) + "/" + year);
        }
    }

    private class ProfilePictureTask extends AsyncTask<Void, Void, Bitmap> {

        private GraphUser user;

        public ProfilePictureTask(GraphUser user) {
            this.user = user;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            try {
                user_pic = new URL("http://graph.facebook.com/" + user.getId() + "/picture?width=200&height=200");
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
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null)
                user_picture.setImageBitmap(Images.getRoundedCornerBitmap(bitmap));
        }
    }
}
