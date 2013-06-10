package com.moment.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.*;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.animations.TimelineAnimation;
import com.moment.classes.CommonUtilities;
import com.moment.classes.DatabaseHelper;
import com.moment.classes.MomentApi;
import com.moment.models.Moment;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TimelineActivity extends SlidingActivity {

    private Intent intentMoment;
    private LayoutInflater inflater;
    private Long actuelMomentSelect = Long.parseLong("-1");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timeline);
        setBehindContentView(R.layout.volet_timeline);
        SlidingMenu sm = getSlidingMenu();
        sm.setBehindOffset(100);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setShadowWidth(10);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
                                System.out.println(momentTemp.getName());
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_timeline, menu);
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
     * Fonction appelŽe quand le bouton "Moments" est selectionnŽ
     * @param view
     */

    public void moments(View view){
        Log.d("TESTTTT", "premier bouton volet");
    }


    /**
     * Fonction appelŽe quand le bouton profil est selectionnŽ
     * Lance la vue profil
     * @param view
     */

    public void profil(View view){
        Intent intent = new Intent(this, ProfilActivity.class);
        startActivity(intent);
    }


    /**
     * Fonction appelŽe quand le bouton paramtres est selectionnŽe
     * Lance la vue paramtre
     * @param view
     */

    public void parametres(View view){

    }



    /**
     * Fonction qui gre l'ajout d'un moment ˆ la timeline
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
     * L'utilisateur a touchŽ le bouton qui permet d'accder directement aux Photos
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
     * L'utilisateur a touchŽ le bouton qui permet d'accder directement aux Infos
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
     * L'utilisateur a touchŽ le bouton qui permet d'accder directement aux Chat
     * @param view
     */

    public void tapDirectChat(View view){

        intentMoment = new Intent(this, MomentInfosActivity.class);
        intentMoment.putExtra("precedente", "timeline");
        intentMoment.putExtra("position", 2);
        intentMoment.putExtra("id", actuelMomentSelect);
        startActivity(intentMoment);
    }
}
