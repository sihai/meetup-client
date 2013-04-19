/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.TelephonyManager;

/**
 * 
 * @author sihai
 *
 */
public class AndroidUtils {

	private static final String ANDROID_ID_PROJECTION[] = {
        "android_id"
    };
    private static final Uri GSERVICES_CONTENT_URI = Uri.parse("content://com.google.android.gsf.gservices");

    
	public static long getAndroidId(Context context) {
        long l = 0L;
        Cursor cursor = null;
        try {
        	cursor = context.getContentResolver().query(GSERVICES_CONTENT_URI, null, null, ANDROID_ID_PROJECTION, null);
        	if(null != cursor && cursor.moveToFirst()) {
        		String s = cursor.getString(1);
        		if(null != s) {
        			l = Long.parseLong(s);
        		}
        	}
        } catch (NumberFormatException e) {
        	
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
        return l;
    }

    public static boolean hasTelephony(Context context)
    {
        boolean flag;
        if(((TelephonyManager)context.getSystemService("phone")).getPhoneType() != 0)
            flag = true;
        else
            flag = false;
        return flag;
    }

}
