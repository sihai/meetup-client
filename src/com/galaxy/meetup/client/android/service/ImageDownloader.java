/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import java.util.HashSet;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;

import com.galaxy.meetup.client.android.api.DownloadImageOperation;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.cache.CachedImageRequest;
import com.galaxy.meetup.client.util.ImageLoadingMetrics;
import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public class ImageDownloader {

	private static EsAccount sAccount;
    private static Context sContext;
    private static Stack sDownloadRequests = new Stack();
    private static ExecutorService sExecutorService;
    private static DownloadProcessor sProcessor = new DownloadProcessor((byte)0);
    private static HashSet sQueuedRequest = new HashSet();
    
	private static synchronized void init(Context context) {
        if(sExecutorService == null) {
            sContext = context.getApplicationContext();
            sExecutorService = Executors.newFixedThreadPool(2);
        }
    }
    
	public static void downloadImage(Context context, EsAccount esaccount, CachedImageRequest cachedimagerequest) {
        if(Property.ENABLE_VOLLEY_IMAGE_DOWNLOAD.getBoolean()) {
            VolleyImageDownloader.downloadImage(context, esaccount, cachedimagerequest);
        } else {
            init(context);
            synchronized(sDownloadRequests) {
                if(!esaccount.equals(sAccount))
                    sDownloadRequests.clear();
                sAccount = esaccount;
                if(!sQueuedRequest.contains(cachedimagerequest)) {
                    if(ImageLoadingMetrics.areImageLoadingMetricsEnabled())
                        ImageLoadingMetrics.recordImageDownloadQueued(cachedimagerequest.getUriForLogging());
                    sQueuedRequest.add(cachedimagerequest);
                    sDownloadRequests.push(cachedimagerequest);
                }
            }
            sExecutorService.execute(sProcessor);
        }
    }

	private static final class DownloadProcessor implements Runnable {

		public final void run() {
			android.os.Process.setThreadPriority(19);
			do {
				CachedImageRequest cachedimagerequest;
				DownloadImageOperation downloadimageoperation;
				synchronized (ImageDownloader.sDownloadRequests) {
					if (ImageDownloader.sDownloadRequests.isEmpty())
						return;
					cachedimagerequest = (CachedImageRequest) ImageDownloader.sDownloadRequests
							.pop();
					downloadimageoperation = new DownloadImageOperation(
							ImageDownloader.sContext, ImageDownloader.sAccount,
							cachedimagerequest, null, null);
				}
				downloadimageoperation.start();
				synchronized (ImageDownloader.sDownloadRequests) {
					ImageDownloader.sQueuedRequest.remove(cachedimagerequest);
				}
			} while (true);
		}

		private DownloadProcessor() {
		}

		DownloadProcessor(byte byte0) {
			this();
		}
	}
}
