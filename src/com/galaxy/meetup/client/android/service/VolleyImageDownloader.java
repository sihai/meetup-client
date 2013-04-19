/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import java.util.HashSet;

import org.apache.http.HttpConnectionMetrics;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ResponseDelivery;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsNetworkData;
import com.galaxy.meetup.client.android.content.MediaImageRequest;
import com.galaxy.meetup.client.android.content.cache.CachedImageRequest;
import com.galaxy.meetup.client.android.content.cache.EsMediaCache;
import com.galaxy.meetup.client.android.network.http.HttpTransactionMetrics;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.client.util.FIFEUtil;
import com.galaxy.meetup.client.util.GifImage;
import com.galaxy.meetup.client.util.ImageProxyUtil;
import com.galaxy.meetup.client.util.ImageUtils;

/**
 * 
 * @author sihai
 * 
 */
public class VolleyImageDownloader {

	private static EsAccount sAccount;
	private static Context sContext;
	private static final Object sImageDownloadTag = new Object();
	private static BasicNetwork sNetwork;
	private static HashSet sQueuedRequest = new HashSet();
	private static RequestQueue sRequestQueue;

	public static void downloadImage(Context context, EsAccount esaccount, CachedImageRequest cachedimagerequest)
    {
        init(context);
        synchronized(sQueuedRequest)
        {
            if(!esaccount.equals(sAccount))
            {
                sRequestQueue.cancelAll(sImageDownloadTag);
                sQueuedRequest.clear();
            }
            sAccount = esaccount;
            if(!sQueuedRequest.contains(cachedimagerequest))
            {
                sQueuedRequest.add(cachedimagerequest);
                DownloadImageRequest downloadimagerequest = new DownloadImageRequest(cachedimagerequest, true);
                downloadimagerequest.setShouldCache(false);
                downloadimagerequest.setTag(sImageDownloadTag);
                sRequestQueue.add(downloadimagerequest);
            }
        }
    }

    private static synchronized void init(Context context)
    {
        if(sNetwork == null)
        {
            sContext = context.getApplicationContext();
            sNetwork = new BasicNetwork(new HurlStack()) {

                public final NetworkResponse performRequest(Request request) throws VolleyError
                {
                    ((DownloadImageRequest)request).getMetrics().onBeginTransaction("VolleyImageDownload");
                    android.os.Process.setThreadPriority(19);
                    return super.performRequest(request);
                }

            };
            RequestQueue requestqueue = new RequestQueue(new NoCache(), sNetwork, 2, new NoResponseDelivery());
            sRequestQueue = requestqueue;
            requestqueue.start();
        }
    }
	
	static byte[] access$500(MediaImageRequest mediaimagerequest, byte abyte0[])
    {
        int i = mediaimagerequest.getMediaType();
        String s = mediaimagerequest.getUrl();
        if(i == 3 || i == 2) {
            if(GifImage.isGif(abyte0)) 
            	return abyte0; 
        } else { 
        	if(i == 1) {
                if(GifImage.isGif(abyte0)) 
                	return abyte0; 
        	} else {
        		return null;
        	}
        }
        
        byte abyte1[];
        int j = mediaimagerequest.getWidth();
        int k = mediaimagerequest.getHeight();
        if(j <= 0 && k <= 0)
        {
            Point point;
            if(FIFEUtil.isFifeHostedUrl(s))
                point = FIFEUtil.getImageUrlSize(s);
            else
                point = ImageProxyUtil.getImageUrlSize(s);
            j = point.x;
            k = point.y;
        }
        
        if(j <= 0 || k <= 0) 
        	return abyte0; 
        else {
        	android.graphics.Bitmap bitmap = ImageUtils.resizeBitmap(abyte0, j, k);
            if(bitmap == null)
                return null;
            else
                return ImageUtils.compressBitmap(bitmap);
        }
    }
	private static final class ConnectionMetrics implements
			HttpConnectionMetrics {

		long receivedBytes;

		public final Object getMetric(String s) {
			return null;
		}

		public final long getReceivedBytesCount() {
			return receivedBytes;
		}

		public final long getRequestCount() {
			return 1L;
		}

		public final long getResponseCount() {
			return 1L;
		}

		public final long getSentBytesCount() {
			return 0L;
		}

		public final void reset() {
		}

		ConnectionMetrics() {
		}
	}

