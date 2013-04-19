/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.EsCursorLoader;
import com.galaxy.meetup.client.android.EsMatrixCursor;

/**
 * 
 * @author sihai
 *
 */
public class AndroidContactSearchLoader extends EsCursorLoader {

	private static final String EMAIL_PROJECTION[] = {
        "lookup", "display_name", "data1"
    };
    private static final String PHONE_PROJECTION[] = {
        "lookup", "display_name", "data1", "data2", "data3"
    };
    private boolean mIncludePhoneNumbers;
    private final int mMinQueryLength = 2;
    private final String mProjection[];
    private final String mQuery;
	
	public AndroidContactSearchLoader(Context context, String as[], String s, int i, boolean flag) {
        super(context);
        mProjection = as;
        mQuery = s;
        mIncludePhoneNumbers = flag;
    }
	
	private void addPhoneNumberRows(EsMatrixCursor esmatrixcursor, Map hashmap, String s) {
        List arraylist = (List)hashmap.get(s);
        if(null == arraylist) {
        	return;
        }
    
        int i = 0;
        do
        {
            if(i >= arraylist.size())
                break;
            PhoneNumber phonenumber = (PhoneNumber)arraylist.get(i);
            Object aobj[] = new Object[mProjection.length];
            int j = 0;
            while(j < mProjection.length) 
            {
                String s1 = mProjection[j];
                if("person_id".equals(s1))
                    aobj[j] = (new StringBuilder("p:")).append(phonenumber.phoneNumber).toString();
                else
                if("lookup_key".equals(s1))
                    aobj[j] = phonenumber.lookupKey;
                else
                if("name".equals(s1))
                    aobj[j] = phonenumber.name;
                else
                if("phone".equals(s1))
                    aobj[j] = phonenumber.phoneNumber;
                else
                if("phone_type".equals(s1))
                    aobj[j] = phonenumber.phoneType;
                j++;
            }
            esmatrixcursor.addRow(aobj);
            i++;
        } while(true);
        hashmap.remove(s);
    }

    private Object[] buildEmailRow(Cursor cursor)
    {
        String s = cursor.getString(2);
        Object aobj[] = new Object[mProjection.length];
        int i = 0;
        while(i < mProjection.length) 
        {
            String s1 = mProjection[i];
            if("person_id".equals(s1))
                aobj[i] = (new StringBuilder("e:")).append(s).toString();
            else
            if("lookup_key".equals(s1))
                aobj[i] = cursor.getString(0);
            else
            if("name".equals(s1))
                aobj[i] = cursor.getString(1);
            else
            if("email".equals(s1))
                aobj[i] = s;
            i++;
        }
        return aobj;
    }

    private Cursor findEmailAddresses() {
        EsMatrixCursor esmatrixcursor;
        Cursor cursor = null;
        esmatrixcursor = new EsMatrixCursor(mProjection);
        Uri uri = Uri.withAppendedPath(android.provider.ContactsContract.CommonDataKinds.Email.CONTENT_FILTER_URI, Uri.encode(mQuery));
        try {
	        cursor = getContext().getContentResolver().query(uri, EMAIL_PROJECTION, null, null, null);
	        do
	        {
	            if(!cursor.moveToNext())
	                break;
	            if(!TextUtils.isEmpty(cursor.getString(2)))
	                esmatrixcursor.addRow(buildEmailRow(cursor));
	        } while(true);
        } finally {
        	if(null != cursor) {
        		cursor.close();
        	}
        }
        
        return esmatrixcursor;
    }

    private Cursor findEmailAddressesAndPhoneNumbers() {
        EsMatrixCursor esmatrixcursor;
        ContentResolver contentresolver;
        android.content.res.Resources resources;
        List arraylist;
        Map hashmap;
        Cursor cursor = null;
        Cursor cursor1 = null;
        esmatrixcursor = new EsMatrixCursor(mProjection);
        contentresolver = getContext().getContentResolver();
        resources = getContext().getResources();
        arraylist = new ArrayList();
        hashmap = new HashMap();
        
        try {
	        cursor = contentresolver.query(Uri.withAppendedPath(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(mQuery)), PHONE_PROJECTION, null, null, null);
	        String s;
	        do
	        {
	            if(!cursor.moveToNext())
	                break;
	            String s2 = cursor.getString(2);
	            if(!TextUtils.isEmpty(s2))
	            {
	                String s3 = cursor.getString(0);
	                PhoneNumber phonenumber = new PhoneNumber();
	                phonenumber.lookupKey = s3;
	                phonenumber.name = cursor.getString(1);
	                phonenumber.phoneNumber = s2;
	                CharSequence charsequence = android.provider.ContactsContract.CommonDataKinds.Phone.getTypeLabel(resources, cursor.getInt(3), cursor.getString(4));
	                if(charsequence != null)
	                    phonenumber.phoneType = charsequence.toString();
	                ArrayList arraylist1 = (ArrayList)hashmap.get(s3);
	                if(arraylist1 == null)
	                {
	                    arraylist1 = new ArrayList();
	                    hashmap.put(s3, arraylist1);
	                    arraylist.add(s3);
	                }
	                arraylist1.add(phonenumber);
	            }
	        } while(true);
	        cursor.close();
	        cursor1 = contentresolver.query(Uri.withAppendedPath(android.provider.ContactsContract.CommonDataKinds.Email.CONTENT_FILTER_URI, Uri.encode(mQuery)), EMAIL_PROJECTION, null, null, null);
	        s = null;
	        do
	        {
	            if(!cursor1.moveToNext())
	                break;
	            String s1 = cursor1.getString(0);
	            if(!s1.equals(s))
	            {
	                addPhoneNumberRows(esmatrixcursor, hashmap, s);
	                s = s1;
	            }
	            if(!TextUtils.isEmpty(cursor1.getString(2)))
	                esmatrixcursor.addRow(buildEmailRow(cursor1));
	        } while(true);
	        if(null != s) {
	        	addPhoneNumberRows(esmatrixcursor, hashmap, s);
	        }
	        for(int i = 0; i < arraylist.size(); i++)
	            addPhoneNumberRows(esmatrixcursor, hashmap, (String)arraylist.get(i));
        } finally {
	    	if(null != cursor) {
	    		cursor.close();
	    	}
	    	if(null != cursor1) {
	    		cursor1.close();
	    	}
	    }
        return esmatrixcursor;
    }

    public final Cursor esLoadInBackground() {
        Object obj;
        if(TextUtils.isEmpty(mQuery) || mQuery.length() < mMinQueryLength)
            obj = new EsMatrixCursor(mProjection);
        else
        if(mIncludePhoneNumbers)
            obj = findEmailAddressesAndPhoneNumbers();
        else
            obj = findEmailAddresses();
        return ((Cursor) (obj));
    }
	
	
	private static final class PhoneNumber {

        String lookupKey;
        String name;
        String phoneNumber;
        String phoneType;

        PhoneNumber() {
        }

    }
}
