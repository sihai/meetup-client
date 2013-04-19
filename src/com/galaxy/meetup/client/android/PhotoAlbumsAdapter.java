/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import WriteReviewOperation.MediaRef;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.ImageResourceView;
import com.galaxy.meetup.client.util.ScreenMetrics;

/**
 * 
 * @author sihai
 *
 */
public class PhotoAlbumsAdapter extends EsCursorAdapter {

	private final Set mImageViews = new HashSet();
    private final boolean mLandscape;
    private android.view.View.OnClickListener mOnClickListener;
    
	public PhotoAlbumsAdapter(Context context, Cursor cursor, ColumnGridView columngridview, android.view.View.OnClickListener onclicklistener) {
        super(context, null);
        int i = 1;
        mOnClickListener = onclicklistener;
        boolean flag;
        if(context.getResources().getConfiguration().orientation == 2)
            flag = true;
        else
            flag = false;
        mLandscape = flag;
        if(!mLandscape)
            i = 2;
        columngridview.setOrientation(i);
        columngridview.setRecyclerListener(new ColumnGridView.RecyclerListener() {

            public final void onMovedToScrapHeap(View view)
            {
                ImageResourceView imageresourceview = (ImageResourceView)view.findViewById(R.id.photo);
                imageresourceview.onRecycle();
                mImageViews.remove(imageresourceview);
                view.setOnClickListener(null);
            }
        });
    }

    public final void bindView(View view, Context context, Cursor cursor) {
        ImageResourceView imageresourceview = (ImageResourceView)view.findViewById(R.id.photo);
        TextView textview = (TextView)view.findViewById(R.id.title);
        TextView textview1 = (TextView)view.findViewById(R.id.count);
        Resources resources = context.getResources();
        view.setTag(Integer.valueOf(cursor.getPosition()));
        view.setOnClickListener(mOnClickListener);
        imageresourceview.setDefaultIconEnabled(true);
        if(!cursor.isNull(10))
        {
            imageresourceview.setMediaRef(new MediaRef(cursor.getString(10), MediaRef.MediaType.IMAGE));
            mImageViews.add(imageresourceview);
        }
        String s;
        int i;
        ColumnGridView.LayoutParams layoutparams;
        ScreenMetrics screenmetrics;
        if(cursor.isNull(8))
            s = resources.getString(R.string.photos_home_unknown_label);
        else
            s = cursor.getString(8);
        textview.setText(s);
        textview.setContentDescription(s);
        if(!cursor.isNull(1))
        {
            Integer integer = Integer.valueOf(cursor.getInt(1));
            textview1.setText(context.getResources().getQuantityString(R.plurals.album_photo_count, integer.intValue(), new Object[] {
                integer
            }).toUpperCase());
            textview1.setVisibility(0);
        } else
        {
            textview1.setVisibility(8);
        }
        if(mLandscape)
            i = 1;
        else
            i = 2;
        layoutparams = new ColumnGridView.LayoutParams(i, -3);
        screenmetrics = ScreenMetrics.getInstance(context);
        if(screenmetrics.screenDisplayType == 1 && mLandscape)
            layoutparams.width = screenmetrics.longDimension / 3;
        view.setLayoutParams(layoutparams);
    }

    public final View newView(Context context, Cursor cursor, ViewGroup viewgroup)
    {
        return LayoutInflater.from(context).inflate(R.layout.photo_home_view_item, null);
    }

    public final void onResume()
    {
        super.onResume();
        for(Iterator iterator = mImageViews.iterator(); iterator.hasNext(); ((ImageResourceView)iterator.next()).onResume());
    }

    public final void onStop()
    {
        super.onStop();
        for(Iterator iterator = mImageViews.iterator(); iterator.hasNext(); ((ImageResourceView)iterator.next()).onStop());
    }

}
