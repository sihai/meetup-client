/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.util.Pair;

import com.galaxy.meetup.client.util.AccountsUtil;

/**
 * 
 * @author sihai
 *
 */
public class AuthData {
	
	private static final Map<Pair<String, String>, String> sActionTokens = newSynchronizedMap();
    private static final Map<Pair<String, String>, String> sAuthTokens = newSynchronizedMap();
    
    public static String getActionToken(String s, String s1) {
        String s2 = (String)sActionTokens.get(new Pair(s, s1));
        if(s2 == null)
            s2 = "XXX";
        return s2;
    }

    public static String getAuthToken(Context context, String s, String s1)
        throws OperationCanceledException, IOException, AuthenticatorException {
        Pair pair = new Pair(s, s1);
        String s2 = (String)sAuthTokens.get(pair);
        if(s2 == null) {
            s2 = AccountsUtil.getAuthToken(context, s, s1);
            sAuthTokens.put(pair, s2);
        }
        return s2;
    }

    public static void invalidateAuthToken(Context context, String s, String s1)
        throws OperationCanceledException, IOException, AuthenticatorException {
        Pair pair = new Pair(s, s1);
        String s2 = (String)sAuthTokens.remove(pair);
        if(s2 == null)
            s2 = AccountsUtil.getAuthToken(context, s, s1);
        AccountsUtil.invalidateAuthToken(context, s2);
    }

    private static Map<Pair<String, String>, String> newSynchronizedMap() {
        return Collections.synchronizedMap(new HashMap<Pair<String, String>, String>());
    }

    public static void setActionToken(String s, String s1, String s2) {
        sActionTokens.put(new Pair(s, s1), s2);
    }
}
