package com.moment.models;

import java.util.List;
import com.moment.models.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
// KEEP INCLUDES END
/**
 * Entity mapped to table users.
 */
public class User implements Parcelable {

    private Long id;
    private Long facebookId;
    private Integer nbFollows;
    private Integer nbFollowers;
    private String email;
    private String secondEmail;
    private String firstName;
    private String lastName;
    private String pictureProfileUrl;
    private String keyBitmap;
    private String numTel;
    private String secondNumTel;
    private String fbPhotoUrl;
    private String idCarnetAdresse;
    private String description;
    private String adress;
    private Boolean isSelect;
    private Long momentId;
    private Long notificationId;
    private Long invitationsId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient UserDao myDao;

    private List<Moment> moments;
    private List<Notification> notifications;
    private List<Notification> invitations;

    // KEEP FIELDS - put your custom fields here
    private Bitmap photoThumbnail;
    private Bitmap photoOriginal;
    // KEEP FIELDS END

    public User() {
        isSelect = false;
    }

    public User(Long id) {
        this.id = id;
    }

    public User(Long id, Long facebookId, Integer nbFollows, Integer nbFollowers, String email, String secondEmail, String firstName, String lastName, String pictureProfileUrl, String keyBitmap, String numTel, String secondNumTel, String fbPhotoUrl, String idCarnetAdresse, String description, String adress, Boolean isSelect, Long momentId, Long notificationId, Long invitationsId) {
        this.id = id;
        this.facebookId = facebookId;
        this.nbFollows = nbFollows;
        this.nbFollowers = nbFollowers;
        this.email = email;
        this.secondEmail = secondEmail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pictureProfileUrl = pictureProfileUrl;
        this.keyBitmap = keyBitmap;
        this.numTel = numTel;
        this.secondNumTel = secondNumTel;
        this.fbPhotoUrl = fbPhotoUrl;
        this.idCarnetAdresse = idCarnetAdresse;
        this.description = description;
        this.adress = adress;
        this.isSelect = isSelect;
        this.momentId = momentId;
        this.notificationId = notificationId;
        this.invitationsId = invitationsId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(Long facebookId) {
        this.facebookId = facebookId;
    }

    public Integer getNbFollows() {
        return nbFollows;
    }

    public void setNbFollows(Integer nbFollows) {
        this.nbFollows = nbFollows;
    }

    public Integer getNbFollowers() {
        return nbFollowers;
    }

    public void setNbFollowers(Integer nbFollowers) {
        this.nbFollowers = nbFollowers;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecondEmail() {
        return secondEmail;
    }

    public void setSecondEmail(String secondEmail) {
        this.secondEmail = secondEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPictureProfileUrl() {
        return pictureProfileUrl;
    }

    public void setPictureProfileUrl(String pictureProfileUrl) {
        this.pictureProfileUrl = pictureProfileUrl;
    }

    public String getKeyBitmap() {
        return keyBitmap;
    }

    public void setKeyBitmap(String keyBitmap) {
        this.keyBitmap = keyBitmap;
    }

    public String getNumTel() {
        return numTel;
    }

    public void setNumTel(String numTel) {
        this.numTel = numTel;
    }

    public String getSecondNumTel() {
        return secondNumTel;
    }

    public void setSecondNumTel(String secondNumTel) {
        this.secondNumTel = secondNumTel;
    }

    public String getFbPhotoUrl() {
        return fbPhotoUrl;
    }

    public void setFbPhotoUrl(String fbPhotoUrl) {
        this.fbPhotoUrl = fbPhotoUrl;
    }

    public String getIdCarnetAdresse() {
        return idCarnetAdresse;
    }

    public void setIdCarnetAdresse(String idCarnetAdresse) {
        this.idCarnetAdresse = idCarnetAdresse;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public Boolean getIsSelect() {
        return isSelect;
    }

    public void setIsSelect(Boolean isSelect) {
        this.isSelect = isSelect;
    }

    public Long getMomentId() {
        return momentId;
    }

    public void setMomentId(Long momentId) {
        this.momentId = momentId;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getInvitationsId() {
        return invitationsId;
    }

    public void setInvitationsId(Long invitationsId) {
        this.invitationsId = invitationsId;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Moment> getMoments() {
        if (moments == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MomentDao targetDao = daoSession.getMomentDao();
            List<Moment> momentsNew = targetDao._queryUser_Moments(id);
            synchronized (this) {
                if(moments == null) {
                    moments = momentsNew;
                }
            }
        }
        return moments;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetMoments() {
        moments = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Notification> getNotifications() {
        if (notifications == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            NotificationDao targetDao = daoSession.getNotificationDao();
            List<Notification> notificationsNew = targetDao._queryUser_Notifications(id);
            synchronized (this) {
                if(notifications == null) {
                    notifications = notificationsNew;
                }
            }
        }
        return notifications;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetNotifications() {
        notifications = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Notification> getInvitations() {
        if (invitations == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            NotificationDao targetDao = daoSession.getNotificationDao();
            List<Notification> invitationsNew = targetDao._queryUser_Invitations(id);
            synchronized (this) {
                if(invitations == null) {
                    invitations = invitationsNew;
                }
            }
        }
        return invitations;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetInvitations() {
        invitations = null;
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


    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public void addMoment(Moment moment) {
        if(this.moments == null) {
            this.moments = new ArrayList<Moment>();
        }
        this.moments.add(moment);
    }

    public void setMoments(List<Moment> moments) {
        this.moments = moments;
    }

    public void setInvitations(List<Notification> invitations) {
        if(invitations == null) {
            this.invitations = new ArrayList<Notification>(invitations);
        } else {
            this.invitations = invitations;
        }
    }

    public Moment getMomentById(Long id){
        List<Moment> moments = this.getMoments();
        for(Moment m : moments){
            if(m.getId().equals(id)){
                return m;
            }
        }
        return null;
    }

    public JSONObject getUserToJSON() throws JSONException {

        JSONObject userJson = new JSONObject();

        if(id!=null){
            userJson.put("id", this.id);
        }
        else{
            if(email!=null) userJson.put("email", this.email);
            if(firstName!=null) userJson.put("firstname", this.firstName);
            if(lastName!=null) userJson.put("lastname", this.lastName);
            if(numTel!=null) userJson.put("phone", this.numTel);
            if(facebookId!=null) userJson.put("facebookId", this.facebookId);
            if(fbPhotoUrl !=null) userJson.put("photo", this.fbPhotoUrl);
            if(secondEmail!=null) userJson.put("secondEmail", this.secondEmail);
            if(secondNumTel!=null) userJson.put("secondPhone", this.secondNumTel);
        }
        return userJson;
    }

    public void setUserFromJson(JSONObject userJson){
        try {
            if(userJson.has("id")) this.setId(userJson.getLong("id"));
            if(userJson.has("facebookId")) this.setFacebookId(userJson.getLong("facebookId"));
            if(userJson.has("firstname")) this.setFirstName(userJson.getString("firstname"));
            if(userJson.has("lastname")) this.setLastName(userJson.getString("lastname"));
            if(userJson.has("email")) this.setEmail(userJson.getString("email"));
            if(userJson.has("profile_picture_url")) this.setPictureProfileUrl(userJson.getString("profile_picture_url"));
            if(userJson.has("facebookId")) this.setFacebookId(userJson.getLong("facebookId"));
            if(userJson.has("description")) this.setDescription(userJson.getString("description"));
            if(userJson.has("nbFollowers")) this.setNbFollowers(userJson.getInt("nbFollowers"));
            if(userJson.has("nbFollows")) this.setNbFollows(userJson.getInt("nbFollows"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(secondEmail);
        dest.writeString(numTel);
        dest.writeString(secondNumTel);
        dest.writeString(pictureProfileUrl);
        dest.writeString(fbPhotoUrl);
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        secondEmail = in.readString();
        numTel = in.readString();
        secondNumTel = in.readString();
        pictureProfileUrl = in.readString();
        fbPhotoUrl = in.readString();
    }
    // KEEP METHODS END

}
