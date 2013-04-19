/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import WriteReviewOperation.MediaRef;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.Intents;
import com.galaxy.meetup.client.android.LocationController;
import com.galaxy.meetup.client.android.PlacesAdapter;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.analytics.OzViews;
import com.galaxy.meetup.client.android.api.LocationQuery;
import com.galaxy.meetup.client.android.content.AudienceData;
import com.galaxy.meetup.client.android.content.DbLocation;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.android.service.EsServiceListener;
import com.galaxy.meetup.client.android.service.ServiceResult;
import com.galaxy.meetup.client.android.ui.view.HostActionBar;
import com.galaxy.meetup.client.android.ui.view.ImageResourceView;
import com.galaxy.meetup.client.android.ui.view.SearchViewAdapter;
import com.galaxy.meetup.client.android.ui.view.SearchViewAdapter.OnQueryChangeListener;
import com.galaxy.meetup.client.util.Property;
import com.galaxy.meetup.client.util.ScreenMetrics;

/**
 * 
 * @author sihai
 *
 */
public class LocationPickerFragment extends HostedEsFragment implements
		LoaderCallbacks, OnScrollListener, OnItemClickListener,
		OnEditorActionListener, OnQueryChangeListener {

	private static final Object ITEM_KEEP_LOCATION = new Object();
    private PlacesAdapter mCurrentAdapter;
    private DbLocation mCurrentLocation;
    private String mCurrentMapUrl;
    private boolean mIsLandscapeMode;
    protected ListView mListView;
    private boolean mLoadPlacesNeeded;
    private boolean mLoadSearchNeeded;
    private LocationController mLocationController;
    private CheckinLocationListener mLocationListener;
    private LocationQuery mLocationQuery;
    private ImageResourceView mMapView;
    private PlacesAdapter mPlacesAdapter;
    private int mPrevScrollItemCount;
    private int mPrevScrollPosition;
    private String mQuery;
    private int mScrollOffset;
    private int mScrollPos;
    private PlacesAdapter mSearchAdapter;
    private boolean mSearchMode;
    private final EsServiceListener mServiceListener = new ServiceListener();
    
    public LocationPickerFragment()
    {
        mPrevScrollPosition = -1;
        mPrevScrollItemCount = -1;
        mLocationListener = new CheckinLocationListener();
    }

    private String createStaticMapUrl(Location location, boolean flag)
    {
        int i = getResources().getDimensionPixelSize(R.dimen.location_picker_map_size);
        int j = Math.max(ScreenMetrics.getInstance(getActivity()).shortDimension, i);
        android.net.Uri.Builder builder = Uri.parse("https://maps.googleapis.com/maps/api/staticmap").buildUpon();
        android.net.Uri.Builder builder1 = builder.appendQueryParameter("zoom", String.valueOf(18));
        Object aobj[] = new Object[2];
        aobj[0] = Integer.valueOf(j);
        aobj[1] = Integer.valueOf(j);
        android.net.Uri.Builder builder2 = builder1.appendQueryParameter("size", String.format("%dx%d", aobj)).appendQueryParameter("format", "png").appendQueryParameter("maptype", "roadmap").appendQueryParameter("sensor", String.valueOf(true));
        Object aobj1[] = new Object[3];
        String s;
        String s1;
        if(flag)
            s = "red";
        else
            s = "blue";
        aobj1[0] = s;
        aobj1[1] = Double.valueOf(location.getLatitude());
        aobj1[2] = Double.valueOf(location.getLongitude());
        builder2.appendQueryParameter("markers", String.format("color:%s|%.6f,%.6f", aobj1));
        s1 = Property.PLUS_STATICMAPS_API_KEY.get();
        if(!TextUtils.isEmpty(s1))
            builder.appendQueryParameter("key", s1);
        return builder.build().toString();
    }

    private void doSearch()
    {
        if(mLocationQuery != null)
        {
            if(!TextUtils.isEmpty(mQuery))
                mLocationQuery = new LocationQuery(mLocationQuery.getLocation(), mQuery);
            else
                mLocationQuery = new LocationQuery(mLocationQuery.getLocation(), null);
            mLoadPlacesNeeded = false;
            mLoadSearchNeeded = true;
            mNewerReqId = Integer.valueOf(EsService.getNearbyLocations(getActivity(), mAccount, mLocationQuery, mCurrentLocation));
            showProgress(getView(), getString(R.string.loading));
            getLoaderManager().restartLoader(1, null, this);
        }
    }

    private boolean isSearchWithNoEntry()
    {
        boolean flag;
        if(mSearchMode && TextUtils.isEmpty(mQuery))
            flag = true;
        else
            flag = false;
        return flag;
    }

    private void removeLocationListener()
    {
        if(mLocationController != null)
        {
            mLocationController.release();
            mLocationController = null;
        }
    }

    private void sendResult(DbLocation dblocation)
    {
        FragmentActivity fragmentactivity = getActivity();
        Intent intent = new Intent();
        intent.putExtra("location", dblocation);
        fragmentactivity.setResult(-1, intent);
        fragmentactivity.finish();
    }

    private void setupAndShowEmptyView(View view)
    {
        Resources resources = getResources();
        String s;
        if(isSearchWithNoEntry())
            s = resources.getString(R.string.enter_location_name);
        else
            s = resources.getString(R.string.no_locations);
        showEmptyView(view, s);
    }

    private void showProgress(View view, String s)
    {
        if(isSearchWithNoEntry())
            setupAndShowEmptyView(view);
        else
            showEmptyViewProgress(view, s);
    }

    private void updateView()
    {
        byte byte0;
        if(mSearchMode && !mIsLandscapeMode)
            byte0 = 8;
        else
            byte0 = 0;
        mMapView.setVisibility(byte0);
        if(mCurrentAdapter != null && mCurrentAdapter.getCursor() != null && mCurrentAdapter.getCursor().getCount() > 0)
            showContent(getView());
        else
        if(mNewerReqId != null)
            showProgress(getView(), getString(R.string.loading));
        else
            setupAndShowEmptyView(getView());
    }

    public final OzViews getViewForLogging()
    {
        return OzViews.LOCATION_PICKER;
    }

    protected final boolean isEmpty()
    {
        boolean flag;
        if(mCurrentAdapter == null || mCurrentAdapter.getCursor() == null || mCurrentAdapter.getCount() == 0)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void onActionButtonClicked(int i)
    {
        if(i == 0)
            setSearchMode(true);
    }

    public final boolean onBackPressed()
    {
        boolean flag;
        if(mSearchMode)
        {
            setSearchMode(false);
            flag = true;
        } else
        {
            flag = super.onBackPressed();
        }
        return flag;
    }

    public final void onCreate(Bundle bundle)
    {
        Intent intent;
        super.onCreate(bundle);
        intent = getActivity().getIntent();
        mAccount = (EsAccount)intent.getParcelableExtra("account");
        if(bundle == null) {
        	 mScrollPos = 0;
             mScrollOffset = 0;
             if(intent.hasExtra("location"))
             {
                 DbLocation dblocation = (DbLocation)intent.getParcelableExtra("location");
                 mLocationQuery = new LocationQuery(dblocation.getAndroidLocation(), null);
                 mCurrentLocation = dblocation;
             } 
        } else { 
        	 mLocationQuery = (LocationQuery)bundle.getParcelable("location");
             mCurrentLocation = (DbLocation)bundle.getParcelable("current_location");
             mSearchMode = bundle.getBoolean("search_mode");
             mQuery = bundle.getString("query");
             mCurrentMapUrl = bundle.getString("current_map_url");
             mScrollPos = bundle.getInt("scroll_pos");
             mScrollOffset = bundle.getInt("scroll_off");
             if(mLocationQuery != null)
             {
                 LoaderManager loadermanager = getLoaderManager();
                 loadermanager.restartLoader(0, null, this);
                 if(mSearchMode)
                     loadermanager.restartLoader(1, null, this);
             }
        }
        
        invalidateActionBar();
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        LocationQuery locationquery;
        String s;
        Uri uri;
        String s1;
        if(i == 0)
            locationquery = new LocationQuery(mLocationQuery.getLocation(), null);
        else
            locationquery = mLocationQuery;
        if(isSearchWithNoEntry())
            s = "no_location_stream_key";
        else
            s = locationquery.getKey();
        uri = EsProvider.buildLocationQueryUri(mAccount, s);
        if(getActivity().getIntent().getBooleanExtra("places_only", false))
            s1 = "name IS NOT NULL";
        else
            s1 = null;
        return new EsCursorLoader(getActivity(), uri, PlacesAdapter.LocationQuery.PROJECTION, s1, null, null);
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.checkin_list, viewgroup, false);
        boolean flag;
        boolean flag1;
        PlacesAdapter placesadapter;
        if(getActivity().getResources().getConfiguration().orientation == 2)
            flag = true;
        else
            flag = false;
        mIsLandscapeMode = flag;
        mListView = (ListView)view.findViewById(0x102000a);
        mListView.setOnScrollListener(this);
        mMapView = (ImageResourceView)view.findViewById(R.id.map);
        mMapView.setImageResourceFlags(1);
        if(mCurrentLocation != null)
        {
            View view1 = getActivity().getLayoutInflater().inflate(R.layout.location_row_layout, mListView, false);
            ((ImageView)view1.findViewById(0x1020006)).setImageResource(R.drawable.ic_location_active);
            TextView textview = (TextView)view1.findViewById(0x1020016);
            TextView textview1 = (TextView)view1.findViewById(0x1020005);
            String s;
            View view2;
            if(mCurrentLocation.isPrecise())
            {
                textview.setText(R.string.my_location);
                s = mCurrentLocation.getLocationName();
            } else
            if(mCurrentLocation.isCoarse())
            {
                textview.setText(R.string.my_city);
                s = mCurrentLocation.getLocationName();
            } else
            {
                textview.setText(mCurrentLocation.getName());
                s = mCurrentLocation.getBestAddress();
            }
            textview1.setText(s);
            view2 = view1.findViewById(R.id.remove_button);
            view2.setVisibility(0);
            view2.setOnClickListener(new android.view.View.OnClickListener() {

                public final void onClick(View view3)
                {
                    sendResult(null);
                }
            });
            mListView.addHeaderView(view1, ITEM_KEEP_LOCATION, true);
            mCurrentMapUrl = createStaticMapUrl(mCurrentLocation.getAndroidLocation(), true);
            flag1 = true;
        } else
        if(!TextUtils.isEmpty(mCurrentMapUrl))
            flag1 = true;
        else
            flag1 = false;
        if(flag1)
            mMapView.setMediaRef(new MediaRef(mCurrentMapUrl, MediaRef.MediaType.IMAGE), true);
        else
            mMapView.setVisibility(8);
        if(mSearchMode && !mIsLandscapeMode)
            mMapView.setVisibility(8);
        mPlacesAdapter = new PlacesAdapter(getActivity());
        mSearchAdapter = new PlacesAdapter(getActivity());
        if(mSearchMode)
            placesadapter = mSearchAdapter;
        else
            placesadapter = mPlacesAdapter;
        mCurrentAdapter = placesadapter;
        mListView.setAdapter(mCurrentAdapter);
        setupEmptyView(view, R.string.no_locations);
        mListView.setOnItemClickListener(this);
        return view;
    }

    public final void onDestroyView()
    {
        super.onDestroyView();
        if(mListView != null)
        {
            mListView.setOnScrollListener(null);
            mListView = null;
        }
    }

    public boolean onEditorAction(TextView textview, int i, KeyEvent keyevent)
    {
        boolean flag;
        if(i == 3)
        {
            doSearch();
            flag = true;
        } else
        {
            flag = false;
        }
        return flag;
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l) {
        Object obj = adapterview.getItemAtPosition(i);
        if(null == obj) {
        	return;
        }
        
        Cursor cursor;
        DbLocation dblocation;
        cursor = mCurrentAdapter.getCursor();
        if(obj == ITEM_KEEP_LOCATION) {
        	dblocation = mCurrentLocation;
        } else if(obj == cursor) {
        	dblocation = PlacesAdapter.getLocation(cursor);
        } else {
        	return;
        }
        
        FragmentActivity fragmentactivity = getActivity();
        Intent intent = fragmentactivity.getIntent();
        if(!"android.intent.action.PICK".equals(intent.getAction()))
        {
            Intent intent1 = Intents.getPostActivityIntent(fragmentactivity, mAccount, dblocation);
            AudienceData audiencedata = (AudienceData)intent.getParcelableExtra("audience");
            if(audiencedata != null)
                intent1.putExtra("audience", audiencedata);
            startActivity(intent1);
        }
        sendResult(dblocation);
        
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        boolean flag;
        if(loader.getId() == 0)
        {
            mPlacesAdapter.swapCursor(cursor);
            if(!mSearchMode)
                flag = true;
            else
                flag = false;
        } else
        {
            mSearchAdapter.swapCursor(cursor);
            flag = mSearchMode;
        }
        if(flag)
        {
            getView();
            updateView();
        }
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onPause()
    {
        super.onPause();
        if(mPlacesAdapter != null && mPlacesAdapter.getCursor() != null)
        {
            PlacesAdapter _tmp = mPlacesAdapter;
            PlacesAdapter.onPause();
        }
        if(mSearchAdapter != null && mSearchAdapter.getCursor() != null)
        {
            PlacesAdapter _tmp1 = mSearchAdapter;
            PlacesAdapter.onPause();
        }
        EsService.unregisterListener(mServiceListener);
        removeLocationListener();
    }

    protected final void onPrepareActionBar(HostActionBar hostactionbar) {
        super.onPrepareActionBar(hostactionbar);
        if(!mSearchMode) {
        	hostactionbar.showTitle(R.string.post_checkin_title);
            boolean flag;
            if(mLocationQuery != null)
                flag = true;
            else
                flag = false;
            if(flag)
                hostactionbar.showActionButton(0, R.drawable.ic_menu_search, R.string.menu_search);
        } else { 
        	hostactionbar.showSearchView();
            SearchViewAdapter searchviewadapter = hostactionbar.getSearchViewAdapter();
            searchviewadapter.setQueryHint(R.string.search_location_hint_text);
            searchviewadapter.addOnChangeListener(this);
        }
    }

    public final void onQueryClose()
    {
        getActionBar().getSearchViewAdapter().setQueryText(null);
    }

    public final void onQueryTextChanged(CharSequence charsequence)
    {
        String s;
        if(charsequence == null)
            s = null;
        else
            s = charsequence.toString().trim();
        mQuery = s;
        doSearch();
    }

    public final void onQueryTextSubmitted(CharSequence charsequence)
    {
    }

    public final void onResume()
    {
        super.onResume();
        if(mPlacesAdapter != null && mPlacesAdapter.getCursor() != null)
            mPlacesAdapter.onResume();
        if(mSearchAdapter != null && mSearchAdapter.getCursor() != null)
            mSearchAdapter.onResume();
        EsService.registerListener(mServiceListener);
        if(mCurrentLocation != null)
        {
            if(mCurrentAdapter.getCount() == 0)
            {
                showProgress(getView(), getString(R.string.loading));
                mLoadPlacesNeeded = true;
                mLoadSearchNeeded = false;
                mNewerReqId = Integer.valueOf(EsService.getNearbyLocations(getActivity(), mAccount, mLocationQuery, mCurrentLocation));
            }
        } else
        {
            if(mLocationController == null)
            {
                FragmentActivity fragmentactivity = getActivity();
                EsAccount esaccount = mAccount;
                Location location;
                if(mLocationQuery != null)
                    location = mLocationQuery.getLocation();
                else
                    location = null;
                mLocationController = new LocationController(fragmentactivity, esaccount, true, 3000L, location, mLocationListener);
            }
            if(!mLocationController.isProviderEnabled())
                getActivity().showDialog(0x1bfb7a8);
            else
                mLocationController.init();
            if(!mLocationController.isProviderEnabled())
                setupAndShowEmptyView(getView());
            else
            if(mLocationQuery == null)
                showProgress(getView(), getString(R.string.finding_your_location));
            else
                showProgress(getView(), getString(R.string.loading));
        }
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        if(!getActivity().isFinishing() && mListView != null)
        {
            if(mListView != null)
            {
                mScrollPos = mListView.getFirstVisiblePosition();
                if(mCurrentAdapter != null)
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
        if(mLocationQuery != null)
        {
            bundle.putParcelable("location", mLocationQuery);
            bundle.putBoolean("search_mode", mSearchMode);
        }
        if(mCurrentLocation != null)
            bundle.putParcelable("current_location", mCurrentLocation);
        bundle.putString("current_map_url", mCurrentMapUrl);
        bundle.putString("query", mQuery);
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

    public final boolean onUpButtonClicked()
    {
        boolean flag;
        if(mSearchMode)
        {
            setSearchMode(false);
            flag = true;
        } else
        {
            flag = super.onUpButtonClicked();
        }
        return flag;
    }

    public final void setSearchMode(boolean flag)
    {
        if(flag != mSearchMode)
        {
            mSearchMode = flag;
            PlacesAdapter placesadapter;
            if(mSearchMode)
                placesadapter = mSearchAdapter;
            else
                placesadapter = mPlacesAdapter;
            mCurrentAdapter = placesadapter;
            mListView.setAdapter(mCurrentAdapter);
            getActionBar().getSearchViewAdapter().setQueryText(null);
            if(flag)
                doSearch();
            else
                getLoaderManager().restartLoader(0, null, this);
            invalidateActionBar();
            getView();
            updateView();
        }
    }
    
    
    private final class CheckinLocationListener implements LocationListener {

	    public final void onLocationChanged(Location location)
	    {
	        boolean flag = mLocationController.isProviderEnabled();
	        removeLocationListener();
	        boolean flag1 = false;
	        boolean flag2;
	        if(!TextUtils.isEmpty(mQuery))
	        {
	            mLocationQuery = new LocationQuery(location, mQuery);
	            flag2 = true;
	        } else
	        {
	            mLocationQuery = new LocationQuery(location, null);
	            flag1 = true;
	            flag2 = false;
	        }
	        invalidateActionBar();
	        if(flag)
	        {
	            showProgress(getView(), getString(R.string.loading));
	            mLoadPlacesNeeded = flag1;
	            mLoadSearchNeeded = flag2;
	            mNewerReqId = Integer.valueOf(EsService.getNearbyLocations(getActivity(), mAccount, mLocationQuery, mCurrentLocation));
	        }
	        if(mMapView != null && mCurrentLocation == null)
	        {
	            mCurrentMapUrl = createStaticMapUrl(location, false);
	            mMapView.setMediaRef(new MediaRef(mCurrentMapUrl, MediaRef.MediaType.IMAGE), false);
	            updateView();
	        }
	    }

	    public final void onProviderDisabled(String s)
	    {
	    }
	
	    public final void onProviderEnabled(String s)
	    {
	    }
	
	    public final void onStatusChanged(String s, int i, Bundle bundle)
	    {
	    }

    }

    private final class ServiceListener extends EsServiceListener {

	    public final void onLocationQuery(int i, ServiceResult serviceresult) {
	        if(mNewerReqId != null && mNewerReqId.intValue() == i)
	        {
	            mNewerReqId = null;
	            if(serviceresult.hasError())
	                Toast.makeText(getActivity(), R.string.checkin_places_error, 0).show();
	            LoaderManager loadermanager = getLoaderManager();
	            if(mLoadPlacesNeeded)
	            {
	                mLoadPlacesNeeded = false;
	                loadermanager.restartLoader(0, null, LocationPickerFragment.this);
	            }
	            if(mLoadSearchNeeded)
	            {
	                mLoadSearchNeeded = false;
	                loadermanager.restartLoader(1, null, LocationPickerFragment.this);
	            }
	        }
	    }
    }

}
