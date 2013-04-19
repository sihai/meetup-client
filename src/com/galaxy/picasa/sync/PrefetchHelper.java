/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StatFs;
import android.util.Log;

import com.android.gallery3d.common.Utils;
import com.galaxy.picasa.Config;
import com.galaxy.picasa.store.MetricsUtils;
import com.galaxy.picasa.store.PicasaStoreFacade;

/**
 * 
 * @author sihai
 *
 */
public class PrefetchHelper {

	private static final String ALBUM_TABLE_NAME;
    private static final String PHOTO_TABLE_NAME;
    private static final String PROJECTION_ID[] = {
        "_id"
    };
    private static final String PROJECTION_ID_CACHE_FLAG_STATUS_THUMBNAIL[] = {
        "_id", "cache_flag", "cache_status", "thumbnail_url"
    };
    private static final String PROJECTION_ID_ROTATION_CONTENT_URL_CONTENT_TYPE_SCREENNAIL_URL[] = {
        "_id", "rotation", "content_url", "content_type", "screennail_url"
    };
    private static final String PROJECTION_ID_SCREENNAIL_URL[] = {
        "_id", "screennail_url"
    };
    private static final String PROJECTION_ID_THUMBNAIL_URL[] = {
        "_id", "thumbnail_url"
    };
    private static final String QUERY_CACHE_STATUS_COUNT;
    private static final String WHERE_ALBUM_ID_AND_CACHE_STATUS = String.format("%s=? AND %s=?", new Object[] {
        "album_id", "cache_status"
    });
    private static final String WHERE_CACHE_STATUS_AND_USER_ID = String.format("%s = ? AND %s = ?", new Object[] {
        "cache_status", "user_id"
    });
    private static final String WHERE_USER_ID_AND_CACHE_FLAG = String.format("%s=? AND %s=?", new Object[] {
        "user_id", "cache_flag"
    });
    private static PrefetchHelper sInstance;
    private AtomicInteger mCacheConfigVersion;
    private String mCacheDir;
    private final Context mContext;
    private final PicasaDatabaseHelper mDbHelper;

    static {
        ALBUM_TABLE_NAME = AlbumEntry.SCHEMA.getTableName();
        PHOTO_TABLE_NAME = PhotoEntry.SCHEMA.getTableName();
        Object aobj[] = new Object[10];
        aobj[0] = PHOTO_TABLE_NAME;
        aobj[1] = "cache_status";
        aobj[2] = PHOTO_TABLE_NAME;
        aobj[3] = ALBUM_TABLE_NAME;
        aobj[4] = PHOTO_TABLE_NAME;
        aobj[5] = "album_id";
        aobj[6] = ALBUM_TABLE_NAME;
        aobj[7] = "_id";
        aobj[8] = ALBUM_TABLE_NAME;
        aobj[9] = "cache_flag";
        QUERY_CACHE_STATUS_COUNT = String.format("SELECT count(*), %s.%s AS status FROM %s, %s WHERE %s.%s = %s.%s AND %s.%s = ? GROUP BY status", aobj);
    }
    
	private PrefetchHelper(Context context) {
		mCacheConfigVersion = new AtomicInteger(0);
		mContext = context.getApplicationContext();
		mDbHelper = PicasaDatabaseHelper.get(context);
	}
	
