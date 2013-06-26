package com.moment.activities;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.maps.MapView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.animations.VoletAcceptAnimation;
import com.moment.classes.MomentApi;
import com.moment.fragments.ChatFragment;
import com.moment.fragments.InfosFragment;
import com.moment.fragments.PhotosFragment;
import com.moment.models.Chat;
import com.moment.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MomentInfosActivity extends SherlockFragmentActivity {

	static final int PICK_CAMERA_COVER = 1;
	static final int PICK_CAMERA_PHOTOS = 2;
    static final int NEW_INVIT = 3;
    static final int LIST_INVIT = 4;

	LayoutInflater inflater;
	Boolean stateAcceptVolet = false;
	
	private MyPagerAdapter mPagerAdapter;
	private ViewPager pager;
    private Long momentID;
	private int position = 1;

    private InfosFragment infosFr;

	Menu myMenu;
	private GoogleMap mMap;
	
	ArrayList<Fragment> fragments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        momentID = getIntent().getLongExtra("id", 1);

        super.setContentView(R.layout.activity_moment_infos);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setIcon(R.drawable.logo);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        String precedente = getIntent().getStringExtra("precedente");        

        if (precedente.equals("timeline")) position = getIntent().getIntExtra("position", 1);

        fragments = new ArrayList<Fragment>();

     	Bundle args = new Bundle();
        infosFr = new InfosFragment();
     	fragments.add(Fragment.instantiate(this, PhotosFragment.class.getName()));
     	fragments.add(infosFr);
   		fragments.add(Fragment.instantiate(this, ChatFragment.class.getName(), args));

   		this.mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
   		pager = (ViewPager) super.findViewById(R.id.viewpager);
   		pager.setAdapter(this.mPagerAdapter);

   		pager.setCurrentItem(position, false);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        this.pager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				System.out.println("Page SELECTIONNE :" + arg0);
                updateMenuItem(arg0);
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

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
		myMenu = menu;
		getSupportMenuInflater().inflate(R.menu.activity_moment_infos, menu);
        updateMenuItem(position);
		return true;
	}

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

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

    public void listInvit(View view){
        callInvit(LIST_INVIT);
    }

    public void postMessage(View view){
    	final EditText postMessage = (EditText)findViewById(R.id.edit_chat_post_message);
    	final String message = postMessage.getText().toString();
    	RequestParams params = new RequestParams();
    	params.put("message", message);
    	
        MomentApi.post("newchat/"+momentID, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {

                Chat chat = new Chat();
                try {
                    chat.chatFromJSON(response.getJSONObject("chat"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                chat.__setDaoSession(AppMoment.getInstance().daoSession);
                chat.setMoment(AppMoment.getInstance().user.getMomentById(momentID));
                chat.setUser(AppMoment.getInstance().user);
                AppMoment.getInstance().user.getMomentById(momentID).addChat(chat);
			    AppMoment.getInstance().chatDao.insert(chat);

		    	messageRight(chat);

		    	postMessage.setText("");
            }
            
            public void onFailure(Throwable error, String content) {
                System.out.println("FAILURE : "+content);
            }
        });
    }

    public void messageRight(Chat chat){
        LinearLayout layoutChat = (LinearLayout)findViewById(R.id.chat_message_layout);
        PullToRefreshScrollView scrollChat = (PullToRefreshScrollView)findViewById(R.id.scroll_chat);
        LinearLayout chatDroit = (LinearLayout) inflater.inflate(R.layout.chat_message_droite, null);
        TextView message = (TextView)chatDroit.findViewById(R.id.chat_message_text);
        message.setText(chat.getMessage());

        ImageView userImage = (ImageView)chatDroit.findViewById(R.id.photo_user);
        User usertemp = chat.getUser();
        usertemp.printProfilePicture(userImage, true);

        layoutChat.addView(chatDroit);

        /*new Handler().postDelayed((new Runnable(){

        	@Override
			public void run(){
        		ScrollView scrollChat = (ScrollView)findViewById(R.id.scroll_chat);
        		scrollChat.fullScroll(View.FOCUS_DOWN);
        	}

        }), 200);*/
    }

    public void messageLeft(Chat chat){

    	LinearLayout layoutChat = (LinearLayout)findViewById(R.id.chat_message_layout);
        PullToRefreshScrollView scrollChat = (PullToRefreshScrollView)findViewById(R.id.scroll_chat);

        LinearLayout chatDroit = (LinearLayout) inflater.inflate(R.layout.chat_message_gauche,null);
        TextView message = (TextView)chatDroit.findViewById(R.id.chat_message_text);
        message.setText(chat.getMessage());

        ImageView userImage = (ImageView)chatDroit.findViewById(R.id.photo_user);
        chat.getUser().printProfilePicture(userImage, true);
        layoutChat.addView(chatDroit);

/*        new Handler().postDelayed((new Runnable(){

        	@Override
			public void run(){
        		ScrollView scrollChat = (ScrollView)findViewById(R.id.scroll_chat);
        		scrollChat.fullScroll(View.FOCUS_DOWN);
        	}

        }), 200);*/
        
    	
    }

    public void editMessage(View view){

        //On scroll vers la bas
        /*new Handler().postDelayed((new Runnable(){

            @Override
            public void run(){
                ScrollView scrollChat = (ScrollView)findViewById(R.id.scroll_chat);
                scrollChat.fullScroll(View.FOCUS_DOWN);
            }

        }), 200);*/

    }
    
    
    
    public void acceptBandeauAnim(View view){

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
    		anim.setInterpolator(new DecelerateInterpolator());
    		voletLayout.startAnimation(anim);
    		
    		stateAcceptVolet = false;
    		
    	}
    		
    }
    
    
    public static class Exchanger {
    	public static MapView mMapView;
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
            intent.putExtra("id", momentID);

           // if (android.os.Build.VERSION.SDK_INT >= 16){
                startActivityForResult(intent, request_code);
                overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left );
            //} else{
              //  startActivityForResult(intent, request_code);
            //}


        }
        else if (request_code==LIST_INVIT){
            intent = new Intent(MomentInfosActivity.this, ListGuestsActivity.class);
            intent.putExtra("id", momentID);

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



    /**
     * Function called when the Not Going button is touched in the RSVP bloc
     * @param view
     */

    public void notRsvp(View view){
        infosFr.notRsvp();
    }

    /**
     * Function called when the Maybe button is touched in the RSVP bloc
     * @param view
     */

    public void maybeRsvp(View view){
        infosFr.maybeRsvp();
    }

    /**
     * Function called when the Going button is touched in the RSVP bloc
     * @param view
     */

    public void goingRsvp(View view){
        infosFr.goingRsvp();
    }
    

}



