/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import java.io.IOException;

import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;

import com.android.gallery3d.common.Utils;

/**
 * 
 * @author sihai
 *
 */
public abstract class SyncTask {

	 protected int mPriority;
	 public final String syncAccount;
	 
	protected SyncTask(String s) {
		syncAccount = (String) Utils.checkNotNull(s);
	}

	protected static boolean isSyncOnBattery(Context context) {
		return queryBooleanSetting(context, "sync_on_battery", true);
	}

	protected static boolean isSyncOnRoaming(Context context) {
		return queryBooleanSetting(context, "sync_on_roaming", false);
	}

	protected static boolean isSyncPicasaOnWifiOnly(Context context) {
		return queryBooleanSetting(context, "sync_picasa_on_wifi_only", true);
	}

	private static boolean queryBooleanSetting(Context context, String s,
			boolean flag) {
		Cursor cursor = null;
		int i = 0;
		try {
			cursor = context.getContentResolver().query(
					PicasaFacade.get(context).getSettingsUri(),
					new String[] { s }, null, null, null);
			if (cursor.moveToNext())
				i = cursor.getInt(0);
			if (i != 0)
				flag = true;
			else
				flag = false;
			return flag;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public abstract void cancelSync();

	public abstract boolean isBackgroundSync();

	public abstract boolean isSyncOnBattery();

	public boolean isSyncOnExternalStorageOnly() {
		return false;
	}

	public boolean isSyncOnRoaming() {
		return true;
	}

	public abstract boolean isSyncOnWifiOnly();

	public abstract void performSync(SyncResult syncresult) throws IOException;

	public String toString() {
		Object aobj[] = new Object[2];
		aobj[0] = getClass().getSimpleName();
		aobj[1] = Utils.maskDebugInfo(syncAccount);
		return String.format("%s (%s)", aobj);
	}
}
