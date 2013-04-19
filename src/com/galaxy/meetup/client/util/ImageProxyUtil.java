/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

import android.graphics.Point;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 
 * @author sihai
 *
 */
public class ImageProxyUtil {

	 private static final Pattern PROXY_HOSTED_IMAGE_URL_RE = Pattern.compile("^(((http(s)?):)?\\/\\/images(\\d)?-.+-opensocial\\.googleusercontent\\.com\\/gadgets\\/proxy\\?)");
	 static int sProxyIndex;
	 
	public static String setImageUrlSize(int i, String s) {
		String s2;
		if (s == null) {
			s2 = s;
		} else {
			String s1;
			if (!isProxyHostedUrl(s)) {
				s1 = createProxyUrl();
			} else {
				s1 = s;
				s = null;
			}
			s2 = setImageUrlSizeOptions(i, i, Uri.parse(s1), s).toString();
		}
		return s2;
	}
	
	public static boolean isProxyHostedUrl(String s) {
		boolean flag;
		if (s == null)
			flag = false;
		else
			flag = PROXY_HOSTED_IMAGE_URL_RE.matcher(s).find();
		return flag;
	}

	public static String setImageUrlSize(int i, int j, String s) {
		String s2;
		if (s == null) {
			s2 = s;
		} else {
			String s1;
			if (!isProxyHostedUrl(s)) {
				s1 = createProxyUrl();
			} else {
				s1 = s;
				s = null;
			}
			s2 = setImageUrlSizeOptions(i, j, Uri.parse(s1), s).toString();
		}
		return s2;
	}
	 
	private static String createProxyUrl() {
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append("https://images").append(getNextProxyIndex())
				.append("-esmobile")
				.append("-opensocial.googleusercontent.com/gadgets/proxy");
		return stringbuffer.toString();
	}
	 
	private static synchronized int getNextProxyIndex() {
		int i;
		i = sProxyIndex += 1;
		sProxyIndex %= 3;
		return i;
	}

    private static Uri setImageUrlSizeOptions(int i, int j, Uri uri, String s)
    {
        // TODO
    	return null;
    }
    
    public static Point getImageUrlSize(String s)
    {
        Point point = new Point();
        if(s != null && isProxyHostedUrl(s)) {
        	int l = 0;
        	Uri uri = Uri.parse(s);
            String s2 = uri.getQueryParameter("resize_w");
            if(!TextUtils.isEmpty(s2)) {
            	int k = Integer.parseInt(s2);
                l = k;
            }
            point.x = l;
            String s1;
            boolean flag;
            int i;
            s1 = uri.getQueryParameter("resize_h");
            flag = TextUtils.isEmpty(s1);
            i = 0;
            if(!flag) {
            	int j = Integer.parseInt(s1);
                i = j;
            }
            point.y = i;
            	
        }
        
        return point;
    }
}
