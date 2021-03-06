package com.chomp.wifistorymachine.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig audiobooksTypeDaoConfig;
    private final DaoConfig audiobooksSortDaoConfig;
    private final DaoConfig audiobooksListDaoConfig;

    private final AudiobooksTypeDao audiobooksTypeDao;
    private final AudiobooksSortDao audiobooksSortDao;
    private final AudiobooksListDao audiobooksListDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        audiobooksTypeDaoConfig = daoConfigMap.get(AudiobooksTypeDao.class).clone();
        audiobooksTypeDaoConfig.initIdentityScope(type);

        audiobooksSortDaoConfig = daoConfigMap.get(AudiobooksSortDao.class).clone();
        audiobooksSortDaoConfig.initIdentityScope(type);

        audiobooksListDaoConfig = daoConfigMap.get(AudiobooksListDao.class).clone();
        audiobooksListDaoConfig.initIdentityScope(type);

        audiobooksTypeDao = new AudiobooksTypeDao(audiobooksTypeDaoConfig, this);
        audiobooksSortDao = new AudiobooksSortDao(audiobooksSortDaoConfig, this);
        audiobooksListDao = new AudiobooksListDao(audiobooksListDaoConfig, this);

        registerDao(AudiobooksType.class, audiobooksTypeDao);
        registerDao(AudiobooksSort.class, audiobooksSortDao);
        registerDao(AudiobooksList.class, audiobooksListDao);
    }
    
    public void clear() {
        audiobooksTypeDaoConfig.getIdentityScope().clear();
        audiobooksSortDaoConfig.getIdentityScope().clear();
        audiobooksListDaoConfig.getIdentityScope().clear();
    }

    public AudiobooksTypeDao getAudiobooksTypeDao() {
        return audiobooksTypeDao;
    }

    public AudiobooksSortDao getAudiobooksSortDao() {
        return audiobooksSortDao;
    }

    public AudiobooksListDao getAudiobooksListDao() {
        return audiobooksListDao;
    }

}
