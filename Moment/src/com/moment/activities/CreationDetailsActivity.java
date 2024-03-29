package com.moment.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.moment.AppMoment;
import com.moment.BuildConfig;
import com.moment.R;
import com.moment.classes.MomentApi;
import com.moment.fragments.CreationStep1Fragment;
import com.moment.fragments.CreationStep2Fragment;
import com.moment.models.Moment;
import com.moment.util.BitmapWorkerTask;
import com.moment.util.CommonUtilities;
import com.moment.util.Images;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@SuppressLint("ValidFragment")
public class CreationDetailsActivity extends SherlockFragmentActivity {

    private int step = -1;
    FragmentTransaction fragmentTransaction;
    private CreationStep2Fragment fragment2;
    private CreationStep1Fragment fragment;
    private Moment moment;
    private static Menu myMenu;
    private static Boolean validateFirst = false;
    private static int validateSecond = 0;
    public static int validateDescription = 0;
    public static int validateAdress = 0;
    public static int validateInfosLieu = 0;
    private static Boolean validateFirstDate = false, validateSecondDate = false;
    private Uri outputFileUri;
    private int YOUR_SELECT_PICTURE_REQUEST_CODE = 1;
    private int PLACE_CHOOSE = 10;
    private int POP_UP_CREA = 11;
    private ProgressDialog dialog;
    private Uri mCoverUri;
    private Boolean inModif = false, isSuccess= false;
    private Button dateDebutEdit, heureDebutEdit,dateFinEdit,heureFinEdit;

    static int pickerChosen = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String nomMoment = getIntent().getStringExtra("nomMoment");
        if(getIntent().hasExtra("moment_id")){
            moment = AppMoment.getInstance().user.getMomentById(getIntent().getLongExtra("moment_id", 0));
            inModif = true;
            validateFirst = true;
            validateSecond = 1;
            validateDescription = 1;
            validateAdress = 1;
        }

        if(moment==null){
            moment = new Moment();
            moment.setName(nomMoment);
        }

        dateDebutEdit = (Button)findViewById(R.id.date_debut_button);
        heureDebutEdit = (Button)findViewById(R.id.heure_debut_button);
        dateFinEdit = (Button)findViewById(R.id.date_fin_button);
        heureFinEdit = (Button)findViewById(R.id.heure_fin_button);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new CreationStep1Fragment();
        fragment2 = new CreationStep2Fragment();

        fragmentTransaction.add(android.R.id.content, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_creation, menu);
        myMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(step==0 || step == -1){
            if(inModif){
                myMenu.findItem(R.id.left_options_creation).setVisible(false);
                myMenu.findItem(R.id.right_options_creation).setIcon(R.drawable.btn_flechedown);
                myMenu.findItem(R.id.right_options_creation).setEnabled(true);
            }
            else{
                menu.findItem(R.id.left_options_creation).setVisible(false);
                menu.findItem(R.id.right_options_creation).setIcon(R.drawable.btn_flechedown);
                myMenu.findItem(R.id.right_options_creation).setEnabled(false);
            }
        }
        else if(step==1){
            menu.findItem(R.id.left_options_creation).setVisible(true);
            menu.findItem(R.id.right_options_creation).setIcon(R.drawable.check);
        }


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.left_options_creation:
                if(step==0) return true;
                else{
                    EasyTracker.getTracker().sendEvent("Create", "button_press", "Back Step one", null);

                    upOne();
                    hideKeyboard();

                    step = 0;

                    myMenu.findItem(R.id.left_options_creation).setVisible(false);
                    myMenu.findItem(R.id.right_options_creation).setIcon(R.drawable.btn_flechedown);
                    myMenu.findItem(R.id.right_options_creation).setEnabled(true);

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.custom_in_inverse,R.anim.custom_out_inverse);

