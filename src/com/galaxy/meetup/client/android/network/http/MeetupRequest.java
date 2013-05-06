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

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class MeetupRequest {

	private EsAccount mAccount;
	private final com.galaxy.meetup.server.client.v2.request.Request mRequest;
    private final byte mPayloadBytes[];

    /**
     * 
     */
    public MeetupRequest(EsAccount account, com.galaxy.meetup.server.client.v2.request.Request request)
    {
        this(account, request, null);
    }

    /**
     * 
     */
    public MeetupRequest(EsAccount account, com.galaxy.meetup.server.client.v2.request.Request request, byte[] payloadBytes)
    {
    	mAccount = account;
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
		arraylist.add(new BasicNameValuePair("_user_", String.format("{\"name\":\"%s\", \"password\":\"%s\"}", mAccount.getName(), mAccount.getPassword())));
        arraylist.add(new BasicNameValuePair("_request_", JsonUtil.toJsonString(mRequest)));
        return new UrlEncodedFormEntity(arraylist);
	}
}
