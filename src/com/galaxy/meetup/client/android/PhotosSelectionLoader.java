/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.database.Cursor;

import com.galaxy.meetup.client.android.content.EsAccount;

/**
 * 
 * @author sihai
 *
 */
public class PhotosSelectionLoader extends PhotoCursorLoader {

	private List mMediaRefsToLoad;
	
	public PhotosSelectionLoader(Context context, EsAccount esaccount, String s, List list)
    {
        super(context, esaccount, s, null, null, "camerasync", null, null, false, 0, null);
        mMediaRefsToLoad = list;
    }

    public final Cursor esLoadInBackground()
    {
        setUri(getLoaderUri());
        if(mMediaRefsToLoad != null && !mMediaRefsToLoad.isEmpty())
        {
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append("( CASE photo_id ");
            StringBuilder stringbuilder1 = new StringBuilder();
            ArrayList arraylist = new ArrayList(2 * mMediaRefsToLoad.size());
            boolean flag = false;
            int i = 0;
            for(Iterator iterator = mMediaRefsToLoad.iterator(); iterator.hasNext();)
            {
                String s = String.valueOf(((MediaRef)iterator.next()).getPhotoId());
                if(flag)
                    stringbuilder1.append(" OR ");
                arraylist.add(s);
                stringbuilder1.append("(photo_id == ?)");
                flag = true;
                StringBuilder stringbuilder2 = (new StringBuilder(" WHEN '")).append(s).append("' THEN ");
                int j = i + 1;
                stringbuilder.append(stringbuilder2.append(i).toString());
                i = j;
            }

            stringbuilder.append(" END )");
            setSortOrder(stringbuilder.toString());
            setSelection(stringbuilder1.toString());
            setSelectionArgs((String[])arraylist.toArray(new String[arraylist.size()]));
        }
        setProjection(AlbumViewLoader.PhotoQuery.PROJECTION);
        return super.esLoadInBackground();
    }
}
