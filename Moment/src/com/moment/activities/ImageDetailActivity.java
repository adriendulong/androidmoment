package com.moment.activities;

/**
 * Created by adriendulong on 23/08/13.
 */
/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;
import com.moment.AppMoment;
import com.moment.BuildConfig;
import com.moment.R;
import com.moment.fragments.ImageDetailFragment;
import com.moment.models.Photo;
import com.moment.models.User;
import com.moment.util.ImageCache;
import com.moment.util.ImageFetcher;
import com.moment.util.Images;
import com.moment.util.Utils;

public class ImageDetailActivity extends FragmentActivity implements View.OnClickListener {
    private static final String IMAGE_CACHE_DIR = "images";
    public static final String MOMENT_ID = "moment_id";
    public static final String IMAGE_POSITION = "position";

    private ImagePagerAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    private ViewPager mPager;

    private Long mMomentId;
    private int mPositionImage;

    private Button backButton;
    private LinearLayout infosPhoto;
    private TextView nameUser, nbLikes;

    @TargetApi(11)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_pager);

        //Graphic elements
        backButton = (Button)findViewById(R.id.button_back);
        infosPhoto = (LinearLayout)findViewById(R.id.infos_photo);
        nameUser = (TextView)findViewById(R.id.name_user_photos);
        nbLikes = (TextView)findViewById(R.id.nb_likes);

        //Get the moment Id
        mMomentId = getIntent().getLongExtra(MOMENT_ID, -1);

        //Get the image position
        mPositionImage = getIntent().getIntExtra(IMAGE_POSITION, 1);

        // Fetch screen height and width, to use as our max size when loading images as this
        // activity runs full screen
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        // For this sample we'll use half of the longest width to resize our images. As the
        // image scaling ensures the image is larger than this, we should be left with a
        // resolution that is appropriate for both portrait and landscape. For best image quality
        // we shouldn't divide by 2, but this will use more memory and require a larger memory
        // cache.
        final int longest = (height > width ? height : width) / 2;

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, longest);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(false);

        // Set up ViewPager and backing adapter
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), AppMoment.getInstance().user.getMomentById(mMomentId).getPhotos().size());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setPageMargin((int) getResources().getDimension(R.dimen.image_detail_pager_margin));
        mPager.setOffscreenPageLimit(2);

        // Set up activity to go full screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Enable some additional newer visibility and ActionBar features to create a more
        // immersive photo viewing experience
        if (Utils.hasHoneycomb()) {
            final ActionBar actionBar = getActionBar();

            // Hide title text and set home as up
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);

            /*
            // Hide and show the ActionBar as the visibility changes
            mPager.setOnSystemUiVisibilityChangeListener(
                    new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int vis) {
                            if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                                actionBar.hide();
                            } else {
                                actionBar.show();
                            }
                        }
                    });*/

            // Start low profile mode and hide ActionBar
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            actionBar.hide();
        }

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                if(infosPhoto.getVisibility()==View.VISIBLE){
                    Photo actualPhoto = AppMoment.getInstance().user.getMomentById(mMomentId).getPhotos().get(mPager.getCurrentItem());
                    User userPhoto = actualPhoto.getUser();
                    String userInfos = userPhoto.getFirstName()+" "+userPhoto.getLastName();
                    nameUser.setText(userInfos);
                    nbLikes.setText(actualPhoto.getNbLike().toString());
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        // Set the current item based on the extra passed in to this activity
        if (mPositionImage != -1) {
            mPager.setCurrentItem(mPositionImage);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Called by the ViewPager child fragments to load images via the one ImageFetcher
     */
    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }

    /**
     * The main adapter that backs the ViewPager. A subclass of FragmentStatePagerAdapter as there
     * could be a large number of items in the ViewPager and we don't want to retain them all in
     * memory at once but create/destroy them on the fly.
     */
    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        private final int mSize;

        public ImagePagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size;
        }

        @Override
        public int getCount() {
            return mSize;
        }

        @Override
        public Fragment getItem(int position) {
            return ImageDetailFragment.newInstance(AppMoment.getInstance().user.getMomentById(mMomentId).getPhotos().get(position).getUrlOriginal());
        }
    }

    /**
     * Set on the ImageView in the ViewPager children fragments, to enable/disable low profile mode
     * when the ImageView is touched.
     */
    @TargetApi(11)
    @Override
    public void onClick(View v) {
        /*final int vis = mPager.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }*/
        updateVisibility();
    }

    public void updateVisibility(){
        if(backButton.getVisibility()==View.VISIBLE){
            backButton.setVisibility(View.INVISIBLE);
            infosPhoto.setVisibility(View.INVISIBLE);
        }
        else{
            Photo actualPhoto = AppMoment.getInstance().user.getMomentById(mMomentId).getPhotos().get(mPager.getCurrentItem());
            User userPhoto = actualPhoto.getUser();
            String userInfos = userPhoto.getFirstName()+" "+userPhoto.getLastName();
            nameUser.setText(userInfos);
            nbLikes.setText(actualPhoto.getNbLike().toString());


            backButton.setVisibility(View.VISIBLE);
            infosPhoto.setVisibility(View.VISIBLE);
        }
    }

    public void back(View v){
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}
