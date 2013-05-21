package com.moment.classes;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.moment.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class Photo {
	
	private int id;
	private int nb_like;
	private User user;
	private Bitmap bitmap_original;
	private Bitmap bitmap_thumbnail;
	private String url_original;
	private String url_thumbnail;
	private PhotoListAdapter photo_adapter;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNb_like() {
		return nb_like;
	}

	public void setNb_like(int nb_like) {
		this.nb_like = nb_like;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Bitmap getBitmap_original() {
		return bitmap_original;
	}

	public void setBitmap_original(Bitmap bitmap_original) {
		this.bitmap_original = bitmap_original;
	}

	public Bitmap getBitmap_thumbnail() {
		return bitmap_thumbnail;
	}

	public void setBitmap_thumbnail(Bitmap bitmap_thumbnail) {
		this.bitmap_thumbnail = bitmap_thumbnail;
	}

	public String getUrl_original() {
		return url_original;
	}

	public void setUrl_original(String url_original) {
		this.url_original = url_original;
	}

	public String getUrl_thumbnail() {
		return url_thumbnail;
	}

	public void setUrl_thumbnail(String url_thumbnail) {
		this.url_thumbnail = url_thumbnail;
	}

	public PhotoListAdapter getAdapter(PhotoListAdapter pa){
		return pa;
	}
	
	public void loadPhoto(ImageView imageView) {
		if(url_original != null && !url_original.equals("")) {
			new ImageLoadTask(imageView).execute(url_original);
		}
	}
	
	public void photoFromJSON(JSONObject photoObject){
		try {
			this.setId(photoObject.getInt("id"));
			this.setNb_like(photoObject.getInt("nb_like"));
			this.setUrl_original(photoObject.getString("url_original"));
			this.setUrl_thumbnail(photoObject.getString("url_thumbnail"));
			User user = new User();
			user.setUserFromJson(photoObject.getJSONObject("taken_by"));
			this.setUser(user);			
		} catch (JSONException e) {e.printStackTrace();}	
	}
}	