/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * @author sihai
 *
 */
public class PicasaNetworkReceiver extends BroadcastReceiver {

	public PicasaNetworkReceiver()
    {
    }

    public void onReceive(Context context, Intent intent)
    {
        intent.setComponent(new ComponentName(context, PicasaNetworkService.class));
        context.startService(intent);
    }

}
