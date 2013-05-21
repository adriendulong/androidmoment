package com.moment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.moment.classes.FbEvent;

public class FacebookEventsActivity extends SherlockActivity {
	
	private ListView eventsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_events);
        
        Bundle extras = getIntent().getExtras();
        ArrayList<FbEvent> events = extras.getParcelableArrayList("events");
        
        //On customise l'Action bar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        
        //On affiche la liste des events
        eventsList = (ListView)findViewById(R.id.eventsListe);
        //ArrayAdapter<FbEvent> adapter = new ArrayAdapter<FbEvent>(this, android.R.layout.simple_list_item_1, events);
        eventsList.setAdapter(new ArrayAdapter<FbEvent>(this, android.R.layout.simple_list_item_1, events));
        
        
        //List Click
        eventsList.setOnItemClickListener(new OnItemClickListener() {
        	  @Override
        	  public void onItemClick(AdapterView<?> parent, View view,
        	    int position, long id) {
        	    Toast.makeText(getApplicationContext(),
        	      "Click ListItem Number " + position, Toast.LENGTH_LONG)
        	      .show();
        	    Log.d("MOMENT","MOMMMMMENT");
        	    //lancerMomentInfos();
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

}
