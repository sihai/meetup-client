/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.store;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.android.gallery3d.common.Utils;
import com.galaxy.meetup.client.util.FIFEUtil;
import com.galaxy.meetup.client.util.ImageProxyUtil;

/**
 * 
 * @author sihai
 *
 */
public class PicasaStoreFacade {

	private static File sCacheDir;
    private static PicasaStoreFacade sInstance;
    private static Class sNetworkReceiver;
    private Uri mAlbumCoversUri;
    private String mAuthority;
    private Uri mCachedFingerprintUri;
    private final Context mContext;
    private Uri mFingerprintUri;
    private MatrixStoreInfo mLocalInfo;
    private MatrixStoreInfo mMasterInfo;
    private Uri mPhotosUri;
    private Uri mRecalculateFingerprintUri;
    
	private PicasaStoreFacade(Context context) {
		mContext = context.getApplicationContext();
		updatePicasaSyncInfo(true);
	}
	
	public static void broadcastOperationReport(String s, long l, long l1, int i, long l2, long l3) {
		if (sInstance != null && sNetworkReceiver != null) {
			Context context = sInstance.mContext;
			Intent intent = new Intent(context, sNetworkReceiver);
			intent.setAction("com.google.android.picasastore.op_report");
			intent.putExtra("op_name", s);
			intent.putExtra("total_time", l);
			intent.putExtra("net_duration", l1);
			intent.putExtra("transaction_count", i);
			intent.putExtra("sent_bytes", l2);
			intent.putExtra("received_bytes", l3);
			context.sendBroadcast(intent);
		}
    }
	
	public static String convertImageUrl(String s, int i, boolean flag) {
		String s1;
		if (FIFEUtil.isFifeHostedUrl(s)) {
			String s2 = FIFEUtil.getImageUrlOptions(s);
			boolean flag1 = s2.contains("I");
			boolean flag2 = s2.contains("k");
			StringBuilder stringbuilder = new StringBuilder();
			stringbuilder.append('s').append(i);
			stringbuilder.append("-no");
			if (flag)
				stringbuilder.append("-c");
			if (flag1)
				stringbuilder.append("-I");
			if (flag2)
				stringbuilder.append("-k");
			s1 = FIFEUtil.setImageUrlOptions(stringbuilder.toString(), s).toString();
		} else {
			if (flag)
				Log.w("gp.PicasaStore",
						"not a FIFE url, ignore the crop option");
			s1 = ImageProxyUtil.setImageUrlSize(i, s);
		}
		return s1;
	}
	
	public static File createCacheFile(long l, String s)
    {
        File file = getCacheDirectory();
        if(file == null) {
        	return null;
        }
        
        // TODO
        return null;
    }
	
	public static synchronized PicasaStoreFacade get(Context context) {
		PicasaStoreFacade r;
		if (sInstance == null)
			sInstance = new PicasaStoreFacade(context);
		r = sInstance;
		return r;
	}
	
	public static File getAlbumCoverCacheFile(long l, String s, String s1)
    {
        File file = getCacheDirectory();
        File file1;
        if(file == null)
            file1 = null;
        else
            file1 = new File(file, (new StringBuilder("picasa_covers/")).append(getAlbumCoverKey(l, s)).append(s1).toString());
        return file1;
    }

    public static String getAlbumCoverKey(long l, String s)
    {
        return (new StringBuilder()).append(l).append('_').append(Utils.crc64Long(s)).toString();
    }

    public static synchronized File getCacheDirectory()
    {
    	try {
	    	boolean flag = false;
	        if(null == sCacheDir) {
	        	File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "cache/com.google.android.googlephotos");
	            sCacheDir = file;
	            if(!file.isDirectory()) {
	            	flag = sCacheDir.mkdirs();
	            }
	            if(flag) {
	            	File file1 = new File(sCacheDir, ".nomedia");
	            	if(!file1.exists())
	                    file1.createNewFile();
	            }
	        }
    	} catch (IOException ioexception) {
    		Log.w("gp.PicasaStore", (new StringBuilder("fail to create '.nomedia' in ")).append(sCacheDir).toString());
            sCacheDir = null;
    	}
    	
    	return sCacheDir;
    }

    public static File getCacheFile(long l, String s)
    {
        File file = getCacheDirectory();
        if(file == null) {
        	return null;
        }
        
        File file2 = null;
        String s1 = (new StringBuilder()).append(l).append(s).toString();
        int i = (int)(l % 10L);
        String s2 = (new StringBuilder("picasa--")).append(i).toString();
        for(int j = 0; j < 5; j++)
        {
            File file1 = new File(file, s2);
            if(!file1.exists())
            {
                file2 = null;
            }
            if(file1.isDirectory())
            {
                file2 = new File(file1, s1);
                if(file2.exists()) {
                	return file2;
                }
            }
            s2 = (new StringBuilder()).append(s2).append("e").toString();
        }

        file2 = null;
        return file2;
    }

    public static void setNetworkReceiver(Class class1)
    {
        sNetworkReceiver = class1;
    }

    private synchronized void updatePicasaSyncInfo(boolean flag)
    {
        // TODO
    }

	public final String getAuthority() {
		return mAuthority;
	}

	public final Uri getFingerprintUri() {
		return mFingerprintUri;
	}

	public final Uri getFingerprintUri(boolean flag, boolean flag1) {
		Uri uri;
		if (flag)
			uri = mRecalculateFingerprintUri;
		else if (flag1)
			uri = mCachedFingerprintUri;
		else
			uri = mFingerprintUri;
		return uri;
	}

	public final Uri getPhotoUri(long l, String s, String s1) {
		return mPhotosUri.buildUpon().appendPath(String.valueOf(l))
				.appendQueryParameter("type", s)
				.appendQueryParameter("content_url", s1).build();
	}

	public final boolean isMaster() {
		boolean flag;
		if (mMasterInfo == mLocalInfo)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public final void onPackageAdded() {
		updatePicasaSyncInfo(false);
	}

	public final void onPackageRemoved() {
		updatePicasaSyncInfo(false);
	}
	
	public static class DummyService extends Service {

		public IBinder onBind(Intent intent) {
			return null;
		}

		public DummyService() {
		}
	}
	
	static final class MatrixStoreInfo {

		public final String authority;
		public final String packageName;
		public final int priority;

		public MatrixStoreInfo(String s, String s1, int i) {
			packageName = s;
			authority = s1;
			priority = i;
		}
	}
}
