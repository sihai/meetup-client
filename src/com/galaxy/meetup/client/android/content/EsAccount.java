/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TODO
 * @author sihai
 *
 */
public class EsAccount implements Parcelable {
	
	private final String mDisplayName;
    private final String mGaiaId;
    private final String mPassword = "";
    private final int mIndex;
    private final boolean mIsChild;
    private final boolean mIsPlusPage;
    private final String mName;
    private final String mRealTimeChatParticipantId;
    
	private EsAccount(Parcel parcel) {
		mName = parcel.readString();
		mGaiaId = parcel.readString();
		mRealTimeChatParticipantId = (new StringBuilder("g:")).append(mGaiaId).toString();
		mDisplayName = parcel.readString();
		mIndex = parcel.readInt();
		mIsChild = parcel.readInt() == 1;
		mIsPlusPage = parcel.readInt() == 1;
	}

	EsAccount(Parcel parcel, byte byte0) {
		this(parcel);
	}
	
	public EsAccount(String s, String s1, String s2, boolean flag, boolean flag1, int i) {
        mName = s;
        mGaiaId = s1;
        mRealTimeChatParticipantId = (new StringBuilder("g:")).append(mGaiaId).toString();
        mDisplayName = s2;
        mIsChild = flag;
        mIsPlusPage = flag1;
        mIndex = i;
    }
	
	public int describeContents() {
        return 0;
    }
	
	public boolean equals(Object obj) {
		
		if(null == obj) {
			return false;
		} else {
			if(!(obj instanceof EsAccount)) {
				return false;
			} else {
				EsAccount esaccount = (EsAccount)obj;
				if(!mName.equals(esaccount.mName)) {
					return false;
				} else {
					if(mGaiaId == null || esaccount.mGaiaId == null)
	                    return true;
	                else
	                   return mGaiaId.equals(esaccount.mGaiaId);
				}
			}
		}
    }

    public final String getDisplayName() {
        return mDisplayName;
    }

    public final String getGaiaId() {
        if(mGaiaId == null)
            throw new IllegalStateException("Gaia id not yet set. Out of box not yet done?");
        else
            return mGaiaId;
    }

    public final int getIndex() {
        return mIndex;
    }

    public final String getName() {
        return mName;
    }
    
    public final String getPassword() {
        return mPassword;
    }
    
    public final String getPersonId() {
        return (new StringBuilder("g:")).append(mGaiaId).toString();
    }

    public final String getRealTimeChatParticipantId() {
        return mRealTimeChatParticipantId;
    }

    public final boolean hasGaiaId() {
        boolean flag;
        if(mGaiaId != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public int hashCode() {
        return mName.hashCode();
    }

    public final boolean isChild() {
        return mIsChild;
    }

    public final boolean isMyGaiaId(String s) {
        boolean flag;
        if(s == null)
            flag = false;
        else
            flag = s.equals(mGaiaId);
        return flag;
    }

    public final boolean isPlusPage() {
        return mIsPlusPage;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder(64);
        stringbuilder.append("Account name: ").append(mName);
        stringbuilder.append(", Gaia id: ").append(mGaiaId);
        stringbuilder.append(", Display name: ").append(mDisplayName);
        stringbuilder.append(", Plotnikov index: ").append(mIndex);
        stringbuilder.append(", isPlusPage: ").append(mIsPlusPage);
        return stringbuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        int j = 1;
        parcel.writeString(mName);
        parcel.writeString(mGaiaId);
        parcel.writeString(mDisplayName);
        parcel.writeInt(mIndex);
        int k;
        if(mIsChild)
            k = j;
        else
            k = 0;
        parcel.writeInt(k);
        if(!mIsPlusPage)
            j = 0;
        parcel.writeInt(j);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel) {
            return new EsAccount(parcel, (byte)0);
        }

        public final Object[] newArray(int i) {
            return new EsAccount[i];
        }
    };
}
