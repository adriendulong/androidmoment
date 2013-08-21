package com.moment.activities;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.maps.MapView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moment.AppMoment;
import com.moment.BuildConfig;
import com.moment.R;
import com.moment.classes.MomentApi;
import com.moment.fragments.ChatFragment;
import com.moment.fragments.InfosFragment;
import com.moment.fragments.PhotosFragment;
import com.moment.models.Chat;
import com.moment.models.Moment;
import com.moment.models.Photo;
import com.moment.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MomentInfosActivity extends SherlockFragmentActivity {

    static final int PICK_CAMERA_COVER = 1;
    static final int PICK_CAMERA_PHOTOS = 2;
    static final int NEW_INVIT = 3;
    static final int LIST_INVIT = 4;

    private int CHAT_PUSH = 3;
    private int PHOTO_PUSH = 2;

    private int type_id, moment_id;
    public static int mNbPhotos=1;

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

    private Moment moment;

    private boolean isSuccess = false;

    private ProgressDialog mProgressDialog;

    private String precedente; //previous screen


    private String TAG = "InfosActivity";


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

        if (BuildConfig.DEBUG) {
            Utils.logHeap();
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        }, intentFilter);


        precedente = getIntent().getStringExtra("precedente");


        if (precedente.equals("timeline")) position = getIntent().getIntExtra("position", 1);
        if (precedente.equals("push") || precedente.equals("notifs")) {
            type_id = getIntent().getIntExtra("type_id", -1);
            momentID = getIntent().getLongExtra("moment_id", 1);

            if (type_id == PHOTO_PUSH) position = 0;
            else if (type_id == CHAT_PUSH) position = 2;
            else position = 1;
        } else momentID = getIntent().getLongExtra("id", 1);


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

        pager.setOffscreenPageLimit(0);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.pager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                Log.e("PAGE", "" + arg0);
                updateMenuItem(arg0);

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });


        if (AppMoment.getInstance().user == null) AppMoment.getInstance().getUser();
        getMoment();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (photosFr.isAsyncRun() == true) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setTitle(getString(R.string.attention))
                    .setMessage(getString(R.string.upload_running))
                    .setCancelable(false)
                    .setNegativeButton(getString(R.string.non), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton(getString(R.string.oui), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        super.onBackPressed();
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        protected final String[] CONTENT = new String[]{"PHOTOS", "INFOS", "CHAT"};

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


    public void listInvit(View view) {
        EasyTracker.getTracker().sendEvent("Infos", "button_press", "List Guests", null);
        callInvit(LIST_INVIT);

    }


    public void addGuests(View view) {
        EasyTracker.getTracker().sendEvent("Infos", "button_press", "Add Guests", null);
        callInvit(NEW_INVIT);
    }

    public void postMessage(final View view) {
        EasyTracker.getTracker().sendEvent("Chat", "button_press", "Post Message", null);

        final EditText postMessage = (EditText) findViewById(R.id.edit_chat_post_message);
        final String message = postMessage.getText().toString();
        if (message.length() > 0) {

            view.setEnabled(false);

            RequestParams params = new RequestParams();
            params.put("message", message);

            MomentApi.post("newchat/" + momentID, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    view.setEnabled(true);

                    Chat chat = new Chat();
                    try {
                        chat.chatFromJSON(response.getJSONObject("chat"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    chat.__setDaoSession(AppMoment.getInstance().daoSession);
                    chat.setMoment(AppMoment.getInstance().user.getMomentById(momentID));
                    chat.setUser(AppMoment.getInstance().user);
                    AppMoment.getInstance().user.getMomentById(momentID).getChats().add(chat);

                    chatFr.newMessage(chat);

                    postMessage.setText("");
                }

                public void onFailure(Throwable error, String content) {
                    view.setEnabled(true);
                    Toast.makeText(getApplicationContext(), getString(R.string.erreur_envoi_meessage), Toast.LENGTH_SHORT).show();
                    System.out.println("FAILURE : " + content);
                }
            });
        }

    }

    public void editMessage(View view) {


    }


    public static class Exchanger {
        public static MapView mMapView;
    }


    public void updateMenuItem(int position) {

        if (position == 1) {
            hideKeyboard();
            System.out.println("INFOS");
            myMenu.findItem(R.id.tab_photo).setIcon(R.drawable.picto_photo_up);
            myMenu.findItem(R.id.tab_infos).setIcon(R.drawable.picto_info_down);
            myMenu.findItem(R.id.tab_chat).setIcon(R.drawable.picto_chat_up);
        } else if (position == 0) {
            hideKeyboard();
            System.out.println("PHOTOS");
            myMenu.findItem(R.id.tab_photo).setIcon(R.drawable.picto_photo_down);
            myMenu.findItem(R.id.tab_infos).setIcon(R.drawable.picto_info_up);
            myMenu.findItem(R.id.tab_chat).setIcon(R.drawable.picto_chat_up);


        } else {
            System.out.println("CHATS");
            myMenu.findItem(R.id.tab_photo).setIcon(R.drawable.picto_photo_up);
            myMenu.findItem(R.id.tab_infos).setIcon(R.drawable.picto_info_up);
            myMenu.findItem(R.id.tab_chat).setIcon(R.drawable.picto_chat_down);
        }

    }


    public void callInvit(int request_code) {
        Intent intent;

        if (request_code == NEW_INVIT) {
            intent = new Intent(MomentInfosActivity.this, InvitationActivity.class);
            intent.putExtra("id", momentID);

            if (android.os.Build.VERSION.SDK_INT >= 16) {
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
                startActivityForResult(intent, request_code, bndlanimation);
            } else {
                startActivityForResult(intent, request_code);
            }


        } else if (request_code == LIST_INVIT) {
            intent = new Intent(MomentInfosActivity.this, ListGuestsActivity.class);
            intent.putExtra("id", momentID);

            if (android.os.Build.VERSION.SDK_INT >= 16) {
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
                startActivityForResult(intent, request_code, bndlanimation);
            } else {
                startActivityForResult(intent, request_code);
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_INVIT) {
            Log.d("FIN ACTIVITY", "NEW INVIT");
        } else if (requestCode == LIST_INVIT) {
            Log.d("FIN ACTIVITY", "LIST");
        }
    }


    public void notRsvp(View view) {
        EasyTracker.getTracker().sendEvent("Infos", "button_press", "Not Going", null);
        infosFr.notRsvp();
    }


    public void maybeRsvp(View view) {
        EasyTracker.getTracker().sendEvent("Infos", "button_press", "Maybe Goiing", null);
        infosFr.maybeRsvp();
    }


    public void goingRsvp(View view) {
        EasyTracker.getTracker().sendEvent("Infos", "button_press", "Going", null);
        infosFr.goingRsvp();
    }


    public void getMoment() {

        if (AppMoment.getInstance().user == null) AppMoment.getInstance().getUser();

        //If we have internet we go to get the moment
        if (AppMoment.getInstance().checkInternet()) {
            mProgressDialog = ProgressDialog.show(this, getString(R.string.loading), getString(R.string.loading_info_moment));
            MomentApi.get("moment/" + momentID, null, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(JSONObject response) {
                    isSuccess = true;
                    Moment tempMoment = new Moment();
                    try {
                        tempMoment.setMomentFromJson(response);
                        if(response.has("nb_photos")) mNbPhotos = response.getInt("nb_photos");
                        Log.v(TAG, tempMoment.getName());
                        moment = tempMoment;


                        AppMoment.getInstance().user.getMoments().add(tempMoment);


                        if (pager.getCurrentItem() == 2) {
                            ((ChatFragment) mPagerAdapter.getItem(2)).createFragment(momentID);
                            ((InfosFragment) mPagerAdapter.getItem(1)).createFragment(momentID);
                        } else if (pager.getCurrentItem() == 1) {
                            ((ChatFragment) mPagerAdapter.getItem(2)).createFragment(momentID);
                            ((InfosFragment) mPagerAdapter.getItem(1)).createFragment(momentID);
                            ((PhotosFragment) mPagerAdapter.getItem(0)).createFragment(momentID);
                        } else if (pager.getCurrentItem() == 0) {
                            ((PhotosFragment) mPagerAdapter.getItem(0)).createFragment(momentID);
                            ((InfosFragment) mPagerAdapter.getItem(1)).createFragment(momentID);
                        }

                        mProgressDialog.dismiss();
                        if (precedente.equals("creation")) callInvit(NEW_INVIT);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON problems");
                    }

                }

                @Override
                public void onFailure(Throwable error, String content) {
                    isSuccess = true;

                    Toast.makeText(getApplicationContext(), getString(R.string.error_dl_moment), Toast.LENGTH_SHORT).show();
                    if (AppMoment.getInstance().user.getMomentById(momentID) != null) {
                        moment = AppMoment.getInstance().user.getMomentById(momentID);
                        mNbPhotos = moment.getPhotos().size();
                        if (pager.getCurrentItem() == 2) {
                            ((ChatFragment) mPagerAdapter.getItem(2)).createFragment(momentID);
                            ((InfosFragment) mPagerAdapter.getItem(1)).createFragment(momentID);
                        } else if (pager.getCurrentItem() == 1) {
                            ((ChatFragment) mPagerAdapter.getItem(2)).createFragment(momentID);
                            ((InfosFragment) mPagerAdapter.getItem(1)).createFragment(momentID);
                            ((PhotosFragment) mPagerAdapter.getItem(0)).createFragment(momentID);
                        } else if (pager.getCurrentItem() == 0) {
                            ((PhotosFragment) mPagerAdapter.getItem(0)).createFragment(momentID);
                            ((InfosFragment) mPagerAdapter.getItem(1)).createFragment(momentID);
                        }
                    }
                    if (precedente.equals("creation")) callInvit(NEW_INVIT);
                    mProgressDialog.dismiss();
                }

                public void onFinish() {

                    if (!isSuccess) {

                        Toast.makeText(getApplicationContext(), getString(R.string.error_dl_moment), Toast.LENGTH_SHORT).show();
                        if (AppMoment.getInstance().user.getMomentById(momentID) != null) {
                            moment = AppMoment.getInstance().user.getMomentById(momentID);
                            mNbPhotos = moment.getPhotos().size();
                            if (pager.getCurrentItem() == 2) {
                                ((ChatFragment) mPagerAdapter.getItem(2)).createFragment(momentID);
                                ((InfosFragment) mPagerAdapter.getItem(1)).createFragment(momentID);
                            } else if (pager.getCurrentItem() == 1) {
                                ((ChatFragment) mPagerAdapter.getItem(2)).createFragment(momentID);
                                ((InfosFragment) mPagerAdapter.getItem(1)).createFragment(momentID);
                                ((PhotosFragment) mPagerAdapter.getItem(0)).createFragment(momentID);
                            } else if (pager.getCurrentItem() == 0) {
                                ((PhotosFragment) mPagerAdapter.getItem(0)).createFragment(momentID);
                                ((InfosFragment) mPagerAdapter.getItem(1)).createFragment(momentID);
                            }
                        }
                        if (precedente.equals("creation")) callInvit(NEW_INVIT);
                        mProgressDialog.dismiss();
                    }
                }
            });
        }
        else{
            if (AppMoment.getInstance().user.getMomentById(momentID) != null) {
                moment = AppMoment.getInstance().user.getMomentById(momentID);
                mNbPhotos = moment.getPhotos().size();
            }
        }
    }

    public Long getMomentId() {
        if (this.moment != null) return this.momentID;
        else return null;

    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(
                "com.google.android.c2dm.intent.RECEIVE");
        mReceiver = new BroadcastReceiver() {
            private int type_notifs
                    ,
                    chat_id
                    ,
                    photo_id;
            private Long moment_id;

            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    JSONArray array = new JSONArray(intent.getExtras().getString("data"));
                    JSONObject jsonMessage = array.getJSONObject(0);
                    moment_id = jsonMessage.getLong("moment_id");
                    type_notifs = jsonMessage.getInt("type_notif");

                    if (type_notifs == CHAT_PUSH) {
                        if (moment_id.equals(momentID)) {
                            chat_id = jsonMessage.getInt("chat_id");

                            if (pager.getCurrentItem() == 2 || pager.getCurrentItem() == 1) {
                                MomentApi.get("chat/" + chat_id, null, new JsonHttpResponseHandler() {

                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.new_message), Toast.LENGTH_SHORT).show();
                                        Chat tempChat = new Chat();
                                        try {
                                            tempChat.chatFromJSON(response.getJSONObject("chat"), moment);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        tempChat.__setDaoSession(AppMoment.getInstance().daoSession);
                                        tempChat.setMoment(AppMoment.getInstance().user.getMomentById(momentID));
                                        tempChat.setUser(tempChat.getUser());
                                        AppMoment.getInstance().user.getMomentById(momentID).getChats().add(tempChat);

                                        chatFr.newMessage(tempChat);

                                        if (pager.getCurrentItem() == 1) pager.setCurrentItem(2);

                                    }

                                    @Override
                                    public void onFailure(Throwable error, String content) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.probleme_chat), Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        } else {
                            /*
                            AlertDialog.Builder monDialogue = new AlertDialog.Builder(MomentInfosActivity.this);
                            monDialogue.setTitle(getString(R.string.nouveaut_chat));
                            monDialogue.setMessage("Nouveau chat sur "+moment.getName());


                            monDialogue.setPositiveButton(getString(R.string.consulter), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intentMoment = new Intent(MomentInfosActivity.this, MomentInfosActivity.class);
                                    intentMoment.putExtra("precedente", "notifs");
                                    intentMoment.putExtra("type_id", type_notifs);
                                    intentMoment.putExtra("moment_id", moment_id);
                                    startActivity(intentMoment);

                                }
                            });
                            monDialogue.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            monDialogue.show();
                            */
                        }
                    } else if (type_notifs == PHOTO_PUSH) {

                        if (pager.getCurrentItem() == 0 || pager.getCurrentItem() == 1) {
                            Toast.makeText(getApplicationContext(), getString(R.string.new_photo), Toast.LENGTH_SHORT).show();
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        this.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {

        super.onPause();

        this.unregisterReceiver(this.mReceiver);
    }


    public void modifyMoment(View view) {
        EasyTracker.getTracker().sendEvent("Infos", "button_press", "Modify Moment", null);
        Intent modifyIntent = new Intent(this, CreationDetailsActivity.class);
        modifyIntent.putExtra("nomMoment", moment.getName());
        modifyIntent.putExtra("moment_id", moment.getId());
        startActivity(modifyIntent);
    }


    public void goPhotos(View view) {
        EasyTracker.getTracker().sendEvent("Infos", "button_press", "Go Photos View", null);
        pager.setCurrentItem(0);
    }

    public void updateInfosPhotos(List<Photo> photos) {
        infosFr.updatePhotos(photos);
    }


    public void detailMap(View view) {
        EasyTracker.getTracker().sendEvent("Infos", "button_press", "Detail Map", null);
        String label = moment.getAdresse();
        String uriBegin = "geo:" + 0 + "," + 0;
        String query = moment.getAdresse();
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (this.getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    public void delete(View view) {
        EasyTracker.getTracker().sendEvent("Infos", "button_press", "Delete Moment", null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle(getResources().getString(R.string.title_pop_up_delete))
                .setMessage(getResources().getString(R.string.pop_up_delete))
                .setCancelable(false)
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog progressDialog = ProgressDialog.show(MomentInfosActivity.this, null, getString(R.string.suppressing));

                        if (moment.getUser().getId() == AppMoment.getInstance().user.getId()) {
                            MomentApi.get("delmoment/" + moment.getId(), null, new JsonHttpResponseHandler() {

                                @Override
                                public void onSuccess(JSONObject response) {
                                    progressDialog.dismiss();
                                    Intent timelineIntent = new Intent(MomentInfosActivity.this, TimelineActivity.class);
                                    startActivity(timelineIntent);

                                }

                                @Override
                                public void onFailure(Throwable error, String content) {
                                    System.out.println(content);
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_delete), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void addPhotosFromInfos(View view){
        pager.setCurrentItem(0);
        photosFr.startDialog();
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



