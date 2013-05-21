package com.moment.infos.fragments;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.moment.DetailPhoto;
import com.moment.MomentInfosActivity.Exchanger;
import com.moment.R;
import com.moment.classes.Images;
import com.moment.classes.MomentApi;
import com.moment.classes.Photo;

public class PhotosFragment extends Fragment {
	
	static final int PICK_CAMERA_PHOTOS = 0;
	
	public View view;
	LayoutInflater inflater;
	LinearLayout momentDetail;
	RelativeLayout detailPhoto;
	GridView gridView;
	ImageAdapter imageAdapter;
	private String albumName = "Moment";

    //Static Man

	private void getBitmapThumbnailFromURL(Photo photo) {
		final int id = photo.getId(); 
		AsyncHttpClient client = new AsyncHttpClient();
		String[] allowedContentTypes = new String[] {"image/png", "image/jpeg"};
		client.get(photo.getUrl_thumbnail(), new BinaryHttpResponseHandler(allowedContentTypes) {    		
			@Override
			public void onSuccess(byte[] fileData) 
			{
				InputStream is = new ByteArrayInputStream(fileData);
				Bitmap bmp = BitmapFactory.decodeStream(is);
				setPhotoBitmapThumbnail(id, bmp);
			}
		});	
	}
	
	public void setPhotoBitmapThumbnail(int id, Bitmap bitmap_thumbnail){
		for(Photo p : Exchanger.photos)
		{
			if(p.getId() == id)
			{
				p.setBitmap_thumbnail(bitmap_thumbnail);
				imageAdapter.notifyDataSetChanged();
				return ;
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Exchanger.photos.isEmpty()){
	        MomentApi.get("photosmoment/"+Exchanger.idMoment, null, new JsonHttpResponseHandler() {
                public void onSuccess(JSONObject response) {
                    try {
                        JSONArray photos = response.getJSONArray("photos");
                        ArrayList<Photo> tempPhotos = new ArrayList<Photo>();

                        for(int i=0;i<photos.length();i++)
                        {
                            Photo tempPhoto = new Photo();
                            addPhoto(tempPhoto);
                            imageAdapter.notifyDataSetChanged();

                            tempPhoto.photoFromJSON(photos.getJSONObject(i));
                            getBitmapThumbnailFromURL(tempPhoto);
                            tempPhotos.add(tempPhoto);
                        }

                        Exchanger.moment.setPhotos(tempPhotos);
				    	
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
	
	/**
	 * 
	 * @author adriendulong
	 *
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
	
	
	public void detailPhoto(){
		Log.d("Detail", "DETAIL");  
	}
	
	
	
	public void addPhoto(Photo photo){
		Log.d("ADD PHOTOS", "IN PHOTOS FRAG");
		Exchanger.photos.add(photo);
		//imageAdapter.addPhoto(image);
		imageAdapter.notifyDataSetChanged();	
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode,
            Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
		Log.d("TEST", "TEST");
        if (requestCode == PICK_CAMERA_PHOTOS) {
            //if (resultCode == RESULT_OK) {
                // A contact was picked.  Here we will just display it
                // to the user.

            	Bundle extras = data.getExtras();
            	
                Bitmap mImageBitmap = (Bitmap) extras.get("data");
                mImageBitmap = Images.resizeBitmap(mImageBitmap, 1500);
                
                Photo photo = new Photo();
                /* Bitemap a uploader -> byte array */
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
                	
                /* Upload nouvelle photo */
                
                	RequestParams params = new RequestParams();
	                try { params.put("photo", file); } catch (FileNotFoundException e) { e.printStackTrace(); }
	                
                MomentApi.post("addphoto/" + Exchanger.moment.getId(), params, new JsonHttpResponseHandler() {});
                photo.setBitmap_thumbnail(mImageBitmap);
                addPhoto(photo);
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

}