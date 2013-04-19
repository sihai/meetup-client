/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.util.Locale;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @author sihai
 *
 */
public class HelpUrl {

	public static Uri getHelpUrl(Context context, String s)
    {
        if(TextUtils.isEmpty(s))
            throw new IllegalArgumentException("getHelpUrl(): fromWhere must be non-empty");
        String s1 = "http://www.google.com/support/mobile/?hl=%locale%";
        if(s1.contains("%locale%"))
        {
            Locale locale = Locale.getDefault();
            s1 = s1.replace("%locale%", (new StringBuilder()).append(locale.getLanguage()).append("-").append(locale.getCountry().toLowerCase()).toString());
        }
        android.net.Uri.Builder builder = Uri.parse(s1).buildUpon();
        builder.appendQueryParameter("p", s);
        try
        {
            builder.appendQueryParameter("version", String.valueOf(context.getPackageManager().getPackageInfo(context.getApplicationInfo().packageName, 0).versionCode));
        }
        catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
        {
            Log.e("HelpUrl", (new StringBuilder("Error finding package ")).append(context.getApplicationInfo().packageName).toString());
        }
        return builder.build();
    }
}
