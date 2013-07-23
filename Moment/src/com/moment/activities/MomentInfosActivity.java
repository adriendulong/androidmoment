package com.moment.activities;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.moment.classes.MomentApi;
import com.moment.fragments.ChatFragment;
import com.moment.fragments.InfosFragment;
import com.moment.fragments.PhotosFragment;
import com.moment.models.Chat;
import com.moment.models.Moment;
import com.moment.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MomentInfosActivity extends SherlockFragmentActivity {

	static final int PICK_CAMERA_COVER = 1;
	static final int PICK_CAMERA_PHOTOS = 2;
    static final int NEW_INVIT = 3;
    static final int LIST_INVIT = 4;

    //PUSH TYPES
    private int CHAT_PUSH = 3;
    private int PHOTO_PUSH = 2;

    //From push
    private int type_id, moment_id;

	LayoutInflater inflater;
	Boolean stateAcceptVolet = false;
	
	private MyPagerAdapter mPagerAdapter;
	private ViewPager pager;
    private Long momentID;
	private int position = 1;

    private InfosFragment infosFr;
    private ChatFragment chatFr;
    private PhotosFragment photosFr;

	Menu myMenu;
	private GoogleMap mMap;
	
	ArrayList<Fragment> fragments;

    //The Moment
    private Moment moment;

    //Progress Dialog when loading Moment infos
    private ProgressDialog  mProgressDialog;

    //Debug Tag
    private String TAG = "InfosActivity";

    //Receiver
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        super.setContentView(R.layout.activity_moment_infos);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setIcon(R.drawable.logo);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        String precedente = getIntent().getStringExtra("precedente");        


        if (precedente.equals("timeline")) position = getIntent().getIntExtra("position", 1);
        if(precedente.equals("push")||precedente.equals("notifs")){
            Log.d("MOMENTINFOS", "TYPE INIT : "+getIntent().getIntExtra("type_id", -1));
            type_id = getIntent().getIntExtra("type_id", -1);
            moment_id = getIntent().getIntExtra("moment_id", -1);
            momentID = getIntent().getLongExtra("moment_id", 1);

            if (type_id == PHOTO_PUSH) position = 0;
            else if(type_id == CHAT_PUSH) position = 2;
            else position=1;
        }
        else momentID = getIntent().getLongExtra("id", 1);

        Log.d("MOMENTINFOS", "Position : "+position);
        Log.d("MOMENTINFOS", "TYPE : "+type_id);

        fragments = new ArrayList<Fragment>();

     	Bundle args = new Bundle();
        infosFr = new InfosFragment();
        photosFr = new PhotosFragment();
        chatFr = new ChatFragment();
     	fragments.add(photosFr);
     	fragments.add(infosFr);
   		fragments.add(chatFr);

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

        //If we come from the moment creation we go to the invits
        if (precedente.equals("creation")) callInvit(NEW_INVIT);

        //We get moment informations
        getMoment();
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

    /**
     * Function called when user click on the guests block
     * @param view
     */

    public void listInvit(View view){
        callInvit(LIST_INVIT);
    }

    /**
     * Function called when user click on plus next to the guests block
     * @param view
     */

    public void addGuests(View view){
        callInvit(NEW_INVIT);
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

            if (android.os.Build.VERSION.SDK_INT >= 16){
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
                startActivityForResult(intent, request_code, bndlanimation);
            } else{
                startActivityForResult(intent, request_code);
            }


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


    /**
     * This functions goal is to get the Moment informations
     * <p>
     *     It will first try on the local database, then to download it from the server
     * </p>
     * @param momentID
     */

    public void getMoment(){

        //If we have it in the local database
        Log.d("MOMENTINFOS", "Moment id :"+momentID);
        AppMoment.getInstance().getUser();
        if(AppMoment.getInstance().user.getMomentById(momentID)!=null){
            moment = AppMoment.getInstance().user.getMomentById(momentID);
        }
        //Otherwise we try to get it from the server
        else {
            mProgressDialog = ProgressDialog.show(this, "Chargement", "Chargement des informations du moment");
            MomentApi.get("moment/"+momentID, null, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(JSONObject response) {
                    Toast.makeText(getApplicationContext(), "Moment infos downloaded", Toast.LENGTH_SHORT).show();
                    Moment tempMoment = new Moment();
                    try{
                        tempMoment.setMomentFromJson(response);
                        Log.v(TAG, tempMoment.getName());
                        moment = tempMoment;

                        //We add it in the local database
                        AppMoment.getInstance().user.addMoment(tempMoment);

                        //Chat fragment
                        if(pager.getCurrentItem()==2){
                            ((ChatFragment)mPagerAdapter.getItem(2)).createFragment(momentID);
                            ((InfosFragment)mPagerAdapter.getItem(1)).createFragment(momentID);
                        }
                        else if(pager.getCurrentItem()==1){
                            ((ChatFragment)mPagerAdapter.getItem(2)).createFragment(momentID);
                            ((InfosFragment)mPagerAdapter.getItem(1)).createFragment(momentID);
                            ((PhotosFragment)mPagerAdapter.getItem(0)).createFragment(momentID);
                        }
                        else if(pager.getCurrentItem()==0){
                            ((PhotosFragment)mPagerAdapter.getItem(0)).createFragment(momentID);
                            ((InfosFragment)mPagerAdapter.getItem(1)).createFragment(momentID);
                        }

                        mProgressDialog.dismiss();
                    }catch(JSONException e){
                        Log.e(TAG, "JSON problems");
                    }

                }

                @Override
                public void onFailure(Throwable error, String content) {
                    Toast.makeText(getApplicationContext(), "Problem when getting moment infos", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            });
        }
    }

    public Long getMomentId(){
        if(this.moment!=null) return this.momentID;
        else return null;

    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(
                "com.google.android.c2dm.intent.RECEIVE");
        mReceiver = new BroadcastReceiver() {
            private int type_notifs, chat_id, photo_id;
            private Long moment_id;

            @Override
            public void onReceive(Context context, Intent intent) {
                try{
                    JSONArray array = new JSONArray(intent.getExtras().getString("data"));
                    JSONObject jsonMessage = array.getJSONObject(0);
                    moment_id = jsonMessage.getLong("moment_id");
                    type_notifs = jsonMessage.getInt("type_notif");
                    Log.v("PUSSH", "LAAAA, moment_id :"+moment_id+"  type_notifs : "+type_notifs+" Notre moment id "+momentID);

                    if(type_notifs==CHAT_PUSH){
                        if(moment_id.equals(momentID)){
                            Log.v("PUSSH", "Ceci est un chat");
                            chat_id = jsonMessage.getInt("chat_id");
                            //If we are on the chat, we print it
                            if(pager.getCurrentItem()==2||pager.getCurrentItem()==1){
                                Log.v("PUSSH", "On est dans le chat");
                                MomentApi.get("chat/"+chat_id, null, new JsonHttpResponseHandler() {

                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        Toast.makeText(getApplicationContext(), "Nouveau message", Toast.LENGTH_SHORT).show();
                                        Chat tempChat = new Chat();
                                        try{
                                            tempChat.chatFromJSON(response.getJSONObject("chat"));
                                        }catch (JSONException e){
                                            Log.e("JSON", "Probleme JSON");
                                        }


                                        Log.v("PUSSH", response.toString());

                                        //We add it
                                        tempChat.__setDaoSession(AppMoment.getInstance().daoSession);
                                        tempChat.setMoment(AppMoment.getInstance().user.getMomentById(momentID));
                                        tempChat.setUser(tempChat.getUser());
                                        AppMoment.getInstance().user.getMomentById(momentID).addChat(tempChat);
                                        AppMoment.getInstance().chatDao.insert(tempChat);

                                        messageLeft(tempChat);

                                        if(pager.getCurrentItem()==1) pager.setCurrentItem(2);

                                    }

                                    @Override
                                    public void onFailure(Throwable error, String content) {
                                        Toast.makeText(getApplicationContext(), "Problème chat", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }
                        else{
                            AlertDialog.Builder monDialogue = new AlertDialog.Builder(MomentInfosActivity.this);
                            monDialogue.setTitle("Nouveau chat");
                            monDialogue.setMessage("Test");

                            //Bouton du dialogue
                            monDialogue.setPositiveButton("Consulter", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intentMoment = new Intent(MomentInfosActivity.this, MomentInfosActivity.class);
                                    intentMoment.putExtra("precedente", "notifs");
                                    intentMoment.putExtra("type_id", type_notifs);
                                    intentMoment.putExtra("moment_id", moment_id);
                                    startActivity(intentMoment);

                                }
                            });
                            monDialogue.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            monDialogue.show();
                        }
                    }
                    else if(type_notifs==PHOTO_PUSH){
                        Log.v("PUSSH", "Ceci est un chat");
                        photo_id = jsonMessage.getInt("photo_id");
                        //If we are on the chat, we print it
                        if(pager.getCurrentItem()==0||pager.getCurrentItem()==1){
                            Log.v("PUSSH", "On est dans le chat");
                            Toast.makeText(getApplicationContext(), "Nouvelle Photo", Toast.LENGTH_SHORT).show();
                        }

                    }


                }catch(JSONException e){
                    Log.e(TAG, "JSON Problem");
                }
            }
        };
        //registering our receiver
        this.registerReceiver(mReceiver, intentFilter);
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //unregister our receiver
        this.unregisterReceiver(this.mReceiver);
    }


    /**
     * When we click on the pen to modify the event
     * @param view
     */

    public void modifyMoment(View view){
        Intent modifyIntent = new Intent(this, CreationDetailsActivity.class);
        modifyIntent.putExtra("nomMoment", moment.getName());
        modifyIntent.putExtra("moment_id", moment.getId());
        startActivity(modifyIntent);
    }



    public void goPhotos(View view){
        pager.setCurrentItem(0);
    }

    

}



