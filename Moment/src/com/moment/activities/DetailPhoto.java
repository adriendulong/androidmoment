package com.moment.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.Images;
import com.moment.classes.MomentApi;
import com.moment.models.Photo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private Activity activity = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = savedInstanceState;

        setContentView(R.layout.activity_detail_photo);
        final ImageView imageView = (ImageView) findViewById(R.id.photo_moment_detail);

        position = getIntent().getIntExtra("position", 0);
        momentID = getIntent().getLongExtra("momentID", 0);
        maxIndex = AppMoment.getInstance().user.getMomentById(momentID).getPhotos().size()-1;

        photo = AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position);
        ImageLoadTask imageLoadTask = new ImageLoadTask(imageView, photo);
        imageLoadTask.execute(photo.getUrlOriginal());

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

        Calendar cal = Calendar.getInstance();
        cal.setTime(photo.getTime());
        int dateJour = cal.get(Calendar.DAY_OF_MONTH);
        int dateMois = cal.get(Calendar.MONTH) + 1;
        int hh       = cal.get(Calendar.HOUR_OF_DAY);
        int mm       = cal.get(Calendar.MINUTE);
        jour.setText(dateJour+"/"+dateMois+" ");
        mois.setText(hh+":"+mm);

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

        if(AppMoment.getInstance().user.getId() == AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getUser().getId()
                || AppMoment.getInstance().user.getId() == AppMoment.getInstance().user.getMomentById(momentID).getUserId())
        {
            trashButton.setImageResource(R.drawable.trash);
        } else {
            trashButton.setImageResource(R.drawable.btn_report);
        }

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures/Moment/");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/Moment/", "moment_"
                        + String.valueOf(System.currentTimeMillis())
                        + ".jpg");

                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            }
        });

        trashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppMoment.getInstance().user.getId() == AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getUser().getId()
                        || AppMoment.getInstance().user.getId() == AppMoment.getInstance().user.getMomentById(momentID).getUserId())
                {
                    MomentApi.get("delphoto/" +AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getId(), null, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {
                            AppMoment.getInstance().user.getMomentById(momentID).getPhotos().remove(position);
                            if(position == 0 && maxIndex == 0) {
                                closeButton.performClick();
                            } else if (position == 0 && maxIndex > 0) {
                                maxIndex --;
                                nextButton.performClick();
                            } else if (position == maxIndex && maxIndex > 0) {
                                maxIndex --;
                                previousButton.performClick();
                            } else {
                                maxIndex --;
                                position --;
                                nextButton.performClick();
                            }
                        }

                        @Override
                        public void onFailure(Throwable e, String response) {
                            Log.e(response, "Alors what ?");
                        }
                    });
                } else {
                    Toast.makeText(getApplication(), "On va balancer" , Toast.LENGTH_LONG).show();
                }
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openActiveSession(activity, true, fbStatusCallback, Arrays.asList(
                            new String[]{"publish_actions"}), bundle);
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
                Toast.makeText(getApplication(), "On va twitter" , Toast.LENGTH_LONG).show();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                photo = AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position);
                ImageLoadTask imageLoadTask = new ImageLoadTask(imageView, AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position));
                imageLoadTask.execute(AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getUrlOriginal());
                if(position == AppMoment.getInstance().user.getMomentById(momentID).getPhotos().size()-1){v.setVisibility(View.INVISIBLE);}
                if(position > 0){previousButton.setVisibility(View.VISIBLE);}
                if(AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getNbLike() > 0){petitCoeur.setVisibility(ImageButton.VISIBLE); nbPetitCoeur.setTextColor(Color.parseColor("#FFFFFF"));; nbPetitCoeur.setText("" + AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getNbLike()); nbPetitCoeur.setVisibility(EditText.VISIBLE);}
                if(AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getNbLike() == 0){petitCoeur.setVisibility(ImageButton.GONE); nbPetitCoeur.setVisibility(EditText.GONE);}
                if(AppMoment.getInstance().user.getId() == AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getUser().getId()
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
                ImageLoadTask imageLoadTask = new ImageLoadTask(imageView, AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position));
                imageLoadTask.execute(AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getUrlOriginal());
                if(position == 0){v.setVisibility(View.INVISIBLE);}
                if(position < AppMoment.getInstance().user.getMomentById(momentID).getPhotos().size()-1){nextButton.setVisibility(View.VISIBLE);}
                if(AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getNbLike() > 0){petitCoeur.setVisibility(ImageButton.VISIBLE); nbPetitCoeur.setTextColor(Color.parseColor("#FFFFFF"));; nbPetitCoeur.setText("" + AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getNbLike()); nbPetitCoeur.setVisibility(EditText.VISIBLE);}
                if(AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getNbLike() == 0){petitCoeur.setVisibility(ImageButton.GONE); nbPetitCoeur.setVisibility(EditText.GONE);}
                if(AppMoment.getInstance().user.getId() == AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position).getUser().getId()
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
                session.openForPublish(openRequest);
                return session;
            }
        }
        return null;
    }

    private Session.StatusCallback fbStatusCallback = new Session.StatusCallback() {
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

    public void sharePicture() {
        Request request = Request.newUploadPhotoRequest(session, bitmap, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                Log.d("Upload", response.toString());
            }
        });
        Request.executeBatchAsync(request);
    }

    @Override
    public void onClick(View v) {}

    public class ImageLoadTask extends AsyncTask<String, Void, Bitmap> {

        private final Photo photo;
        private final WeakReference<ImageView> weakImageView;
        private ProgressBar spinner;

        public ImageLoadTask(ImageView imageView, Photo photo) {
            this.weakImageView = new WeakReference<ImageView>(imageView);
            this.photo = photo;
            this.spinner = (ProgressBar) findViewById(R.id.progressBar);
        }

        @Override
        protected void onPreExecute() {
            spinner.setVisibility(ProgressBar.VISIBLE);
        }

        protected Bitmap doInBackground(String... params) {
            if(photo.getBitmapOriginal() == null){
                final Bitmap bitmap = getBitmapFromURL(params[0]);
                return bitmap;
            }
            else{
                return photo.getBitmapOriginal();
            }
        }

        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null) {
                final ImageView imageView = weakImageView.get();
                if(photo.getBitmapOriginal() == null)
                    photo.setBitmapOriginal(bitmap);
                if(imageView != null)
                    spinner.setVisibility(ProgressBar.GONE);
                imageView.setImageBitmap(bitmap);
            }
        }

        public Bitmap getBitmapFromURL(String src) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
