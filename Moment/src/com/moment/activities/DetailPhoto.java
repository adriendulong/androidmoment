package com.moment.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.google.analytics.tracking.android.EasyTracker;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.MomentApi;
import com.moment.models.Photo;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DetailPhoto extends Activity implements View.OnClickListener {

    private int position;
    private Long momentID;
    private Photo photo;
    private int maxIndex;
    private Session session;
    private Bitmap bitmap;
    private Bundle bundle;
    private String message;
    private EditText dialogText;
    private Bundle params;
    private Request request;
    private float pxBitmap;
    private ImageView imageView;
    private DetailPhoto _this = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = savedInstanceState;

        setContentView(R.layout.activity_detail_photo);
        imageView = (ImageView) findViewById(R.id.photo_moment_detail);


        position = getIntent().getIntExtra("position", 0);
        momentID = getIntent().getLongExtra("momentID", 0);
        maxIndex = AppMoment.getInstance().user.getMomentById(momentID).getPhotos().size()-1;

        photo = AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position);

        //Size of the crop depending on the screen resolution
        pxBitmap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics());
        Picasso.with(this).load(photo.getUrlOriginal()).resize((int)pxBitmap,(int)pxBitmap).centerCrop().placeholder(R.drawable.picto_photo_vide).into(imageView);

        final ImageButton closeButton    = (ImageButton) findViewById(R.id.close);
        final ImageButton previousButton = (ImageButton) findViewById(R.id.previous);
        final ImageButton nextButton     = (ImageButton) findViewById(R.id.next);
        final ImageButton likeButton     = (ImageButton) findViewById(R.id.coeur);
        final ImageButton petitCoeur     = (ImageButton) findViewById(R.id.petit_coeur);
        final ImageButton trashButton    = (ImageButton) findViewById(R.id.trash);
        final ImageButton downloadButton = (ImageButton) findViewById(R.id.download);
        final ImageButton facebookButton = (ImageButton) findViewById(R.id.fb_share);
        final ImageButton twitterButton  = (ImageButton) findViewById(R.id.twitter);
        final EditText    nbPetitCoeur   = (EditText)    findViewById(R.id.editText);
        final TextView    prenom         = (TextView)    findViewById(R.id.prenom);
        final TextView    nom            = (TextView)    findViewById(R.id.nom);
        final TextView    jour           = (TextView)    findViewById(R.id.jour);
        final TextView    mois           = (TextView)    findViewById(R.id.mois);

        closeButton.setClickable(true);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        prenom.setText(photo.getUser().getFirstName().toUpperCase());
        nom.setText(" " + photo.getUser().getLastName().toUpperCase());

        if(photo.getTime()!=null){
            Calendar cal = Calendar.getInstance();
            cal.setTime(photo.getTime());
            int dateJour = cal.get(Calendar.DAY_OF_MONTH);
            int dateMois = cal.get(Calendar.MONTH) + 1;
            int hh       = cal.get(Calendar.HOUR_OF_DAY);
            int mm       = cal.get(Calendar.MINUTE);
            jour.setText(dateJour+"/"+dateMois+" ");
            mois.setText(hh+":"+mm);
        }

        nbPetitCoeur.setClickable(false);
        nbPetitCoeur.setEnabled(false);

        if(photo.getNbLike() > 0)
        {
            petitCoeur.setVisibility(ImageButton.VISIBLE);
            nbPetitCoeur.setTextColor(Color.parseColor("#FFFFFF"));
            nbPetitCoeur.setText("" + photo.getNbLike());
            nbPetitCoeur.setVisibility(EditText.VISIBLE);
        }

        if(position == maxIndex)
        {
            nextButton.setVisibility(View.INVISIBLE);
        }

        if(position == 0)
        {
            previousButton.setVisibility(View.INVISIBLE);
        }

        if(AppMoment.getInstance().user.getId().equals(AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getUser().getId())
                || AppMoment.getInstance().user.getId() == AppMoment.getInstance().user.getMomentById(momentID).getUserId())
        {
            trashButton.setImageResource(R.drawable.trash);
        } else {
            trashButton.setImageResource(R.drawable.btn_report);
        }

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyTracker.getTracker().sendEvent("Photo", "button_press", "Download", null);

                File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures/Moment/");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/Moment/", "moment_"
                        + String.valueOf(System.currentTimeMillis())
                        + ".jpg");

                imageView.buildDrawingCache();
                bitmap = imageView.getDrawingCache();

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                assert bitmap != null;
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                String[] paths = new String[1];
                paths[0] = dir.getAbsolutePath();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(
                        "file://"
                                + Environment.getExternalStorageDirectory()
                                + "/Pictures/Moment/")));

                Toast.makeText(getApplicationContext(), "Photo enregistrée", Toast.LENGTH_LONG).show();
            }
        });

        trashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyTracker.getTracker().sendEvent("Photo", "button_press", "Remove", null);

                if(AppMoment.getInstance().user.getId().equals(AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getUser().getId())
                        || AppMoment.getInstance().user.getId() == AppMoment.getInstance().user.getMomentById(momentID).getUserId())
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(_this, android.R.style.Theme_Holo_Light_Dialog));
                    alertDialogBuilder
                            .setTitle("Suppression Photo")
                            .setMessage("Voulez vous vraiment supprimer cette photo ? Cette action est irreversible !")
                            .setCancelable(false)
                            .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MomentApi.get("delphoto/" + AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getId(), null, new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(String response) {
                                            AppMoment.getInstance().user.getMomentById(momentID).getPhotos().remove(position);
                                            if (position == 0 && maxIndex == 0) {
                                                closeButton.performClick();
                                            } else if (position == 0 && maxIndex > 0) {
                                                maxIndex--;
                                                nextButton.performClick();
                                            } else if (position == maxIndex && maxIndex > 0) {
                                                maxIndex--;
                                                previousButton.performClick();
                                            } else {
                                                maxIndex--;
                                                position--;
                                                nextButton.performClick();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Throwable e, String response) {
                                            Log.e(response, "Alors what ?");
                                        }
                                    });
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(getApplication(), "On va balancer" , Toast.LENGTH_LONG).show();
                }
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyTracker.getTracker().sendEvent("Photo", "button_press", "Share Facebook", null);
                try {
                    openActiveSession(_this, true, fbStatusCallback, Arrays.asList(
                            "publish_actions"), bundle);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                bitmap = photo.getBitmapOriginal();
                sharePicture();
            }
        });

        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyTracker.getTracker().sendEvent("Photo", "button_press", "Share Twitter", null);
                Toast.makeText(getApplication(), "On va twitter" , Toast.LENGTH_LONG).show();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                photo = AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position);
                Picasso.with(getApplicationContext()).load(photo.getUrlOriginal()).resize((int)pxBitmap,(int)pxBitmap).centerCrop().placeholder(R.drawable.picto_photo_vide).into(imageView);
                if(position == AppMoment.getInstance().user.getMomentById(momentID).getPhotos().size()-1){v.setVisibility(View.INVISIBLE);}
                if(position > 0){previousButton.setVisibility(View.VISIBLE);}
                if(AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getNbLike() > 0){petitCoeur.setVisibility(ImageButton.VISIBLE); nbPetitCoeur.setTextColor(Color.parseColor("#FFFFFF")); nbPetitCoeur.setText("" + AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getNbLike()); nbPetitCoeur.setVisibility(EditText.VISIBLE);}
                if(AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getNbLike() == 0){petitCoeur.setVisibility(ImageButton.GONE); nbPetitCoeur.setVisibility(EditText.GONE);}
                if(AppMoment.getInstance().user.getId().equals(AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getUser().getId())
                        || AppMoment.getInstance().user.getId() == AppMoment.getInstance().user.getMomentById(momentID).getUserId())
                {
                    trashButton.setImageResource(R.drawable.trash);
                } else {
                    trashButton.setImageResource(R.drawable.btn_report);
                }
                prenom.setText(photo.getUser().getFirstName().toUpperCase());
                nom.setText(" " + photo.getUser().getLastName().toUpperCase());

                Calendar cal = Calendar.getInstance();
                cal.setTime(photo.getTime());
                int dateJour = cal.get(Calendar.DAY_OF_MONTH);
                int dateMois = cal.get(Calendar.MONTH) + 1;
                int hh       = cal.get(Calendar.HOUR_OF_DAY);
                int mm       = cal.get(Calendar.MINUTE);
                jour.setText(dateJour+"/"+dateMois+" ");
                mois.setText(hh+":"+mm);
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position--;
                photo = AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position);
                Picasso.with(getApplicationContext()).load(photo.getUrlOriginal()).resize((int)pxBitmap,(int)pxBitmap).centerCrop().placeholder(R.drawable.picto_photo_vide).into(imageView);
                if(position == 0){v.setVisibility(View.INVISIBLE);}
                if(position < AppMoment.getInstance().user.getMomentById(momentID).getPhotos().size()-1){nextButton.setVisibility(View.VISIBLE);}
                if(AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getNbLike() > 0){petitCoeur.setVisibility(ImageButton.VISIBLE); nbPetitCoeur.setTextColor(Color.parseColor("#FFFFFF")); nbPetitCoeur.setText("" + AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getNbLike()); nbPetitCoeur.setVisibility(EditText.VISIBLE);}
                if(AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getNbLike() == 0){petitCoeur.setVisibility(ImageButton.GONE); nbPetitCoeur.setVisibility(EditText.GONE);}
                if(AppMoment.getInstance().user.getId().equals(AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getUser().getId())
                        || AppMoment.getInstance().user.getId() == AppMoment.getInstance().user.getMomentById(momentID).getUserId())
                {
                    trashButton.setImageResource(R.drawable.trash);
                } else {
                    trashButton.setImageResource(R.drawable.btn_report);
                }
                prenom.setText(photo.getUser().getFirstName().toUpperCase());
                nom.setText(" " + photo.getUser().getLastName().toUpperCase());

                Calendar cal = Calendar.getInstance();
                cal.setTime(photo.getTime());
                int dateJour = cal.get(Calendar.DAY_OF_MONTH);
                int dateMois = cal.get(Calendar.MONTH) + 1;
                int hh       = cal.get(Calendar.HOUR_OF_DAY);
                int mm       = cal.get(Calendar.MINUTE);
                jour.setText(dateJour+"/"+dateMois+" ");
                mois.setText(hh+":"+mm);
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyTracker.getTracker().sendEvent("Photo", "button_press", "Like", null);
                MomentApi.get("like/" + AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getId(), null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            int nbLike = response.getInt("nb_likes");
                            AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).setNbLike(nbLike);
                            if(nbLike > 0)
                            {
                                nbPetitCoeur.setText("" + nbLike);
                                nbPetitCoeur.setTextColor(Color.parseColor("#FFFFFF"));
                                petitCoeur.setVisibility(ImageButton.VISIBLE);
                                nbPetitCoeur.setVisibility(EditText.VISIBLE);
                            }
                            else
                            {
                                petitCoeur.setVisibility(ImageButton.GONE);
                                nbPetitCoeur.setVisibility(EditText.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private Session openActiveSession(Activity activity, boolean allowLoginUI,
                                      Session.StatusCallback callback, List<String> permissions, Bundle savedInstanceState) {
        Session.OpenRequest openRequest = new Session.OpenRequest(activity).
                setPermissions(permissions).setLoginBehavior(SessionLoginBehavior.
                SSO_WITH_FALLBACK).setCallback(callback).
                setDefaultAudience(SessionDefaultAudience.FRIENDS);

        session = null;

        Log.d("", "" + savedInstanceState);
        if (savedInstanceState != null) {
            session = Session.restoreSession(this, null, fbStatusCallback, savedInstanceState);
        }
        if (session == null) {
            session = new Session(this);
        }
        Session.setActiveSession(session);
        if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED) || allowLoginUI) {
            session.openForPublish(openRequest);
            return session;
        }
        return null;
    }

    private final Session.StatusCallback fbStatusCallback = new Session.StatusCallback() {
        public void call(Session session, SessionState state, Exception exception) {
            if (state.isOpened()) {
                Request.newUploadPhotoRequest(session, bitmap, new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        Log.d("Upload", response.toString());
                    }

                });
            }
        }
    };

    private void sharePicture() {
        request = Request.newUploadPhotoRequest(session, bitmap, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                Log.d("Upload", response.toString());
            }
        });

        params = request.getParameters();

        final Dialog dialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        dialog.setContentView(view);

        assert view != null;
        dialogText = (EditText) view.findViewById(R.id.custom_dialog_text);
        Button dialogBtn = (Button) view.findViewById(R.id.custom_dialog_button);
        message = "Photo prise lors de l'évènement " + AppMoment.getInstance().user.getMomentById(momentID).getName() + " " + photo.getUrlUnique() + " " + "#appmoment";
        dialogText.setText(message);
        dialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFacebookComment();
                Request.executeBatchAsync(request);
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void setFacebookComment() {
        assert dialogText.getText() != null;
        message = dialogText.getText().toString();
        params.putString("message", message);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession()
                .onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {}

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
