package com.moment.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.moment.R;
import com.moment.classes.MomentApi;
import com.moment.fragments.InvitationsFragment;
import com.moment.models.User;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class InvitationActivity extends SherlockFragmentActivity {
	
	private InvitationCollectionPagerAdapter mInvitationCollectionPagerAdapter;
    private ViewPager mViewPager;
    Menu myMenu;
    private InvitationsFragment frFb;
    public static ArrayList<User> invitesUser;
    public static TextView nb_invites;
    private long idMoment;
    private EditText searchGuests;
    ArrayList<InvitationsFragment> frs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invitation);
		
		ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setIcon(R.drawable.logo);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        //On initialise la liste des user invit�s
        invitesUser = new ArrayList<User>();
        nb_invites = (TextView)findViewById(R.id.invites_selected);
        
        //ON recupere l'id du moment que l'on vient de cr�er
        idMoment = getIntent().getLongExtra("id", -1);


        //Initi les fragments
        frs = new ArrayList<InvitationsFragment>();
        
        for(int i=0; i<3; i++){
        	InvitationsFragment fragment = new InvitationsFragment();
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            args.putInt(InvitationsFragment.POSITION, i);
            fragment.setArguments(args);
            frs.add(fragment);
        }
        
        
        
        
        // Le page adapter qui va gerer le passage d'une page � l'autre
        mInvitationCollectionPagerAdapter =
                new InvitationCollectionPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mInvitationCollectionPagerAdapter);
        mViewPager.setCurrentItem(1);
        
        
        /**
         * On ecoute quand la page change pour changer icone en haut top bar
         */
        
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				System.out.println("Page SELECTIONNE :" + arg0);
				//Contacts
				if(arg0 == 1){
					System.out.println("CONTACTSS");
	            	myMenu.findItem(R.id.invitation_facebook).setIcon(R.drawable.picto_fbup);
	            	myMenu.findItem(R.id.invitation_contacts).setIcon(R.drawable.picto_phonedown);
	            	myMenu.findItem(R.id.invitation_favoris).setIcon(R.drawable.picto_starup);
				}
				//Facebook
				else if (arg0 == 0){
					System.out.println("FBBBBBBBBB");
	            	myMenu.findItem(R.id.invitation_facebook).setIcon(R.drawable.picto_fbdown);
	            	myMenu.findItem(R.id.invitation_contacts).setIcon(R.drawable.picto_phoneup);
	            	myMenu.findItem(R.id.invitation_favoris).setIcon(R.drawable.picto_starup);

                    if(frFb==null){
                        frFb = mInvitationCollectionPagerAdapter.getItem(0);
                        frFb.facebook();
                    }
	            	
				}
				//Favoris
				else{
					
	            	myMenu.findItem(R.id.invitation_facebook).setIcon(R.drawable.picto_fbup);
	            	myMenu.findItem(R.id.invitation_contacts).setIcon(R.drawable.picto_phoneup);
	            	myMenu.findItem(R.id.invitation_favoris).setIcon(R.drawable.picto_stardown);
				}
				
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

        //Get the edit text to search
        searchGuests = (EditText)findViewById(R.id.search_guests);
        searchGuests.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(s.toString());
                mInvitationCollectionPagerAdapter.getItem(mViewPager.getCurrentItem()).updateSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		myMenu = menu;
		
		getSupportMenuInflater().inflate(R.menu.activity_invitation, menu);
		return true;
	}
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );
                break;
            case R.id.invitation_contacts:
            	mViewPager.setCurrentItem(1);
            	
            	break;
            
            case R.id.invitation_facebook:
            	mViewPager.setCurrentItem(0);
            	break;
            
            case R.id.invitation_favoris:
            	mViewPager.setCurrentItem(2);
            	break;
            	
               
        }
        return super.onOptionsItemSelected(item);
    }
	
	
	
	// Since this is an object collection, use a FragmentStatePagerAdapter,
	// and NOT a FragmentPagerAdapter.
	public class InvitationCollectionPagerAdapter extends
	        FragmentStatePagerAdapter {
		
		
		
	    public InvitationCollectionPagerAdapter(FragmentManager fm) {
	        super(fm);
	    }

	    @Override
	    public InvitationsFragment getItem(int i) {
	    	Log.d("SIZE INVITATIONS", ""+i);
	    	Log.d("SIZE SIZE", ""+frs.size());
	    	
	    	
	    	return frs.get(i);
	    	
	        
	    }

	    @Override
	    public int getCount() {
	        return frs.size();
	    }

	    @Override
	    public CharSequence getPageTitle(int position) {
	        return "OBJECT " + (position + 1);
	    }
	}
	
	
	
	//Modifier le label du nombre d'invites
	public static void modifyNbInvites(){
		
		nb_invites.setText(invitesUser.size());
	}
	
	
	//On envoit l'invitation au serveur pour tout ces users
	
	public void invite(View view) throws JSONException{

		
	       JSONArray users = new JSONArray();
	       for(int i=0;i<invitesUser.size();i++){
	    	  users.put(invitesUser.get(i).getUserToJSON());
	       }
	       
	       JSONObject object = new JSONObject();
	       object.put("users", users);
	       
	       System.out.println(object.toString());
	       
	       StringEntity se = null;
			try {
				se = new StringEntity(object.toString());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	       
			//On lance la progress dialog
			final ProgressDialog dialog = ProgressDialog.show(InvitationActivity.this, null, "Envoie des invitations");
            se.setContentType("application/json");
			
	       MomentApi.postJSON(this, "newguests/"+idMoment, se, new AsyncHttpResponseHandler() {
	            @Override
	            public void onSuccess(String response) {
		            System.out.println(response);
                    dialog.dismiss();


                    ArrayList<User> SMSUsers = new ArrayList<User>();
                    for(User user:invitesUser){

                        //On fait pas pour les favoris
                        if(user.getId()==null){
                            if(user.getNumTel()!=null){
                                //We send and SMS
                                SMSUsers.add(user);
                            }
                        }
                    }

                    if(SMSUsers.size()>0){
                        String _messageNumber="";
                        for(int i=0;i<SMSUsers.size();i++){
                            _messageNumber += SMSUsers.get(i).getNumTel();
                            if(i<(SMSUsers.size()-1)) _messageNumber += ";";
                        }

                        String messageText = "Hi , Just SMSed to say hello";
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.setData(Uri.parse("sms:" + _messageNumber));
                        sendIntent.putExtra("sms_body", messageText);
                        startActivity(sendIntent);

                    }

                    finish();
                    overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );
		               
	            }

               public void onFailure(Throwable error, String content) {
                   // By default, call the deprecated onFailure(Throwable) for compatibility
                   System.out.println(content);
                   dialog.dismiss();
               }
	        });
		
		
	}

}
