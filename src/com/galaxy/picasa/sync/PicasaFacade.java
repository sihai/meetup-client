/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import android.accounts.AccountManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.galaxy.meetup.client.util.AccountsUtil;

/**
 * 
 * @author sihai
 *
 */
public class PicasaFacade {

	private static PicasaFacade sInstance;
    private Uri mAlbumCoversUri;
    private Uri mAlbumsUri;
    private String mAuthority;
    private final Context mContext;
    private PicasaSyncInfo mLocalInfo;
    private PicasaSyncInfo mMasterInfo;
    private Uri mPhotosUri;
    private Uri mPostAlbumsUri;
    private Uri mPostPhotosUri;
    private Uri mSettingsUri;
    private Uri mSyncRequestUri;
    private Uri mUsersUri;
    
	private PicasaFacade(Context context) {
        mContext = context.getApplicationContext();
        PackageManager packagemanager = mContext.getPackageManager();
        ComponentName componentname = new ComponentName(mContext, PicasaContentProvider.class);
        if(packagemanager.getComponentEnabledSetting(componentname) != 0)
            packagemanager.setComponentEnabledSetting(componentname, 0, 1);
        ComponentName componentname1 = new ComponentName(mContext, PicasaSyncService.class);
        if(packagemanager.getComponentEnabledSetting(componentname1) != 0)
            packagemanager.setComponentEnabledSetting(componentname1, 0, 1);
        updatePicasaSyncInfo(true);
    }
	
	public static synchronized PicasaFacade get(Context context) {
		PicasaFacade sync;
        if(sInstance == null)
            sInstance = new PicasaFacade(context);
        sync = sInstance;
        return sync;
    }
	
	private synchronized void updatePicasaSyncInfo(boolean flag) {
        // TODO
    }
	
	private void updateSyncableState(boolean flag)
    {
        PackageManager packagemanager = mContext.getPackageManager();
        int i;
        ComponentName componentname;
        ComponentName componentname1;
        String s;
        AccountManager accountmanager;
        if(flag)
            i = 1;
        else
            i = 2;
        componentname = new ComponentName(mContext, ConnectivityReceiver.class);
        if(packagemanager.getComponentEnabledSetting(componentname) != i)
            packagemanager.setComponentEnabledSetting(componentname, i, 1);
        componentname1 = new ComponentName(mContext, BatteryReceiver.class);
        if(packagemanager.getComponentEnabledSetting(componentname1) != i)
            packagemanager.setComponentEnabledSetting(componentname1, i, 1);
        s = mLocalInfo.authority;
        accountmanager = AccountManager.get(mContext);
        if(flag)
        {
            android.accounts.Account aaccount1[] = accountmanager.getAccountsByType(AccountsUtil.ACCOUNT_TYPE);
            int l = aaccount1.length;
            for(int i1 = 0; i1 < l; i1++)
            {
                android.accounts.Account account1 = aaccount1[i1];
                if(ContentResolver.getIsSyncable(account1, s) == 0)
                {
                    ContentResolver.setIsSyncable(account1, s, -1);
                    ContentResolver.requestSync(account1, s, new Bundle());
                }
            }

        } else
        {
            android.accounts.Account aaccount[] = accountmanager.getAccountsByType(AccountsUtil.ACCOUNT_TYPE);
            int j = aaccount.length;
            for(int k = 0; k < j; k++)
            {
                android.accounts.Account account = aaccount[k];
                ContentResolver.setIsSyncable(account, s, 0);
                ContentResolver.cancelSync(account, s);
            }

        }
    }
	
	public final Uri getAlbumsUri()
    {
        return mAlbumsUri;
    }

    public final String getAuthority()
    {
        return mAuthority;
    }

    public final PicasaSyncInfo getMasterInfo()
    {
        return mMasterInfo;
    }

    public final Uri getPhotoUri(long l)
    {
        return mPhotosUri.buildUpon().appendPath(String.valueOf(l)).build();
    }

    public final Uri getPhotosUri()
    {
        return mPhotosUri;
    }

    public final Uri getSettingsUri()
    {
        return mSettingsUri;
    }

    public final Uri getSyncRequestUri()
    {
        return mSyncRequestUri;
    }

    public final Uri getUsersUri()
    {
        return mUsersUri;
    }

    public final boolean isMaster()
    {
        boolean flag;
        if(mMasterInfo == mLocalInfo)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void onMediaMounted()
    {
        PicasaSyncManager.get(mContext).updateTasks(0L);
    }

    public final void onPackageAdded$552c4e01()
    {
        updatePicasaSyncInfo(false);
    }

    public final void onPackageRemoved$552c4e01()
    {
        updatePicasaSyncInfo(false);
    }
	
	static final class PicasaSyncInfo {

        public final String authority;
        public boolean enableDownSync;
        public final String packageName;
        public final int priority;

        public PicasaSyncInfo(String s, String s1, int i, boolean flag) {
            packageName = s;
            authority = s1;
            priority = i;
            enableDownSync = flag;
        }
    }
}
