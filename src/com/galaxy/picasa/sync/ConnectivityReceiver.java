/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * @author sihai
 *
 */
public class ConnectivityReceiver extends BroadcastReceiver {

	public ConnectivityReceiver() {
    }

	@Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, AsyncService.class));
    }
    
	public static class AsyncService extends IntentService {

        protected void onHandleIntent(Intent intent) {
            if(PicasaFacade.get(this).isMaster())
                PicasaSyncManager.get(this).onEnvironmentChanged();
        }

        public AsyncService() {
            super("PicasaSyncConnectivityAsync");
        }
    }
}
