package com.moment.activities;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.moment.BuildConfig;
import com.moment.R;
import com.moment.util.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomGallery extends SherlockActivity {
    private int count;
    private Bitmap[] thumbnails;
    private boolean[] thumbnailsselection;
    private String[] arrPath;
    private ImageAdapter imageAdapter;
    private ArrayList<String> selectedPictures;
    private Long momentID;
    private int sizeImage;

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

        float pxBitmap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
        sizeImage = (int)pxBitmap;

        savedInstanceState = getIntent().getExtras();

        momentID = savedInstanceState.getLong("momentID");

        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        selectedPictures = new ArrayList<String>();
        final String orderBy = MediaStore.Images.Media._ID;
        Cursor imagecursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
        int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count = imagecursor.getCount();
        this.thumbnails = new Bitmap[this.count];
        this.arrPath = new String[this.count];
        this.thumbnailsselection = new boolean[this.count];

        for (int i = 0; i < this.count; i++) {
            imagecursor.moveToPosition(i);
            int id = imagecursor.getInt(image_column_index);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            //thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
             //       getApplicationContext().getContentResolver(), id,
              //      MediaStore.Images.Thumbnails.MICRO_KIND, null);
            arrPath[i] = imagecursor.getString(dataColumnIndex);
        }

        GridView imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
        imageAdapter = new ImageAdapter();
        imagegrid.setAdapter(imageAdapter);
        imagecursor.close();


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

                if (momentID == null) {
                    Intent intent = new Intent(this, EditProfilActivity.class);
                    intent.putExtra("photos", selectedPictures);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Intent intent = new Intent(this, MomentInfosActivity.class);
                    intent.putExtra("precedente", "timeline");
                    intent.putExtra("position", 0);
                    intent.putExtra("id", momentID);
                    intent.putExtra("photos", selectedPictures);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        selectedPictures.clear();
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

        public ImageAdapter() {
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


            final AlphaAnimation fadeIn = new AlphaAnimation(0.5f, 1.0f);
            fadeIn.setDuration(325);
            fadeIn.setFillAfter(true);

            final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.5f);
            fadeOut.setDuration(325);
            fadeOut.setFillAfter(true);

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(
                        R.layout.custom_gallery_item, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
                holder.checkbox.setEnabled(false);
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
            Picasso.with(getApplicationContext()).load("file://" + arrPath[holder.imageview.getId()]).resize(sizeImage, sizeImage).centerCrop().into(holder.imageview);

            //holder.imageview.setImageBitmap(thumbnails[position]);
            holder.checkbox.setChecked(thumbnailsselection[position]);
            if( thumbnailsselection[holder.imageview.getId()]==true) holder.imageview.startAnimation(fadeOut);
            else holder.imageview.startAnimation(fadeIn);
            holder.id = position;
            return convertView;
        }
    }

    class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;
        int id;
    }
}
