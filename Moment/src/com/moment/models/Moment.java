package com.moment.models;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.moment.AppMoment;
import com.moment.classes.Images;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
// KEEP INCLUDES END
/**
 * Entity mapped to table moments.
 */
public class Moment {

    private Integer id;
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
    private java.util.Date dateDebut;
    private java.util.Date dateFin;
    private Boolean isOpenInvit;
    private int userId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient MomentDao myDao;

    private User user;
    private Integer user__resolvedKey;

    // KEEP FIELDS - put your custom fields here
    private ArrayList<Photo> photos;
    private ArrayList<Chat> chats;
    // KEEP FIELDS END

    public Moment() {
    }

    public Moment(Integer id) {
        this.id = id;
    }

    public Moment(Integer id, Integer state, Integer guestNumber, Integer guestComing, Integer guestNotComing, Integer privacy, String name, String description, String placeInformations, String infoTransport, String hashtag, String adresse, String keyBitmap, String urlCover, java.util.Date dateDebut, java.util.Date dateFin, Boolean isOpenInvit, int userId) {
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
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.isOpenInvit = isOpenInvit;
        this.userId = userId;
    }

    // GETTERS & SETTERS

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMomentDao() : null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public java.util.Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(java.util.Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public java.util.Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(java.util.Date dateFin) {
        this.dateFin = dateFin;
    }

    public Boolean getIsOpenInvit() {
        return isOpenInvit;
    }

    public void setIsOpenInvit(Boolean isOpenInvit) {
        this.isOpenInvit = isOpenInvit;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    /** To-one relationship, resolved on first access. */
    public User getUser() {
        int __key = this.userId;
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

    public ArrayList<Chat> getChats() {
        return chats;
    }

    public void setChats(ArrayList<Chat> chats) {
        this.chats = chats;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public void setMomentFromJson(JSONObject moment) throws JSONException{
        this.id = moment.getInt("id");
        this.name = moment.getString("name");
        this.adresse = moment.getString("address");
        this.state = moment.getInt("user_state");
        this.keyBitmap = "cover_moment_"+name.toLowerCase();


        //Date de d???but
        String[] dateDedutTemp = moment.getString("startDate").split("-");
        GregorianCalendar dateDebutGreg = new GregorianCalendar(Integer.parseInt(dateDedutTemp[0]), Integer.parseInt(dateDedutTemp[1]), Integer.parseInt(dateDedutTemp[2]));
        this.dateDebut = dateDebutGreg.getTime();

        //Date de fin
        String[] dateFinTemps = moment.getString("endDate").split("-");
        GregorianCalendar dateFinGreg = new GregorianCalendar(Integer.parseInt(dateFinTemps[0]), Integer.parseInt(dateFinTemps[1]), Integer.parseInt(dateFinTemps[2]));
        this.dateFin = dateFinGreg.getTime();


        if (moment.has("cover_photo_url")){
            this.urlCover = moment.getString("cover_photo_url");
        }

        //Invites
        this.guestNumber = moment.getInt("guests_number");
        this.guestComing = moment.getInt("guests_coming");
        this.guestNotComing = moment.getInt("guests_not_coming");

        if(moment.has("hashtag")) this.hashtag = moment.getString("hashtag");
        if(moment.has("description")) this.description = moment.getString("description");

        if (moment.has("owner")){
            JSONObject owner = moment.getJSONObject("owner");
            this.user = new User();
            if(owner.has("email")) this.user.setEmail(owner.getString("email"));
            if(owner.has("firstname")) this.user.setFirstName(owner.getString("firstname"));
            if(owner.has("lastname")) this.user.setLastName(owner.getString("lastname"));
            if(owner.has("profile_picture_url")) this.user.setPictureProfileUrl(owner.getString("profile_picture_url"));
            if(owner.has("id")) this.user.setId(owner.getInt("id"));
        }


    }

    /**
     * Renvoit l'object sous la forme d'un JSONObject
     * @return
     * @throws JSONException
     * @throws FileNotFoundException
     * //TODO: Prendre en charge de pas envoyer la date si y en a pas (Booelan journee entiere ou pas)
     */

    public RequestParams getMomentRequestParams(Context context) throws JSONException {

        RequestParams momentPrams = new RequestParams();

        momentPrams.put("name", this.name);
        momentPrams.put("address", this.adresse);

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(this.dateDebut);
        momentPrams.put("startDate", ""+startDate.get(Calendar.YEAR)+"-"+(startDate.get(Calendar.MONTH)+1)+"-"+startDate.get(Calendar.DAY_OF_MONTH));
        System.out.println("startDate "+startDate.get(Calendar.YEAR)+"-"+(startDate.get(Calendar.MONTH)+1)+"-"+startDate.get(Calendar.DAY_OF_MONTH));
        momentPrams.put("startTime", startDate.get(Calendar.HOUR_OF_DAY)+":"+startDate.get(Calendar.MINUTE));

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(this.dateFin);
        momentPrams.put("endDate", ""+endDate.get(Calendar.YEAR)+"-"+(endDate.get(Calendar.MONTH)+1)+"-"+endDate.get(Calendar.DAY_OF_MONTH));
        momentPrams.put("endTime", endDate.get(Calendar.HOUR_OF_DAY)+":"+endDate.get(Calendar.MINUTE));

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




    /**
     * Fonction qui va chercher sur le serveur afin les infos sur ce Moment et compare avec ses propres infos
     *
     */

    public void updateInfos(Moment tempMoment){

        if(!name.equals(tempMoment.getName())) name = tempMoment.getName();
        if(!adresse.equals(tempMoment.getAdresse())) adresse = tempMoment.getAdresse();
        if(dateDebut!=tempMoment.getDateDebut()) dateDebut = tempMoment.getDateDebut();
        if(dateFin!=tempMoment.getDateFin()) dateFin = tempMoment.getDateFin();
        if((tempMoment.getDescription()!=null)&&(description!=tempMoment.getDescription())) description = tempMoment.getDescription();
        if(guestNumber !=tempMoment.getGuestNumber()) guestNumber =tempMoment.getGuestNumber();
        if(guestComing !=tempMoment.getGuestComing()) guestComing = tempMoment.getGuestComing();
        if(guestNotComing !=tempMoment.getGuestNotComing()) guestNotComing = tempMoment.getGuestNotComing();
        if((tempMoment.getHashtag()!=null)&&(!hashtag.equals(tempMoment.getHashtag()))) hashtag = tempMoment.getHashtag();
        if(state!=tempMoment.getState()) state = tempMoment.getState();

    }


    /**
     * Met une date sous le format YYYY-MM-DD
     * @param date
     */
    public String getDateInFormat(Date date){

        Calendar dateTemp = Calendar.getInstance();
        dateTemp.setTime(date);

        return ""+dateTemp.get(Calendar.YEAR)+"-"+(dateTemp.get(Calendar.MONTH)+1)+"-"+dateTemp.get(Calendar.DAY_OF_MONTH);


    }

    /**
     * Fonction qui prend en param une date sous le format YYYY-MM-DD et renvoit une Date
     * @param dateString
     * @return
     */

    public Date castDate(String dateString){

        String[] dateStringTemp = dateString.split("-");
        GregorianCalendar dateTempGreg = new GregorianCalendar(Integer.parseInt(dateStringTemp[0]), Integer.parseInt(dateStringTemp[1]), Integer.parseInt(dateStringTemp[2]));
        return dateTempGreg.getTime();

    }



    public void printCover(final ImageView targetView, final Boolean isRounded){
        AsyncHttpClient client = new AsyncHttpClient();
        if(AppMoment.getInstance().getBitmapFromMemCache(keyBitmap) == null) {
            Log.e("Moment Cover","DISTANT");
            AppMoment.getInstance().user.getMomentById(id).setKeyBitmap("cover_moment_" + name.toLowerCase());
            String[] allowedContentTypes = new String[] { "image/png", "image/jpeg" };
            client.get(urlCover, new BinaryHttpResponseHandler(allowedContentTypes) {

                @Override
                public void onSuccess(byte[] fileData) {

                    if(fileData!=null){
                        InputStream is = new ByteArrayInputStream(fileData);
                        Bitmap bmp = BitmapFactory.decodeStream(is);
                        AppMoment.getInstance().addBitmapToMemoryCache("cover_moment_"+name.toLowerCase(), bmp);
                        if (isRounded) targetView.setImageBitmap(Images.getRoundedCornerBitmap(bmp));
                        else targetView.setImageBitmap(bmp);
                    }
                }

                @Override
                public void handleFailureMessage(Throwable e, byte[] responseBody) {
                    Log.d("RATEE", "RATEE");
                    onFailure(e, responseBody);
                }
            });
        }
        else {
            Log.e("Moment Cover", "En cache");
            targetView.setImageBitmap(Images.getRoundedCornerBitmap(AppMoment.getInstance().getBitmapFromMemCache(keyBitmap)));
        }
    }
    // KEEP METHODS END

}
