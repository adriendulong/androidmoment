package com.moment.models;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import com.moment.AppMoment;
import com.moment.classes.Images;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;

public class User {
	
	private int id = -1;
	private String email;
	private String secondEmail;
	private String firstname;
	private String lastname;
	private String pictureProfileUrl;
	private ArrayList<Moment> moments;
	private String keyBitmap;
	private String numTel;
	private String secondNumTel;
	private int facebookId = 0;
	private String fbPhotoUrl;
	private Boolean isSelected =false;
	private String idCarnetAdresse;
	private String description;
	private int nbFollowers = 0;
	private int nbFollows = 0;

	private Bitmap photo_thumbnail;
    private Bitmap photo_original;

    public User(String email, String firstname, String lastname, String pictureProfileUrl) {
		this.setEmail(email);
		this.setFirstname(firstname);
		this.setLastname(lastname);
		this.setPictureProfileUrl(pictureProfileUrl);
		
		setMoments(new ArrayList<Moment>());
	}

	public User(String email) {
		this.setEmail(email);
		
		setMoments(new ArrayList<Moment>());
	}
	
	public User() {
		// TODO Auto-generated constructor stub
		
		setMoments(new ArrayList<Moment>());
	}

	
	
	/**
	 * Fonction qui permet de rajouter un moment
	 * @param moment
	 */
	
	public void addMoment(Moment moment){
		this.moments.add(moment);
	}
	
	
	/**
	 * Fonction qui renvoit un Moment en fonction de son id
	 * @param id
	 * @return Moment
	 */
	
	public Moment getMoment(int id){
		
		for(int i=0; i<this.moments.size();i++){
			if(this.moments.get(i).getId()==id) return this.moments.get(i);
		}
		
		return null;
	}
	
	
	/**
	 * Getters and Setters
	 * 
	 */
	
	public int getNbFollowers() {
		return nbFollowers;
	}

	public void setNbFollowers(int nbFollowers) {
		this.nbFollowers = nbFollowers;
	}

	public int getNbFollows() {
		return nbFollows;
	}

	public void setNbFollows(int nbFollows) {
		this.nbFollows = nbFollows;
	}

	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPictureProfileUrl() {
		return pictureProfileUrl;
	}

	public void setPictureProfileUrl(String pictureProfileUrl) {
		this.pictureProfileUrl = pictureProfileUrl;
	}

	public ArrayList<Moment> getMoments() {
		return moments;
	}

