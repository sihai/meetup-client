/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 
 * @author sihai
 *
 */
public class CircleData implements Parcelable, Cloneable {

	private int mCircleType;
    private String mId;
    private String mName;
    private int mSize;
    
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		public final Object createFromParcel(Parcel parcel) {
			return new CircleData(parcel);
		}

		public final Object[] newArray(int i) {
			return new CircleData[i];
		}

    };

	private CircleData(Parcel parcel) {
		mId = parcel.readString();
		mName = parcel.readString();
		mCircleType = parcel.readInt();
		mSize = parcel.readInt();
	}

	public CircleData(String s, int i, String s1, int j) {
		mId = s;
		mCircleType = i;
		mName = s1;
		mSize = j;
	}
    
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mId);
		dest.writeString(mName);
		dest.writeInt(mCircleType);
		dest.writeInt(mSize);
	}
	
	public final String getId() {
		return mId;
	}

	public final String getName() {
		return mName;
	}

	public final int getSize() {
		return mSize;
	}

	public final int getType() {
		return mCircleType;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof CircleData)) {
			return false;
		}
		CircleData circledata = (CircleData) obj;
		if (!TextUtils.equals(mId, circledata.mId)
				|| !TextUtils.equals(mName, circledata.mName)
				|| mCircleType != circledata.mCircleType
				|| mSize != circledata.mSize) {
			return false;
		}

		return true;
	}
	
	public int hashCode() {
		int i = 17;
		if (mId != null)
			i = 527 + mId.hashCode();
		if (mName != null)
			i = i * 31 + mName.hashCode();
		return 31 * (i * 31 + mCircleType) + mSize;
	}

	public String toString() {
		return (new StringBuilder("{CircleData id=")).append(mId)
				.append(" name=").append(mName).append(" type=")
				.append(mCircleType).append(" size=").append(mSize).append("}")
				.toString();
	}
}
