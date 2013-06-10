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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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
import com.moment.activities.MomentInfosActivity.Exchanger;
import com.moment.classes.MomentApi;
import com.moment.classes.PositionOverlay;
import com.moment.models.Moment;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class InfosFragment extends Fragment {

    //Different states
    private int COMING = 2;
    private int NOT_COMING = 3;
    private int MAYBE = 5;
    private int OWNER = 0;
    private int ADMIN = 1;
    private int UNKOWN = 4;
	
	private String[] mois = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Aout", "Septembre", "Aout", "Novembre", "Décembre"};
	private String[] jours = {"Dimanche", "Lundi", "Mardi","Mercredi", "Jeudi", "Vendredi", "Samedi"};
	static final int PICK_CAMERA_COVER = 1;
	private GoogleMap mMap;
    private Long momentId;
    private int stateAnwser;
    private Moment moment;

    //State buttons
    private ImageButton maybeButton, goingButton, notGoigButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            momentId = getActivity().getIntent().getLongExtra("id", 1);
            moment = AppMoment.getInstance().user.getMomentById(momentId);
        }
	}

    @Override
    public void onPause(){
        super.onPause();
        Log.e("PAUSE", "Chats");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.e("RESUME", "Chats");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.e("STOP", "Chats");
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.e("START", "Chats");
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_infos, container, false);
		Log.d("CREATE", "create");
		
		//Problem black derriere carte
		ViewGroup mapHost = (ViewGroup)view.findViewById(R.id.mapHost);
		mapHost.requestTransparentRegion(mapHost);
		
		setUpMapIfNeeded();
		

		
		//Si elle existe dej� on supprime la map
		/*final ViewGroup parent = (ViewGroup) Exchanger.mMapView.getParent();
		if (parent != null) parent.removeView(Exchanger.mMapView);
		
		
		On initialise et insere la map
		Exchanger.mMapView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		Exchanger.mMapView.setId(1111111);
		Exchanger.mMapView.setClickable(false);
		LinearLayout containerMap = (LinearLayout)view.findViewById(R.id.mapContainer);
		containerMap.addView(Exchanger.mMapView);*/
		
		
		
		
		
		
		//On initialise les �l�ments graphiques
		
		//Le Titre
		TextView titreText = (TextView)view.findViewById(R.id.titre_moment);
		titreText.setText(AppMoment.getInstance().user.getMomentById(momentId).getName().substring(1));
		TextView flTitreText = (TextView)view.findViewById(R.id.fl_titre_moment);
		flTitreText.setText(AppMoment.getInstance().user.getMomentById(momentId).getName().substring(0,1));
		
		//La description
		TextView descriptionText = (TextView)view.findViewById(R.id.infos_moment_description);
		descriptionText.setText(AppMoment.getInstance().user.getMomentById(momentId).getDescription());
		
		//L'adresse
		TextView adresse = (TextView)view.findViewById(R.id.infos_moment_adresse);
		adresse.setText(AppMoment.getInstance().user.getMomentById(momentId).getAdresse());

        //Le state
        maybeButton = (ImageButton)view.findViewById(R.id.maybe_button);
        goingButton = (ImageButton)view.findViewById(R.id.going_button);
        notGoigButton = (ImageButton)view.findViewById(R.id.not_going_button);
        //Buttons update
        updateRSVPBloc();

		
		//Dates
		
		//Date de debut
		GregorianCalendar dateDebutCalendar = new GregorianCalendar(Locale.getDefault());
		dateDebutCalendar.setTime(AppMoment.getInstance().user.getMomentById(momentId).getDateDebut());
		
		Calendar dateFinCalendar = Calendar.getInstance();
		dateFinCalendar.setTime(AppMoment.getInstance().user.getMomentById(momentId).getDateFin());
		
		
		TextView dateDebutText = (TextView)view.findViewById(R.id.infos_moment_date_debut);
		dateDebutText.setText(""+jours[dateDebutCalendar.get(Calendar.DAY_OF_WEEK)-1]+" "+dateDebutCalendar.get(Calendar.DAY_OF_MONTH)+" "+mois[dateDebutCalendar.get(Calendar.MONTH)]);
		
		TextView dateFinText = (TextView)view.findViewById(R.id.infos_moment_date_fin);
		dateFinText.setText(""+jours[dateFinCalendar.get(Calendar.DAY_OF_WEEK)-1]+" "+dateFinCalendar.get(Calendar.DAY_OF_MONTH)+" "+mois[dateFinCalendar.get(Calendar.MONTH)]);
		
		//Nombres d'invites
		if(AppMoment.getInstance().user.getMomentById(momentId).getGuestNumber()>0){
			TextView guests_number = (TextView)view.findViewById(R.id.guests_number);
			guests_number.setText(""+AppMoment.getInstance().user.getMomentById(momentId).getGuestNumber());
			
			TextView guests_coming = (TextView)view.findViewById(R.id.guests_coming);
			guests_coming.setText(""+AppMoment.getInstance().user.getMomentById(momentId).getGuestComing());
			
			TextView guests__not_coming = (TextView)view.findViewById(R.id.guests_not_coming);
			guests__not_coming.setText(""+AppMoment.getInstance().user.getMomentById(momentId).getGuestNotComing());
		}

		
		
		//Image
		if(AppMoment.getInstance().user.getMomentById(momentId).getKeyBitmap()!=null){
			Bitmap image_cover_bmp = AppMoment.getInstance().getBitmapFromMemCache(AppMoment.getInstance().user.getMomentById(momentId).getKeyBitmap());
			ImageView image_cover = (ImageView)view.findViewById(R.id.photo_moment);
			image_cover.setImageBitmap(image_cover_bmp);
		}
		
		if(AppMoment.getInstance().user.getMomentById(momentId).getUser()!=null){
			final ImageView owner_picture = (ImageView)view.findViewById(R.id.photo_owner);
			
			
			if(AppMoment.getInstance().user.getMomentById(momentId).getUser().getPictureProfileUrl()!=null) AppMoment.getInstance().user.getMomentById(momentId).getUser().printProfilePicture(owner_picture, true);
			
			TextView firstname = (TextView)view.findViewById(R.id.firstname_owner);
			firstname.setText(AppMoment.getInstance().user.getMomentById(momentId).getUser().getFirstName());
			
			TextView lastname = (TextView)view.findViewById(R.id.lastname_owner);
			lastname.setText(AppMoment.getInstance().user.getMomentById(momentId).getUser().getLastName());
		}
		
		//TextView hashtag = (TextView)view.findViewById(R.id.hashtag);
		//hashtag.setText(Exchanger.moment.getHashtag());
		
		return view;
	}

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    }

	

	
	/**
	  * Navigates a given MapView to the specified Longitude and Latitude
	  */
    public static void navigateToLocation (double latitude, double longitude, MapView mv, Context context) {
    GeoPoint p = new GeoPoint((int) latitude, (int) longitude); //new GeoPoint

    //Les Overlays
    List<Overlay> mapOverlays = Exchanger.mMapView.getOverlays();
    Drawable drawable = context.getResources().getDrawable(R.drawable.picto_o);
    PositionOverlay itemizedoverlay = new PositionOverlay(drawable, context);

    OverlayItem overlayitem = new OverlayItem(p, "Hola, Mundo!", "I'm in Mexico City!");

    itemizedoverlay.addOverlay(overlayitem);
    mapOverlays.add(itemizedoverlay);

    /*mv.getMap()
    mv.displayZoomControls(true); //display Zoom (seems that it doesn't work yet)
    MapController mc = mv.getController();
    mc.animateTo(p); //move map to the given point
    int zoomlevel = mv.getMaxZoomLevel(); //detect maximum zoom level
    mc.setZoom(zoomlevel - 6); //zoom
    mv.setSatellite(false); //display only "normal" mapview	*/
    }


    /**
    * On remplace la photo de couverture du moment
    * @param photo
    */

    public void modifyPhotoMoment(Bitmap photo) {

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
            Bitmap mImageBitmap = (Bitmap) extras.get("data");
            modifyPhotoMoment(mImageBitmap);
        }
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                double lat=0;
                double lon=0;
                Geocoder geocoder = new Geocoder(getActivity());

                try {
                    List<Address> addresses =  geocoder.getFromLocationName(AppMoment.getInstance().user.getMomentById(momentId).getAdresse(), 1);

                    if (addresses.size() == 0) {

                      }
                      else {
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Function called when the Maybe button is touched in the RSVP bloc
     * @param view
     */

    public void maybeRsvp(){
        System.out.println("MAYBE");
        int oldState = stateAnwser;

        if(stateAnwser!=MAYBE){
            stateAnwser = MAYBE;
            moment.setState(MAYBE);
            updateRSVPBloc();
            updateStateServer(oldState, MAYBE);
        }
    }

    /**
     * Function called when the Going button is touched in the RSVP bloc
     * @param view
     */

    public void goingRsvp(){
        System.out.println("Going");
        int oldState = stateAnwser;

        if(stateAnwser!=COMING){
            stateAnwser = COMING;
            moment.setState(COMING);
            updateRSVPBloc();
            updateStateServer(oldState, COMING);
        }
    }

    /**
     * Function called when the Not Going button is touched in the RSVP bloc
     */

    public void notRsvp(){
        System.out.println("Not going");
        int oldState = stateAnwser;

        if(stateAnwser!=NOT_COMING){
            stateAnwser = NOT_COMING;
            moment.setState(NOT_COMING);
            updateRSVPBloc();
            updateStateServer(oldState, NOT_COMING);
        }
    }

    /**
     * Function which update the buttons states in the RSVP bloc
     */

    private void updateRSVPBloc(){

        if(moment.getState()==COMING){
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

    /**
     * Update the state of the user on the server
     * If Fail update the button
     * @param oldState
     * @param newState
     */

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


	  
	  

}
