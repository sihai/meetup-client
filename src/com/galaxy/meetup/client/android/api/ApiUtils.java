/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import android.text.Spannable;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.content.AudienceData;

/**
 * 
 * @author sihai
 *
 */
public class ApiUtils {

	private static final String CIRCLE_ID_PREFIXES[] = {
        "f.", "g."
    };
    public static final int EMAIL_MENTION_PREFIX_LENGTH = 2;
    public static final int ID_MENTION_PREFIX_LENGTH = 2;
    
    public static String buildPostableString(Spannable spannable)
    {
    	// TODO
    	return "";
    }
    
    public static String prependProtocol(String s)
    {
        if(!TextUtils.isEmpty(s) && !s.startsWith("http:") && !s.startsWith("https:"))
            s = (new StringBuilder("https:")).append(s).toString();
        return s;
    }
    
    public static AudienceData removeCircleIdNamespaces(AudienceData audiencedata)
    {
    	// TODO
    	return null;
    }
}
