/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network.http;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecRegistry;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import android.os.StrictMode;
import android.util.Log;

import com.galaxy.meetup.client.android.network.NetworkException;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class HttpTransaction implements HttpRequestInterceptor,
		HttpResponseInterceptor {
	
	private static final HttpParams sHttpParams;
    private static final SchemeRegistry sSupportedSchemes;
    
    private boolean mAborted;
    private final String url;
    private final MeetupRequest request;
    private final HttpRequestBase mHttpMethod;
    private final HttpTransactionListener mListener;
    private HttpTransactionMetrics mMetrics;

    static  {
        sSupportedSchemes = new SchemeRegistry();
        BasicHttpParams basichttpparams = new BasicHttpParams();
        sHttpParams = basichttpparams;
        basichttpparams.setParameter("http.socket.timeout", Integer.valueOf(0x15f90));
        sHttpParams.setParameter("http.connection.timeout", Integer.valueOf(3000));
        sSupportedSchemes.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        sSupportedSchemes.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        
        
    }
    
	public HttpTransaction(String method, String url, HttpRequestConfiguration httprequestconfiguration, HttpTransactionListener httptransactionlistener) throws IOException {
		this(method, url, httprequestconfiguration, null, httptransactionlistener);
	}

	public HttpTransaction(String method, String url, HttpRequestConfiguration httprequestconfiguration, MeetupRequest request, HttpTransactionListener httptransactionlistener) throws IOException {
		this.url = url;
		if (method.equals("POST"))
			mHttpMethod = new HttpPost(url);
		else if (method.equals("PUT"))
			mHttpMethod = new HttpPut(url);
		else
			throw new NetworkException((new StringBuilder("Unsupported method: ")).append(method).toString());
		if (httptransactionlistener == null)
			throw new NetworkException("The listener cannot be null");
		mListener = httptransactionlistener;
		httprequestconfiguration.addHeaders(mHttpMethod);
		this.request = request;
		if (request != null)
			((HttpPost) mHttpMethod).setEntity(request.toEntity());
	}
	

	@Override
	public final void process(HttpRequest httprequest, HttpContext httpcontext) {
        CookieSpecRegistry cookiespecregistry = (CookieSpecRegistry)httpcontext.getAttribute("http.cookiespec-registry");
        HttpHost httphost = (HttpHost)httpcontext.getAttribute("http.target_host");
        ManagedClientConnection managedclientconnection = (ManagedClientConnection)httpcontext.getAttribute("http.connection");
        String s = HttpClientParams.getCookiePolicy(httprequest.getParams());
        URI uri = ((HttpUriRequest)httprequest).getURI();
        String s1 = httphost.getHostName();
        int i = httphost.getPort();
        if(i < 0)
            i = managedclientconnection.getRemotePort();
        CookieOrigin cookieorigin = new CookieOrigin(s1, i, uri.getPath(), managedclientconnection.isSecure());
        httpcontext.setAttribute("http.cookie-spec", cookiespecregistry.getCookieSpec(s, httprequest.getParams()));
        httpcontext.setAttribute("http.cookie-origin", cookieorigin);
        if(mMetrics != null)
            mMetrics.setConnectionMetrics(managedclientconnection.getMetrics());
    }

	@Override
	public final void process(HttpResponse httpresponse, HttpContext httpcontext) {
        CookieSpec cookiespec = (CookieSpec)httpcontext.getAttribute("http.cookie-spec");
        CookieOrigin cookieorigin = (CookieOrigin)httpcontext.getAttribute("http.cookie-origin");
        processCookies(httpresponse.headerIterator("Set-Cookie"), cookiespec, cookieorigin);
        if(cookiespec.getVersion() > 0)
            processCookies(httpresponse.headerIterator("Set-Cookie2"), cookiespec, cookieorigin);
    }
	
	public final void abort() {
		
		if(mAborted) {
			return;
		}
		if(null == mHttpMethod) {
			return;
		}
		if(android.os.Build.VERSION.SDK_INT < 9) {
			mHttpMethod.abort();
		} else {
			StrictMode.ThreadPolicy threadpolicy = StrictMode.getThreadPolicy();
			StrictMode.setThreadPolicy(android.os.StrictMode.ThreadPolicy.LAX);
			mHttpMethod.abort();
			StrictMode.setThreadPolicy(threadpolicy);
		}
    }
	
	public final boolean isAborted() {
		boolean flag;
		if (mAborted)
			flag = true;
		else if (mHttpMethod != null)
			flag = mHttpMethod.isAborted();
		else
			flag = false;
		return flag;
	}
	
	public final void onStartResultProcessing() {
        if(mMetrics != null)
            mMetrics.onStartResultProcessing();
    }
	
	public final void setHttpTransactionMetrics(HttpTransactionMetrics httptransactionmetrics) {
        mMetrics = httptransactionmetrics;
    }
	
	final void printHeaders() {
        if(EsLog.isLoggable("HttpTransaction", 3)) {
            StringBuilder stringbuilder = new StringBuilder("HTTP headers:\n");
            Header aheader[] = mHttpMethod.getAllHeaders();
            int i = aheader.length;
            int j = 0;
            while(j < i)  {
                Header header = aheader[j];
                if("Authorization".equals(header.getName()))
                    stringbuilder.append("Authorization: <removed>");
                else
                    stringbuilder.append(header.toString());
                stringbuilder.append("\n");
                j++;
            }
            Log.d("HttpTransaction", stringbuilder.toString());
        }
    }
	
	public final void execute() throws NetworkException {
		_execute_v2_();
    }
	
	private void _execute_v1_() throws NetworkException {
		int statusCode = 0;
        String reason = "Unknown";
        
		DefaultHttpClient defaulthttpclient = null;
		InputStream inputstream = null;
		
		try {
			if(mAborted) {
				if(mMetrics != null)
		            mMetrics.onEndTransaction();
				throw new NetworkException("Canceled");
			}
			
			defaulthttpclient = new DefaultHttpClient(new SingleClientConnManager(sHttpParams, sSupportedSchemes), sHttpParams);
	        defaulthttpclient.removeRequestInterceptorByClass(RequestAddCookies.class);
	        defaulthttpclient.removeResponseInterceptorByClass(ResponseProcessCookies.class);
	        defaulthttpclient.addRequestInterceptor(this);
	        defaulthttpclient.addResponseInterceptor(this);
	        if(mAborted) {
	        	throw new NetworkException("Canceled");
	        }
	        HttpResponse httpresponse = defaulthttpclient.execute(mHttpMethod);
	        if(mAborted) {
	        	throw new NetworkException("Canceled");
	        }
	        
	        statusCode = httpresponse.getStatusLine().getStatusCode();
	        reason = httpresponse.getStatusLine().getReasonPhrase();
	        
	        
	        HttpEntity httpentity = httpresponse.getEntity();
	        Header[] aheader = httpresponse.getAllHeaders();
	        
	        
	        String encoding = null;
	        if(httpentity.getContentEncoding() != null) {
	        	encoding = httpentity.getContentEncoding().getValue();
	        }
	        String contentType = httpentity.getContentType().getValue();
	        int k = contentType.indexOf(';');
            if(k != -1)
            	contentType = contentType.substring(0, k);
	        int length = (int)httpentity.getContentLength();
	        if(EsLog.isLoggable("HttpTransaction", 3))
	            Log.d("HttpTransaction", (new StringBuilder("readFromHttpStream: Encoding: ")).append(encoding).append(", type: ").append(contentType).append(", length: ").append(length).toString());
	        
	        inputstream = httpentity.getContent();
	        if(null != encoding && encoding.equals("gzip")) {
	        	inputstream = new GZIPInputStream(inputstream);
	        }
	        
	        if(statusCode == 200) {
	        	mListener.onHttpReadFromStream(inputstream, contentType, length, aheader);
	        } else {
	        	mListener.onHttpReadErrorFromStream(inputstream, contentType, length, aheader, statusCode);
	        }
		} catch (NetworkException e) {
			reason = e.getMessage();
			throw e;
		} catch (IOException e) {
			reason = e.getMessage();
		} finally {
			if(defaulthttpclient != null)
	            defaulthttpclient.getConnectionManager().shutdown();
			if(null != inputstream) {
				try {
					inputstream.close();
				} catch (IOException e) {
					// TODO
					e.printStackTrace();
				}
			}
			
			if(mMetrics != null)
	            mMetrics.onEndTransaction();
			mListener.onHttpTransactionComplete(statusCode, reason, new HttpResponseException(statusCode, reason));
		}
	}
	
	private void _execute_v2_() throws NetworkException {
		int statusCode = 0;
        String reason = "Unknown";
		HttpURLConnection httpurlconnection = null;
        try {
        	HttpEntity entity = this.request.toEntity();
	        httpurlconnection = (HttpURLConnection)(new URL(url)).openConnection();
	        httpurlconnection.setInstanceFollowRedirects(false);
	        httpurlconnection.setDoOutput(true);
	        httpurlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        
	        httpurlconnection.setRequestProperty("Content-Length", Long.toString(entity.getContentLength()));
	        OutputStream outputstream = httpurlconnection.getOutputStream();
	        entity.writeTo(outputstream);
	        outputstream.flush();
	        statusCode = httpurlconnection.getResponseCode(); 
	        if(statusCode == 200) {
	        	mListener.onHttpReadFromStream(httpurlconnection.getInputStream(), "", -1, null);
	        } else {
	        	mListener.onHttpReadErrorFromStream(httpurlconnection.getInputStream(), null, -1, null, statusCode);
	        }
        } catch (IOException e) {
        	reason = e.getMessage();
        } finally {
        	if(null != httpurlconnection) {
        		httpurlconnection.disconnect();
        	}
        	mListener.onHttpTransactionComplete(statusCode, reason, new HttpResponseException(statusCode, reason));
        }
	}
	
	//===========================================================================
    //						Private Function
    //===========================================================================
	private void processCookies(HeaderIterator headeriterator,
			CookieSpec cookiespec, CookieOrigin cookieorigin) {
		while (headeriterator.hasNext()) {
			Header header = headeriterator.nextHeader();
			try {
				Iterator iterator = cookiespec.parse(header, cookieorigin)
						.iterator();
				while (iterator.hasNext()) {
					Cookie cookie = (Cookie) iterator.next();
					mListener.onHttpCookie(cookie);
				}
			} catch (MalformedCookieException malformedcookieexception) {
				Log.e("HttpTransaction", "Malformed cookie",
						malformedcookieexception);
			}
		}
	}
	
	//===========================================================================
    //						Inner class
    //===========================================================================
	public static interface HttpTransactionListener {

		void onHttpCookie(Cookie cookie);

		void onHttpReadErrorFromStream(InputStream inputstream, String s, int i, Header aheader[], int j) throws IOException;

        void onHttpReadFromStream(InputStream inputstream, String s, int i, Header aheader[]) throws IOException;

        void onHttpTransactionComplete(int i, String s, Exception exception);
    }
	
	private static final class CountingOutputStream extends FilterOutputStream {

		private final long mChunk;
		private final long mLength;
		private long mNext;
		private final HttpTransaction mTransaction;
		private long mTransferred;

		public CountingOutputStream(HttpTransaction httptransaction, OutputStream outputstream, long l) {
			super(outputstream);
			mTransaction = httptransaction;
			mLength = 2L * l;
			mTransferred = 0L;
			mChunk = mLength / 5L;
			mNext = mChunk;
		}

		public final void write(int i) throws IOException {
			super.write(i);
			mTransferred = 1L + mTransferred;
			if (mTransferred >= mNext) {
				super.flush();
				HttpTransactionListener _tmp = mTransaction.mListener;
				long _tmp1 = mTransferred;
				long _tmp2 = mLength;
				mNext = mNext + mChunk;
			}
		}

		public final void write(byte abyte0[], int i, int j) throws IOException {
			super.write(abyte0, i, j);
			mTransferred = mTransferred + (long) j;
			if (mTransferred >= mNext) {
				super.flush();
				HttpTransactionListener _tmp = mTransaction.mListener;
				long _tmp1 = mTransferred;
				long _tmp2 = mLength;
				mNext = mNext + mChunk;
			}
		}
	}
	
	private static final class MyInputStreamEntity extends HttpEntityWrapper {

		private final HttpTransaction mTransaction;
		
		public MyInputStreamEntity(HttpTransaction httptransaction,
				HttpEntity httpentity) {
			super(httpentity);
			mTransaction = httptransaction;
		}
		
		public final void writeTo(OutputStream outputstream) throws IOException {
			super.writeTo(new CountingOutputStream(mTransaction, outputstream,getContentLength()));
		}

	}
}
