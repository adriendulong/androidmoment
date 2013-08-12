package com.moment.util;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by adriendulong on 09/08/13.
 */


public class BitmapWorkerTask  extends AsyncTask<Uri, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private Uri data;
    private int mReqSize = 0;
    private ContentResolver mContent;

    public BitmapWorkerTask(ImageView imageView, int reqSize, ContentResolver contentResolver) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        mReqSize = reqSize;
        mContent = contentResolver;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Uri... params) {
        data = params[0];
        return Images.decodeSampledBitmapFromURI(data, mContent, mReqSize, mReqSize);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}