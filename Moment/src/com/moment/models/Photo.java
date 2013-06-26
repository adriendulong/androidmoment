package com.moment.models;

import android.graphics.Bitmap;

import com.moment.classes.PhotoListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Photo {
	
	private int id;
	private int nbLike;
	private User user;
	private Bitmap bitmapOriginal;
	private Bitmap bitmapThumbnail;
	private String urlOriginal;
	private String urlThumbnail;
    private Date time;
	
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }


    public PhotoListAdapter getAdapter(PhotoListAdapter pa){
		return pa;
	}
	
	public void photoFromJSON(JSONObject photoObject){
		try {
			this.setId(photoObject.getInt("id"));
			this.setNbLike(photoObject.getInt("nb_like"));
			this.setUrlOriginal(photoObject.getString("url_original"));
			this.setUrlThumbnail(photoObject.getString("url_thumbnail"));
            Date timestamp = new Date(Long.valueOf(photoObject.getString("time"))*1000);

            this.setTime(timestamp);
			User user = new User();
			user.setUserFromJson(photoObject.getJSONObject("taken_by"));
			this.setUser(user);			
		} catch (JSONException e) {e.printStackTrace();}	
	}
}	