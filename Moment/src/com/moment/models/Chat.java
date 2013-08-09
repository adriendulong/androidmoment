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
 * Entity mapped to table chats.
 */
public class Chat {

    private Long id;
    private String message;
    private java.util.Date date;
    private long momentId;
    private long userId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient ChatDao myDao;

    private User user;
    private Long user__resolvedKey;

    private Moment moment;
    private Long moment__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Chat() {
    }

    public Chat(Long id) {
        this.id = id;
    }

    public Chat(Long id, String message, java.util.Date date, long momentId, long userId) {
        this.id = id;
        this.message = message;
        this.date = date;
        this.momentId = momentId;
        this.userId = userId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getChatDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public long getMomentId() {
        return momentId;
    }

    public void setMomentId(long momentId) {
        this.momentId = momentId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public Chat(String message, User user, Date date) {
        this.message = message;
        this.user = user;
        this.date = date;
    }

    public void chatFromJSON(JSONObject chatObject){
        try {
            this.setId(chatObject.getLong("id"));
            this.setMessage(chatObject.getString("message"));
            long time = chatObject.getLong("time");
            time = time * 1000;
            this.setDate(new Date(time));
            User user = new User();
            user.setUserFromJson(chatObject.getJSONObject("user"));
            this.setUser(user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // KEEP METHODS END

}
