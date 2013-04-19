/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.MenuItem;

import com.galaxy.meetup.client.android.EsAsyncTaskLoader;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.api.ResolveVanityIdOperation;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.ui.fragments.DesktopActivityIdLoader;
import com.galaxy.meetup.client.android.ui.fragments.ProfileLoader;
import com.galaxy.meetup.server.client.domain.DataCirclePerson;

/**
 * 
 * @author sihai
 *
 */
public class UrlGatewayLoaderActivity extends EsUrlGatewayActivity {

	private final android.support.v4.app.LoaderManager.LoaderCallbacks mDesktopActivityIdLoaderCallbacks = new android.support.v4.app.LoaderManager.LoaderCallbacks() {

        public final Loader onCreateLoader(int i, Bundle bundle)
        {
            Object obj;
            if(mGaiaId == null)
                obj = null;
            else
                obj = new DesktopActivityIdLoader(UrlGatewayLoaderActivity.this, mAccount, mDesktopActivityId, mGaiaId);
            return ((Loader) (obj));
        }

        public final void onLoadFinished(Loader loader, Object obj)
        {
            Cursor cursor = (Cursor)obj;
            if(cursor != null && cursor.moveToFirst())
                mActivityId = cursor.getString(0);
            (new Handler()).post(new Runnable() {

                public final void run()
                {
                	// TODO
                	//destroyLoader();
                }

            });
        }

        public final void onLoaderReset(Loader loader)
        {
        }

    };
    private final android.support.v4.app.LoaderManager.LoaderCallbacks mProfileLoaderCallbacks = new android.support.v4.app.LoaderManager.LoaderCallbacks() {

        public final Loader onCreateLoader(int i, Bundle bundle)
        {
            Object obj;
            if(mProfileId.startsWith("+"))
                obj = new VanityUrlLoader(UrlGatewayLoaderActivity.this, mAccount, mProfileId);
            else
                obj = new ProfileLoader(UrlGatewayLoaderActivity.this, mAccount, (new StringBuilder("g:")).append(mProfileId).toString(), false);
            return ((Loader) (obj));
        }

        public final void onLoadFinished(Loader loader, Object obj)
        {
            EsPeopleData.ProfileAndContactData profileandcontactdata = (EsPeopleData.ProfileAndContactData)obj;
            if(profileandcontactdata != null)
            {
                UrlGatewayLoaderActivity urlgatewayloaderactivity = UrlGatewayLoaderActivity.this;
                boolean flag;
                if(profileandcontactdata.profileState != 0)
                    flag = true;
                else
                    flag = false;
                urlgatewayloaderactivity.mProfileIdValidated = flag;
                mName = profileandcontactdata.displayName;
                mGaiaId = profileandcontactdata.gaiaId;
            }
            (new Handler()).post(new Runnable() {

                public final void run()
                {
                	// TODO
                    //destroyLoader();
                }

            });
        }

        public final void onLoaderReset(Loader loader)
        {
        }

    };
    
    public UrlGatewayLoaderActivity()
    {
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(isFinishing()) {
        	return;
        }

        setContentView(R.layout.url_gateway_loader_activity);
        findViewById(R.id.list_empty_progress).setVisibility(0);
        LoaderManager loadermanager = getSupportLoaderManager();
        if(mDesktopActivityId != null && mActivityId == null)
        {
            if(mGaiaId == null && mProfileId.startsWith("+"))
            {
                loadermanager.initLoader(0, null, mProfileLoaderCallbacks);
            } else
            {
                mGaiaId = mProfileId;
                loadermanager.initLoader(1, null, mDesktopActivityIdLoaderCallbacks);
            }
        } else
        if(mProfileId != null)
            loadermanager.initLoader(0, null, mProfileLoaderCallbacks);
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag;
        if(menuitem.getItemId() == 0x102002c)
        {
            goHome(mAccount);
            flag = true;
        } else
        {
            flag = super.onOptionsItemSelected(menuitem);
        }
        return flag;
    }

    protected final void onTitlebarLabelClick()
    {
        goHome(mAccount);
    }
    
    public static final class VanityUrlLoader extends EsAsyncTaskLoader {

    	private final EsAccount mAccount;
        private EsPeopleData.ProfileAndContactData mData;
        private final String mVanityId;

        public VanityUrlLoader(Context context, EsAccount esaccount, String s)
        {
            super(context);
            mAccount = esaccount;
            mVanityId = s;
        }
        
        public final void deliverResult(Object obj)
        {
            EsPeopleData.ProfileAndContactData profileandcontactdata = (EsPeopleData.ProfileAndContactData)obj;
            if(!isReset())
            {
                mData = profileandcontactdata;
                if(isStarted())
                    super.deliverResult(profileandcontactdata);
            }
        }

        public final Object esLoadInBackground()
        {
            EsPeopleData.ProfileAndContactData profileandcontactdata = null;
            ResolveVanityIdOperation resolvevanityidoperation = new ResolveVanityIdOperation(getContext(), mAccount, mVanityId, null, null);
            resolvevanityidoperation.start();
            if(!resolvevanityidoperation.hasError()) {
            	DataCirclePerson datacircleperson = resolvevanityidoperation.getPerson();
                profileandcontactdata = null;
                if(datacircleperson != null)
                {
                    profileandcontactdata = new EsPeopleData.ProfileAndContactData();
                    profileandcontactdata.gaiaId = datacircleperson.memberId.obfuscatedGaiaId;
                    profileandcontactdata.displayName = datacircleperson.memberProperties.displayName;
                    if(!TextUtils.isEmpty(profileandcontactdata.gaiaId))
                        profileandcontactdata.profileState = 2;
                } 
            } else { 
            	resolvevanityidoperation.logError("VanityUrlLoader");
            }
            return profileandcontactdata;
        }

        protected final void onStartLoading()
        {
            if(mData == null)
                forceLoad();
        }
    }
}
