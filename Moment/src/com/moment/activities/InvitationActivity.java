package com.moment.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.InvitationsAdapter;
import com.moment.classes.MomentApi;
import com.moment.fragments.InvitationsFragment;
import com.moment.models.Moment;
import com.moment.models.User;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvitationActivity extends SherlockFragmentActivity {

    private InvitationCollectionPagerAdapter mInvitationCollectionPagerAdapter;
    private ViewPager mViewPager;
    Menu myMenu;
    private InvitationsFragment frFb;
    public static ArrayList<User> invitesUser;
    public static TextView nb_invites;
    private long idMoment;
    private EditText searchGuests;
    private ArrayList<User> SMSUsers;
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
        mViewPager.setOffscreenPageLimit(2);


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
                        facebook();
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
        searchGuests.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    handled = true;
                }
                return handled;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        try {
            inviteSMS();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void facebook(){
        try {
            openActiveSession(this, true, fbStatusCallback, Arrays.asList(
                    new String[]{"email"}), null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Session openActiveSession(Activity activity, boolean allowLoginUI,
                                      Session.StatusCallback callback, List<String> permissions, Bundle savedInstanceState) {
        Session.OpenRequest openRequest = new Session.OpenRequest(this).setCallback(callback).
                setDefaultAudience(SessionDefaultAudience.FRIENDS);
        Session session = null;
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, fbStatusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED) || allowLoginUI) {
                session.openForRead(openRequest);
                return session;
            }
        }
        return null;
    }

    private Session.StatusCallback fbStatusCallback = new Session.StatusCallback() {
        public void call(Session session, SessionState state, Exception exception) {
            if (state.isOpened()) {
                Request.executeMyFriendsRequestAsync(session, new Request.GraphUserListCallback() {
                    public void onCompleted(List<GraphUser> friends, Response response) {
                        if (response != null) {
                            try {

                                JSONArray d = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
                                Log.e("FB", d.toString());
                                frFb.adapter = new InvitationsAdapter(getApplicationContext(), R.layout.invitations_cell, frFb.users);
                                frFb.listView.setAdapter(frFb.adapter);
                                for (int i = 0; i < d.length(); i++) {
                                    JSONObject friend = d.getJSONObject(i);

                                    User user = new User();
                                    user.setFirstName(friend.getString("name"));
                                    user.setFacebookId(friend.getLong("id"));
                                    user.setFbPhotoUrl("http://graph.facebook.com/" + user.getFacebookId() + "/picture");
                                    frFb.users.add(user);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("", "Exception e");
                            }

                        }
                    }
                });
            }
        }
    };

    //Modifier le label du nombre d'invites
    public static void modifyNbInvites(){

        nb_invites.setText(invitesUser.size());
    }


    //On envoit l'invitation au serveur pour tout ces users

    public void inviteFacebook(View view) throws JSONException{


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

                ArrayList<User> FBUsers = new ArrayList<User>();
                SMSUsers = new ArrayList<User>();

                for(User user:invitesUser){

                    if(user.getId()==null && user.getNumTel() != null){
                        SMSUsers.add(user);
                    }

                }

                for(User user:invitesUser){
                    if(user.getId()==null && user.getFacebookId() != null){
                        FBUsers.add(user);
                    }
                }

                if(FBUsers.size()>0){
                    ArrayList<String> fbids = new ArrayList<String>();
                    for(User usr: FBUsers){
                        fbids.add(usr.getFacebookId().toString());
                    }

                    Intent intent = new Intent(getApplicationContext(), FacebookAppRequestActivity.class);
                    intent.putExtra("fbids", fbids);
                    intent.putExtra("momentId", idMoment);
                    startActivityForResult(intent, 0);
                } else {
                    try {
                        inviteSMS();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            public void onFailure(Throwable error, String content) {
                // By default, call the deprecated onFailure(Throwable) for compatibility
                System.out.println(content);
                dialog.dismiss();
            }
        });


    }

    public void inviteSMS() throws JSONException {
        Moment moment = AppMoment.getInstance().user.getMomentById(idMoment);

        if(SMSUsers != null && SMSUsers.size()>0){
            String _messageNumber="";
            for(int i=0;i<SMSUsers.size();i++){
                assert SMSUsers.get(i).getNumTel().matches("(0|0033|\\\\+33)[1-9]((([0-9]{2}){4})|((\\\\s[0-9]{2}){4})|((-[0-9]{2}){4}))");
                _messageNumber += SMSUsers.get(i).getNumTel();
                if(i<(SMSUsers.size()-1)) _messageNumber += ";";
            }

            String messageText = "Je viens de t'inviter à "+moment.getName()+" sur Moment : <unique_url>. Rejoins nous pour partager les photos et organiser l'évènement. A bientôt, "+AppMoment.getInstance().user.getFirstName()+" "+AppMoment.getInstance().user.getLastName();
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:" + _messageNumber));
            sendIntent.putExtra("sms_body", messageText);
            startActivity(sendIntent);

            finish();
        }

        overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );
    }


    private void hideKeyboard()
    {
        InputMethodManager inputManager = (InputMethodManager)
                getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(this.getCurrentFocus()!=null){
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

}
