package com.moment.infos.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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

    static final int PICK_CAMERA_PHOTOS = 0;
    private LayoutInflater layoutInflater;
    private RelativeLayout detailPhoto;
    private GridView gridView;
    private ImageAdapter imageAdapter;
    private String albumName = "Moment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Exchanger.photos.isEmpty()){
            MomentApi.get("photosmoment/"+Exchanger.idMoment, null, new JsonHttpResponseHandler() {

                public void onSuccess(JSONObject response) {
                    try {
                        JSONArray jsonPhotos = response.getJSONArray("photos");

                        for(int i=0;i<jsonPhotos.length();i++)
                        {
                            Log.i("Fragment Photo","Load");
                            Photo photo = new Photo();
                            photo.photoFromJSON(jsonPhotos.getJSONObject(i));
                            Exchanger.photos.add(photo);
                            imageAdapter.notifyDataSetChanged();
                            ThumbnailLoadTask imageLoadTask = new ThumbnailLoadTask(photo, imageAdapter, getActivity());
                            imageLoadTask.execute(photo.getUrl_thumbnail());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            //TODO La liste des photos n'est pas vide, checker si nouvelles photos sur serveur
            Log.e("CREATE RESUME", "Photos");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        Log.e("SAVEINSTANCE", "Photos");
        savedInstanceState.putBoolean("Sleep", true);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.e("PAUSE", "Photos");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.e("RESUME", "Photos");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.e("STOP", "Photos");
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.e("START", "Photos");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        Log.d("PHOTO", "Photo création");

        //On recupere le template
        detailPhoto = (RelativeLayout)inflater.inflate(R.layout.detail_photo, null);

        imageAdapter = new ImageAdapter(view.getContext(), Exchanger.photos);

        gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(imageAdapter);


        //Listener onClick sur une miniature
        gridView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                //Si l'utilisateur decide d'ajouter une photo
                if(position==0){
                    Log.d("Ajout Photo", "Ajout PHOTO");
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    try {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createImageFile()));
                    } catch (IOException e) {e.printStackTrace();}


                    startActivityForResult(takePictureIntent, PICK_CAMERA_PHOTOS);
                }
                else{
                    Intent intent = new Intent(getActivity(), DetailPhoto.class);
                    intent.putExtra("position", (position-1));
                    startActivity(intent);
                }
            };
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data) {

        if (requestCode == PICK_CAMERA_PHOTOS) {

            Bundle extras = data.getExtras();

            Bitmap mImageBitmap = (Bitmap) extras.get("data");
            //mImageBitmap = Images.getResizedBitmap(mImageBitmap, 1500, 1500);

            Photo photo = new Photo();

            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/Moment";
            File dir = new File(filePath);
            if(!dir.exists())
                dir.mkdirs();
            File file = new File(dir, "moment" + photo.getId() + ".jpg");
            try {
                FileOutputStream stream = new FileOutputStream(file);
                mImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.flush();
                stream.close();

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            RequestParams params = new RequestParams();
            try { params.put("photo", file); } catch (FileNotFoundException e) { e.printStackTrace(); }

            MomentApi.post("addphoto/" + Exchanger.moment.getId(), params, new JsonHttpResponseHandler() {});
            photo.setBitmap_thumbnail(mImageBitmap);
            addPhoto(photo);
        }
    }


    public void addPhoto(Photo photo){
        Log.d("ADD PHOTOS", "IN PHOTOS FRAG");
        Exchanger.photos.add(photo);
        //imageAdapter.addPhoto(image);
        imageAdapter.notifyDataSetChanged();
    }



    /**
     * ImageAdapter
     */

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<Photo> photos;

        public ImageAdapter(Context c) {
            mContext = c;
            this.photos = new ArrayList<Photo>();
        }

        public ImageAdapter(Context c, ArrayList<Photo> photos) {
            mContext = c;
            this.photos = new ArrayList<Photo>();
            this.photos = photos;
        }

        public void addPhoto(Photo photo){
            photos.add(photo);
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

            layoutView = new LinearLayout(mContext);

            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics());
            layoutView.setLayoutParams(new GridView.LayoutParams((int)px, (int)px));
            layoutView.setBackgroundColor(Color.WHITE);
            layoutView.setPadding(10,10,10,10);

            ImageView imageView = new ImageView(mContext);
            float pxImage = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, r.getDisplayMetrics());

            imageView.setLayoutParams(new GridView.LayoutParams((int)pxImage, (int)pxImage));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            if(position==0) { imageView.setImageResource(R.drawable.plus);}
            else { imageView.setImageBitmap(photos.get(position-1).getBitmap_thumbnail()); }

            layoutView.addView(imageView);
            return layoutView;
        }

    }

    private File getAlbumDir(){
        File storageDir = new File(Environment.getExternalStorageDirectory() + "Pictures/" + this.albumName);
        return storageDir;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File image = File.createTempFile(imageFileName, ".jpg", getAlbumDir());
        return image;
    }

    /**
     * Chargement asynchrone des thumbnails
     */

    public class ThumbnailLoadTask extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<Photo> weakPhoto;
        private final WeakReference<ImageAdapter> weakAdapter;



        public ThumbnailLoadTask(Photo photo, ImageAdapter imageAdapter, Activity activity) {
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