/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class NotificationUtils {

	private static final int INSTANT_SHARE_NOTIFICATION_ID;

    static 
    {
        INSTANT_SHARE_NOTIFICATION_ID = R.id.event_instant_share_notification;
    }
    
	public static void cancelInstantShareEnabled(Context context)
    {
        ((NotificationManager)context.getSystemService("notification")).cancel("InstantShare", INSTANT_SHARE_NOTIFICATION_ID);
    }

    public static void notifyInstantShareEnabled(Context context, CharSequence charsequence, PendingIntent pendingintent)
    {
        notifyInstantShareEnabled(context, charsequence, pendingintent, true);
    }

    public static void notifyInstantShareEnabled(Context context, CharSequence charsequence, PendingIntent pendingintent, boolean flag)
    {
        String s = context.getString(R.string.event_instant_share_enabled_notification_subtitle);
        String s1;
        Notification notification;
        if(flag)
            s1 = s;
        else
            s1 = null;
        notification = new Notification(R.drawable.ic_stat_instant_share, s1, System.currentTimeMillis());
        notification.setLatestEventInfo(context, charsequence, s, pendingintent);
        notification.flags = 34;
        ((NotificationManager)context.getSystemService("notification")).notify("InstantShare", INSTANT_SHARE_NOTIFICATION_ID, notification);
    }

}