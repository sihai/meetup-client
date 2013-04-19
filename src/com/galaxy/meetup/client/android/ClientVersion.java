/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 
 * @author sihai
 *
 */
public class ClientVersion {

	private static volatile Integer sCachedValue;
	private static Object _cache_lock_ = new Object();
	
	public static int from(Context context) {
        if(null == sCachedValue) {
        	int version = Integer.valueOf(getVersionCode(context));
        	synchronized(_cache_lock_) {
				if(null == sCachedValue) {
					sCachedValue = version;
				}
        	}
        }
        return sCachedValue;
    }

    private static int getVersionCode(Context context) {
    	int versionCode = 0;
        try {
        	versionCode  = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch(NameNotFoundException e) { }
        return versionCode;
    }
}