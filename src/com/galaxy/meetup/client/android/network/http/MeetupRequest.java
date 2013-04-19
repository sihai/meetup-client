/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.galaxy.meetup.server.client.domain.GenericJson;

/**
 * 
 * @author sihai
 *
 */
public class MeetupRequest {

	private final GenericJson mRequest;
    private final byte mPayloadBytes[];
    
    /**
     * 
     * @param request
     */
    public MeetupRequest(GenericJson request)
    {
        this(request, null);
    }

    /**
     * 
     * @param request
     * @param payloadBytes
     */
    public MeetupRequest(GenericJson request, byte[] payloadBytes)
    {
        mRequest = request;
        mPayloadBytes = payloadBytes;
    }
    
    /**
     * 
     * @return
     * @throws IOException
     */
	public HttpEntity toEntity() throws IOException {
		List<BasicNameValuePair> arraylist = new ArrayList<BasicNameValuePair>();
        arraylist.add(new BasicNameValuePair("_request_", mRequest.toJsonString()));
        return new UrlEncodedFormEntity(arraylist);
	}
}
