package com.moment.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FbEvent implements Parcelable {

    private String id;
    private String facebookId;

    private String owner_facebookId;
    private String owner_firstname;
    private String owner_picture_url;

    private String address;
    private String description;
    private String name;
    private String privacy;
    private String state;
    private String title;

    private String startDate;
    private String endDate;

    private String startTime;
    private String endTime;

    private String cover_photo_url;

    public FbEvent() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getOwner_facebookId() {
        return owner_facebookId;
    }

    public void setOwner_facebookId(String owner_facebookId) {
        this.owner_facebookId = owner_facebookId;
    }

    public String getOwner_firstname() {
        return owner_firstname;
    }

    public void setOwner_firstname(String owner_firstname) {
        this.owner_firstname = owner_firstname;
    }

    public String getOwner_picture_url() {
        return owner_picture_url;
    }

    public void setOwner_picture_url(String owner_picture_url) {
        this.owner_picture_url = owner_picture_url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCover_photo_url() {
        return cover_photo_url;
    }

    public void setCover_photo_url(String cover_photo_url) {
        this.cover_photo_url = cover_photo_url;
    }

    public static Creator<FbEvent> getCreator() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "FbEvent{" +
                "id='" + id + '\'' +
                ", facebookId='" + facebookId + '\'' +
                ", owner_facebookId='" + owner_facebookId + '\'' +
                ", owner_firstname='" + owner_firstname + '\'' +
                ", owner_picture_url='" + owner_picture_url + '\'' +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", privacy='" + privacy + '\'' +
                ", state='" + state + '\'' +
                ", title='" + title + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", cover_photo_url='" + cover_photo_url + '\'' +
                '}';
    }

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
		dest.writeString(address);

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
			this.address = in.readString();
		}
}