	public void setMoments(ArrayList<Moment> moments) {
		this.moments = moments;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	

	public String getKeyBitmap() {
		return keyBitmap;
	}

	public void setKeyBitmap(String keyBitmap) {
		this.keyBitmap = keyBitmap;
	}
	
	
	public void printProfilePicture(final ImageView targetView, final Boolean isRounded){
		// On recupere la photo et on l'affiche lorsque on l'a
		System.out.println("Print cover at address : "+ pictureProfileUrl);
		
		if(AppMoment.getInstance().getBitmapFromMemCache("profile_picture_"+id)==null){
		
			AsyncHttpClient client = new AsyncHttpClient();
	    	String[] allowedContentTypes = new String[] { "image/png", "image/jpeg" };
	    	client.get(pictureProfileUrl, new BinaryHttpResponseHandler(allowedContentTypes) {
	    		
	    	    @Override
	    	    public void onSuccess(byte[] fileData) {
	    	        
	    	    	
	    	    	InputStream is = new ByteArrayInputStream(fileData);
	    	    	Bitmap bmp = BitmapFactory.decodeStream(is);
	    	    	AppMoment.getInstance().addBitmapToMemoryCache("profile_picture_"+id, bmp);
	    	    	setKeyBitmap("profile_picture_"+id);
	    	    	
	    	    	if (isRounded) targetView.setImageBitmap(Images.getRoundedCornerBitmap(bmp));
	    	    	else targetView.setImageBitmap(bmp);
	    	    }
	    	    
	    	    @Override
	    	    public void handleFailureMessage(Throwable e, byte[] responseBody) {
	    	    	Log.d("RATEE", "RATEE");
	    	        onFailure(e, responseBody);
	    	    }
	    	});
		}
		else{
			targetView.setImageBitmap(Images.getRoundedCornerBitmap(AppMoment.getInstance().getBitmapFromMemCache("profile_picture_"+id)));
		}
	}

	public Bitmap getPhoto_thumbnail() {
		return photo_thumbnail;
	}

	public void setPhoto_thumbnail(Bitmap photo_thumbnail) {
		this.photo_thumbnail = photo_thumbnail;
	}

	public String getNumTel() {
		return numTel;
	}

	public void setNumTel(String numTel) {
		this.numTel = numTel;
	}

	public int getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(int facebookId) {
		this.facebookId = facebookId;
	}

	public String getFbPhotoUrl() {
		return fbPhotoUrl;
	}

	public void setFbPhotoUrl(String fbPhotoUrl) {
		this.fbPhotoUrl = fbPhotoUrl;
	}

	public Boolean getSelected() {
		return isSelected;
	}

	public void setSelected(Boolean selected) {
		this.isSelected = selected;
	}
	

	public String getSecondEmail() {
		return secondEmail;
	}

	public void setSecondEmail(String secondEmail) {
		this.secondEmail = secondEmail;
	}

	public String getSecondNumTel() {
		return secondNumTel;
	}

	public void setSecondNumTel(String secondNumTel) {
		this.secondNumTel = secondNumTel;
	}
	
	public String getIdCarnetAdresse() {
		return idCarnetAdresse;
	}

	public void setIdCarnetAdresse(String idCarnetAdresse) {
		this.idCarnetAdresse = idCarnetAdresse;
	}

    public Bitmap getPhoto_original() {
        return photo_original;
    }

    public void setPhoto_original(Bitmap photo_original) {
        this.photo_original = photo_original;
    }
	
	/**
	 * Renvoit l'object sous la forme d'un JSONObject
	 * @return
	 * @throws JSONException
	 * @throws FileNotFoundException 
	 */
	
	public JSONObject getUserToJSON() throws JSONException {
		
		JSONObject userJson = new JSONObject();
		
		if(id>0){
			userJson.put("id", this.id); 
		}
		else{
			if(email!=null) userJson.put("email", this.email);
			if(firstname!=null) userJson.put("firstname", this.firstname);
			if(lastname!=null) userJson.put("lastname", this.lastname);
			if(numTel!=null) userJson.put("phone", this.numTel);
			if(facebookId>0) userJson.put("facebookId", this.facebookId);
			if(fbPhotoUrl !=null) userJson.put("photo", this.fbPhotoUrl);
			if(secondEmail!=null) userJson.put("secondEmail", this.secondEmail);
			if(secondNumTel!=null) userJson.put("secondPhone", this.secondNumTel);
			
			
		}
		
		return userJson;
	}


    public void setUserFromJson(JSONObject userJson){
        try {
            this.setId(userJson.getInt("id"));
            if(userJson.has("firstname")) this.setFirstname(userJson.getString("firstname"));
            if(userJson.has("lastname")) this.setLastname(userJson.getString("lastname"));
            if(userJson.has("email")) this.setEmail(userJson.getString("email"));
            if(userJson.has("profile_picture_url")) this.setPictureProfileUrl(userJson.getString("profile_picture_url"));
            if(userJson.has("facebookId")) this.setFacebookId(userJson.getInt("facebookId"));
            if(userJson.has("description")) this.setDescription(userJson.getString("description"));
            if(userJson.has("nbFollowers")) this.setNbFollowers(userJson.getInt("nbFollowers"));
            if(userJson.has("nbFollows")) this.setNbFollows(userJson.getInt("nbFollows"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
	
	@Override 
	public boolean equals(Object aThat) {
	    //check for self-comparison
	    if ( this == aThat ) return true;

	    //use instanceof instead of getClass here for two reasons
	    //1. if need be, it can match any supertype, and not just one class;
	    //2. it renders an explict check for "that == null" redundant, since
	    //it does the check for null already - "null instanceof [type]" always
	    //returns false. (See Effective Java by Joshua Bloch.)
	    if ( !(aThat instanceof User) ) return false;
	    //Alternative to the above line :
	    //if ( aThat == null || aThat.getClass() != this.getClass() ) return false;

	    //cast to native object is now safe
	    User that = (User)aThat;

	    
	    if(this.id == that.id) return true;
	    else if (this.email.equals(that.email)) return true;
	    else if(this.numTel.equals((that.numTel))) return true;
	    else if(this.facebookId == that.facebookId) return true;
	    
	    return false;
	     
	  }

	

}
