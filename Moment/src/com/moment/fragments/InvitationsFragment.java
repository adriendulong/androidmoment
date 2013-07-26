package com.moment.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.activities.InvitationActivity;
import com.moment.classes.CommonUtilities;
import com.moment.classes.InvitationsAdapter;
import com.moment.classes.MomentApi;
import com.moment.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvitationsFragment extends Fragment {

    public static final String POSITION = "Position";
    private ListView listView;
    private ArrayList<User> users;
    private ArrayList<User> showUsers;

    private int position;
    private InvitationsAdapter adapter;

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

        Log.v("INVIT", "création fragment");

        System.out.println(""+position);
        if(savedInstanceState==null){
            if (position == 1){
                System.out.println("CCCOOONNNTTTACCCCTTSS");
                users = new ArrayList<User>();
                ContactLoader asyncContact = new ContactLoader();
                asyncContact.execute(true);


            }
            else if (position == 0){
                users = new ArrayList<User>();
                //facebook();
            }
            else {
                users = new ArrayList<User>();
                Log.v("INVIT", "création fragment Favoris");

                MomentApi.get("favoris", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try{

                            JSONArray favorisUsers = response.getJSONArray("favoris");

                            for(int i=0;i<favorisUsers.length();i++){
                                User tempUser = new User();
                                tempUser.setUserFromJson(favorisUsers.getJSONObject(i));
                                users.add(tempUser);
                            }

                            adapter = new InvitationsAdapter(getActivity().getApplicationContext(), R.layout.invitations_cell, users);
                            listView.setAdapter(adapter);


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    public void onFailure(Throwable error, String content) {
                        // By default, call the deprecated onFailure(Throwable) for compatibility
                        System.out.println(content);
                    }
                });

            }
        }
        else{
            users = savedInstanceState.getParcelableArrayList("users");
            Log.v("INVIT", "NB de users : "+users.size());
            adapter = new InvitationsAdapter(getActivity().getApplicationContext(), R.layout.invitations_cell, users);
            listView.setAdapter(adapter);
        }


        return rootView;
    }

	/*
	private void getContacts2(){

			ContentResolver cr = getActivity().getContentResolver();
			
			Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME);

	
			while (cursor.moveToNext()) {
				
				String id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                
                if(!name.contains("@")){
                	
                	System.out.println("Id : "+id+"  fname:"+ name);
                    
                    User user = new User();
                    user.setFirstName(name);
                    user.setIdCarnetAdresse(id);
                    
                    
                    //Photo
                    
                    ImagesContactLoader imContact = new ImagesContactLoader();
                    imContact.execute(user);
                    

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
                    	
                    }

                   
                    
                    
                    //Il a des telephones
                    
                    
                    //On rajoute le user � la liste
                    users.add(user);
                	
                }
                  
			}

			
		}*/


    /**
     * Recuperer photos contacts
     * @return
     */
	
	/*
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
	 */

    public void updateSearch(String search){

        adapter.getFilter().filter(search);

    }

    /**
     * Recupere tous les contacts et en cr�� des users
     */

    public void readContacts(){

        String[] projection =
                {
                        Contacts._ID,
                        Contacts.DISPLAY_NAME
                };
        String sortOrder = Contacts.DISPLAY_NAME +
                " ASC";

        String where = ContactsContract.Contacts.IN_VISIBLE_GROUP + "= ? ";
        String[] selectionArgs = new String[] { "1" };

        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                projection, where, selectionArgs, sortOrder);

        if (cur.getCount() > 0) {

            while (cur.moveToNext()) {

                User userCur = new User();

                String id = cur.getString(cur.getColumnIndex(Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(Contacts.DISPLAY_NAME));

                System.out.println("ID : "+id+"    NAME : "+name);

                if(!name.contains("@")){
                    userCur.setFirstName(name);
                    userCur.setIdCarnetAdresse(id);

                    users.add(userCur);

                }



            }


        }

        Integer[] positions = new Integer[users.size()];

        int i=0;
        for(User user : users){
            positions[i] = i;
            i++;
        }

        cur.close();

        //We get extra infos
        //ExtrasContactLoader asyncContact = new ExtrasContactLoader();
        //asyncContact.execute(positions);
    }

    /**
     * Lorsqu'on arrive dans facebook onglet
     */

    public void facebook(){
        try {
            openActiveSession(getActivity(), true, fbStatusCallback, Arrays.asList(
                    new String[]{"email"}), null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
    }

    private Session openActiveSession(Activity activity, boolean allowLoginUI,
                                      Session.StatusCallback callback, List<String> permissions, Bundle savedInstanceState) {
        Session.OpenRequest openRequest = new Session.OpenRequest(this).setCallback(callback).
                setDefaultAudience(SessionDefaultAudience.FRIENDS);
        Session session = null;
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(getActivity(), null, fbStatusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(getActivity());
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED) || allowLoginUI) {
                session.openForRead(openRequest);
                return session;
            }
        }
        return null;
    }

    private Session.StatusCallback fbStatusCallback = new Session.StatusCallback() {
        public void call(Session session, SessionState state, Exception exception) {
            if (state.isOpened()) {
                Request.executeMyFriendsRequestAsync(session, new Request.GraphUserListCallback() {
                    public void onCompleted(List<GraphUser> friends, Response response) {
                        if (response != null) {
                            try {

                                JSONArray d = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
                                Log.e("FB", d.toString());
                                adapter = new InvitationsAdapter(getActivity().getApplicationContext(), R.layout.invitations_cell, users);
                                listView.setAdapter(adapter);
                                for (int i = 0; i < d.length(); i++) {
                                    JSONObject friend = d.getJSONObject(i);

                                    User user = new User();
                                    user.setFirstName(friend.getString("name"));
                                    user.setFacebookId(friend.getLong("id"));
                                    user.setFbPhotoUrl("http://graph.facebook.com/" + user.getFacebookId() + "/picture");
                                    users.add(user);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("", "Exception e");
                            }

                        }
                    }
                });
            }
        }
    };

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
            Toast.makeText(getActivity().getApplicationContext(), "D�but du traitement asynchrone", Toast.LENGTH_LONG).show();
            dialog = ProgressDialog.show(getActivity(), null, "Chargements des contacts");

        }

        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
            // Mise � jour de la ProgressBar

        }

        @Override
        protected Void doInBackground(Boolean... arg0) {

            readContacts();


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter = new InvitationsAdapter(getActivity().getApplicationContext(), R.layout.invitations_cell, users);
            listView.setAdapter(adapter);
            Toast.makeText(getActivity().getApplicationContext(), "Le traitement asynchrone est termin�", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    /**
     * Load async des Images des contacts
     * @author adriendulong
     *
     */


    private class ExtrasContactLoader extends AsyncTask<Integer, Integer, Boolean>
    {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
            // Mise � jour de la ProgressBar


        }

        @Override
        protected Boolean doInBackground(Integer... position) {
                /*
                String where = ContactsContract.Contacts.IN_VISIBLE_GROUP + "= ? " +
                        "AND "+
                        ContactsContract.RawContacts.Data._ID+" = ?";
                String[] selectionArgs = new String[] { "1",  users.get(i).getIdCarnetAdresse()};

                String[] projection =
                        {
                                ContactsContract.CommonDataKinds.Email.ADDRESS,
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                        };

                ContentResolver cr = getActivity().getContentResolver();
                Cursor tCur = cr.query(ContactsContract.Data.CONTENT_URI,projection,
                        where,
                        selectionArgs, null);

                while (tCur.moveToNext()) {
                    String email = tCur.getString(tCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                    System.out.println("EMAIL :" + email);
                }

                tCur.close();*/

            ContentResolver cr = getActivity().getContentResolver();

            for(Integer i : position){
                System.out.println("name : " + users.get(i).getFirstName() + ", ID : " + users.get(i).getIdCarnetAdresse());

                // get the phone number
                Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                        new String[]{users.get(i).getIdCarnetAdresse()}, null);
                while (pCur.moveToNext()) {
                    String phone = pCur.getString(
                            pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    System.out.println("phone" + phone);

                    if(users.get(i).getNumTel()==null){
                        users.get(i).setNumTel(phone);
                    }
                    else if(users.get(i).getSecondNumTel()==null){
                        users.get(i).setSecondNumTel(phone);
                        break;
                    }
                }
                pCur.close();


                // get email and type

                Cursor emailCur = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{users.get(i).getIdCarnetAdresse()}, null);
                while (emailCur.moveToNext()) {
                    // This would allow you get several email addresses
                    // if the email addresses were stored in an array
                    String email = emailCur.getString(
                            emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    String emailType = emailCur.getString(
                            emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                    System.out.println("Email " + email + " Email Type : " + emailType);
                    if(users.get(i).getEmail()==null){
                        users.get(i).setEmail(email);
                    }
                    else if(users.get(i).getSecondEmail()==null){
                        users.get(i).setSecondEmail(email);
                        break;
                    }
                }
                emailCur.close();
            }

            return true;

        }

        @Override
        protected void onPostExecute(Boolean completed) {
            if(completed) System.out.println("FINISHHHHHHH !!!!!!!!!!!!");

        }
    }
    
    //Listener list
    class Myonclicklistneer implements OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int positionClick, long arg3) {
            boolean isValid = true;

            if(users.get(positionClick).getEmail()!=null) Log.d("SAVE EEEEEEEE Email : ", users.get(positionClick).getEmail());
            if(users.get(positionClick).getNumTel()!=null) Log.d("SAVE EEEEEEEE Tel : ", users.get(positionClick).getNumTel());

            if(!users.get(positionClick).getIsSelect()){
                InvitationActivity.invitesUser.add(users.get(positionClick));
                InvitationActivity.nb_invites.setText(""+InvitationActivity.invitesUser.size());

                //On va chercher les infos supp sur le user
                if(position==1){
                    if(InvitationActivity.invitesUser.get(InvitationActivity.invitesUser.size()-1).getIdCarnetAdresse()!=null)
                    {
                        getExtrasInfos(InvitationActivity.invitesUser.get(InvitationActivity.invitesUser.size()-1));
                        if(InvitationActivity.invitesUser.get(InvitationActivity.invitesUser.size()-1).getEmail()!=null) Log.d("CONTACTS ", "Email : "+InvitationActivity.invitesUser.get(InvitationActivity.invitesUser.size()-1).getEmail());
                        if(InvitationActivity.invitesUser.get(InvitationActivity.invitesUser.size()-1).getSecondEmail()!=null) Log.d("CONTACTS ", "Email : "+InvitationActivity.invitesUser.get(InvitationActivity.invitesUser.size()-1).getSecondEmail());
                        if(InvitationActivity.invitesUser.get(InvitationActivity.invitesUser.size()-1).getNumTel()!=null) Log.d("CONTACTS", "Tel : "+InvitationActivity.invitesUser.get(InvitationActivity.invitesUser.size()-1).getNumTel());
                    }

                }


                User tempUser = InvitationActivity.invitesUser.get(InvitationActivity.invitesUser.size()-1);
                //Not a good email
                if(tempUser.getNumTel()!=null){
                    if(!CommonUtilities.isValidTel(tempUser.getNumTel())){
                        CommonUtilities.popAlert("Information erronnée", "Numéro de téléphone invalide", "Ok", getActivity());
                        isValid = false;
                    }
                }
                if(tempUser.getEmail()!=null){
                    if(!CommonUtilities.isValidEmail(tempUser.getEmail())){
                        CommonUtilities.popAlert("Information erronnée", "Adresse email invalide", "Ok", getActivity());
                        isValid = false;
                    }
                }

                if(isValid){
                    View v = view.findViewById(R.id.bg_cell_invitations);
                    v.setBackgroundColor(getResources().getColor(R.color.orange));
                    users.get(positionClick).setIsSelect(true);
                }

            }
            else{
                RelativeLayout v = (RelativeLayout)view.findViewById(R.id.bg_cell_invitations);
                v.setBackgroundResource(R.drawable.background);
                InvitationActivity.invitesUser.remove(users.get(positionClick));
                InvitationActivity.nb_invites.setText(""+InvitationActivity.invitesUser.size());

                users.get(positionClick).setIsSelect(false);
            }


        }

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("users", users);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        // etc.
    }


    private void getExtrasInfos(User user){
        ContentResolver cr = getActivity().getContentResolver();
        System.out.println("name : " + user.getFirstName() + ", ID : " + user.getIdCarnetAdresse());

        // get the phone number
        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                new String[]{user.getIdCarnetAdresse()}, null);
        while (pCur.moveToNext()) {
            String phone = pCur.getString(
                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            System.out.println("phone" + phone);

            if(user.getNumTel()==null){
                user.setNumTel(phone);
            }
            else if(user.getSecondNumTel()==null){
                user.setSecondNumTel(phone);
                break;
            }
        }
        pCur.close();


        // get email and type

        Cursor emailCur = cr.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{user.getIdCarnetAdresse()}, null);
        while (emailCur.moveToNext()) {
            // This would allow you get several email addresses
            // if the email addresses were stored in an array
            String email = emailCur.getString(
                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            String emailType = emailCur.getString(
                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

            System.out.println("Email " + email + " Email Type : " + emailType);
            if(user.getEmail()==null){
                user.setEmail(email);
            }
            else if(user.getSecondEmail()==null){
                user.setSecondEmail(email);
                break;
            }
        }
        emailCur.close();
    }



}



