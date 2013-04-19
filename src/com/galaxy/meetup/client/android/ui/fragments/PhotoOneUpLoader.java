/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.EsMatrixCursor;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.util.MediaStoreUtils;
import com.galaxy.meetup.server.client.domain.DataVideo;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class PhotoOneUpLoader extends CursorLoader {

	private final EsAccount mAccount;
    private final boolean mDisableComments;
    private final android.support.v4.content.Loader.ForceLoadContentObserver mObserver = new android.support.v4.content.Loader.ForceLoadContentObserver();
    private boolean mObserverRegistered;
    private final String mOwnerId;
    private final long mPhotoId;
    private final String mPhotoUrl;
    
    public PhotoOneUpLoader(Context context, EsAccount esaccount, long l, String s, String s1, boolean flag)
    {
        super(context);
        mAccount = esaccount;
        mPhotoId = l;
        mPhotoUrl = s1;
        mOwnerId = s;
        mDisableComments = flag;
    }

    public final Cursor loadInBackground()
    {
    	MatrixCursor matrixcursor;
        MergeCursor mergecursor;
        ContentResolver contentresolver = getContext().getContentResolver();
        Object obj;
        boolean flag;
        Cursor cursor;
        Cursor cursor1;
        EsMatrixCursor esmatrixcursor;
        if(mPhotoId != 0L)
        {
            obj = contentresolver.query(EsProvider.appendAccountParameter(ContentUris.withAppendedId(EsProvider.PHOTO_BY_PHOTO_ID_URI, mPhotoId), mAccount), PhotoQuery.PROJECTION, null, null, null);
        } else
        {
            String as[] = PhotoQuery.PROJECTION;
            obj = new EsMatrixCursor(as);
            DataVideo datavideo = MediaStoreUtils.toVideoData(getContext(), Uri.parse(mPhotoUrl));
            byte abyte0[];
            String s;
            Object obj1;
            String s1;
            Object aobj[];
            if(datavideo != null)
                abyte0 = JsonUtil.toByteArray(datavideo);
            else
                abyte0 = null;
            if(TextUtils.isEmpty(mOwnerId))
                s = mAccount.getGaiaId();
            else
                s = mOwnerId;
            if(s != null)
                obj1 = null;
            else
                obj1 = Integer.valueOf(1);
            s1 = EsPeopleData.getUserName(getContext(), mAccount, s);
            aobj = new Object[PhotoQuery.PROJECTION.length];
            aobj[0] = Long.valueOf(0L);
            aobj[1] = Integer.valueOf(0);
            aobj[2] = Long.valueOf(0L);
            aobj[3] = s;
            aobj[4] = s1;
            aobj[6] = Long.valueOf(0L);
            aobj[7] = null;
            aobj[8] = null;
            aobj[9] = mPhotoUrl;
            aobj[10] = Integer.valueOf(0);
            aobj[11] = null;
            aobj[12] = Integer.valueOf(0);
            aobj[13] = null;
            aobj[14] = abyte0;
            aobj[15] = Integer.valueOf(0);
            aobj[16] = Integer.valueOf(0);
            aobj[17] = "ORIGINAL";
            aobj[18] = obj1;
            aobj[19] = null;
            aobj[20] = null;
            ((EsMatrixCursor)obj).addRow(aobj);
        }
        flag = mDisableComments;
        cursor = null;
        cursor1 = null;
        esmatrixcursor = null;
        if(!flag)
        {
            int i;
            Uri uri;
            Cursor cursor2;
            int j;
            if(obj != null && ((Cursor) (obj)).moveToFirst())
            {
                i = ((Cursor) (obj)).getInt(12);
                ((Cursor) (obj)).moveToPosition(-1);
            } else
            {
                i = 0;
            }
            uri = EsProvider.appendAccountParameter(ContentUris.withAppendedId(EsProvider.PHOTO_COMMENTS_BY_PHOTO_ID_URI, mPhotoId), mAccount);
            cursor2 = contentresolver.query(uri, PhotoCommentCountQuery.PROJECTION, null, null, null);
            if(cursor2 != null && cursor2.moveToFirst() && cursor2.getInt(2) > 0)
            {
                cursor = cursor2;
            } else
            {
                cursor = null;
                if(cursor2 != null)
                {
                    cursor2.close();
                    cursor = null;
                }
            }
            cursor1 = contentresolver.query(uri, PhotoCommentQuery.PROJECTION, null, null, "create_time");
            if(cursor1 != null)
                j = cursor1.getCount();
            else
                j = 0;
            esmatrixcursor = null;
            if(i != j)
            {
                esmatrixcursor = new EsMatrixCursor(PhotoCommentLoadingQuery.PROJECTION);
                ((EsMatrixCursor)esmatrixcursor).newRow().add(Integer.valueOf(0x7ffffffd)).add(Integer.valueOf(3));
            }
        }
        matrixcursor = new MatrixCursor(LeftoverQuery.PROJECTION);
        matrixcursor.newRow().add(Integer.valueOf(0x7ffffffc)).add(Integer.valueOf(5));
        mergecursor = new MergeCursor(new Cursor[] {
            (Cursor)obj, cursor, cursor1, esmatrixcursor, matrixcursor
        });
        return mergecursor;
    }

    protected final void onAbandon()
    {
        if(mObserverRegistered)
        {
            getContext().getContentResolver().unregisterContentObserver(mObserver);
            mObserverRegistered = false;
        }
    }

    protected final void onReset()
    {
        cancelLoad();
        super.onReset();
        onAbandon();
    }

    protected final void onStartLoading()
    {
        super.onStartLoading();
        if(!mObserverRegistered)
        {
            ContentResolver contentresolver = getContext().getContentResolver();
            Uri uri = ContentUris.withAppendedId(EsProvider.PHOTO_COMMENTS_BY_PHOTO_ID_URI, mPhotoId);
            contentresolver.registerContentObserver(ContentUris.withAppendedId(EsProvider.PHOTO_BY_PHOTO_ID_URI, mPhotoId), false, mObserver);
            contentresolver.registerContentObserver(uri, false, mObserver);
            mObserverRegistered = true;
        }
    }

    protected final void onStopLoading()
    {
    }
    
	public static interface LeftoverQuery
    {

        public static final String PROJECTION[] = {
            "_id", "5 AS row_type"
        };

    }

    public static interface PhotoCommentCountQuery
    {

        public static final String PROJECTION[] = {
            "2147483646 AS _id", "4 AS row_type", "COUNT(*) AS _count"
        };

    }

    public static interface PhotoCommentLoadingQuery
    {

        public static final String PROJECTION[] = {
            "_id", "4 AS row_type"
        };

    }

    public static interface PhotoCommentQuery
    {

        public static final String PROJECTION[] = {
            "_id", "1 AS row_type", "comment_id", "author_id", "owner_name", "avatar", "create_time", "truncated", "content", "plusone_data"
        };

    }

    public static interface PhotoQuery
    {

        public static final String PROJECTION[] = {
            "_id", "0 AS row_type", "photo_id", "owner_id", "owner_name", "owner_avatar_url", "album_id", "album_name", "album_stream", "url", 
            "action_state", "timestamp", "comment_count", "pending_status", "video_data", "plusone_by_me", "plusone_count", "upload_status", "downloadable", "description", 
            "plusone_data"
        };

    }
}
