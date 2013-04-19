/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.EsMatrixCursor;
import com.galaxy.meetup.client.android.api.GetActivityOperation;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class DesktopActivityIdLoader extends EsCursorLoader {

	private final EsAccount mAccount;
    private final String mDesktopActivityId;
    private final String mOwnerGaiaId;
    
    public DesktopActivityIdLoader(Context context, EsAccount esaccount, String s, String s1)
    {
        super(context);
        mAccount = esaccount;
        mDesktopActivityId = s;
        mOwnerGaiaId = s1;
    }

    public final Cursor esLoadInBackground()
    {
        Cursor cursor = null;
        (new ArrayList()).add(mDesktopActivityId);
        GetActivityOperation getactivityoperation = new GetActivityOperation(getContext(), mAccount, mDesktopActivityId, mOwnerGaiaId, null, null, null);
        getactivityoperation.start();
        if(getactivityoperation.getException() != null)
        {
            if(EsLog.isLoggable("DesktopActivityIdLoader", 6))
                Log.e("DesktopActivityIdLoader", (new StringBuilder("Cannot resolve desktop activity ID: ")).append(mDesktopActivityId).toString(), getactivityoperation.getException());
        } else if(getactivityoperation.hasError()) {
        	if(EsLog.isLoggable("DesktopActivityIdLoader", 6))
                Log.e("DesktopActivityIdLoader", (new StringBuilder("Cannot resolve  desktop activity ID: ")).append(mDesktopActivityId).append(": ").append(getactivityoperation.getErrorCode()).toString());
        } else {
        	cursor = new EsMatrixCursor(new String[] {
                    "activity_id"
                });
            Object aobj[] = new Object[1];
            aobj[0] = getactivityoperation.getResponseUpdateId();
            ((EsMatrixCursor) (cursor)).addRow(aobj);
        }
        return cursor;
    }
}
