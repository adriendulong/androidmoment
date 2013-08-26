package com.moment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.R;
import com.moment.classes.MomentApi;
import com.moment.classes.SearchAdapter;
import com.moment.models.Moment;
import com.moment.util.ImageFetcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends SherlockActivity {

    private EditText searchEditText;
    private ListView resultList;
    private ImageFetcher mImageFetcher;
    private ArrayList<Moment> moments;
    private SearchAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        getSupportActionBar().hide();

        moments = new ArrayList<Moment>();
        searchEditText = (EditText) findViewById(R.id.volet_search);
        resultList = (ListView) findViewById(R.id.searchList);
        adapter = new SearchAdapter(this, R.layout.search_activity, moments, mImageFetcher);
        resultList.setAdapter(adapter);

        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Moment moment = moments.get(position);
                Intent intent = new Intent(SearchActivity.this, MomentInfosActivity.class);
                intent.putExtra("id", moment.getId());
                intent.putExtra("precedente", "search");
                startActivity(intent);
            }
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                MomentApi.get("search/" + searchEditText.getText().toString(), null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject result){
                        moments.clear();
                        try {
                            JSONArray publics = result.getJSONArray("public_moments");
                            JSONArray prives = result.getJSONArray("user_moments");

                            for(int i = 0; i < publics.length(); i++)
                            {
                                Moment moment = new Moment();
                                JSONObject momentJSON = publics.getJSONObject(i);
                                moment.setMomentFromJson(momentJSON);
                                moments.add(moment);
                                adapter.notifyDataSetChanged();
                            }

                            for(int i = 0; i < prives.length(); i++)
                            {
                                Moment moment = new Moment();
                                JSONObject momentJSON = prives.getJSONObject(i);
                                moment.setMomentFromJson(momentJSON);
                                moments.add(moment);
                                adapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return false;
            }
        });

    }
}
