/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.util.Iterator;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class PlayStoreInstaller {

	private static final Uri PLAY_STORE_TEST_URI = Uri.parse("market://search?q=com.android.youtube");
	
	private static boolean canResolveIntent(PackageManager packagemanager, Intent intent) {
		if(null == intent) {
			return false;
		}
		
		List list = packagemanager.queryIntentActivities(intent, 0);
		return null != list && !list.isEmpty();
    }

    public static Intent getContinueIntent(PackageManager packagemanager, String s, String s1, String s2)
    {
        Intent intent = new Intent("com.google.android.apps.plus.VIEW_DEEP_LINK");
        intent.setPackage(s);
        intent.setData(Uri.parse("vnd.google.deeplink://link/").buildUpon().appendQueryParameter("deep_link_id", s1).appendQueryParameter("gplus_source", s2).build());
        intent.addFlags(0x14000000);
        if(!canResolveIntent(packagemanager, intent))
            intent = null;
        return intent;
    }

    public static Intent getInstallIntent(String s)
    {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(String.format("market://details?id=%1$s", new Object[] {
            s
        })));
        intent.putExtra("use_direct_purchase", true);
        intent.addFlags(0x80000);
        return intent;
    }

    public static boolean isPackageInstalled(PackageManager packagemanager, String s) {
        for(Iterator iterator = packagemanager.getInstalledApplications(128).iterator(); iterator.hasNext();) {
        	if(((ApplicationInfo)iterator.next()).packageName.equals(s)) {
        		return true;
        	}
        }
        return false;
    }

    public static boolean isPlayStoreInstalled(PackageManager packagemanager) {
    	
    	try {
    		ApplicationInfo applicationinfo = packagemanager.getApplicationInfo("com.android.vending", 0);
    		return null != applicationinfo && canResolveIntent(packagemanager, new Intent("android.intent.action.VIEW", PLAY_STORE_TEST_URI));
    	} catch (android.content.pm.PackageManager.NameNotFoundException namenotfoundexception) {
    		if(EsLog.isLoggable("DeepLinking", 3)) {
    			Log.d("DeepLinking", (new StringBuilder("com.android.vending not found: ")).append(namenotfoundexception.getMessage()).toString());
    		}
    		return false;
    	}
    }

    public static void notifyCompletedInstall(Context context, String s, String s1, String s2, String s3, String s4)
    {
    	int i;
        long l;
        String s5;
        Notification notification;
        PendingIntent pendingintent;
        NotificationManager notificationmanager;
        Object aobj[];
        PackageManager packagemanager = context.getPackageManager();
        Intent intent = getContinueIntent(packagemanager, s2, s3, s4);
        if(intent == null)
        {
            intent = packagemanager.getLaunchIntentForPackage(s2);
            intent.addFlags(0x14000000);
            if(!canResolveIntent(packagemanager, intent))
                intent = null;
            if(Log.isLoggable("DeepLinking", 3))
                Log.d("DeepLinking", (new StringBuilder("Could not resolve continue Intent for ")).append(s2).append(" falling back to launch ").append(intent).toString());
        }
        i = (int)System.currentTimeMillis();
        l = System.currentTimeMillis();
        s5 = context.getString(R.string.source_app_installed_notification, new Object[] {
            s1, s
        });
        notification = new Notification(R.drawable.ic_stat_gplus, s5, l);
        pendingintent = PendingIntent.getActivity(context, i, intent, 0);
        notification.setLatestEventInfo(context, context.getString(R.string.app_name), s5, pendingintent);
        notification.flags = 0x10 | notification.flags;
        notification.defaults = 4 | notification.defaults;
        notificationmanager = (NotificationManager)context.getSystemService("notification");
        aobj = new Object[2];
        aobj[0] = context.getPackageName();
        aobj[1] = s2;
        notificationmanager.notify(String.format("%s:notifications:%s", aobj), 1000, notification);
    }
}
