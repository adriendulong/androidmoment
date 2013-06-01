package com.moment.models;

import android.graphics.Bitmap;
import com.moment.classes.PhotoListAdapter;
import org.json.JSONException;
import org.json.JSONObject;

public class Photo {
	
	private int id;
	private int nbLike;
	private User user;
	private Bitmap bitmapOriginal;
	private Bitmap bitmapThumbnail;
	private String urlOriginal;
	private String urlThumbnail;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNbLike() {
		return nbLike;
	}

	public void setNbLike(int nbLike) {
		this.nbLike = nbLike;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Bitmap getBitmapOriginal() {
		return bitmapOriginal;
	}

	public void setBitmapOriginal(Bitmap bitmapOriginal) {
		this.bitmapOriginal = bitmapOriginal;
	}

	public Bitmap getBitmapThumbnail() {
		return bitmapThumbnail;
	}

	public void setBitmapThumbnail(Bitmap bitmapThumbnail) {
		this.bitmapThumbnail = bitmapThumbnail;
	}

	public String getUrlOriginal() {
		return urlOriginal;
	}

	public void setUrlOriginal(String urlOriginal) {
		this.urlOriginal = urlOriginal;
	}

	public String getUrlThumbnail() {
		return urlThumbnail;
	}

	public void setUrlThumbnail(String urlThumbnail) {
		this.urlThumbnail = urlThumbnail;
	}

	public PhotoListAdapter getAdapter(PhotoListAdapter pa){
		return pa;
	}
	
	public void photoFromJSON(JSONObject photoObject){
		try {
			this.setId(photoObject.getInt("id"));
			this.setNbLike(photoObject.getInt("nbLike"));
			this.setUrlOriginal(photoObject.getString("urlOriginal"));
			this.setUrlThumbnail(photoObject.getString("urlThumbnail"));
			User user = new User();
			user.setUserFromJson(photoObject.getJSONObject("taken_by"));
			this.setUser(user);			
		} catch (JSONException e) {e.printStackTrace();}	
	}
}	