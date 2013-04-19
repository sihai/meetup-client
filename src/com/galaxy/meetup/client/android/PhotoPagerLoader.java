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
public class PhotoPagerLoader extends PhotoCursorLoader {

	private final MediaRef mMediaRefs[];
    private final String mOwnerGaiaId;
    private final String mPhotoUrl;
    
    public PhotoPagerLoader(Context context, EsAccount esaccount, String s, MediaRef amediaref[], String s1, String s2, String s3, 
            String s4, String s5, int i, String s6)
    {
    	super(context, esaccount, s, s1, s2, s3, s4, s5, i != -1 ? true : false, i, s6);
        mOwnerGaiaId = s;
        mMediaRefs = amediaref;
        mPhotoUrl = s5;
    }

    public final Cursor esLoadInBackground()
    {
        int i = 0;
        Object obj;
        if(mMediaRefs != null)
        {
            MediaRef amediaref[] = mMediaRefs;
            obj = new EsMatrixCursor(PhotoQuery.PROJECTION);
            while(i < amediaref.length) 
            {
                if(amediaref[i].getLocalUri() != null)
                    ((EsMatrixCursor) (obj)).newRow().add(Integer.valueOf(i)).add(null).add(amediaref[i].getLocalUri()).add(amediaref[i].getOwnerGaiaId());
                else
                    ((EsMatrixCursor) (obj)).newRow().add(Integer.valueOf(i)).add(Long.valueOf(amediaref[i].getPhotoId())).add(amediaref[i].getUrl()).add(amediaref[i].getOwnerGaiaId());
                i++;
            }
        } else
        if(mPhotoUrl != null)
        {
            String s = mPhotoUrl;
            obj = new EsMatrixCursor(PhotoQuery.PROJECTION);
            ((EsMatrixCursor) (obj)).newRow().add(Integer.valueOf(0)).add(null).add(s).add(mOwnerGaiaId);
        } else
        {
            setUri(getLoaderUri());
            setProjection(PhotoQuery.PROJECTION);
            obj = super.esLoadInBackground();
        }
        return ((Cursor) (obj));
    }
    
    public static interface PhotoQuery
    {

        public static final String PROJECTION[] = {
            "_id", "photo_id", "url", "owner_id", "title", "video_data", "is_panorama", "album_name", "upload_status"
        };

    }
}
