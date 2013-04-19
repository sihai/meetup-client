/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.text.TextUtils;

import com.galaxy.meetup.client.util.AccountsUtil;
import com.galaxy.meetup.client.util.Property;

/**
 * 
 * @author sihai
 *
 */
public final class ApiaryAuthDataFactory {
	
	static final Map sAuthDatas = new HashMap();
	
	public ApiaryAuthDataFactory() {
	}

	public static ApiaryAuthData getAuthData(String s) {
		Object obj;
		synchronized (sAuthDatas) {
			obj = (ApiaryAuthData) sAuthDatas.get(s);
			if (obj == null) {
				obj = new ApiaryAuthDataImpl(s);
				sAuthDatas.put(s, obj);
			}
		}
		return ((ApiaryAuthData) (obj));
	}
	
	public static interface ApiaryAuthData {

		public abstract Long getAuthTime(String s);

		public abstract String getAuthToken(Context context, String s) throws AuthenticatorException,
			OperationCanceledException, IOException;

		public abstract void invalidateAuthToken(Context context, String s) throws OperationCanceledException,
			AuthenticatorException, IOException;
	}

	private static final class ApiaryAuthDataImpl implements ApiaryAuthData {

		private final String mScope;
		private final Map mTokenTimes = new HashMap();
		private final Map mTokens = new HashMap();

		public ApiaryAuthDataImpl(String s) {
			mScope = s;
		}
		
		public final Long getAuthTime(String s) {
			Long long1;
			if (Property.ENABLE_DOGFOOD_FEATURES.getBoolean()
					&& !TextUtils.isEmpty(Property.PLUS_APIARY_AUTH_TOKEN.get()))
				long1 = Long.valueOf(System.currentTimeMillis());
			else
				long1 = (Long) mTokenTimes.get(s);
			return long1;
		}

		public final String getAuthToken(Context context, String s) throws AuthenticatorException, OperationCanceledException, IOException {

			String s1 = null;
			
			if(Property.ENABLE_DOGFOOD_FEATURES.getBoolean()) {
				String s2 = Property.PLUS_APIARY_AUTH_TOKEN.get();
	            if(!TextUtils.isEmpty(s2)) {
	            	return s2;
	            }
			}

			synchronized(this) {
				s1 = (String) mTokens.get(s);
				if (s1 != null) {
					Long long2 = (Long) mTokenTimes.get(s1);
					if (long2 == null || System.currentTimeMillis() - long2.longValue() > 0x36ee80L) {
						mTokens.remove(s);
						mTokenTimes.remove(s1);
						AccountsUtil.invalidateAuthToken(context, s1);
						s1 = null;
					}
				}
			}

			if(s1 != null) {
				return s1; 
			}
			
			Long long1 = Long.valueOf(System.currentTimeMillis());
            s1 = AccountsUtil.getAuthToken(context, s, mScope);
            if(s1 == null) {
            	return null;
            }
            
            synchronized(this) {
	            String s3 = (String)mTokens.remove(s);
	            if(s3 != null)
	                mTokenTimes.remove(s3);
	            mTokens.put(s, s1);
	            mTokenTimes.put(s1, long1);
            }
            return s1;
		}

		public final void invalidateAuthToken(Context context, String s) throws OperationCanceledException,
			AuthenticatorException, IOException {

			if (!Property.ENABLE_DOGFOOD_FEATURES.getBoolean()
					|| TextUtils.isEmpty(Property.PLUS_APIARY_AUTH_TOKEN.get())) {
				String s1;
				s1 = (String) mTokens.remove(s);
				if (s1 != null) {
					mTokenTimes.remove(s1);
					AccountsUtil.invalidateAuthToken(context, s1);
				}

			} else {
				return;
			}
		}
	}
}
