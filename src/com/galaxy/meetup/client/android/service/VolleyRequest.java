/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import android.net.Uri;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.galaxy.meetup.client.android.EsApplication;

/**
 * 
 * @author sihai
 * 
 */
public abstract class VolleyRequest extends Request {

	private static final int MAX_GIF_DOWNLOAD;
	private Uri mContentUri;

	static {
		if (EsApplication.sMemoryClass >= 48)
			MAX_GIF_DOWNLOAD = 0x800000;
		else
			MAX_GIF_DOWNLOAD = 0x200000;
	}

	public VolleyRequest(String s, Uri uri) {
		super(null, null);
		mContentUri = uri;
	}

	public void deliverResponse(Object obj) {
		deliverResponse((byte[]) obj);
	}

	public abstract void deliverResponse(byte abyte0[]);

	public final Uri getContentUri() {
		return mContentUri;
	}

	protected final Response parseNetworkResponse(
			NetworkResponse networkresponse) {
		int i = -1;
		String s;
		Response response;
		if (networkresponse.headers.containsKey("Content-Type"))
			s = (String) networkresponse.headers.get("Content-Type");
		else if (networkresponse.headers.containsKey("content-type"))
			s = (String) networkresponse.headers.get("content-type");
		else
			s = null;
		if (s != null && s.equals("image/gif"))
			i = MAX_GIF_DOWNLOAD;
		if (Log.isLoggable("VolleyRequest", 3))
			Log.d("VolleyRequest",
					(new StringBuilder("Download: "))
							.append(networkresponse.data.length)
							.append(", allowed: ").append(i).append(", type: ")
							.append(s).toString());
		if (i > 0 && networkresponse.data.length > i)
			response = Response.error(new VolleyError((new StringBuilder(
					"Download is too large: "))
					.append(networkresponse.data.length).append(", allowed: ")
					.append(i).append(", type: ").append(s).toString()));
		else
			response = Response.success(networkresponse.data, null);
		return response;
	}
}
