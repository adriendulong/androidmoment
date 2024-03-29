package com.moment.fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.webkit.WebView;
import android.widget.*;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.activities.MomentInfosActivity;
import com.moment.classes.MomentApi;
import com.moment.classes.PositionOverlay;
import com.moment.classes.RecyclingImageView;
import com.moment.classes.RoundTransformation;
import com.moment.models.Moment;
import com.moment.models.Photo;
import com.moment.models.User;
import com.moment.util.CommonUtilities;
import com.moment.util.ImageCache;
import com.moment.util.ImageFetcher;
import com.moment.util.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfosFragment extends Fragment {

    private static final int PICK_CAMERA_COVER = 1;
    //Different states
    private final int COMING = 2;
    private final int NOT_COMING = 3;
    private final int MAYBE = 5;
    private final int OWNER = 0;
    private final int ADMIN = 1;
    private final int UNKOWN = 4;
    private String[] mois;
    private String[] jours;
    private final Transformation roundTrans = new RoundTransformation();
    private GoogleMap mMap;
    private Long momentId;
    private int stateAnwser;
    private Moment moment;
    //All view elements
    private TextView titreText, flTitreText, descriptionText, adresse, dateDebutText, dateFinText, heureDebutText, heureFinText, guests_number, guests_coming, guests__not_coming, firstname, lastname, textRSVP;
    private ImageView image_cover, owner_picture;
    private RelativeLayout modifLayout;
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

    private static final String IMAGE_CACHE_DIR = "thumbs_info";
    private static final String IMAGE_CACHE_DIR_COVER = "cover";
    private ImageFetcher mImageFetcher;
    private ImageFetcher mImageFetcherCover;
    private int mImageThumbSize, mImageThumbSizeCover;

    private static final int REAUTH_ACTIVITY_CODE = 100;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");

    public static void navigateToLocation(double latitude, double longitude, MapView mv, Context context) {
        GeoPoint p = new GeoPoint((int) latitude, (int) longitude);


        List<Overlay> mapOverlays = MomentInfosActivity.Exchanger.mMapView.getOverlays();
        Drawable drawable = context.getResources().getDrawable(R.drawable.picto_o);
        PositionOverlay itemizedoverlay = new PositionOverlay(drawable, context);

        OverlayItem overlayitem = new OverlayItem(p, "Hola, Mundo!", "I'm in Mexico City!");

        itemizedoverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedoverlay);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_mini_thumbnail_size);

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.picto_photo_vide);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);

        //Fetcher cover
        mImageThumbSizeCover = (int)(getActivity().getWindowManager().getDefaultDisplay().getWidth()*1.5);

        ImageCache.ImageCacheParams cacheParamsCover = new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR_COVER);

        cacheParamsCover.setMemCacheSizePercent(0.10f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcherCover = new ImageFetcher(getActivity(), mImageThumbSizeCover);
        mImageFetcherCover.setLoadingImage(R.drawable.picto_photo_vide);
        mImageFetcherCover.addImageCache(getActivity().getSupportFragmentManager(), cacheParamsCover);


        mGaInstance = GoogleAnalytics.getInstance(getActivity());


        mGaTracker = mGaInstance.getTracker(AppMoment.getInstance().GOOGLE_ANALYTICS); // Placeholder tracking ID.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_infos, container, false);


        photos = new ArrayList<Photo>();

        if (view != null) {
            titreText = (TextView) view.findViewById(R.id.titre_moment);
            descriptionText = (TextView) view.findViewById(R.id.infos_moment_description);
            flTitreText = (TextView) view.findViewById(R.id.fl_titre_moment);
            adresse = (TextView) view.findViewById(R.id.infos_moment_adresse);

            maybeButton = (ImageButton) view.findViewById(R.id.maybe_button);
            goingButton = (ImageButton) view.findViewById(R.id.going_button);
            notGoigButton = (ImageButton) view.findViewById(R.id.not_going_button);
            addGuests = (ImageButton) view.findViewById(R.id.add_guests);

            dateDebutText = (TextView) view.findViewById(R.id.infos_moment_date_debut);
            dateFinText = (TextView) view.findViewById(R.id.infos_moment_date_fin);
            heureDebutText = (TextView) view.findViewById(R.id.infos_moment_heure_debut);
            heureFinText = (TextView) view.findViewById(R.id.infos_moment_heure_fin);

            guests_number = (TextView) view.findViewById(R.id.guests_number);
            guests_coming = (TextView) view.findViewById(R.id.guests_coming);
            guests__not_coming = (TextView) view.findViewById(R.id.guests_not_coming);
            modifLayout = (RelativeLayout) view.findViewById(R.id.modif_layout);

            textRSVP = (TextView) view.findViewById(R.id.text_rsvp);

            delMoment = (Button) view.findViewById(R.id.del_moment);


            image_cover = (ImageView) view.findViewById(R.id.photo_moment);
            owner_picture = (ImageView) view.findViewById(R.id.photo_owner);
            firstname = (TextView) view.findViewById(R.id.firstname_owner);
            lastname = (TextView) view.findViewById(R.id.lastname_owner);

            gridPreviewPhotos = (GridView) view.findViewById(R.id.grid_preview_photos);
            mapWeb = (WebView) view.findViewById(R.id.map_web);
        }

        if (((MomentInfosActivity) getActivity()).getMomentId() != null) {
            this.momentId = ((MomentInfosActivity) getActivity()).getMomentId();
            moment = AppMoment.getInstance().user.getMomentById(momentId);
            AppMoment.getInstance().momentDao.update(moment);
            initInfos();
        }

        return view;
    }

    private void modifyPhotoMoment(Bitmap photo) {
        ImageView photoMoment = (ImageView) getActivity().findViewById(R.id.photo_moment);
        photoMoment.setImageBitmap(photo);
    }

    public void touchedPhoto() {
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

    public void facebook() {
        Session.openActiveSession(getActivity(), true, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (session.isOpened()) {
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

    public void maybeRsvp() {
        System.out.println("MAYBE");
        int oldState = stateAnwser;

        if (moment.getState() != OWNER) {
            if (stateAnwser != MAYBE) {
                stateAnwser = MAYBE;
                moment.setState(MAYBE);
                updateRSVPBloc();
                updateStateServer(oldState, MAYBE);
            }
        }

    }

    public void goingRsvp() {
        System.out.println("Going");
        final int oldState = stateAnwser;

        if(moment.getState() == null)
        {
            StringEntity entity = null;
            try {
                JSONArray users = new JSONArray();
                users.put(AppMoment.getInstance().user.getUserToJSON());
                JSONObject tab = new JSONObject();
                tab.put("users", users);
                entity = new StringEntity(tab.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            MomentApi.postJSON(getActivity(), "newguests/"+ momentId, entity, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject result) {
                    stateAnwser = COMING;
                    moment.setState(COMING);
                    updateRSVPBloc();
                    facebook();
                    updateStateServer(oldState, COMING);
                }

                @Override
                public void onFailure(Throwable e, JSONObject response) {
                    e.printStackTrace();
                    response.toString();
                }
            });
        } else {

            if (moment.getState() != OWNER) {
                if (stateAnwser != COMING) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle(getResources().getString(R.string.rsvp_title));
                    alertDialog.setMessage(getResources().getString(R.string.rsvp_text));
                    alertDialog.setPositiveButton("Envoyer", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            stateAnwser = COMING;
                            moment.setState(COMING);
                            facebook();
                            updateRSVPBloc();
                            updateStateServer(oldState, COMING);
                            dialog.cancel();
                        }
                    });

                    alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            stateAnwser = COMING;
                            moment.setState(COMING);
                            updateRSVPBloc();
                            updateStateServer(oldState, COMING);
                            dialog.dismiss();

                        }
                    });

                    alertDialog.show();
                }
            }
        }
    }

    public void notRsvp() {
        System.out.println("Not going");
        int oldState = stateAnwser;

        if (moment.getState() != OWNER) {
            if (stateAnwser != NOT_COMING) {
                stateAnwser = NOT_COMING;
                moment.setState(NOT_COMING);
                updateRSVPBloc();
                updateStateServer(oldState, NOT_COMING);
            }
        }

    }

    private void updateRSVPBloc() {

        if (moment.getState() != null && (moment.getState() == COMING || moment.getState() == OWNER)) {
            goingButton.setSelected(true);
            goingButton.setImageResource(R.drawable.picto_yes_down);
            maybeButton.setSelected(false);
            maybeButton.setImageResource(R.drawable.picto_maybe);
            notGoigButton.setSelected(false);
            notGoigButton.setImageResource(R.drawable.picto_no);
            textRSVP.setText(getResources().getString(R.string.rsvp_coming));
        } else if (moment.getState() != null && moment.getState() == NOT_COMING) {
            notGoigButton.setSelected(true);
            notGoigButton.setImageResource(R.drawable.picto_no_down);
            maybeButton.setSelected(false);
            maybeButton.setImageResource(R.drawable.picto_maybe);
            goingButton.setSelected(false);
            goingButton.setImageResource(R.drawable.picto_valid);
            textRSVP.setText(getResources().getString(R.string.rsvp_not_coming));
        } else if (moment.getState() != null && moment.getState() == MAYBE) {
            maybeButton.setSelected(true);
            maybeButton.setImageResource(R.drawable.picto_maybe_down);
            notGoigButton.setSelected(false);
            notGoigButton.setImageResource(R.drawable.picto_no);
            goingButton.setSelected(false);
            goingButton.setImageResource(R.drawable.picto_valid);
            textRSVP.setText(getResources().getString(R.string.rsvp_maybe));
        } else if (moment.getState() != null && moment.getState() == UNKOWN) {
            Toast.makeText(getActivity(), getResources().getString(R.string.premiere_visite), Toast.LENGTH_SHORT).show();
            maybeRsvp();
        }

    }

    private void updateStateServer(final int oldState, int newState) {

        MomentApi.get("state/" + momentId + "/" + newState, null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject response) {
                Toast.makeText(getActivity(), getResources().getString(R.string.update_answer), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable error, String content) {
                Toast.makeText(getActivity(), getResources().getString(R.string.error_connect), Toast.LENGTH_SHORT).show();
                stateAnwser = oldState;
                moment.setState(oldState);
                updateRSVPBloc();

            }
        });

    }



    public void createFragment(Long momentId) {
        this.momentId = momentId;
        moment = AppMoment.getInstance().user.getMomentById(momentId);
        if (titreText != null) initInfos();
    }



    private void initInfos() {

        titreText.setText(moment.getName().substring(1));
        flTitreText.setText(moment.getName().substring(0, 1));
        descriptionText.setText(moment.getDescription());
        adresse.setText(moment.getAdresse());


        updateRSVPBloc();

        org.joda.time.DateTime dt;

        mois = this.getResources().getStringArray(R.array.mois);
        jours = this.getResources().getStringArray(R.array.jours);

        dt = CommonUtilities.dateFormatISO.parseDateTime(moment.getDateDebut());

        dateDebutText.setText("" + jours[dt.getDayOfWeek() - 1] + " " + dt.getDayOfMonth() + " " + mois[dt.getMonthOfYear() - 1]);
        heureDebutText.setText("" + dt.getHourOfDay() + "H" + dt.getMinuteOfHour());

        dt = CommonUtilities.dateFormatISO.parseDateTime(moment.getDateFin());

        dateFinText.setText("" + jours[dt.getDayOfWeek() - 1] + " " + dt.getDayOfMonth() + " " + mois[dt.getMonthOfYear() - 1]);
        heureFinText.setText("" + dt.getHourOfDay() + "H" + dt.getMinuteOfHour());

        mImageFetcherCover.loadImage(moment.getUrlCover(), image_cover, false);

        if (moment.getUser() != null) {
            if (moment.getUser().getPictureProfileUrl() != null)
            {
                Picasso.with(getActivity()).load(moment.getUser().getPictureProfileUrl()).resize(200, 200).transform(roundTrans).into(owner_picture);
            }
            firstname.setText(moment.getUser().getFirstName());
            lastname.setText(moment.getUser().getLastName());
        }


        if (AppMoment.getInstance().user.getMomentById(momentId).getGuestNumber() > 0) {
            guests_number.setText("" + AppMoment.getInstance().user.getMomentById(momentId).getGuestNumber());
            guests_coming.setText("" + AppMoment.getInstance().user.getMomentById(momentId).getGuestComing());
            guests__not_coming.setText("" + AppMoment.getInstance().user.getMomentById(momentId).getGuestNotComing());
        }


        if (!canInvite(AppMoment.getInstance().user)) {
            ImageButton addGuests = (ImageButton)view.findViewById(R.id.add_guests_button);
            addGuests.setEnabled(false);
            if(Utils.hasHoneycomb()){
                addGuests.setAlpha(0.5f);
            }
            else{
                addGuests.setVisibility(View.GONE);
            }

        }

        if (AppMoment.getInstance().user.getId() != moment.getOwnerId()) {
            modifLayout.setVisibility(View.INVISIBLE);
        } else {
            delMoment.setVisibility(View.VISIBLE);
        }

        imageAdapter = new ImageAdapter(view.getContext(), photos);
        gridPreviewPhotos.setAdapter(imageAdapter);

        if(((MomentInfosActivity)getActivity()).mNbPhotos == 0){
            RelativeLayout photosLayout = (RelativeLayout)view.findViewById(R.id.bloc_photos);
            photosLayout.setVisibility(View.GONE);
            Button addPhotos = (Button)view.findViewById(R.id.add_photos_infos);
            addPhotos.setVisibility(View.VISIBLE);
        }


        if (AppMoment.getInstance().checkInternet()) {
            loadMap();
        }

    }

    private Boolean canInvite(User user) {
        int PUBLIC = 2;
        return moment.getPrivacy() == PUBLIC || moment.getIsOpenInvit() || user.getId() == moment.getOwnerId();
    }




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
        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView imageView;
            float pxImage = getResources().getDimensionPixelSize(R.dimen.image_background__mini_thumbnail_size);
            LinearLayout layout;
            float pxBitmap = getResources().getDimensionPixelSize(R.dimen.image_mini_thumbnail_size);

            if (convertView == null) {
                layout = new LinearLayout(context);
                layout.setLayoutParams(new GridView.LayoutParams((int) pxImage, (int) pxImage));
                layout.setBackgroundColor(getResources().getColor(R.color.white));
                layout.setGravity(Gravity.CENTER);

                imageView = new ImageView(context);
                imageView.setId(0);
                imageView.setLayoutParams(new GridView.LayoutParams((int) pxBitmap, (int) pxBitmap));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                layout.addView(imageView);
            } else {
                layout = (LinearLayout)convertView;
                imageView = (ImageView) convertView.findViewById(0);
            }

            try {
                if(photos.get(position).getUrlThumbnail()!=null) Picasso.with(context).load(photos.get(position).getUrlThumbnail()).placeholder(R.drawable.picto_photo_vide).resize((int)pxBitmap, (int)pxBitmap).into(imageView); //mImageFetcher.loadImage(photos.get(position).getUrlThumbnail(), imageView, false);
                else imageView.setImageDrawable(getResources().getDrawable(R.drawable.picto_photo_vide));
            } catch (OutOfMemoryError outOfMemoryError) {
                outOfMemoryError.printStackTrace();
            }
            return layout;
        }
    }




    public void updatePhotos(List<Photo> photosNew) {

        if(photos!=null){
            if(photos.size()==0){
                for (int i = 0; i < numberOfPhotos(); i++) {
                    if (photosNew.size() > i) photos.add(photosNew.get(i));
                }
                Log.v("INFOSFRAGMENT", "Nombre de photos : " + numberOfPhotos());
                gridPreviewPhotos.setFocusable(false);
                imageAdapter.notifyDataSetChanged();
            }
        }


    }

    private int numberOfPhotos() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();

        float pxImage = getResources().getDimensionPixelSize(R.dimen.image_background__mini_thumbnail_size);

        return Math.round(width / pxImage);
    }

    private void loadMap() {
        double lat;
        double lon;
        Geocoder geocoder = new Geocoder(getActivity());

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        float density = getActivity().getApplicationContext().getResources().getDisplayMetrics().density;
        int realWidth = Math.round((float) width/density );//*

        try {
            List<Address> addresses = geocoder.getFromLocationName(AppMoment.getInstance().user.getMomentById(momentId).getAdresse(), 1);

            if (addresses.size() > 0) {
                Address x = addresses.get(0);
                lat = x.getLatitude();
                lon = x.getLongitude();


                String mapdBoxUrl = "http://a.tiles.mapbox.com/v3/appmoment.map-62jk3rrs/pin-s-star-stroked+ff793d(" + lon + "," + lat + ")/" + lon + "," + lat + ",8/" + realWidth + "x" + 60 + ".png";
                mapWeb.loadUrl(mapdBoxUrl);

            } else {
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


        mGaTracker.sendView("/InfosFragment");
    }


}
