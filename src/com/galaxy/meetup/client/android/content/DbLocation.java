/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.galaxy.meetup.client.util.PrimitiveUtils;
import com.galaxy.meetup.server.client.domain.Checkin;
import com.galaxy.meetup.server.client.domain.Place;

/**
 * 
 * @author sihai
 *
 */
public class DbLocation extends DbSerializer implements Parcelable {

	private final String mBestAddress;
    private final String mClusterId;
    private final boolean mHasCoordinates;
    private final int mLatitudeE7;
    private final int mLongitudeE7;
    private final String mName;
    private final double mPrecisionMeters;
    private final int mType;
    
    public DbLocation(int i, Location location)
    {
        if(location == null)
            throw new IllegalArgumentException();
        mType = 0;
        mHasCoordinates = true;
        mLatitudeE7 = (int)(10000000D * location.getLatitude());
        mLongitudeE7 = (int)(10000000D * location.getLongitude());
        mClusterId = null;
        mBestAddress = null;
        mName = null;
        double d;
        if(location.hasAccuracy())
            d = location.getAccuracy();
        else
            d = -1D;
        mPrecisionMeters = d;
    }

    public DbLocation(int i, com.galaxy.meetup.server.client.domain.Location location)
    {
        if(i < 0 || i > 3 || location == null)
            throw new IllegalArgumentException();
        mType = i;
        mName = location.locationTag;
        mBestAddress = location.bestAddress;
        mClusterId = location.clusterId;
        Integer integer;
        Integer integer1;
        boolean flag;
        double d;
        if(location.latitudeE7 != null)
            integer = location.latitudeE7;
        else
        if(location.latitude != null)
            integer = Integer.valueOf((int)(10000000D * (double)location.latitude.floatValue()));
        else
            integer = null;
        if(location.longitudeE7 != null)
            integer1 = location.longitudeE7;
        else
        if(location.longitude != null)
            integer1 = Integer.valueOf((int)(10000000D * (double)location.longitude.floatValue()));
        else
            integer1 = null;
        if(integer != null && integer1 != null)
            flag = true;
        else
            flag = false;
        mHasCoordinates = flag;
        if(mHasCoordinates)
        {
            mLatitudeE7 = integer.intValue();
            mLongitudeE7 = integer1.intValue();
        } else
        {
            mLongitudeE7 = 0;
            mLatitudeE7 = 0;
        }
        if(location.precisionMeters == null)
            d = -1D;
        else
            d = location.precisionMeters.doubleValue();
        mPrecisionMeters = d;
    }

    public DbLocation(int i, Integer integer, Integer integer1, String s, String s1, String s2, double d)
    {
        if(i < 0 || i > 3)
            throw new IllegalArgumentException();
        mType = i;
        mName = s;
        mBestAddress = s1;
        mClusterId = s2;
        boolean flag;
        if(integer != null && integer1 != null)
            flag = true;
        else
            flag = false;
        mHasCoordinates = flag;
        if(mHasCoordinates)
        {
            mLatitudeE7 = integer.intValue();
            mLongitudeE7 = integer1.intValue();
        } else
        {
            mLongitudeE7 = 0;
            mLatitudeE7 = 0;
        }
        mPrecisionMeters = d;
    }

    private DbLocation(Parcel parcel)
    {
        mType = parcel.readInt();
        mName = parcel.readString();
        mBestAddress = parcel.readString();
        boolean flag;
        if(parcel.readInt() != 0)
            flag = true;
        else
            flag = false;
        mHasCoordinates = flag;
        mLatitudeE7 = parcel.readInt();
        mLongitudeE7 = parcel.readInt();
        mPrecisionMeters = parcel.readDouble();
        mClusterId = parcel.readString();
    }
    
