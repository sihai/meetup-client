/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * @author sihai
 *
 */
public class AccountsChangedReceiver extends BroadcastReceiver {

	public AccountsChangedReceiver()
    {
    }

    public void onReceive(Context context, Intent intent)
    {
        EsService.accountsChanged(context);
    }
}
