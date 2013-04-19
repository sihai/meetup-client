/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network;

import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.network.http.HttpRequestConfiguration;
import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public abstract class PlusiOperation extends ApiaryOperation {

	private final String mLogTag;
	
	public PlusiOperation(Context context, EsAccount esaccount, String command, Intent intent, HttpOperation.OperationListener operationlistener, Class responseClass)
    {
        super(context, esaccount, createPlusiRequestUrl(command), Property.PLUS_BACKEND_URL.get(), "oauth2:https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/plus.stream.read https://www.googleapis.com/auth/plus.stream.write https://www.googleapis.com/auth/plus.circles.write https://www.googleapis.com/auth/plus.circles.read https://www.googleapis.com/auth/plus.photos.readwrite https://www.googleapis.com/auth/plus.native", intent, operationlistener, responseClass);
        mLogTag = truncateLogTagIfNecessary(command);
    }

    protected PlusiOperation(Context context, EsAccount esaccount, String command, Intent intent, HttpOperation.OperationListener operationlistener, HttpRequestConfiguration httprequestconfiguration, Class responseClass)
    {
        super(context, esaccount, createPlusiRequestUrl(command), Property.PLUS_BACKEND_URL.get(), "oauth2:https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/plus.stream.read https://www.googleapis.com/auth/plus.stream.write https://www.googleapis.com/auth/plus.circles.write https://www.googleapis.com/auth/plus.circles.read https://www.googleapis.com/auth/plus.photos.readwrite https://www.googleapis.com/auth/plus.native", intent, operationlistener, httprequestconfiguration, responseClass);
        mLogTag = truncateLogTagIfNecessary(command);
    }

    protected PlusiOperation(Context context, EsAccount esaccount, String command, Bundle bundle, Intent intent, HttpOperation.OperationListener operationlistener, HttpRequestConfiguration httprequestconfiguration, Class responseClass)
    {
        super(context, esaccount, createPlusiRequestUrl(command, bundle), Property.PLUS_BACKEND_URL.get(), "oauth2:https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/plus.stream.read https://www.googleapis.com/auth/plus.stream.write https://www.googleapis.com/auth/plus.circles.write https://www.googleapis.com/auth/plus.circles.read https://www.googleapis.com/auth/plus.photos.readwrite https://www.googleapis.com/auth/plus.native", intent, operationlistener, httprequestconfiguration, responseClass);
        mLogTag = truncateLogTagIfNecessary(command);
    }

    private static String createPlusiRequestUrl(String command) {
    	Bundle bundle = new Bundle();
    	bundle.putString(COMMAND, command);
    	return createPlusiRequestUrl(bundle);
    }
    
    private static String createPlusiRequestUrl(String command, Bundle bundle) {
    	bundle.putString(COMMAND, command);
    	return createPlusiRequestUrl(bundle);
    }
    
    private static String createPlusiRequestUrl(Bundle bundle) {
        Uri.Builder builder = Uri.parse(Property.PLUS_FRONTEND_URL.get()).buildUpon();
        if(bundle != null) {
            String s5;
            for(Iterator iterator = bundle.keySet().iterator(); iterator.hasNext(); builder.appendQueryParameter(s5, bundle.getString(s5)))
                s5 = (String)iterator.next();
        }
        String s2 = Property.TRACING_TOKEN.get();
        if(!TextUtils.isEmpty(s2)) {
            String s3 = Property.TRACING_TOKEN_2.get();
            if(!TextUtils.isEmpty(s3))
                s2 = (new StringBuilder()).append(s2).append(s3).toString();
            String s4 = Property.TRACING_PATH.get();
            if(!TextUtils.isEmpty(s4)) {
                builder.appendQueryParameter("trace", (new StringBuilder("token:")).append(s2).toString());
                if(!TextUtils.isEmpty(Property.TRACING_LEVEL.get()))
                    builder.appendQueryParameter("trace.deb", Property.TRACING_LEVEL.get());
            }
        }
        return builder.build().toString();
    }

    private static String truncateLogTagIfNecessary(String s)
    {
        if(s != null && s.length() > 23)
            s = s.substring(0, 23);
        return s;
    }

    protected final String getLogTag()
    {
        return mLogTag;
    }
}
