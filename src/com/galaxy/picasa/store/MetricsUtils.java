/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.store;

import java.util.ArrayList;
import java.util.List;

import android.os.SystemClock;
import android.util.Log;


/**
 * 
 * @author sihai
 *
 */
public class MetricsUtils {

	private static final long LOG_DURATION_LIMIT = SystemProperties.getLong("picasasync.metrics.time", 100L);
    static Metrics sFreeMetrics = null;
    private static final ThreadLocal sMetricsStack = new ThreadLocal() {

		protected final Object initialValue() {
			return new ArrayList(8);
		}

    };
    
	public static int begin(String s) {
		List arraylist = (List) sMetricsStack.get();
		arraylist.add(Metrics.obtain(s));
		return arraylist.size();
	}

	public static void end(int i) {
		endWithReport(i, null);
	}

	public static void endWithReport(int i, String s) {
		List arraylist = (List) sMetricsStack.get();
		if (i > arraylist.size() || i <= 0) {
			Object aobj[] = new Object[2];
			aobj[0] = Integer.valueOf(arraylist.size());
			aobj[1] = Integer.valueOf(i);
			throw new IllegalArgumentException(String.format(
					"size: %s, id: %s", aobj));
		}
		Metrics metrics1;
		for (; i < arraylist.size(); metrics1.recycle()) {
			metrics1 = (Metrics) arraylist.remove(-1 + arraylist.size());
			Log.w("MetricsUtils", (new StringBuilder(
					"WARNING: unclosed metrics: ")).append(metrics1.toString())
					.toString());
			if (!arraylist.isEmpty())
				((Metrics) arraylist.get(-1 + arraylist.size()))
						.merge(metrics1);
		}

		Metrics metrics = (Metrics) arraylist.remove(-1 + arraylist.size());
		metrics.endTimestamp = SystemClock.elapsedRealtime();
		if (Log.isLoggable("MetricsUtils", 3)
				&& LOG_DURATION_LIMIT >= 0L
				&& metrics.endTimestamp - metrics.startTimestamp >= LOG_DURATION_LIMIT) {
			StringBuilder stringbuilder = new StringBuilder();
			stringbuilder.append("[").append(metrics.name);
			if (metrics.queryResultCount != 0)
				stringbuilder.append(" query-result:").append(
						metrics.queryResultCount);
			if (metrics.updateCount != 0)
				stringbuilder.append(" update:").append(metrics.updateCount);
			if (metrics.inBytes != 0L)
				stringbuilder.append(" in:").append(metrics.inBytes);
			if (metrics.outBytes != 0L)
				stringbuilder.append(" out:").append(metrics.outBytes);
			if (metrics.networkOpDuration > 0L)
				stringbuilder.append(" net-time:").append(
						metrics.networkOpDuration);
			if (metrics.networkOpCount > 1)
				stringbuilder.append(" net-op:").append(metrics.networkOpCount);
			long l = metrics.endTimestamp - metrics.startTimestamp;
			if (l > 0L)
				stringbuilder.append(" time:").append(l);
			if (s != null)
				stringbuilder.append((new StringBuilder(" report:")).append(s)
						.toString());
			Log.d("MetricsUtils", stringbuilder.append(']').toString());
		}
		if (!arraylist.isEmpty())
			((Metrics) arraylist.get(-1 + arraylist.size())).merge(metrics);
		if (s != null && metrics.networkOpCount > 0)
			PicasaStoreFacade.broadcastOperationReport(s, metrics.endTimestamp
					- metrics.startTimestamp, metrics.networkOpDuration,
					metrics.networkOpCount, metrics.outBytes, metrics.inBytes);
		metrics.recycle();
	}

	public static void incrementInBytes(long l) {
		ArrayList arraylist = (ArrayList) sMetricsStack.get();
		int i = arraylist.size();
		if (i > 0) {
			Metrics metrics = (Metrics) arraylist.get(i - 1);
			metrics.inBytes = l + metrics.inBytes;
		}
	}

	public static void incrementNetworkOpCount(long l) {
		List arraylist = (List) sMetricsStack.get();
		int i = arraylist.size();
		if (i > 0) {
			Metrics metrics = (Metrics) arraylist.get(i - 1);
			metrics.networkOpCount = (int) (1L + (long) metrics.networkOpCount);
		}
	}

	public static void incrementNetworkOpDuration(long l) {
		List arraylist = (List) sMetricsStack.get();
		int i = arraylist.size();
		if (i > 0) {
			Metrics metrics = (Metrics) arraylist.get(i - 1);
			metrics.networkOpDuration = l + metrics.networkOpDuration;
		}
	}

	public static void incrementNetworkOpDurationAndCount(long l) {
		List arraylist = (List) sMetricsStack.get();
		int i = arraylist.size();
		if (i > 0) {
			Metrics metrics = (Metrics) arraylist.get(i - 1);
			metrics.networkOpDuration = l + metrics.networkOpDuration;
			metrics.networkOpCount = 1 + metrics.networkOpCount;
		}
	}

	public static void incrementOutBytes(long l) {
		List arraylist = (List) sMetricsStack.get();
		int i = arraylist.size();
		if (i > 0) {
			Metrics metrics = (Metrics) arraylist.get(i - 1);
			metrics.outBytes = l + metrics.outBytes;
		}
	}

	public static void incrementQueryResultCount(int i) {
		List arraylist = (List) sMetricsStack.get();
		int j = arraylist.size();
		if (j > 0) {
			Metrics metrics = (Metrics) arraylist.get(j - 1);
			metrics.queryResultCount = i + metrics.queryResultCount;
		}
	}
	
	private static final class Metrics {
		public long endTimestamp;
        public long inBytes;
        public String name;
        public int networkOpCount;
        public long networkOpDuration;
        public Metrics nextFree;
        public long outBytes;
        public int queryResultCount;
        public long startTimestamp;
        public int updateCount;

		private Metrics() {
		}
        
		public static synchronized Metrics obtain(String s) {
			Metrics metrics;
			metrics = MetricsUtils.sFreeMetrics;
			if (metrics != null)
				MetricsUtils.sFreeMetrics = metrics.nextFree;
			else
				metrics = new Metrics();
			metrics.name = s;
			metrics.startTimestamp = SystemClock.elapsedRealtime();
			return metrics;
		}

        private static synchronized void recycle(Metrics metrics){
            metrics.nextFree = MetricsUtils.sFreeMetrics;
            MetricsUtils.sFreeMetrics = metrics;
        }

        public final void merge(Metrics metrics) {
            queryResultCount = queryResultCount + metrics.queryResultCount;
            updateCount = updateCount + metrics.updateCount;
            inBytes = inBytes + metrics.inBytes;
            outBytes = outBytes + metrics.outBytes;
            networkOpDuration = networkOpDuration + metrics.networkOpDuration;
            networkOpCount = networkOpCount + metrics.networkOpCount;
        }

		public final void recycle() {
			name = null;
			queryResultCount = 0;
			updateCount = 0;
			inBytes = 0L;
			outBytes = 0L;
			networkOpDuration = 0L;
			networkOpCount = 0;
			recycle(this);
		}
    }
}
