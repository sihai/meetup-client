/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.util.List;

import com.galaxy.meetup.server.client.domain.GenericJson;

/**
 * 
 * @author sihai
 * 
 */
public class ApiaryError extends GenericJson {

	public Integer code;
	public String domain;
	public List<ApiaryError> errors;
	public String message;
	public String reason;
}
