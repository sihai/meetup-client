/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network.http;

import java.util.Locale;

import org.apache.http.client.methods.HttpRequestBase;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.ApiaryAuthDataFactory;
import com.galaxy.meetup.client.android.realtimechat.RealTimeChatService;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class ApiaryHttpRequestConfiguration implements HttpRequestConfiguration {

	private static ApiaryAuthDataFactory sAuthDataFactory = new ApiaryAuthDataFactory();
	private final EsAccount mAccount;
	private final String mBackendOverrideUrl;
	private final String mContentType;
	private final Context mContext;
	private final String mScope;
	
	public ApiaryHttpRequestConfiguration(Context context, EsAccount esaccount, String s, String s1) {
		this(context, esaccount, s, s1, "application/json");
	}

	public ApiaryHttpRequestConfiguration(Context context, EsAccount esaccount, String s, String s1, String s2) {
		mContext = context;
		mAccount = esaccount;
		mScope = s;
		mBackendOverrideUrl = s1;
		mContentType = s2;
	}

	public void addHeaders(HttpRequestBase httprequestbase) {
		
		String s;
		String s1;
		String s2;
		httprequestbase.addHeader("Accept-Encoding", "gzip");
		httprequestbase.addHeader("Accept-Language", Locale.getDefault().toString());
		httprequestbase.addHeader("User-Agent", getUserAgentHeader(mContext));
		httprequestbase.addHeader("Content-Type", mContentType);
		if (mAccount != null) {
			try {
				ApiaryAuthDataFactory.ApiaryAuthData apiaryauthdata = ApiaryAuthDataFactory.getAuthData(mScope);
				s1 = apiaryauthdata.getAuthToken(mContext, mAccount.getName());
				s2 = Long.toString(apiaryauthdata.getAuthTime(s1).longValue());
			} catch (Exception exception) {
				throw new RuntimeException(
						"Cannot obtain authentication token", exception);
			}
			httprequestbase.addHeader("Authorization", (new StringBuilder(
					"Bearer ")).append(s1).toString());
			httprequestbase.addHeader("X-Auth-Time", s2);
		}
		s = RealTimeChatService.getStickyC2dmId(mContext);
		if (s != null)
			httprequestbase.addHeader("X-Android-App-ID", s);
		if (!TextUtils.isEmpty(mBackendOverrideUrl)) {
			if (EsLog.isLoggable("HttpTransaction", 3))
				Log.d("HttpTransaction",
						(new StringBuilder("Setting backend override url "))
								.append(mBackendOverrideUrl).toString());
			httprequestbase.addHeader("X-Google-Backend-Override",
					mBackendOverrideUrl);
		}
	}

	protected String getUserAgentHeader(Context context) {
		return (new StringBuilder()).append(UserAgent.from(context))
				.append(" (gzip)").toString();
	}

	public final void invalidateAuthToken() {
	    if(mAccount == null)
	    	return;
	    
	    try {
	    	ApiaryAuthDataFactory.getAuthData(mScope).invalidateAuthToken(mContext, mAccount.getName());
	    } catch (Exception e) {
	    	throw new RuntimeException("Cannot invalidate authentication token", e);
	    }
	}

}