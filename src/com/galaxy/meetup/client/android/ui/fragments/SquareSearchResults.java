/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.EsMatrixCursor;
import com.galaxy.meetup.server.client.domain.SquareId;
import com.galaxy.meetup.server.client.domain.SquareResult;

/**
 * 
 * @author sihai
 *
 */
public class SquareSearchResults implements Parcelable {

	public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public final Object createFromParcel(Parcel parcel)
        {
            return new SquareSearchResults(parcel);
        }

        public final Object[] newArray(int i)
        {
            return new SquareSearchResults[i];
        }

    };
    private String mContinuationToken;
    private EsMatrixCursor mCursor;
    private boolean mCursorValid;
    private boolean mHasMoreResults;
    private long mNextId;
    private final String mProjection[];
    private String mQuery;
    private List mResults;
    
    public SquareSearchResults(Parcel parcel)
    {
        mQuery = parcel.readString();
        mContinuationToken = parcel.readString();
        boolean flag;
        int i;
        int j;
        if(parcel.readInt() != 0)
            flag = true;
        else
            flag = false;
        mHasMoreResults = flag;
        mProjection = parcel.createStringArray();
        i = parcel.readInt();
        mResults = new ArrayList(i);
        j = 0;
        while(j < i) 
        {
            SquareResult squareresult = new SquareResult();
            squareresult.squareId = new SquareId();
            squareresult.squareId.obfuscatedGaiaId = parcel.readString();
            squareresult.displayName = parcel.readString();
            squareresult.photoUrl = parcel.readString();
            squareresult.snippetHtml = parcel.readString();
            if(parcel.readInt() != 0)
                squareresult.memberCount = Long.valueOf(parcel.readLong());
            if(parcel.readInt() != 0)
            {
                boolean flag1;
                if(parcel.readInt() != 0)
                    flag1 = true;
                else
                    flag1 = false;
                squareresult.privatePosts = Boolean.valueOf(flag1);
            }
            j++;
        }
    }
    
    public SquareSearchResults(String as[])
    {
        mProjection = as;
        mResults = new ArrayList();
    }

    public final void addResults(List list)
    {
        mResults.addAll(list);
        mCursorValid = false;
    }

    public int describeContents()
    {
        return 0;
    }

    public final String getContinuationToken()
    {
        return mContinuationToken;
    }

    public final int getCount()
    {
        return mResults.size();
    }

    public final Cursor getCursor() {
    	
    	if(mCursorValid) {
    		return mCursor;
    	}
    	
    	mCursor = new EsMatrixCursor(mProjection);
        mCursorValid = true;
        int i = -1;
        int j = -1;
        int k = -1;
        int l = -1;
        int i1 = -1;
        int j1 = -1;
        int k1 = -1;
        int l1 = 0;
        while(l1 < mProjection.length) 
        {
            String s = mProjection[l1];
            if("_id".equals(s))
                i = l1;
            else
            if("square_id".equals(s))
                j = l1;
            else
            if("square_name".equals(s))
                k = l1;
            else
            if("photo_url".equals(s))
                l = l1;
            else
            if("post_visibility".equals(s))
                i1 = l1;
            else
            if("member_count".equals(s))
                j1 = l1;
            else
            if("snippet".equals(s))
                k1 = l1;
            l1++;
        }
        int i2 = mResults.size();
        int j2 = 0;
        while(j2 < i2) 
        {
            SquareResult squareresult = (SquareResult)mResults.get(j2);
            Object aobj[] = new Object[mProjection.length];
            if(i >= 0)
            {
                long l2 = mNextId;
                mNextId = 1L + l2;
                aobj[i] = Long.valueOf(l2);
            }
            if(j >= 0)
                aobj[j] = squareresult.squareId.obfuscatedGaiaId;
            if(k >= 0)
                aobj[k] = squareresult.displayName;
            if(l >= 0)
                aobj[l] = squareresult.photoUrl;
            if(i1 >= 0 && squareresult.privatePosts != null)
            {
                int k2;
                if(squareresult.privatePosts.booleanValue())
                    k2 = 1;
                else
                    k2 = 0;
                aobj[i1] = Integer.valueOf(k2);
            }
            if(j1 >= 0)
                aobj[j1] = squareresult.memberCount;
            if(k1 >= 0)
                aobj[k1] = squareresult.snippetHtml;
            mCursor.addRow(aobj);
            j2++;
        }
        
        return mCursor;
    	
    }

    public final String getQuery()
    {
        return mQuery;
    }

    public final boolean hasMoreResults()
    {
        return mHasMoreResults;
    }

    public final boolean isEmpty()
    {
        return mResults.isEmpty();
    }

    public final boolean isParcelable()
    {
        boolean flag;
        if(getCount() <= 1000)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final void setContinuationToken(String s)
    {
        mContinuationToken = s;
    }

    public final void setHasMoreResults(boolean flag)
    {
        mHasMoreResults = flag;
    }

    public final void setQueryString(String s)
    {
        if(!TextUtils.equals(mQuery, s))
        {
            mQuery = s;
            mResults.clear();
            mCursor = null;
            mCursorValid = false;
            mContinuationToken = null;
        }
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(mQuery);
        parcel.writeString(mContinuationToken);
        int j;
        int k;
        int l;
        if(mHasMoreResults)
            j = 1;
        else
            j = 0;
        parcel.writeInt(j);
        parcel.writeStringArray(mProjection);
        if(mResults != null)
            k = mResults.size();
        else
            k = 0;
        parcel.writeInt(k);
        l = 0;
        while(l < k) 
        {
            SquareResult squareresult = (SquareResult)mResults.get(l);
            parcel.writeString(squareresult.squareId.obfuscatedGaiaId);
            parcel.writeString(squareresult.displayName);
            parcel.writeString(squareresult.photoUrl);
            parcel.writeString(squareresult.snippetHtml);
            if(squareresult.memberCount != null)
            {
                parcel.writeInt(1);
                parcel.writeLong(squareresult.memberCount.longValue());
            } else
            {
                parcel.writeInt(0);
            }
            if(squareresult.privatePosts != null)
            {
                parcel.writeInt(1);
                int i1;
                if(squareresult.privatePosts.booleanValue())
                    i1 = 1;
                else
                    i1 = 0;
                parcel.writeInt(i1);
            } else
            {
                parcel.writeInt(0);
            }
            l++;
        }
    }
}
