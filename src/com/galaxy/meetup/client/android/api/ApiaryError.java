/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.util.List;

/**
 * 
 * @author sihai
 * 
 */
public class ApiaryError {

	public Integer code;
	public String domain;
	public List<ApiaryError> errors;
	public String message;
	public String reason;
}
