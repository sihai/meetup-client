/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.galaxy.meetup.client.android.EsCursorAdapter;

/**
 * 
 * @author sihai
 *
 */
public class EsListFragment extends EsFragment implements OnScrollListener {

	protected EsCursorAdapter mAdapter;
    protected AbsListView mListView;
    private int mPrevScrollItemCount;
    private int mPrevScrollPosition;
    private int mScrollOffset;
    private int mScrollPos;
    
    EsListFragment()
    {
        mPrevScrollPosition = -1;
        mPrevScrollItemCount = -1;
    }

    protected final boolean isEmpty()
    {
        boolean flag;
        if(mAdapter == null || mAdapter.getCursor() == null || mAdapter.getCount() == 0)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
        {
            mScrollPos = bundle.getInt("scroll_pos");
            mScrollOffset = bundle.getInt("scroll_off");
        } else
        {
            mScrollPos = 0;
            mScrollOffset = 0;
        }
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle, int i)
    {
        View view = super.onCreateView(layoutinflater, viewgroup, bundle, i);
        mListView = (AbsListView)view.findViewById(0x102000a);
        mListView.setOnScrollListener(this);
        return view;
    }

    public void onDestroyView()
    {
        super.onDestroyView();
        if(mListView != null)
        {
            mListView.setOnScrollListener(null);
            mListView = null;
        }
    }

    public void onPause()
    {
        super.onPause();
        if(mAdapter != null && mAdapter.getCursor() != null)
        {
            EsCursorAdapter _tmp = mAdapter;
            EsCursorAdapter.onPause();
        }
    }

    public void onResume()
    {
        super.onResume();
        if(mAdapter != null && mAdapter.getCursor() != null)
            mAdapter.onResume();
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(!getActivity().isFinishing() && mListView != null)
        {
            if(mListView != null)
            {
                mScrollPos = mListView.getFirstVisiblePosition();
                if(mAdapter != null)
                {
                    View view = mListView.getChildAt(0);
                    if(view != null)
                        mScrollOffset = view.getTop();
                    else
                        mScrollOffset = 0;
                } else
                {
                    mScrollOffset = 0;
                }
            }
            bundle.putInt("scroll_pos", mScrollPos);
            bundle.putInt("scroll_off", mScrollOffset);
        }
    }

    public void onScroll(AbsListView abslistview, int i, int j, int k)
    {
        if(k > 0)
        {
            int l = i + j;
            if(l >= k && l == mPrevScrollPosition)
            {
                int _tmp = mPrevScrollItemCount;
            }
            mPrevScrollPosition = l;
            mPrevScrollItemCount = k;
        }
    }

    public void onScrollStateChanged(AbsListView abslistview, int i)
    {
    }

    protected final void restoreScrollPosition()
    {
        if(mListView != null && (mListView instanceof ListView) && (mScrollOffset != 0 || mScrollPos != 0))
        {
            ((ListView)mListView).setSelectionFromTop(mScrollPos, mScrollOffset);
            mScrollPos = 0;
            mScrollOffset = 0;
        }
    }
}
