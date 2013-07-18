package com.moment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;

import android.view.WindowManager;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.R;
import com.moment.classes.MomentApi;
import com.moment.fragments.GuestsFragment;
import com.moment.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListGuestsActivity extends SherlockFragmentActivity {

    private ArrayList<GuestsFragment> frs;
    private Long idMoment;
    private ArrayList<User> owners, admins, comings, notComings, maybe, uk, allComings;
    private InvitationCollectionPagerAdapter mInvitationCollectionPagerAdapter;
    private ViewPager mViewPager;
    private PagerTabStrip tabStrip;
    private Menu myMenu;

    //RequestCode
    private int NEW_GUEST = 0;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_guests_activity);

        //Initialisation of the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setIcon(R.drawable.logo);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //We get the id of the moment
        idMoment = getIntent().getLongExtra("id", -1);

        //Create the three fragments
        //Init les fragments
        frs = new ArrayList<GuestsFragment>();

        for(int i=0; i<3; i++){
            GuestsFragment fragment = new GuestsFragment(i);
            Bundle args = new Bundle();
            args.putInt(GuestsFragment.POSITION, i);
            fragment.setArguments(args);
            frs.add(fragment);
        }


        //Manage the fragments
        // Le page adapter qui va gerer le passage d'une page � l'autre
        mInvitationCollectionPagerAdapter =
                new InvitationCollectionPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager_guests);
        mViewPager.setAdapter(mInvitationCollectionPagerAdapter);
        mViewPager.setCurrentItem(1);

        //Customize the tab strip
        tabStrip = (PagerTabStrip)findViewById(R.id.pager_tab_strip_guests);
        tabStrip.setTabIndicatorColor(getResources().getColor(R.color.orange));
        tabStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f);

        if(savedInstanceState==null){
            //We init all the array list
            owners = new ArrayList<User>();
            admins = new ArrayList<User>();
            comings = new ArrayList<User>();
            notComings = new ArrayList<User>();
            maybe = new ArrayList<User>();
            uk = new ArrayList<User>();
            allComings = new ArrayList<User>();

            //We get all the guests
            MomentApi.get("guests/" +idMoment, null, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(JSONObject response) {
                    try {

                        Log.d("RESPONSE GUEST", response.toString());

                        JSONArray ownerUsers = response.getJSONArray("owner");
                        JSONArray adminUsers = response.getJSONArray("admin");
                        JSONArray comingUsers = response.getJSONArray("coming");
                        JSONArray notComingUsers = response.getJSONArray("not_coming");
                        JSONArray maybeUsers = response.getJSONArray("maybe");
                        JSONArray ukUsers = response.getJSONArray("unknown");

                        //We add the owners (normally only one
                        for(int i=0;i<ownerUsers.length();i++){
                            User tempUser = new User();
                            tempUser.setUserFromJson(ownerUsers.getJSONObject(i));
                            owners.add(tempUser);

                            //All the owner are coming
                            allComings.add(tempUser);
                        }

                        //We add the user who are admins
                        for(int i=0;i<adminUsers.length();i++){
                            User tempUser = new User();
                            tempUser.setUserFromJson(adminUsers.getJSONObject(i));
                            admins.add(tempUser);

                            //All the admins are coming
                            allComings.add(tempUser);
                        }

                        //We add the users who are coming
                        for(int i=0;i<comingUsers.length();i++){
                            User tempUser = new User();
                            tempUser.setUserFromJson(comingUsers.getJSONObject(i));
                            comings.add(tempUser);

                            allComings.add(tempUser);
                        }

                        //We add the users who are not coming
                        for(int i=0;i<notComingUsers.length();i++){
                            User tempUser = new User();
                            tempUser.setUserFromJson(notComingUsers.getJSONObject(i));
                            notComings.add(tempUser);
                        }

                        //We add the users who are maybe coming
                        for(int i=0;i<maybeUsers.length();i++){
                            User tempUser = new User();
                            tempUser.setUserFromJson(maybeUsers.getJSONObject(i));
                            maybe.add(tempUser);
                        }

                        //We add the users for who we don't know their response
                        for(int i=0;i<ukUsers.length();i++){
                            User tempUser = new User();
                            tempUser.setUserFromJson(ukUsers.getJSONObject(i));
                            uk.add(tempUser);
                        }

                        //Update the fragment displaying the users who are maybe coming
                        frs.get(0).updateListGuests(maybe);
                        //Update the fragment displaying the users who are  coming
                        frs.get(1).updateListGuests(allComings);
                        //Update the fragment displaying the users we don't know their response
                        frs.get(2).updateListGuests(uk);


                    } catch (JSONException e) {
                        // Auto-generated catch block
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailure(Throwable error, String content) {
                    System.out.println(content);
                }

            });
        }


    }


    @Override
    public void onResume(){
        super.onResume();
    }


    /**
     * Menu
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        myMenu = menu;

        getSupportMenuInflater().inflate(R.menu.activity_guests_list, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );
                break;
            case R.id.add_guests:
                Intent i = new Intent(ListGuestsActivity.this, InvitationActivity.class);
                i.putExtra("id", idMoment);
                startActivityForResult(i, NEW_GUEST);
                overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left );
                break;


        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Pager Adapter
     */

    public class InvitationCollectionPagerAdapter extends
            FragmentStatePagerAdapter {

        public InvitationCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public GuestsFragment getItem(int i) {
            return frs.get(i);


        }

        @Override
        public int getCount() {
            return frs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==0) return getString(R.string.title_maybe);
            else if(position==1) return getString(R.string.title_coming);
            else return getString(R.string.title_unknown);
        }
    }


    /**
     * Activity result
     * When we come back from the add of guests we know that the info view will need to update
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==NEW_GUEST){
            Log.d("FIN ACTIVITY", "NEW INVIT");
        }
    }
}