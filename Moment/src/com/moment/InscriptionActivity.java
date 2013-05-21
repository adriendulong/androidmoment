package com.moment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ComponentName;
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
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gcm.GCMRegistrar;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moment.classes.AppMoment;
import com.moment.classes.CommonUtilities;
import com.moment.classes.Images;
import com.moment.classes.MomentApi;
import com.moment.classes.User;

public class InscriptionActivity extends SherlockActivity {

	private Uri outputFileUri;
	private int YOUR_SELECT_PICTURE_REQUEST_CODE = 0;
	private Bitmap profile_picture;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inscription);
		
		
		/**
         * On enregistre le device pour les notifs push
         */
        
        GCMRegistrar.checkDevice(this);
        
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);

        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
            	Log.v("GCM", "Already registered and on server");

            } else {
                
            	Log.v("GCM", "Not registered and on server");

            }
        }
	}

	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	     //getSupportMenuInflater().inflate(R.menu.activity_creation, menu);
	     return true;
	 }

	    
	 @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	     switch (item.getItemId()) {
	          case android.R.id.home:
	             NavUtils.navigateUpFromSameTask(this);
	             return true;
	     }
	     return super.onOptionsItemSelected(item);
	  }
	 
	 public void selectImage(View view){
		 openImageIntent();
	 }
	 
	 
	 /** Appeler lorsque l'utilisateur clique sur le bouton de inscription */
	    public void inscription(View view) throws JSONException {
	       Log.d("Connection", "conncetion reussi");
	       
	       EditText nomEdit = (EditText)findViewById(R.id.inscription_nom);
	       String nom = nomEdit.getText().toString();
	       
	       EditText prenomEdit = (EditText)findViewById(R.id.inscription_prenom);
	       String prenom = prenomEdit.getText().toString();
	       
	       EditText emailEdit = (EditText)findViewById(R.id.inscription_email);
	       String email = emailEdit.getText().toString();
	       
	       EditText mdpEdit = (EditText)findViewById(R.id.inscription_mdp);
	       String mdp = mdpEdit.getText().toString();
	       
	       Log.d("Nom Prénom Email MDP", ""+nom+" "+prenom+" "+email+" "+mdp);
	       
	       
	       
	       //On fait la requete pour créer le comtpe
	       RequestParams params = new RequestParams();
	       params.put("firstname", prenom);
	       params.put("lastname", nom);
	       params.put("password", mdp);
	       params.put("email", email);
	       
	       //Toutes les infos sur le tel et le push
	       if (!GCMRegistrar.getRegistrationId(this).equals("")) params.put("notif_id", GCMRegistrar.getRegistrationId(this));
	    	params.put("os", "1");
	    	params.put("os_version", android.os.Build.VERSION.RELEASE);
	    	params.put("model", CommonUtilities.getDeviceName());
	    	params.put("device_id", AppMoment.getInstance().tel_id);
	       

	       File image = getApplicationContext().getFileStreamPath("profile_picture");
	       if (image!=null){
		       try {
		    	    params.put("photo", image);
		    	    image.delete();
		    	} catch(FileNotFoundException e) {}
	       }
	       
	       //On créé notre futur User
	       AppMoment.getInstance().user = new User(email);
	       
	       //On initialise le User (qu'on effacera si le register ne marche pas
	       AppMoment.getInstance().user.setFirstname(prenom);
	       AppMoment.getInstance().user.setLastname(nom);
	       
	       
	       MomentApi.initialize(getApplicationContext());
	       MomentApi.post("register", params, new JsonHttpResponseHandler() {
	            @Override
	            public void onSuccess(JSONObject response) {
	            		int id = -1;
	            	
		            	try {
							id = response.getInt("id");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            	
		            	AppMoment.getInstance().user.setId(id);
		            	
		               System.out.println(response);
		               System.out.println(MomentApi.myCookieStore.getCookies().get(0).toString());
		               
		               //On recupere les infos du user
		               MomentApi.get("user", null, new JsonHttpResponseHandler() {
				            @Override
				            public void onSuccess(JSONObject response) {
				            	try {
									String firstname = response.getString("firstname");
									AppMoment.getInstance().user.setFirstname(firstname);
									
									String lastname = response.getString("lastname");
									AppMoment.getInstance().user.setLastname(lastname);
									
									if(response.has("profile_picture_url")){
										String profile_picture_url = response.getString("profile_picture_url");
										AppMoment.getInstance().user.setPicture_profile_url(profile_picture_url);
									}
									
									
								    
								    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						       				InscriptionActivity.this);
						        
						       			// set title
						       			alertDialogBuilder.setTitle("Compte créé");
						        
						       			// set dialog message
						       			alertDialogBuilder
						       				.setMessage("Le compte a été créé :)")
						       				.setCancelable(false)
						       				.setPositiveButton("Cool",new DialogInterface.OnClickListener() {
						       					@Override
												public void onClick(DialogInterface dialog,int id) {
						       						// if this button is clicked, close
						       						// current activity
						       						Intent intent = new Intent(InscriptionActivity.this, TimelineActivity.class);
						       				       startActivity(intent);
						       					}
						       				  });
						        
						       				// create alert dialog
						       				AlertDialog alertDialog = alertDialogBuilder.create();
						        
						       				// show it
						       				alertDialog.show();
							    	
									
									
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				            }
				        });
		               
		               
		       		}
	            
	            
	            @Override
	            public void onFailure(Throwable e, JSONObject errorResponse) {
	            	System.out.println(errorResponse.toString());
	            	
	            	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
		       				InscriptionActivity.this);
		        
		       			// set title
		       			alertDialogBuilder.setTitle("Compte créé");
		        
		       			// set dialog message
		       			alertDialogBuilder
		       				.setMessage(errorResponse.toString())
		       				.setCancelable(false)
		       				.setPositiveButton("Mince !",new DialogInterface.OnClickListener() {
		       					@Override
								public void onClick(DialogInterface dialog,int id) {
		       						// if this button is clicked, close
		       						// current activity
		       						dialog.cancel();
		       					}
		       				  });
		        
		       				// create alert dialog
		       				AlertDialog alertDialog = alertDialogBuilder.create();
		        
		       				// show it
		       				alertDialog.show();
	            	
	            	//On reintialise le user
	            	AppMoment.getInstance().user = null;
	            }
	        });
	       
	       /*
	       AsyncHttpClient client = new AsyncHttpClient();
	       final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
	       client.setCookieStore(myCookieStore);
	       client.post("http://api.appmoment.fr/register", params, new AsyncHttpResponseHandler() {
	           @Override
	           public void onSuccess(String response) {
	               System.out.println(response);
	               System.out.println(myCookieStore.getCookies().get(0).toString());
	           }
	       });*/
	       
	    }
	    
	    
	    /**
	     * Retour vers le premier écran
	     */
	    
	    public void retour(View view){
	    	Intent intent = new Intent(this, MomentActivity.class);
		    startActivity(intent);
	    }

	    
	    private void openImageIntent() {

	    	// Determine Uri of camera image to save.
	    	final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Moment" + File.separator + "Images");
	    	System.out.println(Environment.getExternalStorageDirectory() + File.separator + "Moment" + File.separator + "Images");
	    	root.mkdirs();
	    	final String fname = "profile_picture.jpg";
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
						//Images.saveImageToInternalStorage(bitmap, getApplicationContext(), "profile_picture", 100);
						//getContentResolver().delete(selectedImageUri, null, null);
						
						//this.profile_picture = Images.getBitmapFromInternalStorage("profile_picture", getApplicationContext());
						//System.out.println(""+profile_picture.getHeight());
						
						ImageButton profile_picture_button = (ImageButton)findViewById(R.id.profile_picture);
						profile_picture_button.setImageBitmap(Images.getRoundedCornerBitmap(bitmap));
						profile_picture = Images.resizeBitmap(bitmap, 300);
						Images.saveImageToInternalStorage(profile_picture, getApplicationContext(), "profile_picture", 100);
						
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
