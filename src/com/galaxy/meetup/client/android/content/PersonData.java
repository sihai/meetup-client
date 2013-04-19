// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.galaxy.meetup.client.android.content;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class PersonData implements Parcelable, Cloneable {

	private PersonData(Parcel parcel) {
		mObfuscatedId = parcel.readString();
		mName = parcel.readString();
		mEmail = parcel.readString();
		mCompressedPhotoUrl = parcel.readString();
	}

	PersonData(Parcel parcel, byte byte0) {
		this(parcel);
	}

	public PersonData(String s, String s1, String s2) {
		this(s, s1, s2, null);
	}

	public PersonData(String s, String s1, String s2, String s3) {
		mObfuscatedId = s;
		mName = s1;
		mEmail = s2;
		mCompressedPhotoUrl = s3;
	}

	public int describeContents() {
		return 0;
	}

	public boolean equals(Object obj) {
		boolean flag = true;
		if (!(obj instanceof PersonData)) {
			flag = false;
		} else {
			PersonData persondata = (PersonData) obj;
			if (!TextUtils.equals(mObfuscatedId, persondata.mObfuscatedId)
					|| !TextUtils.equals(mName, persondata.mName)
					|| !TextUtils.equals(mEmail, persondata.mEmail)
					|| !TextUtils.equals(mCompressedPhotoUrl,
							persondata.mCompressedPhotoUrl))
				flag = false;
		}
		return flag;
	}

	public final String getCompressedPhotoUrl() {
		return mCompressedPhotoUrl;
	}

	public final String getEmail() {
		return mEmail;
	}

	public final String getName() {
		return mName;
	}

	public final String getObfuscatedId() {
		return mObfuscatedId;
	}

	public int hashCode() {
		int i = 17;
		if (mObfuscatedId != null)
			i = 527 + mObfuscatedId.hashCode();
		if (mName != null)
			i = i * 31 + mName.hashCode();
		if (mEmail != null)
			i = i * 31 + mEmail.hashCode();
		if (mCompressedPhotoUrl != null)
			i = i * 31 + mCompressedPhotoUrl.hashCode();
		return i;
	}

	public String toString() {
		return (new StringBuilder("{PersonData id=")).append(mObfuscatedId)
				.append(" name=").append(mName).append(" email=")
				.append(mEmail).append(" compressed photo url=")
				.append(mCompressedPhotoUrl).append("}").toString();
	}

	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(mObfuscatedId);
		parcel.writeString(mName);
		parcel.writeString(mEmail);
		parcel.writeString(mCompressedPhotoUrl);
	}

	public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

		public final Object createFromParcel(Parcel parcel) {
			return new PersonData(parcel, (byte) 0);
		}

		public final Object[] newArray(int i) {
			return new PersonData[i];
		}

	};
	private String mCompressedPhotoUrl;
	private String mEmail;
	private String mName;
	private String mObfuscatedId;

}
