/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.galaxy.meetup.client.android.api.LocationQuery;
import com.galaxy.meetup.client.android.api.SnapToPlaceOperation;
import com.galaxy.meetup.client.android.content.DbLocation;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.GoogleLocationSettingHelper;
import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public class LocationController {

	private final EsAccount mAccount;
    private final Context mContext;
    private final boolean mDisplayDebugToast;
    private LocationListener mGpsListener;
    private final Handler mHandler;
    private Location mLastSentLocation;
    private LocationListener mLastSuccessfulLocationListener;
    private LocationListener mListener;
    private Location mLocation;
    private Runnable mLocationAcquisitionTimer;
    private final LocationManager mLocationManager;
    private LocationListener mNetworkListener;
    private final boolean mReverseGeo;
    private final long mTimeout;
	
    public LocationController(Context context, EsAccount esaccount, boolean flag, long l, Location location, LocationListener locationlistener) {
        mContext = context;
        mAccount = esaccount;
        mListener = locationlistener;
        mHandler = new Handler();
        mReverseGeo = true;
        mLocation = location;
        mTimeout = 3000L;
        mLocationManager = (LocationManager)context.getSystemService("location");
        List list = mLocationManager.getProviders(true);
        int i = list.size();
        for(int j = 0; j < i; j++) {
        	// FIXME only better one ? 
        	if("network".equals(list.get(j))) {
        		mNetworkListener = new LocalLocationListener();
                mLocationManager.requestLocationUpdates("network", 3000L, 0.0F, mNetworkListener);
        	}
        	if("gps".equals(list.get(j))) {
        		mGpsListener = new LocalLocationListener();
                mLocationManager.requestLocationUpdates("gps", 3000L, 0.0F, mGpsListener);
        	}
        }
        
        mDisplayDebugToast = Property.LOCATION_DEBUGGING.get().equalsIgnoreCase("TRUE");
        return;
    }

    public static boolean areSameLocations(Location location, Location location1)
    {
        boolean flag;
        if(location.getLatitude() == location1.getLatitude() && location.getLongitude() == location1.getLongitude() && (float)(int)location.getAccuracy() - location1.getAccuracy() == 0.0F)
            flag = true;
        else
            flag = false;
        return flag;
    }

    private boolean canFindLocation()
    {
        boolean flag;
        if(mLocationManager.isProviderEnabled("network") || mLocationManager.isProviderEnabled("gps"))
            flag = true;
        else
            flag = false;
        return flag;
    }

    private static boolean canFindLocation(Context context)
    {
        LocationManager locationmanager = (LocationManager)context.getSystemService("location");
        boolean flag;
        if(locationmanager.isProviderEnabled("network") || locationmanager.isProviderEnabled("gps"))
            flag = true;
        else
            flag = false;
        return flag;
    }

    public static DbLocation getCityLevelLocation(Location location)
    {
        return (DbLocation)getExtras(location).getParcelable("coarse_location");
    }

    private static Bundle getExtras(Location location)
    {
        Bundle bundle = location.getExtras();
        if(bundle == null)
            bundle = Bundle.EMPTY;
        return bundle;
    }

    public static String getFormattedAddress(Location location)
    {
        Bundle bundle = getExtras(location);
        bundle.setClassLoader(DbLocation.class.getClassLoader());
        return bundle.getString("location_description");
    }

    public static DbLocation getStreetLevelLocation(Location location)
    {
        return (DbLocation)getExtras(location).getParcelable("finest_location");
    }

    public static boolean isProviderEnabled(Context context) {
    	boolean flag = false;
        if(android.os.Build.VERSION.SDK_INT >= 11) {
        	switch(GoogleLocationSettingHelper.getUseLocationForServices(context))
            {
	            default:
	                flag = canFindLocation(context);
	                break;
	
	            case 0: // '\0'
	                flag = false;
	                break;
            }
        } else { 
        	flag = canFindLocation(context);
        }
        return flag;
    }

    public final void init()
    {
        if(mLocationAcquisitionTimer != null)
        {
            mHandler.removeCallbacks(mLocationAcquisitionTimer);
            mLocationAcquisitionTimer = null;
        }
        mLastSentLocation = null;
        if(mTimeout > 0L && mNetworkListener != null && mGpsListener != null)
        {
            mLocationAcquisitionTimer = new Runnable() {

                public final void run()
                {
                    if(mLocation != null)
                    {
                        if(EsLog.isLoggable("LocationController", 3))
                            Log.d("LocationController", "----> locationAcquisitionTimer: timeout, with location");
                        if(mLastSentLocation == null || !LocationController.areSameLocations(mLastSentLocation, mLocation))
                        {
                            if(mReverseGeo)
                                startSnapToPlace();
                            else
                            if(mListener != null)
                                mListener.onLocationChanged(mLocation);
                            mLastSentLocation = mLocation;
                        }
                    } else
                    {
                        if(EsLog.isLoggable("LocationController", 3))
                            Log.d("LocationController", "----> locationAcquisitionTimer: timeout, without location");
                        mHandler.postDelayed(this, mTimeout / 2L);
                    }
                }
            };
            mHandler.postDelayed(mLocationAcquisitionTimer, mTimeout);
        }
    }

    public final boolean isProviderEnabled() {
    	boolean flag = false;
        if(android.os.Build.VERSION.SDK_INT >= 11) {
        	switch(GoogleLocationSettingHelper.getUseLocationForServices(mContext))
            {
	            default:
	                flag = canFindLocation();
	                break;
	
	            case 0: // '\0'
	                flag = false;
	                break;
            }
        } else { 
        	flag = canFindLocation();
        }
        return flag;
    }

    public final void release()
    {
        if(mLocationAcquisitionTimer != null)
        {
            mHandler.removeCallbacks(mLocationAcquisitionTimer);
            mLocationAcquisitionTimer = null;
        }
        if(mNetworkListener != null)
            mLocationManager.removeUpdates(mNetworkListener);
        if(mGpsListener != null)
            mLocationManager.removeUpdates(mGpsListener);
        mListener = null;
    }
    
    private void startSnapToPlace() {
    	new SnapToPlaceThread().start();
    }
    
	private class SnapToPlaceThread extends Thread {

        public final void run() {
            SnapToPlaceOperation snaptoplaceoperation = new SnapToPlaceOperation(mContext, mAccount, null, null, new LocationQuery(mLocation, null), null, false);
            snaptoplaceoperation.start();
            Bundle bundle = new Bundle();
            if(mDisplayDebugToast && mLocation.getExtras() != null)
                bundle.putString("location_source", mLocation.getExtras().getString("location_source"));
            
            String s;
            DbLocation dblocation;
            if(!snaptoplaceoperation.hasPreciseLocation()) {
            	if(snaptoplaceoperation.hasPlaceLocation())
                {
                    dblocation = snaptoplaceoperation.getPlaceLocation();
                    s = "finest_location";
                } else
                {
                    boolean flag = snaptoplaceoperation.hasCoarseLocation();
                    s = null;
                    dblocation = null;
                    if(flag)
                    {
                        dblocation = snaptoplaceoperation.getCoarseLocation();
                        s = "coarse_location";
                    }
                }
            } else { 
            	
                dblocation = snaptoplaceoperation.getPreciseLocation();
                s = "finest_location";
            }
            
            if(dblocation != null)
            {
                bundle.putParcelable(s, dblocation);
                String s1 = dblocation.getLocationName();
                if(!TextUtils.isEmpty(s1))
                    bundle.putString("location_description", s1);
            }
            mLocation.setExtras(bundle);
            mHandler.post(new Runnable() {

                public final void run() {
                    if(mDisplayDebugToast)
                        Toast.makeText(mContext, (new StringBuilder()).append(mLocation.getExtras().getString("location_source")).append(LocationController.getFormattedAddress(mLocation)).toString(), 0).show();
                    if(mListener != null)
                        mListener.onLocationChanged(mLocation);
                }
            });
            return;
        }
    }
	
	private final class LocalLocationListener implements LocationListener {

	    private void triggerLocationListener()
	    {
	        if((mLastSuccessfulLocationListener == null || mLastSuccessfulLocationListener == this) && mLocationAcquisitionTimer != null || mLastSentLocation != null && LocationController.areSameLocations(mLastSentLocation, mLocation)) { 
	        	mLastSuccessfulLocationListener = this;
	        } else { 
	        	if(EsLog.isLoggable("LocationController", 3))
		        {
		            StringBuilder stringbuilder = new StringBuilder("----> onLocationChanged: triggering location change because ");
		            String s;
		            if(mLocationAcquisitionTimer == null)
		                s = "only this location listener was registered";
		            else
		                s = "a previous location listener was successful";
		            Log.d("LocationController", stringbuilder.append(s).toString());
		        }
	        	if(!mReverseGeo) {
	        		if(mListener != null)
	    	            mListener.onLocationChanged(mLocation);
	        	} else { 
	        		startSnapToPlace();
	        	}
	        	mLastSentLocation = mLocation;
        		mLastSuccessfulLocationListener = this;
    	        return;
	        }
	    }
	    
	    public final void onLocationChanged(Location location)
        {
            if(EsLog.isLoggable("LocationController", 3))
                Log.d("LocationController", (new StringBuilder("====> onLocationChanged: ")).append(location.getTime()).append(" from provider: ").append(location.getProvider()).toString());
            if(mLocation != null)
            {
            	triggerLocationListener();
                    
            } else
            {
                if(EsLog.isLoggable("LocationController", 3))
                    Log.d("LocationController", (new StringBuilder("----> onLocationChanged: new location: ")).append(location.getAccuracy()).toString());
                mLocation = location;
                if(mDisplayDebugToast)
                {
                    String s;
                    if(this == mNetworkListener)
                        s = "net: ";
                    else
                        s = "gps: ";
                    mLocation.getExtras().putString("location_source", s);
                }
                triggerLocationListener();
            }
        }

        public final void onProviderDisabled(String s)
        {
            if(mListener != null)
                mListener.onProviderDisabled(s);
        }

        public final void onProviderEnabled(String s)
        {
            if(mListener != null)
                mListener.onProviderEnabled(s);
        }

        public final void onStatusChanged(String s, int i, Bundle bundle)
        {
            if(mListener != null)
                mListener.onStatusChanged(s, i, bundle);
        }
	}
}
