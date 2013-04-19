/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.galaxy.meetup.client.android.EsCursorAdapter;
import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsEventData;
import com.galaxy.meetup.client.android.ui.view.EsImageView;
import com.galaxy.meetup.client.android.ui.view.EventThemeView;

/**
 * 
 * @author sihai
 *
 */
public class EventThemeListFragment extends EsListFragment implements
		LoaderCallbacks, OnItemClickListener, Refreshable {

	private static final String EVENT_THEME_COLUMNS[] = {
        "_id", "theme_id", "image_url", "placeholder_path"
    };
    private boolean mDataLoaded;
    private int mFilter;
    private OnThemeSelectedListener mListener;
    private ProgressBar mProgressBarView;
    
    public EventThemeListFragment()
    {
    }

    public EventThemeListFragment(int i)
    {
        mFilter = i;
    }

    private void updateProgressBarVisibility()
    {
        if(mProgressBarView != null)
        {
            ProgressBar progressbar = mProgressBarView;
            byte byte0;
            if(mDataLoaded)
                byte0 = 8;
            else
                byte0 = 0;
            progressbar.setVisibility(byte0);
        }
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
            mFilter = bundle.getInt("filter");
        getLoaderManager().initLoader(0, null, this);
    }

    public final Loader onCreateLoader(int i, Bundle bundle) {
    	Loader loader = null;
        final FragmentActivity context = getActivity();
        final EsAccount esaccount = (EsAccount)getActivity().getIntent().getExtras().get("account");
        if(0 == i) {
        	loader = new EsCursorLoader(context) {

                public final Cursor esLoadInBackground()
                {
                    return EsEventData.getEventThemes(context, esaccount, mFilter, EventThemeListFragment.EVENT_THEME_COLUMNS);
                }
            };
        }
        return loader;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = super.onCreateView(layoutinflater, viewgroup, bundle, R.layout.event_theme_list_fragment);
        mAdapter = new EventThemeListAdapter(getActivity(), null);
        ((ListView)mListView).setAdapter(mAdapter);
        ((ListView)mListView).setOnItemClickListener(this);
        ((ListView)mListView).setRecyclerListener(new android.widget.AbsListView.RecyclerListener() {

            public final void onMovedToScrapHeap(View view1)
            {
                ((EventThemeView)view1.findViewById(R.id.image)).onRecycle();
            }

        });
        setupEmptyView(view, R.string.event_theme_list_empty);
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

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        Cursor cursor = (Cursor)mAdapter.getItem(i);
        int j = cursor.getInt(1);
        String s = cursor.getString(2);
        if(mListener != null)
            mListener.onThemeSelected(j, s);
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        mDataLoaded = true;
        mAdapter.swapCursor(cursor);
        View view = getView();
        if(view != null)
        {
            if(mDataLoaded)
                showContent(view);
            else
                showEmptyViewProgress(view, getString(R.string.loading));
            updateProgressBarVisibility();
        }
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
        bundle.putInt("filter", mFilter);
    }

    public void onScroll(AbsListView abslistview, int i, int j, int k)
    {
        super.onScroll(abslistview, i, j, k);
    }

    public void onScrollStateChanged(AbsListView abslistview, int i)
    {
        super.onScrollStateChanged(abslistview, i);
    }

    public final void setOnThemeSelectedListener(OnThemeSelectedListener onthemeselectedlistener)
    {
        mListener = onthemeselectedlistener;
    }

    public final void setProgressBar(ProgressBar progressbar)
    {
        mProgressBarView = progressbar;
        updateProgressBarVisibility();
    }
    
    //==================================================================================================================
    //									Inner class
    //==================================================================================================================
    final class EventThemeListAdapter extends EsCursorAdapter {

        public final void bindView(View view, Context context, Cursor cursor)
        {
            ProgressBar progressbar = (ProgressBar)view.findViewById(R.id.progress_bar);
            progressbar.setVisibility(0);
            EventThemeView eventthemeview = (EventThemeView)view.findViewById(R.id.image);
            eventthemeview.setOnImageLoadedListener((EsImageView.OnImageLoadedListener)view.getTag());
            String s = cursor.getString(2);
            String s1 = cursor.getString(3);
            if(!TextUtils.isEmpty(s1))
            {
                android.net.Uri.Builder builder = new android.net.Uri.Builder();
                builder.path(s1);
                eventthemeview.setDefaultImageUri(builder.build());
                progressbar.setVisibility(8);
            }
            eventthemeview.setImageUrl(s);
        }

        public final View newView(Context context, Cursor cursor, ViewGroup viewgroup)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.event_theme_list_item, viewgroup, false);
            final ProgressBar progressBar = ((ProgressBar)view.findViewById(R.id.progress_bar));
            view.setTag(new EsImageView.OnImageLoadedListener() {

                public final void onImageLoaded()
                {
                    progressBar.setVisibility(8);
                }

            });
            return view;
        }

        public EventThemeListAdapter(Context context, Cursor cursor)
        {
            super(context, null);
        }
    }

    public static interface OnThemeSelectedListener
    {

        public abstract void onThemeSelected(int i, String s);
    }

}
