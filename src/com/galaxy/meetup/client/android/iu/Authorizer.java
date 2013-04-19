/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.iu;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.android.gallery3d.common.Utils;
import com.galaxy.meetup.client.util.AccountsUtil;
import com.galaxy.meetup.client.util.EsLog;

/**
 * 
 * @author sihai
 *
 */
public class Authorizer {

	private final AccountManager mAccountManager;
	 
	public Authorizer(Context context) {
        mAccountManager = AccountManager.get(context);
    }

    public final String getAuthToken(String s, String s1) throws OperationCanceledException, IOException, AuthenticatorException {
        Account aaccount[];
        if(EsLog.isLoggable("UploaderAuthorizer", 3))
        {
            Object aobj[] = new Object[2];
            aobj[0] = s1;
            aobj[1] = Utils.maskDebugInfo(s);
            Log.d("UploaderAuthorizer", String.format("Authorizer.getAuthToken: authTokenType=%s; account=%s;", aobj));
        }
        aaccount = mAccountManager.getAccountsByType("com.galaxy");
        int length = aaccount.length;
        Account account;
        for(int i = 0; i < length; i++) {
        	account = aaccount[i];
        	if(account.name.equals(s)) {
        		Bundle bundle = (Bundle)mAccountManager.getAuthToken(account, s1, true, null, null).getResult(30000L, TimeUnit.MILLISECONDS);
                String s2 = null;
                if(bundle != null)
                    s2 = bundle.getString("authtoken");
                return s2;
        	}
        }
        return null;
    }

    public final String getFreshAuthToken(String s, String s1, String s2) throws OperationCanceledException, IOException, AuthenticatorException
    {
        if(EsLog.isLoggable("UploaderAuthorizer", 3))
            Log.d("UploaderAuthorizer", (new StringBuilder("Refreshing authToken for ")).append(Utils.maskDebugInfo(s)).toString());
        mAccountManager.invalidateAuthToken(AccountsUtil.ACCOUNT_TYPE, s2);
        return getAuthToken(s, s1);
    }

}
