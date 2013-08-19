package com.moment.models;

import java.util.List;
import com.moment.models.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.moment.AppMoment;
import com.moment.util.CommonUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
// KEEP INCLUDES END
/**
 * Entity mapped to table moments.
 */
public class Moment {

    private Long id;
    private Integer state;
    private Integer guestNumber;
    private Integer guestComing;
    private Integer guestNotComing;
    private Integer privacy;
    private String name;
    private String description;
    private String placeInformations;
    private String infoTransport;
    private String hashtag;
    private String adresse;
    private String keyBitmap;
    private String urlCover;
    private String uniqueUrl;
    private String dateDebut;
    private String dateFin;
    private Boolean isOpenInvit;
    private long ownerId;
    private Long userId;
    private Long photoId;
    private Long chatId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient MomentDao myDao;

    private User user;
    private Long user__resolvedKey;

    private List<User> users;
    private List<Photo> photos;
    private List<Chat> chats;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Moment() {
    }

    public Moment(Long id) {
        this.id = id;
    }

    public Moment(Long id, Integer state, Integer guestNumber, Integer guestComing, Integer guestNotComing, Integer privacy, String name, String description, String placeInformations, String infoTransport, String hashtag, String adresse, String keyBitmap, String urlCover, String uniqueUrl, String dateDebut, String dateFin, Boolean isOpenInvit, long ownerId, Long userId, Long photoId, Long chatId) {
        this.id = id;
        this.state = state;
        this.guestNumber = guestNumber;
        this.guestComing = guestComing;
        this.guestNotComing = guestNotComing;
        this.privacy = privacy;
        this.name = name;
        this.description = description;
        this.placeInformations = placeInformations;
        this.infoTransport = infoTransport;
        this.hashtag = hashtag;
        this.adresse = adresse;
        this.keyBitmap = keyBitmap;
        this.urlCover = urlCover;
        this.uniqueUrl = uniqueUrl;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.isOpenInvit = isOpenInvit;
        this.ownerId = ownerId;
        this.userId = userId;
        this.photoId = photoId;
        this.chatId = chatId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMomentDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getGuestNumber() {
        return guestNumber;
    }

    public void setGuestNumber(Integer guestNumber) {
        this.guestNumber = guestNumber;
    }

    public Integer getGuestComing() {
        return guestComing;
    }

    public void setGuestComing(Integer guestComing) {
        this.guestComing = guestComing;
    }

    public Integer getGuestNotComing() {
        return guestNotComing;
    }

    public void setGuestNotComing(Integer guestNotComing) {
        this.guestNotComing = guestNotComing;
    }

    public Integer getPrivacy() {
        return privacy;
    }

    public void setPrivacy(Integer privacy) {
        this.privacy = privacy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlaceInformations() {
        return placeInformations;
    }

    public void setPlaceInformations(String placeInformations) {
        this.placeInformations = placeInformations;
    }

    public String getInfoTransport() {
        return infoTransport;
    }

    public void setInfoTransport(String infoTransport) {
        this.infoTransport = infoTransport;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getKeyBitmap() {
        return keyBitmap;
    }

    public void setKeyBitmap(String keyBitmap) {
        this.keyBitmap = keyBitmap;
    }

    public String getUrlCover() {
        return urlCover;
    }

    public void setUrlCover(String urlCover) {
        this.urlCover = urlCover;
    }

    public String getUniqueUrl() {
        return uniqueUrl;
    }

    public void setUniqueUrl(String uniqueUrl) {
        this.uniqueUrl = uniqueUrl;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public Boolean getIsOpenInvit() {
        return isOpenInvit;
    }

    public void setIsOpenInvit(Boolean isOpenInvit) {
        this.isOpenInvit = isOpenInvit;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    /** To-one relationship, resolved on first access. */
    public User getUser() {
        long __key = this.ownerId;
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
            throw new DaoException("To-one property 'ownerId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.user = user;
            ownerId = user.getId();
            user__resolvedKey = ownerId;
        }
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<User> getUsers() {
        if (users == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            List<User> usersNew = targetDao._queryMoment_Users(id);
            synchronized (this) {
                if(users == null) {
                    users = usersNew;
                }
            }
        }
        return users;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetUsers() {
        users = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Photo> getPhotos() {
        if (photos == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PhotoDao targetDao = daoSession.getPhotoDao();
            List<Photo> photosNew = targetDao._queryMoment_Photos(id);
            synchronized (this) {
                if(photos == null) {
                    photos = photosNew;
                }
            }
        }
        return photos;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetPhotos() {
        photos = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Chat> getChats() {
        if (chats == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ChatDao targetDao = daoSession.getChatDao();
            List<Chat> chatsNew = targetDao._queryMoment_Chats(id);
            synchronized (this) {
                if(chats == null) {
                    chats = chatsNew;
                }
            }
        }
        return chats;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetChats() {
        chats = null;
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

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public void setMomentFromJson(JSONObject moment) throws JSONException {
        try{
            this.id = moment.getLong("id");
            this.name = moment.getString("name");
            this.adresse = moment.getString("address");
            this.state = moment.getInt("user_state");
            this.keyBitmap = "cover_moment_"+name.toLowerCase();

            org.joda.time.DateTime dt;

            String dateDebut;
            String dateFin;
            if(moment.has("startTime")) dateDebut = moment.getString("startDate") + " " + moment.getString("startTime");
            else dateDebut = moment.getString("startDate") + " 00:00:00";
            if(moment.has("endTime")) dateFin = moment.getString("endDate") + " " + moment.getString("endTime");
            else dateFin = moment.getString("endDate") + " 00:00:00";

            dt = CommonUtilities.dateFormat.parseDateTime(dateDebut);
            this.dateDebut = dt.toString();

            dt = CommonUtilities.dateFormat.parseDateTime(dateFin);
            this.dateFin = dt.toString();

            if (moment.has("cover_photo_url")){
                this.urlCover = moment.getString("cover_photo_url");
            }

            this.guestNumber = moment.getInt("guests_number");
            this.guestComing = moment.getInt("guests_coming");
            this.guestNotComing = moment.getInt("guests_not_coming");
            if(moment.has("hashtag")) this.hashtag = moment.getString("hashtag");
            if(moment.has("description")) this.description = moment.getString("description");
            if(moment.has("privacy")) this.privacy = moment.getInt("privacy");
            if(moment.has("isOpenInvit")) this.isOpenInvit = moment.getBoolean("isOpenInvit");
            if(moment.has("unique_url")) this.uniqueUrl = moment.getString("unique_url");

            if (moment.has("owner")){
                JSONObject owner = moment.getJSONObject("owner");
                this.user = new User();
                if(owner.has("email")) this.user.setEmail(owner.getString("email"));
                if(owner.has("firstname")) this.user.setFirstName(owner.getString("firstname"));
                if(owner.has("lastname")) this.user.setLastName(owner.getString("lastname"));
                if(owner.has("profile_picture_url")) this.user.setPictureProfileUrl(owner.getString("profile_picture_url"));
                if(owner.has("id"))
                {
                    this.user.setId(owner.getLong("id"));
                    this.setOwnerId(owner.getLong("id"));
                    AppMoment.getInstance().userDao.insertOrReplace(this.user);
                }
                if(owner.has("facebookId")) this.user.setFacebookId(owner.getLong("facebookId"));

                AppMoment.getInstance().momentDao.insertOrReplace(this);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public RequestParams getMomentRequestParams(Context context) throws JSONException {

        RequestParams momentPrams = new RequestParams();

        momentPrams.put("name", this.name);
        momentPrams.put("address", this.adresse);

        org.joda.time.DateTime dt;

        dt = CommonUtilities.dateFormat.parseDateTime(this.dateDebut);

        momentPrams.put("startDate", CommonUtilities.dateFormatReverseTiret.format(dt.toDate()));
        momentPrams.put("startTime", CommonUtilities.timeFormat.format(dt.toDate()));

        dt = CommonUtilities.dateFormat.parseDateTime(this.dateFin);

        momentPrams.put("endDate", CommonUtilities.dateFormatReverseTiret.format(dt.toDate()));
        momentPrams.put("endTime", CommonUtilities.timeFormat.format(dt.toDate()));

        if (this.description != null) momentPrams.put("description", this.description);
        if (this.placeInformations != null) momentPrams.put("placeInformations", this.placeInformations);
        if (this.hashtag != null) momentPrams.put("hashtag", this.hashtag);

        if(this.keyBitmap!=null){
            File image = context.getFileStreamPath("cover_picture");
            try {
                momentPrams.put("photo", image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return momentPrams;
    }

    public String getStartDateString(){
        org.joda.time.DateTime dt = new org.joda.time.DateTime(this.getDateDebut());
        return ""+dt.getYear()+"-"+dt.getMonthOfYear()+"-"+dt.getDayOfMonth();
    }

    public String getStartHourString(){
        org.joda.time.DateTime dt = new org.joda.time.DateTime(this.getDateDebut());
        return ""+dt.getHourOfDay()+":"+dt.getMinuteOfHour();
    }

    public String getEndDateString(){
        org.joda.time.DateTime dt = new org.joda.time.DateTime(this.getDateFin());
        return ""+dt.getYear()+"-"+dt.getMonthOfYear()+"-"+dt.getDayOfMonth();
    }

    public String getEndHourString(){
        org.joda.time.DateTime dt = new org.joda.time.DateTime(this.getDateFin());
        return ""+dt.getHourOfDay()+":"+dt.getMinuteOfHour();
    }
    // KEEP METHODS END

}
