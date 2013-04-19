/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.http.Header;
import org.apache.http.client.HttpResponseException;
import org.apache.http.cookie.Cookie;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.content.EsNetworkData;
import com.galaxy.meetup.client.android.network.MemoryException;
import com.galaxy.meetup.client.android.network.NetworkException;
import com.galaxy.meetup.client.android.network.http.HttpTransaction.HttpTransactionListener;
import com.galaxy.meetup.client.android.service.EsSyncAdapterService;
import com.galaxy.meetup.client.android.thread.MeetupThreadFactory;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class HttpOperation implements HttpTransactionListener {

	private static final Vector<byte[]> sBufferCache;
	private static final ExecutorService sExecutorService;
	protected static final Handler sHandler = new Handler(Looper.getMainLooper());
	private static Exception sSimulateException = null;
	private static final ThreadFactory sThreadFactory;
	
	private boolean mAborted;
	protected final EsAccount mAccount;
	protected final Context mContext;
	private volatile HttpTransaction mCurrentTransaction;
	private int mErrorCode;
	private Exception mEx;
	private final HttpRequestConfiguration mHttpRequestConfig;
	private final Intent mIntent;
	private final OperationListener mListener;
	private final String mMethod;
	private OutputStream mOutputStream;
	private String mReasonPhrase;
	private int mRetriesRemaining;
	private boolean mThreaded;
	private final String mUrl;

	static {
		sBufferCache = new Vector<byte[]>(1);
		for (int i = 0; i <= 0; i++)
			sBufferCache.add(new byte[2048]);

		sThreadFactory = new MeetupThreadFactory("HttpOperation", null, true, Thread.MIN_PRIORITY);
		sExecutorService = Executors.newCachedThreadPool(sThreadFactory);
	}
	
	//===========================================================================
    //						Constructor
    //===========================================================================
	public HttpOperation(Context context, String method, String url, EsAccount esaccount, OutputStream outputstream, Intent intent, OperationListener operationlistener){
        this(context, method, url, ((HttpRequestConfiguration) (new DefaultHttpRequestConfiguration(context, esaccount))), esaccount, outputstream, intent, operationlistener);
    }

    public HttpOperation(Context context, String method, String url, HttpRequestConfiguration httprequestconfiguration, EsAccount esaccount, OutputStream outputstream, Intent intent, 
            OperationListener operationlistener) {
        mErrorCode = -1;
        mRetriesRemaining = 2;
        mContext = context;
        mMethod = method;
        mUrl = url;
        mHttpRequestConfig = httprequestconfiguration;
        mAccount = esaccount;
        mOutputStream = outputstream;
        mIntent = intent;
        mListener = operationlistener;
    }
	    
	//===========================================================================
    //						Public function
    //===========================================================================
    
	public final void start() {
		if (EsLog.ENABLE_DOGFOOD_FEATURES) {
			HttpTransactionMetrics httptransactionmetrics = new HttpTransactionMetrics();
			start(null, httptransactionmetrics);
			httptransactionmetrics.log("HttpTransaction", "");
		} else {
			start(null, null);
		}
	}
	
    public void start(EsSyncAdapterService.SyncState syncstate, HttpTransactionMetrics httptransactionmetrics) {
    	
    	if(mAborted) {
    		if(syncstate != null && httptransactionmetrics != null)
                syncstate.getHttpTransactionMetrics().accumulateFrom(httptransactionmetrics);
    		return;
    	}
    	//if(EsLog.isLoggable("HttpTransaction", 3))
            Log.d("HttpTransaction", (new StringBuilder("Starting op: ")).append(mUrl).toString());
    	
    	try {
    		MeetupRequest request = createPostData();
	    	HttpTransaction httptransaction = null;
	    	if(null == request) {
	    		httptransaction = new HttpTransaction(mMethod, mUrl, mHttpRequestConfig, (HttpTransactionListener)this);
	    	} else {
	    		httptransaction = new HttpTransaction(mMethod, mUrl, mHttpRequestConfig, request, (HttpTransactionListener)this);
	    	}
	    	 if(EsLog.isLoggable("HttpTransaction", 3))
	             httptransaction.printHeaders();
	         if(httptransactionmetrics != null)
	             httptransactionmetrics.onBeginTransaction(getName());
	         httptransaction.setHttpTransactionMetrics(httptransactionmetrics);
	         mCurrentTransaction = httptransaction;
	         httptransaction.execute();
	         EsNetworkData.insertData(mContext, mAccount, httptransactionmetrics, null);
	         mCurrentTransaction = null;
	         if(syncstate != null && httptransactionmetrics != null)
	             syncstate.getHttpTransactionMetrics().accumulateFrom(httptransactionmetrics);
	         // TODO
    	} catch (IOException e) {
    		Log.e("ERROR", e.getMessage());
    		onHttpTransactionComplete(0, null, e);
    	} finally {
    	}
    }
	
	public final void startThreaded() {
		mThreaded = true;
		sExecutorService.execute(new Runnable() {

			public final void run() {
				start();
			}
		});
	}
    
	public final void abort() {
        mAborted = true;
        HttpTransaction httptransaction = mCurrentTransaction;
        if(httptransaction != null)
            httptransaction.abort();
    }
	
	public final EsAccount getAccount()
    {
        return mAccount;
    }

    public final int getErrorCode()
    {
        return mErrorCode;
    }

    public final Exception getException()
    {
        return mEx;
    }

    public final Intent getIntent()
    {
        return mIntent;
    }

    public final String getMethod()
    {
        return mMethod;
    }

    public String getName()
    {
        return getClass().getSimpleName();
    }

    public final OutputStream getOutputStream()
    {
        return mOutputStream;
    }

    public final String getReasonPhrase()
    {
        return mReasonPhrase;
    }

    public final String getUrl()
    {
        return mUrl;
    }

    public boolean hasError()
    {
        boolean flag;
        if(mErrorCode != 200)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public final boolean isAborted()
    {
        return mAborted;
    }
    
	public final void setOutputStream(OutputStream outputstream) {
		mOutputStream = outputstream;
	}
	
	public void setErrorInfo(int i, String s, Exception exception) {
        mErrorCode = i;
        mReasonPhrase = s;
        mEx = exception;
    }
    
	public final void logAndThrowExceptionIfFailed(String s) throws IOException {
		if (hasError()) {
			logError(s);
			if (hasError()) {
				if (mEx != null)
					if (android.os.Build.VERSION.SDK_INT < 9)
						throw new IOException((new StringBuilder())
								.append(getName()).append(" operation failed ")
								.append(mEx.getMessage()).toString());
					else
						throw new IOException((new StringBuilder())
								.append(getName()).append(" operation failed")
								.toString(), mEx);
				if (hasError())
					throw new IOException((new StringBuilder())
							.append(getName())
							.append(" operation failed, error: ")
							.append(mErrorCode).append(" [")
							.append(mReasonPhrase).append("]").toString());
			}
		}
	}
	
	public void logError(String s) {
		if(null != mEx) {
			Log.e(s, (new StringBuilder("[")).append(getName()).append("] failed due to exception: ").append(mEx).toString(), mEx);
			return;
		}
		if(hasError() && EsLog.isLoggable(s, 4))
            Log.i(s, (new StringBuilder("[")).append(getName()).append("] failed due to error: ").append(mErrorCode).append(" [").append(mReasonPhrase).append("]").toString());
    }
    
	@Override
	public void onHttpCookie(Cookie cookie) {
		// TODO Auto-generated method stub

	}
	
	public void onHttpOperationComplete(int i, String s, Exception exception) {
	}
	
	

	@Override
	public void onHttpReadErrorFromStream(InputStream inputstream, String s,
			int i, Header[] aheader, int j) throws IOException {
		// TODO Auto-generated method stub

	}

	public void onHttpReadFromStream(InputStream inputstream, String s, int i, Header aheader[]) throws IOException {
		OutputStream outputstream = mOutputStream;
		if (outputstream != null) {
			readFromStream(inputstream, i, outputstream);
			//onHttpHandleContentFromStream(null);
		} else if (s != null)
			onHttpHandleContentFromStream(inputstream);
		else
			Log.e("HttpTransaction", "Content type not specified");
	}

	public final void onHttpTransactionComplete(final int errorCode, final String reasonPhrase, Exception exception) {
		Exception result = exception;
		try {
	        if(!isImmediatelyRetryableError(exception) || mRetriesRemaining <= 0)
	            return;
	        if(isAuthenticationError(exception))
	            mHttpRequestConfig.invalidateAuthToken();
	        Log.i("HttpTransaction", "====> Restarting operation...");
	        mRetriesRemaining = -1 + mRetriesRemaining;
	        try {
	        	start();
	        } catch (Exception e) {
	        	result = e;
	        	Log.e("HttpTransaction", "====> Retry failed");
	            e.printStackTrace();
	        }
		} finally {
            final Exception fex = result;
			if(mThreaded) {
                sHandler.post(new Runnable() {
                    public final void run()
                    {
                        completeOperation(errorCode, reasonPhrase, fex);
                    }
                });
			} else {
                completeOperation(errorCode, reasonPhrase, fex);
			}
		}
    }
	
	public final void onStartResultProcessing() {
        if(mCurrentTransaction != null)
            mCurrentTransaction.onStartResultProcessing();
    }

	
	//===========================================================================
    //						Protected function
    //===========================================================================
	protected boolean isAuthenticationError(Exception exception) {
		if(!(exception instanceof HttpResponseException)) {
			return false;
		}
		return 401 == ((HttpResponseException)exception).getStatusCode();
    }
	
	protected boolean isImmediatelyRetryableError(Exception exception) {
        return isAuthenticationError(exception);
    }
	
	protected static InputStream captureResponse(InputStream inputstream, int i, StringBuilder stringbuilder) throws IOException {
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
		readFromStream(inputstream, i, bytearrayoutputstream);
		byte abyte0[] = bytearrayoutputstream.toByteArray();
		stringbuilder.append(new String(abyte0));
		return new ByteArrayInputStream(abyte0);
	}
	
	public MeetupRequest createPostData() throws IOException {
		return null;
	}
	
	public void onHttpHandleContentFromStream(InputStream inputstream) throws IOException {
		
	}
	
	//===========================================================================
    //						Private function
    //===========================================================================
	private void completeOperation(int i, String s, Exception exception)
    {
        setErrorInfo(i, s, exception);
        if(mListener != null)
            mListener.onOperationComplete(this);
    }
	
	private static void readFromStream(InputStream inputstream, int i, OutputStream outputstream) throws NetworkException, MemoryException {
		try {
			byte[] abyte0 = null;
			boolean flag = false;
			try {
				abyte0 = (byte[])sBufferCache.remove(0);
				flag = true;
			} catch (IndexOutOfBoundsException e) {
				abyte0 = new byte[2048];
		        flag = false;
			}
			
			if(i != -1) {
				do {
		            int i1 = inputstream.read(abyte0, 0, abyte0.length);
		            if(i1 == -1)
		                break;
		            outputstream.write(abyte0, 0, i1);
		        } while(true);
			} else {
				int rest = i;
				int readed = 0;
				try {
					while(rest > 0) {
						readed = inputstream.read(abyte0, 0, Math.min(rest, abyte0.length));
						if(readed == -1)
				            throw new NetworkException((new StringBuilder("Invalid content length: ")).append(rest).toString());
						else if(readed == 0) {
							break;
						}
						rest -= readed;
						outputstream.write(abyte0, 0, readed);
					}
				} catch (OutOfMemoryError e) {
					throw new MemoryException(e.getMessage());
				}
			}
			if(flag)
	            sBufferCache.add(abyte0);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		} finally {
			try {
				inputstream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				outputstream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				outputstream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}
	
	//===========================================================================
    //						Inner class
    //===========================================================================
	public static interface OperationListener {

        void onOperationComplete(HttpOperation httpoperation);
    }
}
