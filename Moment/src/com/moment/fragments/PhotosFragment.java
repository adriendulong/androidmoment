package com.moment.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.activities.CustomGallery;
import com.moment.activities.DetailPhoto;
import com.moment.classes.Images;
import com.moment.classes.MomentApi;
import com.moment.models.Moment;
import com.moment.models.Photo;
import com.moment.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PhotosFragment extends Fragment {

    private Long momentID;

    private LayoutInflater layoutInflater;
    private RelativeLayout detailPhoto;
    private GridView gridView;
    private ImageAdapter imageAdapter;

    private Uri outputFileUri;

    private Bitmap bitmap = null;

    private ArrayList<Photo> photos;
    private ArrayList<String> photos_uri;
    private ArrayList<Bitmap> photos_files;

    File tempFile;

    Intent intent;

    int CAMERA_PICTURE = 1;
    int GALLERY_PICTURE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            momentID = getActivity().getIntent().getLongExtra("id", 1);
            savedInstanceState = getActivity().getIntent().getExtras();
            photos_files = new ArrayList<Bitmap>();
            if(savedInstanceState.getStringArrayList("photos") != null)
            {
                photos_uri = savedInstanceState.getStringArrayList("photos");

                for(String s : photos_uri) {
                    tempFile = new File(s);
                    try {
                        FileInputStream fi = new FileInputStream(tempFile);
                        photos_files.add(BitmapFactory.decodeStream(fi)) ;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        detailPhoto = (RelativeLayout) inflater.inflate(R.layout.detail_photo, null);
        Log.e("PhotosFragment", "onCreateView");
        User user = AppMoment.getInstance().user;
        Moment moment = user.getMomentById(momentID);
        photos = moment.getPhotos();
        imageAdapter = new ImageAdapter(view.getContext(), photos);
        gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(imageAdapter);
        return view;
    }

    @Override
    public void onStart(){
        Log.e("PhotosFragment","OnStart");
        super.onStart();

        if(!photos_files.isEmpty())
        {

            RequestParams requestParams = new RequestParams();

            for(String s : photos_uri){
                File file = new File(s);
                bitmap = BitmapFactory.decodeFile(file.getPath());

                try {
                    FileOutputStream stream = new FileOutputStream(file);
                    Bitmap bitmap2 = Images.resizeBitmap(bitmap, 1500);
                    bitmap2.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    stream.close();
                    bitmap = bitmap2;
                    Photo tempPhoto = new Photo();
                    tempPhoto.setUser(AppMoment.getInstance().user);
                    tempPhoto.setBitmapThumbnail(bitmap2);
                    tempPhoto.setBitmapOriginal(bitmap2);
                    photos.add(tempPhoto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    requestParams.put("photo", file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                MomentApi.post("addphoto/" + momentID, requestParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        imageAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        MomentApi.get("photosmoment/" + momentID, null, new JsonHttpResponseHandler() {

            public void onSuccess(JSONObject response) {
                try {
                    JSONArray jsonPhotos = response.getJSONArray("photos");

                    for (int i = 0; i < jsonPhotos.length(); i++) {
                        Photo photo = new Photo();
                        photo.photoFromJSON(jsonPhotos.getJSONObject(i));
                        AppMoment.getInstance().user.getMomentById(momentID).getPhotos().add(photo);
                        imageAdapter.notifyDataSetChanged();

                        if (AppMoment.getInstance().getBitmapFromMemCache("thumbnail_" + photo.getId()) == null) {
                            ThumbnailLoadTask imageLoadTask = new ThumbnailLoadTask(photo, imageAdapter, getActivity());
                            imageLoadTask.execute(photo.getUrlThumbnail());
                        } else {
                            photo.setBitmapThumbnail(AppMoment.getInstance().getBitmapFromMemCache("thumbnail_" + photo.getId()));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == 0) {


                    startDialog();

                } else {
                    Intent intent = new Intent(getActivity(), DetailPhoto.class);
                    intent.putExtra("position", (position - 1));
                    intent.putExtra("momentID", momentID);
                    startActivity(intent);
                }
            }
        });
    }

    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                intent = new Intent(getActivity(), CustomGallery.class);
                intent.setType("image/*");
                intent.putExtra("return-data", true);
                intent.putExtra("momentID", momentID);
                startActivityForResult(intent, GALLERY_PICTURE);
            }
        });

        myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures/Moment/");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/Moment/", "moment_"
                        + String.valueOf(System.currentTimeMillis())
                        + ".jpg");

                outputFileUri = Uri.fromFile(file);
                try {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(intent, CAMERA_PICTURE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        myAlertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == CAMERA_PICTURE && resultCode == Activity.RESULT_OK)
        {
            UploadTask uploadTask = new UploadTask();
            uploadTask.execute();
        }
    }

    @Override
    public void onPause(){
        Log.e("PhotosFragment", "OnPause");
        super.onPause();
    }

    @Override
    public void onStop () {
        photos.clear();
        imageAdapter.notifyDataSetChanged();
        super.onStop();
        Log.e("onStop", "PhotosFragment");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e("onDestroy", "PhotosFragment");
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        imageAdapter.notifyDataSetChanged();
    }

    public class ImageAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<Photo> photos;

        public ImageAdapter(Context context, ArrayList<Photo> photos) {
            this.context = context;
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

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View  getView(int position, View convertView, ViewGroup parent) {

            ImageView imageView;

            if(convertView == null) {
                imageView = new ImageView(context);
            }

            else {
                ImageView restoringView = (ImageView) convertView;
                imageView = restoringView;
            }

                float pxImage = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

                imageView.setLayoutParams(new GridView.LayoutParams((int)pxImage, (int)pxImage));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setCropToPadding(true);
                imageView.setPadding(10, 10, 10, 10);
                imageView.setBackgroundColor(Color.WHITE);

            if(position==0) { imageView.setImageResource(R.drawable.plus);}
            else { imageView.setImageBitmap(photos.get(position - 1).getBitmapThumbnail()); }
            return imageView;
        }
    }

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
            final Bitmap bitmap = getBitmapFromURL(params[0]);
            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null) {
                final Photo photo = weakPhoto.get();
                final ImageAdapter adapter = weakAdapter.get();
                if(photo != null)
                    photo.setBitmapThumbnail(bitmap);
                    AppMoment.getInstance().addBitmapToMemoryCache("thumbnail_"+photo.getId(), photo.getBitmapThumbnail());
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
        private Photo tempPhoto;


        public UploadTask(){
            this.mContext = getActivity();
            this.mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            this.tempPhoto = new Photo();
            imageAdapter.notifyDataSetChanged();
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        private void createNotification(String contentTitle, String contentText, boolean stop) {

            Notification.Builder builder = new Notification.Builder(mContext)
                    .setSmallIcon(android.R.drawable.stat_sys_upload)
                    .setAutoCancel(true)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText);

            if(stop == false)
                builder.setProgress(0,0,true);
            else
                builder.setProgress(0,0,false);

            mNotification = builder.getNotification();

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

                tempPhoto.setUser(AppMoment.getInstance().user);
                tempPhoto.setBitmapThumbnail(bitmap2);
                tempPhoto.setBitmapOriginal(bitmap2);
                photos.add(tempPhoto);
            } catch (Exception e) {
                e.printStackTrace();
            }

            RequestParams requestParams = new RequestParams();
            try {
                requestParams.put("photo",file);
            } catch (Exception e) { e.printStackTrace(); }


            MomentApi.post("addphoto/" + momentID, requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    createNotification("YEAH", "FUCK", true);
                    Log.e("UploadPhoto", "ADD");
                    imageAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Throwable e,JSONObject response){

                }
            });
            return bitmap;
        }
    }

}