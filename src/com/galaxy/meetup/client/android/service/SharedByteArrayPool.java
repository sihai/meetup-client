/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import com.android.volley.toolbox.ByteArrayPool;

/**
 * 
 * @author sihai
 *
 */
public class SharedByteArrayPool {

	private static ByteArrayPool sByteArrayPool;
	
	public static ByteArrayPool getInstance() {
		if (sByteArrayPool == null)
			sByteArrayPool = new ByteArrayPool(0x20000);
		return sByteArrayPool;
	}
}
