/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.api;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author sihai
 * 
 */
public class OzServerException extends ProtocolException {

	private static Map<String, Integer> sErrorCodeMap;
	private static final long serialVersionUID = 0xa6135beddde36db8L;
	private final ApiaryErrorResponse mError;

	static {

		sErrorCodeMap = new HashMap<String, Integer>(16);
		sErrorCodeMap.put("INVALID_CREDENTIALS", Integer.valueOf(1));
		sErrorCodeMap.put("FORBIDDEN", Integer.valueOf(2));
		sErrorCodeMap.put("NOT_FOUND", Integer.valueOf(3));
		sErrorCodeMap.put("INVALID_VALUE", Integer.valueOf(4));
		sErrorCodeMap.put("SERVICE_UNAVAILABLE", Integer.valueOf(5));
		sErrorCodeMap.put("INVALID_ACTION_TOKEN", Integer.valueOf(6));
		sErrorCodeMap.put("PERMISSION_ERROR", Integer.valueOf(7));
		sErrorCodeMap.put("NETWORK_ERROR", Integer.valueOf(8));
		sErrorCodeMap.put("OUT_OF_BOX_REQUIRED", Integer.valueOf(9));
		sErrorCodeMap.put("APP_UPGRADE_REQUIRED", Integer.valueOf(10));
		sErrorCodeMap.put("HAS_PLUSONE_OPT_IN_REQUIRED", Integer.valueOf(11));
		sErrorCodeMap.put("BAD_PROFILE", Integer.valueOf(12));
		sErrorCodeMap.put("AGE_RESTRICTED", Integer.valueOf(13));
		sErrorCodeMap.put("ES_STREAM_POST_RESTRICTIONS_NOT_SUPPORTED",
				Integer.valueOf(14));
		sErrorCodeMap
				.put("ES_BLOCKED_FOR_DOMAIN_BY_ADMIN", Integer.valueOf(15));
	}

	public OzServerException(ApiaryErrorResponse apiaryerrorresponse) {
		super(sErrorCodeMap.get(null == apiaryerrorresponse.getErrorType() ? 0
				: sErrorCodeMap.get(apiaryerrorresponse.getErrorType())),
				(new StringBuilder())
						.append(apiaryerrorresponse.getErrorType())
						.append(": ")
						.append(apiaryerrorresponse.getErrorMessage())
						.toString());

		mError = apiaryerrorresponse;
	}
}