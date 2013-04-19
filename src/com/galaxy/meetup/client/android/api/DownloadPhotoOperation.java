/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.http.HttpOperation;

/**
 * 
 * @author sihai
 *
 */
public class DownloadPhotoOperation extends HttpOperation {

	private byte mBytes[];
	
	public DownloadPhotoOperation(Context context, EsAccount esaccount, String s)
    {
        this(context, "GET", s, esaccount, ((OutputStream) (new ByteArrayOutputStream(15000))), null, null);
    }

    DownloadPhotoOperation(Context context, String s, String s1, EsAccount esaccount, OutputStream outputstream, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, s, s1, esaccount, outputstream, intent, operationlistener);
    }

    public final byte[] getBytes()
    {
        return mBytes;
    }

    public final void onHttpHandleContentFromStream(InputStream inputstream) throws IOException
    {
    	ByteArrayOutputStream bytearrayoutputstream = null;
    	try {
	        onStartResultProcessing();
	        if((getOutputStream() instanceof ByteArrayOutputStream)) {
	        	bytearrayoutputstream = (ByteArrayOutputStream)getOutputStream();
	            mBytes = bytearrayoutputstream.toByteArray();
	        }
    	} catch (OutOfMemoryError outofmemoryerror) {
    		Log.w("HttpTransaction", (new StringBuilder("DownloadPhotoOperation OutOfMemoryError on photo bytes: ")).append(bytearrayoutputstream.size()).toString(), outofmemoryerror);
            throw new ProtocolException("Cannot handle downloaded photo");
    	}
    }

}
