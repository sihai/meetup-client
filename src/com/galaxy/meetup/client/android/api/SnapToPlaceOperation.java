/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.galaxy.meetup.client.android.content.DbLocation;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPostsData;
import com.galaxy.meetup.client.android.network.PlusiOperation;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.util.PrimitiveUtils;
import com.galaxy.meetup.server.client.domain.LocationResult;
import com.galaxy.meetup.server.client.domain.request.SnapToPlaceRequest;
import com.galaxy.meetup.server.client.domain.response.SnapToPlaceResponse;
import com.galaxy.meetup.server.client.v2.request.Request;
import com.galaxy.meetup.server.client.v2.response.Response;

/**
 * 
 * @author sihai
 *
 */
public class SnapToPlaceOperation extends PlusiOperation {

	private DbLocation mCoarseLocation;
    private DbLocation mFirstPlace;
    private final boolean mIsPlaceSearch;
    private final LocationQuery mLocationQuery;
    private final DbLocation mOmitLocation;
    private DbLocation mPreciseLocation;
    private final boolean mStoreResult;
    
    public SnapToPlaceOperation(Context context, EsAccount esaccount, Intent intent, HttpOperation.OperationListener operationlistener, LocationQuery locationquery, DbLocation dblocation, boolean flag)
    {
        super(context, esaccount, "snaptoplace", intent, operationlistener, SnapToPlaceResponse.class);
        mLocationQuery = locationquery;
        mOmitLocation = dblocation;
        mStoreResult = flag;
        mIsPlaceSearch = mLocationQuery.hasQueryString();
    }
    
    public final DbLocation getCoarseLocation()
    {
        return mCoarseLocation;
    }

    public final DbLocation getPlaceLocation()
    {
        return mFirstPlace;
    }

    public final DbLocation getPreciseLocation()
    {
        return mPreciseLocation;
    }

    protected final void handleResponse(Response response) throws IOException {
        DbLocation dblocation = null;
        ArrayList arraylist;
        int i = 0;
        SnapToPlaceResponse snaptoplaceresponse = (SnapToPlaceResponse)response;
        if(snaptoplaceresponse.preciseLocation != null)
            mPreciseLocation = new DbLocation(1, snaptoplaceresponse.preciseLocation.location);
        if(snaptoplaceresponse.cityLocation != null)
            mCoarseLocation = new DbLocation(2, snaptoplaceresponse.cityLocation.location);
        List list = snaptoplaceresponse.localPlace;
        int j;
        DbLocation dblocation1;
        if(list == null)
            j = 0;
        else
            j = list.size();
        if(PrimitiveUtils.safeBoolean(snaptoplaceresponse.userIsAtFirstPlace) && j > 0)
            mFirstPlace = new DbLocation(3, ((LocationResult)list.get(0)).location);
        
        if(!mStoreResult) {
        	return; 
        }
        DbLocation dblocation2;
        DbLocation dblocation3;
        if(j > 0)
        {
            arraylist = new ArrayList(j);
            for(; i < j; i++)
            {
                dblocation1 = new DbLocation(3, ((LocationResult)list.get(i)).location);
                if(!dblocation1.isSamePlace(mOmitLocation))
                    arraylist.add(dblocation1);
            }

        } else
        {
            arraylist = null;
        }
        dblocation2 = mPreciseLocation;
        dblocation3 = mCoarseLocation;
        if(!mIsPlaceSearch) {
        	if(mOmitLocation != null)
            {
                if(mOmitLocation.isSamePlace(mPreciseLocation))
                    dblocation2 = null;
                if(mOmitLocation.isSamePlace(mCoarseLocation))
                {
                    dblocation = dblocation2;
                    dblocation3 = null;
                    EsPostsData.insertLocations(mContext, mAccount, mLocationQuery, dblocation, dblocation3, arraylist);
                    return;
                }
            }
            dblocation = dblocation2;
        } else {
        	dblocation3 = null;
        }
        EsPostsData.insertLocations(mContext, mAccount, mLocationQuery, dblocation, dblocation3, arraylist);
    }

    public final boolean hasCoarseLocation()
    {
        boolean flag;
        if(mCoarseLocation != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean hasPlaceLocation()
    {
        boolean flag;
        if(mFirstPlace != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean hasPreciseLocation()
    {
        boolean flag;
        if(mPreciseLocation != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    protected final Request populateRequest()
    {
        SnapToPlaceRequest snaptoplacerequest = new SnapToPlaceRequest();
        Location location = mLocationQuery.getLocation();
        snaptoplacerequest.latitudeE7 = Integer.valueOf((int)(10000000D * location.getLatitude()));
        snaptoplacerequest.longitudeE7 = Integer.valueOf((int)(10000000D * location.getLongitude()));
        if(location.hasAccuracy())
            snaptoplacerequest.precisionMeters = Double.valueOf(location.getAccuracy());
        if(mLocationQuery.hasQueryString())
            snaptoplacerequest.searchQuery = mLocationQuery.getQueryString();
        return snaptoplacerequest;
    }

}
