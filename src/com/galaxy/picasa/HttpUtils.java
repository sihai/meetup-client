/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ConnectionReleaseTrigger;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.galaxy.picasa.store.MetricsUtils;

import android.util.Log;

/**
 * 
 * @author sihai
 *
 */
public class HttpUtils {

	private static final ClientConnectionManager sConnectionManager;
    private static final HttpParams sHttpClientParams;

    static {
        BasicHttpParams basichttpparams = new BasicHttpParams();
        ConnManagerParams.setTimeout(basichttpparams, 20000L);
        SchemeRegistry schemeregistry = new SchemeRegistry();
        schemeregistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeregistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        sConnectionManager = new MetricsTrackingConnectionManager(basichttpparams, schemeregistry);
        BasicHttpParams basichttpparams1 = new BasicHttpParams();
        HttpConnectionParams.setStaleCheckingEnabled(basichttpparams1, false);
        HttpConnectionParams.setConnectionTimeout(basichttpparams1, 20000);
        HttpConnectionParams.setSoTimeout(basichttpparams1, 20000);
        HttpClientParams.setRedirecting(basichttpparams1, true);
        HttpProtocolParams.setUserAgent(basichttpparams1, "PicasaSync/1.0");
        sHttpClientParams = basichttpparams1;
    }
    
    public static void abortConnectionSilently(InputStream inputstream) {
        if((inputstream instanceof ConnectionReleaseTrigger))
        	try {
        		((ConnectionReleaseTrigger)inputstream).abortConnection();
        	} catch (Throwable t) {
        		Log.w("HttpUtils", "cannot abort connection", t);
        	}
    }
    
    public static HttpClient createHttpClient(String s) {
        HttpParams httpparams = sHttpClientParams.copy();
        HttpProtocolParams.setUserAgent(httpparams, s);
        return new DefaultHttpClient(sConnectionManager, httpparams);
    }
	
    private static void freeHttpEntity(HttpEntity httpentity) {
        if(null != httpentity)
        	try {
        		httpentity.consumeContent();
        	} catch (Throwable t) {
        		Log.w("HttpUtils", "cannot free entity", t);
        	}
    }
    
	public static InputStream openInputStream(String s) throws IOException {
		HttpEntity httpentity = null;
		try {
			HttpResponse httpresponse = (new DefaultHttpClient(
					sConnectionManager, sHttpClientParams))
					.execute(new HttpGet(s));
			httpentity = httpresponse.getEntity();
			int i = httpresponse.getStatusLine().getStatusCode();
			if (i != 200)
				throw new IOException((new StringBuilder("http status: "))
						.append(i).toString());
			InputStream inputstream = httpentity.getContent();
			return inputstream;
		} finally {
			if (null != httpentity) {
				freeHttpEntity(httpentity);
			}
		}
	}
    
	private static final class MetricsTrackingConnectionManager extends ThreadSafeClientConnManager {

		public final void releaseConnection(
				ManagedClientConnection managedclientconnection, long l,
				TimeUnit timeunit) {
			HttpConnectionMetrics httpconnectionmetrics = managedclientconnection
					.getMetrics();
			if (httpconnectionmetrics != null) {
				MetricsUtils.incrementInBytes(httpconnectionmetrics
						.getReceivedBytesCount());
				MetricsUtils.incrementOutBytes(httpconnectionmetrics
						.getSentBytesCount());
			}
			super.releaseConnection(managedclientconnection, l, timeunit);
		}

		public final ClientConnectionRequest requestConnection(
				HttpRoute httproute, Object obj) {
			final ClientConnectionRequest request = super.requestConnection(httproute, obj);
			return new ClientConnectionRequest() {

				public final void abortRequest() {
					request.abortRequest();
				}

				public final ManagedClientConnection getConnection(long l,
						TimeUnit timeunit) throws InterruptedException,
						ConnectionPoolTimeoutException {
					ManagedClientConnection managedclientconnection = request.getConnection(l, timeunit);
					HttpConnectionMetrics httpconnectionmetrics = managedclientconnection
							.getMetrics();
					if (httpconnectionmetrics != null)
						httpconnectionmetrics.reset();
					return managedclientconnection;
				}

			};
		}

		public MetricsTrackingConnectionManager(HttpParams httpparams,
				SchemeRegistry schemeregistry) {
			super(httpparams, schemeregistry);
		}
	}
}
