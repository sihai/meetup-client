/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;

import com.galaxy.meetup.client.android.api.GetPlusOnePeopleOperation;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.ui.fragments.PlusOnePeopleFragment;
import com.galaxy.meetup.server.client.domain.DataPerson;

/**
 * 
 * @author sihai
 *
 */
public class PlusOnePeopleLoader extends EsCursorLoader {

	private final EsAccount mAccount;
    private final String mPlusOneId;
    
    public PlusOnePeopleLoader(Context context, EsAccount esaccount, String s)
    {
        super(context);
        mAccount = esaccount;
        mPlusOneId = s;
    }

    public final Cursor esLoadInBackground()
    {
        Object obj;
        GetPlusOnePeopleOperation getplusonepeopleoperation;
        obj = null;
        getplusonepeopleoperation = new GetPlusOnePeopleOperation(getContext(), mAccount, null, null, mPlusOneId, 50);
        getplusonepeopleoperation.start();
        if(getplusonepeopleoperation.hasError()) {
        	getplusonepeopleoperation.logError("PlusOnePeopleLoader");
        	return ((Cursor) (obj)); 
        }
        List list = getplusonepeopleoperation.getPeople();
        obj = null;
        if(list != null)
        {
            MatrixCursor matrixcursor = new MatrixCursor(PlusOnePeopleFragment.PeopleSetQuery.PROJECTION, list.size());
            int i = 0;
            for(Iterator iterator = list.iterator(); iterator.hasNext();)
            {
                DataPerson dataperson = (DataPerson)iterator.next();
                Object aobj[] = new Object[5];
                int j = i + 1;
                aobj[0] = Integer.valueOf(i);
                aobj[1] = (new StringBuilder("g:")).append(dataperson.obfuscatedId).toString();
                aobj[2] = dataperson.obfuscatedId;
                aobj[3] = dataperson.userName;
                aobj[4] = dataperson.photoUrl;
                matrixcursor.addRow(aobj);
                i = j;
            }

            obj = matrixcursor;
        }
        	
        return ((Cursor) (obj));
    }
}
