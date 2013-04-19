/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import java.util.Collection;

/**
 * 
 * @author sihai
 * 
 */
public interface SyncTaskProvider {

	void collectTasks(Collection<SyncTask> syncTasks);

	void resetSyncStates();
}
