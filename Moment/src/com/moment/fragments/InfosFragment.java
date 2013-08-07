package com.moment.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.*;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
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
import com.moment.classes.InvitationsAdapter;
import com.moment.classes.MomentApi;
import com.moment.classes.PositionOverlay;
import com.moment.classes.RoundTransformation;
import com.moment.models.Moment;

import com.moment.models.Photo;
import com.moment.models.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

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
    private TextView titreText, flTitreText, descriptionText, adresse, dateDebutText, dateFinText, heureDebutText, heureFinText, guests_number, guests_coming, guests__not_coming, firstname, lastname, textRSVP;
    private ImageView image_cover, owner_picture;
    private RelativeLayout modifLayout;
    private ImageView blocRSVP;
    //State buttons
    private ImageButton maybeButton, goingButton, notGoigButton, addGuests;
    private GridView gridPreviewPhotos;
    private ArrayList<Photo> photos;
    private ImageAdapter imageAdapter;
    private View view;
    private WebView mapWeb;
    private Button delMoment;
    private Tracker mGaTracker;
    private GoogleAnalytics mGaInstance;

    private static final int REAUTH_ACTIVITY_CODE = 100;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the GoogleAnalytics singleton. Note that the SDK uses
        // the application context to avoid leaking the current context.
        mGaInstance = GoogleAnalytics.getInstance(getActivity());

        // Use the GoogleAnalytics singleton to get a Tracker.
        mGaTracker = mGaInstance.getTracker(AppMoment.getInstance().GOOGLE_ANALYTICS); // Placeholder tracking ID.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_infos, container, false);

        //Init
        photos = new ArrayList<Photo>();

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
            heureDebutText = (TextView)view.findViewById(R.id.infos_moment_heure_debut);
            heureFinText = (TextView)view.findViewById(R.id.infos_moment_heure_fin);

            guests_number = (TextView) view.findViewById(R.id.guests_number);
            guests_coming = (TextView) view.findViewById(R.id.guests_coming);
            guests__not_coming = (TextView) view.findViewById(R.id.guests_not_coming);
            modifLayout = (RelativeLayout) view.findViewById(R.id.modif_layout);

            blocRSVP = (ImageView) view.findViewById(R.id.bloc_rsvp);
            textRSVP = (TextView)view.findViewById(R.id.text_rsvp);

            delMoment = (Button)view.findViewById(R.id.del_moment);


            image_cover = (ImageView) view.findViewById(R.id.photo_moment);
            owner_picture = (ImageView) view.findViewById(R.id.photo_owner);
            firstname = (TextView) view.findViewById(R.id.firstname_owner);
            lastname = (TextView) view.findViewById(R.id.lastname_owner);

            gridPreviewPhotos = (GridView)view.findViewById(R.id.grid_preview_photos);
            mapWeb = (WebView)view.findViewById(R.id.map_web);
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
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
        }
    }

    public void facebook(){
        Session.openActiveSession(getActivity(),true, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if(session.isOpened()){
                    List<String> permissions = session.getPermissions();
                    if (!permissions.containsAll(PERMISSIONS)) {
                        requestPublishPermissions(session);
                    }
                    postEvent();
                }
            }
        });
    }


    void requestPublishPermissions(Session session) {
        if (session != null) {
            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS)
                    .setDefaultAudience(SessionDefaultAudience.FRIENDS)
                    .setRequestCode(REAUTH_ACTIVITY_CODE);
            session.requestNewPublishPermissions(newPermissionsRequest);
        }
    }

    private void postEvent() {
        Bundle fbParams = new Bundle();
        fbParams.putString("evenement", moment.getUniqueUrl());
        Request postScoreRequest = new Request(Session.getActiveSession(),
                "me/appmoment:participe",
                fbParams,
                HttpMethod.POST,
                new Request.Callback() {

                    @Override
                    public void onCompleted(Response response) {
                        FacebookRequestError error = response.getError();
                        if (error != null) {
                            Log.e("FAIL", error.toString());
                        } else {
                            Log.i("WIN", response.toString());
                        }
                    }
                });
        Request.executeBatchAsync(postScoreRequest);
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
                facebook();
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
            textRSVP.setText(getResources().getString(R.string.rsvp_coming));
        }
        else if (moment.getState()==NOT_COMING){
            notGoigButton.setSelected(true);
            notGoigButton.setImageResource(R.drawable.picto_no_down);
            maybeButton.setSelected(false);
            maybeButton.setImageResource(R.drawable.picto_maybe);
            goingButton.setSelected(false);
            goingButton.setImageResource(R.drawable.picto_valid);
            textRSVP.setText(getResources().getString(R.string.rsvp_not_coming));
        }
        else if (moment.getState()==MAYBE){
            maybeButton.setSelected(true);
            maybeButton.setImageResource(R.drawable.picto_maybe_down);
            notGoigButton.setSelected(false);
            notGoigButton.setImageResource(R.drawable.picto_no);
            goingButton.setSelected(false);
            goingButton.setImageResource(R.drawable.picto_valid);
            textRSVP.setText(getResources().getString(R.string.rsvp_maybe));
        }
        else if(moment.getState()==UNKOWN){
            Toast.makeText(getActivity(), "Première visite sur le moment", Toast.LENGTH_SHORT).show();
            maybeRsvp();
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
        heureDebutText.setText(""+dateDebutCalendar.get(Calendar.HOUR)+"H"+dateDebutCalendar.get(Calendar.MINUTE));
        heureFinText.setText(""+dateFinCalendar.get(Calendar.HOUR)+"H"+dateFinCalendar.get(Calendar.MINUTE));

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
        else{
            delMoment.setVisibility(View.VISIBLE);
        }

        imageAdapter = new ImageAdapter(view.getContext(), photos);
        gridPreviewPhotos.setAdapter(imageAdapter);

        //Map
        //setUpMapIfNeeded();
        if(AppMoment.getInstance().checkInternet()){
            loadMap();
        }

    }

    private Boolean canInvite(User user){
        int PUBLIC = 2;
        return moment.getPrivacy() == PUBLIC || moment.getIsOpenInvit() || user.getId() == moment.getUserId();
    }


    /**
     * Adapter for the grid view
     * Grid view which contains the previews of the photos
     */

    public class ImageAdapter extends BaseAdapter {

        private final Context context;
        private ArrayList<Photo> photos;

        public ImageAdapter(Context context, ArrayList<Photo> photos) {
            this.context = context;
            this.photos = photos;
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View  getView(int position, View convertView, ViewGroup parent) {

            ImageView imageView;
            float pxImage = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics());
            float pxBitmap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

            if(convertView == null) {
                imageView = new ImageView(context);

                imageView.setLayoutParams(new GridView.LayoutParams((int)pxImage, (int)pxImage));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setCropToPadding(true);
                imageView.setPadding(5, 5, 5, 5);
                imageView.setBackground(getResources().getDrawable(R.drawable.bg));
            }

            else {
                imageView = (ImageView) convertView;
            }

            Picasso.with(context).load(photos.get(position).getUrlThumbnail()).resize((int)pxBitmap,(int)pxBitmap).centerCrop().placeholder(R.drawable.picto_photo_vide).into(imageView);

            return imageView;
        }
    }


    /**
     * Function to update the photos
     */

    public void updatePhotos(ArrayList<Photo> photosNew){

        for(int i=0;i<numberOfPhotos();i++){
            if(photosNew.size()>i) photos.add(photosNew.get(i));
        }
        Log.v("INFOSFRAGMENT", "Nombre de photos : "+numberOfPhotos());
        gridPreviewPhotos.setFocusable(false);
        imageAdapter.notifyDataSetChanged();
    }

    private int numberOfPhotos(){
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();  // deprecated

        float pxImage = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());

        return Math.round(width / pxImage);
    }

    private void loadMap(){
        double lat;
        double lon;
        Geocoder geocoder = new Geocoder(getActivity());

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        float density = getActivity().getApplicationContext().getResources().getDisplayMetrics().density;
        int realWidth =  Math.round((float)width / density);

        try {
            List<Address> addresses =  geocoder.getFromLocationName(AppMoment.getInstance().user.getMomentById(momentId).getAdresse(), 1);

            if (addresses.size() > 0) {
                Address x = addresses.get(0);
                lat = x.getLatitude();
                lon = x.getLongitude();



                String mapdBoxUrl = "http://a.tiles.mapbox.com/v3/appmoment.map-62jk3rrs/pin-s-star-stroked+ff793d("+lon+","+lat+")/"+lon+","+lat+",8/"+realWidth+"x"+60+".png";
                mapWeb.loadUrl(mapdBoxUrl);

            }
            else{
                String mapdBoxUrl = "http://a.tiles.mapbox.com/v3/appmoment.map-62jk3rrs/0,0,1/"+realWidth+"x"+60+".png";
                mapWeb.loadUrl(mapdBoxUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        // Send a screen view when the Activity is displayed to the user.
        mGaTracker.sendView("/InfosFragment");
    }





}
