package com.moment.fragments;

import android.annotation.TargetApi;
import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.BuildConfig;
import com.moment.R;
import com.moment.activities.CustomGallery;
import com.moment.activities.DetailPhoto;
import com.moment.activities.MomentInfosActivity;
import com.moment.models.Chat;
import com.moment.util.ImageCache;
import com.moment.util.ImageFetcher;
import com.moment.util.Images;
import com.moment.classes.MomentApi;
import com.moment.classes.RecyclingImageView;
import com.moment.models.Moment;
import com.moment.models.Photo;
import com.moment.models.User;
import com.moment.util.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhotosFragment extends Fragment {

    private Long momentID;

    private ImageAdapter imageAdapter;

    private Uri outputFileUri;

    private List<Photo> photos;
    private ArrayList<Uri> photos_uri;
    private ArrayList<Bitmap> photos_files;

    private View view;

    private Intent intent;
    private GridView gridView;
    private Button defaultButton;

    private boolean asyncRun = false;

    private final int CAMERA_PICTURE = 1;
    private final int GALLERY_PICTURE = 2;

    private Tracker mGaTracker;
    private GoogleAnalytics mGaInstance;

    private int uploadingPhotos = 0;
    private int currentUploading = 1;

    private static final String IMAGE_CACHE_DIR = "thumbs";
    private ImageFetcher mImageFetcher;
    private int mImageThumbSize;

    private static final String TAG = "PhotosFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Utils.logHeap();
        }

        photos_uri = new ArrayList<Uri>();

        if (savedInstanceState != null) {
            photos = savedInstanceState.getParcelableArrayList("photos");
            if(BuildConfig.DEBUG){
                Log.d(TAG, "Photos saved");
            }
        }

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.picto_photo_vide);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);

        mGaInstance = GoogleAnalytics.getInstance(getActivity());
        mGaTracker = mGaInstance.getTracker(AppMoment.getInstance().GOOGLE_ANALYTICS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_photos, container, false);

        //Init Grid view
        gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == 0) {
                    EasyTracker.getTracker().sendEvent("Photo", "button_press", "Add Photo", null);
                    startDialog();
                } else {
                    EasyTracker.getTracker().sendEvent("Photo", "button_press", "Open Detail Photo", null);
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

        if(photos!=null){
            initViewPhotos();
            imageAdapter = new ImageAdapter(view.getContext(), photos);
            gridView.setAdapter(imageAdapter);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (((MomentInfosActivity) getActivity()).getMomentId() != null) {
            this.momentID = ((MomentInfosActivity) getActivity()).getMomentId();
            initPhoto();
        }

        mGaTracker.sendView("/PhotosFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("PhotoFragment", "PAUSE");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.momentID != null) imageAdapter.notifyDataSetChanged();
        Log.e("PhotoFragment", "RESUME");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
        myAlertDialog.setTitle(getResources().getString(R.string.partager_photos));
        myAlertDialog.setMessage(getResources().getString(R.string.galerie));

        myAlertDialog.setPositiveButton("Galerie", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                EasyTracker.getTracker().sendEvent("Photo", "button_press", "Pick from Gallery", null);
                intent = new Intent(getActivity(), CustomGallery.class);
                intent.setType("image/*");
                intent.putExtra("return-data", true);
                intent.putExtra("momentID", momentID);
                startActivityForResult(intent, GALLERY_PICTURE);
            }
        });

        myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                EasyTracker.getTracker().sendEvent("Photo", "button_press", "Pick from Camera", null);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PICTURE && resultCode == Activity.RESULT_OK) {
            if (photos_uri == null) {
                photos_uri = new ArrayList<Uri>();
            }
            photos_uri.add(outputFileUri);
            for (Uri s : photos_uri) {
                MultiUploadTask multiUploadTask = new MultiUploadTask(s);
                multiUploadTask.execute();
            }
            photos_uri.clear();
        }
        else if(requestCode == GALLERY_PICTURE && resultCode == Activity.RESULT_OK){
            if(data.getExtras().containsKey("photos")){
                photos_uri = data.getExtras().getParcelableArrayList("photos");
                uploadingPhotos = photos_uri.size();
                currentUploading = 1;
                MultiUploadTask multiUploadTask = new MultiUploadTask(photos_uri.get(0));
                multiUploadTask.execute();
            }
        }
    }

    public boolean isAsyncRun() {
        return asyncRun;
    }

    public class ImageAdapter extends BaseAdapter {

        private final Context context;

        public ImageAdapter(Context context, List<Photo> photos) {
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
        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView imageView;
            float pxImage = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
            float pxBitmap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());

            if (convertView == null) {
                imageView = new RecyclingImageView(context);
                imageView.setId(0);
                imageView.setLayoutParams(new GridView.LayoutParams((int) pxImage, (int) pxImage));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setCropToPadding(true);
                imageView.setPadding(10, 10, 10, 10);
                imageView.setBackgroundColor(getResources().getColor(R.color.white));
            } else {
                imageView = (ImageView) convertView;
            }

            if (position == 0) {
                imageView.setImageResource(R.drawable.plus);
            } else {
                try {
                    if(photos.get(position-1).getUrlThumbnail()!=null) mImageFetcher.loadImage(photos.get(position-1).getUrlThumbnail(), imageView, false);
                    else imageView.setImageDrawable(getResources().getDrawable(R.drawable.picto_photo_vide));
                } catch (OutOfMemoryError outOfMemoryError) {
                    outOfMemoryError.printStackTrace();
                }
            }
            return imageView;
        }
    }

    /**
     * Async Task to upload photos
     */

    private class MultiUploadTask extends AsyncTask<Void, Void, String> {
        private final Context context;
        private final int notificationId = 1;
        private final NotificationManager notificationManager;
        private Notification notification;
        private Photo photo;
        private final Uri photo_uri;
        private int position;


        public MultiUploadTask(Uri photo_uri) {
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

            if (!stop)
                builder.setProgress(0, 0, true);
            else
                builder.setProgress(0, 0, false);

            notification = builder.getNotification();

            notificationManager.notify(notificationId, notification);
        }

        @Override
        protected void onPreExecute() {
            createNotification("Upload", "Upload Photo "+currentUploading+"/"+uploadingPhotos, false);
            asyncRun = true;
            photo = new Photo();
            photos.add(photo);
            position = photos.size() - 1;
            photos_uri.remove(0);
            imageAdapter.notifyDataSetChanged();
            gridView.smoothScrollToPosition(position + 1);
            gridView.setVisibility(View.VISIBLE);
            currentUploading += 1;
        }

        @Override
        protected String doInBackground(Void... params) {



            Bitmap bitmap = Images.decodeSampledBitmapFromURI(photo_uri, getActivity().getContentResolver(), 900, 900);

            if (bitmap != null) {

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpContext localContext = new BasicHttpContext();
                    localContext.setAttribute(ClientContext.COOKIE_STORE, MomentApi.myCookieStore);
                    HttpPost httpPost = new HttpPost(MomentApi.BASE_URL + "addphoto/" + momentID);
                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                    byte[] data = bos.toByteArray();
                    entity.addPart("photo", new ByteArrayBody(data, "photo.png"));
                    httpPost.setEntity(entity);
                    HttpResponse response = httpClient.execute(httpPost, localContext);

                    String sResponse = EntityUtils.toString(response.getEntity());
                    bitmap.recycle();
                    return sResponse;
                } catch (Exception e) {
                    Log.e(e.getClass().getName(), e.getMessage(), e);
                    bitmap.recycle();
                    return null;
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            Log.e("RESULT", result.toString());

       //     try {
                try {
                    JSONObject jsresult = new JSONObject(result);
                    JSONObject json = jsresult.getJSONObject("success");

                    photo.setId(json.getLong("id"));

                    photo.setNbLike(json.getInt("nb_like"));
                    photo.setUrlOriginal(json.getString("url_original"));
                    photo.setUrlThumbnail(json.getString("url_thumbnail"));
                    photo.setUrlUnique(json.getString("unique_url"));
                    Date timestamp = new Date(Long.valueOf(json.getString("time")) * 1000);

                    photo.setTime(timestamp);
                    User user = new User();
                    user.setUserFromJson(json.getJSONObject("taken_by"));
                    photo.setUser(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                float pxBitmap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
                //Picasso.with(context).load(photo.getUrlThumbnail()).resize((int) pxBitmap, (int) pxBitmap).centerCrop().into(photo.getGridImage());
                mImageFetcher.loadImage(photos.get(position).getUrlThumbnail(), (ImageView)gridView.getChildAt((position+1)-gridView.getFirstVisiblePosition()).findViewById(0), false);

                if (photos_uri.size() == 0) {
                    createNotification("Upload", context.getString(R.string.termine), true);
                    asyncRun = false;
                }
/*
            } catch (NullPointerException npe) {
                Log.e("NPE", "");
                npe.printStackTrace();
            }*/

            if (isAdded()) {
                if (photos_uri.size() > 0) {
                    MultiUploadTask multiUploadTask = new MultiUploadTask(photos_uri.get(0));
                    multiUploadTask.execute();
                }
            } else {
                createNotification(context.getString(R.string.upload_annul), photos_uri.size() + context.getString(R.string.upload_annul_2), true);
            }
        }

    }

    public void createFragment(Long momentId) {
        this.momentID = momentId;
        initPhoto();
    }


    private void initPhoto() {

        User user = AppMoment.getInstance().user;
        Moment moment = user.getMomentById(momentID);

        if (photos == null || photos.isEmpty()) {
            photos = moment.getPhotos();

            //Init adapter
            imageAdapter = new ImageAdapter(view.getContext(), photos);
            gridView.setAdapter(imageAdapter);

            if (AppMoment.getInstance().checkInternet()) {
                if(BuildConfig.DEBUG) Log.d(TAG, "Download photos");

                MomentApi.get("photosmoment/" + momentID, null, new JsonHttpResponseHandler() {

                    public void onSuccess(JSONObject response) {
                        try {
                            JSONArray jsonPhotos = response.getJSONArray("photos");


                            photos.clear();
                            for (int i = 0; i < jsonPhotos.length(); i++) {
                                Photo photo = new Photo();
                                photo.photoFromJSON(jsonPhotos.getJSONObject(i));
                                AppMoment.getInstance().user.getMomentById(momentID).getPhotos().add(photo);

                                AppMoment.getInstance().photoDao.insertOrReplace(photo);
                            }

                            initViewPhotos();
                            imageAdapter.notifyDataSetChanged();
                            //We update it in the infos view also
                            ((MomentInfosActivity) getActivity()).updateInfosPhotos(AppMoment.getInstance().user.getMomentById(momentID).getPhotos());

                            if (BuildConfig.DEBUG) {
                                Utils.logHeap();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String content) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.pb_get_photos), Toast.LENGTH_LONG).show();
                        initViewPhotos();
                        imageAdapter.notifyDataSetChanged();
                    }
                });
            } else {

                initViewPhotos();
                imageAdapter.notifyDataSetChanged();
            }

        }


    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putParcelableArrayList("photos", (ArrayList<Photo>)photos);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void initViewPhotos(){
        if(photos.size()>0){
            gridView = (GridView) view.findViewById(R.id.gridview);
            gridView.setVisibility(View.VISIBLE);
        }
        else{
            defaultButton = (Button) view.findViewById(R.id.default_button_photos);
            defaultButton.setVisibility(View.VISIBLE);
            defaultButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EasyTracker.getTracker().sendEvent("Photo", "button_press", "Add Photo First", null);
                    startDialog();
                }
            });

        }
    }
}