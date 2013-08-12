package com.moment.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.analytics.tracking.android.EasyTracker;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.util.CommonUtilities;
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
import java.util.Collections;
import java.util.Comparator;
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
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setIcon(R.drawable.logo);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        invitesUser = new ArrayList<User>();
        nb_invites = (TextView)findViewById(R.id.invites_selected);


        idMoment = getIntent().getLongExtra("id", -1);


        frs = new ArrayList<InvitationsFragment>();

        for(int i=0; i<3; i++){
            InvitationsFragment fragment = new InvitationsFragment();
            Bundle args = new Bundle();

            args.putInt(InvitationsFragment.POSITION, i);
            fragment.setArguments(args);
            frs.add(fragment);
        }


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

                System.out.println("Page SELECTIONNE :" + arg0);

                if(arg0 == 1){
                    System.out.println("CONTACTSS");
                    myMenu.findItem(R.id.invitation_facebook).setIcon(R.drawable.picto_fbup);
                    myMenu.findItem(R.id.invitation_contacts).setIcon(R.drawable.picto_phonedown);
                    myMenu.findItem(R.id.invitation_favoris).setIcon(R.drawable.picto_starup);
                }

                else if (arg0 == 0){
                    System.out.println("FBBBBBBBBB");
                    myMenu.findItem(R.id.invitation_facebook).setIcon(R.drawable.picto_fbdown);
                    myMenu.findItem(R.id.invitation_contacts).setIcon(R.drawable.picto_phoneup);
                    myMenu.findItem(R.id.invitation_favoris).setIcon(R.drawable.picto_starup);

                    if(frFb==null){
                        frFb = mInvitationCollectionPagerAdapter.getItem(0);
                        facebook();
                        progressDialog = new ProgressDialog(InvitationActivity.this);
                        progressDialog.setTitle("Facebook");
                        progressDialog.setMessage("Importation des contacts");
                        progressDialog.show();
                    }

                }

                else{

                    myMenu.findItem(R.id.invitation_facebook).setIcon(R.drawable.picto_fbup);
                    myMenu.findItem(R.id.invitation_contacts).setIcon(R.drawable.picto_phoneup);
                    myMenu.findItem(R.id.invitation_favoris).setIcon(R.drawable.picto_stardown);
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {


            }

            @Override
            public void onPageScrollStateChanged(int arg0) {


            }
        });


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

        myMenu = menu;

        getSupportMenuInflater().inflate(R.menu.activity_invitation, menu);
        return true;
    }

    @Override
    public void onBackPressed(){

        if(invitesUser.size()>0) {
            EasyTracker.getTracker().sendEvent("Invite", "state", "Back without invite", null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setTitle("Attention")
                    .setMessage("Vous avez des invitations non envoyées, voulez vous les envoyer ?")
                    .setCancelable(false)
                    .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                            overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );
                        }
                    })
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            invite();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if(invitesUser.size()>0){
                    EasyTracker.getTracker().sendEvent("Invite", "state", "Back without invite", null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder
                            .setTitle(getString(R.string.attention))
                            .setMessage(getString(R.string.invitations_left))
                            .setCancelable(false)
                            .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                    overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );
                                }
                            })
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    invite();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                else{
                    finish();
                    overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );
                }

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
        if(data.getBooleanExtra("result", false) == true)
        {
            if(SMSUsers != null && !SMSUsers.isEmpty())
            {
                try {
                    inviteSMS();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                finish();
            }
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
                                Collections.sort(frFb.users, new CustomComparator());
                                progressDialog.cancel();
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


    public static void modifyNbInvites(){

        nb_invites.setText(invitesUser.size());
    }




    public void invite()  {

        try{

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
                e.printStackTrace();
            }


            final ProgressDialog dialog = ProgressDialog.show(InvitationActivity.this, null, getString(R.string.invit_sent));
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

                    System.out.println(content);
                    dialog.dismiss();
                }
            });
        }catch(JSONException e){
            System.out.println(e);
        }

    }

    public void inviteFacebook(View view) throws JSONException{
        EasyTracker.getTracker().sendEvent("Invite", "button_press", "Invite people button", null);
        if(invitesUser.size()==0){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setTitle(getResources().getString(R.string.titre_pop_up_no_invite))
                    .setMessage(getResources().getString(R.string.pop_up_no_invite))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else {invite();}
    }

    public void inviteSMS() throws JSONException {
        Moment moment = AppMoment.getInstance().user.getMomentById(idMoment);

        if(SMSUsers != null && SMSUsers.size()>0){
            String _messageNumber="";
            for(int i=0;i<SMSUsers.size();i++){
                assert CommonUtilities.isValidTel(SMSUsers.get(i).getNumTel());
                _messageNumber += SMSUsers.get(i).getNumTel();
                if(i<(SMSUsers.size()-1)) _messageNumber += ";";
            }

            String messageText =
                    getResources().getString(R.string.text_sms_1)+" "
                            + moment.getName()+" "
                            + getResources().getString(R.string.text_sms_2)+"\n"
                            + moment.getUniqueUrl()+"\n"
                            + getResources().getString(R.string.text_sms_3)+" "
                            + AppMoment.getInstance().user.getFirstName()+" "
                            + AppMoment.getInstance().user.getLastName();

            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:" + _messageNumber));
            sendIntent.putExtra("sms_body", messageText);
            startActivity(sendIntent);

            finish();
        } else {
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

    private class CustomComparator implements Comparator<User> {
        @Override
        public int compare(User lhs, User rhs) {
            return lhs.getFirstName().compareTo(rhs.getFirstName());
        }
    }

}
