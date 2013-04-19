/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.galaxy.meetup.client.android.R;
import com.galaxy.meetup.client.android.content.EsAccount;
import com.galaxy.meetup.client.android.network.NetworkException;
import com.galaxy.meetup.server.client.domain.response.AuthorizeResponse;
import com.galaxy.meetup.server.client.util.JsonUtil;

/**
 * 
 * @author sihai
 *
 */
public class AccountsUtil {

	public static final String ACCOUNT_TYPE = "com.galaxy";
	
	public static void addAccount(Activity activity) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("allowSkip", false);
        bundle.putCharSequence("introMessage", activity.getString(R.string.create_account_prompt));
        AccountManager.get(activity).addAccount(ACCOUNT_TYPE, "webupdates", null, bundle, activity, new AccountManagerCallback<Bundle>(){

			@Override
			public void run(AccountManagerFuture<Bundle> future) {
				try {
					Bundle result = future.getResult();
					Log.i("Create Account failed", result.toString());
				} catch (AuthenticatorException e) {
					Log.e("Create Account failed", "AuthenticatorException", e);
				} catch (IOException e) {
					Log.e("Create Account failed", "IOException", e);
				} catch (OperationCanceledException e) {
					Log.e("Create Account failed", "OperationCanceledException", e);
				}
			}
        	
        }, null);
    }

    public static List<Account> getAccounts(Context context) {
        String s = Property.AUTH_EMAIL.get();
        List<Account> list;
        if(TextUtils.isEmpty(s)) {
            Account aaccount[] = AccountManager.get(context).getAccountsByType(ACCOUNT_TYPE);
            List<Account> arraylist = new ArrayList<Account>(aaccount.length);
            int i = aaccount.length;
			for (int j = 0; j < i; j++) {
				arraylist.add(aaccount[j]);
			}

            list = Collections.unmodifiableList(arraylist);
        } else {
            list = Collections.singletonList(new Account(s, ACCOUNT_TYPE));
        }
        return list;
    }

    public static String getAuthToken(Context context, String name, String service)
        throws AuthenticatorException, NetworkException, OperationCanceledException {
        String authURL = Property.AUTH_URL.get();
        String email = Property.AUTH_EMAIL.get();
        String password =  Property.AUTH_PASSWORD.get();
        
        if(authURL == null || !name.equals(email) || password == null) {
        	AccountManager accountmanager = AccountManager.get(context);
            Account aaccount[] = accountmanager.getAccountsByType(ACCOUNT_TYPE);
            int i = 0;
            String token;
            do {
                if(i >= aaccount.length)
                    break;
                if(aaccount[i].name.equals(name)) {
                    try {
                    	token = accountmanager.blockingGetAuthToken(aaccount[i], service, true);
                        if(name == null)
                            throw new NetworkException("Cannot get auth token");
                        else
                        	return name;
                    } catch(IOException ioexception) {
                        throw new NetworkException("Cannot get auth token", ioexception);
                    }
                }
                i++;
            } while(true);
            throw new IllegalArgumentException((new StringBuilder("Account not found: ")).append(name).toString());
        } else {
            try
            {
                List<BasicNameValuePair> arraylist = new ArrayList<BasicNameValuePair>();
                arraylist.add(new BasicNameValuePair("_command_", "authorize"));
                arraylist.add(new BasicNameValuePair("Email", email));
                arraylist.add(new BasicNameValuePair("Passwd", password));
                arraylist.add(new BasicNameValuePair("accountType", "GOOGLE"));
                arraylist.add(new BasicNameValuePair("service", service));
                AuthorizeResponse respnse = post(authURL, new UrlEncodedFormEntity(arraylist));
                if(!respnse.isSucceed()) {
                	throw new IOException(String.format("Get Authorize Token failed, errorMsg:%s", respnse.getErrorMsg()));
                }
                return respnse.getToken();
            }
            catch(IOException ioexception1)
            {
                throw new NetworkException("Cannot get auth token", ioexception1);
            }
        }
    }

    public static void invalidateAuthToken(Context context, String s) {
        AccountManager.get(context).invalidateAuthToken(ACCOUNT_TYPE, s);
    }

    public static boolean isRestrictedCircleForAccount(EsAccount esaccount, int i) {
        boolean flag;
        if(esaccount.isChild() && (i == 9 || i == 7))
            flag = true;
        else
            flag = false;
        return flag;
    }

    public static Account newAccount(String s) {
        return new Account(s, ACCOUNT_TYPE);
    }

    private static AuthorizeResponse post(String url, UrlEncodedFormEntity urlencodedformentity) throws IOException {
        HttpURLConnection httpurlconnection = null;
        try {
	        httpurlconnection = (HttpURLConnection)(new URL(url)).openConnection();
	        httpurlconnection.setInstanceFollowRedirects(false);
	        httpurlconnection.setDoOutput(true);
	        httpurlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        httpurlconnection.setRequestProperty("Content-Length", Long.toString(urlencodedformentity.getContentLength()));
	        OutputStream outputstream = httpurlconnection.getOutputStream();
	        urlencodedformentity.writeTo(outputstream);
	        outputstream.flush();
	        int code = httpurlconnection.getResponseCode(); 
	        if(200 != code)
	        	throw new IOException((new StringBuilder("Unexpected HTTP response code: ")).append(code).toString());
	        return (AuthorizeResponse)JsonUtil.fromInputStream(httpurlconnection.getInputStream(), AuthorizeResponse.class);
        } finally {
        	if(null != httpurlconnection) {
        		httpurlconnection.disconnect();
        	}
        }
    }
    
    private static Properties mockpost(String url, UrlEncodedFormEntity urlencodedformentity) throws IOException {
    	Log.i("Mock", String.format("Mocked http request, url:%s", url));
    	Properties properties = new Properties();
    	properties.put("Auth", "mocked_auth_token");
    	return properties;
    }
}
