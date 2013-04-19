/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.galaxy.meetup.client.android.InstantUpload;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;

/**
 * 
 * @author sihai
 *
 */
public class PicasaQuotaChangedReceiver extends BroadcastReceiver {

	public void onReceive(final Context context, Intent intent) {
        final android.os.PowerManager.WakeLock wl = ((PowerManager)context.getSystemService("power")).newWakeLock(1, "Quota Changed");
        final int quotaLimit = intent.getIntExtra("quota_limit", -1);
        final int quotaUsed = intent.getIntExtra("quota_used", -1);
        final boolean picasaFullSizeDisabled = intent.getBooleanExtra("full_size_disabled", false);
        final EsAccount account = EsAccountsData.getActiveAccount(context);
        wl.acquire();
        (new Thread(new Runnable() {

            public final void run() {
            	try {
            		InstantUpload.showOutOfQuotaNotification(context, account, quotaLimit, quotaUsed, picasaFullSizeDisabled);
            	} finally {
            		wl.release();
            	}
            }
            
        })).start();
    }

}
