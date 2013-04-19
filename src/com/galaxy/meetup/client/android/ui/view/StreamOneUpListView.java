/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.galaxy.meetup.client.android.ui.fragments.SettableItemAdapter;
import com.galaxy.meetup.client.android.ui.view.OneUpBaseView.OnMeasuredListener;

/**
 * 
 * @author sihai
 *
 */
public class StreamOneUpListView extends ListView implements OnMeasuredListener {

	private int mMaxWidth;
    private OneUpBaseView.OnMeasuredListener mOnMeasuredListener;
    private android.widget.AbsListView.OnScrollListener listener = new android.widget.AbsListView.OnScrollListener() {

        public final void onScroll(AbsListView abslistview, int i, int j, int k)
        {
        }

        public final void onScrollStateChanged(AbsListView abslistview, int i)
        {
            if(i != 0)
            {
                int j = 0;
                for(int k = abslistview.getChildCount(); j < k; j++)
                {
                    View view = abslistview.getChildAt(j);
                    if(view instanceof StreamOneUpCommentView)
                        ((StreamOneUpCommentView)view).cancelPressedState();
                }

            }
        }
    };
    
	public StreamOneUpListView(Context context)
    {
        super(context);
        mMaxWidth = -1;
        setOnScrollListener(listener);
    }

    public StreamOneUpListView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mMaxWidth = -1;
        setOnScrollListener(listener);
    }

    public StreamOneUpListView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mMaxWidth = -1;
        setOnScrollListener(listener);
    }

    protected void layoutChildren()
    {
        super.layoutChildren();
        SettableItemAdapter settableitemadapter = (SettableItemAdapter)getAdapter();
        if(settableitemadapter != null)
        {
            int i = getFirstVisiblePosition();
            int j = -1 + getChildCount();
            while(j >= 0) 
            {
                settableitemadapter.setItemHeight(i + j, getChildAt(j).getMeasuredHeight());
                j--;
            }
        }
    }

    protected void onMeasure(int i, int j)
    {
        super.onMeasure(i, j);
        if(mMaxWidth > 0 && getMeasuredWidth() > mMaxWidth)
            super.onMeasure(android.view.View.MeasureSpec.makeMeasureSpec(mMaxWidth, 0x40000000), j);
        if(mOnMeasuredListener != null)
            mOnMeasuredListener.onMeasured(this);
    }

    public final void onMeasured(View view)
    {
        int i = -1;
        int j = -1 + getChildCount();
        for(; j >= 0; j--) {
        	if(getChildAt(j).equals(view)) {
        		i = j;
        		break;
        	}
        }
        
        if(i >= 0)
        {
            int k = i + getFirstVisiblePosition();
            SettableItemAdapter settableitemadapter = (SettableItemAdapter)getAdapter();
            if(settableitemadapter != null)
                settableitemadapter.setItemHeight(k, view.getMeasuredHeight());
        }
    }

    public void setMaxWidth(int i)
    {
        mMaxWidth = i;
    }

    public void setOnMeasureListener(OneUpBaseView.OnMeasuredListener onmeasuredlistener)
    {
        mOnMeasuredListener = onmeasuredlistener;
    }
}
