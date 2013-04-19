/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.hangout.crash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import com.galaxy.meetup.client.android.hangout.Log;

/**
 * 
 * @author sihai
 *
 */
public class CrashSender {

	public static boolean sendReport(Map map, String s, byte abyte0[])
    {
        String s1 = (new StringBuilder("----------")).append(Long.toString((long)(1234567890123D * Math.random()))).append(System.currentTimeMillis()).toString();
        HttpURLConnection httpurlconnection = null;
        int i;
        
        try {
	        httpurlconnection = (HttpURLConnection)(new URL("https://clients2.google.com/cr/report")).openConnection();
	        httpurlconnection.setDoOutput(true);
	        httpurlconnection.setRequestMethod("POST");
	        httpurlconnection.setRequestProperty("Content-Type", (new StringBuilder("multipart/form-data; boundary=")).append(s1).toString());
	        writePostContent(httpurlconnection.getOutputStream(), s1, map, s, abyte0);
	        i = httpurlconnection.getResponseCode();
	        Object aobj[] = new Object[2];
	        aobj[0] = map.get("sig");
	        aobj[1] = Integer.valueOf(i);
	        Log.info("Sent crash report with signature %s, response %d", aobj);
	        if(i != 200) {
	        	return false; 
	        } else {
	        	 String s2 = (new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()))).readLine();
	             Log.info((new StringBuilder("Report id: ")).append(s2).toString());
	             return true;
	        }
        } catch (IOException e) {
        	return false;
        } finally {
        	if(null != httpurlconnection) {
        		httpurlconnection.disconnect();
        	}
        }
    }

    private static void writePostContent(OutputStream outputstream, String s, Map map, String s1, byte abyte0[])
        throws IOException
    {
        java.util.Map.Entry entry;
        for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); outputstream.write((new StringBuilder("--")).append(s).append("\r\nContent-Disposition: form-data; name=\"").append((String)entry.getKey()).append("\"\r\n").append("\r\n").append((String)entry.getValue()).append("\r\n").toString().getBytes()))
            entry = (java.util.Map.Entry)iterator.next();

        if(abyte0 != null)
        {
            outputstream.write((new StringBuilder("--")).append(s).append("\r\nContent-Disposition: form-data; name=\"").append(s1).append("\"; filename=\"").append(s1).append("\"\r\n").append("Content-Type: application/octet-stream\r\n").append("\r\n").toString().getBytes());
            outputstream.write(abyte0);
            outputstream.write("\r\n".getBytes());
        }
        outputstream.write((new StringBuilder("--")).append(s).append("--\r\n").toString().getBytes());
    }
}
