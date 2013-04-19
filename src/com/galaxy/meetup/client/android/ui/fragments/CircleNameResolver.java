/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.content.EsAccount;

/**
 * 
 * @author sihai
 *
 */
public class CircleNameResolver implements LoaderCallbacks {

	private final EsAccount mAccount;
    private Map mCircleNames;
    private final Context mContext;
    private final DataSetObservable mDataSetObservable;
    private boolean mLoaded;
    private final int mLoaderId;
    private final LoaderManager mLoaderManager;
    private final StringBuilder mStringBuilder;
    
    public CircleNameResolver(Context context, LoaderManager loadermanager, EsAccount esaccount)
    {
        this(context, loadermanager, esaccount, 0);
    }

    public CircleNameResolver(Context context, LoaderManager loadermanager, EsAccount esaccount, int i)
    {
        mDataSetObservable = new DataSetObservable();
        mStringBuilder = new StringBuilder();
        mContext = context;
        mLoaderManager = loadermanager;
        mAccount = esaccount;
        mLoaderId = i + 2048;
    }

    public final synchronized List getCircleNameListForPackedIds(String s) {
        List arraylist = new ArrayList();
        if(mLoaded && !TextUtils.isEmpty(s)) {
        	int i = 0;
        	int j;
        	for(;;) {
        		if(i >= s.length()) {
        			break;
        		}
        		j = s.indexOf('|', i);
        		if(j == -1)
        			j = s.length();
        		String s1 = (String)mCircleNames.get(s.substring(i, j));
                if(s1 != null)
                    arraylist.add(s1);
                i = j + 1;
        	}
        }
        return arraylist;
    }

    public final synchronized CharSequence getCircleNamesForPackedIds(String s) {
    	
    	if(mLoaded && !TextUtils.isEmpty(s)) {
    		mStringBuilder.setLength(0);
            int j;
            for(int i = 0; i < s.length(); i = j + 1)
            {
                j = s.indexOf('|', i);
                if(j == -1)
                    j = s.length();
                String s3 = (String)mCircleNames.get(s.substring(i, j));
                if(s3 != null)
                {
                    if(mStringBuilder.length() != 0)
                        mStringBuilder.append(", ");
                    mStringBuilder.append(s3);
                }
            }

            return mStringBuilder.toString();
    	} else {
    		return "";
    	}
    	
    }

    public final void initLoader()
    {
        mLoaderManager.initLoader(mLoaderId, null, this);
    }

    public final boolean isLoaded()
    {
        return mLoaded;
    }

    public final Loader onCreateLoader(int i, Bundle bundle)
    {
        return new CircleListLoader(mContext, mAccount, 0, new String[] {
            "circle_id", "circle_name"
        });
    }

    public final void onLoadFinished(Loader loader, Object obj)
    {
        Cursor cursor = (Cursor)obj;
        HashMap hashmap = new HashMap();
        if(cursor != null && cursor.moveToFirst())
            do
                hashmap.put(cursor.getString(0), cursor.getString(1));
            while(cursor.moveToNext());
        mCircleNames = hashmap;
        mLoaded = true;
        mDataSetObservable.notifyChanged();
    }

    public final void onLoaderReset(Loader loader)
    {
        mDataSetObservable.notifyInvalidated();
    }

    public final void registerObserver(DataSetObserver datasetobserver)
    {
        mDataSetObservable.registerObserver(datasetobserver);
    }

}
