/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.util.HashSet;
import java.util.Set;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.galaxy.meetup.client.android.ui.view.AlbumColumnGridItemView;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.ImageResourceView;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class AlbumGridViewAdapter extends EsCursorAdapter {

	private static StateFilter sDefaultFilter = new StateFilter() {

        public final int getState(int i)
        {
            return 0;
        }

    };
    private final String mAlbumType;
    private final android.view.View.OnClickListener mClickListener;
    private StateFilter mFilter;
    private final ColumnGridView mGridView;
    private Handler mHandler;
    private Boolean mHasDisabledPhotos;
    private final boolean mLandscape;
    private final android.view.View.OnLongClickListener mLongClickListener;
    private Set mSelectedMediaRefs;
    private final ViewUseListener mViewUseListener;
    
    public AlbumGridViewAdapter(Context context, Cursor cursor, String s, ColumnGridView columngridview, View.OnClickListener onclicklistener, View.OnLongClickListener onlongclicklistener, ViewUseListener viewuselistener) {
    	super(context, null);
        int i = 1;
        mHandler = new Handler(Looper.getMainLooper());
        mFilter = sDefaultFilter;
        boolean flag;
        ColumnGridView columngridview1;
        Resources resources;
        int j;
        int k;
        DisplayMetrics displaymetrics;
        int l;
        int i1;
        int j1;
        int k1;
        int l1;
        int i2;
        int j2;
        if(context.getResources().getConfiguration().orientation == 2)
            flag = true;
        else
            flag = false;
        mLandscape = flag;
        if(!mLandscape)
            i = 2;
        columngridview.setOrientation(i);
        mViewUseListener = viewuselistener;
        mClickListener = onclicklistener;
        mLongClickListener = onlongclicklistener;
        mGridView = columngridview;
        mAlbumType = s;
        columngridview1 = mGridView;
        resources = context.getResources();
        j = resources.getDimensionPixelOffset(R.dimen.album_photo_grid_width);
        k = resources.getDimensionPixelOffset(R.dimen.album_photo_grid_spacing);
        columngridview1.setPadding(k, k, k, k);
        columngridview1.setItemMargin(k);
        displaymetrics = new DisplayMetrics();
        ((WindowManager)context.getSystemService("window")).getDefaultDisplay().getMetrics(displaymetrics);
        l = displaymetrics.widthPixels;
        i1 = displaymetrics.heightPixels;
        j1 = l - k * (-1 + l / j);
        k1 = j1 / (j1 / j);
        l1 = i1 - k * (-1 + i1 / j);
        i2 = Math.max(k1, l1 / (l1 / j));
        j2 = Math.min(l, i1) / i2;
        if(EsLog.isLoggable("AlbumGridViewAdapter", 3))
        {
            Log.d("AlbumGridViewAdapter", (new StringBuilder("Usable width: ")).append(l).append(", usable height: ").append(i1).toString());
            Log.d("AlbumGridViewAdapter", (new StringBuilder("Thumbnail size: ")).append(i2).append(", columns: ").append(j2).toString());
        }
        columngridview1.setColumnCount(j2);
        columngridview.setRecyclerListener(new ColumnGridView.RecyclerListener() {

            public final void onMovedToScrapHeap(View view)
            {
                ImageResourceView imageresourceview = (ImageResourceView)view;
                if(imageresourceview != null)
                {
                    imageresourceview.onRecycle();
                    imageresourceview.setTag(null);
                    imageresourceview.setOnClickListener(null);
                    imageresourceview.setOnLongClickListener(null);
                }
            }
        });
    }

    private MediaRef createMediaRef(String s, long l, MediaRef.MediaType mediatype, String s1)
    {
        MediaRef mediaref;
        if(TextUtils.equals(mAlbumType, "camera_photos"))
            mediaref = new MediaRef(s, 0L, null, Uri.parse(s1), mediatype);
        else
            mediaref = new MediaRef(s, l, s1, null, mediatype);
        return mediaref;
    }

    private static MediaRef.MediaType getMediaTypeForRow(Cursor cursor)
    {
        MediaRef.MediaType mediatype;
        if(!cursor.isNull(11))
            mediatype = MediaRef.MediaType.VIDEO;
        else
        if(cursor.getInt(12) != 0)
            mediatype = MediaRef.MediaType.PANORAMA;
        else
            mediatype = MediaRef.MediaType.IMAGE;
        return mediatype;
    }

    public final void bindView(View view, Context context, Cursor cursor) {
        AlbumColumnGridItemView albumcolumngriditemview;
        int i;
        albumcolumngriditemview = (AlbumColumnGridItemView)view;
        i = cursor.getInt(14);
        int state = mFilter.getState(i);
        if(0 == state) {
        	albumcolumngriditemview.setOnClickListener(mClickListener);
            albumcolumngriditemview.setOnLongClickListener(mLongClickListener);
            albumcolumngriditemview.setEnabled(true);
        } else if(1 == state) {
        	albumcolumngriditemview.setOnClickListener(null);
            albumcolumngriditemview.setOnLongClickListener(null);
            albumcolumngriditemview.setEnabled(false);
        }
        
        final int cursorPosition = cursor.getPosition();
        int j = R.string.photo_in_list_count;
        Object aobj[] = new Object[2];
        aobj[0] = Integer.valueOf(cursorPosition + 1);
        aobj[1] = Integer.valueOf(cursor.getCount());
        albumcolumngriditemview.setContentDescription(context.getString(j, aobj));
        albumcolumngriditemview.setTag(R.id.tag_position, Integer.valueOf(cursorPosition));
        int k;
        if(!cursor.isNull(9))
        {
            String s = cursor.getString(5);
            long l = cursor.getLong(8);
            String s1 = cursor.getString(9);
            MediaRef mediaref = createMediaRef(s, l, getMediaTypeForRow(cursor), s1);
            albumcolumngriditemview.setMediaRef(mediaref);
            albumcolumngriditemview.setTag(mediaref);
            int i1;
            int j1;
            boolean flag;
            if(cursor.isNull(4))
                i1 = 0;
            else
                i1 = cursor.getInt(4);
            if(i1 > 0)
                albumcolumngriditemview.setPlusOneCount(Integer.valueOf(i1));
            else
                albumcolumngriditemview.setPlusOneCount(null);
            if(cursor.isNull(2))
                j1 = 0;
            else
                j1 = cursor.getInt(2);
            if(j1 > 0)
                albumcolumngriditemview.setCommentCount(Integer.valueOf(j1));
            else
                albumcolumngriditemview.setCommentCount(null);
            if(!cursor.isNull(10))
                flag = true;
            else
                flag = false;
            albumcolumngriditemview.setNotification(flag);
            if(mSelectedMediaRefs != null && mSelectedMediaRefs.contains(mediaref))
                mGridView.select(cursorPosition);
            else
                mGridView.deselect(cursorPosition);
        } else
        {
            albumcolumngriditemview.setMediaRef(null);
        }
        if(mLandscape)
            k = 1;
        else
            k = 2;
        
        view.setLayoutParams(new ColumnGridView.LayoutParams(k, -3));
        if(mViewUseListener != null && cursorPosition < getCount())
            mHandler.post(new Runnable() {

                public final void run()
                {
                    mViewUseListener.onViewUsed(cursorPosition);
                }

            });
    }

    public final MediaRef getMediaRefForItem(int i)
    {
        Cursor cursor = getCursor();
        MediaRef mediaref;
        if(cursor != null && cursor.moveToPosition(i))
            mediaref = createMediaRef(cursor.getString(5), cursor.getLong(8), getMediaTypeForRow(cursor), cursor.getString(9));
        else
            mediaref = null;
        return mediaref;
    }

    public final long getTimestampForItem(int i)
    {
        Cursor cursor = getCursor();
        long l;
        if(cursor != null && cursor.moveToPosition(i) && !cursor.isNull(13))
            l = cursor.getLong(13);
        else
            l = 0L;
        return l;
    }

    public final boolean hasStableIds()
    {
        return false;
    }

    public final boolean isAnyPhotoDisabled() {
        if(mHasDisabledPhotos != null) 
        	return mHasDisabledPhotos.booleanValue(); 
        else {
        	mHasDisabledPhotos = Boolean.valueOf(false);
        	while(mCursor.moveToNext()) {
        		int i = mCursor.getInt(14);
        		if(mFilter.getState(i) == 1) {
        			mHasDisabledPhotos = Boolean.valueOf(true);
        		}
        	}
        }
        
        return mHasDisabledPhotos.booleanValue();
    }

    public final View newView(Context context, Cursor cursor, ViewGroup viewgroup)
    {
        return LayoutInflater.from(context).inflate(R.layout.album_column_grid_view_item, null);
    }

    public final void onResume()
    {
        super.onResume();
        if(mGridView != null)
        {
            int i = 0;
            for(int j = mGridView.getChildCount(); i < j; i++)
                ((ImageResourceView)mGridView.getChildAt(i)).onResume();

            mGridView.onResume();
        }
    }

    public final void onStop()
    {
        super.onStop();
        int i = 0;
        for(int j = mGridView.getChildCount(); i < j; i++)
            ((ImageResourceView)mGridView.getChildAt(i)).onStop();

    }

    public final void setSelectedMediaRefs(HashSet hashset)
    {
        mSelectedMediaRefs = hashset;
    }

    public final void setStateFilter(StateFilter statefilter)
    {
        if(statefilter == null)
            mFilter = sDefaultFilter;
        else
            mFilter = statefilter;
    }

    public final Cursor swapCursor(Cursor cursor)
    {
        mHasDisabledPhotos = null;
        return super.swapCursor(cursor);
    }

	
	public static interface StateFilter
    {

        public abstract int getState(int i);
    }

    public static interface ViewUseListener
    {

        public abstract void onViewUsed(int i);
    }
}
