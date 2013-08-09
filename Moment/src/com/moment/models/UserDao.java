package com.moment.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.moment.models.User;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table users.
*/
public class UserDao extends AbstractDao<User, Long> {

    public static final String TABLENAME = "users";

    /**
     * Properties of entity User.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property FacebookId = new Property(1, Long.class, "facebookId", false, "FACEBOOK_ID");
        public final static Property NbFollows = new Property(2, Integer.class, "nbFollows", false, "NB_FOLLOWS");
        public final static Property NbFollowers = new Property(3, Integer.class, "nbFollowers", false, "NB_FOLLOWERS");
        public final static Property Email = new Property(4, String.class, "email", false, "EMAIL");
        public final static Property SecondEmail = new Property(5, String.class, "secondEmail", false, "SECOND_EMAIL");
        public final static Property FirstName = new Property(6, String.class, "firstName", false, "FIRST_NAME");
        public final static Property LastName = new Property(7, String.class, "lastName", false, "LAST_NAME");
        public final static Property PictureProfileUrl = new Property(8, String.class, "pictureProfileUrl", false, "PICTURE_PROFILE_URL");
        public final static Property KeyBitmap = new Property(9, String.class, "keyBitmap", false, "KEY_BITMAP");
        public final static Property NumTel = new Property(10, String.class, "numTel", false, "NUM_TEL");
        public final static Property SecondNumTel = new Property(11, String.class, "secondNumTel", false, "SECOND_NUM_TEL");
        public final static Property FbPhotoUrl = new Property(12, String.class, "fbPhotoUrl", false, "FB_PHOTO_URL");
        public final static Property IdCarnetAdresse = new Property(13, String.class, "idCarnetAdresse", false, "ID_CARNET_ADRESSE");
        public final static Property Description = new Property(14, String.class, "description", false, "DESCRIPTION");
        public final static Property Adress = new Property(15, String.class, "adress", false, "ADRESS");
        public final static Property IsSelect = new Property(16, Boolean.class, "isSelect", false, "IS_SELECT");
        public final static Property NotifId = new Property(17, long.class, "notifId", false, "NOTIF_ID");
    };

    private DaoSession daoSession;


    public UserDao(DaoConfig config) {
        super(config);
    }
    
    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'users' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'FACEBOOK_ID' INTEGER," + // 1: facebookId
                "'NB_FOLLOWS' INTEGER," + // 2: nbFollows
                "'NB_FOLLOWERS' INTEGER," + // 3: nbFollowers
                "'EMAIL' TEXT," + // 4: email
                "'SECOND_EMAIL' TEXT," + // 5: secondEmail
                "'FIRST_NAME' TEXT," + // 6: firstName
                "'LAST_NAME' TEXT," + // 7: lastName
                "'PICTURE_PROFILE_URL' TEXT," + // 8: pictureProfileUrl
                "'KEY_BITMAP' TEXT," + // 9: keyBitmap
                "'NUM_TEL' TEXT," + // 10: numTel
                "'SECOND_NUM_TEL' TEXT," + // 11: secondNumTel
                "'FB_PHOTO_URL' TEXT," + // 12: fbPhotoUrl
                "'ID_CARNET_ADRESSE' TEXT," + // 13: idCarnetAdresse
                "'DESCRIPTION' TEXT," + // 14: description
                "'ADRESS' TEXT," + // 15: adress
                "'IS_SELECT' INTEGER," + // 16: isSelect
                "'NOTIF_ID' INTEGER NOT NULL );"); // 17: notifId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'users'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long facebookId = entity.getFacebookId();
        if (facebookId != null) {
            stmt.bindLong(2, facebookId);
        }
 
        Integer nbFollows = entity.getNbFollows();
        if (nbFollows != null) {
            stmt.bindLong(3, nbFollows);
        }
 
        Integer nbFollowers = entity.getNbFollowers();
        if (nbFollowers != null) {
            stmt.bindLong(4, nbFollowers);
        }
 
        String email = entity.getEmail();
        if (email != null) {
            stmt.bindString(5, email);
        }
 
        String secondEmail = entity.getSecondEmail();
        if (secondEmail != null) {
            stmt.bindString(6, secondEmail);
        }
 
        String firstName = entity.getFirstName();
        if (firstName != null) {
            stmt.bindString(7, firstName);
        }
 
        String lastName = entity.getLastName();
        if (lastName != null) {
            stmt.bindString(8, lastName);
        }
 
        String pictureProfileUrl = entity.getPictureProfileUrl();
        if (pictureProfileUrl != null) {
            stmt.bindString(9, pictureProfileUrl);
        }
 
        String keyBitmap = entity.getKeyBitmap();
        if (keyBitmap != null) {
            stmt.bindString(10, keyBitmap);
        }
 
        String numTel = entity.getNumTel();
        if (numTel != null) {
            stmt.bindString(11, numTel);
        }
 
        String secondNumTel = entity.getSecondNumTel();
        if (secondNumTel != null) {
            stmt.bindString(12, secondNumTel);
        }
 
        String fbPhotoUrl = entity.getFbPhotoUrl();
        if (fbPhotoUrl != null) {
            stmt.bindString(13, fbPhotoUrl);
        }
 
        String idCarnetAdresse = entity.getIdCarnetAdresse();
        if (idCarnetAdresse != null) {
            stmt.bindString(14, idCarnetAdresse);
        }
 
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(15, description);
        }
 
        String adress = entity.getAdress();
        if (adress != null) {
            stmt.bindString(16, adress);
        }
 
        Boolean isSelect = entity.getIsSelect();
        if (isSelect != null) {
            stmt.bindLong(17, isSelect ? 1l: 0l);
        }
        stmt.bindLong(18, entity.getNotifId());
    }

    @Override
    protected void attachEntity(User entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public User readEntity(Cursor cursor, int offset) {
        User entity = new User( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // facebookId
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // nbFollows
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // nbFollowers
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // email
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // secondEmail
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // firstName
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // lastName
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // pictureProfileUrl
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // keyBitmap
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // numTel
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // secondNumTel
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // fbPhotoUrl
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // idCarnetAdresse
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // description
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // adress
            cursor.isNull(offset + 16) ? null : cursor.getShort(offset + 16) != 0, // isSelect
            cursor.getLong(offset + 17) // notifId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFacebookId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setNbFollows(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setNbFollowers(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setEmail(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setSecondEmail(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setFirstName(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setLastName(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setPictureProfileUrl(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setKeyBitmap(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setNumTel(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setSecondNumTel(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setFbPhotoUrl(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setIdCarnetAdresse(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setDescription(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setAdress(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setIsSelect(cursor.isNull(offset + 16) ? null : cursor.getShort(offset + 16) != 0);
        entity.setNotifId(cursor.getLong(offset + 17));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(User entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(User entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
