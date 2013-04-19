/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.common.Recyclable;
import com.galaxy.meetup.client.android.ui.view.ColumnGridView;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.SearchViewAdapter.OnQueryChangeListener;
import com.galaxy.meetup.client.util.ScreenMetrics;

/**
 * 
 * @author sihai
 *
 */
public class HostedSquareSearchFragment extends HostedEsFragment implements
		OnQueryChangeListener, SquareSearchAdapter.SearchListAdapterListener {

	protected static ScreenMetrics sScreenMetrics;
    private SquareSearchAdapter mAdapter;
    private ColumnGridView mGridView;
    
    public HostedSquareSearchFragment()
    {
    }

    private void updateView(View view)
    {
        mGridView.setVisibility(0);
        showContent(view);
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.SQUARE_SEARCH;
    }

    protected final boolean isEmpty()
    {
        return false;
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        android.support.v4.app.LoaderManager loadermanager = getLoaderManager();
        mAdapter = new SquareSearchAdapter(getActivity(), getFragmentManager(), loadermanager, mAccount);
        mAdapter.onCreate(bundle);
        mAdapter.setListener(this);
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.hosted_square_search_fragment, viewgroup, false);
        mGridView = (ColumnGridView)view.findViewById(R.id.grid);
        mGridView.setAdapter(mAdapter);
        if(sScreenMetrics == null)
            sScreenMetrics = ScreenMetrics.getInstance(getActivity());
        Resources resources = getActivity().getResources();
        int i = resources.getConfiguration().orientation;
        boolean flag = false;
        if(i == 2)
            flag = true;
        if(flag)
        {
            mGridView.setOrientation(1);
            mGridView.setColumnCount(-1);
            mGridView.setMinColumnWidth(resources.getDimensionPixelSize(R.dimen.square_card_min_height));
        } else
        {
            mGridView.setOrientation(2);
            ColumnGridView columngridview = mGridView;
            int j;
            if(sScreenMetrics.screenDisplayType == 0)
                j = 1;
            else
                j = 2;
            columngridview.setColumnCount(j);
        }
        mGridView.setItemMargin(sScreenMetrics.itemMargin);
        mGridView.setPadding(sScreenMetrics.itemMargin, sScreenMetrics.itemMargin, sScreenMetrics.itemMargin, sScreenMetrics.itemMargin);
        mGridView.setRecyclerListener(new ColumnGridView.RecyclerListener() {

            public final void onMovedToScrapHeap(View view1)
            {
                if(view1 instanceof Recyclable)
                    ((Recyclable)view1).onRecycle();
            }

        });
        updateView(view);
        return view;
    }

    protected final void onPrepareActionBar(HostActionBar hostactionbar)
    {
        hostactionbar.showSearchView();
        hostactionbar.getSearchViewAdapter().setQueryHint(R.string.search_squares_hint_text);
        hostactionbar.getSearchViewAdapter().addOnChangeListener(this);
    }

    public final void onQueryClose()
    {
    }

    public final void onQueryTextChanged(CharSequence charsequence)
    {
        if(mAdapter != null)
        {
            SquareSearchAdapter squaresearchadapter = mAdapter;
            String s;
            if(charsequence == null)
                s = null;
            else
                s = charsequence.toString().trim();
            squaresearchadapter.setQueryString(s);
        }
    }

    public final void onQueryTextSubmitted(CharSequence charsequence)
    {
    }

    public final void onResume()
    {
        super.onResume();
        updateView(getView());
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mAdapter != null)
            mAdapter.onSaveInstanceState(bundle);
    }

    public final void onSearchListAdapterStateChange()
    {
        View view = getView();
        if(view != null)
            updateView(view);
    }

    public final void onSquareSelected(String s)
    {
        startActivity(Intents.getSquareStreamActivityIntent(getActivity(), mAccount, s, null, null));
    }

    public final void onStart()
    {
        super.onStart();
        mAdapter.onStart();
    }

    public final void onStop()
    {
        super.onStart();
        mAdapter.onStop();
    }
}
