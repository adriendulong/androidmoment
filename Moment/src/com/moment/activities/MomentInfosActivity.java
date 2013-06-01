package com.moment.activities;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.maps.MapView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.*;
import com.moment.animations.VoletAcceptAnimation;
import com.moment.fragments.ChatFragment;
import com.moment.fragments.InfosFragment;
import com.moment.fragments.PhotosFragment;
import com.moment.models.Chat;
import com.moment.models.Moment;
import com.moment.models.Photo;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class MomentInfosActivity extends SherlockFragmentActivity {

    //Request codes
	static final int PICK_CAMERA_COVER = 1;
	static final int PICK_CAMERA_PHOTOS = 2;
    static final int NEW_INVIT = 3;
    static final int LIST_INVIT = 4;

	LayoutInflater inflater;
	Boolean stateAcceptVolet = false;
	
	private MyPagerAdapter mPagerAdapter;
	private ViewPager pager;
	private Moment moment;
    private int momentID;
	private int position = 1;

	Menu myMenu;
	private GoogleMap mMap;
	
	ArrayList<Fragment> fragments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        momentID = getIntent().getIntExtra("id", 1);

        super.setContentView(R.layout.activity_moment_infos);
        // setup action bar for tabs
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setIcon(R.drawable.logo);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        //On regarde de ou l'on vient
        String precedente = getIntent().getStringExtra("precedente");        
        //if(precedente.equals("creation")) 
        
        
        //Si on vient de la timeline on recupere la position � laquelle on doit se mettre
        if (precedente.equals("timeline")) position = getIntent().getIntExtra("position", 1);

        //We get the moment id


        //We get the moment thans to its id
        Exchanger.moment = AppMoment.getInstance().user.getMoment(momentID);
        Exchanger.idMoment = momentID;
        

        
        
        if (moment==null) Log.d("NULL", "NULL");
        else{
        	Log.d("Description", ""+Exchanger.moment.getDescription());
        	Log.d("Titre", ""+Exchanger.moment.getName());
        	Log.d("Date Debut", ""+Exchanger.moment.getDateDebut().toString());	
        }

        // Cr�ation de la liste de Fragments que fera d�filer le PagerAdapter
        fragments = new ArrayList<Fragment>();

     	Bundle args = new Bundle();
        args.putParcelable("moment", moment);
     	// Ajout des Fragments dans la liste
     	fragments.add(Fragment.instantiate(this, PhotosFragment.class.getName()));
     	fragments.add(Fragment.instantiate(this,InfosFragment.class.getName(), args));
   		fragments.add(Fragment.instantiate(this,ChatFragment.class.getName(), args));

   		// Cr�ation de l'adapter qui s'occupera de l'affichage de la liste de
   		// Fragments
   		this.mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
   		pager = (ViewPager) super.findViewById(R.id.viewpager);
   		pager.setAdapter(this.mPagerAdapter);

   		pager.setCurrentItem(position, false);
        
   		
        //Inflater 
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        //Exchanger.mMapView = new MapView(this, "0SvfMFBiO0svOHR50Ed3noqFcTqNJVgTlgYR1vQ");
        
        
        this.pager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				System.out.println("Page SELECTIONNE :" + arg0);
				//Contacts
                updateMenuItem(arg0);
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});


        //If we come from the creation, we directly launch the invit screen
        if (precedente.equals("creation")) callInvit(NEW_INVIT);


    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
    }
    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		myMenu = menu;
		getSupportMenuInflater().inflate(R.menu.activity_moment_infos, menu);
        updateMenuItem(position);
		return true;
	}

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e("MomentInfo","Destroy");
    }
    
    
