/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network;

import java.io.IOException;

/**
 * 
 * @author sihai
 *
 */
public class MemoryException extends IOException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1285730672059050679L;

	public MemoryException(String errorMsg) {
		super(errorMsg);
	}
}
