package com.moment.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.BuildConfig;
import com.moment.R;
import com.moment.classes.MomentApi;
import com.moment.classes.MomentsAdapter;
import com.moment.models.Moment;
import com.moment.models.Notification;
import com.moment.util.CommonUtilities;
import com.moment.util.ImageCache;
import com.moment.util.ImageFetcher;
import com.moment.util.Utils;
import com.moment.models.User;
import com.moment.util.*;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TimelineActivity extends SlidingFragmentActivity {

    private static final String IMAGE_CACHE_DIR = "timeline";
    private Intent intentMoment;
    private LayoutInflater inflater;
    private Long actuelMomentSelect = Long.parseLong("-1");
    private RelativeLayout myMoments, profile, settings;
    private ListView notifsListView;
    private ArrayList<Notification> notifications;
    private ArrayList<Notification> invitations;
    private TextView totalNotifText;
    private ProgressBar notifProgress;
    private int totalNotifs, nbNotifs, nbInvit;
    private ScrollView scrollView;
    private SlidingMenu sm;
    private ListView momentsList;
    private List<Moment> moments;
    private MomentsAdapter adapter;
    private ProgressDialog dialog;
    private ImageView todayBtn;
    private Bitmap todayBitmap;
    private ImageFetcher mImageFetcher;
    private int mImageThumbSize;
    private EditText searchEditText;
    private ListView searchlist;
    private ImageView separator;
    private ImageView separator2;
    private ImageView separator3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Utils.logHeap();
        }

        TelephonyManager manager =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        manager.getLine1Number();

        //Broadcast Listener (when logout remove from history)
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        }, intentFilter);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);


        setContentView(R.layout.activity_timeline);
        setBehindContentView(R.layout.volet_timeline);

        sm = getSlidingMenu();
        sm.setBehindOffset(getMarginRightSlider());
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setShadowWidth(10);

        //Fetcher images
        mImageThumbSize = this.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.picto_photo_vide);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);


        //Action bar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final RelativeLayout actionBarLayout = (RelativeLayout) inflater.inflate(R.layout.notif_action_bar, null);
        totalNotifText = (TextView) actionBarLayout.findViewById(R.id.actionbar_notifcation_textview);
        notifProgress = (ProgressBar) actionBarLayout.findViewById(R.id.progress_notifs);
        getSupportActionBar().setCustomView(actionBarLayout);
        actionBarLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        todayBtn = (ImageView) findViewById(R.id.today_btn);
        myMoments = (RelativeLayout) sm.getRootView().findViewById(R.id.my_moments_button);
        profile = (RelativeLayout) sm.getRootView().findViewById(R.id.profile_button_volet);
        settings = (RelativeLayout) sm.getRootView().findViewById(R.id.settings_button_volet);
        todayBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.btn_today);
        separator = (ImageView) findViewById(R.id.separator);
        separator2 = (ImageView) findViewById(R.id.separator2);
        separator3 = (ImageView) findViewById(R.id.separator3);

        searchlist = (ListView) findViewById(R.id.searchList);
        searchlist.setVisibility(View.INVISIBLE);

        searchEditText = (EditText) sm.getRootView().findViewById(R.id.volet_search);
        searchEditText.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                sm.setBehindOffset(0);
                myMoments.setVisibility(View.INVISIBLE);
                profile.setVisibility(View.INVISIBLE);
                settings.setVisibility(View.INVISIBLE);
                separator.setVisibility(View.INVISIBLE);
                separator2.setVisibility(View.INVISIBLE);
                separator3.setVisibility(View.INVISIBLE);
            }
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searchlist.setVisibility(View.VISIBLE);
                MomentApi.get("search/" + searchEditText.getText().toString(), null, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(JSONObject response){
                        response.toString();
                    }
                });
                return false;
            }
        });

        sm.setOnCloseListener(new SlidingMenu.OnCloseListener() {
            @Override
            public void onClose() {
                sm.setBehindOffset(getMarginRightSlider());
                myMoments.setVisibility(View.VISIBLE);
                profile.setVisibility(View.VISIBLE);
                settings.setVisibility(View.VISIBLE);
                separator.setVisibility(View.VISIBLE);
                separator2.setVisibility(View.VISIBLE);
                separator3.setVisibility(View.VISIBLE);
                searchlist.setVisibility(View.INVISIBLE);
                searchEditText.setText("");
            }
        });

        moments = new ArrayList<Moment>();
        momentsList = (ListView) findViewById(R.id.list_moments);
        momentsList.setSelector(android.R.color.transparent);
        adapter = new MomentsAdapter(this, R.layout.timeline_moment, moments, mImageFetcher);
        momentsList.setAdapter(adapter);
        momentsList.setOnScrollListener(new TImelineScrollListener());

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == myMoments.getId()) {
                    EasyTracker.getTracker().sendEvent("Volet", "button_press", "Timeline", null);
                    toggle();


                } else if (v.getId() == profile.getId()) {
                    EasyTracker.getTracker().sendEvent("Volet", "button_press", "Profil", null);
                    Intent intent = new Intent(getApplication(), EditProfilActivity.class);
                    startActivity(intent);
                } else if (v.getId() == settings.getId()) {
                    EasyTracker.getTracker().sendEvent("Volet", "button_press", "Settings", null);
                    Intent intent = new Intent(getApplication(), SettingsActivity.class);
                    startActivity(intent);
                }
            }
        };

        myMoments.setOnClickListener(listener);
        profile.setOnClickListener(listener);
        settings.setOnClickListener(listener);

        notifications = new ArrayList<Notification>();
        invitations = new ArrayList<Notification>();

        notifsListView = (ListView) sm.getRootView().findViewById(R.id.list_notifs);

        if (savedInstanceState == null) {

            if (AppMoment.getInstance().checkInternet() == false) {
                if (!AppMoment.getInstance().momentDao.loadAll().isEmpty()) {
                    List<Moment> momentList = AppMoment.getInstance().momentDao.loadAll();
                    Collections.sort(momentList, new CustomComparator());
                    for (Moment moment : momentList) {
                        AppMoment.getInstance().user.getMoments().add(moment);
                        moments.add(moment);
                    }
                    goToToday(false);

                    List<Notification> tempNotifs = AppMoment.getInstance().notificationDao.loadAll();
                    for (Notification notif : tempNotifs) {
                        if (notif.getTypeNotif() == 0) {
                            invitations.add(notif);
                        } else {
                            notifications.add(notif);
                        }
                    }
                    totalNotifText.setText("" + 0);
                    totalNotifText.setVisibility(View.VISIBLE);
                    AppMoment.getInstance().user.setNotifications(notifications);
                    AppMoment.getInstance().user.setInvitations(invitations);
                    notifProgress.setVisibility(View.INVISIBLE);
                }
            } else {
                dialog = ProgressDialog.show(this, null, getResources().getString(R.string.moment_dl));

                MomentApi.initialize(getApplicationContext());
                MomentApi.get("moments", null, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            System.out.println(response.toString());
                            JSONArray momentsArray = response.getJSONArray("moments");
                            int nbMoments = momentsArray.length();

                            for (int j = 0; j < nbMoments; j++) {

                                JSONObject momentJson = (JSONObject) momentsArray.get(j);
                                Moment momentTemp = new Moment();
                                momentTemp.setMomentFromJson(momentJson);
                                AppMoment.getInstance().user.addMoment(momentTemp);
                                moments.add(momentTemp);

                                if (AppMoment.getInstance().momentDao.load(momentTemp.getId()) == null) {
                                    AppMoment.getInstance().momentDao.insert(momentTemp);
                                } else {
                                    AppMoment.getInstance().momentDao.update(momentTemp);
                                }
                            }

                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                            goToToday(false);

                            if (BuildConfig.DEBUG) {
                                Utils.logHeap();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable error, String content) {
                        Log.e("TIMELINE", content);
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.echec_dl_moments), Toast.LENGTH_LONG).show();
                        if (!AppMoment.getInstance().momentDao.loadAll().isEmpty()) {
                            List<Moment> momentList = AppMoment.getInstance().momentDao.loadAll();
                            for (Moment moment : momentList) {
                                AppMoment.getInstance().user.getMoments().add(moment);
                                moments.add(moment);
                            }
                            goToToday(false);
                        }


                    }

                });

                getNotifications();
            }
        } else {

            Log.e("TIMELINE", "LOSTTTTTT");
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            Long savedUserID = sharedPreferences.getLong("userID", -1);
            AppMoment.getInstance().user = AppMoment.getInstance().userDao.load(savedInstanceState.getLong("userID"));
            Log.e("TIMELINE", "User id : " + savedInstanceState.getLong("userID"));

            if (AppMoment.getInstance().user == null) {
                Intent intent = new Intent(TimelineActivity.this, MomentActivity.class);
                startActivity(intent);
            }

            dialog = ProgressDialog.show(this, null, getResources().getString(R.string.moment_dl));

            MomentApi.initialize(getApplicationContext());
            MomentApi.get("moments", null, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        System.out.println(response.toString());
                        JSONArray momentsArray = response.getJSONArray("moments");
                        int nbMoments = momentsArray.length();
                        Toast.makeText(getApplicationContext(), "Nombre de Moments " + nbMoments, Toast.LENGTH_LONG).show();

                        for (int j = 0; j < nbMoments; j++) {

                            JSONObject momentJson = (JSONObject) momentsArray.get(j);
                            Moment momentTemp = new Moment();
                            momentTemp.setMomentFromJson(momentJson);
                            AppMoment.getInstance().user.getMoments().add(momentTemp);
                            moments.add(momentTemp);

                            if (AppMoment.getInstance().momentDao.load(momentTemp.getId()) == null) {
                                AppMoment.getInstance().momentDao.insert(momentTemp);
                            }
                        }
                        adapter.notifyDataSetChanged();

                        dialog.dismiss();

                        goToToday(false);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    Log.e("TIMELINE", content);
                }

            });

            getNotifications();
        }


        //Get a rate from the user
        RateMeMaybe rmm = new RateMeMaybe(this);
        rmm.setPromptMinimums(5, 5, 10, 15);
        rmm.setRunWithoutPlayStore(true);
        rmm.setDialogMessage(getString(R.string.rate_body));
        rmm.setDialogTitle(getString(R.string.rate_title));
        rmm.setPositiveBtn(getString(R.string.rate_yes));
        rmm.setNegativeBtn(getString(R.string.rate_no));
        rmm.setNeutralBtn(getString(R.string.rate_neutral));
        rmm.run();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggle();
                return true;
            /*
            case R.id.menu_creer:
                Intent intent = new Intent(this, CreationActivity.class);
                startActivity(intent);
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong("userID", AppMoment.getInstance().user.getId());
    }

    public void notifications(View view) {
        EasyTracker.getTracker().sendEvent("Timeline", "button_press", "Notifications", null);
        if (notifProgress.getVisibility() != View.VISIBLE) {
            Intent notifs = new Intent(this, NotificationsActivity.class);
            startActivity(notifs);
        }

    }

    public void getNotifications() {
        MomentApi.get("notifications", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    totalNotifs = response.getInt("total_notifs");
                    totalNotifText.setText("" + totalNotifs);
                    totalNotifText.setVisibility(View.VISIBLE);
                    notifProgress.setVisibility(View.INVISIBLE);

                    JSONArray notifsObject = response.getJSONArray("notifications");

                    for (int i = 0; i < notifsObject.length(); i++) {
                        Notification notif = new Notification();
                        Log.e("EX", notifsObject.getJSONObject(i).toString());
                        notif.setFromJson(notifsObject.getJSONObject(i));

                        //If has no moment the notif will be null
                        if(notif.getId()!=null){
                            if (notif.getTypeNotif() == 2 || notif.getTypeNotif() == 3) {
                                notifications.add(notif);
                            }
                        }

                    }

                    AppMoment.getInstance().user.setNotifications(notifications);

                    if (AppMoment.getInstance().user != null) {
                        AppMoment.getInstance().userDao.update(AppMoment.getInstance().user);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                System.out.println(content);
                totalNotifText.setText("-");
                totalNotifText.setVisibility(View.VISIBLE);
                notifProgress.setVisibility(View.INVISIBLE);
            }
        });


        MomentApi.get("invitations", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray notifsObject = response.getJSONArray("invitations");

                    for (int i = 0; i < notifsObject.length(); i++) {

                        Notification notif = new Notification();
                        notif.setFromJson(notifsObject.getJSONObject(i));
                        invitations.add(notif);
                    }

                    AppMoment.getInstance().user.setInvitations(invitations);

                    Log.e("NB INVITATIONS", "" + AppMoment.getInstance().user.getInvitations().size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                System.out.println(content);
            }
        });
    }

    public void selectedMoment(View view) {
        EasyTracker.getTracker().sendEvent("Timeline", "button_press", "Select Moment", null);
        Log.v("TIMELINE", "" + view.getTag());

        intentMoment = new Intent(this, MomentInfosActivity.class);
        intentMoment.putExtra("precedente", "timeline");
        intentMoment.putExtra("position", 1);
        intentMoment.putExtra("id", (Long) view.getTag());
        startActivity(intentMoment);

    }

    public void goToToday(Boolean smooth) {

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, r.getDisplayMetrics());


        if (smooth) {
            if (android.os.Build.VERSION.SDK_INT > 10)
                momentsList.smoothScrollToPositionFromTop(getNearestDate(), (height / 2) - (int) px);
            else momentsList.setSelectionFromTop(getNearestDate(), height / 2);
        } else momentsList.setSelectionFromTop(getNearestDate(), height / 2);
    }

    public int getNearestDate() {
        Date currentDate = new Date();
        long minDiff = -1, currentTime = currentDate.getTime();
        Date minDate = null;
        int positionDate = -1;
        for (int i = 0; i < moments.size(); i++) {

            org.joda.time.DateTime dt = CommonUtilities.dateFormatISO.parseDateTime(moments.get(i).getDateDebut());
            long diff = Math.abs(currentTime - dt.toDate().getTime());
            if ((minDiff == -1) || (diff < minDiff)) {
                minDiff = diff;
                minDate = dt.toDate();
                positionDate = i;
            }
        }
        return positionDate;
    }

    public void today(View view) {
        EasyTracker.getTracker().sendEvent("Timeline", "button_press", "Go Today", null);
        goToToday(true);
    }

    public void rotateToday(float deg) {
        Matrix matrix = new Matrix();
        matrix.postRotate(deg);
        Bitmap rot = Bitmap.createBitmap(todayBitmap, 0, 0, todayBitmap.getWidth(), todayBitmap.getHeight(),
                matrix, true);
        todayBtn.setImageBitmap(rot);
    }

    /**
     * Calculate the margin right of the slide in order to have at least 200 dp width
     *
     * @return width
     */

    public int getMarginRightSlider() {
        int offset;
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        Display display = getWindowManager().getDefaultDisplay();
        int widthScreen = display.getWidth();
        int dpWidth = (int) (widthScreen / density);

        if (dpWidth > 250) {
            offset = dpWidth - 250;
        } else offset = 0;


        return offset;
    }

    public void createMoment(View view) {
        Intent intent = new Intent(this, CreationActivity.class);
        startActivity(intent);
    }

    public void volet(View view) {
        toggle();
    }

    private class TImelineScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 1;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;
        private boolean allFutur = false;
        private boolean allPast = false;
        private String TAG = "ScrollListener";

        public TImelineScrollListener() {
        }

        public TImelineScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }

            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                if (!allFutur) {
                    EasyTracker.getTracker().sendEvent("Timeline", "scroll", "Load futur Moments", null);

                    DateTime dt = CommonUtilities.dateFormatISO.parseDateTime(moments.get(moments.size() - 1).getDateDebut());
                    MomentApi.get("momentsafter/" + CommonUtilities.dateFormatReverseTiret.format(dt.toDate()) + "/1", null, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(JSONObject response) {
                            try {
                                System.out.println(response.toString());
                                JSONArray momentsArray = response.getJSONArray("moments");
                                int nbMoments = momentsArray.length();

                                for (int j = 0; j < nbMoments; j++) {

                                    JSONObject momentJson = (JSONObject) momentsArray.get(j);
                                    Moment momentTemp = new Moment();
                                    momentTemp.setMomentFromJson(momentJson);
                                    AppMoment.getInstance().user.getMoments().add(momentTemp);
                                    moments.add(momentTemp);

                                    if (AppMoment.getInstance().momentDao.load(momentTemp.getId()) == null) {
                                        AppMoment.getInstance().momentDao.insert(momentTemp);
                                    }
                                }

                                loading = false;

                                if (nbMoments == 0) allFutur = true;
                                else adapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Throwable error, String content) {
                            Log.e("TIMELINE", content);
                            Toast.makeText(getApplicationContext(), "Echec", Toast.LENGTH_LONG).show();
                        }

                    });

                    loading = true;
                }

            } else if (!loading && (firstVisibleItem) <= 3) {
                if (!allPast) {
                    EasyTracker.getTracker().sendEvent("Timeline", "scroll", "Load Old Moments", null);
                    Log.d(TAG, "Load PAST MOMENTS");

                    DateTime dt = CommonUtilities.dateFormatISO.parseDateTime(moments.get(0).getDateDebut());
                    MomentApi.get("momentsafter/" + CommonUtilities.dateFormatReverseTiret.format(dt.toDate()) + "/0", null, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(JSONObject response) {
                            try {
                                System.out.println(response.toString());
                                JSONArray momentsArray = response.getJSONArray("moments");
                                int nbMoments = momentsArray.length();

                                for (int j = 0; j < nbMoments; j++) {

                                    JSONObject momentJson = (JSONObject) momentsArray.get(j);
                                    Moment momentTemp = new Moment();
                                    momentTemp.setMomentFromJson(momentJson);
                                    AppMoment.getInstance().user.getMoments().add(momentTemp);
                                    moments.add(0, momentTemp);

                                    if (AppMoment.getInstance().momentDao.load(momentTemp.getId()) == null) {
                                        AppMoment.getInstance().momentDao.insert(momentTemp);
                                    }
                                }

                                if (nbMoments == 0) allPast = true;
                                else {
                                    adapter.notifyDataSetChanged();
                                    momentsList.setSelection(nbMoments + 1);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Throwable error, String content) {
                            Log.e("TIMELINE", content);
                            Toast.makeText(getApplicationContext(), "Echec", Toast.LENGTH_LONG).show();
                        }

                    });

                    loading = true;
                }

            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

    }

    private class CustomComparator implements Comparator<Moment> {
        @Override
        public int compare(Moment lhs, Moment rhs) {
            DateTime dateOne = new DateTime(lhs.getDateDebut());
            DateTime dateTwo = new DateTime(rhs.getDateDebut());
            return dateOne.compareTo(dateTwo);
        }
    }

}
