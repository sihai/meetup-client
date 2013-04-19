/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galaxy.meetup.client.android.EsCursorAdapter;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.ui.view.OneUpBaseView;
import com.galaxy.meetup.client.android.ui.view.OneUpListener;
import com.galaxy.meetup.client.android.ui.view.StreamOneUpActivityView;
import com.galaxy.meetup.client.android.ui.view.StreamOneUpCommentCountView;
import com.galaxy.meetup.client.android.ui.view.StreamOneUpCommentView;
import com.galaxy.meetup.client.android.ui.view.StreamOneUpLeftoverView;

/**
 * 
 * @author sihai
 *
 */
public class StreamOneUpAdapter extends EsCursorAdapter implements SettableItemAdapter {

	private int mActivityPosition;
    private int mContainerHeight;
    private Set mFlaggedComments;
    private SparseIntArray mHeights;
    private int mLeftoverPosition;
    private boolean mLoading;
    private final OneUpBaseView.OnMeasuredListener mOnMeasuredListener;
    private final OneUpListener mOneUpListener;
 
    public StreamOneUpAdapter(Context context, Cursor cursor, OneUpListener oneuplistener, OneUpBaseView.OnMeasuredListener onmeasuredlistener)
    {
        super(context, null);
        mActivityPosition = -1;
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
	        	StreamOneUpActivityView streamoneupactivityview = (StreamOneUpActivityView)view;
	            streamoneupactivityview.setOneUpClickListener(mOneUpListener);
	            streamoneupactivityview.setOnMeasureListener(mOnMeasuredListener);
	            streamoneupactivityview.bind(cursor);
	        	break;
	        case 1:
	        	StreamOneUpCommentCountView streamoneupcommentcountview = (StreamOneUpCommentCountView)view;
	            streamoneupcommentcountview.setOnMeasureListener(mOnMeasuredListener);
	            streamoneupcommentcountview.bind(cursor);
	        	break;
	        case 2:
	        	String s = cursor.getString(5);
	            boolean flag = mFlaggedComments.contains(s);
	            StreamOneUpCommentView streamoneupcommentview = (StreamOneUpCommentView)view;
	            streamoneupcommentview.setOneUpClickListener(mOneUpListener);
	            streamoneupcommentview.setOnMeasureListener(mOnMeasuredListener);
	            streamoneupcommentview.bind(cursor, flag);
	        	break;
	        case 3:
	        	if(mLoading)
	                view.findViewById(R.id.loading_spinner).setVisibility(0);
	            else
	                view.findViewById(R.id.loading_spinner).setVisibility(8);
	            view.invalidate();
	            view.requestLayout();
	        	break;
	        case 4:
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
	        	break;
	        default:
	        	break;
        }
    }

    public final String getAclText() {
        int i;
        String s;
        i = mActivityPosition;
        s = null;
        if(i < 0) {
        	return s;
        }
     
        Cursor cursor = (Cursor)getItem(mActivityPosition);
        s = null;
        if(cursor != null)
            s = cursor.getString(3);
        return s;
    }

    public final String getActivityAuthorId()
    {
        String s;
        if(mActivityPosition < 0 || mActivityPosition > getCount())
            s = null;
        else
            s = ((Cursor)getItem(mActivityPosition)).getString(4);
        return s;
    }

    public final int getItemViewType(int i)
    {
        return ((Cursor)getItem(i)).getInt(1);
    }

    public final int getViewTypeCount()
    {
        return 5;
    }

    public final View newView(Context context, Cursor cursor, ViewGroup viewgroup) {
    	View view = null;
    	LayoutInflater layoutinflater = (LayoutInflater)context.getSystemService("layout_inflater");
        int value = cursor.getInt(1);
        switch(value) {
	        case 0:
	        	view = layoutinflater.inflate(R.layout.stream_one_up_activity_view, viewgroup, false);
	        	break;
	        case 1:
	        	view = layoutinflater.inflate(R.layout.stream_one_up_comment_count_view, viewgroup, false);
	        	break;
	        case 2:
	        	view = layoutinflater.inflate(R.layout.stream_one_up_comment_view, viewgroup, false);
	        	break;
	        case 3:
	        	view = layoutinflater.inflate(R.layout.stream_one_up_loading_view, viewgroup, false);
	        	break;
	        case 4:
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
        if(mLoading != flag)
        {
            mLoading = flag;
            notifyDataSetChanged();
        }
    }

    public final Cursor swapCursor(Cursor cursor) {
        mActivityPosition = -1;
        if(cursor == null) {
        	mHeights = null;
            mLeftoverPosition = -1;
        } else { 
        	int i = cursor.getCount();
            mHeights = new SparseIntArray(i);
            mLeftoverPosition = i - 1;
            while(cursor.moveToNext()) {
            	if(cursor.getInt(1) == 0) {
            		mActivityPosition = cursor.getPosition(); 
            		break;
            	}
            }
            cursor.moveToFirst();
        }
		return super.swapCursor(cursor);
    }
    
    
    public static interface ActivityQuery
    {

        public static final String PROJECTION[] = {
            "2147483647 AS _id", "0 AS row_type", "activity_id", "acl_display", "author_id", "name", "avatar", "total_comment_count", "plus_one_data", "loc", 
            "created", "is_edited", "modified", "source_id", "source_name", "public", "spam", "can_comment", "can_reshare", "has_muted", 
            "data_state", "content_flags", "annotation", "title", "original_author_id", "original_author_name", "embed_deep_link", "embed_appinvite", "embed_media", "embed_skyjam", 
            "embed_place_review", "embed_hangout", "embed_square", "embed_emotishare"
        };

    }

    public static interface CommentCountQuery
    {

        public static final String PROJECTION[] = {
            "2147483646 AS _id", "1 AS row_type", "COUNT(*) AS _count"
        };

    }

    public static interface CommentQuery
    {

        public static final String PROJECTION[] = {
            "_id", "2 AS row_type", "author_id", "name", "avatar", "comment_id", "content", "created", "plus_one_data"
        };

    }

    public static interface LeftoverQuery
    {

        public static final String PROJECTION[] = {
            "2147483645 AS _id", "4 AS row_type"
        };

    }

    public static interface LoadingQuery
    {

        public static final String PROJECTION[] = {
            "0 AS _id", "3 AS row_type"
        };

    }
}
