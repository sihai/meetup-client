// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.galaxy.meetup.client.android.network;

import android.util.Log;
import java.util.*;
import org.apache.http.HttpConnectionMetrics;

public final class HttpTransactionMetrics {
	private static final class HttpTransactionMetricsHolder {

		public final String toString() {
			return (new StringBuilder("[")).append(name)
					.append("], duration: ").append(duration)
					.append("ms, network: ")
					.append(duration - processingDuration)
					.append("ms, processing: ").append(processingDuration)
					.append("ms, requests: ").append(requestCount)
					.append(", sent: ").append(sentBytes)
					.append(", received: ").append(receivedBytes).toString();
		}

		private long duration;
		private String name;
		private long processingDuration;
		private long receivedBytes;
		private long requestCount;
		private long sentBytes;

		private HttpTransactionMetricsHolder() {
		}

		HttpTransactionMetricsHolder(byte byte0) {
			this();
		}
	}

	public HttpTransactionMetrics() {
	}

	public final void accumulateFrom(
			HttpTransactionMetrics httptransactionmetrics) {
		if (mTransaction == null)
			onBeginTransaction(httptransactionmetrics.getName());
	}

	public final long getDuration() {
		ArrayList arraylist = new ArrayList(mMap.keySet());
		long l = 0L;
		for (Iterator iterator = arraylist.iterator(); iterator.hasNext();) {
			String s = (String) iterator.next();
			l += ((HttpTransactionMetricsHolder) mMap.get(s)).duration;
		}

		return l;
	}

	public final String getName() {
		String s;
		if (!mMap.isEmpty())
			s = (String) mMap.keySet().iterator().next();
		else
			s = "Unknown";
		return s;
	}

	public final long getProcessingDuration() {
		ArrayList arraylist = new ArrayList(mMap.keySet());
		long l = 0L;
		for (Iterator iterator = arraylist.iterator(); iterator.hasNext();) {
			String s = (String) iterator.next();
			l += ((HttpTransactionMetricsHolder) mMap.get(s)).processingDuration;
		}

		return l;
	}

	public final long getReceivedBytes() {
		ArrayList arraylist = new ArrayList(mMap.keySet());
		long l = 0L;
		for (Iterator iterator = arraylist.iterator(); iterator.hasNext();) {
			String s = (String) iterator.next();
			l += ((HttpTransactionMetricsHolder) mMap.get(s)).receivedBytes;
		}

		return l;
	}

	public final long getRequestCount() {
		ArrayList arraylist = new ArrayList(mMap.keySet());
		long l = 0L;
		for (Iterator iterator = arraylist.iterator(); iterator.hasNext();) {
			String s = (String) iterator.next();
			l += ((HttpTransactionMetricsHolder) mMap.get(s)).requestCount;
		}

		return l;
	}

	public final long getSentBytes() {
		ArrayList arraylist = new ArrayList(mMap.keySet());
		long l = 0L;
		for (Iterator iterator = arraylist.iterator(); iterator.hasNext();) {
			String s = (String) iterator.next();
			l += ((HttpTransactionMetricsHolder) mMap.get(s)).sentBytes;
		}

		return l;
	}

	public final void log(String s, String s1) {
		ArrayList arraylist = new ArrayList(mMap.keySet());
		Collections.sort(arraylist);
		String s2;
		for (Iterator iterator = arraylist.iterator(); iterator.hasNext(); Log
				.i(s, (new StringBuilder()).append(s1).append(mMap.get(s2))
						.toString()))
			s2 = (String) iterator.next();

	}

	public final void onBeginTransaction(String s) {
		mTransaction = (HttpTransactionMetricsHolder) mMap.get(s);
		if (mTransaction == null) {
			mTransaction = new HttpTransactionMetricsHolder((byte) 0);
			mTransaction.name = s;
			mMap.put(s, mTransaction);
		}
		mTransactionStartMillis = System.currentTimeMillis();
		mProcessingStartMillis = 0L;
	}

	public final void onEndResultProcessing() {
		if (mProcessingStartMillis != 0L) {
			mProcessingStartMillis = 0L;
		}
	}

	public final void onEndTransaction() {
		onEndResultProcessing();
	}

	public final void onStartResultProcessing() {
		mProcessingStartMillis = System.currentTimeMillis();
	}

	public final void setConnectionMetrics(
			HttpConnectionMetrics httpconnectionmetrics) {
		mConnectionMetrics = httpconnectionmetrics;
		mBaseRequestCount = 0L;
		mBaseReceivedBytes = 0L;
		mBaseSentBytes = 0L;
	}

	private long mBaseReceivedBytes;
	private long mBaseRequestCount;
	private long mBaseSentBytes;
	private HttpConnectionMetrics mConnectionMetrics;
	private final HashMap mMap = new HashMap();
	private long mProcessingStartMillis;
	private HttpTransactionMetricsHolder mTransaction;
	private long mTransactionStartMillis;
}
