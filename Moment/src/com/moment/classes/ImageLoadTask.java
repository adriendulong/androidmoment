package com.moment.classes;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoadTask extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> weakReference;

    public ImageLoadTask(ImageView imageView) {
        this.weakReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected void onPreExecute() {
        Log.i("ImageLoadingTask","Loading images ...");
    }

    protected Bitmap doInBackground(String... params) {
        Log.i("ImageLoadTask", "Attempting to load image URL:" + params[0]);
        final Bitmap bitmap = getBitmapFromURL(params[0]);
        return bitmap;
    }

    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null) {
            final ImageView imageView = (ImageView) weakReference.get();
            if(imageView != null)
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
