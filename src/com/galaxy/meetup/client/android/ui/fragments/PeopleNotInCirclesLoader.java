/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.EsMatrixCursor;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsPeopleData;
import com.galaxy.meetup.client.android.content.EsProvider;
import com.galaxy.meetup.client.android.content.PersonData;

/**
 * 
 * @author sihai
 *
 */
public class PeopleNotInCirclesLoader extends EsCursorLoader {

	private static final String PROJECTION[] = {
        "person_id", "in_my_circles"
    };
    private final EsAccount mAccount;
    private final Map mPeopleMap;
    private final String mProjection[];
    
    public PeopleNotInCirclesLoader(Context context, EsAccount esaccount, String as[], Map hashmap, boolean flag)
    {
        super(context);
        setUri(EsProvider.CONTACTS_URI);
        mAccount = esaccount;
        mProjection = as;
        mPeopleMap = hashmap;
        String s;
        if(flag)
            s = "gaia_id IS NOT NULL";
        else
            s = null;
        setSelection(s);
    }

    private void buildSortedMatrixCursor(EsMatrixCursor esmatrixcursor, Map hashmap)
    {
        int i = esmatrixcursor.getColumnIndex("_id");
        int j = esmatrixcursor.getColumnIndex("person_id");
        final int nameColumnIndex = esmatrixcursor.getColumnIndex("name");
        int k = esmatrixcursor.getColumnIndex("gaia_id");
        ArrayList arraylist = new ArrayList();
        int l = 0;
        for(Iterator iterator = hashmap.entrySet().iterator(); iterator.hasNext();)
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            String s = (String)entry.getKey();
            PersonData persondata = (PersonData)entry.getValue();
            Object aobj[] = new Object[mProjection.length];
            int i1 = l + 1;
            aobj[i] = Integer.valueOf(l);
            aobj[j] = s;
            aobj[nameColumnIndex] = persondata.getName();
            if(!TextUtils.isEmpty(persondata.getObfuscatedId()))
                aobj[k] = persondata.getObfuscatedId();
            arraylist.add(((Object) (aobj)));
            l = i1;
        }

        Collections.sort(arraylist, new Comparator() {

            public final int compare(Object obj, Object obj1)
            {
                Object aobj1[] = (Object[])obj;
                Object aobj2[] = (Object[])obj1;
                String s1 = (String)aobj1[nameColumnIndex];
                String s2;
                String s3;
                if(s1 == null)
                    s2 = "";
                else
                    s2 = s1;
                s3 = (String)aobj2[nameColumnIndex];
                if(s3 == null)
                    s3 = "";
                return s2.compareToIgnoreCase(s3);
            }

        });
        for(Iterator iterator1 = arraylist.iterator(); iterator1.hasNext(); esmatrixcursor.addRow((Object[])iterator1.next()));
    }

    private boolean removePeopleInMyCircles(Map hashmap)
    {
        Cursor cursor = null;
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("person_id IN(");
        for(int i = 0; i < mPeopleMap.size(); i++)
        {
            if(i > 0)
                stringbuilder.append(',');
            stringbuilder.append('?');
        }

        stringbuilder.append(')');
        String s = stringbuilder.toString();
        String as[] = (String[])mPeopleMap.keySet().toArray(new String[0]);
        
        boolean flag = false;
        try {
        	cursor = EsPeopleData.getPeople(getContext(), mAccount, null, null, PROJECTION, s, as);
        	if(null != cursor) {
        		while(cursor.moveToNext()) {
        			if(cursor.getInt(1) != 0) {
                        hashmap.remove(cursor.getString(0));
                        flag = true;
        			}
        		}
        	}
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
        
        return flag;
    }

    public final Cursor esLoadInBackground() {
        EsMatrixCursor esmatrixcursor = new EsMatrixCursor(mProjection);
        if(mPeopleMap.size() != 0) {
        	Map hashmap = new HashMap(mPeopleMap);
            if(!removePeopleInMyCircles(hashmap))
                esmatrixcursor = null;
            else
            if(!hashmap.isEmpty())
                buildSortedMatrixCursor(esmatrixcursor, hashmap);
        }
        return esmatrixcursor;
    }
}
