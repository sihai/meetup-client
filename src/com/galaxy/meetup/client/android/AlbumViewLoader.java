/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.database.Cursor;

import com.galaxy.meetup.client.android.content.EsAccount;

/**
 * 
 * @author sihai
 *
 */
public class AlbumViewLoader extends PhotoCursorLoader {

	private static final String COUNT_PROJECTION[] = {
        "_count"
    };
    private int mExcludedCount;
    private MediaRef mExcludedMedia[];
    
    public AlbumViewLoader(Context context, EsAccount esaccount, String s, String s1, String s2, String s3, String s4, 
            String s5, MediaRef amediaref[])
    {
        super(context, esaccount, s, s1, s2, s3, s4, null, true, 2, s5);
        mExcludedMedia = amediaref;
    }

    public final Cursor esLoadInBackground() {
        // TODO
    	return null;
    }

    public final int getExcludedCount()
    {
        return mExcludedCount;
    }
    
    public static interface PhotoQuery
    {

        public static final String PROJECTION[] = {
            "_id", "action_state", "comment_count", "plusone_by_me", "plusone_count", "owner_id", "fingerprint", "title", "photo_id", "url", 
            "pending_status", "video_data", "is_panorama", "timestamp", "width"
        };

    }
}
