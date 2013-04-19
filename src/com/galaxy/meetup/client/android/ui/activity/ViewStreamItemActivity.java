/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.EsCursorLoader;

/**
 * 
 * @author sihai
 *
 */
public class ViewStreamItemActivity extends FragmentActivity implements
		LoaderCallbacks {

	private static final String STREAM_ITEMS_PROJECTION[] = {
        "stream_item_sync1"
    };
	
	public ViewStreamItemActivity()
    {
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        Uri uri = getIntent().getData();
        if(uri == null)
            finish();
        if(EsAccountsData.getActiveAccount(this) == null)
            finish();
        Bundle bundle1 = new Bundle();
        bundle1.putParcelable("stream_item_uri", uri);
        getSupportLoaderManager().initLoader(0, bundle1, this);
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        return new EsCursorLoader(this, (Uri)bundle.getParcelable("stream_item_uri"), STREAM_ITEMS_PROJECTION, null, null, null);
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        EsAccount esaccount = EsAccountsData.getActiveAccount(this);
        if(esaccount == null)
            finish();
        if(cursor != null && cursor.moveToFirst())
            startActivity(Intents.getPostCommentsActivityIntent(this, esaccount, cursor.getString(0)));
        finish();
    }

    public final void onLoaderReset(Loader loader)
    {
    }

}
