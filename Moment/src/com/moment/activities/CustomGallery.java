package com.moment.activities;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.*;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.moment.BuildConfig;
import com.moment.R;
import com.moment.classes.RecyclingImageView;
import com.moment.util.*;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomGallery extends SherlockFragmentActivity {
    private int count;
    private String[] thumbnailsUri;
    private boolean[] thumbnailsselection;
    private Uri[] arrPath;
    private ImageAdapter imageAdapter;
    private ArrayList<Uri> selectedPictures;
    private Long momentID;

    private static final String IMAGE_CACHE_DIR = "thumbs";
    private ImageFetcher mImageFetcher;
    private int mImageThumbSize;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_gallery);

        if (BuildConfig.DEBUG) {
            Utils.logHeap();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setIcon(R.drawable.logo);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.10f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.picto_photo_vide);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);

        if(savedInstanceState==null){

            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            selectedPictures = new ArrayList<Uri>();
            final String orderBy = MediaStore.Images.Media._ID;
            Cursor imagecursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                    null, orderBy);
            int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
            this.count = imagecursor.getCount();
            this.thumbnailsUri = new String[this.count];
            this.arrPath = new Uri[this.count];
            this.thumbnailsselection = new boolean[this.count];

            for (int i = 0; i < this.count; i++) {
                imagecursor.moveToPosition(i);
                int id = imagecursor.getInt(image_column_index);

                //Get infos about thumb

                Cursor ca = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns.DATA }, MediaStore.MediaColumns._ID + "=?", new String[] {String.valueOf(id)}, null);
                if(ca!=null && ca.moveToFirst()) thumbnailsUri[i] = ca.getString(ca.getColumnIndex(MediaStore.MediaColumns.DATA));
                if(BuildConfig.DEBUG) Log.d("URI", "URI thumb : " + thumbnailsUri[i]);
                ca.close();

                arrPath[i] = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
                if(BuildConfig.DEBUG) Log.d("URI", ""+arrPath[i]);
            }
            imagecursor.close();
        }
        else{
            this.arrPath = (Uri[])savedInstanceState.getParcelableArray("full_images");
            this.thumbnailsUri = savedInstanceState.getStringArray("thumbnails");
            this.selectedPictures = savedInstanceState.getParcelableArrayList("selectedUri");
            this.thumbnailsselection = savedInstanceState.getBooleanArray("thumbnailsSelection");

        }

        GridView imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
        imageAdapter = new ImageAdapter(this);
        imagegrid.setAdapter(imageAdapter);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_custom_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                selectedPictures.clear();
                finish();

            case R.id.validate:
                /*for(Bitmap t : thumbnails){
                    t.recycle();
                }*/
                if (BuildConfig.DEBUG) {
                    Utils.logHeap();
                }

                Intent intent = new Intent(this, MomentInfosActivity.class);
                intent.putExtra("photos", selectedPictures);
                setResult(RESULT_OK, intent);
                finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //selectedPictures.clear();
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    public class ImageAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private Context mContext;
        final float pxImage = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        final float pxBitmap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());

        public ImageAdapter(Context context) {
            mContext = context;
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            RelativeLayout layout;

            final AlphaAnimation fadeIn = new AlphaAnimation(0.5f, 1.0f);
            fadeIn.setDuration(325);
            fadeIn.setFillAfter(true);

            final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.5f);
            fadeOut.setDuration(325);
            fadeOut.setFillAfter(true);

            if (convertView == null) {
                holder = new ViewHolder();

                layout = new RelativeLayout(mContext);
                layout.setLayoutParams(new GridView.LayoutParams((int) pxImage, (int) pxImage));
                layout.setBackgroundColor(getResources().getColor(R.color.white));
                layout.setGravity(Gravity.CENTER);

                holder.imageview = new RecyclingImageView(mContext);
                holder.imageview.setId(0);
                holder.imageview.setLayoutParams(new GridView.LayoutParams((int) pxBitmap, (int) pxBitmap));
                holder.imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp2.addRule(RelativeLayout.CENTER_IN_PARENT, 1);

                holder.checkbox = new CheckBox((mContext));
                holder.checkbox.setLayoutParams(new GridView.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT));
                holder.checkbox.setButtonDrawable(getResources().getDrawable(R.drawable.checkbox));
                RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp1.addRule(RelativeLayout.ALIGN_RIGHT, holder.imageview.getId());
                lp1.addRule(RelativeLayout.ALIGN_TOP, holder.imageview.getId());
                holder.checkbox.setLayoutParams(lp1);

                layout.addView(holder.imageview);
                layout.addView(holder.checkbox);

                holder.checkbox.setEnabled(false);
                convertView = layout;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.checkbox.setId(position);
            holder.imageview.setId(position);

            holder.imageview.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (holder.checkbox.isChecked() == true) {
                        selectedPictures.remove(arrPath[holder.imageview.getId()]);
                        holder.checkbox.setChecked(false);
                        thumbnailsselection[holder.imageview.getId()] = false;
                        v.startAnimation(fadeIn);
                    } else {
                        holder.checkbox.setChecked(true);
                        v.startAnimation(fadeOut);
                        selectedPictures.add(arrPath[holder.imageview.getId()]);
                        thumbnailsselection[holder.imageview.getId()] = true;
                    }
                }
            });
            mImageFetcher.loadImage(thumbnailsUri[holder.imageview.getId()], holder.imageview, false);

            holder.checkbox.setChecked(thumbnailsselection[position]);
            if( thumbnailsselection[holder.imageview.getId()]==true) holder.imageview.startAnimation(fadeOut);
            else holder.imageview.startAnimation(fadeIn);
            holder.id = position;


            return convertView;
        }
    }

    class ViewHolder {
        RecyclingImageView imageview;
        CheckBox checkbox;
        int id;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putParcelableArray("full_images", arrPath);
        savedInstanceState.putStringArray("thumbnails", thumbnailsUri);
        savedInstanceState.putParcelableArrayList("selectedUri", selectedPictures);
        savedInstanceState.putBooleanArray("thumbnailsSelection", thumbnailsselection);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}
