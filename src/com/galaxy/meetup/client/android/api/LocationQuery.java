/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * 
 * @author sihai
 *
 */
public class LocationQuery implements Parcelable {

	private final String mKey;
    private final Location mLocation;
    private final String mQuery;
    
    public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel)
        {
            return new LocationQuery(parcel);
        }

        public final Object[] newArray(int i)
        {
            return new LocationQuery[i];
        }

    };
    
    public LocationQuery(Location location, String query)
    {
        if(location == null)
        {
            throw new NullPointerException("Location is null");
        } else
        {
            mLocation = location;
            mQuery = query;
            mKey = buildKey();
            return;
        }
    }

    LocationQuery(Parcel parcel)
    {
        mLocation = (Location)parcel.readParcelable(LocationQuery.class.getClassLoader());
        mQuery = parcel.readString();
        mKey = buildKey();
    }

    private String buildKey()
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(mLocation.getLatitude()).append('|');
        stringbuilder.append(mLocation.getLongitude()).append('|');
        stringbuilder.append(mLocation.getAccuracy()).append('|');
        if(hasQueryString())
            stringbuilder.append(mQuery);
        return stringbuilder.toString();
    }

    public int describeContents()
    {
        return 0;
    }

    public boolean equals(Object obj) {
    	if(null == obj) {
    		return false;
    	}
    	if(!(obj instanceof LocationQuery)) {
    		return false;
    	}
    	return mKey.equals(((LocationQuery)obj).mKey);
    }

    public final String getKey()
    {
        return mKey;
    }

    public final Location getLocation()
    {
        return mLocation;
    }

    public final String getQueryString()
    {
        return mQuery;
    }

    public final boolean hasQueryString()
    {
        boolean flag;
        if(mQuery != null && mQuery.length() > 0)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public int hashCode()
    {
        return mKey.hashCode();
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeParcelable(mLocation, i);
        parcel.writeString(mQuery);
    }

}
