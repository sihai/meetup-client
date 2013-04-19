/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.api.ApiaryErrorResponse;
import com.galaxy.meetup.client.android.api.OzServerException;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.http.ApiaryHttpRequestConfiguration;
import com.galaxy.meetup.client.android.network.http.HttpOperation;
import com.galaxy.meetup.client.android.network.http.HttpRequestConfiguration;
import com.galaxy.meetup.client.android.network.http.MeetupRequest;
import com.galaxy.meetup.client.android.service.AndroidNotification;
import com.galaxy.meetup.client.util.EsLog;
import com.galaxy.meetup.server.client.domain.GenericJson;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 * 
 */
public abstract class ApiaryOperation extends HttpOperation {

	public static final String COMMAND = "_command_";
	
	private Class responseClass;
	
	protected ApiaryOperation(Context context, EsAccount esaccount, String url, Intent intent, HttpOperation.OperationListener operationlistener, HttpRequestConfiguration httprequestconfiguration, String method, Class responseClass) {
		super(context, method, url, httprequestconfiguration, esaccount, null, intent, operationlistener);
		this.responseClass = responseClass;
	}

	public ApiaryOperation(Context context, EsAccount esaccount, String s, String s1, String s2, Intent intent, HttpOperation.OperationListener operationlistener, Class responseClass) {
		this(context, esaccount, s, s1, s2, intent, operationlistener, ((HttpRequestConfiguration) (new ApiaryHttpRequestConfiguration( context, esaccount, s2, s1))), responseClass);
	}

	protected ApiaryOperation(Context context, EsAccount esaccount, String url, String s1, String s2, Intent intent, HttpOperation.OperationListener operationlistener, HttpRequestConfiguration httprequestconfiguration, Class responseClass) {
		this(context, esaccount, url, intent, operationlistener, httprequestconfiguration, "POST", responseClass);
	}
    
	protected abstract GenericJson populateRequest();
	
	protected abstract void handleResponse(GenericJson genericjson) throws IOException;
	
	protected MeetupRequest createHttpEntity(GenericJson genericjson)
    {
        return new MeetupRequest(genericjson);
    }

	public MeetupRequest createPostData() {
		GenericJson genericjson = populateRequest();
        if(EsLog.isLoggable("HttpTransaction", 3) || EsLog.isLoggable(getLogTag(), 3)) {
        	EsLog.doWriteToLog(3, "HttpTransaction", (new StringBuilder("Apiary request: ")).append(genericjson.getClass().getSimpleName()).append("\n").append(genericjson.toJsonString()).toString());
        }
        return createHttpEntity(genericjson);
    }

    protected String getLogTag()
    {
        return "HttpTransaction";
    }

    protected final boolean isAuthenticationError(Exception exception) {
    	if(!(exception instanceof OzServerException)) {
    		return super.isAuthenticationError(exception);
    	}
    	if(1 ==  ((OzServerException)exception).getErrorCode()) {
    		return true;
    	}
    	return super.isAuthenticationError(exception);
    }

    protected final boolean isImmediatelyRetryableError(Exception exception) {
    	if(!(exception instanceof OzServerException)) {
    		return super.isImmediatelyRetryableError(exception);
    	}
    	int errorCode = ((OzServerException)exception).getErrorCode();
    	if(1 == errorCode || 6 == errorCode) {
    		return true;
    	} else {
    		return super.isImmediatelyRetryableError(exception);
    	}
    }

    public final void onHttpHandleContentFromStream(InputStream inputstream) throws IOException {
        onStartResultProcessing();
        GenericJson genericjson = (GenericJson)JsonUtil.fromInputStream(inputstream, responseClass);
        if(EsLog.isLoggable("HttpTransaction", 2) || EsLog.isLoggable(getLogTag(), 2))
        	EsLog.doWriteToLog(2, "HttpTransaction", (new StringBuilder("Apiary response: ")).append(genericjson.getClass().getSimpleName()).append("\n").append(genericjson.toJsonString()).toString());
        handleResponse(genericjson);
    }

    public void onHttpReadErrorFromStream(InputStream inputstream, String s, int i, Header aheader[], int j) throws IOException {
        if(EsLog.isLoggable("HttpTransaction", 4) || EsLog.isLoggable(getLogTag(), 4)) {
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append("Apiary error response: ").append(getName()).append('\n');
            inputstream = captureResponse(inputstream, i, stringbuilder);
            Log.i("HttpTransaction", stringbuilder.toString());
        }
        if(401 == j) {
        	return;
        }
        ApiaryErrorResponse apiaryerrorresponse = (ApiaryErrorResponse)JsonUtil.fromInputStream(inputstream, ApiaryErrorResponse.class);
        if(TextUtils.isEmpty(apiaryerrorresponse.getErrorType()))
            return;
        if(EsLog.isLoggable("HttpTransaction", 6) || EsLog.isLoggable(getLogTag(), 6))
            Log.e("HttpTransaction", (new StringBuilder("Apiary error reason: ")).append(apiaryerrorresponse.getErrorType()).toString());
        OzServerException ozserverexception = new OzServerException(apiaryerrorresponse);
        if(10 == ozserverexception.getErrorCode()) {
        	AndroidNotification.showUpgradeRequiredNotification(mContext);
        } 
        throw ozserverexception;
    }
}
