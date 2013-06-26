package com.moment.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.classes.Images;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS
// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table users.
 */
public class User {

    private Long id;
    private Integer facebookId;
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
    private Boolean isSelect;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient UserDao myDao;

    private List<Moment> moments;
    private ArrayList<Notification> notifications;
    private ArrayList<Notification> invitations;

    // KEEP FIELDS - put your custom fields here
    Bitmap photoThumbnail;
    Bitmap photoOriginal;
    // KEEP FIELDS END

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    public User(Long id, Integer facebookId, Integer nbFollows, Integer nbFollowers, String email, String secondEmail, String firstName, String lastName, String pictureProfileUrl, String keyBitmap, String numTel, String secondNumTel, String fbPhotoUrl, String idCarnetAdresse, String description, Boolean isSelect) {
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
        this.isSelect = isSelect;
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

    public Integer getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(Integer facebookId) {
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

    public Boolean getIsSelect() {
        return isSelect;
    }

    public void setIsSelect(Boolean isSelect) {
        this.isSelect = isSelect;
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }

    public ArrayList<Notification> getInvitations() {
        return invitations;
    }

    public void setInvitations(ArrayList<Notification> invitations) {
        this.invitations = invitations;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Moment> getMoments() {
        if (moments == null) {
            if (AppMoment.getInstance().daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MomentDao targetDao = AppMoment.getInstance().daoSession.getMomentDao();
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

    public void addMoment(Moment moment){
        this.getMoments().add(moment);
    }

    public Bitmap getPhotoOriginal() {
        return photoOriginal;
    }

    public void setPhotoOriginal(Bitmap photoOriginal) {
        this.photoOriginal = photoOriginal;
    }

    public Bitmap getPhotoThumbnail() {
        return photoThumbnail;
    }

    public void setPhotoThumbnail(Bitmap photoThumbnail) {
        this.photoThumbnail = photoThumbnail;
    }

    public Moment getMomentById(Long id){
        for(Moment m : moments){
            if(m.getId().equals(id)){
                return m;
            }
        }
        return null;
    }

    public JSONObject getUserToJSON() throws JSONException {

        JSONObject userJson = new JSONObject();

        if(id>0){
            userJson.put("id", this.id);
        }
        else{
            if(email!=null) userJson.put("email", this.email);
            if(firstName!=null) userJson.put("firstname", this.firstName);
            if(lastName!=null) userJson.put("lastname", this.lastName);
            if(numTel!=null) userJson.put("phone", this.numTel);
            if(facebookId>0) userJson.put("facebookId", this.facebookId);
            if(fbPhotoUrl !=null) userJson.put("photo", this.fbPhotoUrl);
            if(secondEmail!=null) userJson.put("secondEmail", this.secondEmail);
            if(secondNumTel!=null) userJson.put("secondPhone", this.secondNumTel);


        }

        return userJson;
    }

    public void setUserFromJson(JSONObject userJson){
        try {
            this.setId(userJson.getLong("id"));
            if(userJson.has("firstname")) this.setFirstName(userJson.getString("firstname"));
            if(userJson.has("lastname")) this.setLastName(userJson.getString("lastname"));
            if(userJson.has("email")) this.setEmail(userJson.getString("email"));
            if(userJson.has("profile_picture_url")) this.setPictureProfileUrl(userJson.getString("profile_picture_url"));
            if(userJson.has("facebookId")) this.setFacebookId(userJson.getInt("facebookId"));
            if(userJson.has("description")) this.setDescription(userJson.getString("description"));
            if(userJson.has("nbFollowers")) this.setNbFollowers(userJson.getInt("nbFollowers"));
            if(userJson.has("nbFollows")) this.setNbFollows(userJson.getInt("nbFollows"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void printProfilePicture(final ImageView targetView, final Boolean isRounded){
        if(AppMoment.getInstance().getBitmapFromMemCache("profile_picture_"+id)==null){

            AsyncHttpClient client = new AsyncHttpClient();
            String[] allowedContentTypes = new String[] { "image/png", "image/jpeg" };

            client.get(pictureProfileUrl, new BinaryHttpResponseHandler(allowedContentTypes) {

                @Override
                public void onSuccess(byte[] fileData) {
                    InputStream is = new ByteArrayInputStream(fileData);
                    Bitmap bmp = BitmapFactory.decodeStream(is);
                    AppMoment.getInstance().addBitmapToMemoryCache("profile_picture_"+id, bmp);
                    setKeyBitmap("profile_picture_"+id);

                    if (isRounded) targetView.setImageBitmap(Images.getRoundedCornerBitmap(bmp));
                    else targetView.setImageBitmap(bmp);
                }

                @Override
                public void handleFailureMessage(Throwable e, byte[] responseBody) {
                    onFailure(e, responseBody);
                }
            });
        }
        else{
            targetView.setImageBitmap(Images.getRoundedCornerBitmap(AppMoment.getInstance().getBitmapFromMemCache("profile_picture_"+id)));
        }
    }
    // KEEP METHODS END

}
