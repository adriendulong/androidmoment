package com.moment.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.activities.MomentInfosActivity;
import com.moment.activities.MomentInfosActivity.Exchanger;
import com.moment.classes.MomentApi;
import com.moment.classes.PositionOverlay;
import com.moment.classes.RoundTransformation;
import com.moment.models.Moment;

import com.moment.models.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class InfosFragment extends Fragment {

    private static final int PICK_CAMERA_COVER = 1;
    //Different states
    private final int COMING = 2;
    private final int NOT_COMING = 3;
    private final int MAYBE = 5;
    private final int OWNER = 0;
    private final int ADMIN = 1;
    private final int UNKOWN = 4;
    private final String[] mois = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Aout", "Septembre", "Aout", "Novembre", "Décembre"};
    private final String[] jours = {"Dimanche", "Lundi", "Mardi","Mercredi", "Jeudi", "Vendredi", "Samedi"};
    private final Transformation roundTrans = new RoundTransformation();
    private GoogleMap mMap;
    private Long momentId;
    private int stateAnwser;
    private Moment moment;
    //All view elements
    private TextView titreText, flTitreText, descriptionText, adresse, dateDebutText, dateFinText, guests_number, guests_coming, guests__not_coming, firstname, lastname;
    private ImageView image_cover, owner_picture;
    private RelativeLayout modifLayout;
    private ImageView blocRSVP;
    //State buttons
    private ImageButton maybeButton, goingButton, notGoigButton, addGuests;

    public static void navigateToLocation (double latitude, double longitude, MapView mv, Context context) {
        GeoPoint p = new GeoPoint((int) latitude, (int) longitude); //new GeoPoint

        //Les Overlays
        List<Overlay> mapOverlays = Exchanger.mMapView.getOverlays();
        Drawable drawable = context.getResources().getDrawable(R.drawable.picto_o);
        PositionOverlay itemizedoverlay = new PositionOverlay(drawable, context);

        OverlayItem overlayitem = new OverlayItem(p, "Hola, Mundo!", "I'm in Mexico City!");

        itemizedoverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedoverlay);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_infos, container, false);

        if (view != null) {
            titreText = (TextView) view.findViewById(R.id.titre_moment);
            descriptionText = (TextView) view.findViewById(R.id.infos_moment_description);
            flTitreText = (TextView) view.findViewById(R.id.fl_titre_moment);
            adresse = (TextView) view.findViewById(R.id.infos_moment_adresse);

            maybeButton     = (ImageButton) view.findViewById(R.id.maybe_button);
            goingButton     = (ImageButton) view.findViewById(R.id.going_button);
            notGoigButton   = (ImageButton) view.findViewById(R.id.not_going_button);
            addGuests = (ImageButton) view.findViewById(R.id.add_guests);

            dateDebutText = (TextView) view.findViewById(R.id.infos_moment_date_debut);
            dateFinText = (TextView) view.findViewById(R.id.infos_moment_date_fin);

            guests_number = (TextView) view.findViewById(R.id.guests_number);
            guests_coming = (TextView) view.findViewById(R.id.guests_coming);
            guests__not_coming = (TextView) view.findViewById(R.id.guests_not_coming);
            modifLayout = (RelativeLayout) view.findViewById(R.id.modif_layout);

            blocRSVP = (ImageView) view.findViewById(R.id.bloc_rsvp);



            image_cover = (ImageView) view.findViewById(R.id.photo_moment);
            owner_picture = (ImageView) view.findViewById(R.id.photo_owner);
            firstname = (TextView) view.findViewById(R.id.firstname_owner);
            lastname = (TextView) view.findViewById(R.id.lastname_owner);
        }

        if(((MomentInfosActivity)getActivity()).getMomentId()!=null){
            this.momentId = ((MomentInfosActivity)getActivity()).getMomentId();
            moment = AppMoment.getInstance().user.getMomentById(momentId);
            initInfos();
        }

        return view;
    }

    private void modifyPhotoMoment(Bitmap photo) {
        ImageView photoMoment = (ImageView)getActivity().findViewById(R.id.photo_moment);
        photoMoment.setImageBitmap(photo);
    }

    public void touchedPhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, PICK_CAMERA_COVER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CAMERA_COVER) {
            Bundle extras = data.getExtras();
            Bitmap mImageBitmap;
            if (extras != null) {
                mImageBitmap = (Bitmap) extras.get("data");
                modifyPhotoMoment(mImageBitmap);
            }
        }
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                double lat;
                double lon;
                Geocoder geocoder = new Geocoder(getActivity());

                try {
                    List<Address> addresses =  geocoder.getFromLocationName(AppMoment.getInstance().user.getMomentById(momentId).getAdresse(), 1);

                    if (addresses.size() > 0) {



                        Address x = addresses.get(0);
                        lat = x.getLatitude();
                        lon = x.getLongitude();

                        final LatLng ADRESS = new LatLng(lat,lon);

                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ADRESS, 15));

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(ADRESS)      // Sets the center of the map to Mountain View
                                .zoom(10)                   // Sets the zoom
                                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                                .build();

                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void maybeRsvp(){
        System.out.println("MAYBE");
        int oldState = stateAnwser;

        if(moment.getState()!=OWNER){
            if(stateAnwser!=MAYBE){
                stateAnwser = MAYBE;
                moment.setState(MAYBE);
                updateRSVPBloc();
                updateStateServer(oldState, MAYBE);
            }
        }

    }

    public void goingRsvp(){
        System.out.println("Going");
        int oldState = stateAnwser;

        if(moment.getState()!=OWNER){
            if(stateAnwser!=COMING){
                stateAnwser = COMING;
                moment.setState(COMING);
                updateRSVPBloc();
                updateStateServer(oldState, COMING);
            }
        }

    }

    public void notRsvp(){
        System.out.println("Not going");
        int oldState = stateAnwser;

        if(moment.getState()!=OWNER){
            if(stateAnwser!=NOT_COMING){
                stateAnwser = NOT_COMING;
                moment.setState(NOT_COMING);
                updateRSVPBloc();
                updateStateServer(oldState, NOT_COMING);
            }
        }

    }

    private void updateRSVPBloc(){

        if((moment.getState()==COMING)||(moment.getState()==OWNER)){
            goingButton.setSelected(true);
            goingButton.setImageResource(R.drawable.picto_yes_down);
            maybeButton.setSelected(false);
            maybeButton.setImageResource(R.drawable.picto_maybe);
            notGoigButton.setSelected(false);
            notGoigButton.setImageResource(R.drawable.picto_no);
        }
        else if (moment.getState()==NOT_COMING){
            notGoigButton.setSelected(true);
            notGoigButton.setImageResource(R.drawable.picto_no_down);
            maybeButton.setSelected(false);
            maybeButton.setImageResource(R.drawable.picto_maybe);
            goingButton.setSelected(false);
            goingButton.setImageResource(R.drawable.picto_valid);
        }
        else if (moment.getState()==MAYBE){
            maybeButton.setSelected(true);
            maybeButton.setImageResource(R.drawable.picto_maybe_down);
            notGoigButton.setSelected(false);
            notGoigButton.setImageResource(R.drawable.picto_no);
            goingButton.setSelected(false);
            goingButton.setImageResource(R.drawable.picto_valid);
        }

    }

    private void updateStateServer(final int oldState, int newState){

        MomentApi.get("state/"+momentId+"/"+newState, null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject response) {
                Toast.makeText(getActivity(), "Réponse mise à jour", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable error, String content) {
                Toast.makeText(getActivity(), "Problème de connexion lors de l'envoie de votre réponse au serveur. Veuillez ressayer plus tard.", Toast.LENGTH_SHORT).show();
                stateAnwser = oldState;
                moment.setState(oldState);
                updateRSVPBloc();

            }
        });

    }

    /**
     * Function called when the activity got the Moment
     */

    public void createFragment(Long momentId){
        this.momentId = momentId;
        moment = AppMoment.getInstance().user.getMomentById(momentId);

        //We init only if we already built the view, otherwise the Start function will do it
        if(titreText!=null) initInfos();

    }

    /**
     * Function that takes care of building the infos fragment when we have the moment infos
     */

    private void initInfos(){
        titreText.setText(moment.getName().substring(1));
        flTitreText.setText(moment.getName().substring(0,1));
        descriptionText.setText(moment.getDescription());
        adresse.setText(moment.getAdresse());

        //Update State
        updateRSVPBloc();

        //Dates
        GregorianCalendar dateDebutCalendar = new GregorianCalendar(Locale.getDefault());
        dateDebutCalendar.setTime(moment.getDateDebut());
        Calendar dateFinCalendar = Calendar.getInstance();
        dateFinCalendar.setTime(moment.getDateFin());

        dateDebutText.setText(""+jours[dateDebutCalendar.get(Calendar.DAY_OF_WEEK)-1]+" "+dateDebutCalendar.get(Calendar.DAY_OF_MONTH)+" "+mois[dateDebutCalendar.get(Calendar.MONTH)]);
        dateFinText.setText(""+jours[dateFinCalendar.get(Calendar.DAY_OF_WEEK)-1]+" "+dateFinCalendar.get(Calendar.DAY_OF_MONTH)+" "+mois[dateFinCalendar.get(Calendar.MONTH)]);

        //Cover
        Picasso.with(getActivity()).load(moment.getUrlCover()).into(image_cover);

        //Owner
        if(moment.getUser()!=null){
            if(moment.getUser().getPictureProfileUrl()!=null) Picasso.with(getActivity()).load(moment.getUser().getPictureProfileUrl()).transform(roundTrans).into(owner_picture);
            firstname.setText(AppMoment.getInstance().user.getMomentById(momentId).getUser().getFirstName());
            lastname.setText(AppMoment.getInstance().user.getMomentById(momentId).getUser().getLastName());
        }

        //Guests
        if(AppMoment.getInstance().user.getMomentById(momentId).getGuestNumber()>0){
            guests_number.setText(""+AppMoment.getInstance().user.getMomentById(momentId).getGuestNumber());
            guests_coming.setText(""+AppMoment.getInstance().user.getMomentById(momentId).getGuestComing());
            guests__not_coming.setText(""+AppMoment.getInstance().user.getMomentById(momentId).getGuestNotComing());
        }

        //If it is not the owner we have to hide some stuff
        if(!canInvite(AppMoment.getInstance().user)){
            addGuests.setVisibility(View.INVISIBLE);
            RelativeLayout .LayoutParams params = (RelativeLayout.LayoutParams)blocRSVP.getLayoutParams();
            if (params != null) {
                params.setMargins(0, 0, 5, 0); //substitute parameters for left, top, right, bottom
            }
            blocRSVP.setLayoutParams(params);
        }

        if(AppMoment.getInstance().user.getId()!=moment.getUserId()){
            modifLayout.setVisibility(View.INVISIBLE);
        }

        //Map
        setUpMapIfNeeded();
    }

    private Boolean canInvite(User user){
        int PUBLIC = 2;
        return moment.getPrivacy() == PUBLIC || moment.getIsOpenInvit() || user.getId() == moment.getUserId();
    }





}
