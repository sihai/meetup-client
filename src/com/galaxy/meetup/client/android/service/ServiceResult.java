
package com.galaxy.meetup.client.android.service;

import com.galaxy.meetup.client.android.network.http.HttpOperation;

public final class ServiceResult {

	public ServiceResult() {
		this(200, "Ok", null);
	}

	public ServiceResult(int i, String s, Exception exception) {
		mErrorCode = i;
		mReasonPhrase = s;
		mException = exception;
	}

	public ServiceResult(HttpOperation httpoperation) {
		mErrorCode = httpoperation.getErrorCode();
		mReasonPhrase = httpoperation.getReasonPhrase();
		mException = httpoperation.getException();
	}

    public ServiceResult(boolean flag) {
        this(flag ? (int)'\310' : '\0', flag ? "Ok" : "Error", null);
    }
    
	public final int getErrorCode() {
		return mErrorCode;
	}

	public final Exception getException() {
		return mException;
	}

	public final String getReasonPhrase() {
		return mReasonPhrase;
	}

	public final boolean hasError() {
		boolean flag;
		if (mErrorCode != 200)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public final String toString() {
		Object aobj[] = new Object[3];
		aobj[0] = Integer.valueOf(mErrorCode);
		aobj[1] = mReasonPhrase;
		aobj[2] = mException;
		return String.format(
				"ServiceResult(errorCode=%d, reasonPhrase=%s, exception=%s)",
				aobj);
	}

	private final int mErrorCode;
	private final Exception mException;
	private final String mReasonPhrase;
}
