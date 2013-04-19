/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import com.android.volley.ExecutorDelivery;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.ByteArrayPool;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;
import com.android.volley.toolbox.PoolingByteArrayOutputStream;

/**
 * 
 * @author sihai
 *
 */
public class VolleyRequestQueue {

	private EsNetwork mNetwork;
    private RequestQueue mRequestQueue;
    
    public VolleyRequestQueue(Context context, Handler handler) {
        mNetwork = new EsNetwork(context.getApplicationContext(), new HurlStack(), SharedByteArrayPool.getInstance());
        mRequestQueue = new RequestQueue(new NoCache(), mNetwork, 2, new ExecutorDelivery(handler));
        mRequestQueue.start();
    }

	public final void add(VolleyRequest volleyrequest) {
		mRequestQueue.add(volleyrequest);
	}

	public final void cancelAll(
			com.android.volley.RequestQueue.RequestFilter requestfilter) {
		mRequestQueue.cancelAll(requestfilter);
	}
    
	private static final class EsNetwork extends BasicNetwork {

		private Context mContext;

        public EsNetwork(Context context, HttpStack httpstack, ByteArrayPool bytearraypool) {
            super(httpstack, bytearraypool);
            mContext = context;
        }
        
        private byte[] tryContentUri(Uri uri) throws FileNotFoundException, IOException {
        	InputStream inputstream = null;
            PoolingByteArrayOutputStream poolingbytearrayoutputstream = null;
            byte abyte0[] = null;
            byte abyte1[] = null;
            try {
            	poolingbytearrayoutputstream = new PoolingByteArrayOutputStream(mPool);
            	abyte1 = mPool.getBuf(1024);
            	inputstream = mContext.getContentResolver().openInputStream(uri);
	            do
	            {
	                int i = inputstream.read(abyte1);
	                if(i == -1)
	                    break;
	                poolingbytearrayoutputstream.write(abyte1, 0, i);
	            } while(true);
	            return poolingbytearrayoutputstream.toByteArray();
            } finally {
            	if(null != abyte1) {
            		mPool.returnBuf(abyte1);
            	}
            	if(null != inputstream) {
            		try
                    {
            			inputstream.close();
                    }
                    catch(IOException ex)
                    {
                    }
            	}
            	if(null != poolingbytearrayoutputstream) {
            		try
                    {
                        poolingbytearrayoutputstream.close();
                    }
                    catch(IOException ex)
                    {
                    }
            	}
            }
        }

		public final NetworkResponse performRequest(Request request) throws VolleyError {
			android.os.Process.setThreadPriority(19);
			NetworkResponse networkresponse;
			try {
				if (request instanceof VolleyRequest) {
					Uri uri = ((VolleyRequest) request).getContentUri();
					if (uri != null) {
						byte abyte0[] = tryContentUri(uri);
						if (abyte0 != null && abyte0.length != 0) {
							networkresponse = new NetworkResponse(abyte0);
						}
					}
				}
				networkresponse = super.performRequest(request);
			} catch (FileNotFoundException e) {
				throw new VolleyError(e);
			} catch (IOException e) {
				throw new VolleyError(e);
			} catch (OutOfMemoryError outofmemoryerror) {
				throw new VolleyOutOfMemoryError(outofmemoryerror);
			}
			return networkresponse;
		}
    }
	
}
