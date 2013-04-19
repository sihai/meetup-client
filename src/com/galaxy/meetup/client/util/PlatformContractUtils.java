/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.galaxy.meetup.client.android.network.ApiaryApiInfo;

/**
 * 
 * @author sihai
 *
 */
public class PlatformContractUtils {

	public static Map getCallingPackageAnalytics(ApiaryApiInfo apiaryapiinfo)
    {
        HashMap hashmap = new HashMap();
        if(apiaryapiinfo != null && apiaryapiinfo.getSourceInfo() != null)
            hashmap.put("CONTAINER_URL", getContainerUrl(apiaryapiinfo));
        return hashmap;
    }

    public static String getCertificate(String s, PackageManager packagemanager) {
    	
        String s1;
        PackageInfo packageinfo;
        Signature asignature[];
        try {
        	packageinfo = packagemanager.getPackageInfo(s, 64);
        	
        	asignature = packageinfo.signatures;
            s1 = null;
            if(null != asignature) {
            	int i = packageinfo.signatures.length;
                s1 = null;
                if(i > 0) {
                	 
                	try {
    	            	 byte abyte0[];
    	                 MessageDigest messagedigest;
    	                 abyte0 = packageinfo.signatures[0].toByteArray();
    	                 messagedigest = MessageDigest.getInstance("SHA1");
    	                 if(messagedigest != null) {
    	                	 String s2;
    	                     byte abyte1[] = messagedigest.digest(abyte0);
    	                     if(null != abyte1)
    	                     {
    	                    	 s2 = Base64.encodeToString(abyte1, 2);
    	                         s1 = s2;
    	                     }
    	                 } else { 
    	                	 s1 = null;
    	                 }
                	} catch (NoSuchAlgorithmException nosuchalgorithmexception) {
                        if(EsLog.isLoggable("PlatformContractUtils", 5)) {
                        	Log.w("PlatformContractUtils", "Unable to compute digest, returning zeros");
                        }
                        s1 = null;
                	}
                }
            }
        } catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception) {
        	if(EsLog.isLoggable("PlatformContractUtils", 5)) {
            	Log.w("PlatformContractUtils", "Unable to get package info, returning zeros");
            }
            s1 = null;
        }
        
        if(s1 == null)
            s1 = "0";
        return s1;
    }

    public static String getContainerUrl(ApiaryApiInfo apiaryapiinfo)
    {
        if(apiaryapiinfo.getSourceInfo() != null)
            apiaryapiinfo = apiaryapiinfo.getSourceInfo();
        String s;
        String s1;
        String s2;
        String s3;
        android.net.Uri.Builder builder;
        if(apiaryapiinfo.getCertificate() != null)
            s = apiaryapiinfo.getCertificate();
        else
            s = "0";
        s1 = apiaryapiinfo.getClientId();
        s2 = apiaryapiinfo.getApiKey();
        s3 = apiaryapiinfo.getPackageName();
        builder = Uri.parse((new StringBuilder("http://")).append(Uri.encode(s)).append(".apps.googleusercontent.com/").toString()).buildUpon();
        if(s1 != null)
            builder.appendQueryParameter("client_id", s1);
        if(s2 != null)
            builder.appendQueryParameter("api_key", s2);
        if(s3 != null)
            builder.appendQueryParameter("pkg", s3);
        return builder.build().toString();
    }
}
