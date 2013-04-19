/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author sihai
 *
 */
public class SquareSearchLoaderResults {

	private final String mNextToken;
    private final List mResults;
    private final String mToken;
    
	public SquareSearchLoaderResults()
    {
        mToken = null;
        mNextToken = null;
        mResults = new ArrayList();
    }

    public SquareSearchLoaderResults(String s, String s1, List list)
    {
        mToken = s;
        mNextToken = s1;
        if(list == null)
            list = new ArrayList();
        mResults = list;
    }

    public final String getNextToken()
    {
        return mNextToken;
    }

    public final List getResults()
    {
        return mResults;
    }

    public final String getToken()
    {
        return mToken;
    }
    
}
