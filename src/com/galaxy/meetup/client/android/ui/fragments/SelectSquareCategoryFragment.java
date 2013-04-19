/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.DbSquareStream;
import com.galaxy.meetup.client.android.content.SquareTargetData;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;

/**
 * 
 * @author sihai
 *
 */
public class SelectSquareCategoryFragment extends HostedEsFragment implements
		LoaderCallbacks, OnItemClickListener {

	private ArrayAdapter mAdapter;
    private ListView mListView;
    private boolean mLoaderError;
    private final EsServiceListener mServiceListener = new EsServiceListener() {

        public final void onGetSquareComplete(int i, ServiceResult serviceresult) {
            if(mNewerReqId != null && i == mNewerReqId.intValue()) {
            	mNewerReqId = null;
                if(!serviceresult.hasError() || mLoaderError) {
                	if(null != getActivity() && !getActivity().isFinishing())
                		getLoaderManager().restartLoader(0, null, SelectSquareCategoryFragment.this);
                } else {
                	Toast.makeText(getActivity(), getString(R.string.people_list_error), 0).show();
                }
                updateSpinner();
            }
        }
    };
    private String mSquareId;
    private String mSquareName;
    private boolean mSquareStreamLoaderActive;
    
    public SelectSquareCategoryFragment()
    {
        mSquareStreamLoaderActive = true;
    }

    private boolean isLoading()
    {
        boolean flag;
        if(mAdapter == null || mAdapter.getCount() == 0)
            flag = true;
        else
            flag = false;
        return flag;
    }

    private void updateView(View view)
    {
        View view1 = view.findViewById(0x102000a);
        View view2 = view.findViewById(R.id.server_error);
        if(mLoaderError)
        {
            view1.setVisibility(8);
            view2.setVisibility(0);
            showContent(view);
        } else
        if(isLoading())
        {
            view1.setVisibility(8);
            view2.setVisibility(8);
            showEmptyViewProgress(view);
        } else
        if(isEmpty())
        {
            view1.setVisibility(8);
            view2.setVisibility(8);
            showEmptyView(view, getString(R.string.no_squares));
        } else
        {
            view1.setVisibility(0);
            view2.setVisibility(8);
            showContent(view);
        }
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.PEOPLE_PICKER;
    }

    protected final boolean isEmpty()
    {
        boolean flag;
        if(isLoading() || mAdapter.isEmpty())
            flag = true;
        else
            flag = false;
        return flag;
    }

    protected final boolean isProgressIndicatorVisible()
    {
        boolean flag;
        if(super.isProgressIndicatorVisible() || mSquareStreamLoaderActive)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mAdapter = new ArrayAdapter(activity, 0x1090003);
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        Intent intent = getActivity().getIntent();
        mSquareId = intent.getStringExtra("square_id");
        mSquareName = intent.getStringExtra("square_name");
        getLoaderManager().initLoader(0, null, this);
    }

    public final Loader onCreateLoader(int i, Bundle bundle) {
    	Loader loader = null;
    	if(0 == i) {
        	loader = new SquareCategoryLoader(getActivity(), getAccount(), mSquareId);
        }
    	return loader;
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.edit_audience_fragment, viewgroup, false);
        mListView = (ListView)view.findViewById(0x102000a);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setFastScrollEnabled(false);
        return view;
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        SquareStreamInfo squarestreaminfo = (SquareStreamInfo)mAdapter.getItem(i);
        String s = squarestreaminfo.getStreamId();
        String s1 = squarestreaminfo.getStreamName();
        AudienceData audiencedata = new AudienceData(new SquareTargetData(mSquareId, mSquareName, s, s1));
        Intent intent = new Intent();
        intent.putExtra("audience", audiencedata);
        FragmentActivity fragmentactivity = getActivity();
        fragmentactivity.setResult(-1, intent);
        fragmentactivity.finish();
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        boolean flag;
        int i;
        DbSquareStream adbsquarestream[];
        flag = true;
        i = 0;
        adbsquarestream = (DbSquareStream[])obj;
        boolean flag1;
        if(adbsquarestream == null)
            flag1 = flag;
        else
            flag1 = false;
        mLoaderError = flag1;
        int id = loader.getId();
        if(0 == id) {
        	mSquareStreamLoaderActive = false;
            if((loader instanceof SquareCategoryLoader) && ((SquareCategoryLoader)loader).isDataStale())
                refresh();
            if(mLoaderError) {
            	updateView(getView());
            	return;
            } else {
            	boolean flag2;
                int j = mAdapter.getCount();
                int k = adbsquarestream.length;
                DbSquareStream dbsquarestream;
                if(k != j)
                    flag2 = flag;
                else
                    flag2 = false;
                if(flag2) {
                	flag = flag2; 
                } else { 
                	for(int l = 0; l < k; l++) {
                		if(!TextUtils.equals(adbsquarestream[l].getStreamId(), ((SquareStreamInfo)mAdapter.getItem(l)).getStreamId())) {
                			if(flag)
                	        {
                	            mAdapter.clear();
                	            for(; i < k; i++)
                	            {
                	                dbsquarestream = adbsquarestream[i];
                	                mAdapter.add(new SquareStreamInfo(dbsquarestream.getName(), dbsquarestream.getStreamId()));
                	            }

                	        }
                			updateView(getView());
                			break;
                		}
                	}
                }
            }
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final boolean onOptionsItemSelected(MenuItem menuitem)
    {
        boolean flag;
        if(menuitem.getItemId() == R.id.refresh)
        {
            refresh();
            flag = true;
        } else
        {
            flag = super.onOptionsItemSelected(menuitem);
        }
        return flag;
    }

    public final void onPause()
    {
        super.onPause();
        EsService.unregisterListener(mServiceListener);
    }

    protected final void onPrepareActionBar(HostActionBar hostactionbar)
    {
        hostactionbar.showTitle(getActivity().getIntent().getStringExtra("title"));
        hostactionbar.showRefreshButton();
    }

    public final void onResume()
    {
        super.onResume();
        EsService.registerListener(mServiceListener);
        updateView(getView());
    }

    public final void refresh()
    {
        super.refresh();
        if(mNewerReqId == null && getActivity() != null)
            mNewerReqId = Integer.valueOf(EsService.getSquare(getActivity(), mAccount, mSquareId));
        updateSpinner();
    }
    
    //==================================================================================================================
    //									Inner class
    //==================================================================================================================
    private static final class SquareStreamInfo {
    	private final String mStreamId;
        private final String mStreamName;

        public SquareStreamInfo(String s, String s1)
        {
            mStreamId = s1;
            mStreamName = s;
        }
        
        public final String getStreamId()
        {
            return mStreamId;
        }

        public final String getStreamName()
        {
            return mStreamName;
        }

        public final String toString()
        {
            return mStreamName;
        }

    }
}
