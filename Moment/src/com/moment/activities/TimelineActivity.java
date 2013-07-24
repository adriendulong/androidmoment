package com.moment.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.animations.TimelineAnimation;
import com.moment.classes.DatabaseHelper;
import com.moment.classes.MomentApi;
import com.moment.classes.MomentsAdapter;
import com.moment.models.Moment;
import com.moment.models.Notification;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimelineActivity extends SlidingActivity {

    private Intent intentMoment;
    private LayoutInflater inflater;
    private Long actuelMomentSelect = Long.parseLong("-1");
    private RelativeLayout myMoments, profile, settings;
    private ListView notifsListView;
    private ArrayList<Notification> notifications;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v("TIMELINE", "CRETAE");

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        setContentView(R.layout.activity_timeline);
        setBehindContentView(R.layout.volet_timeline);
        sm = getSlidingMenu();
        sm.setBehindOffset(250);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setShadowWidth(10);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        todayBtn = (ImageView)findViewById(R.id.today_btn);

        myMoments = (RelativeLayout)sm.getRootView().findViewById(R.id.my_moments_button);
        myMoments.setBackgroundResource(R.drawable.bg_section);
        profile = (RelativeLayout)sm.getRootView().findViewById(R.id.profile_button_volet);
        settings = (RelativeLayout)sm.getRootView().findViewById(R.id.settings_button_volet);
        //missingMoments = (RelativeLayout)sm.getRootView().findViewById(R.id.missing_button_volet);
        todayBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.btn_today);

        moments = new ArrayList<Moment>();
        //ListView
        momentsList = (ListView)findViewById(R.id.list_moments);
        momentsList.setSelector(android.R.color.transparent);
        adapter = new MomentsAdapter(this, R.layout.timeline_moment, moments);
        momentsList.setAdapter(adapter);
        momentsList.setOnScrollListener(new TImelineScrollListener());

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==myMoments.getId()){
                    myMoments.setBackgroundResource(R.drawable.bg_section);
                    toggle();

                }
                else if(v.getId()==profile.getId()){
                    profile.setBackgroundResource(R.drawable.bg_section);
                    Intent intent = new Intent(getApplication(), EditProfilActivity.class);
                    startActivity(intent);
                }
                else if(v.getId()==settings.getId()){
                    settings.setBackgroundResource(R.drawable.bg_section);
                    Intent intent = new Intent(getApplication(), SettingsActivity.class);
                    startActivity(intent);
                }
            }
        };

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(v.getId()==myMoments.getId()){
                    myMoments.setBackgroundResource(R.drawable.bg_section);
                }
                else if(v.getId()==profile.getId()){
                    profile.setBackgroundResource(R.drawable.bg_section);
                }
                else if(v.getId()==settings.getId()){
                    settings.setBackgroundResource(R.drawable.bg_section);
                }
                return false;
            }
        };

        myMoments.setOnClickListener(listener);
        myMoments.setOnTouchListener(touchListener);
        profile.setOnClickListener(listener);
        profile.setOnTouchListener(touchListener);
        settings.setOnClickListener(listener);
        settings.setOnTouchListener(touchListener);

        notifications = new ArrayList<Notification>();

        notifsListView = (ListView)sm.getRootView().findViewById(R.id.list_notifs);

        if(savedInstanceState == null){

            if(AppMoment.getInstance().checkInternet() == false){
                if(!DatabaseHelper.getMomentsFromDataBase().isEmpty()){
                    List<Moment> momentList = AppMoment.getInstance().momentDao.loadAll();
                    for (Moment moment : momentList){
                        AppMoment.getInstance().user.getMoments().add(moment);
                        //ajoutMoment(moment);
                    }
                }
            }

            else {
                dialog = ProgressDialog.show(this, null, "Téléchargement des Moments");

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

                                if(DatabaseHelper.getMomentByIdFromDataBase(momentTemp.getId()) == null){
                                    DatabaseHelper.addMoment(momentTemp);
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
            }
        }
        //After crash
        else{
            Log.e("TIMELINE", "LOSTTTTTT");
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            Long savedUserID = sharedPreferences.getLong("userID", -1);
            AppMoment.getInstance().user = DatabaseHelper.getUserByIdFromDataBase(savedInstanceState.getLong("userID"));
            Log.e("TIMELINE", "User id : "+savedInstanceState.getLong("userID"));

            dialog = ProgressDialog.show(this, null, "Téléchargement des Moments");

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
                            AppMoment.getInstance().user.addMoment(momentTemp);
                            moments.add(momentTemp);

                            if(DatabaseHelper.getMomentByIdFromDataBase(momentTemp.getId()) == null){
                                DatabaseHelper.addMoment(momentTemp);
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


        }
    }

    @Override
    public void onStart(){
        super.onStart();
        getNotifications();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_timeline, menu);

        RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(R.id.badge).getActionView();
        totalNotifText = (TextView) badgeLayout.findViewById(R.id.actionbar_notifcation_textview);
        notifProgress = (ProgressBar)badgeLayout.findViewById(R.id.progress_notifs);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                toggle();
                return true;

            case R.id.menu_creer:
                Intent intent = new Intent(this, CreationActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putLong("userID", AppMoment.getInstance().user.getId());
        // etc.
    }

    /*
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
                View v = momentsLayout.findViewById(CommonUtilities.longToInt(actuelMomentSelect));
                reduireMoment(v);
            }
            actuelMomentSelect = Long.valueOf(view.getId());
            grandirMoment(view);
        }

        //intentMoment = new Intent(this, MomentInfosActivity.class);
        //startActivity(intentMoment);
    }
    */

    /**
     * Fonction appel�e quand le bouton "Moments" est selectionn�
     * @param view
     */

    public void moments(View view){
        Log.d("TESTTTT", "premier bouton volet");
    }


    /**
     * Fonction appel�e quand le bouton profil est selectionn�
     * Lance la vue profil
     * @param view
     */

    public void profil(View view){
        Intent intent = new Intent(this, ProfilActivity.class);
        startActivity(intent);
    }


    /**
     * Fonction appel�e quand le bouton param�tres est selectionn�e
     * Lance la vue param�tre
     * @param view
     */

    public void parametres(View view){

    }



    /**
     * Fonction qui g�re l'ajout d'un moment � la timeline
     * @param id
     * @param nom
     */

    /*
    public void ajoutMoment(Moment moment){

        LinearLayout momentsLayout = (LinearLayout)findViewById(R.id.timeline_moments);
        RelativeLayout momentLayout = (RelativeLayout) inflater.inflate(R.layout.moment, null);
        momentLayout.setId(CommonUtilities.longToInt(moment.getId()));
        TextView nomMoment = (TextView)momentLayout.findViewById(R.id.nom_moment);
        nomMoment.setText(moment.getName());

        if(AppMoment.getInstance().checkInternet() == true){
            if(moment.getUrlCover()!= null){
                final ImageView imageMoment = (ImageView)momentLayout.findViewById(R.id.image_moment);
                moment.printCover(imageMoment, true);
            }
        }

        else {
            // TODO Check cache
            final ImageView imageMoment = (ImageView)momentLayout.findViewById(R.id.image_moment);
        }

        momentsLayout.addView(momentLayout);

        Resources r = getResources();
        float pxRatio = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
        momentLayout.getLayoutParams().height = (int)(110*pxRatio);
        momentLayout.getLayoutParams().width = (int)(110*pxRatio);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)momentLayout.getLayoutParams();
        params.setMargins(0, 10, 0, 50);
    }*/




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
     * L'utilisateur a touch� le bouton qui permet d'acc�der directement aux Photos
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
     * L'utilisateur a touch� le bouton qui permet d'acc�der directement aux Infos
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
     * L'utilisateur a touch� le bouton qui permet d'acc�der directement aux Chat
     * @param view
     */

    public void tapDirectChat(View view){

        intentMoment = new Intent(this, MomentInfosActivity.class);
        intentMoment.putExtra("precedente", "timeline");
        intentMoment.putExtra("position", 2);
        intentMoment.putExtra("id", actuelMomentSelect);
        startActivity(intentMoment);
    }


    public void notifications(View view){
        Log.e("NOTIFS", "HERE NOTIFS");
        Intent notifs = new Intent(this, NotificationsActivity.class);
        startActivity(notifs);
    }


    public void getNotifications(){


        MomentApi.get("notifications", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    totalNotifs = response.getInt("total_notifs");
                    totalNotifText.setText(""+totalNotifs);
                    totalNotifText.setVisibility(View.VISIBLE);
                    notifProgress.setVisibility(View.INVISIBLE);

                    JSONArray notifsObject = response.getJSONArray("notifications");

                    for(int i=0;i<notifsObject.length();i++){
                        Notification notif = new Notification();
                        Log.e("EX", notifsObject.getJSONObject(i).toString());
                        notif.setFromJson(notifsObject.getJSONObject(i));

                        //On prend que les notifs de photos ou chats (pas celle de followers)
                        if(notif.getTypeNotif()==2||notif.getTypeNotif()==3){
                            notifications.add(notif);
                        }

                    }

                    AppMoment.getInstance().user.setNotifications(notifications);


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


    public void selectedMoment(View view){
        Log.v("TIMELINE", ""+view.getTag());

        intentMoment = new Intent(this, MomentInfosActivity.class);
        intentMoment.putExtra("precedente", "timeline");
        intentMoment.putExtra("position", 1);
        intentMoment.putExtra("id", (Long)view.getTag());
        startActivity(intentMoment);

    }

    public void deleteMoment(View view){
        final Long momentId = (Long)view.getTag();
        final Moment momentToDel = AppMoment.getInstance().user.getMomentById(momentId);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle("Suppresion Moment");
        // set dialog message
        alertDialogBuilder
                .setMessage("Voulez vous vraiment supprimer ce Moment ? Cette action est irreversible !")
                .setCancelable(false)
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final ProgressDialog progressDialog = ProgressDialog.show(TimelineActivity.this, null, "Suppression en cours");

                        if(momentToDel.getUserId()==AppMoment.getInstance().user.getId()){
                            MomentApi.get("delmoment/"+momentId, null, new JsonHttpResponseHandler() {

                                @Override
                                public void onSuccess(JSONObject response) {
                                    for(int i=0;i<moments.size();i++){
                                        if(moments.get(i).getId().equals(momentToDel.getId())) moments.remove(i);
                                    }
                                    if(AppMoment.getInstance().user.getMoments().remove(momentToDel)) Log.v("TIMELINE", "REMOVED INSTANCE");
                                    //TODO : REMOVE en base
                                    adapter.notifyDataSetChanged();

                                    progressDialog.dismiss();

                                }

                                @Override
                                public void onFailure(Throwable error, String content) {
                                    System.out.println(content);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    public void goToToday(Boolean smooth){

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, r.getDisplayMetrics());


        if(smooth){
            if(android.os.Build.VERSION.SDK_INT > 10) momentsList.smoothScrollToPositionFromTop(getNearestDate(), (height/2)-(int)px);
            else momentsList.setSelectionFromTop(getNearestDate(), height/2);
        }
        else momentsList.setSelectionFromTop(getNearestDate(), height/2);
    }

    /**
     * Find the Moment which is the closest from today
     * @return
     */

    public int getNearestDate() {
        Date currentDate = new Date();
        long minDiff = -1, currentTime = currentDate.getTime();
        Date minDate = null;
        int positionDate=-1;
        for(int i=0;i<moments.size();i++){
            long diff = Math.abs(currentTime - moments.get(i).getDateDebut().getTime());
            if ((minDiff == -1) || (diff < minDiff)) {
                minDiff = diff;
                minDate = moments.get(i).getDateDebut();
                positionDate = i;
            }
        }
        return positionDate;
    }

    public void today(View view){
        goToToday(true);
    }

    public void rotateToday(float deg){
        Matrix matrix=new Matrix();
        matrix.postRotate(deg);
        Bitmap rot = Bitmap.createBitmap(todayBitmap, 0, 0, todayBitmap.getWidth(), todayBitmap.getHeight(),
                matrix, true);
        todayBtn.setImageBitmap(rot);
    }


    /**
     * Background task that will load the next moments
     */


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

            //We load futur moments
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                if(!allFutur){
                    Log.d(TAG, "LOAD FUTUR MOMENTS");
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String date = df.format(moments.get(moments.size()-1).getDateDebut());
                    MomentApi.get("momentsafter/"+date+"/1", null, new JsonHttpResponseHandler() {

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


                                    if(DatabaseHelper.getMomentByIdFromDataBase(momentTemp.getId()) == null){
                                        DatabaseHelper.addMoment(momentTemp);
                                    }

                                }

                                loading = false;

                                if(nbMoments==0) allFutur = true;
                                else adapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Throwable error, String content) {
                            Log.e("TIMELINE", content);
                            Toast.makeText(getApplicationContext(),"Echec", Toast.LENGTH_LONG).show();
                        }

                    });

                    loading = true;
                }

            }
            //We load old moments
            else if (!loading && (firstVisibleItem) <= 3) {
                if(!allPast){
                    Log.d(TAG, "Load PAST MOMENTS");
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String date = df.format(moments.get(0).getDateDebut());
                    MomentApi.get("momentsafter/"+date+"/0", null, new JsonHttpResponseHandler() {

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
                                    moments.add(0, momentTemp);


                                    if(DatabaseHelper.getMomentByIdFromDataBase(momentTemp.getId()) == null){
                                        DatabaseHelper.addMoment(momentTemp);
                                    }

                                }

                                if(nbMoments==0) allPast = true;
                                else {
                                    adapter.notifyDataSetChanged();
                                    momentsList.setSelection(nbMoments+1);
                                }

                                Toast.makeText(getApplicationContext(), "Moments supp : " + nbMoments, Toast.LENGTH_LONG).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Throwable error, String content) {
                            Log.e("TIMELINE", content);
                            Toast.makeText(getApplicationContext(),"Echec", Toast.LENGTH_LONG).show();
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



}
