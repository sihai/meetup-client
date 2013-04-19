/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author sihai
 *
 */
public class ImageLoadingMetrics {

	private static boolean sImageLoadingMetricsEnabled = false;
    public static List<ImageRequestMetrics> sMetrics;
    public static Map<String, ImageRequestMetrics> sMetricsMap;
    
	public static boolean areImageLoadingMetricsEnabled() {
        return false;
    }
	
	private static synchronized ImageRequestMetrics getRequestMetrics(String url) {
        if(sMetricsMap == null) {
            sMetrics = new ArrayList<ImageRequestMetrics>();
            sMetricsMap = new HashMap<String, ImageRequestMetrics>();
        }
        ImageRequestMetrics imagerequestmetrics = sMetricsMap.get(url);
        if(imagerequestmetrics == null) {
            imagerequestmetrics = new ImageRequestMetrics();
            imagerequestmetrics.url = url;
            sMetrics.add(imagerequestmetrics);
            sMetricsMap.put(url, imagerequestmetrics);
        }
        return imagerequestmetrics;
    }

    public static void recordImageDelivered(String url, int compressedByteCount, int uncompressedByteCount) {
        ImageRequestMetrics imagerequestmetrics = getRequestMetrics(url);
        if(imagerequestmetrics.deliveredTimestamp == 0L) {
            imagerequestmetrics.deliveredTimestamp = System.currentTimeMillis();
            imagerequestmetrics.compressedByteCount = compressedByteCount;
            imagerequestmetrics.uncompressedByteCount = uncompressedByteCount;
        }
    }

    public static void recordImageDownloadFinished(String url) {
        getRequestMetrics(url).downloadFinishedTimestamp = System.currentTimeMillis();
    }

    public static void recordImageDownloadQueued(String url) {
        getRequestMetrics(url).downloadQueuedTimestamp = System.currentTimeMillis();
    }

    public static void recordImageDownloadStarted(String url) {
        getRequestMetrics(url).downloadStartedTimestamp = System.currentTimeMillis();
    }

    public static synchronized void recordLoadImageRequest(String url) {
        ImageRequestMetrics imagerequestmetrics = getRequestMetrics(url);
        if(imagerequestmetrics.downloadFinishedTimestamp != 0L && System.currentTimeMillis() - imagerequestmetrics.downloadFinishedTimestamp > 1000L) {
            sMetricsMap.remove(url);
            imagerequestmetrics = getRequestMetrics(url);
        }
        imagerequestmetrics.requestCount = 1 + imagerequestmetrics.requestCount;
        if(imagerequestmetrics.requestTimestamp == 0L)
            imagerequestmetrics.requestTimestamp = System.currentTimeMillis();
    }
    
	public static final class ImageRequestMetrics {

        public int compressedByteCount;
        public long deliveredTimestamp;
        public long downloadFinishedTimestamp;
        public long downloadQueuedTimestamp;
        public long downloadStartedTimestamp;
        public int requestCount;
        public long requestTimestamp;
        public int uncompressedByteCount;
        public String url;

        public ImageRequestMetrics() {
        }
    }
}
