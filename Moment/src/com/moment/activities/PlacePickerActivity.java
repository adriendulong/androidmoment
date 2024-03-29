package com.moment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.R;
import com.moment.classes.PlacesAdapter;
import com.moment.models.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;


public class PlacePickerActivity extends SherlockActivity {

    private ListView placesListView;
    private String GOOGLE_PLACE_API_KEY = "AIzaSyBOpJuAT7dEsXCxPbd_6m89wJPUbEIEM80";
    private ArrayList<Place> places = new ArrayList<Place>();
    private PlacesAdapter adapter;
    private int previous_count = -1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_picker_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setIcon(R.drawable.logo);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);


        EditText search = (EditText) findViewById(R.id.places_search);
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                System.out.println("COUNT :" + s.length());


                if (s.length() != previous_count) {
                    if (s.length() > 2) {
                        places.clear();


                        Place customPlace = new Place(s.toString());
                        places.add(customPlace);

                        System.out.println(Locale.getDefault().getLanguage());
                        getPlaces(s.toString(), "fr");
                    } else {
                        places.clear();

                        Place customPlace = new Place(s.toString());
                        places.add(customPlace);

                        adapter.notifyDataSetChanged();
                    }

                }

                previous_count = s.length();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        adapter = new PlacesAdapter(this, places, R.layout.places_cell);


        placesListView = (ListView) findViewById(R.id.list_places);
        placesListView.setAdapter(adapter);


        placesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("Test :" + i);
                Toast.makeText(PlacePickerActivity.this, "Position : " + i, Toast.LENGTH_SHORT);


                String place_label;
                TextView mainInfo = (TextView) findViewById(R.id.place_main_info);


                if (i == 0) place_label = places.get(0).getPlaceOne();
                else {
                    if (places.get(i).getPlaceThree() != null)
                        place_label = places.get(i).getPlaceOne() + ", " + places.get(i).getPlaceTwo() + ", " + places.get(i).getPlaceThree();
                    else
                        place_label = places.get(i).getPlaceOne() + ", " + places.get(i).getPlaceTwo();
                }

                Intent intent = getIntent();
                intent.putExtra("place_label", place_label);
                setResult(RESULT_OK, intent);
                finish();

            }
        });

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


    public void getPlaces(String place, String lg) {


        String placeQuery = null;
        try {
            placeQuery = URLEncoder.encode(place, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + placeQuery + "&types=geocode&language=" + lg + "&sensor=true&key=" + GOOGLE_PLACE_API_KEY, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                try {

                    JSONArray predictions = response.getJSONArray("predictions");


                    for (int i = 0; i < predictions.length(); i++) {
                        JSONObject place = predictions.getJSONObject(i);
                        JSONArray terms = place.getJSONArray("terms");


                        Place newPlace = new Place();

                        for (int j = 0; j < terms.length(); j++) {
                            JSONObject detailPlace = terms.getJSONObject(j);

                            if (j == 0) newPlace.setPlaceOne(detailPlace.getString("value"));
                            else if (j == 1) newPlace.setPlaceTwo(detailPlace.getString("value"));
                            else if (j == 2) newPlace.setPlaceThree(detailPlace.getString("value"));
                        }
                        places.add(newPlace);
                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }

            public void onFailure(Throwable error, String content) {

                System.out.println("COUCOU :" + error + " " + content);
            }

        });

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


}