    private DbLocation(Checkin checkin)
    {
        if(checkin == null)
            throw new IllegalArgumentException();
        mType = 3;
        if(checkin.location != null)
        {
            String s;
            String s1;
            if(checkin.location.getName() == null)
                s = checkin.name;
            else
                s = checkin.location.getName();
            mName = s;
            if(checkin.location.getAddress() == null)
                s1 = null;
            else
                s1 = checkin.location.getAddress().getName();
            mBestAddress = s1;
            if(checkin.location.getGeo() != null)
            {
                boolean flag;
                if(checkin.location.getGeo().getLatitude() != null && checkin.location.getGeo().getLongitude() != null)
                    flag = true;
                else
                    flag = false;
                mHasCoordinates = flag;
                mLatitudeE7 = (int)(10000000D * PrimitiveUtils.safeDouble(checkin.location.getGeo().getLatitude()));
                mLongitudeE7 = (int)(10000000D * PrimitiveUtils.safeDouble(checkin.location.getGeo().getLongitude()));
            } else
            {
                mHasCoordinates = false;
                mLongitudeE7 = 0;
                mLatitudeE7 = 0;
            }
            mClusterId = checkin.location.getClusterId();
        } else
        {
            mName = checkin.name;
            mBestAddress = null;
            mHasCoordinates = false;
            mLongitudeE7 = 0;
            mLatitudeE7 = 0;
            mClusterId = null;
        }
        mPrecisionMeters = -1D;
    }

    private DbLocation(Place place)
    {
        if(place == null)
            throw new IllegalArgumentException();
        mType = 3;
        mName = place.getName();
        String s;
        if(place.getAddress() == null)
            s = null;
        else
            s = place.getAddress().getName();
        mBestAddress = s;
        if(place.getGeo() != null)
        {
            boolean flag;
            if(place.getGeo().getLatitude() != null && place.getGeo().getLongitude() != null)
                flag = true;
            else
                flag = false;
            mHasCoordinates = flag;
            mLatitudeE7 = (int)(10000000D * PrimitiveUtils.safeDouble(place.getGeo().getLatitude()));
            mLongitudeE7 = (int)(10000000D * PrimitiveUtils.safeDouble(place.getGeo().getLongitude()));
        } else
        {
            mHasCoordinates = false;
            mLongitudeE7 = 0;
            mLatitudeE7 = 0;
        }
        mPrecisionMeters = -1D;
        mClusterId = null;
    }

    public static DbLocation deserialize(byte abyte0[])
    {
        DbLocation dblocation = null;
        if(abyte0 != null)
        {
            ByteBuffer bytebuffer = ByteBuffer.wrap(abyte0);
            int i = bytebuffer.getInt();
            String s = getShortString(bytebuffer);
            String s1 = getShortString(bytebuffer);
            boolean flag;
            int j;
            int k;
            double d;
            String s2;
            Integer integer;
            Integer integer1;
            if(bytebuffer.getInt() != 0)
                flag = true;
            else
                flag = false;
            j = bytebuffer.getInt();
            k = bytebuffer.getInt();
            d = bytebuffer.getDouble();
            s2 = getShortString(bytebuffer);
            if(flag)
                integer = Integer.valueOf(j);
            else
                integer = null;
            integer1 = null;
            if(flag)
                integer1 = Integer.valueOf(k);
            dblocation = new DbLocation(i, integer, integer1, s, s1, s2, d);
        }
        return dblocation;
    }

