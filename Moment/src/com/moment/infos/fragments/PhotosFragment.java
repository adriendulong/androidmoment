package com.moment.infos.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moment.DetailPhoto;
import com.moment.MomentInfosActivity.Exchanger;
import com.moment.R;
import com.moment.classes.Images;
import com.moment.classes.MomentApi;
import com.moment.classes.Photo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PhotosFragment extends Fragment {

    static final int PICK_CAMERA_PHOTOS = 1;

    private LayoutInflater layoutInflater;
    private RelativeLayout detailPhoto;
    private GridView gridView;
    private ImageAdapter imageAdapter;

    private String albumName = "Moment";
    private Uri outputFileUri;


    Bitmap bitmap = null;
    Photo photo = new Photo();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("PhotoFragment","OnCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        detailPhoto = (RelativeLayout)inflater.inflate(R.layout.detail_photo, null);
        imageAdapter = new ImageAdapter(view.getContext(), Exchanger.photos);
        gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(imageAdapter);

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        MomentApi.get("photosmoment/"+getActivity().getIntent().getIntExtra("id", 1), null, new JsonHttpResponseHandler() {

            public void onSuccess(JSONObject response) {
                try {
                    JSONArray jsonPhotos = response.getJSONArray("photos");

                    for(int i=0;i<jsonPhotos.length();i++)
                    {
                        Photo photo = new Photo();
                        photo.photoFromJSON(jsonPhotos.getJSONObject(i));
                        imageAdapter.photos.add(photo);
                        imageAdapter.notifyDataSetChanged();
                        ThumbnailLoadTask imageLoadTask = new ThumbnailLoadTask(photo, imageAdapter, getActivity());
                        imageLoadTask.execute(photo.getUrl_thumbnail());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        gridView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                if(position==0){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File file = new File(Environment.getExternalStorageDirectory(), "tmp_"
                            + String.valueOf(System.currentTimeMillis())
                            +".jpg");

                    outputFileUri = Uri.fromFile(file);

                    try {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, PICK_CAMERA_PHOTOS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent intent = new Intent(getActivity(), DetailPhoto.class);
                    intent.putExtra("position", (position-1));
                    startActivity(intent);
                }
            };
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        imageAdapter.photos.clear();
        Log.e("PAUSE", "Photos");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Bitmap bmp = null;
        photo.setBitmap_thumbnail(bmp);
        addPhoto(photo);
        UploadTask uploadTask = new UploadTask();
        uploadTask.execute();
    }

    public void addPhoto(Photo photo){
        Exchanger.photos.add(photo);
        imageAdapter.notifyDataSetChanged();
    }

    /**
     * ImageAdapter
     */

    public class ImageAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<Photo> photos;

        public ImageAdapter(Context context, ArrayList<Photo> photos) {
            this.context = context;
            this.photos = new ArrayList<Photo>();
            this.photos = photos;
        }

        @Override
        public int getCount() {
            return (photos.size() + 1);
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout layoutView;
            Resources r = Resources.getSystem();

            layoutView = new LinearLayout(context);

            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics());
            layoutView.setLayoutParams(new GridView.LayoutParams((int)px, (int)px));
            layoutView.setBackgroundColor(Color.WHITE);
            layoutView.setPadding(10,10,10,10);

            ImageView imageView = new ImageView(context);
            float pxImage = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, r.getDisplayMetrics());

            imageView.setLayoutParams(new GridView.LayoutParams((int)pxImage, (int)pxImage));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            if(position==0) { imageView.setImageResource(R.drawable.plus);}
            else { imageView.setImageBitmap(photos.get(position-1).getBitmap_thumbnail()); }

            layoutView.addView(imageView);
            return layoutView;
        }

    }

    /**
     * Chargement asynchrone des thumbnails
     */

    private class ThumbnailLoadTask extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<Photo> weakPhoto;
        private final WeakReference<ImageAdapter> weakAdapter;

        private ThumbnailLoadTask(Photo photo, ImageAdapter imageAdapter, Activity activity) {
            this.weakPhoto = new WeakReference<Photo>(photo);
            this.weakAdapter = new WeakReference<ImageAdapter>(imageAdapter);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Bitmap doInBackground(String... params) {
            Log.i("ImageLoadTask", "Attempting to load image URL:" + params[0]);
            final Bitmap bitmap = getBitmapFromURL(params[0]);
            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null) {
                final Photo photo = weakPhoto.get();
                final ImageAdapter adapter = weakAdapter.get();
                if(photo != null)
                    photo.setBitmap_thumbnail(bitmap);
                adapter.notifyDataSetChanged();
            }
        }

        private Bitmap getBitmapFromURL(String src) {
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

    private class UploadTask extends AsyncTask<Void, Void, Bitmap>
    {

        private Context mContext;
        private int NOTIFICATION_ID = 1;
        private Notification mNotification;
        private NotificationManager mNotificationManager;

        public UploadTask(){

            this.mContext = getActivity();

            //Get the notification manager
            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        private void createNotification(String contentTitle, String contentText, boolean stop) {

            //Build the notification using Notification.Builder
            Notification.Builder builder = new Notification.Builder(mContext)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setAutoCancel(true)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText);

            if(stop == false)
                builder.setProgress(0,0,true);
            else
                builder.setProgress(0,0,false);

            //Get current notification
            mNotification = builder.getNotification();

            //Show the notification
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            createNotification("FUCK","YEAH",false);

            File file = new File(outputFileUri.getPath());
            bitmap = BitmapFactory.decodeFile(file.getPath());


            try {
                FileOutputStream stream = new FileOutputStream(file);
                Bitmap bitmap2 = Images.resizeBitmap(bitmap, 1500);
                bitmap2.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                stream.close();
                bitmap = bitmap2;
            } catch (Exception e) {
                e.printStackTrace();
            }

            RequestParams requestParams = new RequestParams();
            try {
                requestParams.put("photo",file);
            } catch (Exception e) { e.printStackTrace(); }

            MomentApi.post("addphoto/" + Exchanger.moment.getId(), requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.e("GOO", ""+response.toString());
                    createNotification("YEAH", "FUCK", true);
                    photo.setBitmap_thumbnail(Bitmap.createScaledBitmap(bitmap, 90, 90, false));
                    imageAdapter.notifyDataSetChanged();
                    photo.setBitmap_original(bitmap);
                }

                @Override
                public void onFailure(Throwable e,JSONObject response){
                    Log.e("DOWN",""+response.toString());
                }

            });
            return bitmap;
        }
    }

}