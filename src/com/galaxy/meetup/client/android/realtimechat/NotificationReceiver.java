/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.realtimechat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;

/**
 * 
 * @author sihai
 *
 */
public class NotificationReceiver extends BroadcastReceiver {

	public NotificationReceiver()
    {
    }

    public void onReceive(Context context, Intent intent)
    {
        RealTimeChatService.resetNotifications(context, (EsAccount)intent.getParcelableExtra("account"));
    }

}
