package com.moment.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.moment.AppMoment;
import com.moment.R;
import com.moment.util.CommonUtilities;
import com.moment.classes.MomentApi;
import com.moment.models.Moment;
import com.moment.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;

public class MomentActivity extends Activity {

    private static final String EXTRA_MESSAGE = "message";
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME =
            "onServerExpirationTimeMs";
    private static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;
    private static final String TAG = "GCMDemo";
    private static Typeface fontNumans;
    private final String SENDER_ID = CommonUtilities.SENDER_ID;
    private GoogleCloudMessaging gcm;
    private Context context;
    private String regid;
    private ProgressDialog dialog;
    private boolean isSuccess = false;
    private TextView forgotPass;

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            Log.v(TAG, "" + packageInfo.versionCode);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {

            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment);

        fontNumans = Typeface.createFromAsset(getAssets(), "fonts/Numans-Regular.otf");

        forgotPass = (TextView) findViewById(R.id.forgot_pass);

        MomentApi.initialize(getApplicationContext());

        if (MomentApi.myCookieStore.getCookies().size() > 0) {

            SharedPreferences sharedPreferences = getSharedPreferences(AppMoment.PREFS_NAME, MODE_PRIVATE);

            if (!AppMoment.getInstance().userDao.loadAll().isEmpty()) {
                Long savedUserID = sharedPreferences.getLong("userID", -1);
                AppMoment.getInstance().user = AppMoment.getInstance().userDao.load(savedUserID);
                Intent intent = new Intent(MomentActivity.this, TimelineActivity.class);
                startActivity(intent);
            } else {
                AppMoment.getInstance().user = new User();
                MomentApi.get("user", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            Long id = response.getLong("id");
                            AppMoment.getInstance().user.setId(id);

                            String email = response.getString("email");
                            AppMoment.getInstance().user.setEmail(email);

                            String firstname = response.getString("firstname");
                            AppMoment.getInstance().user.setFirstName(firstname);

                            String lastname = response.getString("lastname");
                            AppMoment.getInstance().user.setLastName(lastname);

                            if (response.has("profile_picture_url")) {
                                String profile_picture_url = response.getString("profile_picture_url");
                                AppMoment.getInstance().user.setPictureProfileUrl(profile_picture_url);
                            }

                            if (AppMoment.getInstance().userDao.load(id) == null) {
                                AppMoment.getInstance().userDao.insert(AppMoment.getInstance().user);
                                if (!AppMoment.getInstance().momentDao.loadAll().isEmpty())
                                {
                                    AppMoment.getInstance().user.setMoments(AppMoment.getInstance().momentDao.loadAll());
                                    AppMoment.getInstance().userDao.update(AppMoment.getInstance().user);
                                }
                            }

                        Intent intent = new Intent(MomentActivity.this, TimelineActivity.class);
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    context = getApplicationContext();
    regid = getRegistrationId(context);
    regid = "";

    if (regid.length() == 0) {
        registerBackground();
    }
    Log.v(TAG, "REGID : " + regid);
    gcm = GoogleCloudMessaging.getInstance(this);


    EditText password = (EditText) findViewById(R.id.password_login);

    password.setOnEditorActionListener(new OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_GO) {

                hideKeyboard();
                connectionserveur();

                return true;
            }
            return false;
        }
    });
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_moment, menu);
        return true;
    }

    public void connect(View view) throws JSONException {
        RelativeLayout button_inscription = (RelativeLayout) findViewById(R.id.inscrire_button_login);
        button_inscription.setVisibility(View.INVISIBLE);
        EditText edit_email = (EditText) findViewById(R.id.email_login);
        edit_email.setVisibility(View.VISIBLE);
        EditText edit_password = (EditText) findViewById(R.id.password_login);
        edit_password.setVisibility(View.VISIBLE);

        LinearLayout layout_buttons = (LinearLayout) findViewById(R.id.layout_button_login);

        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f
        );
        animation.setFillAfter(true);
        animation.setDuration(300);
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout connection_finale = (RelativeLayout) findViewById(R.id.connection_finale);
                connection_finale.setVisibility(View.VISIBLE);
                RelativeLayout connection_layout = (RelativeLayout) findViewById(R.id.connection_relative);
                connection_layout.setVisibility(View.INVISIBLE);
                forgotPass.setVisibility(View.VISIBLE);
            }
        });
        layout_buttons.startAnimation(animation);

        ImageButton fleche_back = (ImageButton) findViewById(R.id.fleche_back_connection);
        fleche_back.setVisibility(View.VISIBLE);
    }

    public void closeConnection(View view) {

        ImageButton fleche_back = (ImageButton) findViewById(R.id.fleche_back_connection);
        fleche_back.setVisibility(View.INVISIBLE);

        RelativeLayout connection_layout = (RelativeLayout) findViewById(R.id.connection_relative);
        connection_layout.setVisibility(View.VISIBLE);
        EditText edit_email = (EditText) findViewById(R.id.email_login);
        edit_email.setVisibility(View.INVISIBLE);
        EditText edit_password = (EditText) findViewById(R.id.password_login);
        edit_password.setVisibility(View.INVISIBLE);
        forgotPass.setVisibility(View.GONE);


        LinearLayout layout_buttons = (LinearLayout) findViewById(R.id.layout_button_login);

        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setFillAfter(true);
        animation.setDuration(200);
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

                RelativeLayout connection_finale = (RelativeLayout) findViewById(R.id.connection_finale);
                connection_finale.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout button_inscription = (RelativeLayout) findViewById(R.id.inscrire_button_login);
                button_inscription.setVisibility(View.VISIBLE);
            }

        });
        layout_buttons.startAnimation(animation);

    }

    public void inscription(View view) {
        Intent intent = new Intent(this, InscriptionActivity.class);
        startActivity(intent);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void connectionserveur(View v) {
        connectionserveur();
    }

    private void connectionserveur() {

        String email = ((EditText) findViewById(R.id.email_login)).getText().toString();
        String password = ((EditText) findViewById(R.id.password_login)).getText().toString();

        if ((email.length() > 0) && (password.length() > 0)) {

            AppMoment.getInstance().user = new User();
            AppMoment.getInstance().user.setEmail(email);

            RequestParams params = new RequestParams();
            params.put("email", email);
            params.put("password", password);
            if (!regid.equals("")) params.put("notif_id", regid);
            params.put("os", "1");
            params.put("os_version", android.os.Build.VERSION.RELEASE);
            params.put("model", CommonUtilities.getDeviceName());
            params.put("device_id", AppMoment.getInstance().tel_id);


            MomentApi.initialize(getApplicationContext());

            dialog = ProgressDialog.show(this, null, "Connexion");
            MomentApi.post("login", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    isSuccess = true;
                    Long id = null;
                    try {
                        id = Long.parseLong(response.getString("id"));
                        AppMoment.getInstance().user.setId(id);

                        SharedPreferences sharedPreferences = getSharedPreferences(AppMoment.PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("userID", id);
                        editor.commit();

                        MomentApi.get("user", null, new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(JSONObject response) {
                                try {
                                    String firstname = response.getString("firstname");
                                    AppMoment.getInstance().user.setFirstName(firstname);

                                    String lastname = response.getString("lastname");
                                    AppMoment.getInstance().user.setLastName(lastname);

                                    if (response.has("profile_picture_url")) {
                                        String profile_picture_url = response.getString("profile_picture_url");
                                        AppMoment.getInstance().user.setPictureProfileUrl(profile_picture_url);
                                    }

                                    dialog.dismiss();

                                    Intent intent = new Intent(MomentActivity.this, TimelineActivity.class);
                                    startActivity(intent);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println(id);
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    dialog.dismiss();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MomentActivity.this);

                    alertDialogBuilder.setTitle("Connexion impossible");

                    alertDialogBuilder
                            .setMessage("Mauvais email/mot de passe.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
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
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MomentActivity.this);

                        alertDialogBuilder.setTitle("Connexion impossible");

                        alertDialogBuilder
                                .setMessage("Mauvais email/mot de passe.")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
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
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setTitle("Données incorrectes");

            alertDialogBuilder
                    .setMessage("Veuillez renseigner votre email et votre mot de passe")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }


    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.length() == 0) {
            Log.v(TAG, "Registration not found.");
            return "";
        }


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
                String msg;
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
        Log.v(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        long expirationTime = System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;

        Log.v(TAG, "Setting registration expiry time to " +
                new Timestamp(expirationTime));
        editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
        editor.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    public void forgotPass(View view) {
        final EditText ed = new EditText(context);
        ed.setTextColor(getResources().getColor(android.R.color.black));
        ed.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        AlertDialog.Builder monDialogue = new AlertDialog.Builder(MomentActivity.this);
        monDialogue.setTitle("Mot de passe oublié");
        monDialogue.setMessage("Veuillez rentrer votre email afin de vous envoyer votre nouveau mot de passe.");
        monDialogue.setView(ed);

        monDialogue.setPositiveButton("Envoyer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (CommonUtilities.isValidEmail(ed.getText())) {
                    dialog.dismiss();
                    MomentApi.get("lostpass/" + ed.getText().toString(), null, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(JSONObject response) {
                            Toast.makeText(getApplicationContext(), "Email envoyé avec votre nouveau mot de passe", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Throwable error, String content) {
                            Toast.makeText(getApplicationContext(), "Erreur", Toast.LENGTH_LONG).show();

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Email invalide", Toast.LENGTH_LONG).show();
                }
            }
        });
        monDialogue.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        monDialogue.show();
    }
}
