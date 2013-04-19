/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsProvider;

/**
 * 
 * @author sihai
 *
 */
public class CircleMembershipManager {

	private static String sAccountName;
    private static ConcurrentHashMap sCompletedRequests = new ConcurrentHashMap();
    private static Handler sHandler;
    private static ConcurrentHashMap sPendingRequests = new ConcurrentHashMap();
    private static boolean sPeopleListVisible;
    
    private static void checkAccount(EsAccount esaccount)
    {
        if(!TextUtils.equals(sAccountName, esaccount.getName()))
        {
            sPendingRequests.clear();
            sAccountName = esaccount.getName();
        }
    }

    public static boolean isCircleMembershipRequestPending(String s)
    {
        return sPendingRequests.containsKey(s);
    }

    public static void onPeopleListVisibilityChange(boolean flag)
    {
        if(sPeopleListVisible != flag)
        {
            sPeopleListVisible = flag;
            if(flag)
                sCompletedRequests.clear();
        }
    }

    public static void onStartAddToCircleRequest(Context context, EsAccount esaccount, String s)
    {
        checkAccount(esaccount);
        sPendingRequests.put(s, "");
        context.getContentResolver().notifyChange(EsProvider.CONTACTS_URI, null);
    }

    public static void setCircleMembershipResult(Context context, EsAccount esaccount, String s, String s1, boolean flag)
    {
        checkAccount(esaccount);
        sCompletedRequests.put(s, "");
        sPendingRequests.remove(s);
        if(!flag)
        {
            AndroidNotification.showCircleAddFailedNotification(context, esaccount, s, s1);
            context.getContentResolver().notifyChange(EsProvider.CONTACTS_URI, null);
        }
    }

    public static void showToastIfNeeded(final Context context, EsAccount esaccount) {

        if(sPeopleListVisible || sPendingRequests.size() > 0) {
        	return; 
        }
        
        Cursor cursor = null;
        int i;
        boolean flag = false;
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("in_my_circles!= 0 AND person_id IN (");
        for(Iterator iterator = sCompletedRequests.keySet().iterator(); iterator.hasNext();)
        {
            DatabaseUtils.appendEscapedSQLString(stringbuilder, (String)iterator.next());
            stringbuilder.append(',');
            flag = true;
        }

        sCompletedRequests.clear();
        if(flag)
        	stringbuilder.setLength(-1 + stringbuilder.length());
        stringbuilder.append(")");
        
        try {
        	cursor = context.getContentResolver().query(EsProvider.appendAccountParameter(EsProvider.CONTACTS_URI, esaccount), new String[] {
                "person_id"
            }, stringbuilder.toString(), null, null);
            i = 0;
            if(null != cursor && cursor.getCount() != 0) {
            	Resources resources = context.getResources();
                int j = R.plurals.added_to_circle_notification_message;
                Object aobj[] = new Object[1];
                aobj[0] = Integer.valueOf(i);
                final String s = resources.getQuantityString(j, i, aobj);
                if(sHandler == null)
                    sHandler = new Handler(Looper.getMainLooper());
                sHandler.post(new Runnable() {

                    public final void run()
                    {
                        Toast.makeText(context, s, 0).show();
                    }

                });
            }
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
    }
}
