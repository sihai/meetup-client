/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;

import com.galaxy.meetup.client.android.hangout.RectangleDimensions;

/**
 * 
 * @author sihai
 *
 */
public class Utils {

	private static boolean debuggable;
    private static String version;
    private static String versionName;
    
	public static RectangleDimensions fitContentInContainer(double d, int i, int j)
    {
        int k;
        int l;
        if(d < (double)i / (double)j)
        {
            k = (int)(d * (double)j);
            l = j;
        } else
        {
            k = i;
            l = (int)((double)i * Math.pow(d, -1D));
        }
        if(l <= 0)
            l = 1;
        return new RectangleDimensions(k, l);
    }

    public static String getVersion()
    {
        return version;
    }

    public static void initialize(Context context)
    {
        PackageManager packagemanager;
        String s;
        packagemanager = context.getPackageManager();
        s = context.getPackageName();
        try {
	        boolean flag = true;
	        if((2 & packagemanager.getApplicationInfo(s, 128).flags) == 0) {
	        	flag = false;
	        }
	        debuggable = flag;
	        PackageInfo packageinfo = packagemanager.getPackageInfo(s, 0);
	        versionName = packageinfo.versionName;
	        version = (new StringBuilder()).append(packageinfo.versionName).append("-").append(packageinfo.versionCode).toString();
        } catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception) {
        	debuggable = false;
            versionName = "Error reading version";
        }
    }

    public static boolean isAppInstalled(String s, Context context)
    {
        boolean flag = true;
        PackageManager packagemanager = context.getPackageManager();
        try
        {
            packagemanager.getPackageInfo(s, 1);
        }
        catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
        {
            flag = false;
        }
        return flag;
    }

    public static boolean isOnMainThread(Context context)
    {
        Looper looper = Looper.myLooper();
        boolean flag;
        if(looper != null && looper == context.getMainLooper())
            flag = true;
        else
            flag = false;
        return flag;
    }

}
