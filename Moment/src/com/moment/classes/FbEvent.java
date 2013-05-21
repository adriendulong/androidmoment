package com.moment.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class FbEvent implements Parcelable {

	private String id;
	private String title;
	private String startTime;
	private String location;
 
	public FbEvent(String _id, String _title, String _sT, String _loc){
		this.id = _id;
		this.title = _title;
		this.startTime = _sT;
		this.location = _loc;
	}
 
	public String getId(){
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}
 
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
 
	public String getStartTime(){
		return startTime;
	}
	
	public void setStartTime(String startTime){
		this.startTime = startTime;
	}
 
	public String getLocation(){
		return location;
	}
	
	public void setLocation(String location){
		this.location = location;
	}
	
	@Override
    public String toString() {
        return this.title;
    }

	//A partir de lˆ les fonction pour Parcelable
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(id);
		dest.writeString(title);
		dest.writeString(startTime);
		dest.writeString(location);
		
	}
	
	public static final Parcelable.Creator<FbEvent> CREATOR = new Parcelable.Creator<FbEvent>(){
			    @Override
			    public FbEvent createFromParcel(Parcel source)
			    {
			        return new FbEvent(source);
			    }

			    @Override
			    public FbEvent[] newArray(int size)
			    {
				return new FbEvent[size];
			    }
	};

	
	public FbEvent(Parcel in) {
			this.id = in.readString();
			this.title = in.readString();
			this.startTime = in.readString();
			this.location = in.readString();
		}
}
