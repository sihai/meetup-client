/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class AndroidContactsNotificationService extends IntentService {

	public AndroidContactsNotificationService()
    {
        super("ContactsNotificationSvc");
    }

    protected void onHandleIntent(Intent intent)
    {
        android.net.Uri uri = intent.getData();
        if(EsLog.isLoggable("ContactsNotificationSvc", 4))
            Log.i("ContactsNotificationSvc", (new StringBuilder("Contact opened in Contacts: ")).append(uri).toString());
        AndroidContactsSync.syncRawContact(this, uri);
    }

}
