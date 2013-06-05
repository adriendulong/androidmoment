package com.moment.models;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import java.util.Map;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig momentDaoConfig;

    private final MomentDao momentDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        momentDaoConfig = daoConfigMap.get(MomentDao.class).clone();
        momentDaoConfig.initIdentityScope(type);

        momentDao = new MomentDao(momentDaoConfig, this);

        registerDao(Moment.class, momentDao);
    }
    
    public void clear() {
        momentDaoConfig.getIdentityScope().clear();
    }

    public MomentDao getMomentDao() {
        return momentDao;
    }

}
