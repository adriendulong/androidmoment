package com.moment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.classes.AppMoment;
import com.moment.classes.Moment;
import com.moment.classes.MomentApi;
import com.moment.classes.animations.TimelineAnimation;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivity;

public class TimelineActivity extends SlidingActivity {
	
	private ListView momentListView;
	private Intent intentMoment;
	private int i=0;
	private LayoutInflater inflater;
	private int actuelMomentSelect = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_timeline);
        
        //Sliding menu
        setBehindContentView(R.layout.volet_timeline);
        
        //Customize Sliding view
     	SlidingMenu sm = getSlidingMenu();
   		sm.setBehindOffset(100);
   		sm.setShadowDrawable(R.drawable.shadow);
   		sm.setShadowWidth(10);
        
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        
        
        //Inflater 
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        

        MomentApi.get("moments", null, new JsonHttpResponseHandler() {
			            @Override
			            public void onSuccess(JSONObject response) {
			            	
			            	System.out.println("OK");
			            	try {
			            		System.out.println(response.toString());
				            	JSONArray momentsArray = response.getJSONArray("moments");
				            	
				            	// Nombre de moment
				            	int nbMoments = momentsArray.length();
				            	Toast.makeText(getApplicationContext(), "Nombre de Moments "+nbMoments, Toast.LENGTH_LONG).show();
				            		
				            	for(int j=0; j<nbMoments; j++){
				            		JSONObject momentJson = (JSONObject)momentsArray.get(j);
									
									Moment momentTemp = new Moment();
									momentTemp.setMomentFromJson(momentJson);
									
									System.out.println(momentTemp.getName());
									
									AppMoment.getInstance().user.addMoment(momentTemp);
									
									ajoutMoment(momentTemp);
									
									
				            	}
										
									
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			            }
			        });
        
        //On rajoute des moments
        /*for (i = 0; i < 10; i++) {
        	 ajoutMoment(i, "Moment "+i);
		}*/
       
        
        
        
       /* momentListView = (ListView)findViewById(R.id.momentsListe);
        String[] listeStrings = {"France","Allemagne","Russie"};
        momentListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listeStrings));
        
        
        //List Click
        momentListView.setOnItemClickListener(new OnItemClickListener() {
        	  @Override
        	  public void onItemClick(AdapterView<?> parent, View view,
        	    int position, long id) {
        	    Toast.makeText(getApplicationContext(),
        	      "Click ListItem Number " + position, Toast.LENGTH_LONG)
        	      .show();
        	    Log.d("MOMENT","MOMMMMMENT");
        	    lancerMomentInfos();
        	  }
        	}); */
      
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getSupportMenuInflater().inflate(R.menu.activity_timeline, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
    	Log.d("Action", ""+item.getItemId());
    	
    	switch (item.getItemId()) {
	    	case android.R.id.home:
	    		toggle();
				return true;
			
	    	case R.id.menu_creer:
	    		Log.d("Creer", "On va creer ! "+getSupportActionBar().getHeight());
	    		//Creer et demarrer l'activité de creation de moment
	    	    Intent intent = new Intent(this, CreationActivity.class);
	    	    startActivity(intent);
	    		return true;
    	}
    	
    	return super.onOptionsItemSelected(item);
    	
    }
    
    
    public void tap(View view){
    	
    	if(view.getId()==actuelMomentSelect){
    		
    		
    		intentMoment = new Intent(this, MomentInfosActivity.class);
    		intentMoment.putExtra("precedente", "timeline");
    		intentMoment.putExtra("position", 1);
    		intentMoment.putExtra("id", actuelMomentSelect);
        	startActivity(intentMoment);
    	}
    	else{
    		if(actuelMomentSelect!=-1) {
    			LinearLayout momentsLayout = (LinearLayout)findViewById(R.id.timeline_moments);
    			View v = momentsLayout.findViewById(actuelMomentSelect);
    			reduireMoment(v);
    		}
    		
    		actuelMomentSelect = view.getId();
    		grandirMoment(view);
    	}
    	
    	//intentMoment = new Intent(this, MomentInfosActivity.class);
    	//startActivity(intentMoment);
    }
    
    
    /**
     * Fonction appelée quand le bouton "Moments" est selectionné
     * @param view
     */
    
    public void moments(View view){
    	Log.d("TESTTTT", "premier bouton volet");
    }
    
    
    /**
     * Fonction appelée quand le bouton profil est selectionné
     * Lance la vue profil
     * @param view
     */
    
    public void profil(View view){
    	
    	Intent intent = new Intent(this, ProfilActivity.class);
	    startActivity(intent);
    	
    }
    
    
    /**
     * Fonction appelée quand le bouton paramètres est selectionnée
     * Lance la vue paramètre
     * @param view
     */
    
	public void parametres(View view){
	    	
	}
	
	
	
	/**
	 * Fonction qui gère l'ajout d'un moment à la timeline
	 * @param id
	 * @param nom
	 */
	
	public void ajoutMoment(Moment moment){
		//On recupere le layout dans lequel on insère les moments
        LinearLayout momentsLayout = (LinearLayout)findViewById(R.id.timeline_moments);
		
        
        // On recupère le template de moment
        RelativeLayout momentLayout = (RelativeLayout) inflater.inflate(R.layout.moment,
                null);
        momentLayout.setId(moment.getId());
    	
    	
    	//On modifie le titre du moment
    	TextView nomMoment = (TextView)momentLayout.findViewById(R.id.nom_moment);
    	nomMoment.setText(moment.getName());

    	//On recupere l'emplacement de l'image
    	
    	if(moment.getUrlCover()!=null){
    		final ImageView imageMoment = (ImageView)momentLayout.findViewById(R.id.image_moment);
    		moment.printCover(imageMoment, true);
    	}
    	
    	//On ajoute le moment à la vue
		momentsLayout.addView(momentLayout);
		
		//On adapte les tailles
		Resources r = getResources();
    	float pxRatio = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
    	momentLayout.getLayoutParams().height = (int)(110*pxRatio);
    	momentLayout.getLayoutParams().width = (int)(110*pxRatio);
    	//moment.setGravity(Gravity.CENTER_HORIZONTAL);
		
		//On lui modifie son espacement par raport aux autres moments
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)momentLayout.getLayoutParams();
		params.setMargins(0, 10, 0, 50);
		
		
	}
	
	
	
	
	public void grandirMoment(View view){
		
		Resources r = getResources();
    	float ratio = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
		
		TimelineAnimation anim = new TimelineAnimation(view, ratio, false);
		anim.setDuration(400);
		//anim.setFillAfter(true);
		anim.setInterpolator(new AccelerateInterpolator());
		view.startAnimation(anim);
		
	}
	
	
	public void reduireMoment(View view){
		
		Resources r = getResources();
    	float ratio = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
		
		TimelineAnimation anim = new TimelineAnimation(view, ratio, true);
		anim.setDuration(400);
		anim.setFillAfter(true);
		anim.setInterpolator(new AccelerateInterpolator());
		view.startAnimation(anim);
		
	}
	
	/**
	 * L'utilisateur a touché le bouton qui permet d'accèder directement aux Photos
	 * @param view
	 */
	
	public void tapDirectPhotos(View view){
		
		intentMoment = new Intent(this, MomentInfosActivity.class);
		intentMoment.putExtra("precedente", "timeline");
		intentMoment.putExtra("position", 0);
		intentMoment.putExtra("id", actuelMomentSelect);
    	startActivity(intentMoment);
		
	}
	
	
	
	/**
	 * L'utilisateur a touché le bouton qui permet d'accèder directement aux Infos
	 * @param view
	 */
	
	public void tapDirectInfos(View view){
		
		intentMoment = new Intent(this, MomentInfosActivity.class);
		intentMoment.putExtra("precedente", "timeline");
		intentMoment.putExtra("position", 1);
		intentMoment.putExtra("id", actuelMomentSelect);
    	startActivity(intentMoment);
		
	}
	
	
	/**
	 * L'utilisateur a touché le bouton qui permet d'accèder directement aux Chat
	 * @param view
	 */
	
	public void tapDirectChat(View view){

		
		intentMoment = new Intent(this, MomentInfosActivity.class);
		intentMoment.putExtra("precedente", "timeline");
		intentMoment.putExtra("position", 2);
		intentMoment.putExtra("id", actuelMomentSelect);
    	startActivity(intentMoment);
		
	}
}
