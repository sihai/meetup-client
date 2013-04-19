package com.galaxy.meetup.client.android.content;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.R;

/**
 * 
 * @author sihai
 *
 */
public class AudienceData implements Parcelable, Cloneable {

	public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel)
        {
            return new AudienceData(parcel);
        }

        public final Object[] newArray(int i)
        {
            return new AudienceData[i];
        }

    };

	private CircleData mCircles[];
	private SquareTargetData mSquareTargets[];
	private int mTotalPersonCount;
	private PersonData mUsers[];


	private AudienceData(Parcel parcel) {
		mUsers = new PersonData[parcel.readInt()];
		parcel.readTypedArray(mUsers, PersonData.CREATOR);
		mCircles = new CircleData[parcel.readInt()];
		parcel.readTypedArray(mCircles, CircleData.CREATOR);
		mSquareTargets = new SquareTargetData[parcel.readInt()];
		parcel.readTypedArray(mSquareTargets, SquareTargetData.CREATOR);
		mTotalPersonCount = parcel.readInt();
	}

	public AudienceData(CircleData circledata) {
		mUsers = new PersonData[0];
		mCircles = new CircleData[1];
		mSquareTargets = new SquareTargetData[0];
		mCircles[0] = circledata;
	}

	public AudienceData(PersonData persondata) {
		mUsers = new PersonData[1];
		mCircles = new CircleData[0];
		mSquareTargets = new SquareTargetData[0];
		mUsers[0] = persondata;
		mTotalPersonCount = 1;
	}

	public AudienceData(SquareTargetData squaretargetdata) {
		mUsers = new PersonData[0];
		mCircles = new CircleData[0];
		mSquareTargets = new SquareTargetData[1];
		mSquareTargets[0] = squaretargetdata;
	}

	public AudienceData(List list, List list1) {
		this(list, list1, ((List) (null)));
	}

	public AudienceData(List list, List list1, int i) {
		this(list, null, null, i);
	}

	public AudienceData(List list, List list1, List list2) {
		this(list, list1, list2, null == list ? 0 : list.size());
	}

	public AudienceData(List list, List list1, List list2, int i) {
		if (list != null) {
			mUsers = new PersonData[list.size()];
			list.toArray(mUsers);
		} else {
			mUsers = new PersonData[0];
		}
		if (list1 != null) {
			mCircles = new CircleData[list1.size()];
			list1.toArray(mCircles);
		} else {
			mCircles = new CircleData[0];
		}
		if (list2 != null) {
			mSquareTargets = new SquareTargetData[list2.size()];
			list2.toArray(mSquareTargets);
		} else {
			mSquareTargets = new SquareTargetData[0];
		}
		mTotalPersonCount = i;
	}

	public final AudienceData clone() {
		return new AudienceData(Arrays.asList(mUsers), Arrays.asList(mCircles),
				Arrays.asList(mSquareTargets), mTotalPersonCount);
	}

	public int describeContents() {
		return 0;
	}

	public boolean equals(Object obj) {
		boolean flag = true;
		if (!(obj instanceof AudienceData))
			flag = false;
		AudienceData audiencedata = (AudienceData) obj;
		if (mTotalPersonCount != audiencedata.mTotalPersonCount
				|| !Arrays.equals(mUsers, audiencedata.mUsers)
				|| !Arrays.equals(mCircles, audiencedata.mCircles)
				|| !Arrays.equals(mSquareTargets, audiencedata.mSquareTargets))
			flag = false;
		return flag;
	}

	public final CircleData getCircle(int i) {
		return mCircles[i];
	}

	public final int getCircleCount() {
		return mCircles.length;
	}

	public final CircleData[] getCircles() {
		return mCircles;
	}

	public final int getHiddenUserCount() {
		return Math.max(0, mTotalPersonCount - mUsers.length);
	}

	public final SquareTargetData getSquareTarget(int i) {
		return mSquareTargets[0];
	}

	public final int getSquareTargetCount() {
		return mSquareTargets.length;
	}

	public final SquareTargetData[] getSquareTargets() {
		return mSquareTargets;
	}

	public final PersonData getUser(int i) {
		return mUsers[i];
	}

	public final int getUserCount() {
		return mUsers.length;
	}

	public final PersonData[] getUsers() {
		return mUsers;
	}

	public int hashCode() {
		return 31
				* (31 * (31 * (527 + mTotalPersonCount) + Arrays
						.hashCode(mUsers)) + Arrays.hashCode(mCircles))
				+ Arrays.hashCode(mSquareTargets);
	}

	public final boolean isEmpty() {
		boolean flag;
		if (mUsers.length == 0 && mCircles.length == 0
				&& mSquareTargets.length == 0)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public final String toNameList(Context context) {
		Resources resources = context.getResources();
		String s = resources.getString(R.string.compose_acl_separator);
		String s1 = resources.getString(0x104000e);
		String s2 = resources.getString(R.string.loading);
		String s3 = resources.getString(R.string.square_unknown);
		int i = mCircles.length + mUsers.length + mSquareTargets.length;
		StringBuilder stringbuilder = new StringBuilder();
		int j = 0;
		int k = 0;
		while (k < mCircles.length) {
			String s8 = mCircles[k].getName();
			if (TextUtils.isEmpty(s8))
				s8 = s2;
			stringbuilder.append(s8);
			if (++j < i)
				stringbuilder.append(s);
			k++;
		}
		int l = 0;
		while (l < mUsers.length) {
			String s6 = mUsers[l].getName();
			String s7 = mUsers[l].getEmail();
			if (TextUtils.isEmpty(s6))
				if (!TextUtils.isEmpty(s7))
					s6 = s7;
				else
					s6 = s1;
			stringbuilder.append(s6);
			if (++j < i)
				stringbuilder.append(s);
			l++;
		}
		int i1 = 0;
		while (i1 < mSquareTargets.length) {
			String s4 = mSquareTargets[i1].getSquareName();
			String s5 = mSquareTargets[i1].getSquareStreamName();
			if (TextUtils.isEmpty(s4))
				s4 = s3;
			if (TextUtils.isEmpty(s5))
				stringbuilder.append(s4);
			else
				stringbuilder.append(resources
						.getString(R.string.square_name_and_topic,
								new Object[] { s4, s5 }));
			if (++j < i)
				stringbuilder.append(s);
			i1++;
		}
		return stringbuilder.toString();
	}

	public String toString() {
		return (new StringBuilder("Audience circles: "))
				.append(Arrays.asList(mCircles)).append(", users: ")
				.append(Arrays.asList(mUsers)).append(", squares: ")
				.append(Arrays.asList(mSquareTargets))
				.append(", hidden users: ").append(getHiddenUserCount())
				.toString();
	}

	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(mUsers.length);
		parcel.writeTypedArray(mUsers, 0);
		parcel.writeInt(mCircles.length);
		parcel.writeTypedArray(mCircles, 0);
		parcel.writeInt(mSquareTargets.length);
		parcel.writeTypedArray(mSquareTargets, 0);
		parcel.writeInt(mTotalPersonCount);
	}
}
