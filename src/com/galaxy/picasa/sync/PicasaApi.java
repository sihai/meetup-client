/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.android.gallery3d.common.Utils;
import com.galaxy.meetup.client.util.FIFEUtil;
import com.galaxy.meetup.client.util.ImageProxyUtil;
import com.galaxy.picasa.store.MetricsUtils;

/**
 * 
 * @author sihai
 *
 */
public class PicasaApi {

	private final String mBaseUrl;
    private final GDataClient mClient = new GDataClient();
    private final GDataClient.Operation mOperation = new GDataClient.Operation();
	
    public PicasaApi(ContentResolver contentresolver) {
        String s = android.provider.Settings.Secure.getString(contentresolver, "picasa_gdata_base_url");
        if(s == null)
            s = "https://picasaweb.google.com/data/feed/api/";
        mBaseUrl = s;
    }
    
	public static String convertImageUrl(String s, int i, boolean flag) {
		String s1;
		if (FIFEUtil.isFifeHostedUrl(s)) {
			boolean flag1 = FIFEUtil.getImageUrlOptions(s).contains("I");
			StringBuilder stringbuilder = new StringBuilder();
			stringbuilder.append('s').append(i);
			if (flag)
				stringbuilder.append("-c");
			if (flag1)
				stringbuilder.append("-I");
			s1 = FIFEUtil.setImageUrlOptions(stringbuilder.toString(), s)
					.toString();
		} else {
			if (flag)
				Log.w("gp.PicasaAPI", "not a FIFE url, ignore the crop option");
			s1 = ImageProxyUtil.setImageUrlSize(i, s);
		}
		return s1;
	}
    
	private static String encodeUsername(String s) {
		String s1 = s.toLowerCase();
		if (s1.contains("@gmail.") || s1.contains("@googlemail."))
			s1 = s1.substring(0, s1.indexOf('@'));
		return Uri.encode(s1);
	}
	
	private int getAlbumPhotos(AlbumEntry albumentry, String s, String s1, PhotoCollectorJson photocollectorjson) {
		int i = MetricsUtils.begin((new StringBuilder("PicasaApi.")).append(s1).toString());
        if(Log.isLoggable("gp.PicasaAPI", 2)) {
            String s2 = (new StringBuilder()).append(s1).append(" for %s / %s, etag: %s").toString();
            Object aobj[] = new Object[3];
            aobj[0] = Utils.maskDebugInfo(albumentry.user);
            aobj[1] = Utils.maskDebugInfo(Long.valueOf(albumentry.id));
            aobj[2] = albumentry.photosEtag;
            Log.v("gp.PicasaAPI", String.format(s2, aobj));
        }
        int value = 0;
        GDataClient.Operation operation;
        operation = mOperation;
        operation.inOutEtag = albumentry.photosEtag;
        try {
	        mClient.get(s, operation);
	        switch(operation.outStatus) {
		        case 200:
		        	albumentry.photosEtag = operation.inOutEtag;
		            photocollectorjson.parse(operation.outBody);
		            value = 0;
		            break;
		        case 304:
		        	value = 1;
		            break;
		        case 401:
		        case 403:
		        	value = 2;
		            break;
	        	default:
	        		Log.e("gp.PicasaAPI", (new StringBuilder()).append(s1).append(" fail: ").append(operation.outStatus).toString());
	        		value = 3;
	        		break;
	        }
        } catch (Exception e) {
        	Utils.handleInterrruptedException(e);
        	Log.e("gp.PicasaAPI", (new StringBuilder()).append(s1).append(" fail").toString(), e);
        	//throw e;
        } finally {
        	Utils.closeSilently(operation.outBody);
            MetricsUtils.end(i);
        }
        return value;
    }

	public final int getAlbumPhotos(AlbumEntry albumentry,
			EntryHandler entryhandler) {
		return getAlbumPhotos(
				albumentry,
				(new StringBuilder(mBaseUrl))
						.append("user/")
						.append(encodeUsername(albumentry.user))
						.append("/albumid/")
						.append(albumentry.id)
						.append("?max-results=1000&imgmax=d&thumbsize=640u&visibility=visible&v=4&alt=json&fd=shapes")
						.append("&kind=photo").toString(), "getAlbumPhotos",
				new PhotoCollectorJson(entryhandler));
	}

    public final int getAlbums(UserEntry userentry, EntryHandler entryhandler) {
        int i;
        StringBuilder stringbuilder;
        i = MetricsUtils.begin("PicasaApi.getAlbums");
        stringbuilder = (new StringBuilder(mBaseUrl)).append("user/").append(encodeUsername(userentry.account)).append("?max-results=1000&imgmax=d&thumbsize=640u&visibility=visible&v=4&alt=json&fd=shapes").append("&kind=album");
        GDataClient.Operation operation;
        operation = mOperation;
        operation.inOutEtag = userentry.albumsEtag;
        if(Log.isLoggable("gp.PicasaAPI", 2)) {
            Object aobj2[] = new Object[2];
            aobj2[0] = Utils.maskDebugInfo(userentry.account);
            aobj2[1] = userentry.albumsEtag;
            Log.v("gp.PicasaAPI", String.format("getAlbums for %s, etag = %s", aobj2));
        }
        int value = 0;
        try {
	        mClient.get(stringbuilder.toString(), operation);
	        switch(operation.outStatus) {
		        case 200:
		        	 userentry.albumsEtag = operation.inOutEtag;
		             (new AlbumCollectorJson(entryhandler)).parse(operation.outBody);
		             value = 0;
		             break;
		        case 304:
		        	value = 1;
		            break;
		        case 401:
		        case 403:
		        	 Object aobj[] = new Object[2];
		             aobj[0] = Utils.maskDebugInfo(stringbuilder.toString());
		             aobj[1] = Integer.valueOf(operation.outStatus);
		             Log.e("gp.PicasaAPI", String.format("    getAlbums fail - uri: %s, status code: %s", aobj));
		        	value = 2;
		            break;
	        	default:
	        		Object aobj1[] = new Object[2];
	                aobj1[0] = Utils.maskDebugInfo(stringbuilder.toString());
	                aobj1[1] = Integer.valueOf(operation.outStatus);
	                Log.e("gp.PicasaAPI", String.format("    getAlbums fail - uri: %s, status code: %s", aobj1));
	                value = 3;
	                break;
	        }
        } catch (Exception e) {
        	Utils.handleInterrruptedException(e);
        	Log.e("gp.PicasaAPI", "getAlbums fail", e);
        	//throw e;
        } finally {
        	Utils.closeSilently(operation.outBody);
            MetricsUtils.end(i);
        }
        return value;
    }

	public final void setAuthToken(String s) {
		mClient.setAuthToken(s);
	}
    
	public static interface EntryHandler {

		void handleEntry(ContentValues contentvalues);
	}
}
