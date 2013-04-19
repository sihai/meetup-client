/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.iu;

import com.galaxy.picasa.sync.SyncTask;

/**
 * 
 * @author sihai
 *
 */
public interface SyncTaskProvider {

	SyncTask getNextTask(String s);

    void onSyncStart();
}
