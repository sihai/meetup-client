/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network.http;

import org.apache.http.client.methods.HttpRequestBase;

import android.content.Context;
import android.text.TextUtils;

import com.galaxy.meetup.client.android.AuthData;
import com.galaxy.meetup.client.android.ClientVersion;
import com.galaxy.meetup.client.android.content.EsAccount;

/**
 * 
 * @author sihai
 *
 */
public class DefaultHttpRequestConfiguration implements HttpRequestConfiguration {

	private static final String sEnabledFeatures[] = {
        "278", "296", "301", "342", "383"
    };
    private final EsAccount mAccount;
    private final String mAuthTokenType;
    private final Context mContext;
    
    //===========================================================================
    //						Constructor
    //===========================================================================
    public DefaultHttpRequestConfiguration(Context context, EsAccount esaccount) {
        this(context, esaccount, "webupdates");
    }

    public DefaultHttpRequestConfiguration(Context context, EsAccount esaccount, String s) {
        mContext = context;
        mAccount = esaccount;
        mAuthTokenType = s;
    }
    
	@Override
	public void addHeaders(HttpRequestBase httprequestbase) {
		httprequestbase.addHeader("Cache-Control", "no-cache, no-transform");
        httprequestbase.addHeader("X-Wap-Proxy-Cookie", "none");
        httprequestbase.addHeader("X-Mobile-Google-Client", "1");
        httprequestbase.addHeader("Accept-Encoding", "gzip");
        httprequestbase.addHeader("User-Agent", (new StringBuilder()).append(UserAgent.from(mContext)).append(" (gzip)").toString());
        if(httprequestbase.getURI().getScheme().equalsIgnoreCase("https") || httprequestbase.getURI().getHost().equals("10.0.2.2")) {
            String s;
            try {
                s = AuthData.getAuthToken(mContext, mAccount.getName(), mAuthTokenType);
            } catch(Exception exception) {
                throw new RuntimeException("Cannot obtain authentication token", exception);
            }
            httprequestbase.addHeader("Authorization", (new StringBuilder("GoogleLogin auth=")).append(s).toString());
        }
        httprequestbase.addHeader("X-Mobile-Google-Client-Version", Integer.toString(ClientVersion.from(mContext)));
        if(sEnabledFeatures != null && sEnabledFeatures.length > 0)
            httprequestbase.addHeader("X-Mobile-Google-Features", TextUtils.join(",", sEnabledFeatures));
	}

	@Override
	public void invalidateAuthToken() {
		try {
            AuthData.invalidateAuthToken(mContext, mAccount.getName(), mAuthTokenType);
            return;
        } catch(Exception exception) {
            throw new RuntimeException("Cannot invalidate authentication token", exception);
        }
	}
}
