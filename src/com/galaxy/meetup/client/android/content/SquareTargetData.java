// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.galaxy.meetup.client.android.content;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class SquareTargetData implements Parcelable, Cloneable {

	private SquareTargetData(Parcel parcel) {
		mSquareId = parcel.readString();
		mSquareName = parcel.readString();
		mSquareStreamId = parcel.readString();
		mSquareStreamName = parcel.readString();
	}

	SquareTargetData(Parcel parcel, byte byte0) {
		this(parcel);
	}

	public SquareTargetData(String s, String s1, String s2, String s3) {
		mSquareId = s;
		mSquareName = s1;
		mSquareStreamId = s2;
		mSquareStreamName = s3;
	}

	public int describeContents() {
		return 0;
	}

	public boolean equals(Object obj) {
		boolean flag = true;

		if (!(obj instanceof SquareTargetData))
			flag = false;
		SquareTargetData squaretargetdata = (SquareTargetData) obj;
		if (!TextUtils.equals(mSquareId, squaretargetdata.mSquareId)
				|| !TextUtils.equals(mSquareName, squaretargetdata.mSquareName)
				|| !TextUtils.equals(mSquareStreamId,
						squaretargetdata.mSquareStreamId)
				|| !TextUtils.equals(mSquareStreamName,
						squaretargetdata.mSquareStreamName))
			flag = false;

		return flag;
	}

	public final String getSquareId() {
		return mSquareId;
	}

	public final String getSquareName() {
		return mSquareName;
	}

	public final String getSquareStreamId() {
		return mSquareStreamId;
	}

	public final String getSquareStreamName() {
		return mSquareStreamName;
	}

	public int hashCode() {
		int i = 17;
		if (mSquareId != null)
			i = 527 + mSquareId.hashCode();
		if (mSquareName != null)
			i = i * 31 + mSquareName.hashCode();
		if (mSquareStreamId != null)
			i = i * 31 + mSquareStreamId.hashCode();
		if (mSquareStreamName != null)
			i = i * 31 + mSquareStreamName.hashCode();
		return i;
	}

	public String toString() {
		return (new StringBuilder("{SquareStreamData name="))
				.append(mSquareName).append(" stream=")
				.append(mSquareStreamName).append('}').toString();
	}

	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(mSquareId);
		parcel.writeString(mSquareName);
		parcel.writeString(mSquareStreamId);
		parcel.writeString(mSquareStreamName);
	}

	public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

		public final Object createFromParcel(Parcel parcel) {
			return new SquareTargetData(parcel, (byte) 0);
		}

		public final Object[] newArray(int i) {
			return new SquareTargetData[i];
		}

	};
	private String mSquareId;
	private String mSquareName;
	private String mSquareStreamId;
	private String mSquareStreamName;

}
