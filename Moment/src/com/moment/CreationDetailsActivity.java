package com.moment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
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
import android.text.Editable;
import android.text.TextWatcher;
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
import com.moment.classes.AppMoment;
import com.moment.classes.Images;
import com.moment.classes.Moment;
import com.moment.classes.MomentApi;
import com.moment.infos.fragments.CreationStep1Fragment;
import com.moment.infos.fragments.CreationStep2Fragment;

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
	private int validateFirst = 0;
	private static int validateSecond = 0;
	public static int validateDescription = 0;
	public static int validateAdress = 0;
	public static int validateInfosLieu = 0;
	private Uri outputFileUri;
	private int YOUR_SELECT_PICTURE_REQUEST_CODE = 1;
	private ProgressDialog dialog;
	
	//Permet de savoir quel picker est entrain d'etre choisi (0 pour debut, 1 pour fin)
	static int pickerChosen = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_creation_details);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        //On recupere le nom du moment
        String nomMoment = getIntent().getStringExtra("nomMoment");
        Log.d("Nom Moment", nomMoment);
        
        //On cree l'objet Moment qui servira pendant toute la creation
        moment = new Moment();
        
        moment.setName(nomMoment);
        
        
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
    		menu.findItem(R.id.left_options_creation).setVisible(false);
    		menu.findItem(R.id.right_options_creation).setIcon(R.drawable.btn_flechedown);
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
            		
            		downTwo();
            		
            		myMenu.findItem(R.id.left_options_creation).setVisible(true);
            		myMenu.findItem(R.id.right_options_creation).setIcon(R.drawable.check_disabled);
            		
            		step = 1;
            		
            		
            		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
         	       fragmentTransaction.setCustomAnimations(R.anim.custom_in,R.anim.custom_out);
         	       
         	       fragmentTransaction.replace(android.R.id.content, fragment2);
         	       fragmentTransaction.commit();

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
	       
	       moment.setInfoLieu(adresseInfoLieu.getText().toString());
	       moment.setInfoTransport(adresseInfoTransport.getText().toString());
	       moment.setAdresse(adresse.getText().toString());
	       //moment.setTitre(nomLieu.getText().toString());
	       
	       
	       
	       
	       FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
	       fragmentTransaction.setCustomAnimations(R.anim.custom_in,R.anim.custom_out);
	       
	       fragmentTransaction.replace(android.R.id.content, fragment2);
	       fragmentTransaction.commit();
	       
	    }
    
    
    /**
     * Une fois que le 2eme ecran est apparu on ecoute tout changement
     */
    
    public void prepareTwo(){
    	
    	EditText descriptionEdit = (EditText)findViewById(R.id.creation_moment_description);
    	EditText adresseEdit = (EditText)findViewById(R.id.creation_moment_adresse);
    	EditText infosLieuEdit = (EditText)findViewById(R.id.creation_moment_infos_lieu);

    	descriptionEdit.addTextChangedListener(new TextWatcher(){
            @Override
			public void afterTextChanged(Editable s) {}
            @Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
            	if(count >0) validateDescription = 1;
            	else validateDescription = 0;
            	
            	System.out.println("CA BOUGE");
            	
            	CreationDetailsActivity.validateSecondFields();
            }
        }); 
    	
    	
    	adresseEdit.addTextChangedListener(new TextWatcher(){
            @Override
			public void afterTextChanged(Editable s) {}
            @Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
            	if(count >0) validateAdress = 1;
            	else validateAdress = 0;
            	
            	CreationDetailsActivity.validateSecondFields();
            }
        }); 
    	
    	
    	infosLieuEdit.addTextChangedListener(new TextWatcher(){
            @Override
			public void afterTextChanged(Editable s) {}
            @Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
            	if(count >0) validateInfosLieu = 1;
            	else validateInfosLieu = 0;
            	
            	CreationDetailsActivity.validateSecondFields();
            }
        }); 
    	
    }
    
    
    /**
     * GEre le passage � la 2eme etape
     * @param view
     */
    
    public void downTwo() {
	       Log.d("Down", "DOWN OK");  
	       
	       
	       /**
	        * On enregistre les dates
	        */
	       
	       Button dateDebutEdit = (Button)findViewById(R.id.date_debut_button);
	       Button heureDebutEdit = (Button)findViewById(R.id.heure_debut_button);
	       Button dateFinEdit = (Button)findViewById(R.id.date_fin_button);
	       Button heureFinEdit = (Button)findViewById(R.id.heure_fin_button);
	       
	       
	       //On cr�� la date de d�but
	       int jourDebut = Integer.parseInt(dateDebutEdit.getText().toString().split("/")[0]);
	       int moisDebut = Integer.parseInt(dateDebutEdit.getText().toString().split("/")[1])-1;
	       int anneeDebut = Integer.parseInt(dateDebutEdit.getText().toString().split("/")[2]);
	       
	       GregorianCalendar calendarDebut = new GregorianCalendar(anneeDebut, moisDebut, jourDebut);
	       
	       System.out.println(calendarDebut.toString());
	       
	       
	       //On enregistre la date de d�but
	       moment.setDateDebut(calendarDebut.getTime());
	       System.out.println(moment.getDateDebut().toString());
	       
	       //On cr�� la date de fin
	       int jourFin = Integer.parseInt(dateFinEdit.getText().toString().split("/")[0]);
	       int moisFin = Integer.parseInt(dateFinEdit.getText().toString().split("/")[1])-1;
	       int anneeFin = Integer.parseInt(dateFinEdit.getText().toString().split("/")[2]);
	       
	       GregorianCalendar calendarFin = new GregorianCalendar(anneeFin, moisFin, jourFin);
	       
	       System.out.println(calendarFin.toString());
	       
	       
	       //On enregistre la date de fin
	       moment.setDateFin(calendarFin.getTime());
	       
	       System.out.println(moment.getDateFin().toString());
	       
	    }
    
    
    /**
     * Gere le retour � la step 1
     * @param v
     */
    
    public void upOne(){
    	
    	EditText description = (EditText)findViewById(R.id.creation_moment_description);
    	if(description.getText()!=null){
    		this.moment.setDescription(description.getText().toString());
    	}
    	
    	EditText adresse = (EditText)findViewById(R.id.creation_moment_adresse);
    	if(adresse.getText()!=null){
    		this.moment.setAdresse(adresse.getText().toString());
    	}
    	
    	EditText infosLieu = (EditText)findViewById(R.id.creation_moment_infos_lieu);
    	if(infosLieu.getText()!=null){
    		this.moment.setInfoLieu(infosLieu.getText().toString());
    	}
    	
    	EditText hashtag = (EditText)findViewById(R.id.creation_moment_hashtag);
    	if(hashtag.getText()!=null){
    		this.moment.setHashtag(hashtag.getText().toString());
    	}

    }
    
    
    /**
     * Gere le retour � la step 2
     * @param v
     */
    
    public void upThree(View v){
    	
    	FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    	fragmentTransaction.setCustomAnimations(R.anim.custom_in_inverse,R.anim.custom_out_inverse);
	       
	    fragmentTransaction.replace(android.R.id.content, fragment2);
	    fragmentTransaction.commit();
	    
	    //Date debut
        /*Calendar calDebut = Calendar.getInstance();
        calDebut.setTime(moment.getDateDebut());
        int anneeDebut = calDebut.get(Calendar.YEAR);
        int moisDebut = calDebut.get(Calendar.MONTH);
        int jourDebut = calDebut.get(Calendar.DAY_OF_MONTH);
    	
    	TextView dateDebut = (TextView)findViewById(R.id.spinner_date_debut);
    	dateDebut.setText(""+jourDebut+"/"+moisDebut+"/"+anneeDebut);
    	
    	
    	//DateFin
    	Calendar calFin = Calendar.getInstance();
        calFin.setTime(moment.getDateFin());
        int anneeFin = calDebut.get(Calendar.YEAR);
        int moisFin = calDebut.get(Calendar.MONTH);
        int jourFin = calDebut.get(Calendar.DAY_OF_MONTH);
    	
    	TextView dateFin = (TextView)findViewById(R.id.spinner_date_fin);
    	dateFin.setText(""+jourFin+"/"+moisFin+"/"+anneeFin);*/
    }
    
    
    /**
     * Gere le date picker pour choisir la date de d�but
     * @param view
     */
    
    public void dateDebut(View view) {
    	
    	//On ouvre le date picker
    	DialogFragment newFragment = new DatePickerFragment((Button)view.findViewById(R.id.date_debut_button));
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
				this.heureEdit.setText(""+hourOfDay+":"+minute);
				
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
    	
    	Button dateEdit;
    	
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
			// Do something with the date chosen by the user
			//updateTextDate(year, month, day);
			this.dateEdit.setText(""+day+"/"+(month+1)+"/"+year);
		
		}
	}
    
    
    /**
     * Fonction appel�e quand on clique sur cr�ation d'un moment
     * @param view
     * @throws JSONException 
     *  
     */
    
    public void creerMoment() throws JSONException {
    	EditText descriptionEdit = (EditText)findViewById(R.id.creation_moment_description);
    	EditText adresseEdit = (EditText)findViewById(R.id.creation_moment_adresse);
    	EditText infosLieuEdit = (EditText)findViewById(R.id.creation_moment_infos_lieu);
    	
    	EditText hashtagEdit = (EditText)findViewById(R.id.creation_moment_hashtag);
    	
    	
    	
    	
    	
    	Log.d("Description", descriptionEdit.getText().toString());
    	moment.setDescription(descriptionEdit.getText().toString());
    	moment.setAdresse(adresseEdit.getText().toString());
    	moment.setInfoLieu(infosLieuEdit.getText().toString());
    	if(hashtagEdit != null) moment.setHashtag(hashtagEdit.getText().toString());
    
    	dialog = ProgressDialog.show(this, null, "Cr�ation en cours");
    	MomentApi.post("newmoment", moment.getMomentRequestParams(getApplicationContext()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
            		try {
            			dialog.dismiss();
            			
            			System.out.println("Cr�er !");
            			
						moment.setId(response.getInt("id"));
						AppMoment.getInstance().user.addMoment(moment);
						
						
						//On lance l'activit� en faisant passer le moment alors qu'on pourrait juste faire passer l'id et le recuperer de AppMoment
						//On lance l'activit� Info Moment
				    	Intent intent = new Intent(CreationDetailsActivity.this, InvitationActivity.class);
				    	//intent.putExtra("precedente", "creation");
				    	intent.putExtra("id", moment.getId());
					    
					    startActivity(intent);
					    finish();
						
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
    public void validateFirstFields(){
    	
    	Button dateDebut = (Button)findViewById(R.id.date_debut_button);
    	Button dateFin = (Button)findViewById(R.id.date_fin_button);
    	
    	if((dateDebut.getText()!=null) && (dateFin.getText()!=null)){
    		this.validateFirst = 1;
    		myMenu.getItem(R.id.right_options_creation).setIcon(R.drawable.btn_flechedown);
    		
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
					
					
					bitmap = Images.resizeBitmap(bitmap, 600);
					Images.saveImageToInternalStorage(bitmap, getApplicationContext(), "cover_picture", 100);
					
					
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
        }
    }
   

}
