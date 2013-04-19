/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.content;

import org.json.JSONArray;
import org.json.JSONException;

import android.net.Uri;
import android.util.Log;

import com.galaxy.meetup.client.android.api.CallToActionData;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class PreviewRequestData {

	public final CallToActionData callToAction;
    public final Uri uri;
    
    public PreviewRequestData(String s, CallToActionData calltoactiondata)
    {
        uri = Uri.parse(s);
        callToAction = calltoactiondata;
    }

    public static PreviewRequestData fromSelectionArg(String s) {
    	
    	try {
	        JSONArray jsonarray = new JSONArray(s);
	        int i = jsonarray.length();
	        CallToActionData calltoactiondata = null;
	        if(i > 1)
	        {
	            String s3;
	            String s4;
	            String s5;
	            if(jsonarray.isNull(1))
	                s3 = null;
	            else
	                s3 = jsonarray.getString(1);
	            if(jsonarray.isNull(2))
	                s4 = null;
	            else
	                s4 = jsonarray.getString(2);
	            if(jsonarray.isNull(3))
	                s5 = null;
	            else
	                s5 = jsonarray.getString(3);
	            calltoactiondata = new CallToActionData(s3, s4, s5);
	        }
	        if(jsonarray.isNull(0)) 
	        	return null;
	        return new PreviewRequestData(jsonarray.getString(0), calltoactiondata);
    	} catch (JSONException jsonexception) {
    		if(EsLog.isLoggable("PreviewRequestData", 5))
                Log.w("PreviewRequestData", "Failed to deserialize PreviewRequestData JSON.");
    		return null;
    	}
    }

    public final boolean equals(Object obj) {
        boolean flag = true;
        if(this == obj) {
        	return true;
        }
        
        if(!(obj instanceof PreviewRequestData))
        {
            return false;
        } else
        {
            PreviewRequestData previewrequestdata = (PreviewRequestData)obj;
            if(uri != previewrequestdata.uri && (uri == null || !uri.equals(previewrequestdata.uri)) || callToAction != previewrequestdata.callToAction && (callToAction == null || !callToAction.equals(previewrequestdata.callToAction)))
                return false;
        }
        return true;
    }

    public final int hashCode()
    {
        int i;
        int j;
        CallToActionData calltoactiondata;
        int k;
        if(uri == null)
            i = 0;
        else
            i = uri.hashCode();
        j = 31 * (i + 527);
        calltoactiondata = callToAction;
        k = 0;
        if(calltoactiondata != null)
            k = callToAction.hashCode();
        return j + k;
    }
}
