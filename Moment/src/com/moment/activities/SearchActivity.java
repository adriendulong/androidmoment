package com.moment.activities;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.moment.R;

public class SearchActivity extends SherlockActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        getActionBar().hide();
    }

}