	private static final class DownloadImageRequest extends Request {

		private final ConnectionMetrics mConnectionMetrics = new ConnectionMetrics();
		private CachedImageRequest mImageRequest;
		public final HttpTransactionMetrics mMetrics = new HttpTransactionMetrics();
		private boolean mSaveToCache;

		public DownloadImageRequest(CachedImageRequest cachedimagerequest,
				boolean flag) {
			super(cachedimagerequest.getDownloadUrl(), null);
			mImageRequest = cachedimagerequest;
			mSaveToCache = flag;
			mMetrics.setConnectionMetrics(mConnectionMetrics);
		}
		
		public final int compareTo(Request request) {
			com.android.volley.Request.Priority priority = com.android.volley.Request.Priority.NORMAL;
			com.android.volley.Request.Priority priority1 = com.android.volley.Request.Priority.NORMAL;
			int i;
			if (priority == priority1)
				i = request.getSequence() - getSequence();
			else
				i = priority1.ordinal() - priority.ordinal();
			return i;
		}

		public final int compareTo(Object obj) {
			return compareTo((Request) obj);
		}

		protected final void deliverResponse(Object obj) {
		}

		public final CachedImageRequest getImageRequest() {
			return mImageRequest;
		}

		public final HttpTransactionMetrics getMetrics() {
			return mMetrics;
		}

		protected final Response parseNetworkResponse(NetworkResponse networkresponse)
        {
            mMetrics.onStartResultProcessing();
            mConnectionMetrics.receivedBytes = networkresponse.data.length;
            Response response;
            try
            {
                byte abyte0[];
                if(mImageRequest instanceof MediaImageRequest)
                    abyte0 = access$500((MediaImageRequest)mImageRequest, networkresponse.data);
                else
                    abyte0 = networkresponse.data;
                if(mSaveToCache && abyte0 != null)
                    EsMediaCache.insertMedia(VolleyImageDownloader.sContext, mImageRequest, abyte0);
                response = Response.success(abyte0, null);
            }
            catch(OutOfMemoryError outofmemoryerror)
            {
                Log.w("VolleyImageDownloader", (new StringBuilder("DownloadImageOperation OutOfMemoryError on image bytes: ")).append(networkresponse.data.length).toString(), outofmemoryerror);
                response = Response.error(new VolleyError(outofmemoryerror));
            }
            return response;
        }
	}

	private static final class NoResponseDelivery implements ResponseDelivery {

		NoResponseDelivery() {
		}
		
		private static void finishRequest(DownloadImageRequest downloadimagerequest, VolleyError volleyerror) {
			HttpTransactionMetrics httptransactionmetrics = downloadimagerequest.getMetrics();
			httptransactionmetrics.onEndTransaction();
			EsNetworkData.insertData(VolleyImageDownloader.sContext,
					VolleyImageDownloader.sAccount, httptransactionmetrics,
					volleyerror);
			synchronized (VolleyImageDownloader.sQueuedRequest) {
				VolleyImageDownloader.sQueuedRequest
						.remove(downloadimagerequest.getImageRequest());
			}
		}

		public final void postError(Request request, VolleyError volleyerror) {
			if (EsLog.isLoggable("VolleyImageDownloader", 5))
				Log.w("VolleyImageDownloader", (new StringBuilder("ERROR: "))
						.append(request.getSequence()).toString(), volleyerror);
			finishRequest((DownloadImageRequest) request, volleyerror);
		}

		@Override
		public final void postResponse(Request request, Response response) {
			postResponse(request, response, null);
		}
		
		@Override
		public final void postResponse(Request request, Response response, Runnable runnable) {
			if (EsLog.isLoggable("VolleyImageDownloader", 3))
				Log.d("VolleyImageDownloader",
						(new StringBuilder("RESPONSE: ")).append(
								request.getSequence()).toString());
			finishRequest((DownloadImageRequest) request, null);
		}

		@Override
		@Deprecated
		public void discardBefore(int sequence) {
		}
	}
}