    public static byte[] serialize(DbLocation dblocation)
        throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(32);
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        dataoutputstream.writeInt(dblocation.mType);
        putShortString(dataoutputstream, dblocation.mName);
        putShortString(dataoutputstream, dblocation.mBestAddress);
        int i;
        byte abyte0[];
        if(dblocation.mHasCoordinates)
            i = 1;
        else
            i = 0;
        dataoutputstream.writeInt(i);
        dataoutputstream.writeInt(dblocation.mLatitudeE7);
        dataoutputstream.writeInt(dblocation.mLongitudeE7);
        dataoutputstream.writeDouble(dblocation.mPrecisionMeters);
        putShortString(dataoutputstream, dblocation.mClusterId);
        abyte0 = bytearrayoutputstream.toByteArray();
        dataoutputstream.close();
        return abyte0;
    }

    public static byte[] serialize(Checkin checkin)
        throws IOException
    {
        return serialize(new DbLocation(checkin));
    }

    public static byte[] serialize(Place place)
        throws IOException
    {
        return serialize(new DbLocation(place));
    }

    public int describeContents()
    {
        return 0;
    }

    public final Location getAndroidLocation() {
        Location location = new Location((String)null);
        if(mHasCoordinates) {
            location.setLatitude((double)mLatitudeE7 / 10000000D);
            location.setLongitude((double)mLongitudeE7 / 10000000D);
        }
        if(mPrecisionMeters >= 0.0D)
            location.setAccuracy((float)mPrecisionMeters);
        return location;
    }

    public final String getBestAddress()
    {
        return mBestAddress;
    }

    public final String getClusterId()
    {
        return mClusterId;
    }

    public final int getLatitudeE7()
    {
        return mLatitudeE7;
    }

    public final String getLocationName()
    {
        String s;
        if(!TextUtils.isEmpty(mName))
            s = mName;
        else
        if(!TextUtils.isEmpty(mBestAddress))
            s = mBestAddress;
        else
            s = "";
        return s;
    }

    public final int getLongitudeE7()
    {
        return mLongitudeE7;
    }

    public final String getName()
    {
        return mName;
    }

    public final double getPrecisionMeters()
    {
        return mPrecisionMeters;
    }

    public final boolean hasCoordinates()
    {
        return mHasCoordinates;
    }

    public final boolean isCoarse()
    {
        boolean flag;
        if(mType == 2)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean isPrecise()
    {
        boolean flag = true;
        if(mType != 1)
            flag = false;
        return flag;
    }

    public final boolean isSamePlace(DbLocation dblocation) {
    	if(this == dblocation) {
    		return true;
    	}
    	boolean flag = true;
    	if(dblocation == null)
            flag = false;
        else
        if((!isPrecise() || !dblocation.isPrecise()) && (!isCoarse() || !dblocation.isCoarse()) && (mType != 3 || dblocation.mType != 3 || !TextUtils.equals(mName, dblocation.mName) || !TextUtils.equals(mBestAddress, dblocation.mBestAddress) || mHasCoordinates != dblocation.mHasCoordinates || mLatitudeE7 != dblocation.mLatitudeE7 || mLongitudeE7 != dblocation.mLongitudeE7))
            flag = false;
    	return flag;
    }

    public final com.galaxy.meetup.server.client.domain.Location toProtocolObject() {
    	com.galaxy.meetup.server.client.domain.Location location = new com.galaxy.meetup.server.client.domain.Location();
        location.locationTag = mName;
        location.bestAddress = mBestAddress;
        location.clusterId = mClusterId;
        if(mHasCoordinates)
        {
            location.latitudeE7 = Integer.valueOf(mLatitudeE7);
            location.longitudeE7 = Integer.valueOf(mLongitudeE7);
        }
        if(mPrecisionMeters >= 0.0D)
            location.precisionMeters = Double.valueOf(mPrecisionMeters);
        return location;
    }

	public String toString() {
		StringBuilder stringbuilder = new StringBuilder("LocationValue type: ");
		String type = null;
		if (1 == mType) {
			type = "precise";
		} else if (2 == mType) {
			type = "coarse";
		} else if (3 == mType) {
			type = "place";
		} else {
			type = String.format("unknown(%d)", mType);
		}
		return stringbuilder.append(type).append(", name: ").append(mName)
				.append(", addr: ").append(mBestAddress).append(", hasCoord: ")
				.append(mHasCoordinates).append(", latE7: ")
				.append(mLatitudeE7).append(", lngE7: ").append(mLongitudeE7)
				.append(", cluster: ").append(mClusterId)
				.append(", precision: ").append(mPrecisionMeters).toString();
	}

	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(mType);
		parcel.writeString(mName);
		parcel.writeString(mBestAddress);
		int j;
		if (mHasCoordinates)
			j = 1;
		else
			j = 0;
		parcel.writeInt(j);
		parcel.writeInt(mLatitudeE7);
		parcel.writeInt(mLongitudeE7);
		parcel.writeDouble(mPrecisionMeters);
		parcel.writeString(mClusterId);
	}
    
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		public final Object createFromParcel(Parcel parcel) {
			return new DbLocation(parcel);
		}

		public final Object[] newArray(int i) {
			return new DbLocation[i];
		}

	};
}
