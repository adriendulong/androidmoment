package com.moment.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import com.moment.R;
import com.moment.AppMoment;
import com.moment.classes.MomentApi;
import com.moment.models.Photo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class DetailPhoto extends Activity implements View.OnClickListener {

    private int position;
    private Long momentID;
    private Photo photo;
    private int maxIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_photo);
        final ImageView imageView = (ImageView) findViewById(R.id.photo_moment_detail);

        position = getIntent().getIntExtra("position", 0);
        momentID = getIntent().getLongExtra("momentID", 0);
        maxIndex = AppMoment.getInstance().user.getMomentById(momentID).getPhotos().size()-1;

        photo = AppMoment.getInstance().user.getMomentById(momentID).getPhotos().get(position);
        ImageLoadTask imageLoadTask = new ImageLoadTask(imageView, photo);
        imageLoadTask.execute(photo.getUrlOriginal());

        final ImageButton closeButton = (ImageButton) findViewById(R.id.close);
        final ImageButton previousButton = (ImageButton) findViewById(R.id.previous);
        final ImageButton nextButton     = (ImageButton) findViewById(R.id.next);
        final ImageButton likeButton     = (ImageButton) findViewById(R.id.coeur);
        final ImageButton petitCoeur     = (ImageButton) findViewById(R.id.petit_coeur);
        final ImageButton trashButton    = (ImageButton) findViewById(R.id.trash);
        final ImageButton downloadButton = (ImageButton) findViewById(R.id.download);
        final ImageButton facebookButton = (ImageButton) findViewById(R.id.button_facebook);
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

        prenom.setText(photo.getUser().getFirstName());
        nom.setText(" " + photo.getUser().getLastName());

        Calendar cal = Calendar.getInstance();
        cal.setTime(photo.getTime());
        int dateJour = cal.get(Calendar.DAY_OF_MONTH);
        int dateMois = cal.get(Calendar.MONTH) + 1;
        jour.setText(""+dateJour);
        mois.setText("/"+dateMois);

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
                Toast.makeText(getApplication(), "On va download" , Toast.LENGTH_LONG).show();
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

/*        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "On va facebooker" , Toast.LENGTH_LONG).show();
            }
        });

        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "On va twitter" , Toast.LENGTH_LONG).show();
            }
        });*/

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
                prenom.setText(photo.getUser().getFirstName());
                nom.setText(" " + photo.getUser().getLastName());

                Calendar cal = Calendar.getInstance();
                cal.setTime(photo.getTime());
                int dateJour = cal.get(Calendar.DAY_OF_MONTH);
                int dateMois = cal.get(Calendar.MONTH) + 1;
                jour.setText(""+dateJour);
                mois.setText("/"+dateMois);
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
                prenom.setText(photo.getUser().getFirstName());
                nom.setText(" " + photo.getUser().getLastName());

                Calendar cal = Calendar.getInstance();
                cal.setTime(photo.getTime());
                int dateJour = cal.get(Calendar.DAY_OF_MONTH);
                int dateMois = cal.get(Calendar.MONTH) + 1;
                jour.setText(""+dateJour);
                mois.setText("/"+dateMois);
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
