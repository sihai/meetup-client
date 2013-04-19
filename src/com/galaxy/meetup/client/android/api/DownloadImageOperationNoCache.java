/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.http.HttpOperation;

/**
 * 
 * @author sihai
 *
 */
public class DownloadImageOperationNoCache extends HttpOperation {

	private Bitmap mBitmap;
	
	public DownloadImageOperationNoCache(Context context, EsAccount esaccount, String s, Intent intent, HttpOperation.OperationListener operationlistener)
    {
        super(context, "GET", s, esaccount, new ByteArrayOutputStream(15000), null, null);
    }

    public final Bitmap getBitmap()
    {
        return mBitmap;
    }

    public final void onHttpHandleContentFromStream(InputStream inputstream) throws IOException
    {
        onStartResultProcessing();
        ByteArrayOutputStream bytearrayoutputstream = (ByteArrayOutputStream)getOutputStream();
        try
        {
            byte abyte0[] = bytearrayoutputstream.toByteArray();
            mBitmap = BitmapFactory.decodeByteArray(abyte0, 0, abyte0.length);
            return;
        }
        catch(OutOfMemoryError outofmemoryerror)
        {
            Log.w("HttpTransaction", (new StringBuilder("DownloadImageOperation OutOfMemoryError on image bytes: ")).append(bytearrayoutputstream.size()).toString(), outofmemoryerror);
        }
        throw new ProtocolException("Cannot handle downloaded image");
    }
}
