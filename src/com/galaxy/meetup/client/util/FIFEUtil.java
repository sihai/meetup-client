/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import android.graphics.Point;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 
 * @author sihai
 *
 */
public class FIFEUtil {

	private static final Pattern FIFE_HOSTED_IMAGE_URL_RE = Pattern.compile("^((http(s)?):)?\\/\\/((((lh[3-6]\\.((ggpht)|(googleusercontent)|(google)))|(bp[0-3]\\.blogger))\\.com)|(www\\.google\\.com\\/visualsearch\\/lh))\\/");
    private static final Joiner JOIN_ON_SLASH = Joiner.on("/");
    //private static final Splitter SPLIT_ON_EQUALS = Splitter.on("=").omitEmptyStrings();
    //private static final Splitter SPLIT_ON_SLASH = Splitter.on("/").omitEmptyStrings();
 
	public static String setImageUrlSize(int i, String s, boolean flag) {
		if (s != null && isFifeHostedUrl(s)) {
			StringBuffer stringbuffer = new StringBuffer();
			stringbuffer.append("s").append(i);
			stringbuffer.append("-d-no");
			if (flag)
				stringbuffer.append("-c");
			s = makeUriString(setImageUrlOptions(stringbuffer.toString(), s));
		}
		return s;
	}
	
	public static String setImageUrlSize(int i, int j, String s, boolean flag, boolean flag1) {
        String s1;
        if(s == null || !isFifeHostedUrl(s)) {
            s1 = s;
        } else {
            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer.append("w").append(i);
            stringbuffer.append("-h").append(j);
            stringbuffer.append("-d-no");
            s1 = makeUriString(setImageUrlOptions(stringbuffer.toString(), s));
        }
        return s1;
    }
	
	public static boolean isFifeHostedUrl(String s) {
		boolean flag;
		if (s == null)
			flag = false;
		else
			flag = FIFE_HOSTED_IMAGE_URL_RE.matcher(s).find();
		return flag;
	}

	public static Uri setImageUrlOptions(String s, String s1) {
		return setImageUriOptions(s, Uri.parse(s1));
	}
	
	public static Uri setImageUriOptions(String s, Uri uri) {
        // TODO
		return uri;
    }

	private static String makeUriString(Uri uri) {
        StringBuilder stringbuilder = new StringBuilder();
        String s = uri.getScheme();
        if(s != null)
            stringbuilder.append(s).append(':');
        String s1 = uri.getEncodedAuthority();
        if(s1 != null)
            stringbuilder.append("//").append(s1);
        String s2 = Uri.encode(uri.getPath(), "/=");
        if(s2 != null)
            stringbuilder.append(s2);
        String s3 = uri.getEncodedQuery();
        if(!TextUtils.isEmpty(s3))
            stringbuilder.append('?').append(s3);
        String s4 = uri.getEncodedFragment();
        if(!TextUtils.isEmpty(s4))
            stringbuilder.append('#').append(s4);
        return stringbuilder.toString();
    }
	
	public static Point getImageUrlSize(String s)
    {
        Point point = new Point();
        String s1;
        if(s != null && isFifeHostedUrl(s)) {
        	if(!TextUtils.isEmpty(s1 = getImageUriOptions(Uri.parse(s))))
            {
                String as[] = s1.split("-");
                int i = 0;
                while(i < as.length) 
                {
                    String s2 = as[i];
                    try
                    {
                        if(s2.startsWith("w"))
                            point.x = Integer.parseInt(s2.substring(1));
                        else
                        if(s2.startsWith("h"))
                            point.y = Integer.parseInt(s2.substring(1));
                        else
                        if(s2.startsWith("s"))
                        {
                            int j = Integer.parseInt(s2.substring(1));
                            point.y = j;
                            point.x = j;
                        }
                    }
                    catch(NumberFormatException numberformatexception) { }
                    i++;
                }
            }
        }
        return point;
    }
	
	public static String getImageUrlOptions(String s)
    {
		// TODO
		return null;
    }
	
	public static String getImageUriOptions(Uri uri)
    {
        // TODO
		return null;
		
    }
    
    //===========================================================================
    //						Inner class
    //===========================================================================
	private static final class Joiner {

		private final String separator;

		private Joiner(String s) {
			separator = s;
		}

		public static Joiner on(String s) {
			return new Joiner(s);
		}

		private static CharSequence toString(Object obj) {
			Object obj1;
			if (obj instanceof CharSequence)
				obj1 = (CharSequence) obj;
			else
				obj1 = obj.toString();
			return ((CharSequence) (obj1));
		}

		public final StringBuilder appendTo(StringBuilder stringbuilder, Iterable iterable) {
			Iterator iterator = iterable.iterator();
			if (iterator.hasNext()) {
				stringbuilder.append(toString(iterator.next()));
				for (; iterator.hasNext(); stringbuilder.append(toString(iterator.next())))
					stringbuilder.append(separator);

			}
			return stringbuilder;
		}
	}

}
