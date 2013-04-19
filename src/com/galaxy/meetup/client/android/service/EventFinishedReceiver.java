/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.InstantUpload;
import com.galaxy.meetup.client.android.content.EsEventData;

/**
 * 
 * @author sihai
 *
 */
public class EventFinishedReceiver extends BroadcastReceiver {

	public static final Intent sIntent = new Intent("com.galaxy.meetup.android.eventfinished");
	

	public EventFinishedReceiver()
    {
    }

    public void onReceive(final Context context, final Intent intent)
    {
        final android.os.PowerManager.WakeLock wakelock = ((PowerManager)context.getSystemService("power")).newWakeLock(1, "EventFinishedReceiver");
        wakelock.acquire();
        (new Thread(new Runnable() {

            public final void run()
            {
            	try {
	                if(TextUtils.equals(intent.getStringExtra("event_id"), InstantUpload.getInstantShareEventId(context)))
	                    EsEventData.disableInstantShare(context);
            	} finally {
            		wakelock.release();
            	}
                
            }

        })).start();
    }
}
