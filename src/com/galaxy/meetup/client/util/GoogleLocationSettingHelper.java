/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * 
 * @author sihai
 *
 */
public class GoogleLocationSettingHelper {

	private static final Uri GOOGLE_SETTINGS_CONTENT_URI = Uri.parse("content://com.google.settings/partner");
	
	public static int getUseLocationForServices(Context context) {
        Cursor cursor = null;
        ContentResolver contentresolver = context.getContentResolver();
        String s1;
        try {
        	cursor = contentresolver.query(GOOGLE_SETTINGS_CONTENT_URI, new String[] {
                "value"
            }, "name=?", new String[] {
                "use_location_for_services"
            }, null);
        	String s = null;
            if(null == cursor) {
            	// FIXME
            	return 2;
            }
            if(!cursor.moveToNext()) {
            	// FIXME
            	return 2;
            }
            return  Integer.parseInt(cursor.getString(0));
        } catch (Throwable t) {
        	Log.w("GoogleLocationSettingHelper", "Failed to get 'Use My Location' setting", t);
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
        return 2;
    }
}
