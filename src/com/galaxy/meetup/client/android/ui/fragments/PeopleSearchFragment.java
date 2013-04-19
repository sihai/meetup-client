/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import com.galaxy.meetup.client.android.DumpDatabase;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.CircleData;
import com.galaxy.meetup.client.android.content.PersonData;
import com.galaxy.meetup.client.android.ui.fragments.PeopleSearchAdapter.SearchListAdapterListener;
import com.galaxy.meetup.client.android.ui.view.PeopleListItemView;
import com.galaxy.meetup.client.android.ui.view.PeopleListItemView.OnActionButtonClickListener;
import com.galaxy.meetup.client.android.ui.view.SearchViewAdapter;
import com.galaxy.meetup.client.android.ui.view.SearchViewAdapter.OnQueryChangeListener;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class PeopleSearchFragment extends EsPeopleListFragment implements
		SearchListAdapterListener, Refreshable, OnActionButtonClickListener,
		OnQueryChangeListener {

	private PeopleSearchListAdapter mAdapter;
    private boolean mAddToCirclesActionEnabled;
    private int mCircleUsageType;
    private OnSelectionChangeListener mListener;
    private boolean mPeopleInCirclesEnabled;
    private boolean mPhoneOnlyContactsEnabled;
    private boolean mPlusPagesEnabled;
    private ProgressBar mProgressView;
    private boolean mPublicProfileSearchEnabled;
    private String mQuery;
    private SearchViewAdapter mSearchViewAdapter;
    
	public PeopleSearchFragment()
    {
        mCircleUsageType = -1;
    }

    protected final ListAdapter getAdapter()
    {
        return mAdapter;
    }

    protected final int getEmptyText()
    {
        return 0;
    }

    protected final View inflateView(LayoutInflater layoutinflater, ViewGroup viewgroup)
    {
        return layoutinflater.inflate(R.layout.people_search_fragment, viewgroup, false);
    }

    protected final boolean isEmpty()
    {
        boolean flag;
        if(mAdapter == null || mAdapter.isSearchingForFirstResult())
            flag = true;
        else
            flag = false;
        return flag;
    }

    protected final boolean isError()
    {
        return mAdapter.isError();
    }

    protected final boolean isLoaded()
    {
        return mAdapter.isLoaded();
    }

    public final void onActionButtonClick(PeopleListItemView peoplelistitemview, int i)
    {
    }

    public final void onAddPersonToCirclesAction(String s, String s1, boolean flag)
    {
        showCircleMembershipDialog(s, s1);
    }

    public final void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mAdapter = new PeopleSearchListAdapter(activity, getFragmentManager(), getLoaderManager(), getAccount());
        mAdapter.setAddToCirclesActionEnabled(mAddToCirclesActionEnabled);
        mAdapter.setPublicProfileSearchEnabled(mPublicProfileSearchEnabled);
        mAdapter.setIncludePhoneNumberContacts(mPhoneOnlyContactsEnabled);
        mAdapter.setCircleUsageType(mCircleUsageType);
        mAdapter.setIncludePlusPages(mPlusPagesEnabled);
        mAdapter.setIncludePeopleInCircles(mPeopleInCirclesEnabled);
        mAdapter.setShowProgressWhenEmpty(false);
        mAdapter.setFilterNullGaiaIds(activity.getIntent().getBooleanExtra("filter_null_gaia_ids", false));
        mAdapter.setListener(this);
        mAdapter.setQueryString(mQuery);
    }

    public final void onChangeCirclesAction(String s, String s1)
    {
        showCircleMembershipDialog(s, s1);
    }

    public final void onCircleSelected(String s, CircleData circledata)
    {
        mListener.onCircleSelected(s, circledata);
    }

    public final void onCreate(Bundle bundle)
    {
        if(bundle != null)
            mQuery = bundle.getString("query");
        super.onCreate(bundle);
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = super.onCreateView(layoutinflater, viewgroup, bundle);
        mSearchViewAdapter = SearchViewAdapter.createInstance(view.findViewById(R.id.search_src_text));
        mSearchViewAdapter.setQueryHint(R.string.search_people_hint_text);
        mSearchViewAdapter.addOnChangeListener(this);
        updateView(view);
        return view;
    }

    public final void onDismissSuggestionAction(String s, String s1)
    {
    }

    protected final void onInitLoaders(Bundle bundle)
    {
        mAdapter.onCreate(bundle);
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        mAdapter.onItemClick(i);
    }

    public final void onPersonSelected(String s, String s1, PersonData persondata)
    {
        mListener.onPersonSelected(s, s1, persondata);
    }

    public final void onQueryClose()
    {
        mSearchViewAdapter.setQueryText(null);
        mAdapter.setQueryString(null);
    }

    public final void onQueryTextChanged(CharSequence charsequence)
    {
        String s;
        if(charsequence == null)
            s = null;
        else
            s = charsequence.toString().trim();
        mQuery = s;
        if(EsLog.ENABLE_DOGFOOD_FEATURES && null != charsequence) {
        	 
        	if(!"*#*#dumpdb*#*#".equals(charsequence.toString())) {
        		if("*#*#cleandb*#*#".equals(charsequence.toString()))
                    DumpDatabase.cleanNow(getActivity()); 
        	} else {
        		DumpDatabase.dumpNow(getActivity());
        	}
        }
        
        if(mAdapter != null)
            mAdapter.setQueryString(mQuery);
        return;
        
    }

    public final void onQueryTextSubmitted(CharSequence charsequence)
    {
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(mAdapter != null)
            mAdapter.onSaveInstanceState(bundle);
        bundle.putString("query", mQuery);
    }

    public final void onSearchListAdapterStateChange(PeopleSearchAdapter peoplesearchadapter)
    {
        View view = getView();
        if(view != null)
            updateView(view);
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

    public final void onUnblockPersonAction(String s, boolean flag)
    {
    }

    public final void setAddToCirclesActionEnabled(boolean flag)
    {
        mAddToCirclesActionEnabled = flag;
        if(mAdapter != null)
            mAdapter.setAddToCirclesActionEnabled(flag);
    }

    public final void setCircleUsageType(int i)
    {
        mCircleUsageType = i;
        if(mAdapter != null)
            mAdapter.setCircleUsageType(i);
    }

    public final void setInitialQueryString(String s)
    {
        if(mQuery == null)
            mQuery = s;
    }

    public final void setOnSelectionChangeListener(OnSelectionChangeListener onselectionchangelistener)
    {
        mListener = onselectionchangelistener;
    }

    public final void setPeopleInCirclesEnabled(boolean flag)
    {
        mPeopleInCirclesEnabled = flag;
        if(mAdapter != null)
            mAdapter.setIncludePeopleInCircles(flag);
    }

    public final void setPhoneOnlyContactsEnabled(boolean flag)
    {
        mPhoneOnlyContactsEnabled = flag;
        if(mAdapter != null)
            mAdapter.setIncludePhoneNumberContacts(flag);
    }

    public final void setPlusPagesEnabled(boolean flag)
    {
        mPlusPagesEnabled = flag;
        if(mAdapter != null)
            mAdapter.setIncludePlusPages(flag);
    }

    public final void setProgressBar(ProgressBar progressbar)
    {
        mProgressView = progressbar;
        updateSpinner(mProgressView);
    }

    public final void setPublicProfileSearchEnabled(boolean flag)
    {
        mPublicProfileSearchEnabled = flag;
        if(mAdapter != null)
            mAdapter.setPublicProfileSearchEnabled(flag);
    }

    public final void startSearch()
    {
        if(mSearchViewAdapter != null)
            mSearchViewAdapter.setQueryText(mQuery);
    }

    protected final void updateView(View view)
    {
        if(mAdapter != null)
            if(mAdapter.isSearchingForFirstResult())
            {
                view.findViewById(0x102000a).setVisibility(0);
                view.findViewById(R.id.shim).setVisibility(0);
                showEmptyViewProgress(view);
            } else
            if(!TextUtils.isEmpty(mQuery))
            {
                view.findViewById(0x102000a).setVisibility(0);
                view.findViewById(R.id.shim).setVisibility(8);
                showContent(view);
            } else
            {
                view.findViewById(0x102000a).setVisibility(8);
                view.findViewById(R.id.shim).setVisibility(8);
                showContent(view);
            }
    }
	
	public static interface OnSelectionChangeListener {

        public abstract void onCircleSelected(String s, CircleData circledata);

        public abstract void onPersonSelected(String s, String s1, PersonData persondata);
    }
}
