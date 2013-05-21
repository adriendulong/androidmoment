package com.moment.infos.fragments;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.moment.InvitationActivity;
import com.moment.R;
import com.moment.classes.AppMoment;
import com.moment.classes.InvitationsAdapter;
import com.moment.classes.User;

public class InvitationsFragment extends Fragment {
	
	public static final String POSITION = "Position";
	private ListView listView;
	private ArrayList<User> users;
	// facebook vars
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;
	private int position;
	
	public InvitationsFragment(){
		
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
		
		
        View rootView = inflater.inflate(
                R.layout.activity_invitations_fragment, container, false);
        listView = (ListView)rootView.findViewById(R.id.list_view_contacts);
        
        Myonclicklistneer myClickList = new Myonclicklistneer();
        listView.setOnItemClickListener(myClickList);
        
        
       
        
        
        Bundle args = getArguments();
        position = args.getInt(POSITION);
        
        System.out.println(""+position);
        if(users==null){
        	if (position == 1){
        		System.out.println("CCCOOONNNTTTACCCCTTSS");
        		users = new ArrayList<User>();
            	ContactLoader asyncContact = new ContactLoader();
            	asyncContact.execute(true);
            	

            }
            else if (position == 0){
            	users = new ArrayList<User>();
            	mFacebook = new Facebook(AppMoment.APP_FB_ID);
     	       mAsyncRunner = new AsyncFacebookRunner(mFacebook);
            }
            else {
            	users = new ArrayList<User>();
            	User u = new User("&@&.com");
            	u.setFirstname("OOO");
            	users.add(u);
            	InvitationsAdapter adapter = new InvitationsAdapter(getActivity().getApplicationContext(), R.layout.invitations_cell, users);
                listView.setAdapter(adapter);

            }
        }
        
        
        
        
        
        return rootView;
    }
	
	
	
	
	/**
	 * Recupere tous les contacts et en créé des users
	 */
	

	
	private void getContacts2(){

			ContentResolver cr = getActivity().getContentResolver();
			
			Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME);

	
			while (cursor.moveToNext()) {
				
				String id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                
                if(!name.contains("@")){
                	
                	System.out.println("Id : "+id+"  fname:"+ name);
                    
                    User user = new User();
                    user.setFirstname(name);
                    user.setId_carnet_adresse(id);
                    
                    
                    //Photo
                    
                    ImagesContactLoader imContact = new ImagesContactLoader();
                    imContact.execute(user);
                    
                    /*
                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    	
                    	Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = ?", new String[] { id }, null);
                    	
                    	while (pCur.moveToNext()) {
                            String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            System.out.println("phone" + phone);
                            if(user[0].getNumTel()==null){
                            	user[0].setNumTel(phone);
                            }
                            else if(user[0].getSecondNumTel()==null) user[0].setSecondNumTel(phone);
                            

                        }
                        pCur.close();
                    	
                    }*/
                    
                   
                    
                    
                    //Il a des telephones
                    
                    
                    //On rajoute le user à la liste
                    users.add(user);
                	
                }
                  
			}

			
		}
	
	
	/**
	 * Recuperer photos contacts
	 * @param contactId
	 * @return
	 */
	
	
	public InputStream openPhoto(long contactId) {
	     Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
	     Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
	     Cursor cursor = getActivity().getContentResolver().query(photoUri,
	          new String[] {Contacts.Photo.PHOTO}, null, null, null);
	     if (cursor == null) {
	         return null;
	     }
	     try {
	         if (cursor.moveToFirst()) {
	             byte[] data = cursor.getBlob(0);
	             if (data != null) {
	                 return new ByteArrayInputStream(data);
	             }
	         }
	     } catch (SQLException e) {
	          Log.e("e", "sql exception in dbio_count", e);            
	        } catch(Exception ex) {
	          Log.e("e", "other exception in dbio_count", ex);
	        } finally {
	          if (cursor != null) {
	        	  cursor.close();
	          }
	        }
	     return null;
	 }
	
	
	
	/**
	 * Load async des contacts
	 * @author adriendulong
	 *
	 */
	
	private class ContactLoader extends AsyncTask<Boolean, Integer, Void>
	{
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Toast.makeText(getActivity().getApplicationContext(), "Début du traitement asynchrone", Toast.LENGTH_LONG).show();
			dialog = ProgressDialog.show(getActivity(), null, "Chargements des contacts");
			
		}

		@Override
		protected void onProgressUpdate(Integer... values){
			super.onProgressUpdate(values);
			// Mise à jour de la ProgressBar
			
		}

		@Override
		protected Void doInBackground(Boolean... arg0) {

			getContacts2();
			
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			InvitationsAdapter adapter = new InvitationsAdapter(getActivity().getApplicationContext(), R.layout.invitations_cell, users);
            listView.setAdapter(adapter);
			Toast.makeText(getActivity().getApplicationContext(), "Le traitement asynchrone est terminé", Toast.LENGTH_LONG).show();
			dialog.dismiss();
		}
	}
	
	
	
	
	/**
	 * Load async des Images des contacts
	 * @author adriendulong
	 *
	 */
	
	private class ImagesContactLoader extends AsyncTask<User, Integer, User>
	{
		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			
		}

		@Override
		protected void onProgressUpdate(Integer... values){
			super.onProgressUpdate(values);
			// Mise à jour de la ProgressBar
			
		}

		@Override
		protected User doInBackground(User... user) {

			ContentResolver cr = getActivity().getContentResolver();
			
			InputStream photoStream = openPhoto(Long.parseLong(user[0].getId_carnet_adresse()));
            if(photoStream!=null){
            	user[0].setPhoto_thumbnail(BitmapFactory.decodeStream(photoStream));
            }
            
            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = ?", new String[] { user[0].getId_carnet_adresse() }, null);
            
            	try{
            		
            		while (pCur.moveToNext()) {
                        String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //System.out.println("phone" + phone);
                        if(user[0].getNumTel()==null){
                        	user[0].setNumTel(phone);
                        }
                        else if(user[0].getSecondNumTel()==null) user[0].setSecondNumTel(phone);
                        

                    }
            		
            	} catch (SQLException e) {
      	          Log.e("e", "sql exception in dbio_count", e);            
    	        } catch(Exception ex) {
    	          Log.e("e", "other exception in dbio_count", ex);
    	        } finally {
    	          if (pCur != null) {
    	        	  pCur.close();
    	          }
    	        }
            	
            	
            	Cursor eCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID+ " = ?", new String[] { user[0].getId_carnet_adresse() }, null);
                
            	try{
            		
            		while (eCur.moveToNext()) {
                        String email = eCur.getString(eCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                        //System.out.println("email" + email);
                        if(user[0].getEmail()==null){
                        	user[0].setEmail(email);
                        }
                        else if(user[0].getSecondEmail()==null) user[0].setSecondEmail(email);
                        

                    }
            		
            	} catch (SQLException e) {
      	          Log.e("e", "sql exception in dbio_count", e);            
    	        } catch(Exception ex) {
    	          Log.e("e", "other exception in dbio_count", ex);
    	        } finally {
    	          if (eCur != null) {
    	        	  eCur.close();
    	          }
    	        }   	
			
			return user[0];
		}

		@Override
		protected void onPostExecute(User user) {
			
			
		}
	}
	
	
	/**
	 * Lorsqu'on arrive dans facebook onglet
	 * @param view
	 */
	
	public void facebook() {
	       Log.d("Connection", "conncetion reussi");
	       
	       
	       if(mFacebook.isSessionValid()){
	    	   mAsyncRunner.request("me/friends", new FriendsRequestListener()); 
	       }
	       else mFacebook.authorize(getActivity(), AppMoment.PERMS_FB, new LoginDialogListener());
	       
	    }
	
	/**
	    * Class utilisé lrsque l'utilisateur se connecte à Facebook 
	    * @author adriendulong
	    *
	    */
	   
	private class LoginDialogListener implements DialogListener {
	    	 
    	@Override
		public void onComplete(Bundle values) {
    		Log.d("FACEBOOK", "OK");
    		mAsyncRunner.request("me/friends", new FriendsRequestListener());
    	}
     
    	@Override
		public void onFacebookError(FacebookError e) {
    		// TODO Auto-generated method stub
     
    	}

    	@Override
		public void onError(DialogError e) {
    		// TODO Auto-generated method stub
     
    	}
	     
    	@Override
		public void onCancel() {
    		// TODO Auto-generated method stub
     
    	}
    }
	
	
	
	/**
	 * Recupere les infos des amis
	 */
	
	/**
     * Class utilisée lorsque l'on recupere les évènements FB de l'utilisateur
     * @author adriendulong
     *
     */
    
    private class FriendsRequestListener implements RequestListener {
    	
    	String fbReponse;
    	 
    	@Override
		public void onComplete(String response, Object state) {
    		try {
    			// process the response here: executed in background thread
    			Log.d("FACEBOOK RESP", "Response: " + response.toString());
    			fbReponse = response;
    			
    			final JSONObject json = new JSONObject(response);
    			JSONArray d = json.getJSONArray("data");
    			
    			for (int i = 0; i < d.length(); i++) {
    				JSONObject friend = d.getJSONObject(i);
    				
    				User user = new User();
    				user.setFirstname(friend.getString("name"));
    				user.setFacebookId(friend.getInt("id"));
    				user.setFb_photo_url("http://graph.facebook.com/"+ user.getFacebookId() +"/picture");
    				users.add(user);
     
    			}
     
	     
    			
     
    			//Lorsque l'on a les evenements on les affiche dans une nouvelle activité
    			getActivity().runOnUiThread(new Runnable() {
    				@Override
					public void run() {
    					InvitationsAdapter adapter = new InvitationsAdapter(getActivity().getApplicationContext(), R.layout.invitations_cell, users);
    	                listView.setAdapter(adapter);
    				}
    			});
    		} catch (JSONException e) {
    			Log.w("Facebook Erro", "JSON Error in response");
    		}
    	}
     
    	@Override
		public void onIOException(IOException e, Object state) {
    		// TODO Auto-generated method stub
     
    	}
     
    	@Override
		public void onFileNotFoundException(FileNotFoundException e,
    			Object state) {
    		// TODO Auto-generated method stub
     
    	}
     
    	@Override
		public void onMalformedURLException(MalformedURLException e,
    			Object state) {
    		// TODO Auto-generated method stub
     
    	}
     
    	@Override
		public void onFacebookError(FacebookError e, Object state) {
    		// TODO Auto-generated method stub
     
    	}
    }
    
    
    
    //Listener list
    class Myonclicklistneer implements OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
	        
        	if(users.get(position).getEmail()!=null) Log.d("SAVE EEEEEEEE Email : ", users.get(position).getEmail());
        	if(users.get(position).getNumTel()!=null) Log.d("SAVE EEEEEEEE Email : ", users.get(position).getNumTel());
        	
        	if(!users.get(position).getIs_selected()){
        		View v = view.findViewById(R.id.bg_cell_invitations);
	            v.setBackgroundColor(getResources().getColor(R.color.orange));
	            
	           InvitationActivity.invitesUser.add(users.get(position));
	           InvitationActivity.nb_invites.setText(""+InvitationActivity.invitesUser.size());
	           users.get(position).setIs_selected(true);
        	}
        	else{
        		RelativeLayout v = (RelativeLayout)view.findViewById(R.id.bg_cell_invitations);
	            v.setBackgroundResource(R.drawable.background);
	            InvitationActivity.invitesUser.remove(users.get(position));
	            
	            users.get(position).setIs_selected(false);
        	}
        		

        }

    }
    
    

}



