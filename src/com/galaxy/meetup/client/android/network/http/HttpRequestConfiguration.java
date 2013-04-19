/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network.http;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * 
 * @author sihai
 *
 */
public interface HttpRequestConfiguration {
	
	void addHeaders(HttpRequestBase httprequestbase);

    void invalidateAuthToken();
}
