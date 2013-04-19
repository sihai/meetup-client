/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.iu;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.android.gallery3d.common.Fingerprint;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.picasa.store.PicasaStoreFacade;

/**
 * 
 * @author sihai
 *
 */
public class FingerprintHelper {

	private static FingerprintHelper sInstance;
    private final Uri mCachedFingerprintUri;
    private final Uri mFingerprintUri;
    private final Uri mRecalculateFingerprintUri;
    private final ContentResolver mResolver;
    
    private FingerprintHelper(Context context)
    {
        mResolver = context.getContentResolver();
        PicasaStoreFacade picasastorefacade = PicasaStoreFacade.get(context);
        mFingerprintUri = picasastorefacade.getFingerprintUri();
        mCachedFingerprintUri = picasastorefacade.getFingerprintUri(false, true);
        mRecalculateFingerprintUri = picasastorefacade.getFingerprintUri(true, false);
    }

    public static synchronized FingerprintHelper get(Context context)
    {
        FingerprintHelper fingerprinthelper;
        if(sInstance == null)
            sInstance = new FingerprintHelper(context);
        fingerprinthelper = sInstance;
        return fingerprinthelper;
    }

    private Fingerprint getFingerprint(Uri uri, String s) {
    	
    	Cursor cursor = null;
    	
    	try {
	        cursor = mResolver.query(uri, new String[] {
	            s
	        }, null, null, null);
	        if(null != cursor && cursor.moveToNext() && !cursor.isNull(0)) {
	        	return new Fingerprint(cursor.getBlob(0));
	        }
	        return null;
    	} catch (Throwable t) {
    		if(EsLog.isLoggable("FingerprintHelper", 5))
                Log.w("FingerprintHelper", (new StringBuilder("cannot get fingerprint for ")).append(s).toString(), t);
    		return null;
    	} finally {
    		if(null != cursor) {
    			cursor.close();
    		}
    	}
    }

    public final Fingerprint getCachedFingerprint(String s)
    {
        return getFingerprint(mCachedFingerprintUri, s);
    }

    public final synchronized Fingerprint getFingerprint(String s, boolean flag)
    {
        Fingerprint fingerprint = getFingerprint(mRecalculateFingerprintUri, s);
        return fingerprint;
    }

    public final void invalidate(String s)
    {
        mResolver.delete(mFingerprintUri, null, new String[] {
            s
        });
    }
}
