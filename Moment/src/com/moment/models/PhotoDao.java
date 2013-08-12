package com.moment.models;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import com.moment.models.Photo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table photos.
*/
public class PhotoDao extends AbstractDao<Photo, Long> {

    public static final String TABLENAME = "photos";

    /**
     * Properties of entity Photo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property NbLike = new Property(1, Integer.class, "nbLike", false, "NB_LIKE");
        public final static Property UrlOriginal = new Property(2, String.class, "urlOriginal", false, "URL_ORIGINAL");
        public final static Property UrlThumbnail = new Property(3, String.class, "urlThumbnail", false, "URL_THUMBNAIL");
        public final static Property UrlUnique = new Property(4, String.class, "urlUnique", false, "URL_UNIQUE");
        public final static Property Time = new Property(5, java.util.Date.class, "time", false, "TIME");
        public final static Property UserId = new Property(6, long.class, "userId", false, "USER_ID");
        public final static Property PhotoId = new Property(7, Long.class, "photoId", false, "PHOTO_ID");
    };

    private DaoSession daoSession;

    private Query<Photo> moment_PhotosQuery;

    public PhotoDao(DaoConfig config) {
        super(config);
    }
    
    public PhotoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'photos' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'NB_LIKE' INTEGER," + // 1: nbLike
                "'URL_ORIGINAL' TEXT," + // 2: urlOriginal
                "'URL_THUMBNAIL' TEXT," + // 3: urlThumbnail
                "'URL_UNIQUE' TEXT," + // 4: urlUnique
                "'TIME' INTEGER," + // 5: time
                "'USER_ID' INTEGER NOT NULL ," + // 6: userId
                "'PHOTO_ID' INTEGER);"); // 7: photoId
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_photos__id ON photos" +
                " (_id);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'photos'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Photo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer nbLike = entity.getNbLike();
        if (nbLike != null) {
            stmt.bindLong(2, nbLike);
        }
 
        String urlOriginal = entity.getUrlOriginal();
        if (urlOriginal != null) {
            stmt.bindString(3, urlOriginal);
        }
 
        String urlThumbnail = entity.getUrlThumbnail();
        if (urlThumbnail != null) {
            stmt.bindString(4, urlThumbnail);
        }
 
        String urlUnique = entity.getUrlUnique();
        if (urlUnique != null) {
            stmt.bindString(5, urlUnique);
        }
 
        java.util.Date time = entity.getTime();
        if (time != null) {
            stmt.bindLong(6, time.getTime());
        }
        stmt.bindLong(7, entity.getUserId());
    }

    @Override
    protected void attachEntity(Photo entity) {
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
    public Photo readEntity(Cursor cursor, int offset) {
        Photo entity = new Photo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // nbLike
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // urlOriginal
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // urlThumbnail
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // urlUnique
            cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)), // time
            cursor.getLong(offset + 6) // userId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Photo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setNbLike(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setUrlOriginal(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setUrlThumbnail(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setUrlUnique(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setTime(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
        entity.setUserId(cursor.getLong(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Photo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Photo entity) {
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
    
    /** Internal query to resolve the "photos" to-many relationship of Moment. */
    public List<Photo> _queryMoment_Photos(Long photoId) {
        synchronized (this) {
            if (moment_PhotosQuery == null) {
                QueryBuilder<Photo> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.PhotoId.eq(null));
                moment_PhotosQuery = queryBuilder.build();
            }
        }
        Query<Photo> query = moment_PhotosQuery.forCurrentThread();
        query.setParameter(0, photoId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getUserDao().getAllColumns());
            builder.append(" FROM photos T");
            builder.append(" LEFT JOIN users T0 ON T.'USER_ID'=T0.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Photo loadCurrentDeep(Cursor cursor, boolean lock) {
        Photo entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        User user = loadCurrentOther(daoSession.getUserDao(), cursor, offset);
         if(user != null) {
            entity.setUser(user);
        }

        return entity;    
    }

    public Photo loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<Photo> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Photo> list = new ArrayList<Photo>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<Photo> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Photo> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