/**
 * FragmentPagerAdapter
 * @author adriendulong
 *
 */
    
 public class MyPagerAdapter extends FragmentStatePagerAdapter {


    	protected final String[] CONTENT = new String[] {"PHOTOS","INFOS","CHAT"};

    	public MyPagerAdapter(FragmentManager fm) {
    		super(fm);
    	}

    	@Override
    	public Fragment getItem(int position) {
    		return fragments.get(position);
    	}

    	@Override
    	public int getCount() {
    		return fragments.size();
    	}
    	
    	@Override
        public CharSequence getPageTitle(int position) {
          return CONTENT[position % CONTENT.length];
        }
    	
    	
    	
 }
 

 @Override
 public boolean onOptionsItemSelected(MenuItem item) {
     switch (item.getItemId()) {
         case android.R.id.home:
             NavUtils.navigateUpFromSameTask(this);
             return true;
         case R.id.tab_infos:
         	pager.setCurrentItem(1);
         	break;
         
         case R.id.tab_photo:
        	pager.setCurrentItem(0);
         	break;
         
         case R.id.tab_chat:
        	pager.setCurrentItem(2);
         	break;
         	
            
     }
     return super.onOptionsItemSelected(item);
 }


    ////////////////////
    ///// LISTENERS DU FRAGMENT INFOS
    ///////////////////////////


    public void listInvit(View view){
        callInvit(LIST_INVIT);
    }


    ////////////////////
    ///// LISTENERS DU FRAGMENT CHAT
    ///////////////////////////

    /**
     * Recupere l'evenement lorsque l'on clique sur "envoyer" dans le chat
     * @param view
     */
    
    
    public void postMessage(View view){
    
    	//On recupere le message
    	final EditText postMessage = (EditText)findViewById(R.id.edit_chat_post_message);
    	Log.d("Message", postMessage.getText().toString());
    	final String message = postMessage.getText().toString();
    	
    	
    	
    	RequestParams params = new RequestParams();
    	params.put("message", message);
    	
        MomentApi.post("newchat/"+Exchanger.moment.getId(), params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
				//On construit un nouveau Chat
			    Chat chat = new Chat(message, AppMoment.getInstance().user, new Date());
			    	
		    	Exchanger.moment.addChat(chat);
			        
		    	messageRight(chat);
			    	
		    	//On efface le contenu de l'edit text
		    	postMessage.setText("");
            }
            
            public void onFailure(Throwable error, String content) {
                // By default, call the deprecated onFailure(Throwable) for compatibility
                System.out.println("FAILURE : "+content);
            }
        });
    	
    	
    	
    	
    }
    
    
    /**
     * Poste le message de droite (utilisateur de l'application)
     */
    
    public void messageRight(Chat chat){
    	
    	//On recupere le Linear Layout dans lequel on va ins�rer notre message
    	LinearLayout layoutChat = (LinearLayout)findViewById(R.id.chat_message_layout);
    	ScrollView scrollChat = (ScrollView)findViewById(R.id.scroll_chat);
    	
    	//On recupere le template
        LinearLayout chatDroit = (LinearLayout) inflater.inflate(R.layout.chat_message_droite,
                null);
        
        //On insere le texte dans le template
        TextView message = (TextView)chatDroit.findViewById(R.id.chat_message_text);
        message.setText(chat.getMessage());
        
        //On insere la photo du user
        ImageView userImage = (ImageView)chatDroit.findViewById(R.id.photo_user);
        chat.getUser().printProfilePicture(userImage, true);
        
        
        //On vient ins�rer le message de droite au fil de conversation
        layoutChat.addView(chatDroit);
        //scrollChat.scrollTo(0, (scrollChat.getMaxScrollAmount()+chatDroit.getHeight()));
        
        //On scroll vers la bas 
        new Handler().postDelayed((new Runnable(){

        	@Override
			public void run(){
        		ScrollView scrollChat = (ScrollView)findViewById(R.id.scroll_chat);
        		scrollChat.fullScroll(View.FOCUS_DOWN);
        	}

        }), 200);
        
        
        
    	
    }
    
    
    /**
     * Poste le message de gauche 
     */
    
    public void messageLeft(Chat chat){
    	
    	//On recupere le Linear Layout dans lequel on va ins�rer notre message
    	LinearLayout layoutChat = (LinearLayout)findViewById(R.id.chat_message_layout);
    	ScrollView scrollChat = (ScrollView)findViewById(R.id.scroll_chat);
    	
    	//On recupere le template
        LinearLayout chatDroit = (LinearLayout) inflater.inflate(R.layout.chat_message_gauche,
                null);
        
        //On insere le texte dans le template
        TextView message = (TextView)chatDroit.findViewById(R.id.chat_message_text);
        message.setText(chat.getMessage());
        
        //On insere la photo du user
        ImageView userImage = (ImageView)chatDroit.findViewById(R.id.photo_user);
        chat.getUser().printProfilePicture(userImage, true);
        
        //On vient ins�rer le message de droite au fil de conversation
        layoutChat.addView(chatDroit);
        //scrollChat.scrollTo(0, (scrollChat.getMaxScrollAmount()+chatDroit.getHeight()));
        
        //On scroll vers la bas 
        new Handler().postDelayed((new Runnable(){

        	@Override
			public void run(){
        		ScrollView scrollChat = (ScrollView)findViewById(R.id.scroll_chat);
        		scrollChat.fullScroll(View.FOCUS_DOWN);
        	}

        }), 200);
        
    	
    }
    
    /**
     * Lorsque le focus arrive sur l'edit text pour poster un message on scroll down 
     * la liste pour pas qu'elle soit cach�e par le clavier.
     * @param view
     */
    
    public void editMessage(View view){
    	
    	//On scroll vers la bas 
        new Handler().postDelayed((new Runnable(){

        	@Override
			public void run(){
        		ScrollView scrollChat = (ScrollView)findViewById(R.id.scroll_chat);
        		scrollChat.fullScroll(View.FOCUS_DOWN);
        	}

        }), 200);
    	
    }
    
    
    
    public void acceptBandeauAnim(View view){
    	
    	//Log.d("test", ""+voletAccept.getScaleX());
    	
    	
    	
    	RelativeLayout voletLayout = (RelativeLayout)findViewById(R.id.volet_layout);
    	Resources r = getResources();
    	float ratio = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
    	
    	
    	
    	
    	
    	if(!stateAcceptVolet){
    		
    		VoletAcceptAnimation anim = new VoletAcceptAnimation(voletLayout, 125, true, ratio);
    		anim.setDuration(400);
    		anim.setInterpolator(new DecelerateInterpolator());
    		voletLayout.startAnimation(anim);
    		
    		stateAcceptVolet = true;
    		
    		
    	}
    	else{

    		VoletAcceptAnimation anim = new VoletAcceptAnimation(voletLayout, 125, false, ratio);
    		anim.setDuration(400);
    		//anim.setFillAfter(true);
    		anim.setInterpolator(new DecelerateInterpolator());
    		voletLayout.startAnimation(anim);
    		
    		stateAcceptVolet = false;
    		
    	}
    		
    }
    
    
    public static class Exchanger {
    	// We will use this MapView always.
    	public static MapView mMapView;
    	public static Moment moment;
    	public static ArrayList<Photo> photos = new ArrayList<Photo>();
    	public static int idMoment;
    }  
    
    
    
    /**
	   * L'utilisateur clique sur la photo du moment afin d'en prendre un autre, on appelle alors la fonction concernee
	   * @param view
	   */
	  
	  public void changePhoto(View view){
	    	InfosFragment infosFragment = (InfosFragment)fragments.get(1);
            infosFragment.touchedPhoto();
	    }


    /**
     * Fonciton which modify the menu item in the top bar depending on the position
     * @param position
     */

    public void updateMenuItem(int position){

        if(position == 1){
            System.out.println("INFOS");
            myMenu.findItem(R.id.tab_photo).setIcon(R.drawable.picto_photo_up);
            myMenu.findItem(R.id.tab_infos).setIcon(R.drawable.picto_info_down);
            myMenu.findItem(R.id.tab_chat).setIcon(R.drawable.picto_chat_up);
        }
        //Facebook
        else if (position == 0){
            System.out.println("PHOTOS");
            myMenu.findItem(R.id.tab_photo).setIcon(R.drawable.picto_photo_down);
            myMenu.findItem(R.id.tab_infos).setIcon(R.drawable.picto_info_up);
            myMenu.findItem(R.id.tab_chat).setIcon(R.drawable.picto_chat_up);


        }
        //Favoris
        else{
            System.out.println("CHATS");
            myMenu.findItem(R.id.tab_photo).setIcon(R.drawable.picto_photo_up);
            myMenu.findItem(R.id.tab_infos).setIcon(R.drawable.picto_info_up);
            myMenu.findItem(R.id.tab_chat).setIcon(R.drawable.picto_chat_down);
        }

    }

    /**
     * This function start the right activity depending on the request code
     * @param request_code
     */


    public void callInvit(int request_code){
        Intent intent;

        if(request_code==NEW_INVIT){
            intent = new Intent(MomentInfosActivity.this, InvitationActivity.class);
            intent.putExtra("id", Exchanger.moment.getId());

           // if (android.os.Build.VERSION.SDK_INT >= 16){
                startActivityForResult(intent, request_code);
                overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left );
            //} else{
              //  startActivityForResult(intent, request_code);
            //}


        }
        else if (request_code==LIST_INVIT){
            intent = new Intent(MomentInfosActivity.this, ListGuestsActivity.class);
            intent.putExtra("id", Exchanger.moment.getId());

            if (android.os.Build.VERSION.SDK_INT >= 16){
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
                startActivityForResult(intent, request_code, bndlanimation);
            } else{
                startActivityForResult(intent, request_code);
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==NEW_INVIT){
            Log.d("FIN ACTIVITY", "NEW INVIT");
        }
        else if(requestCode==LIST_INVIT){
            Log.d("FIN ACTIVITY", "LIST");
        }
    }
   
    
    

}



