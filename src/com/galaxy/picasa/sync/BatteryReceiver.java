/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * @author sihai
 *
 */
public class BatteryReceiver extends BroadcastReceiver {

	public BatteryReceiver()
    {
    }

    public void onReceive(Context context, Intent intent)
    {
        if(PicasaFacade.get(context).isMaster())
        {
            boolean flag = "android.intent.action.ACTION_POWER_CONNECTED".equals(intent.getAction());
            PicasaSyncManager.get(context).onBatteryStateChanged(flag);
        }
    }

}
