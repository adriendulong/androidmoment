package com.moment.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.animations.TimelineAnimation;
import com.moment.classes.CommonUtilities;
import com.moment.classes.DatabaseHelper;
import com.moment.classes.MomentApi;
import com.moment.classes.NotificationsAdapter;
import com.moment.models.Moment;
import com.moment.models.Notification;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends SlidingActivity {

    private Intent intentMoment;
    private LayoutInflater inflater;
    private Long actuelMomentSelect = Long.parseLong("-1");
    private RelativeLayout myMoments, news, profile, settings, missingMoments;
    private ListView notifsListView;
    private NotificationsAdapter adapter;
    private ArrayList<Notification> notifications;
    private TextView totalNotifText;
    private ProgressBar notifProgress;
    private int totalNotifs, nbNotifs, nbInvit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        setContentView(R.layout.activity_timeline);
        setBehindContentView(R.layout.volet_timeline);
        SlidingMenu sm = getSlidingMenu();
        sm.setBehindOffset(250);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setShadowWidth(10);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);



        //We get all the relativeLayout that are buttons
        myMoments = (RelativeLayout)sm.getRootView().findViewById(R.id.my_moments_button);
        myMoments.setBackgroundResource(R.drawable.bg_section);
        news = (RelativeLayout)sm.getRootView().findViewById(R.id.news_button_volet);
        profile = (RelativeLayout)sm.getRootView().findViewById(R.id.profile_button_volet);
        settings = (RelativeLayout)sm.getRootView().findViewById(R.id.settings_button_volet);
        missingMoments = (RelativeLayout)sm.getRootView().findViewById(R.id.missing_button_volet);


        //OnClickListener
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==myMoments.getId()){
                    myMoments.setBackgroundResource(R.drawable.bg_section);
                }
                else if(v.getId()==news.getId()){
                    news.setBackgroundResource(R.drawable.bg_section);
                }
                else if(v.getId()==profile.getId()){
                    profile.setBackgroundResource(R.drawable.bg_section);
                }
                else if(v.getId()==settings.getId()){
                    settings.setBackgroundResource(R.drawable.bg_section);
                    Intent intent = new Intent(getApplication(), SettingsActivity.class);
                    startActivity(intent);
                }

                else if(v.getId()==missingMoments.getId()){
                    missingMoments.setBackgroundResource(R.drawable.bg_section);
                }
            }
        };

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(v.getId()==myMoments.getId()){
                    myMoments.setBackgroundResource(R.drawable.bg_section);
                }
                else if(v.getId()==news.getId()){
                    news.setBackgroundResource(R.drawable.bg_section);
                }
                else if(v.getId()==profile.getId()){
                    profile.setBackgroundResource(R.drawable.bg_section);
                }
                else if(v.getId()==settings.getId()){
                    settings.setBackgroundResource(R.drawable.bg_section);
                }
                else if(v.getId()==missingMoments.getId()){
                    missingMoments.setBackgroundResource(R.drawable.bg_section);
                }
                return false;
            }
        };



        //Associate buttons with listeners
        myMoments.setOnClickListener(listener);
        myMoments.setOnTouchListener(touchListener);
        news.setOnClickListener(listener);
        news.setOnTouchListener(touchListener);
        profile.setOnClickListener(listener);
        profile.setOnTouchListener(touchListener);
        settings.setOnClickListener(listener);
        settings.setOnTouchListener(touchListener);
        missingMoments.setOnClickListener(listener);
        missingMoments.setOnTouchListener(touchListener);

        //Initialize the notifications list
        notifications = new ArrayList<Notification>();

        notifsListView = (ListView)sm.getRootView().findViewById(R.id.list_notifs);
        //adapter = new NotificationsAdapter(this, R.layout.notifs_cell, notifications);
        //notifsListView.setAdapter(adapter);




        if(savedInstanceState == null){

            if(AppMoment.getInstance().checkInternet() == false){
                if(!DatabaseHelper.getMomentsFromDataBase().isEmpty()){
                    List<Moment> momentList = AppMoment.getInstance().momentDao.loadAll();
                    for (Moment moment : momentList){
                        AppMoment.getInstance().user.getMoments().add(moment);
                        ajoutMoment(moment);
                    }
                }
            }

            else {
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
                                ajoutMoment(momentTemp);

                                if(DatabaseHelper.getMomentByIdFromDataBase(momentTemp.getId()) == null){
                                    DatabaseHelper.addMoment(momentTemp);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
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
    }




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



}
