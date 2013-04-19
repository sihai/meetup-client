/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.EsMatrixCursor;
import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.api.PeopleSearchQueryOperation;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsAvatarData;
import com.galaxy.meetup.server.client.domain.DataCircleMemberId;
import com.galaxy.meetup.server.client.domain.DataCircleMemberProperties;
import com.galaxy.meetup.server.client.domain.PeopleResult;

/**
 * 
 * @author sihai
 *
 */
public class PublicProfileSearchLoader extends EsCursorLoader {

	public static final MatrixCursor ABORTED = new MatrixCursor(new String[0]);
    private final EsAccount mAccount;
    private boolean mIncludePlusPages;
    private final int mMinQueryLength = 2;
    private volatile PeopleSearchQueryOperation mOperation;
    private final String mProjection[];
    private final String mQuery;
    private final String mToken;
    
    public PublicProfileSearchLoader(Context context, EsAccount esaccount, String as[], String s, int i, boolean flag, boolean flag1, 
            String s1)
    {
        super(context);
        mIncludePlusPages = true;
        mAccount = esaccount;
        mProjection = as;
        mQuery = s;
        mIncludePlusPages = flag;
        String s2;
        if(flag1)
            s2 = "gaia_id IS NOT NULL";
        else
            s2 = null;
        setSelection(s2);
        mToken = s1;
    }

    private void abort()
    {
        PeopleSearchQueryOperation peoplesearchqueryoperation = mOperation;
        if(peoplesearchqueryoperation != null)
            peoplesearchqueryoperation.abort();
        mOperation = null;
    }

    public final boolean cancelLoad()
    {
        abort();
        return super.cancelLoad();
    }

    public final Cursor esLoadInBackground() {
        if(TextUtils.isEmpty(mQuery) || mQuery.length() < mMinQueryLength) 
        	return new EsMatrixCursor(mProjection);
        
        PeopleSearchQueryOperation peoplesearchqueryoperation = new PeopleSearchQueryOperation(getContext(), mAccount, mQuery, mToken, mIncludePlusPages, null, null);
        mOperation = peoplesearchqueryoperation;
        peoplesearchqueryoperation.start();
        mOperation = null;
        if(peoplesearchqueryoperation.isAborted()) {
        	return ABORTED;
        }
        if(peoplesearchqueryoperation.hasError()) {
        	peoplesearchqueryoperation.logError("PublicProfileSearch");
        	return null;
        }
        
        Object obj = new EsMatrixCursor(mProjection);
        List list = peoplesearchqueryoperation.getPeopleSearchResults();
        String s = peoplesearchqueryoperation.getContinuationToken();
        Resources resources = getContext().getResources();
        Object aobj[] = new Object[mProjection.length];
        aobj[0] = mToken;
        aobj[1] = s;
        ((EsMatrixCursor) (obj)).addRow(aobj);
        int i;
        int j;
        if(list != null)
            i = list.size();
        else
            i = 0;
        j = 0;
        while(j < i) 
        {
            PeopleResult peopleresult = (PeopleResult)list.get(j);
            DataCircleMemberProperties datacirclememberproperties = peopleresult.memberProperties;
            DataCircleMemberId datacirclememberid = peopleresult.memberId;
            if(datacirclememberid != null && datacirclememberproperties != null)
            {
                Object aobj1[] = new Object[mProjection.length];
                int k = 0;
                while(k < mProjection.length) 
                {
                    String s1 = mProjection[k];
                    if("_id".equals(s1))
                        aobj1[k] = Integer.valueOf(j);
                    else
                    if("gaia_id".equals(s1))
                        aobj1[k] = datacirclememberid.obfuscatedGaiaId;
                    else
                    if("person_id".equals(s1))
                        aobj1[k] = (new StringBuilder("g:")).append(datacirclememberid.obfuscatedGaiaId).toString();
                    else
                    if("name".equals(s1))
                        aobj1[k] = datacirclememberproperties.displayName;
                    else
                    if("profile_type".equals(s1))
                    {
                        if(datacirclememberproperties.entityInfo != null && datacirclememberproperties.entityInfo.type != null)
                            aobj1[k] = datacirclememberproperties.entityInfo.type;
                        else
                            aobj1[k] = Integer.valueOf(1);
                    } else
                    if("avatar".equals(s1))
                        aobj1[k] = EsAvatarData.compressAvatarUrl(datacirclememberproperties.photoUrl);
                    else
                    if("snippet".equals(s1))
                    {
                        String s2 = peopleresult.snippetHtml;
                        if(s2 == null)
                            if(datacirclememberproperties.company != null)
                            {
                                if(datacirclememberproperties.occupation != null)
                                {
                                    int l = R.string.people_search_job;
                                    Object aobj2[] = new Object[2];
                                    aobj2[0] = datacirclememberproperties.occupation;
                                    aobj2[1] = datacirclememberproperties.company;
                                    s2 = resources.getString(l, aobj2);
                                } else
                                {
                                    s2 = datacirclememberproperties.company;
                                }
                            } else
                            {
                                s2 = datacirclememberproperties.occupation;
                            }
                        aobj1[k] = s2;
                    }
                    k++;
                }
                ((EsMatrixCursor) (obj)).addRow(aobj1);
            }
            j++;
        }
        
        return (EsMatrixCursor) (obj);
    }

    public final String getToken()
    {
        return mToken;
    }

    public final void onAbandon()
    {
        abort();
    }
}
