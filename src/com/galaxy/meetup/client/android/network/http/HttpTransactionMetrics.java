/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpConnectionMetrics;

import android.util.Log;

/**
 * 
 * @author sihai
 *
 */
public class HttpTransactionMetrics {

	private long mBaseReceivedBytes;
    private long mBaseRequestCount;
    private long mBaseSentBytes;
    private HttpConnectionMetrics mConnectionMetrics;
    private final Map<String, HttpTransactionMetricsHolder> mMap = new HashMap<String, HttpTransactionMetricsHolder>();
    private long mProcessingStartMillis;
    private HttpTransactionMetricsHolder mTransaction;
    private long mTransactionStartMillis;
    
    public HttpTransactionMetrics() {
    }
    
    public final void accumulateFrom(HttpTransactionMetrics httpTransactionMetrics) {
    	if (this.mTransaction == null)
    		onBeginTransaction(httpTransactionMetrics.getName());
    	this.mTransaction.duration += httpTransactionMetrics.getDuration();
    	this.mTransaction.processingDuration += httpTransactionMetrics.getProcessingDuration();
    	this.mTransaction.requestCount += httpTransactionMetrics.getRequestCount();
    	this.mTransaction.receivedBytes += httpTransactionMetrics.getReceivedBytes();
    	this.mTransaction.sentBytes += httpTransactionMetrics.getSentBytes();
    }
    
    public final void onStartResultProcessing() {
        mProcessingStartMillis = System.currentTimeMillis();
    }
    
	public final void onEndResultProcessing() {
		if (this.mProcessingStartMillis != 0L) {
			this.mTransaction.processingDuration += (System.currentTimeMillis() - this.mProcessingStartMillis);
			this.mProcessingStartMillis = 0L;
		}
	}

	public final void onBeginTransaction(String s) {
        mTransaction = (HttpTransactionMetricsHolder)mMap.get(s);
        if(mTransaction == null) {
            mTransaction = new HttpTransactionMetricsHolder();
            mTransaction.name = s;
            mMap.put(s, mTransaction);
        }
        mTransactionStartMillis = System.currentTimeMillis();
        mProcessingStartMillis = 0L;
    }
	
    public final void onEndTransaction() {
    	onEndResultProcessing();
    	this.mTransaction.duration += (System.currentTimeMillis() - this.mTransactionStartMillis);
    	if (this.mConnectionMetrics != null) {
    		this.mTransaction.requestCount += this.mConnectionMetrics.getRequestCount();
    		this.mTransaction.receivedBytes += this.mConnectionMetrics.getReceivedBytesCount();
    		this.mTransaction.sentBytes += this.mConnectionMetrics.getSentBytesCount();
    	}
    	this.mConnectionMetrics = null;
    }
    
	public final void onEndTransaction(long paramLong1, long paramLong2) {
		this.mTransaction.duration = paramLong1;
		this.mTransaction.processingDuration = paramLong2;
		if (this.mConnectionMetrics != null) {
			this.mTransaction.requestCount += this.mConnectionMetrics.getRequestCount();
			this.mTransaction.receivedBytes += this.mConnectionMetrics.getReceivedBytesCount();
			this.mTransaction.sentBytes += this.mConnectionMetrics.getSentBytesCount();
		}
		this.mConnectionMetrics = null;
	}
    
    
    public final void setConnectionMetrics(HttpConnectionMetrics httpconnectionmetrics) {
        mConnectionMetrics = httpconnectionmetrics;
        mBaseRequestCount = 0L;
        mBaseReceivedBytes = 0L;
        mBaseSentBytes = 0L;
    }
    
	public final String getName() {
		String s;
		if (!mMap.isEmpty())
			s = mMap.keySet().iterator().next();
		else
			s = "Unknown";
		return s;
	}
    
	public final long getDuration() {
		List<String> arraylist = new ArrayList<String>(mMap.keySet());
		long l = 0L;
		Iterator<String> iterator = arraylist.iterator();
		while (iterator.hasNext()) {
			String str = (String) iterator.next();
			l += ((HttpTransactionMetricsHolder) this.mMap.get(str)).duration;
		}
		return l;
	}
	
	public final long getProcessingDuration() {
		List<String> arraylist = new ArrayList<String>(mMap.keySet());
		long l = 0L;
		Iterator<String> iterator = arraylist.iterator();
		while (iterator.hasNext()) {
			String str = (String) iterator.next();
			l += ((HttpTransactionMetricsHolder) this.mMap.get(str)).processingDuration;
		}
		return l;
	}
    
	public final long getReceivedBytes() {
		List<String> arraylist = new ArrayList<String>(mMap.keySet());
		long l = 0L;
		Iterator<String> iterator = arraylist.iterator();
		while (iterator.hasNext()) {
			String str = (String) iterator.next();
			l += ((HttpTransactionMetricsHolder) this.mMap.get(str)).receivedBytes;
		}
		return l;
	}
    
	public final long getRequestCount() {
		List<String> arraylist = new ArrayList<String>(mMap.keySet());
		long l = 0L;
		Iterator<String> iterator = arraylist.iterator();
		while (iterator.hasNext()) {
			String str = (String) iterator.next();
			l += ((HttpTransactionMetricsHolder) this.mMap.get(str)).requestCount;
		}
		return l;
	}
    
	public final long getSentBytes() {
		List<String> arraylist = new ArrayList<String>(mMap.keySet());
		long l = 0L;
		Iterator<String> iterator = arraylist.iterator();
		while (iterator.hasNext()) {
			String str = iterator.next();
			l += ((HttpTransactionMetricsHolder) this.mMap.get(str)).sentBytes;
		}
		return l;
	}
    
    public final void log(String s, String s1) {
        List<String> arraylist = new ArrayList<String>(mMap.keySet());
        Collections.sort(arraylist);
        String s2;
        for(Iterator<String> iterator = arraylist.iterator(); iterator.hasNext(); Log.i(s, (new StringBuilder()).append(s1).append(mMap.get(s2)).toString()))
            s2 = (String)iterator.next();

    }
    
    private static final class HttpTransactionMetricsHolder {

    	public long duration;
    	public String name;
    	public long processingDuration;
    	public long receivedBytes;
    	public long requestCount;
    	public long sentBytes;
        
        public final String toString() {
            return (new StringBuilder("[")).append(name).append("], duration: ").append(duration).append("ms, network: ").append(duration - processingDuration).append("ms, processing: ").append(processingDuration).append("ms, requests: ").append(requestCount).append(", sent: ").append(sentBytes).append(", received: ").append(receivedBytes).toString();
        }
        
        private HttpTransactionMetricsHolder() {
        }
    }
}
