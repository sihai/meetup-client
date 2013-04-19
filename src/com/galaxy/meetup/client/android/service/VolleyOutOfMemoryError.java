/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.service;

import com.android.volley.VolleyError;

/**
 * 
 * @author sihai
 * 
 */
public class VolleyOutOfMemoryError extends VolleyError {

	public VolleyOutOfMemoryError(OutOfMemoryError outofmemoryerror) {
		super(outofmemoryerror);
	}
}
