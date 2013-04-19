/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.fragments.LoadingFragment;
import com.galaxy.meetup.client.android.ui.fragments.PhotoOneUpFragment;

/**
 * 
 * @author sihai
 *
 */
public class PhotoPagerAdapter extends EsCursorPagerAdapter {

	final EsAccount mAccount;
    final boolean mAllowPlusOne;
    private final String mDefaultAlbumName;
    final boolean mDisableComments;
    final String mEventId;
    final Long mForceLoadId;
    private Pageable mPageable;
    final String mStreamId;
    
    public PhotoPagerAdapter(Context context, FragmentManager fragmentmanager, Cursor cursor, EsAccount esaccount, Long long1, String s, String s1, 
            String s2, boolean flag, boolean flag1)
    {
        super(context, fragmentmanager, null);
        mAccount = esaccount;
        mForceLoadId = long1;
        mStreamId = s;
        mEventId = s1;
        mDefaultAlbumName = s2;
        mAllowPlusOne = flag;
        mDisableComments = flag1;
    }

    public final int getCount()
    {
        int i;
        if(mPageable != null && mPageable.hasMore())
            i = 1 + super.getCount();
        else
            i = super.getCount();
        return i;
    }

    public final Fragment getItem(int i)
    {
        Cursor cursor;
        Object obj;
        if(isDataValid())
            cursor = getCursor();
        else
            cursor = null;
        if(cursor != null && (cursor.isClosed() || i >= cursor.getCount()))
        {
            mPageable.loadMore();
            obj = new LoadingFragment();
        } else
        {
            obj = super.getItem(i);
        }
        return ((Fragment) (obj));
    }

    public final Fragment getItem(Cursor cursor)
    {
        int i = 1;
        long l = cursor.getLong(i);
        String s = cursor.getString(2);
        String s1 = cursor.getString(3);
        String s2 = cursor.getString(4);
        boolean flag;
        Intents.PhotoViewIntentBuilder photoviewintentbuilder;
        Intents.PhotoViewIntentBuilder photoviewintentbuilder1;
        MediaRef.MediaType mediatype;
        PhotoOneUpFragment photooneupfragment;
        if(cursor.getInt(6) == 0)
            i = 0;
        flag = "PLACEHOLDER".equals(cursor.getString(8));
        photoviewintentbuilder = Intents.newPhotoViewFragmentIntentBuilder(mContext);
        photoviewintentbuilder1 = photoviewintentbuilder.setAccount(mAccount).setPhotoId(Long.valueOf(l)).setGaiaId(s1).setPhotoUrl(s);
        if(i != 0)
            mediatype = MediaRef.MediaType.PANORAMA;
        else
            mediatype = MediaRef.MediaType.IMAGE;
        photoviewintentbuilder1.setMediaType(mediatype).setDisplayName(s2).setAlbumName(mDefaultAlbumName).setStreamId(mStreamId).setEventId(mEventId).setAllowPlusOne(Boolean.valueOf(mAllowPlusOne)).setForceLoadId(mForceLoadId).setDisableComments(Boolean.valueOf(mDisableComments)).setIsPlaceholder(Boolean.valueOf(flag));
        photooneupfragment = new PhotoOneUpFragment();
        photooneupfragment.setArguments(photoviewintentbuilder.build().getExtras());
        return photooneupfragment;
    }

    public final void setPageable(Pageable pageable)
    {
        mPageable = pageable;
    }
}
