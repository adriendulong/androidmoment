package com.moment.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.R;
import com.moment.classes.MomentApi;
import com.moment.classes.SearchAdapter;
import com.moment.models.Moment;
import com.moment.models.User;
import com.moment.util.CommonUtilities;
import com.moment.util.ImageFetcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchActivity extends SherlockActivity {

    private EditText searchEditText;
    private ListView resultList;
    private ImageFetcher mImageFetcher;
    private ArrayList<Moment> moments;
    private SearchAdapter adapter;
    private Menu myMenu;
    private SearchView searchView;
    private ProgressBar searchProgress;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setIcon(R.drawable.picto_o);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        moments = new ArrayList<Moment>();
        searchProgress = (ProgressBar) findViewById(R.id.search_progress);
        resultList = (ListView) findViewById(R.id.searchList);
        adapter = new SearchAdapter(this, R.layout.search_activity, moments, mImageFetcher);
        resultList.setAdapter(adapter);

        searchProgress.setVisibility(View.INVISIBLE);

        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Moment moment = moments.get(position);
                Intent intent = new Intent(SearchActivity.this, MomentInfosActivity.class);
                intent.putExtra("id", moment.getId());
                intent.putExtra("precedente", "search");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        myMenu = menu;
        getSupportMenuInflater().inflate(R.menu.activity_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.requestFocusFromTouch();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchProgress.setVisibility(View.VISIBLE);
                query = query.replaceAll("\\s", "");
                MomentApi.get("search/" + query, null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        moments.clear();
                        try {
                            JSONArray publics = result.getJSONArray("public_moments");
                            JSONArray prives = result.getJSONArray("user_moments");

                            for (int i = 0; i < publics.length(); i++) {
                                Moment moment = new Moment();
                                JSONObject momentJSON = publics.getJSONObject(i);
                                moment.setMomentFromJson(momentJSON);
                                moments.add(moment);
                                adapter.notifyDataSetChanged();
                            }

                            for (int i = 0; i < prives.length(); i++) {
                                Moment moment = new Moment();
                                JSONObject momentJSON = prives.getJSONObject(i);
                                moment.setMomentFromJson(momentJSON);
                                moments.add(moment);
                                adapter.notifyDataSetChanged();
                            }

                            Collections.sort(moments, new CustomComparator());

                            searchProgress.setVisibility(View.INVISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    private class CustomComparator implements Comparator<Moment> {
        @Override
        public int compare(Moment lhs, Moment rhs) {
            return lhs.getIsOpenInvit().compareTo(rhs.getIsOpenInvit());
        }
    }

}
