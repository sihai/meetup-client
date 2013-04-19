/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.HttpClient;

import com.galaxy.picasa.HttpUtils;

/**
 * 
 * @author sihai
 *
 */
public class GDataClient {

	 private String mAuthToken;
	 private HttpClient mHttpClient;
	    
	GDataClient() {
        mHttpClient = HttpUtils.createHttpClient("GData/1.0; gzip");
    }

    public final void get(String s, Operation operation) throws IOException {
        // TODO
    }

	public final void setAuthToken(String s) {
		mAuthToken = s;
	}

	public static final class Operation {

		public String inOutEtag;
		public InputStream outBody;
		public int outStatus;

		public Operation() {
		}
	}
}
