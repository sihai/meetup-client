/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.io.IOException;
import java.util.HashSet;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galaxy.meetup.client.android.EsCursorAdapter;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.DbPlusOneData;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.client.android.ui.view.OneUpBaseView;
import com.galaxy.meetup.client.android.ui.view.OneUpListener;
import com.galaxy.meetup.client.android.ui.view.PhotoOneUpInfoView;
import com.galaxy.meetup.client.android.ui.view.StreamOneUpCommentCountView;
import com.galaxy.meetup.client.android.ui.view.StreamOneUpCommentView;
import com.galaxy.meetup.client.android.ui.view.StreamOneUpLeftoverView;
import com.galaxy.meetup.server.client.domain.DataPlusOne;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class PhotoOneUpAdapter extends EsCursorAdapter implements
		SettableItemAdapter {


	private int mContainerHeight;
    private HashSet mFlaggedComments;
    private SparseIntArray mHeights;
    private int mLeftoverPosition;
    private boolean mLoading;
    private final OneUpBaseView.OnMeasuredListener mOnMeasuredListener;
    private final OneUpListener mOneUpListener;
    private int mPhotoPosition;
    
    
    public PhotoOneUpAdapter(Context context, Cursor cursor, OneUpListener oneuplistener, OneUpBaseView.OnMeasuredListener onmeasuredlistener)
    {
        super(context, null);
        mPhotoPosition = -1;
        mLeftoverPosition = -1;
        mOneUpListener = oneuplistener;
        mOnMeasuredListener = onmeasuredlistener;
    }

    public final void addFlaggedComment(String s)
    {
        mFlaggedComments.add(s);
        notifyDataSetChanged();
    }

    public final void bindView(View view, Context context, Cursor cursor)
    {
        int value = cursor.getInt(1);
        switch(value) {
        case 0:
        	PhotoOneUpInfoView photooneupinfoview = (PhotoOneUpInfoView)view;
            photooneupinfoview.setOneUpClickListener(mOneUpListener);
            photooneupinfoview.setOnMeasureListener(mOnMeasuredListener);
            photooneupinfoview.setOwner(cursor.getString(3), cursor.getString(4), EsAvatarData.uncompressAvatarUrl(cursor.getString(5)));
            photooneupinfoview.setCaption(cursor.getString(19));
            if(!cursor.isNull(11))
                photooneupinfoview.setDate(cursor.getLong(11));
            photooneupinfoview.setPlusOne(cursor.getBlob(20));
            photooneupinfoview.setAlbum(cursor.getString(6));
            photooneupinfoview.invalidate();
            photooneupinfoview.requestLayout();
        	break;
        case 1:
        	String s;
            byte abyte0[];
            byte abyte1[];
            s = cursor.getString(2);
            abyte0 = cursor.getBlob(9);
            abyte1 = null;
            if(abyte0 != null) {
            	try {
	            	byte abyte2[] = DbPlusOneData.serialize((DataPlusOne)JsonUtil.fromByteArray(abyte0, DataPlusOne.class));
	            	abyte1 = abyte2;
            	} catch (IOException e) {
            		abyte1 = null;
            	}
        	}
            boolean flag = mFlaggedComments.contains(s);
            StreamOneUpCommentView streamoneupcommentview = (StreamOneUpCommentView)view;
            streamoneupcommentview.setOneUpClickListener(mOneUpListener);
            streamoneupcommentview.setOnMeasureListener(mOnMeasuredListener);
            streamoneupcommentview.setAuthor(cursor.getString(3), cursor.getString(4), EsAvatarData.uncompressAvatarUrl(cursor.getString(5)));
            streamoneupcommentview.setComment(s, cursor.getString(8), flag);
            streamoneupcommentview.setPlusOne(abyte1);
            streamoneupcommentview.setDate(cursor.getLong(6));
            streamoneupcommentview.invalidate();
            streamoneupcommentview.requestLayout();
        	break;
        case 2:
        	
        	break;
        case 3:
        	view.findViewById(R.id.loading_spinner).setVisibility(0);
            view.invalidate();
            view.requestLayout();
        	break;
        case 4:
        	StreamOneUpCommentCountView streamoneupcommentcountview = (StreamOneUpCommentCountView)view;
            streamoneupcommentcountview.setOnMeasureListener(mOnMeasuredListener);
            streamoneupcommentcountview.setCount(cursor.getInt(2));
            streamoneupcommentcountview.invalidate();
            streamoneupcommentcountview.requestLayout();
        	break;
        case 5:
        	StreamOneUpLeftoverView streamoneupleftoverview = (StreamOneUpLeftoverView)view;
            int i = mContainerHeight;
            if(mHeights != null)
            {
                for(int j = -1 + mHeights.size(); i > 0 && j >= 0; j--)
                {
                    int k = mHeights.keyAt(j);
                    i -= mHeights.get(k);
                }

            }
            streamoneupleftoverview.bind(i);
            streamoneupleftoverview.invalidate();
            streamoneupleftoverview.requestLayout();
        	break;
        default:
        	break;
        }
        
    }

    public final int getItemViewType(int i)
    {
        return ((Cursor)getItem(i)).getInt(1);
    }

    public final int getViewTypeCount()
    {
        return 6;
    }

    public final View newView(Context context, Cursor cursor, ViewGroup viewgroup)
    {
    	View view = null;
        LayoutInflater layoutinflater = (LayoutInflater)context.getSystemService("layout_inflater");
        int value = cursor.getInt(1);
        switch(value) {
        case 0:
        	view = layoutinflater.inflate(R.layout.photo_one_up_info_view, viewgroup, false);
        	break;
        case 1:
        	view = layoutinflater.inflate(R.layout.stream_one_up_comment_view, viewgroup, false);
        	break;
        case 2:
        	
        	break;
        case 3:
        	view = layoutinflater.inflate(R.layout.stream_one_up_loading_view, viewgroup, false);
        	break;
        case 4:
        	view = layoutinflater.inflate(R.layout.stream_one_up_comment_count_view, viewgroup, false);
        	break;
        case 5:
        	view = layoutinflater.inflate(R.layout.stream_one_up_leftover_view, viewgroup, false);
            mLeftoverPosition = cursor.getPosition();
        	break;
        default:
        	break;
        }
        return view;
    }

    public final void removeFlaggedComment(String s)
    {
        mFlaggedComments.remove(s);
        notifyDataSetChanged();
    }

    public final void setContainerHeight(int i)
    {
        mContainerHeight = i;
    }

    public final void setFlaggedComments(HashSet hashset)
    {
        mFlaggedComments = hashset;
    }

    public final void setItemHeight(int i, int j)
    {
        if(i >= 0 && mHeights != null && i != mLeftoverPosition)
            mHeights.put(i, j);
    }

    public final void setLoading(boolean flag)
    {
        if(flag != mLoading)
        {
            mLoading = flag;
            notifyDataSetChanged();
        }
    }

    public final Cursor swapCursor(Cursor cursor)
    {
        mPhotoPosition = -1;
        if(cursor == null) {
        	mHeights = null;
            mLeftoverPosition = -1;
            return super.swapCursor(cursor);
        } else { 
        	int i = cursor.getCount();
            mHeights = new SparseIntArray(i);
            mLeftoverPosition = i - 1;
            if(!cursor.moveToFirst()) {
            	return super.swapCursor(cursor);
            } else { 
            	do {
            		if(cursor.getInt(1) == 0) {
            			mPhotoPosition = cursor.getPosition();
            			cursor.moveToFirst();
            		}
            	} while(cursor.moveToNext());
            	
            	return super.swapCursor(cursor);
            }
        }
    }
}
