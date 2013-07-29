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
import android.graphics.Rect;
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
import com.moment.activities.MomentInfosActivity;
import com.moment.classes.Images;
import com.moment.classes.MomentApi;
import com.moment.models.Moment;
import com.moment.models.Photo;
import com.moment.models.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class PhotosFragment extends Fragment {

    private Long momentID;

    private ImageAdapter imageAdapter;

    private Uri outputFileUri;

    private ArrayList<Photo> photos;
    private ArrayList<String> photos_uri;
    private ArrayList<Bitmap> photos_files;

    private View view;

    private Intent intent;
    private GridView gridView;

    private boolean asyncRun = false;

    private final int CAMERA_PICTURE = 1;
    private final int GALLERY_PICTURE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            savedInstanceState = getActivity().getIntent().getExtras();
            photos_files = new ArrayList<Bitmap>();

            assert savedInstanceState != null;
            if(savedInstanceState.getStringArrayList("photos") != null)
            {
                photos_uri = savedInstanceState.getStringArrayList("photos");
                assert photos_uri != null;
                for(String s : photos_uri) {
                    File tempFile = new File(s);
                    try {
                        FileInputStream fi = new FileInputStream(tempFile);
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        photos_files.add(BitmapFactory.decodeStream(fi, new Rect(0,0,0,0), options)) ;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_photos, container, false);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        if(((MomentInfosActivity)getActivity()).getMomentId()!=null){
            this.momentID = ((MomentInfosActivity)getActivity()).getMomentId();
            initPhoto();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.e("PhotoFragment","PAUSE");
    }

    @Override
    public void onResume() {
        super.onResume();
        if(this.momentID!=null) imageAdapter.notifyDataSetChanged();
        Log.e("PhotoFragment","RESUME");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        photos.clear();
        Log.e("PhotoFragment","DESTROY");
    }

    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
        myAlertDialog.setTitle("Partager des Photos");
        myAlertDialog.setMessage("Prendre une photo ou importer des photos depuis la galerie");

        myAlertDialog.setPositiveButton("Galerie", new DialogInterface.OnClickListener() {
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
            if(photos_uri == null) { photos_uri = new ArrayList<String>(); }
            photos_uri.add(outputFileUri.getPath());
            for(String s: photos_uri){
                MultiUploadTask multiUploadTask = new MultiUploadTask(s);
                multiUploadTask.execute();
            }
            photos_uri.clear();
        }
    }

    public boolean isAsyncRun() {
        return asyncRun;
    }

    public class ImageAdapter extends BaseAdapter {

        private final Context context;

        public ImageAdapter(Context context, ArrayList<Photo> photos) {
            this.context = context;
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
            float pxImage = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
            float pxBitmap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());

            if(convertView == null) {
                imageView = new ImageView(context);

                imageView.setLayoutParams(new GridView.LayoutParams((int)pxImage, (int)pxImage));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setCropToPadding(true);
                imageView.setPadding(10, 10, 10, 10);
                imageView.setBackgroundColor(Color.WHITE);
            }

            else {
                imageView = (ImageView) convertView;
            }

            if(position==0) { imageView.setImageResource(R.drawable.plus);}
            else {
                imageView.setTag(photos.get(position-1).getId());
                Picasso.with(context).load(photos.get(position-1).getUrlThumbnail()).resize((int)pxBitmap,(int)pxBitmap).centerCrop().placeholder(R.drawable.picto_photo_vide).into(imageView);
                photos.get(position-1).setGridImage(imageView);
            }
            return imageView;
        }

        public void updateView(int position){

        }
    }

    private class MultiUploadTask extends AsyncTask<Void, Void, Void>
    {
        private final Context context;
        private final int notificationId = 1;
        private final NotificationManager notificationManager;
        private Notification notification;
        private Photo photo;
        private final String photo_uri;
        private int position;


        public MultiUploadTask(String photo_uri){
            this.context = getActivity();
            this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            this.photo_uri = photo_uri;
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        private void createNotification(String contentTitle, String contentText, boolean stop) {

            Notification.Builder builder = new Notification.Builder(context)
                    .setSmallIcon(android.R.drawable.stat_sys_upload)
                    .setAutoCancel(true)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText);

            if(!stop)
                builder.setProgress(0,0,true);
            else
                builder.setProgress(0,0,false);

            notification = builder.getNotification();

            notificationManager.notify(notificationId, notification);
        }

        @Override
        protected void onPreExecute(){
            createNotification("Upload", photos_uri.size() + " Photos", false);
            asyncRun = true;
            photo = new Photo();
            photos.add(photo);
            position = photos.size()-1;
            photos_uri.remove(0);
            imageAdapter.notifyDataSetChanged();
            gridView.smoothScrollToPosition(position+1);
        }

        @Override
        protected Void doInBackground(Void... params) {

            RequestParams requestParams = new RequestParams();


            File file = new File(photo_uri);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());

            try {
                FileOutputStream stream = new FileOutputStream(file);
                bitmap = Images.resizeBitmap(bitmap, 900);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                stream.close();
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

                    try {
                        try {
                            JSONObject json = response.getJSONObject("success");

                            photo.setId(json.getInt("id"));

                            photo.setNbLike(json.getInt("nb_like"));
                            photo.setUrlOriginal(json.getString("url_original"));
                            photo.setUrlThumbnail(json.getString("url_thumbnail"));
                            photo.setUrlUnique(json.getString("unique_url"));
                            Date timestamp = new Date(Long.valueOf(json.getString("time"))*1000);

                            photo.setTime(timestamp);
                            User user = new User();
                            user.setUserFromJson(json.getJSONObject("taken_by"));
                            photo.setUser(user);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        float pxBitmap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
                        Picasso.with(context).load(photo.getUrlThumbnail()).resize((int) pxBitmap, (int) pxBitmap).centerCrop().into(photo.getGridImage());

                        if(photos_uri.size() == 0)
                        {
                            createNotification("Upload", "Termine", true);
                            asyncRun = false;
                        }

                    } catch (NullPointerException npe) {
                        Log.e("NPE", "");
                        npe.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable e,JSONObject response){
                    e.printStackTrace();
                    System.out.println(response.toString());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if(isAdded()){
                if(photos_uri.size() > 0){
                    MultiUploadTask multiUploadTask = new MultiUploadTask(photos_uri.get(0));
                    multiUploadTask.execute();
                }
            } else {
                createNotification("Upload annule", photos_uri.size() + " photos n'ont pas etees uploadees", true);
            }
        }

    }

    public void createFragment(Long momentId){
        this.momentID = momentId;

        initPhoto();
    }

    /**
     * Function called to init the photo view when all the infos are ready
     */

    private void initPhoto(){

        User user = AppMoment.getInstance().user;
        Moment moment = user.getMomentById(momentID);

        if(photos == null || photos.isEmpty())
        {
            photos = moment.getPhotos();

            MomentApi.get("photosmoment/" + momentID, null, new JsonHttpResponseHandler() {

                public void onSuccess(JSONObject response) {
                    try {
                        JSONArray jsonPhotos = response.getJSONArray("photos");

                        for (int i = 0; i < jsonPhotos.length(); i++) {
                            Photo photo = new Photo();
                            photo.photoFromJSON(jsonPhotos.getJSONObject(i));
                            AppMoment.getInstance().user.getMomentById(momentID).getPhotos().add(photo);
                            imageAdapter.notifyDataSetChanged();
                        }

                        if(!photos_files.isEmpty() && !photos_uri.isEmpty())
                        {

                            MultiUploadTask multiUploadTask = new MultiUploadTask(photos_uri.get(0));
                            multiUploadTask.execute();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        imageAdapter = new ImageAdapter(view.getContext(), photos);
        gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == 0) {
                    startDialog();
                } else {
                    Intent intent = new Intent(getActivity(), DetailPhoto.class);
                    if (photos.get(position - 1).getUrlOriginal() == null) {
                        intent.putExtra("position", (0));
                    } else {
                        intent.putExtra("position", (position - 1));
                    }
                    intent.putExtra("momentID", momentID);
                    startActivity(intent);
                }
            }
        });

    }
}