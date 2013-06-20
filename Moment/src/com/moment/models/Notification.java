package com.moment.models;

import java.util.Date;

/**
 * Created by adriendulong on 20/06/13.
 */
public class Notification {

    private int typeNotif;
    private Date time;
    private int momentId;
    private int followerId;

    public Notification(int typeNotif, Long timestamp, int momentId, int followerId){
        this.typeNotif = typeNotif;
        this.time = new Date((Long)timestamp*1000);
        this.momentId = momentId;
        this.followerId = followerId;

    }

    public Notification(){
        this.typeNotif = 1;
        this.time = new Date();
        this.momentId = 1;
        this.followerId = 1;
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

    public int getMomentId() {
        return momentId;
    }

    public void setMomentId(int momentId) {
        this.momentId = momentId;
    }

    public int getFollowerId() {
        return followerId;
    }

    public void setFollowerId(int followerId) {
        this.followerId = followerId;
    }
}