                    fragmentTransaction.replace(android.R.id.content, fragment);
                    fragmentTransaction.commit();

                }
                return true;

            case R.id.right_options_creation:
                if(step==0 || step == -1){
                    if(validateFirst){
                        EasyTracker.getTracker().sendEvent("Create", "button_press", "Go Step 2", null);
                        downTwo();

                        if(areDatesCorrect()){
                            if(validateSecond==1){
                                myMenu.findItem(R.id.left_options_creation).setVisible(true);
                                myMenu.findItem(R.id.right_options_creation).setIcon(R.drawable.check);
                            }
                            else{
                                myMenu.findItem(R.id.left_options_creation).setVisible(true);
                                myMenu.findItem(R.id.right_options_creation).setIcon(R.drawable.check_disabled);
                                myMenu.findItem(R.id.right_options_creation).setEnabled(false);
                            }
                            step = 1;
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.setCustomAnimations(R.anim.custom_in,R.anim.custom_out);

                            fragmentTransaction.replace(android.R.id.content, fragment2);
                            fragmentTransaction.commit();
                        }
                        else{
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                            alertDialogBuilder.setTitle(getResources().getString(R.string.incorrect_date_title));

                            alertDialogBuilder
                                    .setMessage(getResources().getString(R.string.incorrect_date_body))
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    }
                }
                else{
                    EasyTracker.getTracker().sendEvent("Create", "button_press", "Create Moment", null);
                    try {
                        creerMoment();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    public void downTwo() {
        moment.setDateDebut(String.valueOf(fragment.getStartDate()));
        moment.setDateFin(String.valueOf(fragment.getEndDate()));
    }

    public void upOne(){

        EditText description = (EditText)findViewById(R.id.creation_moment_description);
        if(description.getText()!=null){
            this.moment.setDescription(description.getText().toString());
        }

        Button adresse = (Button)findViewById(R.id.creation_moment_adresse);
        if(adresse.getText()!=null){
            this.moment.setAdresse(adresse.getText().toString());
        }

        EditText infosLieu = (EditText)findViewById(R.id.creation_moment_infos_lieu);
        if(infosLieu.getText()!=null){
            this.moment.setPlaceInformations(infosLieu.getText().toString());
        }

        myMenu.findItem(R.id.left_options_creation).setVisible(false);
        myMenu.findItem(R.id.right_options_creation).setIcon(R.drawable.btn_flechedown);
        myMenu.findItem(R.id.right_options_creation).setEnabled(true);

    }

    public void dateDebut(View view) {
        EasyTracker.getTracker().sendEvent("Create", "button_press", "Modify Beginning Date", null);
        DialogFragment newFragment = new DatePickerFragment((Button)view.findViewById(R.id.date_debut_button), (Button)findViewById(R.id.date_fin_button));
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void heureDebut(View view) {
        EasyTracker.getTracker().sendEvent("Create", "button_press", "Modify Beginning Hour", null);
        DialogFragment newFragment = new TimePickerFragment((Button)view.findViewById(R.id.heure_debut_button));
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void dateFin(View view) {
        EasyTracker.getTracker().sendEvent("Create", "button_press", "Modify End Date", null);
        DialogFragment newFragment = new DatePickerFragment((Button)view.findViewById(R.id.date_fin_button));
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void heureFin(View view) {
        EasyTracker.getTracker().sendEvent("Create", "button_press", "Modify End Hour", null);
        DialogFragment newFragment = new TimePickerFragment((Button)view.findViewById(R.id.heure_fin_button));
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @SuppressLint("ValidFragment")
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        Button heureEdit;

        public TimePickerFragment(Button heureEdit){
            this.heureEdit = heureEdit;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int hour, minute;

            if(this.heureEdit.getText().toString().split(":").length!=2){
                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
            }
            else{
                hour = Integer.parseInt(this.heureEdit.getText().toString().split(":")[0]);
                minute = Integer.parseInt(this.heureEdit.getText().toString().split(":")[1]);
            }

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if(minute>9) this.heureEdit.setText(""+hourOfDay+":"+minute);
            else this.heureEdit.setText(""+hourOfDay+":0"+minute);
        }
    }

    @SuppressLint("ValidFragment")
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        Button dateEdit, otherDateEdit;

        public DatePickerFragment(Button dateEdit, Button otherDateEdit){
            this.dateEdit = dateEdit;
            this.otherDateEdit = otherDateEdit;
        }

        public DatePickerFragment(Button dateEdit){
            this.dateEdit = dateEdit;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year, month, day;

            if(this.dateEdit.getText().toString().split("/").length!=3){
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            } else {
                year = Integer.parseInt(this.dateEdit.getText().toString().split("/")[2]);
                month = Integer.parseInt(this.dateEdit.getText().toString().split("/")[1]);
                month --;
                day = Integer.parseInt(this.dateEdit.getText().toString().split("/")[0]);
            }

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {

            if(this.dateEdit.getTag().equals("debutDate")){
                this.dateEdit.setText(""+day+"/"+(month+1)+"/"+year);
                validateFirstDate = true;

                if(this.otherDateEdit.getText().toString().split("/").length<2){
                    GregorianCalendar selectedDate = new GregorianCalendar(year, month, day);
                    selectedDate.add(GregorianCalendar.DAY_OF_MONTH, 1);
                    this.otherDateEdit.setText(""+selectedDate.get(GregorianCalendar.DAY_OF_MONTH)+"/"+(selectedDate.get(GregorianCalendar.MONTH)+1)+"/"+selectedDate.get(GregorianCalendar.YEAR));
                }
            }
            else{
                validateSecondDate = true;
                this.dateEdit.setText(""+day+"/"+(month+1)+"/"+year);
            }

            validateFirstFields();
        }
    }

    public void creerMoment() throws JSONException {

        EditText descriptionEdit = (EditText)findViewById(R.id.creation_moment_description);
        Button adressButton = (Button)findViewById(R.id.creation_moment_adresse);
        EditText infosLieuEdit = (EditText)findViewById(R.id.creation_moment_infos_lieu);

        moment.setDescription(descriptionEdit.getText().toString());
        moment.setAdresse(adressButton.getText().toString());
        if(infosLieuEdit.getText().toString().length()>0) moment.setPlaceInformations(infosLieuEdit.getText().toString());


        if(!inModif){
            CreateMomentTask task = new CreateMomentTask(true);
            task.execute(moment);
        }
        else{
            CreateMomentTask task = new CreateMomentTask(false);
            task.execute(moment);
        }
    }

    public Moment getMoment(){
        return this.moment;
    }

    private void hideKeyboard()
    {
        InputMethodManager inputManager = (InputMethodManager)
                getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(this.getCurrentFocus()!=null){
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void validateFirstFields(){

        if(validateFirstDate){
            validateFirst = true;
            myMenu.findItem(R.id.right_options_creation).setIcon(R.drawable.btn_flechedown);
            myMenu.findItem(R.id.right_options_creation).setEnabled(true);
        }
        else{
            validateFirst = false;
            myMenu.findItem(R.id.right_options_creation).setIcon(R.drawable.btn_flechedown_desactivated);
            myMenu.findItem(R.id.right_options_creation).setEnabled(false);
        }

    }

    public static void validateSecondFields(){

        if((validateDescription==1)&&(validateAdress==1)){
            validateSecond = 1;
            myMenu.findItem(R.id.right_options_creation).setIcon(R.drawable.check);
            myMenu.findItem(R.id.right_options_creation).setEnabled(true);
        }
        else{
            validateSecond = 0;
            myMenu.findItem(R.id.right_options_creation).setIcon(R.drawable.check_disabled);
            myMenu.findItem(R.id.right_options_creation).setEnabled(false);
        }
    }

    public void changePhoto(View view){
        EasyTracker.getTracker().sendEvent("Create", "button_press", "Add Photo", null);
        openImageIntent();
    }

    public void choosePlace(View view) {
        EasyTracker.getTracker().sendEvent("Create", "button_press", "Choose Place", null);
        Intent intent = new Intent(this, PlacePickerActivity.class);
        startActivityForResult(intent, PLACE_CHOOSE);
    }

    private void openImageIntent() {

        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Moment" + File.separator + "Images");
        root.mkdirs();
        final String fname = "photo_moment.png";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);

        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        final Intent chooserIntent = Intent.createChooser(galleryIntent, getResources().getString(R.string.select_source));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, YOUR_SELECT_PICTURE_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            if(requestCode == YOUR_SELECT_PICTURE_REQUEST_CODE)
            {
                final boolean isCamera;
                if(data == null)
                {
                    isCamera = true;
                }
                else
                {
                    final String action = data.getAction();
                    if(action == null)
                    {
                        isCamera = false;
                    }
                    else
                    {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if(isCamera)
                {
                    selectedImageUri = outputFileUri;
                }
                else
                {
                    selectedImageUri = data == null ? null : data.getData();
                }

                try{
                    mCoverUri = selectedImageUri;
                    if(BuildConfig.DEBUG) Log.d("URI", ""+mCoverUri);


                    ImageView moment_image = (ImageView)findViewById(R.id.creation_moment_image);
                    //moment_image.setImageBitmap(bitmap);
                    ContentResolver cr = getContentResolver();
                    BitmapWorkerTask task = new BitmapWorkerTask(moment_image, 900,cr);
                    task.execute(mCoverUri);

                }catch (OutOfMemoryError e){
                    Log.e("OUTOFMEMORY", e.toString());
                }


            }

            if(requestCode == POP_UP_CREA){
                if(resultCode == RESULT_OK){
                    if(data.getExtras().containsKey("privacy")){
                        AppMoment.getInstance().user.getMomentById(moment.getId()).setPrivacy(data.getIntExtra("privacy", 0));
                        AppMoment.getInstance().user.getMomentById(moment.getId()).setIsOpenInvit(data.getBooleanExtra("isOpenInvit", false));
                    }
                    Intent intent = new Intent(CreationDetailsActivity.this, MomentInfosActivity.class);
                    intent.putExtra("id", moment.getId());
                    intent.putExtra("precedente", "creation");

                    startActivity(intent);
                    finish();
                }
            }
            if(requestCode == PLACE_CHOOSE){


                if(data.getExtras().containsKey("place_label")){
                    Button adressButton = (Button)findViewById(R.id.creation_moment_adresse);
                    adressButton.setText(data.getStringExtra("place_label"));

                    if(data.getStringExtra("place_label").length()>0){
                        validateAdress=1;
                        CreationDetailsActivity.validateSecondFields();
                    }
                    else CreationDetailsActivity.validateSecondFields();
                }
            }
        }
    }

    public boolean areDatesCorrect(){
        if(fragment.getEndDate().isAfter(fragment.getStartDate())) return true;
        else return false;
    }

    public Uri getCoverUri(){
        if(mCoverUri!=null){
            return mCoverUri;
        }

        return null;
    }

    /**
     * Task that create or modify the moment
     */

    private class CreateMomentTask extends AsyncTask<Moment, Void, HttpResponse> {
        private boolean mIsCreation;
        private ProgressDialog dialog;


        public CreateMomentTask(boolean isCreation) {
            mIsCreation = isCreation;
        }

        @Override
        protected void onPreExecute() {
            if(mIsCreation) dialog = ProgressDialog.show(CreationDetailsActivity.this, null, getResources().getString(R.string.creation_progress));
            else dialog = ProgressDialog.show(CreationDetailsActivity.this, null, getResources().getString(R.string.modification_progress));
        }

        @Override
        protected HttpResponse doInBackground(Moment... params) {

            if (AppMoment.getInstance().user == null) AppMoment.getInstance().getUser();
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                localContext.setAttribute(ClientContext.COOKIE_STORE, MomentApi.myCookieStore);

                //Depend creation or modification
                HttpPost httpPost;
                if(mIsCreation){
                    httpPost = new HttpPost(MomentApi.CREATION_MOMENT);
                }
                else{
                    httpPost = new HttpPost(MomentApi.MODIF_MOMENT+moment.getId());
                }

                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                //We put the image in the parameters (if there is a new one and so an Uri)
                if(mCoverUri!=null){
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    Images.decodeSampledBitmapFromURI(mCoverUri, getContentResolver(), 900, 900).compress(Bitmap.CompressFormat.JPEG, 80, bos);
                    byte[] data = bos.toByteArray();
                    entity.addPart("photo", new ByteArrayBody(data, "photo.png"));
                }
                //We set the other parameters of the moment
                if(moment.getName()!=null) entity.addPart("name", new StringBody(moment.getName(), Charset.defaultCharset()));
                if(moment.getDescription()!=null) entity.addPart("description", new StringBody(moment.getDescription(), Charset.defaultCharset()));
                if(moment.getAdresse()!=null) entity.addPart("address", new StringBody(moment.getAdresse(), Charset.defaultCharset()));
                entity.addPart("startDate", new StringBody(moment.getStartDateString()));
                entity.addPart("endDate", new StringBody(moment.getEndDateString()));
                entity.addPart("startTime", new StringBody(moment.getStartHourString()));
                entity.addPart("endTime", new StringBody(moment.getEndHourString()));
                if(moment.getPlaceInformations() != null) entity.addPart("placeInformations", new StringBody(moment.getPlaceInformations(), Charset.defaultCharset()));

                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost, localContext);
                return response;
            } catch (Exception e) {
                Log.e(e.getClass().getName(), e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(HttpResponse result) {
            dialog.dismiss();
            if(CommonUtilities.isSuccessRequest(result)){
                try{
                    if(mIsCreation){
                        moment.setMomentFromJson(CommonUtilities.getJSONResponse(result));
                        AppMoment.getInstance().user.addMoment(moment);
                        Intent intent = new Intent(CreationDetailsActivity.this, CreationPopUp.class);
                        intent.putExtra("momentId", moment.getId());
                        startActivityForResult(intent, POP_UP_CREA);

                    }
                    else{
                        AppMoment.getInstance().user.getMoments().remove(moment);
                        AppMoment.getInstance().momentDao.delete(moment);

                        Intent intent = new Intent(CreationDetailsActivity.this, MomentInfosActivity.class);
                        intent.putExtra("id", moment.getId());
                        intent.putExtra("precedente", "modif");
                        startActivity(intent);
                        finish();
                    }

                }catch(JSONException e){

                }

            }
            else{
                if(mIsCreation) Toast.makeText(getApplicationContext(), getString(R.string.problem_creation), Toast.LENGTH_SHORT).show();
                else Toast.makeText(getApplicationContext(), getString(R.string.problem_modif), Toast.LENGTH_SHORT).show();
                try{
                    Log.e("HTTP", EntityUtils.toString(result.getEntity()));
                }catch(IOException e){

                }

            }
        }

    }

}
