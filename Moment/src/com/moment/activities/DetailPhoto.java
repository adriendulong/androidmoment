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

public class DetailPhoto extends Activity implements View.OnClickListener {

    private int position;
    private int momentID;
    private Photo photo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_photo);
        final ImageView imageView = (ImageView) findViewById(R.id.photo_moment_detail);

        position = getIntent().getIntExtra("position", 0); Log.e("DetailPhoto","position " + position);
        momentID = getIntent().getIntExtra("momentID", 0); Log.e("DetailPhoto","momentID " + momentID);
        Log.e("",""+AppMoment.getInstance().user.getMoment(momentID).getPhotos().size());

        photo = AppMoment.getInstance().user.getMoment(momentID).getPhotos().get(position);
        Log.e("DetailPhoto",""+photo.toString());
        ImageLoadTask imageLoadTask = new ImageLoadTask(imageView, photo);
        imageLoadTask.execute(photo.getUrlOriginal());

        ImageButton closeButton = (ImageButton) findViewById(R.id.close);
        closeButton.setClickable(true);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final ImageButton previousButton = (ImageButton) findViewById(R.id.previous);
        final ImageButton nextButton     = (ImageButton) findViewById(R.id.next);
        final ImageButton likeButton     = (ImageButton) findViewById(R.id.coeur);
        final ImageButton petitCoeur     = (ImageButton) findViewById(R.id.petit_coeur);
        final EditText editText          = (EditText)    findViewById(R.id.editText);

        editText.setClickable(false);
        editText.setEnabled(false);

        if(photo.getNbLike() > 0)
        {
            petitCoeur.setVisibility(ImageButton.VISIBLE);
            editText.setTextColor(Color.parseColor("#FFFFFF"));
            editText.setText(""+photo.getNbLike());
            editText.setVisibility(EditText.VISIBLE);
        }

        if(position == AppMoment.getInstance().user.getMoment(momentID).getPhotos().size()-1)
        {
            nextButton.setVisibility(View.INVISIBLE);
        }

        if(position == 0)
        {
            previousButton.setVisibility(View.INVISIBLE);
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                ImageLoadTask imageLoadTask = new ImageLoadTask(imageView, AppMoment.getInstance().user.getMoment(momentID).getPhotos().get(position));
                imageLoadTask.execute(AppMoment.getInstance().user.getMoment(momentID).getPhotos().get(position).getUrlOriginal());
                if(position == AppMoment.getInstance().user.getMoment(momentID).getPhotos().size()-1){v.setVisibility(View.INVISIBLE);}
                if(position > 0){previousButton.setVisibility(View.VISIBLE);}
                if(AppMoment.getInstance().user.getMoment(momentID).getPhotos().get(position).getNbLike() > 0){petitCoeur.setVisibility(ImageButton.VISIBLE); editText.setTextColor(Color.parseColor("#FFFFFF"));; editText.setText(""+AppMoment.getInstance().user.getMoment(momentID).getPhotos().get(position).getNbLike()); editText.setVisibility(EditText.VISIBLE);}
                if(AppMoment.getInstance().user.getMoment(momentID).getPhotos().get(position).getNbLike() == 0){petitCoeur.setVisibility(ImageButton.GONE); editText.setVisibility(EditText.GONE);}
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position--;
                ImageLoadTask imageLoadTask = new ImageLoadTask(imageView, AppMoment.getInstance().user.getMoment(momentID).getPhotos().get(position));
                imageLoadTask.execute(AppMoment.getInstance().user.getMoment(momentID).getPhotos().get(position).getUrlOriginal());
                if(position == 0){v.setVisibility(View.INVISIBLE);}
                if(position < AppMoment.getInstance().user.getMoment(momentID).getPhotos().size()-1){nextButton.setVisibility(View.VISIBLE);}
                if(AppMoment.getInstance().user.getMoment(momentID).getPhotos().get(position).getNbLike() > 0){petitCoeur.setVisibility(ImageButton.VISIBLE); editText.setTextColor(Color.parseColor("#FFFFFF"));; editText.setText(""+AppMoment.getInstance().user.getMoment(momentID).getPhotos().get(position).getNbLike()); editText.setVisibility(EditText.VISIBLE);}
                if(AppMoment.getInstance().user.getMoment(momentID).getPhotos().get(position).getNbLike() == 0){petitCoeur.setVisibility(ImageButton.GONE); editText.setVisibility(EditText.GONE);}
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MomentApi.get("like/" + AppMoment.getInstance().user.getMoment(momentID).getPhotos().get(position).getId(), null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            int nbLike = response.getInt("nb_likes");
                            AppMoment.getInstance().user.getMoment(momentID).getPhotos().get(position).setNbLike(nbLike);
                            if(nbLike > 0)
                            {
                                editText.setText(""+nbLike);
                                editText.setTextColor(Color.parseColor("#FFFFFF"));
                                petitCoeur.setVisibility(ImageButton.VISIBLE);
                                editText.setVisibility(EditText.VISIBLE);
                            }
                            else
                            {
                                petitCoeur.setVisibility(ImageButton.GONE);
                                editText.setVisibility(EditText.GONE);
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

    /**
     * Chargement asynchrone des originaux
     */

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
