/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import java.util.HashSet;
import java.util.Set;

import com.android.gallery3d.common.Utils;

/**
 * 同步锁管理器
 * @author sihai
 *
 */
public class SyncLockManager {

	private final Set<SyncLock> mLocks = new HashSet<SyncLock>();
	
	SyncLockManager() {
	}

	public final SyncLock acquireLock(int type, Object key)
			throws InterruptedException {
		SyncLock synclock = null;
		synchronized (mLocks) {
			for (synclock = new SyncLock(type, key); !mLocks.add(synclock); mLocks.wait())
				;
		}
		return synclock;
	}
    
	public final class SyncLock {

		private Object mKey;
		private int mType;

		SyncLock(int type, Object key) {
			super();
			mType = type;
			mKey = key;
		}

		public final boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (null == obj) {
				return false;
			}
			if (!(obj instanceof SyncLock)) {
				return false;
			}
			SyncLock synclock = (SyncLock) obj;
			if (mType != synclock.mType || !mKey.equals(synclock.mKey)) {
				return false;
			}
			return true;
		}

		public final int hashCode() {
			return mType ^ mKey.hashCode();
		}

		public final void unlock() {
			synchronized (mLocks) {
				Utils.assertTrue(mLocks.remove(this));
				mLocks.notifyAll();
			}
		}
	}
}
