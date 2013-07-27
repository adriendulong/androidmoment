package com.moment.activities;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.DatabaseHelper;
import com.moment.classes.Images;
import com.moment.classes.MomentApi;
import com.moment.fragments.CreationStep1Fragment;
import com.moment.fragments.CreationStep2Fragment;
import com.moment.models.Moment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@SuppressLint("ValidFragment")
public class CreationDetailsActivity extends SherlockFragmentActivity {

	//Step = 0 premiere etape, step = 1 deuxieme etape
	private int step = -1;
	FragmentTransaction fragmentTransaction;
	CreationStep2Fragment fragment2;
	CreationStep1Fragment fragment;
	static TextView dateFin;
	ImageButton dateDebut;
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
    private Boolean inModif = false;
    private Button dateDebutEdit, heureDebutEdit,dateFinEdit,heureFinEdit;


	//Permet de savoir quel picker est entrain d'etre choisi (0 pour debut, 1 pour fin)
	static int pickerChosen = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String nomMoment = getIntent().getStringExtra("nomMoment");
        Log.d("Nom Moment", nomMoment);
        if(getIntent().hasExtra("moment_id")){
            moment = AppMoment.getInstance().user.getMomentById(getIntent().getLongExtra("moment_id", 0));
            inModif = true;
            validateFirst = true;
            validateSecond = 1;
            validateDescription = 1;
            validateAdress = 1;
        }

        //On cree l'objet Moment qui servira pendant toute la creation
        if(moment==null){
            moment = new Moment();
            moment.setName(nomMoment);
        }

        //Buttons
        dateDebutEdit = (Button)findViewById(R.id.date_debut_button);
        heureDebutEdit = (Button)findViewById(R.id.heure_debut_button);
        dateFinEdit = (Button)findViewById(R.id.date_fin_button);
        heureFinEdit = (Button)findViewById(R.id.heure_fin_button);


