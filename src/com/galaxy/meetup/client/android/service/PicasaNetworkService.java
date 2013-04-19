/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import org.apache.http.impl.HttpConnectionMetricsImpl;
import org.apache.http.impl.io.HttpTransportMetricsImpl;

import android.app.IntentService;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccountsData;
import com.galaxy.meetup.client.android.content.EsNetworkData;
import com.galaxy.meetup.client.android.network.http.HttpTransactionMetrics;

/**
 * 
 * @author sihai
 *
 */
public class PicasaNetworkService extends IntentService {

	public PicasaNetworkService()
    {
        super("PicasaNetworkService");
    }

    protected void onHandleIntent(Intent intent)
    {
        String s = intent.getStringExtra("op_name");
        long l = intent.getLongExtra("total_time", 0L);
        long l1 = intent.getLongExtra("net_duration", 0L);
        long l2 = intent.getLongExtra("sent_bytes", 0L);
        long l3 = intent.getLongExtra("received_bytes", 0L);
        int i = intent.getIntExtra("transaction_count", 1);
        HttpTransactionMetrics httptransactionmetrics = new HttpTransactionMetrics();
        HttpTransportMetricsImpl httptransportmetricsimpl = new HttpTransportMetricsImpl();
        httptransportmetricsimpl.setBytesTransferred(l3);
        HttpTransportMetricsImpl httptransportmetricsimpl1 = new HttpTransportMetricsImpl();
        httptransportmetricsimpl1.setBytesTransferred(l2);
        HttpConnectionMetricsImpl httpconnectionmetricsimpl = new HttpConnectionMetricsImpl(httptransportmetricsimpl, httptransportmetricsimpl1);
        for(int j = 0; j < i; j++)
        {
            httpconnectionmetricsimpl.incrementRequestCount();
            httpconnectionmetricsimpl.incrementResponseCount();
        }

        httptransactionmetrics.onBeginTransaction(s);
        httptransactionmetrics.setConnectionMetrics(httpconnectionmetricsimpl);
        httptransactionmetrics.onEndTransaction(l, l - l1);
        EsNetworkData.insertData(this, EsAccountsData.getActiveAccount(this), httptransactionmetrics, null);
    }
}
