package com.moment.models;

import com.moment.models.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
// KEEP INCLUDES END
/**
 * Entity mapped to table notifications.
 */
public class Notification {

    private Integer typeNotif;
    private java.util.Date time;
    private long userId;
    private long momentId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient NotificationDao myDao;

    private User user;
    private Long user__resolvedKey;

    private Moment moment;
    private Long moment__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Notification() {
    }

    public Notification(Integer typeNotif, java.util.Date time, long userId, long momentId) {
        this.typeNotif = typeNotif;
        this.time = time;
        this.userId = userId;
        this.momentId = momentId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getNotificationDao() : null;
    }

    public Integer getTypeNotif() {
        return typeNotif;
    }

    public void setTypeNotif(Integer typeNotif) {
        this.typeNotif = typeNotif;
    }

    public java.util.Date getTime() {
        return time;
    }

    public void setTime(java.util.Date time) {
        this.time = time;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getMomentId() {
        return momentId;
    }

    public void setMomentId(long momentId) {
        this.momentId = momentId;
    }

    /** To-one relationship, resolved on first access. */
    public User getUser() {
        long __key = this.userId;
        if (user__resolvedKey == null || !user__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User userNew = targetDao.load(__key);
            synchronized (this) {
                user = userNew;
            	user__resolvedKey = __key;
            }
        }
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new DaoException("To-one property 'userId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.user = user;
            userId = user.getId();
            user__resolvedKey = userId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public Moment getMoment() {
        long __key = this.momentId;
        if (moment__resolvedKey == null || !moment__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MomentDao targetDao = daoSession.getMomentDao();
            Moment momentNew = targetDao.load(__key);
            synchronized (this) {
                moment = momentNew;
            	moment__resolvedKey = __key;
            }
        }
        return moment;
    }

    public void setMoment(Moment moment) {
        if (moment == null) {
            throw new DaoException("To-one property 'momentId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.moment = moment;
            momentId = moment.getId();
            moment__resolvedKey = momentId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here

    public void setFromJson(JSONObject notifJson){
        try{
            this.typeNotif = notifJson.getInt("type_id");

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
    // KEEP METHODS END

}
