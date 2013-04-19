/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.widget.Toast;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.EsCursorLoader;

/**
 * 
 * @author sihai
 *
 */
public class ViewCircleActivity extends FragmentActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks {

	public ViewCircleActivity()
    {
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        Uri uri = getIntent().getData();
        if(uri == null)
            finish();
        Bundle bundle1 = new Bundle();
        bundle1.putParcelable("group_uri", uri);
        getSupportLoaderManager().initLoader(0, bundle1, this);
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        return new EsCursorLoader(this, (Uri)bundle.getParcelable("group_uri"), new String[] {
            "sourceid"
        }, null, null, null);
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        EsAccount esaccount = EsAccountsData.getActiveAccount(this);
        if(esaccount == null)
            finish();
        else
        if(cursor == null || !cursor.moveToFirst())
        {
            Toast.makeText(this, R.string.circle_does_not_exist, 0).show();
            finish();
        } else
        {
            String s = cursor.getString(0);
            if(TextUtils.isEmpty(s))
            {
                Toast.makeText(this, R.string.circle_does_not_exist, 0).show();
                finish();
            } else
            {
                Intent intent = Intents.getCirclePostsActivityIntent(this, esaccount, s);
                intent.addFlags(0x2000000);
                startActivity(intent);
                finish();
            }
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }
}
