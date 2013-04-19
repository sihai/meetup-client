/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.content.Intent;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.cache.CachedImageRequest;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.network.http.HttpTransactionMetrics;
import com.galaxy.meetup.client.android.service.EsSyncAdapterService;
import com.galaxy.meetup.client.util.ImageLoadingMetrics;

/**
 * 
 * @author sihai
 *
 */
public class DownloadImageOperation extends HttpOperation {

	private byte[] mImageBytes;
    private final CachedImageRequest mRequest;
    private final boolean mSaveToCache;
    
    public DownloadImageOperation(Context context, EsAccount esaccount, CachedImageRequest cachedimagerequest, Intent intent, HttpOperation.OperationListener operationlistener) {
        this(context, esaccount, cachedimagerequest, true, null, null);
    }

    public DownloadImageOperation(Context context, EsAccount esaccount, CachedImageRequest cachedimagerequest, boolean flag, Intent intent, HttpOperation.OperationListener operationlistener) {
        super(context, "GET", cachedimagerequest.getDownloadUrl(), esaccount, new ByteArrayOutputStream(15000), intent, operationlistener);
        mRequest = cachedimagerequest;
        mSaveToCache = flag;
    }

    public final byte[] getImageBytes() {
        return mImageBytes;
    }
    
    
    public final void start(EsSyncAdapterService.SyncState syncstate, HttpTransactionMetrics httptransactionmetrics) {
        if(ImageLoadingMetrics.areImageLoadingMetricsEnabled())
            ImageLoadingMetrics.recordImageDownloadStarted(mRequest.getUriForLogging());
        super.start(syncstate, httptransactionmetrics);
    }
}
