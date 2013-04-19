/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.xmpp;

import java.io.UnsupportedEncodingException;

import android.util.Base64;

/**
 * 
 * @author sihai
 *
 */
public class Commands {

	public static String authenticate(String s)
    {
        String s1 = (new StringBuilder("\000x@x.com\0")).append(s).toString();
        return (new StringBuilder("<ns2:auth ns3:service='webupdates' mechanism='X-GOOGLE-TOKEN' xmlns:ns3='http://www.google.com/talk/protocol/auth' xmlns:ns2='urn:ietf:params:xml:ns:xmpp-sasl' ns3:allow-generated-jid='true' ns3:client-uses-full-bind-result='true'>")).append(Base64.encodeToString(encodeUtf8(s1), 0)).append("</ns2:auth>").toString();
    }

    private static byte[] encodeUtf8(String s)
    {
        byte abyte0[];
        try
        {
            abyte0 = s.getBytes("UTF-8");
        }
        catch(UnsupportedEncodingException unsupportedencodingexception)
        {
            throw new AssertionError();
        }
        return abyte0;
    }
}
