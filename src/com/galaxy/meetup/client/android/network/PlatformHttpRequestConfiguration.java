/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network;

import org.apache.http.client.methods.HttpRequestBase;

import android.content.Context;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.http.ApiaryHttpRequestConfiguration;
import com.galaxy.meetup.client.android.network.http.UserAgent;
import com.galaxy.meetup.client.util.PlatformContractUtils;

/**
 * 
 * @author sihai
 *
 */
public class PlatformHttpRequestConfiguration extends ApiaryHttpRequestConfiguration {

	private final ApiaryApiInfo mApiInfo;
	
	public PlatformHttpRequestConfiguration(Context context, EsAccount esaccount, String s, String s1, ApiaryApiInfo apiaryapiinfo)
    {
        super(context, esaccount, s, s1);
        mApiInfo = apiaryapiinfo;
    }

    public final void addHeaders(HttpRequestBase httprequestbase)
    {
        super.addHeaders(httprequestbase);
        httprequestbase.addHeader("X-Container-Url", PlatformContractUtils.getContainerUrl(mApiInfo));
    }

    protected final String getUserAgentHeader(Context context)
    {
        StringBuilder stringbuilder = new StringBuilder(UserAgent.from(context));
        stringbuilder.append("; G+ SDK/");
        String s;
        if(mApiInfo.getSdkVersion() == null)
            s = "1.0.0";
        else
            s = mApiInfo.getSdkVersion();
        stringbuilder.append(s);
        stringbuilder.append(";");
        return stringbuilder.toString();
    }

}
