package com.moment.classes;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.moment.DetailPhoto;
import com.moment.R;

public class PhotoListAdapter extends BaseAdapter{
	
	public List<Photo> photos = Collections.emptyList();
	
	private final Context context;
	
	public PhotoListAdapter(Context context) {
		this.context = context;
	}

	public void updatePhotos(List<Photo> photos) {
		this.photos = photos;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return photos.size();
	}
	
	@Override
	public Photo getItem(int position) {
		return photos.get(position);
	}
	
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.detail_photo, parent, false);
		}
		
		ImageView photoView = (ImageView) convertView.findViewById(R.id.photo_moment_detail);
		
		Photo photo = getItem(position);
		photoView.setImageBitmap(photo.getBitmap_original());
		
		return convertView;
	}

}
