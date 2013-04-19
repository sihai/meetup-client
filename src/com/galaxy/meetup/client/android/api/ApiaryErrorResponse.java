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
public class ApiaryErrorResponse extends GenericJson {

	public static final String AGE_RESTRICTED = "AGE_RESTRICTED";
    public static final String APP_UPGRADE_REQUIRED = "APP_UPGRADE_REQUIRED";
    public static final String BAD_PROFILE = "BAD_PROFILE";
    public static final String ES_BLOCKED_FOR_DOMAIN_BY_ADMIN = "ES_BLOCKED_FOR_DOMAIN_BY_ADMIN";
    public static final String ES_STREAM_POST_RESTRICTIONS_NOT_SUPPORTED = "ES_STREAM_POST_RESTRICTIONS_NOT_SUPPORTED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String HAS_PLUSONE_OPT_IN_REQUIRED = "HAS_PLUSONE_OPT_IN_REQUIRED";
    public static final String INVALID_ACTION_TOKEN = "INVALID_ACTION_TOKEN";
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String INVALID_VALUE = "INVALID_VALUE";
    public static final String NETWORK_ERROR = "NETWORK_ERROR";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String OUT_OF_BOX_REQUIRED = "OUT_OF_BOX_REQUIRED";
    public static final String PERMISSION_ERROR = "PERMISSION_ERROR";
    public static final String SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";
    public ApiaryError error;
    
	public ApiaryErrorResponse() {
	}

	public String getErrorMessage() {
		if (null == error) {
			return null;
		}
		if (null != error.message) {
			return error.message;
		}
		if (null != error.errors) {
			List<ApiaryError> list = error.errors;
			int i = 0;
			int j = list.size();
			do {
				if (i >= j)
					break;
				ApiaryError apiaryerror = list.get(i);
				if (apiaryerror.message != null) {
					return apiaryerror.message;

				}
				i++;
			} while (true);
		}

		return null;

	}

	public String getErrorType() {
		if (null == error) {
			return null;
		}
		if (null != error.reason) {
			return error.reason;
		}
		if (null != error.errors) {
			List<ApiaryError> arraylist = error.errors;
			int i = 0;
			int j = arraylist.size();
			do {
				if (i >= j)
					break;
				ApiaryError apiaryerror = arraylist.get(i);
				if (apiaryerror.reason != null) {
					return apiaryerror.reason;
				}
				i++;
			} while (true);
		}
		return null;
	}
}
