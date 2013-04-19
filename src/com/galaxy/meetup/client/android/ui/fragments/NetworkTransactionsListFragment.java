/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.NetworkTransactionsAdapter;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsCursorLoader;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.service.EsService;

/**
 * 
 * @author sihai
 *
 */
public class NetworkTransactionsListFragment extends EsListFragment implements
		LoaderCallbacks {

	private EsAccount mAccount;
    private ProgressBar mProgressView;
    
    public NetworkTransactionsListFragment()
    {
    }

    public final void clear()
    {
        EsService.clearNetworkTransactionsData(getActivity(), mAccount);
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        mAccount = (EsAccount)getActivity().getIntent().getParcelableExtra("account");
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(0, null, this);
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        android.net.Uri uri = EsProvider.appendAccountParameter(EsProvider.NETWORK_DATA_TRANSACTIONS_URI, mAccount);
        return new EsCursorLoader(getActivity(), uri, NetworkTransactionsAdapter.NetworkTransactionsQuery.PROJECTION, null, null, "time DESC");
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = super.onCreateView(layoutinflater, viewgroup, bundle, R.layout.list_layout);
        mAdapter = new NetworkTransactionsAdapter(getActivity(), null);
        ((ListView)mListView).setAdapter(mAdapter);
        setupEmptyView(view, R.string.no_network_transactions);
        showEmptyViewProgress(view);
        return view;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle, int i)
    {
        return super.onCreateView(layoutinflater, viewgroup, bundle, i);
    }

    public final void onDestroyView()
    {
        super.onDestroyView();
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        ((NetworkTransactionsAdapter)mAdapter).swapCursor(cursor);
        restoreScrollPosition();
        if(cursor.getCount() > 0)
            showContent(getView());
        else
            showEmptyView(getView());
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onPause()
    {
        super.onPause();
    }

    public final void onResume()
    {
        super.onResume();
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
    }

    public void onScroll(AbsListView abslistview, int i, int j, int k)
    {
        super.onScroll(abslistview, i, j, k);
    }

    public void onScrollStateChanged(AbsListView abslistview, int i)
    {
        super.onScrollStateChanged(abslistview, i);
    }

    public final void setProgressBar(ProgressBar progressbar)
    {
        mProgressView = progressbar;
        updateSpinner(mProgressView);
    }
}
