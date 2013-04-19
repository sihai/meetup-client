/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.util.PlayStoreInstaller;

/**
 * 
 * @author sihai
 *
 */
public class PackageAddedReceiver extends BroadcastReceiver {

	public PackageAddedReceiver()
    {
    }

    public void onReceive(Context context, Intent intent)
    {
        if(Log.isLoggable("DeepLinking", 3))
            Log.d("DeepLinking", (new StringBuilder("PackageAddedReceiver.onReceive() ")).append(intent).toString());
        String s = intent.getDataString();
        if(s != null)
        {
            String s1 = s.substring(8);
            if(PlayStoreInstaller.isPackageInstalled(context.getPackageManager(), s1))
            {
                EsAccount esaccount = EsAccountsData.getActiveAccount(context);
                if(esaccount != null)
                    EsService.notifyDeepLinkingInstall(context, esaccount, s1);
            }
        }
    }
}
