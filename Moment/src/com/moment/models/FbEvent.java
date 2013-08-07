package com.moment.models;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.RequestParams;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FbEvent {

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


    public RequestParams getMomentRequestParams(Context context) throws JSONException, ParseException {

        RequestParams momentPrams = new RequestParams();

        momentPrams.put("name", this.name);
        momentPrams.put("facebookId", this.facebookId);
        momentPrams.put("cover_photo_url", this.cover_photo_url);
        momentPrams.put("owner_facebookId", this.owner_facebookId);
        momentPrams.put("owner_firstname", this.owner_firstname);
        momentPrams.put("owner_picture_url", this.getOwner_picture_url());
        momentPrams.put("state", this.getState());

        if(this.address != null)
        {
            momentPrams.put("address", this.address);
        } else {
            momentPrams.put("address", "Test test test");
        }

        if(this.getStartDate() != null)
        {
                momentPrams.put("startDate", this.getStartDate());
            if(this.getStartTime() != null)
            {
                momentPrams.put("startTime", this.getStartTime());
            }
        }

        if(this.getEndDate() != null)
        {
            momentPrams.put("endDate", this.getEndDate());
            if(this.getEndTime() != null)
            {
                momentPrams.put("endTime", this.getEndTime());
            }
        }

        if (this.description != null) momentPrams.put("description", this.description);

        Log.e("Params", momentPrams.toString());

        return momentPrams;
    }

    @Override
    public String toString() {
        return "FbEvent{" +
                "facebookId='" + facebookId + '\'' +
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
}