        // On instantie le fragment manager et on vient ajouter le premier fragment
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
    	if(step==0){
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

            		//On enregistre les champs
            		upOne();

            		hideKeyboard();

            		step = 0;

            		//supportInvalidateOptionsMenu();

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
                        downTwo();

                        //Second date after first one ?
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

                            // set title
                            alertDialogBuilder.setTitle("Dates incorrectes");

                            // set dialog message
                            alertDialogBuilder
                                    .setMessage("Vérifiez que la date de fin est supérieur à celle de début")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // if this button is clicked, just close
                                            // the dialog box and do nothing
                                            dialog.cancel();
                                        }
                                    });
                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            // show it
                            alertDialog.show();
                        }
                    }
            	}
            	else{
            		System.out.println("VALIDERRRR");
            		try {
						creerMoment();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            	return true;

        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Gere le passage de la step 1 � la step2
     * @param view
     */

    public void down(View view) {
	       Log.d("Down", "DOWN OK");

	       //On r�cup�re tous les �l�ments rentr�s
	       EditText nomLieu = (EditText)findViewById(R.id.edit_lieu);
	       //EditText numeroAdresse = (EditText)findViewById(R.id.edit_adresse_numero_rue);
	       //EditText adresseCodePostal = (EditText)findViewById(R.id.edit_adresse_code_postale);
	       EditText adresse = (EditText)findViewById(R.id.edit_adresse);
	       EditText adresseInfoLieu = (EditText)findViewById(R.id.edit_info_lieu);
	       EditText adresseInfoTransport = (EditText)findViewById(R.id.edit_adresse_info_transport);

	       //Adresse adressTemp = new Adresse(numeroAdresse.getText().toString(), Integer.parseInt(adresseCodePostal.getText().toString()), adresseVille.getText().toString());

	       moment.setPlaceInformations(adresseInfoLieu.getText().toString());
	       moment.setInfoTransport(adresseInfoTransport.getText().toString());
	       moment.setAdresse(adresse.getText().toString());
	       //moment.setTitre(nomLieu.getText().toString());




	       FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
	       fragmentTransaction.setCustomAnimations(R.anim.custom_in,R.anim.custom_out);

	       fragmentTransaction.replace(android.R.id.content, fragment2);
	       fragmentTransaction.commit();

	    }




    /**
     * GEre le passage � la 2eme etape
     */

    public void downTwo() {

       //On enregistre la date de d�but
       moment.setDateDebut(fragment.getStartDate());
       //On enregistre la date de fin
       moment.setDateFin(fragment.getEndDate());

	 }


    /**
     * Gere le retour � la step 1
     */

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
    	
    	/*EditText hashtag = (EditText)findViewById(R.id.creation_moment_hashtag);
    	if(hashtag.getText()!=null){
    		this.moment.setHashtag(hashtag.getText().toString());
    	}*/

    }

    /**
     * Gere le date picker pour choisir la date de d�but
     * @param view
     */

    public void dateDebut(View view) {

    	//On ouvre le date picker
    	DialogFragment newFragment = new DatePickerFragment((Button)view.findViewById(R.id.date_debut_button), (Button)findViewById(R.id.date_fin_button));
        newFragment.show(getSupportFragmentManager(), "datePicker");

	}

    /**
     * Gere le time picker pour l'heure de d�but
     * @param view
     */

    public void heureDebut(View view) {

    	//On ouvre le time picker
    	DialogFragment newFragment = new TimePickerFragment((Button)view.findViewById(R.id.heure_debut_button));
        newFragment.show(getSupportFragmentManager(), "timePicker");


	}

    /**
     * Gere le date picker pour choisir la date de fin
     * @param view
     */

    public void dateFin(View view) {

    	//On ouvre le date picker
    	DialogFragment newFragment = new DatePickerFragment((Button)view.findViewById(R.id.date_fin_button));
        newFragment.show(getSupportFragmentManager(), "datePicker");
	}

    /**
     * Gere le time picker pour l'heure de fin
     * @param view
     */

    public void heureFin(View view) {

    	//On ouvre le time picker
    	DialogFragment newFragment = new TimePickerFragment((Button)view.findViewById(R.id.heure_fin_button));
        newFragment.show(getSupportFragmentManager(), "timePicker");

	}



    /**
     * TimePicker Fragment : g�re l'ouverture d'une boite de dialogue pour choisir la date
     * @author adriendulong
     *
     */

    @SuppressLint("ValidFragment")
	public static class TimePickerFragment extends DialogFragment
	    implements TimePickerDialog.OnTimeSetListener {

    	Button heureEdit;

    	public TimePickerFragment(Button heureEdit){
    		//if wichDebut = 0 ==> D�but else Fin
    		this.heureEdit = heureEdit;
    	}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int hour, minute;

			if(this.heureEdit.getText().toString().split(":").length!=2){
				// Use the current time as the default values for the picker
				final Calendar c = Calendar.getInstance();
				hour = c.get(Calendar.HOUR_OF_DAY);
				minute = c.get(Calendar.MINUTE);
			}
			else{
				hour = Integer.parseInt(this.heureEdit.getText().toString().split(":")[0]);
				minute = Integer.parseInt(this.heureEdit.getText().toString().split(":")[1]);
			}


			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
			DateFormat.is24HourFormat(getActivity()));
		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// Do something with the time chosen by the user

				if(minute>9) this.heureEdit.setText(""+hourOfDay+":"+minute);
                else this.heureEdit.setText(""+hourOfDay+":0"+minute);


			}
		}


    /**
     * TimePicker Fragment : g�re l'ouverture d'une boite de dialogue pour choisir la date
     * @author adriendulong
     *
     */

    @SuppressLint("ValidFragment")
	public static class DatePickerFragment extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {

    	Button dateEdit, otherDateEdit;

    	public DatePickerFragment(Button dateEdit, Button otherDateEdit){
    		//if wichDebut = 0 ==> D�but else Fin
    		this.dateEdit = dateEdit;
            this.otherDateEdit = otherDateEdit;
    	}

        public DatePickerFragment(Button dateEdit){
            //if wichDebut = 0 ==> D�but else Fin
            this.dateEdit = dateEdit;
        }

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			int year, month, day;

			if(this.dateEdit.getText().toString().split("/").length!=3){
				final Calendar c = Calendar.getInstance();
				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DAY_OF_MONTH);
			}
			else{
				year = Integer.parseInt(this.dateEdit.getText().toString().split("/")[2]);
				month = Integer.parseInt(this.dateEdit.getText().toString().split("/")[1]);
				month -= 1;
				day = Integer.parseInt(this.dateEdit.getText().toString().split("/")[0]);
			}



			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {

            if(this.dateEdit.getTag().equals("debutDate")){
                Log.e("CREATION", "TAG DATE DEBUT");
                this.dateEdit.setText(""+day+"/"+(month+1)+"/"+year);
                validateFirstDate = true;

                if(this.otherDateEdit.getText().toString().split("/").length<2){
                    GregorianCalendar selectedDate = new GregorianCalendar(year, month, day);
                    selectedDate.add(GregorianCalendar.DAY_OF_MONTH, 1);
                    this.otherDateEdit.setText(""+selectedDate.get(GregorianCalendar.DAY_OF_MONTH)+"/"+(selectedDate.get(GregorianCalendar.MONTH)+1)+"/"+selectedDate.get(GregorianCalendar.YEAR));
                }
            }
            else{
                Log.e("CREATION", "TAG DATE FIN");
                validateSecondDate = true;
                this.dateEdit.setText(""+day+"/"+(month+1)+"/"+year);
            }

            validateFirstFields();
		}
	}


    /**
     * Fonction appel�e quand on clique sur cr�ation d'un moment
     * @throws JSONException
     *
     */

    public void creerMoment() throws JSONException {



    	EditText descriptionEdit = (EditText)findViewById(R.id.creation_moment_description);
    	Button adressButton = (Button)findViewById(R.id.creation_moment_adresse);
    	EditText infosLieuEdit = (EditText)findViewById(R.id.creation_moment_infos_lieu);
    	//EditText hashtagEdit = (EditText)findViewById(R.id.creation_moment_hashtag);


    	Log.d("Description", descriptionEdit.getText().toString());
    	moment.setDescription(descriptionEdit.getText().toString());
    	moment.setAdresse(adressButton.getText().toString());
    	if(infosLieuEdit.getText().toString().length()>0) moment.setPlaceInformations(infosLieuEdit.getText().toString());
    	//if(hashtagEdit != null) moment.setHashtag(hashtagEdit.getText().toString());



        if(!inModif){
            dialog = ProgressDialog.show(this, null, "Création en cours");
            MomentApi.post("newmoment", moment.getMomentRequestParams(getApplicationContext()), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        dialog.dismiss();

                        //We set the moment id and it to the user moments
                        moment.setMomentFromJson(response);
                        AppMoment.getInstance().user.addMoment(moment);

                        Intent intent = new Intent(CreationDetailsActivity.this, CreationPopUp.class);
                        intent.putExtra("momentId", moment.getId());
                        startActivityForResult(intent, POP_UP_CREA);


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Throwable e, JSONObject errorResponse) {
                    System.out.println(errorResponse.toString());
                    dialog.dismiss();
                }
            });
        }
        else{
            dialog = ProgressDialog.show(this, null, "Modification en cours");
            MomentApi.post("moment/"+moment.getId(), moment.getMomentRequestParams(getApplicationContext()), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    dialog.dismiss();


                    AppMoment.getInstance().user.getMoments().remove(moment);
                    DatabaseHelper.removeMoment(moment);

                    Intent intent = new Intent(CreationDetailsActivity.this, MomentInfosActivity.class);
                    intent.putExtra("id", moment.getId());
                    intent.putExtra("precedente", "modif");
                    startActivity(intent);
                }

                @Override
                public void onFailure(Throwable e, JSONObject errorResponse) {
                    System.out.println(errorResponse.toString());
                    dialog.dismiss();
                }
            });
        }






    }


    /**
     * Function that return the moment to the fracment
     * @return Moment
     */

    public Moment getMoment(){
    	return this.moment;
    }

    /**
     * Fonction pour cacher le clavier
     */

    private void hideKeyboard()
    {
    	InputMethodManager inputManager = (InputMethodManager)
    			  getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    	if(this.getCurrentFocus()!=null){
    		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
    			    InputMethodManager.HIDE_NOT_ALWAYS);
    	}

    }



    /**
     * Valide que les champs obligatoires du deuxi�me ecran sont bien remplis
     */
    public static void validateFirstFields(){

    	if(validateFirstDate && validateSecondDate){
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


    /**
     * Valide que les champs obligatoires du deuxi�me ecran sont bien remplis
     */
    public static void validateSecondFields(){


    	if((validateDescription==1)&&(validateAdress==1)){
    		validateSecond = 1;
    		myMenu.findItem(R.id.right_options_creation).setIcon(R.drawable.check);
    		myMenu.findItem(R.id.right_options_creation).setEnabled(true);
    	}
    	else{
    		System.out.println("PAS ENCORE BON");
    		validateSecond = 0;
    		myMenu.findItem(R.id.right_options_creation).setIcon(R.drawable.check_disabled);
    		myMenu.findItem(R.id.right_options_creation).setEnabled(false);
    	}


    }


    /**
     * L'utilisateur d�cide de changer la photo du moment
     * @param view
     */

    public void changePhoto(View view){
    	openImageIntent();
    }


    /**
     * Fonction qui detecte clic sur bouton Lieu et envoie vers le choix du lieu
     */

    public void choosePlace(View view) {
        Intent intent = new Intent(this, PlacePickerActivity.class);
        startActivityForResult(intent, PLACE_CHOOSE);
    }




    /**
     * Gestion prise photo
     */


    private void openImageIntent() {

    	// Determine Uri of camera image to save.
    	final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Moment" + File.separator + "Images");
    	System.out.println(Environment.getExternalStorageDirectory() + File.separator + "Moment" + File.separator + "Images");
    	root.mkdirs();
    	final String fname = "photo_moment.png";
    	final File sdImageMainDirectory = new File(root, fname);
    	outputFileUri = Uri.fromFile(sdImageMainDirectory);


    	    // Camera.
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

    	    // Filesystem.
    	    final Intent galleryIntent = new Intent();
    	    galleryIntent.setType("image/*");
    	    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

    	    // Chooser of filesystem options.
    	    final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

    	    // Add the camera options.
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

                try {
                	//On recupere l'image, on la sauvegarde dans l'internal storage et on l'efface de l'external
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

					Log.d("URI", selectedImageUri.toString() + " "+ selectedImageUri.getHost());
					
					
					/*
					Cursor c = getContentResolver().query(selectedImageUri, null, null, null, null); 

	                if (c.moveToFirst()) { 
	                        do { 
	                                int max = c.getColumnCount(); 
	                                for (int i = 0; i < max; i++) { 
	                                        String colName = c.getColumnName(i); 
	                                        String value = c 
	                                                        .getString(c.getColumnIndex(colName)); 

	                                        if (colName != null){ 
	                                                Log.d("columnName: ", colName); 
	                                        } 

	                                        if (value != null) { 
	                                                Log.d("value", value); 
	                                        } 
	                                } 
	                        } while (c.moveToNext()); 
	                } 
					
	                String name = null;
	                String path = null;
					Cursor cursor = getContentResolver().query(selectedImageUri, null, null, null, null);
				    if(cursor.moveToFirst()){;
				       int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
				       name = cursor.getString(column_index);
				    }
				    
				    path = File.separator + selectedImageUri.getHost() + selectedImageUri.getPath() + File.separator + name;
				    Log.d("FINALE PATH !!!!", path);
				  
					*/


					bitmap = Images.resizeBitmap(bitmap, 1000);
					Images.saveImageToInternalStorage(bitmap, getApplicationContext(), "cover_picture", 90);


					AppMoment.getInstance().addBitmapToMemoryCache("cover_moment_"+this.moment.getName().toLowerCase(), bitmap);
					this.moment.setKeyBitmap("cover_moment_"+this.moment.getName().toLowerCase());

					ImageView moment_image = (ImageView)findViewById(R.id.creation_moment_image);
					moment_image.setImageBitmap(bitmap);
					//profile_picture = Images.resizeBitmap(bitmap, 600);
					//Images.saveImageToInternalStorage(profile_picture, getApplicationContext(), "profile_picture", 100);

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

            //Finish activity
            if(requestCode == POP_UP_CREA){
                if(resultCode == RESULT_OK){
                    if(data.getExtras().containsKey("privacy")){
                        AppMoment.getInstance().user.getMomentById(moment.getId()).setPrivacy(data.getIntExtra("privacy", 0));
                        AppMoment.getInstance().user.getMomentById(moment.getId()).setIsOpenInvit(data.getBooleanExtra("isOpenInvit", false));
                    }

                    //We go the invit part, we pass the id of the moment
                    Intent intent = new Intent(CreationDetailsActivity.this, MomentInfosActivity.class);
                    intent.putExtra("id", moment.getId());
                    intent.putExtra("precedente", "creation");

                    startActivity(intent);
                    finish();

                }
            }
            if(requestCode == PLACE_CHOOSE){

                System.out.println("PLACES RESULT PAS ENCORE REPONSE");

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

    /**
     * Can be called by the fragment if all the fields are already filled when the fragment is built
     */

    public void validateFirstStep(){
        validateFirstDate = true;
        validateSecondDate = true;
        validateFirstFields();
    }

    /**
     * Validate that the first date is superior to the second one
     */

    public boolean areDatesCorrect(){

        if(fragment.getEndDate().after(fragment.getStartDate())) return true;
        else return false;

    }


}
