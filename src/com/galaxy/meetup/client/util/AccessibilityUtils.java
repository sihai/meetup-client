/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.content.Context;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;

/**
 * 
 * @author sihai
 *
 */
public class AccessibilityUtils {
	
	public static void appendAndSeparateIfNotEmpty(StringBuilder stringbuilder, CharSequence charsequence)
    {
        if(!TextUtils.isEmpty(charsequence))
            stringbuilder.append(charsequence).append(". ");
    }

    public static boolean isAccessibilityEnabled(Context context)
    {
        boolean flag;
        if(context != null)
            flag = AccessibilityManagerCompat.isTouchExplorationEnabled((AccessibilityManager)context.getSystemService("accessibility"));
        else
            flag = false;
        return flag;
    }
}