	private static void collectKeepSet(SQLiteDatabase sqlitedatabase, long l, HashMap hashmap, Integer integer) {
        Cursor cursor = null;
        String as[] = new String[1];
        as[0] = String.valueOf(l);
        try {
        	cursor = sqlitedatabase.query(PHOTO_TABLE_NAME, PROJECTION_ID, "album_id=?", as, null, null, null);
        	while(cursor.moveToNext()) 
        		hashmap.put(Long.valueOf(cursor.getLong(0)), integer);
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }
	
	private void deleteUnusedAlbumCovers(HashSet hashset) throws IOException {
		File file = new File(getCacheDirectory(), "picasa_covers");
		String as[] = file.list();
		if (as != null) {
			int i = as.length;
			int j = 0;
			while (j < i) {
				String s = as[j];
				int k = s.lastIndexOf('.');
				String s1;
				if (k < 0)
					s1 = s;
				else
					s1 = s.substring(0, k);
				if (!hashset.contains(s1) && !(new File(file, s)).delete())
					Log.w("PrefetchHelper", (new StringBuilder(
							"cannot delete album cover: ")).append(s)
							.toString());
				j++;
			}
		}
	}
	
	private void deleteUnusedCacheFiles(PrefetchContext prefetchcontext,
			HashMap hashmap) throws IOException {
		String s;
		String as[];
		int i;
		int j;
		s = getCacheDirectory();
		as = (new File(s)).list();
		i = as.length;
		j = 0;
		while (j < i) {
			String s1 = as[j];
			if (prefetchcontext.syncInterrupted()) {
				break;
			}
			if (s1.startsWith("picasa-")) {
				File file;
				String as1[];
				file = new File(s, s1);
				as1 = file.list();

				int k;
				int l;
				k = as1.length;
				l = 0;
				while (l < k) {
					String s2 = as1[l];
					if (!prefetchcontext.syncInterrupted()) {
						if (!keepCacheFile(file, s2, hashmap))
							(new File(file, s2)).delete();
					}
				}
				if (file.list().length == 0)
					file.delete();
			}
		}
	}
	
	
	private String getCacheDirectory() throws IOException {
		if (mCacheDir == null) {
			File file = PicasaStoreFacade.getCacheDirectory();
			if (file == null)
				throw new IOException("external storage is not present");
			mCacheDir = file.getAbsolutePath();
		}
		return mCacheDir;
	}
	
	private static boolean keepCacheFile(File file, String s, HashMap hashmap) {
		boolean flag = false;
		int i = s.lastIndexOf('.');
		if (-1 == i) {
			return flag;
		}

		String s1 = s.substring(0, i);
		String s2 = s.substring(i);
		long l = Long.parseLong(s1);
		Integer integer = (Integer) hashmap.get(Long.valueOf(l));
		if (null != integer) {
			if (2 == integer.intValue()) {
				flag = false;
				if (".full".equals(s2)) {
					boolean i1 = (new File(file, (new StringBuilder())
							.append(s1).append(".screen").toString())).length() != 0L;
					flag = false;
					if (i1)
						hashmap.remove(Long.valueOf(l));
				}
				return flag;
			} else if (1 == integer.intValue()) {
				if (".screen".equals(s2)) {
					hashmap.remove(Long.valueOf(l));
					flag = true;
				}
				return flag;
			}
		}
		return true;
	}
	
	private void notifyAlbumsChange() {
		mContext.getContentResolver().notifyChange(
				PicasaFacade.get(mContext).getAlbumsUri(), null, false);
	}
	
	private static void setCacheStatus(SQLiteDatabase sqlitedatabase,
			HashMap hashmap) {
		String as[];
		ContentValues contentvalues = new ContentValues();
		as = new String[1];
		Iterator iterator = hashmap.entrySet().iterator();
		try {
			sqlitedatabase.beginTransaction();
			Map.Entry entry;
			while (iterator.hasNext()) {
				entry = (Map.Entry) iterator.next();
				if (((Integer) entry.getValue()).intValue() == 2) {
					contentvalues.put("cache_status", 2);
					as[0] = String.valueOf(entry.getKey());
					sqlitedatabase.update(PHOTO_TABLE_NAME, contentvalues,
							"_id=?", as);
				}
			}
			sqlitedatabase.endTransaction();
		} finally {
			sqlitedatabase.endTransaction();
		}
	}
	
	private boolean syncOnePhoto(PrefetchContext prefetchcontext, long l,
			String s, String s1) throws IOException {
		long l1 = getAvailableStorage();
		if (l1 < 0x40000000L)
			throw new RuntimeException(
					(new StringBuilder("space not enough: ")).append(l1)
							.append(", stop sync").toString());
		File file = PicasaStoreFacade.createCacheFile(l, ".download");
		if (file == null)
			throw new IOException("external storage absent?");
		if (Log.isLoggable("PrefetchHelper", 2) && s1 == ".full")
			Log.v("PrefetchHelper",
					(new StringBuilder("download full image for ")).append(l)
							.append(": ").append(Utils.maskDebugInfo(s))
							.toString());
		boolean flag;
		if (!downloadPhoto(prefetchcontext, s, file)) {
			file.delete();
			prefetchcontext.onDownloadFinish(l, false);
			flag = false;
		} else if (!file.renameTo(PicasaStoreFacade.createCacheFile(l, s1))) {
			Log.e("PrefetchHelper", (new StringBuilder("cannot rename file: "))
					.append(file).toString());
			file.delete();
			prefetchcontext.onDownloadFinish(l, false);
			flag = false;
		} else {
			prefetchcontext.onDownloadFinish(l, true);
			ContentValues contentvalues = new ContentValues();
			contentvalues.put("cache_status", Integer.valueOf(0));
			SQLiteDatabase sqlitedatabase = mDbHelper.getWritableDatabase();
			String s2 = PHOTO_TABLE_NAME;
			String as[] = new String[1];
			as[0] = String.valueOf(l);
			sqlitedatabase.update(s2, contentvalues, "_id=?", as);
			flag = true;
		}
		return flag;
	}
	
	public final void syncFullImagesForUser(PrefetchContext prefetchcontext, UserEntry userentry) throws IOException {
		// TODO
	}
	
	private void updateAlbumCacheStatus(SQLiteDatabase sqlitedatabase, long l,
			int i) {
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("cache_status", Integer.valueOf(i));
		String as[] = new String[1];
		as[0] = String.valueOf(l);
		sqlitedatabase.update(ALBUM_TABLE_NAME, contentvalues, "_id=?", as);
		notifyAlbumsChange();
	}
	
	public final void cleanCache(PrefetchContext prefetchcontext)
			throws IOException {
		int i;
		HashMap hashmap;
		HashSet hashset;
		SQLiteDatabase sqlitedatabase;
		Cursor cursor = null;
		i = MetricsUtils.begin("PrefetchHelper.cleanCache");
		hashmap = new HashMap();
		hashset = new HashSet();
		sqlitedatabase = mDbHelper.getWritableDatabase();
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("cache_status", Integer.valueOf(0));
		sqlitedatabase.update(PHOTO_TABLE_NAME, contentvalues,
				"cache_status <> 0", null);
		try {
			cursor = sqlitedatabase.query(ALBUM_TABLE_NAME,
					PROJECTION_ID_CACHE_FLAG_STATUS_THUMBNAIL, null, null,
					null, null, null);

			while (cursor.moveToNext()) {
				if (prefetchcontext.syncInterrupted()) {
					return;
				}
				long l = cursor.getLong(0);
				int j = cursor.getInt(1);
				int k = cursor.getInt(2);
				hashset.add(PicasaStoreFacade.getAlbumCoverKey(l,
						cursor.getString(3)));
				if (j == 2) {
					if ((k != 3) && (k != 1))
						updateAlbumCacheStatus(sqlitedatabase, l, 1);
					collectKeepSet(sqlitedatabase, l, hashmap,
							Integer.valueOf(2));
				} else if (j == 1) {
					if (k != 0)
						updateAlbumCacheStatus(sqlitedatabase, l, 0);
					collectKeepSet(sqlitedatabase, l, hashmap,
							Integer.valueOf(1));
				} else if ((j == 0) && (k != 0)) {
					updateAlbumCacheStatus(sqlitedatabase, l, 0);
				}
			}
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}

		deleteUnusedAlbumCovers(hashset);
		deleteUnusedCacheFiles(prefetchcontext, hashmap);
		setCacheStatus(sqlitedatabase, hashmap);
		MetricsUtils.end(i);
	}
	
	public final PrefetchContext createPrefetchContext(SyncResult syncresult, Thread thread) {
        return new PrefetchContext(syncresult, thread);
    }
	
	public final CacheStats getCacheStatistics(int i) {
		Cursor cursor = null;
		CacheStats cachestats;
		SQLiteDatabase sqlitedatabase = mDbHelper.getReadableDatabase();
		String as[] = new String[1];
		as[0] = String.valueOf(2);
		try {
			cursor = sqlitedatabase.rawQuery(QUERY_CACHE_STATUS_COUNT, as);
			cachestats = new CacheStats();
			if (null != cursor) {
				while (cursor.moveToNext()) {
					int j = cursor.getInt(0);
					if (cursor.getInt(1) != 0)
						cachestats.pendingCount = j + cachestats.pendingCount;
					cachestats.totalCount = j + cachestats.totalCount;
				}
			}
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return cachestats;
	}
	
	public final void setAlbumCachingFlag(long l, int i) {

		if (0 == i || 1 == i || 2 == i) {
			ContentValues contentvalues = new ContentValues();
			contentvalues.put("cache_flag", Integer.valueOf(i));
			String as[] = new String[1];
			as[0] = String.valueOf(l);
			if (mDbHelper.getWritableDatabase().update(ALBUM_TABLE_NAME,
					contentvalues, "_id=?", as) > 0) {
				mCacheConfigVersion.incrementAndGet();
				notifyAlbumsChange();
				PicasaSyncManager.get(mContext).requestPrefetchSync();
			}
		}
	}
	
	public final void syncAlbumCoversForUser(PrefetchContext prefetchcontext,
			UserEntry userentry) throws IOException {
		File file = new File(getCacheDirectory(), "picasa_covers");
		if (!file.isDirectory() && !file.mkdirs()) {
			Log.e("PrefetchHelper", "cannot create album-cover folder");
			return;
		}

		Cursor cursor = null;
		SQLiteDatabase sqlitedatabase = mDbHelper.getWritableDatabase();
		String as[] = new String[1];
		as[0] = String.valueOf(userentry.id);
		try {
			cursor = sqlitedatabase.query(ALBUM_TABLE_NAME,
					PROJECTION_ID_THUMBNAIL_URL, "user_id=?", as, null, null,
					null);
			if (null != cursor) {
				while (cursor.moveToNext()) {
					if (prefetchcontext.syncInterrupted()) {
						break;
					}
					long l;
					String s;
					File file1;
					l = cursor.getLong(0);
					s = cursor.getString(1);
					file1 = getAlbumCoverCacheFile(l, s, ".thumb");
					if (file1.isFile())
						continue;
					long size = getAvailableStorage();
					if (size < 0x40000000L)
						throw new RuntimeException((new StringBuilder(
								"space not enough: ")).append(size)
								.append(", stop sync").toString());
					File file2 = getAlbumCoverCacheFile(l, s, ".download");
					if (!downloadPhoto(prefetchcontext,
							PicasaApi.convertImageUrl(s, Config.sThumbNailSize,
									true), file2))
						file2.delete();
					else if (!file2.renameTo(getAlbumCoverCacheFile(l, s,
							".thumb"))) {
						Log.e("PrefetchHelper", (new StringBuilder(
								"cannot rename file: ")).append(file2)
								.toString());
						file2.delete();
					}
				}
			}
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
	}
	
	private static File getAlbumCoverCacheFile(long l, String s, String s1) throws IOException {
		File file = PicasaStoreFacade.getAlbumCoverCacheFile(l, s, s1);
		if (file == null)
			throw new IOException("external storage not present");
		else
			return file;
	}
	
	private long getAvailableStorage() {
		try {
			StatFs statfs = new StatFs(getCacheDirectory());
			return statfs.getAvailableBlocks() * statfs.getBlockSize();
		} catch (Throwable t) {
			Log.w("PrefetchHelper", "Fail to getAvailableStorage", t);
			return 0L;
		}
	}
	
	private static boolean downloadPhoto(PrefetchContext prefetchcontext, String s, File file)
    {
        // TODO
		return false;
    }
	
	public final void syncScreenNailsForUser(PrefetchContext prefetchcontext, UserEntry userentry) throws IOException {
        Cursor cursor = null;
        String as[] = new String[2];
        as[0] = String.valueOf(1);
        as[1] = String.valueOf(userentry.id);
        try {
        	cursor = mDbHelper.getWritableDatabase().query(PHOTO_TABLE_NAME, PROJECTION_ID_SCREENNAIL_URL, WHERE_CACHE_STATUS_AND_USER_ID, as, null, null, "display_index");
        	if(null != cursor) {
        		while(cursor.moveToNext()) {
	        		 if(syncOnePhoto(prefetchcontext, cursor.getLong(0), PicasaApi.convertImageUrl(cursor.getString(1), Config.sScreenNailSize, false), ".screen") || prefetchcontext.getDownloadFailCount() <= 3) 
	        			 continue; 
	        		 else 
	        			 throw new RuntimeException("too many fail downloads");
        		}
        	}
        	
        	if(!prefetchcontext.checkCacheConfigVersion())
	        {
	            Log.w("PrefetchHelper", "cache config has changed, stop sync");
	            prefetchcontext.stopSync();
	        }
	        prefetchcontext.syncInterrupted();
	        
        } finally {
        	if(null == cursor) {
        		cursor.close();
        	}
        }
    }
	
	public static synchronized PrefetchHelper get(Context context)
    {
        PrefetchHelper prefetchhelper;
        if(sInstance == null)
            sInstance = new PrefetchHelper(context);
        prefetchhelper = sInstance;
        return prefetchhelper;
    }

	
	public static final class CacheStats {

		public int pendingCount;
		public int totalCount;

		public CacheStats() {
		}
	}

	public final class PrefetchContext {
		private PrefetchListener mCacheListener;
		private int mDownloadFailCount;
		private int mLastVersion;
		private volatile boolean mStopSync;
		private Thread mThread;
		public SyncResult result;

		public PrefetchContext(SyncResult syncresult, Thread thread) {
			super();
			result = (SyncResult) Utils.checkNotNull(syncresult);
			mThread = thread;
		}

		public final boolean checkCacheConfigVersion() {
			boolean flag;
			if (mLastVersion == mCacheConfigVersion.get())
				flag = true;
			else
				flag = false;
			return flag;
		}

		public final int getDownloadFailCount() {
			return mDownloadFailCount;
		}

		public final void onDownloadFinish(long l, boolean flag) {
			int i;
			if (flag)
				i = 0;
			else
				i = 1 + mDownloadFailCount;
			mDownloadFailCount = i;
			if (mCacheListener != null)
				mCacheListener.onDownloadFinish();
		}

		public final void setCacheDownloadListener(
				PrefetchListener prefetchlistener) {
			mCacheListener = prefetchlistener;
		}

		public final void stopSync() {
			mStopSync = true;
			mThread.interrupt();
		}

		public final boolean syncInterrupted() {
			return mStopSync;
		}

		public final void updateCacheConfigVersion() {
			mLastVersion = mCacheConfigVersion.get();
		}
	}

	public static interface PrefetchListener {

		void onDownloadFinish();
	}
    
}
