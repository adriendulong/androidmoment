package com.moment.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by adriendulong on 20/06/13.
 */
public class Notification {

    private int typeNotif;
    private Date time;
    private Moment moment;
    private User user;


    public Notification(){
        this.typeNotif = 1;
        this.time = new Date();
    }

    public int getTypeNotif() {
        return typeNotif;
    }

    public void setTypeNotif(int typeNotif) {
        this.typeNotif = typeNotif;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Moment getMoment() {
        return moment;
    }

    public void setMoment(Moment moment) {
        this.moment = moment;
    }

    public User getFollower() {
        return user;
    }

    public void setFollower(User follower) {
        this.user = follower;
    }

    public void setFromJson(JSONObject notifJson){
        try{
            this.typeNotif = notifJson.getInt("type_id");

            //Time
            String timeString = notifJson.getString("time");
            Long time = Long.parseLong(timeString);
            this.time = new Date(time*1000);


            if(notifJson.has("moment")){
                Moment moment = new Moment();
                moment.setMomentFromJson(notifJson.getJSONObject("moment"));
                this.moment = moment;
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
