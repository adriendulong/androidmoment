package com.moment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.moment.MomentInfosActivity.Exchanger;
import com.moment.classes.Photo;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailPhoto extends Activity implements View.OnClickListener {

    private int position;
    private Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_photo);
        final ImageView imageView = (ImageView) findViewById(R.id.photo_moment_detail);

        position = getIntent().getIntExtra("position", 0);

        ImageLoadTask imageLoadTask = new ImageLoadTask(imageView, Exchanger.photos.get(position), context);
        imageLoadTask.execute(Exchanger.photos.get(position).getUrl_original());


        ImageButton closeButton = (ImageButton) findViewById(R.id.close);
        closeButton.setClickable(true);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final ImageButton previousButton = (ImageButton) findViewById(R.id.previous);
        final ImageButton nextButton = (ImageButton) findViewById(R.id.next);

        if(position == Exchanger.photos.size()-1){nextButton.setVisibility(View.INVISIBLE);}
        if(position == 0){previousButton.setVisibility(View.INVISIBLE);}

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position++;
                ImageLoadTask imageLoadTask = new ImageLoadTask(imageView, Exchanger.photos.get(position), context);
                imageLoadTask.execute(Exchanger.photos.get(position).getUrl_original());
                if(position == Exchanger.photos.size()-1){v.setVisibility(View.INVISIBLE);}
                if(position > 0){previousButton.setVisibility(View.VISIBLE);}
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position--;
                ImageLoadTask imageLoadTask = new ImageLoadTask(imageView, Exchanger.photos.get(position), context);
                imageLoadTask.execute(Exchanger.photos.get(position).getUrl_original());
                if(position == 0){v.setVisibility(View.INVISIBLE);}
                if(position < Exchanger.photos.size()-1){nextButton.setVisibility(View.VISIBLE);}
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * Chargement asynchrone des originaux
     */

    public class ImageLoadTask extends AsyncTask<String, Void, Bitmap> {

        private final Photo photo;
        private final WeakReference<ImageView> weakImageView;
        private ProgressDialog progressDialog;

        public ImageLoadTask(ImageView imageView, Photo photo, Context context) {
            this.weakImageView = new WeakReference<ImageView>(imageView);
            this.photo = photo;
            this.progressDialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            this.progressDialog.setMessage("Loading...");
            this.progressDialog.show();
        }

        protected Bitmap doInBackground(String... params) {
            Log.e("ImageLoadTask", "Attempting to load image URL:" + params[0]);
            if(photo.getBitmap_original() == null){
                Log.e("ImageLoadTask", "Va chercher le bitmap " + params[0]);
                final Bitmap bitmap = getBitmapFromURL(params[0]);
                return bitmap;
            }
            else{
                Log.e("ImageLoadTask", "On a deja l'image: " + params[0]);
                return photo.getBitmap_original();
            }
        }

        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null) {
                final ImageView imageView = weakImageView.get();
                if(photo.getBitmap_original() == null)
                    photo.setBitmap_original(bitmap);
                if(imageView != null)
                    imageView.setImageBitmap(bitmap);
            }
            if(progressDialog != null){
                progressDialog.dismiss();
                progressDialog = null;
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
