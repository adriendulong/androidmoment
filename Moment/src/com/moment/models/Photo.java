package com.moment.models;

import com.moment.models.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here

import android.graphics.Bitmap;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
// KEEP INCLUDES END
/**
 * Entity mapped to table photos.
 */
public class Photo {

    private Long id;
    private Integer nbLike;
    private String urlOriginal;
    private String urlThumbnail;
    private String urlUnique;
    private java.util.Date time;
    private long userId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient PhotoDao myDao;

    private User user;
    private Long user__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    private Bitmap bitmapOriginal;
    private Bitmap bitmapThumbnail;
    private ImageView gridImage;
    // KEEP FIELDS END

    public Photo() {
    }

    public Photo(Long id) {
        this.id = id;
    }

    public Photo(Long id, Integer nbLike, String urlOriginal, String urlThumbnail, String urlUnique, java.util.Date time, long userId) {
        this.id = id;
        this.nbLike = nbLike;
        this.urlOriginal = urlOriginal;
        this.urlThumbnail = urlThumbnail;
        this.urlUnique = urlUnique;
        this.time = time;
        this.userId = userId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPhotoDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNbLike() {
        return nbLike;
    }

    public void setNbLike(Integer nbLike) {
        this.nbLike = nbLike;
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

    public String getUrlUnique() {
        return urlUnique;
    }

    public void setUrlUnique(String urlUnique) {
        this.urlUnique = urlUnique;
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


    public Bitmap getBitmapOriginal() {
        return bitmapOriginal;
    }

    public void photoFromJSON(JSONObject photoObject){
        try {
            this.setId(photoObject.getLong("id"));
            this.setNbLike(photoObject.getInt("nb_like"));
            this.setUrlOriginal(photoObject.getString("url_original"));
            this.setUrlThumbnail(photoObject.getString("url_thumbnail"));
            this.setUrlUnique(photoObject.getString("unique_url"));

            Date timestamp = new Date(Long.valueOf(photoObject.getString("time"))*1000);
            this.setTime(timestamp);

            User user = new User();
            user.setUserFromJson(photoObject.getJSONObject("taken_by"));
            this.setUser(user);

        } catch (JSONException e) {e.printStackTrace();}
    }
    // KEEP METHODS END

}
