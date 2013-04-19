/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.EsMatrixCursor;

/**
 * 
 * @author sihai
 * 
 */
public class PeopleSearchResults implements Parcelable {

	public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

		public final Object createFromParcel(Parcel parcel) {
			return new PeopleSearchResults(parcel);
		}

		public final Object[] newArray(int i) {
			return new PeopleSearchResults[i];
		}

	};

	private static final String PROJECTION[] = { "_id", "person_id",
			"lookup_key", "gaia_id", "name", "profile_type", "avatar",
			"packed_circle_ids", "matched_email", "email", "phone",
			"phone_type", "snippet" };
	private final List mContacts;
	private EsMatrixCursor mCursor;
	private boolean mCursorValid;
	private final Map mGaiaIdsAndCircles;
	private boolean mGaiaIdsAndCirclesLoaded;
	private boolean mHasMoreResults;
	private boolean mIncludePeopleInCircles;
	private final List mLocalProfiles;
	private boolean mLocalProfilesLoaded;
	private String mMyPersonId;
	private long mNextId;
	private final List mPublicProfiles;
	private String mQuery;
	private String mToken;

	public PeopleSearchResults() {
		mGaiaIdsAndCircles = new HashMap();
		mGaiaIdsAndCirclesLoaded = false;
		mContacts = new ArrayList();
		mLocalProfiles = new ArrayList();
		mLocalProfilesLoaded = false;
		mPublicProfiles = new ArrayList();
		mIncludePeopleInCircles = true;
	}

	public PeopleSearchResults(Parcel parcel) {
		super();
		boolean flag = true;
		mGaiaIdsAndCircles = new HashMap();
		mGaiaIdsAndCirclesLoaded = false;
		mContacts = new ArrayList();
		mLocalProfiles = new ArrayList();
		mLocalProfilesLoaded = false;
		mPublicProfiles = new ArrayList();
		mIncludePeopleInCircles = flag;
		mMyPersonId = parcel.readString();
		mQuery = parcel.readString();
		mToken = parcel.readString();
		boolean flag1;
		int i;
		if (parcel.readInt() != 0)
			flag1 = flag;
		else
			flag1 = false;
		mHasMoreResults = flag1;
		if (parcel.readInt() == 0)
			flag = false;
		mIncludePeopleInCircles = flag;
		i = parcel.readInt();
		for (int j = 0; j < i; j++) {
			String s = parcel.readString();
			String s1 = parcel.readString();
			String s2 = parcel.readString();
			int k = parcel.readInt();
			String s3 = parcel.readString();
			String s4 = parcel.readString();
			mPublicProfiles.add(new PublicProfile(s, s1, s2, k, s3, s4));
		}

	}

	public static void onFinishContacts() {
	}

	public final void addContact(String s, String s1, String s2, String s3,
			String s4, String s5) {
		mContacts.add(new Contact(s, s1, s2, s3, s4, s5));
	}

	public final void addGaiaIdAndCircles(String s, String s1) {
		mGaiaIdsAndCircles.put(s, s1);
	}

	public final void addLocalProfile(String s, String s1, String s2, int i,
			String s3, String s4, String s5, String s6, String s7) {
		if (!s.equals(mMyPersonId))
			mLocalProfiles.add(new LocalProfile(s, s1, s2, i, s3, s4, s5, null,
					null));
	}

	public final void addPublicProfile(String s, String s1, String s2, int i,
			String s3, String s4) {
		if (!s.equals(mMyPersonId)) {
			mPublicProfiles.add(new PublicProfile(s, s1, s2, i, s3, s4));
			mCursorValid = false;
		}
	}

	public int describeContents() {
		return 0;
	}

	public final int getCount() {
		return getCursor().getCount();
	}

	public final Cursor getCursor() {
		EsMatrixCursor esmatrixcursor;
		if (mCursorValid) {
			esmatrixcursor = mCursor;
		} else {
			mCursor = new EsMatrixCursor(PROJECTION);
			mCursorValid = true;
			if (!mLocalProfilesLoaded || !mGaiaIdsAndCirclesLoaded) {
				esmatrixcursor = mCursor;
			} else {
				HashSet hashset = new HashSet();
				HashSet hashset1 = new HashSet();
				HashSet hashset2 = new HashSet();
				if (mIncludePeopleInCircles) {
					Iterator iterator2 = mLocalProfiles.iterator();
					do {
						if (!iterator2.hasNext())
							break;
						LocalProfile localprofile = (LocalProfile) iterator2
								.next();
						String s5 = localprofile.gaiaId;
						String s6 = localprofile.email;
						EsMatrixCursor esmatrixcursor3 = mCursor;
						Object aobj4[] = new Object[13];
						long l3 = mNextId;
						mNextId = 1L + l3;
						aobj4[0] = Long.valueOf(l3);
						aobj4[1] = localprofile.personId;
						aobj4[2] = null;
						aobj4[3] = s5;
						aobj4[4] = localprofile.name;
						aobj4[5] = Integer.valueOf(localprofile.profileType);
						aobj4[6] = localprofile.avatarUrl;
						aobj4[7] = localprofile.packedCircleIds;
						aobj4[8] = s6;
						aobj4[9] = null;
						aobj4[10] = localprofile.phoneNumber;
						aobj4[11] = localprofile.phoneType;
						aobj4[12] = null;
						esmatrixcursor3.addRow(aobj4);
						hashset.add(s5);
						hashset1.add(localprofile.name);
						if (s6 != null)
							hashset2.add(s6);
					} while (true);
					Iterator iterator3 = mPublicProfiles.iterator();
					do {
						if (!iterator3.hasNext())
							break;
						PublicProfile publicprofile1 = (PublicProfile) iterator3
								.next();
						String s3 = publicprofile1.gaiaId;
						if (!hashset.contains(s3)) {
							String s4 = (String) mGaiaIdsAndCircles.get(s3);
							if (!TextUtils.isEmpty(s4)) {
								EsMatrixCursor esmatrixcursor2 = mCursor;
								Object aobj3[] = new Object[13];
								long l2 = mNextId;
								mNextId = 1L + l2;
								aobj3[0] = Long.valueOf(l2);
								aobj3[1] = publicprofile1.personId;
								aobj3[2] = null;
								aobj3[3] = s3;
								aobj3[4] = publicprofile1.name;
								aobj3[5] = Integer
										.valueOf(publicprofile1.profileType);
								aobj3[6] = publicprofile1.avatarUrl;
								aobj3[7] = s4;
								aobj3[8] = null;
								aobj3[9] = null;
								aobj3[10] = null;
								aobj3[11] = null;
								aobj3[12] = publicprofile1.snippet;
								esmatrixcursor2.addRow(aobj3);
								hashset.add(s3);
								hashset1.add(publicprofile1.name);
							}
						}
					} while (true);
				}
				if (!mContacts.isEmpty()) {
					HashMap hashmap = new HashMap();
					Iterator iterator = mContacts.iterator();
					do {
						if (!iterator.hasNext())
							break;
						Contact contact = (Contact) iterator.next();
						if (!hashset1.contains(contact.name)) {
							String s1 = contact.email;
							if (!hashset2.contains(s1)) {
								Object aobj1[] = (Object[]) hashmap.get(s1);
								if (aobj1 != null) {
									String s2 = (String) aobj1[4];
									if ((TextUtils.isEmpty(s2) || s2
											.equalsIgnoreCase(s1))
											&& !TextUtils.isEmpty(contact.name)) {
										aobj1[1] = contact.personId;
										aobj1[2] = contact.lookupKey;
										aobj1[4] = contact.name;
										if (aobj1[10] == null)
											aobj1[10] = contact.phoneNumber;
										if (aobj1[11] == null)
											aobj1[11] = contact.phoneType;
									}
								} else {
									Object aobj2[] = new Object[13];
									long l1 = mNextId;
									mNextId = 1L + l1;
									aobj2[0] = Long.valueOf(l1);
									aobj2[1] = contact.personId;
									aobj2[2] = contact.lookupKey;
									aobj2[3] = null;
									aobj2[4] = contact.name;
									aobj2[5] = Integer.valueOf(1);
									aobj2[6] = null;
									aobj2[7] = null;
									aobj2[8] = null;
									aobj2[9] = contact.email;
									aobj2[10] = contact.phoneNumber;
									aobj2[11] = contact.phoneType;
									aobj2[12] = null;
									hashmap.put(s1, ((Object) (aobj2)));
									mCursor.addRow(aobj2);
								}
							}
						}
					} while (true);
				}
				Iterator iterator1 = mPublicProfiles.iterator();
				do {
					if (!iterator1.hasNext())
						break;
					PublicProfile publicprofile = (PublicProfile) iterator1
							.next();
					String s = publicprofile.gaiaId;
					if (!hashset.contains(s)
							&& !mGaiaIdsAndCircles.containsKey(s)) {
						EsMatrixCursor esmatrixcursor1 = mCursor;
						Object aobj[] = new Object[13];
						long l = mNextId;
						mNextId = 1L + l;
						aobj[0] = Long.valueOf(l);
						aobj[1] = publicprofile.personId;
						aobj[2] = null;
						aobj[3] = s;
						aobj[4] = publicprofile.name;
						aobj[5] = Integer.valueOf(publicprofile.profileType);
						aobj[6] = publicprofile.avatarUrl;
						aobj[7] = null;
						aobj[8] = null;
						aobj[9] = null;
						aobj[10] = null;
						aobj[11] = null;
						aobj[12] = publicprofile.snippet;
						esmatrixcursor1.addRow(aobj);
					}
				} while (true);
				esmatrixcursor = mCursor;
			}
		}
		return esmatrixcursor;
	}

	public final int getPublicProfileCount() {
		return mPublicProfiles.size();
	}

	public final String getQuery() {
		return mQuery;
	}

	public final String getToken() {
		return mToken;
	}

	public final boolean hasMoreResults() {
		return mHasMoreResults;
	}

	public final boolean isParcelable() {
		boolean flag;
		if (mLocalProfiles.size() + mPublicProfiles.size() <= 1000)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public final void onFinishGaiaIdsAndCircles() {
		mGaiaIdsAndCirclesLoaded = true;
	}

	public final void onFinishLocalProfiles() {
		mLocalProfilesLoaded = true;
	}

	public final void onStartContacts() {
		mContacts.clear();
		mCursorValid = false;
	}

	public final void onStartGaiaIdsAndCircles() {
		mGaiaIdsAndCircles.clear();
		mCursorValid = false;
	}

	public final void onStartLocalProfiles() {
		mLocalProfiles.clear();
		mLocalProfilesLoaded = false;
		mCursorValid = false;
	}

	public final void setHasMoreResults(boolean flag) {
		mHasMoreResults = flag;
	}

	public final void setIncludePeopleInCircles(boolean flag) {
		mIncludePeopleInCircles = flag;
	}

	public final void setMyProfile(String s) {
		mMyPersonId = s;
	}

	public final void setQueryString(String s) {
		if (!TextUtils.equals(mQuery, s)) {
			mQuery = s;
			mLocalProfiles.clear();
			mPublicProfiles.clear();
			mLocalProfilesLoaded = false;
			mCursorValid = false;
			mToken = null;
		}
	}

	public final void setToken(String s) {
		mToken = s;
	}

	public void writeToParcel(Parcel parcel, int i) {
		int j = 1;
		parcel.writeString(mMyPersonId);
		parcel.writeString(mQuery);
		parcel.writeString(mToken);
		int k;
		int l;
		if (mHasMoreResults)
			k = j;
		else
			k = 0;
		parcel.writeInt(k);
		if (!mIncludePeopleInCircles)
			j = 0;
		parcel.writeInt(j);
		l = mPublicProfiles.size();
		parcel.writeInt(l);
		for (int i1 = 0; i1 < l; i1++) {
			PublicProfile publicprofile = (PublicProfile) mPublicProfiles
					.get(i1);
			parcel.writeString(publicprofile.personId);
			parcel.writeString(publicprofile.gaiaId);
			parcel.writeString(publicprofile.name);
			parcel.writeInt(publicprofile.profileType);
			parcel.writeString(publicprofile.avatarUrl);
			parcel.writeString(publicprofile.snippet);
		}

	}

	private static final class Contact extends Profile {

		String email;
		String lookupKey;
		String phoneNumber;
		String phoneType;

		Contact(String s, String s1, String s2, String s3, String s4, String s5) {
			super(s, null, s2, 1, null);
			lookupKey = s1;
			email = s3;
			phoneNumber = s4;
			phoneType = s5;
		}
	}

	private static final class LocalProfile extends Profile {

		String email;
		String packedCircleIds;
		String phoneNumber;
		String phoneType;

		LocalProfile(String s, String s1, String s2, int i, String s3,
				String s4, String s5, String s6, String s7) {
			super(s, s1, s2, i, s3);
			packedCircleIds = s4;
			email = s5;
			phoneNumber = s6;
			phoneType = s7;
		}
	}

	private static abstract class Profile {

		String avatarUrl;
		String gaiaId;
		String name;
		String personId;
		int profileType;

		Profile(String s, String s1, String s2, int i, String s3) {
			personId = s;
			gaiaId = s1;
			name = s2;
			profileType = i;
			avatarUrl = s3;
		}
	}

	private static final class PublicProfile extends Profile {

		String snippet;

		PublicProfile(String s, String s1, String s2, int i, String s3,
				String s4) {
			super(s, s1, s2, i, s3);
			snippet = s4;
		}
	}

}
