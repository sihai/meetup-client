/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.io.IOException;

/**
 * 
 * @author sihai
 *
 */
public class ProtocolException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -364178397696906504L;
	
	private final int mErrorCode;

	public ProtocolException() {
		this.mErrorCode = 0;
	}

	public ProtocolException(int errorCode, String msg) {
		super(msg);
		this.mErrorCode = errorCode;
	}

	public ProtocolException(String msg) {
		this(0, msg);
	}

	public final int getErrorCode() {
		return this.mErrorCode;
	}
}
