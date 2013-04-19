/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network.http;

import java.util.Locale;

import android.content.Context;
import android.os.Build;

import com.galaxy.meetup.client.android.ClientVersion;

/**
 * 
 * @author sihai
 *
 */
public class UserAgent {
	
	private static volatile String cachedValue = null;
	private static Object _cache_lock_ = new Object();
	
	public static String from(Context context) {
		if(null == cachedValue) {
			StringBuilder stringbuilder = new StringBuilder();
			stringbuilder.append(context.getPackageName());
			stringbuilder.append('/');
			stringbuilder.append(ClientVersion.from(context));
			stringbuilder.append(" (Linux; U; Android ");
			stringbuilder.append(Build.VERSION.RELEASE);
			stringbuilder.append("; ");
			stringbuilder.append(Locale.getDefault().toString());
			String s = Build.MODEL;
			if (s.length() > 0) {
				stringbuilder.append("; ");
				stringbuilder.append(s);
			}
			String s1 = Build.ID;
			if (s1.length() > 0) {
				stringbuilder.append("; Build/");
				stringbuilder.append(s1);
			}
			stringbuilder.append(')');
			
			synchronized(_cache_lock_) {
				if(null == cachedValue) {
					cachedValue = stringbuilder.toString();
				}
			}
		}
		
		return cachedValue;
	}
}
