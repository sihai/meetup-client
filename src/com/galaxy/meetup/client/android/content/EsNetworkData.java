// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.galaxy.meetup.client.android.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.galaxy.meetup.client.android.network.http.HttpTransactionMetrics;
import com.galaxy.meetup.client.util.EsLog;

// Referenced classes of package com.google.android.apps.plus.content:
//            EsDatabaseHelper, EsProvider, EsAccount

public final class EsNetworkData {
	private static interface StatsQuery {

		public static final String PROJECTION[] = { "network_duration", "process_duration", "sent", "recv", "req_count" };

	}

	private static interface TransactionIdsQuery {

		public static final String PROJECTION[] = { "_id" };

	}

	static void cleanupData() {
	}

	public static void clearTransactionData(Context context, EsAccount esaccount) {
		SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
		sqlitedatabase.beginTransaction();
		sqlitedatabase.delete("network_data_transactions", null, null);
		sqlitedatabase.setTransactionSuccessful();
		sqlitedatabase.endTransaction();
		context.getContentResolver().notifyChange(
				EsProvider.appendAccountParameter(EsProvider.NETWORK_DATA_TRANSACTIONS_URI, esaccount), null);
		sqlitedatabase.endTransaction();
	}

	private static void deleteOldTransactions(SQLiteDatabase sqlitedatabase) {
		long l;
		l = EsDatabaseHelper.getRowsCount(sqlitedatabase, "network_data_transactions", null, null);
		if (EsLog.isLoggable("EsNetworkData", 3))
			Log.d("EsNetworkData", (new StringBuilder("deleteOldTransactions count: ")).append(l).toString());

		Cursor cursor;
		StringBuffer stringbuffer;
		cursor = sqlitedatabase.query("network_data_transactions", TransactionIdsQuery.PROJECTION, null, null, null,
				null, "time ASC", Long.toString(l - 100L));

		stringbuffer = new StringBuffer(256);
		stringbuffer.append("_id IN(");
		while (cursor.moveToNext()) {
			stringbuffer.append('\'');
			stringbuffer.append(cursor.getString(0));
			stringbuffer.append('\'');
			stringbuffer.append(',');
		}
		stringbuffer.append(')');
		sqlitedatabase.delete("network_data_transactions", stringbuffer.toString(), null);
		cursor.close();
	}

	public static void insertData(Context context, EsAccount esaccount, HttpTransactionMetrics httptransactionmetrics, Exception exception) {

		if (EsLog.ENABLE_DOGFOOD_FEATURES)
			if (httptransactionmetrics == null) {
				Log.w("EsNetworkData", "Please use HttpTransactionMetrics for network traffic tracking",
						new Throwable());
			} else {
				SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount)
						.getWritableDatabase();
				sqlitedatabase.beginTransaction();
				ContentValues contentvalues = new ContentValues();

				if (exception == null) {
					httptransactionmetrics.log("EsNetworkData", " + ");

					contentvalues.put("name", httptransactionmetrics.getName());
					contentvalues.put("time", Long.valueOf(System.currentTimeMillis()));
					contentvalues.put("network_duration", Long.valueOf(httptransactionmetrics.getDuration()));
					contentvalues.put("process_duration", Long.valueOf(httptransactionmetrics.getProcessingDuration()));
					contentvalues.put("sent", Long.valueOf(httptransactionmetrics.getSentBytes()));
					contentvalues.put("recv", Long.valueOf(httptransactionmetrics.getReceivedBytes()));
					contentvalues.put("req_count", Long.valueOf(httptransactionmetrics.getRequestCount()));
					sqlitedatabase.insert("network_data_transactions", "_id", contentvalues);

					deleteOldTransactions(sqlitedatabase);
				} else {
					if (exception.getMessage() == null) {
						contentvalues.put("exception", exception.getClass().getSimpleName());
					} else {
						contentvalues.put(
								"exception",
								(new StringBuilder()).append(exception.getClass().getSimpleName()).append("[")
										.append(exception.getMessage()).append("]").toString());

					}
					httptransactionmetrics
							.log("EsNetworkData", (new StringBuilder(" + ")).append(exception).toString());
					String as[] = { httptransactionmetrics.getName() };
					Object obj = mSyncLock;
					Cursor cursor = sqlitedatabase.query("network_data_stats", StatsQuery.PROJECTION, "name=?", as,
							null, null, null);
					if (cursor != null && cursor.moveToFirst()) {
						ContentValues contentvalues1 = new ContentValues();
						contentvalues1.put("last", Long.valueOf(System.currentTimeMillis()));
						contentvalues1.put("network_duration",
								Long.valueOf(httptransactionmetrics.getDuration() + cursor.getLong(0)));
						contentvalues1.put("process_duration",
								Long.valueOf(httptransactionmetrics.getProcessingDuration() + cursor.getLong(1)));
						contentvalues1.put("sent",
								Long.valueOf(httptransactionmetrics.getSentBytes() + cursor.getLong(2)));
						contentvalues1.put("recv",
								Long.valueOf(httptransactionmetrics.getReceivedBytes() + cursor.getLong(3)));
						contentvalues1.put("req_count",
								Long.valueOf(cursor.getLong(4) + httptransactionmetrics.getRequestCount()));
						sqlitedatabase.update("network_data_stats", contentvalues1, "name=?", as);
						contentvalues1.put("name", httptransactionmetrics.getName());
						contentvalues1.put("first", Long.valueOf(System.currentTimeMillis()));
						contentvalues1.put("last", Long.valueOf(System.currentTimeMillis()));
						contentvalues1.put("network_duration", Long.valueOf(httptransactionmetrics.getDuration()));
						contentvalues1.put("process_duration",
								Long.valueOf(httptransactionmetrics.getProcessingDuration()));
						contentvalues1.put("sent", Long.valueOf(httptransactionmetrics.getSentBytes()));
						contentvalues1.put("recv", Long.valueOf(httptransactionmetrics.getReceivedBytes()));
						contentvalues1.put("req_count", Integer.valueOf(1));
						sqlitedatabase.insert("network_data_stats", "_id", contentvalues1);
						cursor.close();

					} else {
						context.getContentResolver().notifyChange(
								EsProvider.appendAccountParameter(EsProvider.NETWORK_DATA_TRANSACTIONS_URI, esaccount),
								null);
						context.getContentResolver().notifyChange(
								EsProvider.appendAccountParameter(EsProvider.NETWORK_DATA_STATS_URI, esaccount), null);
						Log.w("EsNetworkData", (new StringBuilder("Cannot insert network data for operation: "))
								.append(httptransactionmetrics.getName()).toString());
					}
					sqlitedatabase.setTransactionSuccessful();
					sqlitedatabase.endTransaction();
				}
				if (esaccount == null)
					Log.w("EsNetworkData", "Account not specified");
			}

	}

	public static void resetStatsData(Context context, EsAccount esaccount) {
		SQLiteDatabase sqlitedatabase = EsDatabaseHelper.getDatabaseHelper(context, esaccount).getWritableDatabase();
		sqlitedatabase.beginTransaction();
		sqlitedatabase.delete("network_data_stats", null, null);
		sqlitedatabase.setTransactionSuccessful();
		sqlitedatabase.endTransaction();
		context.getContentResolver().notifyChange(
				EsProvider.appendAccountParameter(EsProvider.NETWORK_DATA_STATS_URI, esaccount), null);
	}

	private static final Object mSyncLock = new Object();

}
