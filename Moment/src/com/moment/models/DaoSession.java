package com.moment.models;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.moment.models.Moment;
import com.moment.models.User;
import com.moment.models.Chat;
import com.moment.models.Photo;
import com.moment.models.Notification;

import com.moment.models.MomentDao;
import com.moment.models.UserDao;
import com.moment.models.ChatDao;
import com.moment.models.PhotoDao;
import com.moment.models.NotificationDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig momentDaoConfig;
    private final DaoConfig userDaoConfig;
    private final DaoConfig chatDaoConfig;
    private final DaoConfig photoDaoConfig;
    private final DaoConfig notificationDaoConfig;

    private final MomentDao momentDao;
    private final UserDao userDao;
    private final ChatDao chatDao;
    private final PhotoDao photoDao;
    private final NotificationDao notificationDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        momentDaoConfig = daoConfigMap.get(MomentDao.class).clone();
        momentDaoConfig.initIdentityScope(type);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        chatDaoConfig = daoConfigMap.get(ChatDao.class).clone();
        chatDaoConfig.initIdentityScope(type);

        photoDaoConfig = daoConfigMap.get(PhotoDao.class).clone();
        photoDaoConfig.initIdentityScope(type);

        notificationDaoConfig = daoConfigMap.get(NotificationDao.class).clone();
        notificationDaoConfig.initIdentityScope(type);

        momentDao = new MomentDao(momentDaoConfig, this);
        userDao = new UserDao(userDaoConfig, this);
        chatDao = new ChatDao(chatDaoConfig, this);
        photoDao = new PhotoDao(photoDaoConfig, this);
        notificationDao = new NotificationDao(notificationDaoConfig, this);

        registerDao(Moment.class, momentDao);
        registerDao(User.class, userDao);
        registerDao(Chat.class, chatDao);
        registerDao(Photo.class, photoDao);
        registerDao(Notification.class, notificationDao);
    }
    
    public void clear() {
        momentDaoConfig.getIdentityScope().clear();
        userDaoConfig.getIdentityScope().clear();
        chatDaoConfig.getIdentityScope().clear();
        photoDaoConfig.getIdentityScope().clear();
        notificationDaoConfig.getIdentityScope().clear();
    }

    public MomentDao getMomentDao() {
        return momentDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public ChatDao getChatDao() {
        return chatDao;
    }

    public PhotoDao getPhotoDao() {
        return photoDao;
    }

    public NotificationDao getNotificationDao() {
        return notificationDao;
    }

}
