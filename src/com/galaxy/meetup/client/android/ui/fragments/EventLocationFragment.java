/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxy.meetup.client.android.EsCursorAdapter;
import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.EsMatrixCursor;
import com.galaxy.meetup.client.android.LocationController;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.api.LocationQuery;
import com.galaxy.meetup.client.android.content.DbLocation;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.service.EsService;
import com.galaxy.meetup.client.util.LocationUtils;
import com.galaxy.meetup.client.util.SoftInput;
import com.galaxy.meetup.server.client.domain.Place;

/**
 * 
 * @author sihai
 *
 */
public class EventLocationFragment extends EsListFragment implements
		LoaderCallbacks, TextWatcher, OnItemClickListener {

	private static final String LOCATION_PROJECTION[] = {
        "_id", "type", "title", "description", "location"
    };
    private double mCurrentLatitude;
    private double mCurrentLongitude;
    private String mInitialQuery;
    private OnLocationSelectedListener mListener;
    private LocationController mLocationController;
    private LocationListener mLocationListener;
    private LocationQuery mLocationQuery;
    private EditText mLocationText;
    private String mQuery;
    
    public EventLocationFragment()
    {
        mLocationListener = new LocationListener() {

            public final void onLocationChanged(Location location)
            {
                removeLocationListener();
                EventLocationFragment.access$100(EventLocationFragment.this, location.getLatitude(), location.getLongitude());
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
        };
    }

    private void buildLocationQuery()
    {
        if(!TextUtils.isEmpty(mQuery) && isCurrentLocationKnown())
        {
            Location location = new Location((String)null);
            location.setLatitude(mCurrentLatitude);
            location.setLongitude(mCurrentLongitude);
            mLocationQuery = new LocationQuery(location, mQuery);
        } else
        {
            mLocationQuery = null;
        }
    }

    private EsAccount getAccount()
    {
        return (EsAccount)getActivity().getIntent().getExtras().get("account");
    }

    private boolean isCurrentLocationKnown()
    {
        boolean flag;
        if(mCurrentLatitude != 0.0D && mCurrentLongitude != 0.0D)
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

    private void runQuery()
    {
        if(mLocationQuery != null)
        {
            getLoaderManager().restartLoader(0, null, this);
            EsService.getNearbyLocations(getActivity(), getAccount(), mLocationQuery);
        }
    }

    private void updateAdapter(Cursor cursor) {
        EsMatrixCursor esmatrixcursor = new EsMatrixCursor(LOCATION_PROJECTION);
        if(TextUtils.isEmpty(mQuery)) {
        	Object aobj3[] = new Object[5];
            aobj3[0] = Integer.valueOf(1);
            aobj3[1] = Integer.valueOf(0);
            aobj3[2] = getString(R.string.event_location_none_title);
            aobj3[3] = getString(R.string.event_location_none_description);
            aobj3[4] = null;
            esmatrixcursor.addRow(aobj3); 
        } else {
        	Object aobj[] = new Object[5];
            int i = 1 + 1;
            aobj[0] = Integer.valueOf(1);
            aobj[1] = Integer.valueOf(1);
            int j = R.string.event_location_add;
            Object aobj1[] = new Object[1];
            aobj1[0] = mQuery;
            aobj[2] = getString(j, aobj1);
            aobj[3] = null;
            aobj[4] = null;
            esmatrixcursor.addRow(aobj);
            if(cursor != null && cursor.moveToFirst())
                do
                {
                    byte abyte0[] = cursor.getBlob(0);
                    DbLocation dblocation = DbLocation.deserialize(abyte0);
                    if(dblocation != null)
                    {
                        Object aobj2[] = new Object[5];
                        int k = i + 1;
                        aobj2[0] = Integer.valueOf(i);
                        aobj2[1] = Integer.valueOf(2);
                        aobj2[2] = dblocation.getName();
                        aobj2[3] = dblocation.getBestAddress();
                        aobj2[4] = abyte0;
                        esmatrixcursor.addRow(aobj2);
                        i = k;
                    }
                } while(cursor.moveToNext());
        }
        mAdapter.swapCursor(esmatrixcursor);
    }

    public void afterTextChanged(Editable editable)
    {
    }

    public void beforeTextChanged(CharSequence charsequence, int i, int j, int k)
    {
    }

    public final void onAttach(Activity activity)
    {
        super.onAttach(activity);
        if(!isCurrentLocationKnown())
        {
            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if(sharedpreferences.contains("event.current.latitude"))
            {
                mCurrentLatitude = Double.parseDouble(sharedpreferences.getString("event.current.latitude", "0"));
                mCurrentLongitude = Double.parseDouble(sharedpreferences.getString("event.current.longitude", "0"));
            }
        }
    }

    public final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(bundle != null)
        {
            mQuery = bundle.getString("query");
            mCurrentLatitude = bundle.getDouble("latitude");
            mCurrentLongitude = bundle.getDouble("longitude");
            buildLocationQuery();
        }
        getLoaderManager().initLoader(0, null, this);
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        String s;
        android.net.Uri uri;
        if(mLocationQuery == null)
            s = "no_location_stream_key";
        else
            s = mLocationQuery.getKey();
        uri = EsProvider.buildLocationQueryUri(getAccount(), s);
        return new EsCursorLoader(getActivity(), uri, new String[] {
            "location"
        }, null, null, null);
    }

    public final View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = super.onCreateView(layoutinflater, viewgroup, bundle, R.layout.event_location_fragment);
        mAdapter = new EventLocationAdapter(getActivity());
        ((ListView)mListView).setAdapter(mAdapter);
        ((ListView)mListView).setOnItemClickListener(this);
        mLocationText = (EditText)view.findViewById(R.id.location_text);
        mLocationText.addTextChangedListener(this);
        mLocationText.setText(mInitialQuery);
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

    public void onItemClick(AdapterView adapterview, View view, int i, long l) {
        Cursor cursor = (Cursor)mAdapter.getItem(i);
        int value = cursor.getInt(1);
        Place place = null;
        if(0 == value) {
        	place = null;
        } else if (1 == value) {
        	place = new Place();
            place.name = mQuery;
        } else {
        	byte abyte0[] = cursor.getBlob(4);
            if(abyte0 != null)
                place = LocationUtils.convertLocationToPlace(DbLocation.deserialize(abyte0).toProtocolObject());
            else
                place = null;
        }
        
        SoftInput.hide(getView());
        if(mListener != null)
            mListener.onLocationSelected(place);
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        updateAdapter((Cursor)obj);
    }

    public final void onLoaderReset(Loader loader)
    {
    }

    public final void onPause()
    {
        super.onPause();
        removeLocationListener();
    }

    public final void onResume()
    {
        super.onResume();
        if(mLocationController == null)
            mLocationController = new LocationController(getActivity(), getAccount(), true, 3000L, null, mLocationListener);
        if(mLocationController.isProviderEnabled())
            mLocationController.init();
    }

    public final void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putString("query", mQuery);
        bundle.putDouble("latitude", mCurrentLatitude);
        bundle.putDouble("longitude", mCurrentLongitude);
    }

    public void onScroll(AbsListView abslistview, int i, int j, int k)
    {
        super.onScroll(abslistview, i, j, k);
    }

    public void onScrollStateChanged(AbsListView abslistview, int i)
    {
        super.onScrollStateChanged(abslistview, i);
    }

    public void onTextChanged(CharSequence charsequence, int i, int j, int k)
    {
        String s = mLocationText.getText().toString().trim();
        if(!TextUtils.equals(mQuery, s))
        {
            mQuery = s;
            if(isAdded())
            {
                updateAdapter(null);
                buildLocationQuery();
                runQuery();
            }
        }
    }

    public final void setInitialQueryString(String s)
    {
        mInitialQuery = s;
    }

    public final void setOnLocationSelectedListener(OnLocationSelectedListener onlocationselectedlistener)
    {
        mListener = onlocationselectedlistener;
    }
    
    static void access$100(EventLocationFragment eventlocationfragment, double d, double d1)
    {
        if(eventlocationfragment.isCurrentLocationKnown())
        {
            float af[] = new float[1];
            Location.distanceBetween(eventlocationfragment.mCurrentLatitude, eventlocationfragment.mCurrentLongitude, d, d1, af);
            if(af[0] < 200F) {
                return;
            }
        }
        eventlocationfragment.mCurrentLatitude = d;
        eventlocationfragment.mCurrentLongitude = d1;
        android.content.SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(eventlocationfragment.getActivity()).edit();
        editor.putString("event.current.latitude", Double.toString(eventlocationfragment.mCurrentLatitude));
        editor.putString("event.current.longitude", Double.toString(eventlocationfragment.mCurrentLongitude));
        if(android.os.Build.VERSION.SDK_INT >= 9)
            editor.apply();
        else
            editor.commit();
        eventlocationfragment.buildLocationQuery();
        eventlocationfragment.runQuery();
    }
    
    //==================================================================================================================
    //									Inner class
    //==================================================================================================================
    private static final class EventLocationAdapter extends EsCursorAdapter {

        public final void bindView(View view, Context context, Cursor cursor)
        {
            ImageView imageview;
            TextView textview;
            TextView textview1;
            imageview = (ImageView)view.findViewById(0x1020006);
            textview = (TextView)view.findViewById(0x1020016);
            textview1 = (TextView)view.findViewById(0x1020005);
            int value = cursor.getInt(1);
            if(0 == value) {
            	imageview.setVisibility(0);
                imageview.setImageResource(R.drawable.ic_location_none);
            } else if(1 == value) {
            	imageview.setVisibility(8);
            } else if(2 == value) {
            	imageview.setVisibility(0);
                imageview.setImageResource(R.drawable.ic_location_grey);
            }
            
            textview.setText(cursor.getString(2));
            String s = cursor.getString(3);
            if(!TextUtils.isEmpty(s))
            {
                textview1.setVisibility(0);
                textview1.setText(s);
            } else
            {
                textview1.setVisibility(8);
            }
            
        }

        public final View newView(Context context, Cursor cursor, ViewGroup viewgroup)
        {
            return LayoutInflater.from(context).inflate(R.layout.location_row_layout, viewgroup, false);
        }

        public EventLocationAdapter(Context context)
        {
            super(context, null);
        }
    }

    public static interface OnLocationSelectedListener
    {

        public abstract void onLocationSelected(Place place);
    }
}
