/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import android.content.Context;
import android.database.Cursor;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.EsProvider;

/**
 * 
 * @author sihai
 *
 */
public class RecentImagesLoader extends EsCursorLoader {

	public RecentImagesLoader(Context context, EsAccount esaccount)
    {
        super(context);
        android.net.Uri.Builder builder = EsProvider.PHOTO_BY_STREAM_ID_AND_OWNER_ID_URI.buildUpon().appendPath("camerasync").appendPath(esaccount.getGaiaId());
        EsProvider.appendAccountParameter(builder, esaccount);
        setUri(builder.build());
        setProjection(RecentImagesQuery.PROJECTION);
        setSelectionArgs(null);
        setSortOrder("timestamp DESC LIMIT 10");
    }

    public final Cursor esLoadInBackground()
    {
        long l = EsAccountsData.loadRecentImagesTimestamp(getContext());
        long l1 = System.currentTimeMillis();
        long l2;
        if(l1 - l >= 0x5265c00L)
            l2 = Math.max(l1 - 0x19bfcc00L, l);
        else
            l2 = 0x7fffffffffffffffL;
        setSelection((new StringBuilder("timestamp > ")).append(l2).toString());
        return super.esLoadInBackground();
    }
    
    public static interface RecentImagesQuery
    {

        public static final String PROJECTION[] = {
            "photo_id", "url", "timestamp", "video_data"
        };

    }
}
