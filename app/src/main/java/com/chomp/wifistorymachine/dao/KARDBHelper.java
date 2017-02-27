package com.chomp.wifistorymachine.dao;

import android.content.Context;

import com.chomp.wifistorymachine.dao.DaoMaster.DevOpenHelper;
import java.util.List;

//db帮助类
public class KARDBHelper {
	private static final String DB_NAME = "KAR_DB";

	private static KARDBHelper instance;
	private static DaoMaster daoMaster;
	private static DaoSession daoSession;

	private static AudiobooksTypeDao audiobooksTypeDao;
	private static AudiobooksSortDao audiobooksSortDao;
	private static AudiobooksListDao audiobooksListDao;

//	private static StoryDBDao storyDBDao;
//	private static StoryTypeDBDao storyTypeDBDao;

	private KARDBHelper(Context context) {
		DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(context,
				DB_NAME, null);
		daoMaster = new DaoMaster(openHelper.getWritableDatabase());
		daoSession = daoMaster.newSession();

		audiobooksTypeDao=daoSession.getAudiobooksTypeDao();
		audiobooksSortDao=daoSession.getAudiobooksSortDao();
		audiobooksListDao=daoSession.getAudiobooksListDao();
	}

	public static KARDBHelper newInstance(Context context) {
		if (instance == null) {
			instance = new KARDBHelper(context);
		}
		return instance;
	}





	private boolean isAudiobooksTypeEmpty(){
		int count= audiobooksTypeDao.queryBuilder().list().size();
		if(count<=0){
			return true;
		}else {
			return false;
		}
	}

	public List<AudiobooksType> queryAllAudiobooksType(){
		return audiobooksTypeDao.queryBuilder().list();
	}


	public void insertAllAudiobooksType(List<AudiobooksType> audiobooksTypes){
		for (AudiobooksType audiobooksType:audiobooksTypes){
			audiobooksTypeDao.insert(audiobooksType);
		}
	}

	public void deleteAllAudiobooksType(){
		audiobooksTypeDao.deleteAll();
	}





	private boolean isAudiobooksSortEmpty(String album){
		int count= audiobooksSortDao.queryBuilder().where(AudiobooksSortDao.Properties.N.eq(album)).list().size();
		if(count<=0){
			return true;
		}else {
			return false;
		}
	}

	public List<AudiobooksSort> queryAudiobooksSort(String album){
        return audiobooksSortDao.queryBuilder().where(AudiobooksSortDao.Properties.N.eq(album)).list();
	}

	public void deleteAudiobooksSort(String album){
		List<AudiobooksSort> deleteList = audiobooksSortDao.queryBuilder().where(AudiobooksSortDao.Properties.N.eq(album)).list();
		for (AudiobooksSort audiobooksSort : deleteList){
			audiobooksSortDao.delete(audiobooksSort);
		}
	};


	public void insertAllAudiobooksSort(List<AudiobooksSort> audiobooksSorts){
		for (AudiobooksSort audiobooksSort:audiobooksSorts){
			audiobooksSortDao.insert(audiobooksSort);
		}
	}





	private boolean isAudiobooksListEmpty(String album, String subfolder){
		int count = audiobooksListDao.queryBuilder().where(AudiobooksListDao.Properties.Album.eq(album),AudiobooksListDao.Properties.Subfolder.eq(subfolder)).list().size();
		if(count<=0){
			return true;
		}else {
			return false;
		}
	}

	public List<AudiobooksList> queryAudiobooksList(String album, String subfolder){
         return audiobooksListDao.queryBuilder().where(AudiobooksListDao.Properties.Album.eq(album),AudiobooksListDao.Properties.Subfolder.eq(subfolder)).list();
	}

	public void deleteAudiobooksList(String album){
		List<AudiobooksList> deleteList=audiobooksListDao.queryBuilder().where(AudiobooksListDao.Properties.Album.eq(album)).list();
		for (AudiobooksList audiobooksList : deleteList){
			audiobooksListDao.delete(audiobooksList);
		}
	}


	public void insertAllAudiobooksList(List<AudiobooksList> audiobooksLists){
		for (AudiobooksList audiobooksList:audiobooksLists){
			audiobooksListDao.insert(audiobooksList);
		}
	}

	public void insertAudiobooksList(AudiobooksList audiobooksLists){
		audiobooksListDao.insert(audiobooksLists);
	}


//
//
//	public boolean isStoryTypeEmpty() {
//		int count = storyTypeDBDao.queryBuilder().list().size();
//		if (count == 0) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	public List<StoryTypeDB> queryAllStoryType() {
//		return storyTypeDBDao.queryBuilder().list();
//	}
//
//	public void deleteAllStoryType() {
//		storyTypeDBDao.deleteAll();
//	}
//
//	public void insertAllStoryType(List<StoryTypeDB> typeDBs) {
//		for (StoryTypeDB storyTypeDB : typeDBs) {
//			storyTypeDBDao.insert(storyTypeDB);
//		}
//	}
//
//	public int getStoryTypeVersion(String name) {
//		QueryBuilder<StoryTypeDB> queryBuilder = storyTypeDBDao.queryBuilder();
//		List<StoryTypeDB> storyTypeDBs = queryBuilder.where(
//				StoryTypeDBDao.Properties.Fn.eq(name)).list();
//		if (storyTypeDBs != null && storyTypeDBs.size() == 1) {
//			StoryTypeDB storyTypeDB = storyTypeDBs.get(0);
//			return storyTypeDB.getVer();
//		}
//		return 1;
//	}
//
//	public void updateStoryTypeVersion(String name, int version) {
//		QueryBuilder<StoryTypeDB> queryBuilder = storyTypeDBDao.queryBuilder();
//		List<StoryTypeDB> storyTypeDBs = queryBuilder.where(
//				StoryTypeDBDao.Properties.Fn.eq(name)).list();
//		if (storyTypeDBs != null && storyTypeDBs.size() == 1) {
//			StoryTypeDB storyTypeDB = storyTypeDBs.get(0);
//			storyTypeDB.setFn(storyTypeDB.getFn());
//			storyTypeDB.setId(storyTypeDB.getId());
//			storyTypeDB.setVer(version);
//			storyTypeDBDao.update(storyTypeDB);
//		}
//	}
//
//	public boolean isStoryEmpty(String type) {
//		int count = storyDBDao.queryBuilder()
//				.where(StoryDBDao.Properties.Type.eq(type)).list().size();
//		if (count == 0) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	public List<StoryDB> queryAllStoryByType(String type) {
//		QueryBuilder<StoryDB> queryBuilder = storyDBDao.queryBuilder();
//		return queryBuilder.where(StoryDBDao.Properties.Type.eq(type)).list();
//	}
//
//	public void deleteAllStoryByType(String type) {
//		QueryBuilder<StoryDB> queryBuilder = storyDBDao.queryBuilder();
//		queryBuilder.where(StoryDBDao.Properties.Type.eq(type)).buildDelete();
//	}
//
//	public void deleteAllStory() {
//		storyDBDao.deleteAll();
//	}
//
//	public void insertAllStory(List<StoryDB> storyDBs) {
//		for (StoryDB storyDB : storyDBs) {
//			storyDBDao.insert(storyDB);
//		}
//	}
}
